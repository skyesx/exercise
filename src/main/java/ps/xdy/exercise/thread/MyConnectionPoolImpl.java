package ps.xdy.exercise.thread;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MyConnectionPoolImpl {
	
	public static class ConnectionPool{
		
		LinkedList<Connection> lstConn = null;
		
		public ConnectionPool(int poolSize){
			lstConn = new LinkedList<>();
			for(int i = 0 ;i < poolSize ; i++){
				lstConn.add(ConnectionDriver.createConnection());
			}
		}
		
		public Connection getConnection(int mills){
			synchronized (lstConn) {
				if(lstConn.size() != 0){
					return lstConn.removeFirst();
				}else{
					long currentTime = System.currentTimeMillis();
					long expireTime = currentTime + mills;
					while((currentTime = System.currentTimeMillis())  < expireTime && lstConn.size() != 0){
						long restTime = expireTime - currentTime;
						try {
							lstConn.wait(restTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if(lstConn.size() != 0){
						return lstConn.removeFirst();
					}else{
						return null;
					}
				}
			}
		}
		
		public void realseConnection(Connection conn){
			synchronized(lstConn){
				lstConn.add(conn);
				lstConn.notify();//or use notifyALl?which is better?efficient or safe?
			}
		}
		
		public static class ConnectionDriver{
			
			public static Connection createConnection(){
				return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(),new Class[]{Connection.class}, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						
						if(method.getName().contains("commit")){
//							Thread.yield();
							Thread.sleep(100);
						}
						
						return null;
					}
				});
			}
		}
		
	}
	
	
	
	public static class Consumer extends Thread{
		
		public static AtomicInteger got = new AtomicInteger(0);
		public static AtomicInteger ungot = new AtomicInteger(0);
		
		private ConnectionPool cnnPool = null;
		private int count = 0;
		private CountDownLatch start;
		private CountDownLatch end;

		public Consumer(ConnectionPool cnnPool, int count, CountDownLatch start, CountDownLatch end) {
			super();
			this.cnnPool = cnnPool;
			this.count = count;
			this.start = start;
			this.end = end;
		}


		@Override
		public void run(){
			
			try {
				start.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			while(count-- > 0){
				Connection connection = cnnPool.getConnection(1000);
				if(connection == null){
					ungot.incrementAndGet();
				}else{
					try {
						connection.createStatement();
						connection.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						cnnPool.realseConnection(connection);
						got.incrementAndGet();
					}
				}
			}
		
			end.countDown();
		}
	}
	
	public static void main(String[] args) {
		
		int poolSize = 10;// 100tps
		int loadThread = 11;//
		int loadPerThread = 20;//expireTime 2s
		
		CountDownLatch end = new CountDownLatch(loadThread);
		CountDownLatch start = new CountDownLatch(1);
		
		ConnectionPool cnnPool = new ConnectionPool(poolSize);
		for(int i = 0; i < loadThread ; i++){
			new Consumer(cnnPool, loadPerThread, start, end).start();
		}
		 
		start.countDown();
		try {
			end.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(String.format("got:%d", Consumer.got.get()));
		System.out.println(String.format("ungot:%d", Consumer.ungot.get()));
		
	}
	

}
