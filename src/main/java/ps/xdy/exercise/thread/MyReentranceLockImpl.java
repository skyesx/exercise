package ps.xdy.exercise.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * develop a ReentranceLock using AQS
 * @author Administrator
 *
 */
public class MyReentranceLockImpl {
	
	
	
	/**
	 * @author Administrator
	 *
	 */
	public static class MyReentranceLock implements Lock{

		private static class Sync extends AbstractQueuedSynchronizer{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			protected boolean tryAcquire(int arg) {
				
				int state = getState();
				if(state > 0){
					//lock was obtained
					Thread ownerThread = getExclusiveOwnerThread();
					//check whether this thread is owner
					if(ownerThread == Thread.currentThread()){
						//this thread is owner
						setState(arg + state);
						return true;
					}else{
						//not owner
						return false;
					}
				}else{
					//lock not obtained
					if(compareAndSetState(0, arg)){
						setExclusiveOwnerThread(Thread.currentThread());
						return true;
					}else{
						return false;
					}
				}
				
			}
			
			@Override
			protected boolean tryRelease(int arg) {
				if(getExclusiveOwnerThread() != Thread.currentThread()){
					throw new RuntimeException();
				}else{
					int newState = getState() - arg;
					if(newState < 0){
						throw new RuntimeException();
					}else if (newState > 0){
						setState(newState);
						return false;
					}else{
						setExclusiveOwnerThread(null);
						setState(0);
						return true;
					}
				}
			}
			
			protected Condition newCondition(){return new ConditionObject();}
			
		}
		
		
		private final Sync sync = new Sync();
		
		@Override
		public void lock() {
			sync.acquire(1);
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			sync.acquireInterruptibly(1);
		}

		@Override
		public boolean tryLock() {
			return sync.tryAcquire(1);
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			return sync.tryAcquireNanos(1, unit.toNanos(time));
		}

		@Override
		public void unlock() {
			sync.release(1);
		}

		@Override
		public Condition newCondition() {
			return sync.newCondition();
		}
	}
	
	
	public static void main(String[] args) {
		//t1 can reentrance and unlock
		//t2 will get the lock after t1 totally release
		
		
		MyReentranceLock lock = new MyReentranceLock();
		lock.lock();
		System.out.println("main thread obtained lock");

		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("T2 try to obtained lock");
				lock.lock();
				System.out.println("T2 obtained lock");
				
			}
		},"T2").start();

		Thread.yield();
		lock.lock();
		System.out.println("main thread obtained lock,again");
		lock.unlock();
		lock.unlock();
		System.out.println("main thread realse lock");
		
	}

}
