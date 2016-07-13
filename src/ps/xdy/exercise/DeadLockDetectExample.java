package ps.xdy.exercise;


public class DeadLockDetectExample {
	
	public static void main(String[] args) {
		final Object l1 = new Object();
		final Object l2 = new Object();
		
		
		Thread t1 = new Thread(){
			public void run() {
				synchronized (l1) {
					System.out.println("t1 lock l1...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (l2) {
						System.out.println("t2 thread lock l2...");
					}
				}
			};
		};
		t1.setName("t1");
		
		
		Thread t2 = new Thread(){
			public void run() {
				synchronized (l2) {
					System.out.println("t2 lock l2...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (l1) {
						System.out.println("t2 thread lock l1...");
					}
				}
			};
		};
		t2.setName("t2");
		
		
		t1.start();
		t2.start();
		
		
		// run jstack or jmc to detect deadlock
	}
	
}