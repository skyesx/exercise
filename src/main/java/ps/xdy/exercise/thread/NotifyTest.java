package ps.xdy.exercise.thread;

public class NotifyTest {

	public static boolean exit = false;
	
	public static void main(String[] args) {
		// multi-thread wait on an object, after notifyAll check whether is serial
		
		Object waitObj = new Object();
		
		Thread t1 = new WaitObjThread(waitObj, "T1");
		Thread t2 = new WaitObjThread(waitObj, "T2");
		
		t1.start();
		t2.start();
		
		
		exit = true;
		waitObj.notifyAll();
//		
//		synchronized(waitObj){
//			exit = true;
//			waitObj.notifyAll();
//		}
		
		System.out.println();
		
	}
	
	public static class WaitObjThread extends Thread{
		
		private Object waitObj;
		
		public WaitObjThread(Object waitObj,String threadName) {
			this.waitObj = waitObj;
			this.setName(threadName);
		}
		
		@Override
		public void run(){
			
			synchronized(waitObj){
				
				System.out.println(getName() + " get Lock!");
				while(!exit){
					try{
						waitObj.wait();
						Thread.yield();//relase CPU
					}catch(InterruptedException e){
						System.out.println(getName() + " Interupted!");
					}
				}
				
				System.out.println(getName() + " before Realease Lock!");
			}
			
			System.out.println(getName() + " Realse Lock!");
			
		}
	}
	
}
