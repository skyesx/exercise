package ps.xdy.exercise.thread;

import java.util.concurrent.locks.ReentrantLock;

public class LockTest {
	
	
	public static void main(String[] args) {
		
		MyReentrantLock lock = new MyReentrantLock();
		
		try{
			lock.lock();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		
	}
	
	
	private static class MyReentrantLock extends ReentrantLock{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public void lock() {
			super.lock();
			throw new RuntimeException("my exception");
		}
		
	}
	

}
