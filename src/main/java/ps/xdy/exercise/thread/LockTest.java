package ps.xdy.exercise.thread;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockTest {
	
	public static void main(String[] args) {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		
		lock.writeLock().lock();
		new Thread(){
			@Override
			public void run() {
				lock.readLock().lock();
				lock.readLock().unlock();
			}
		}.start();
		
		new Thread(){
			@Override
			public void run() {
				lock.readLock().lock();
				lock.readLock().unlock();
			}
		}.start();
		
		lock.writeLock().unlock();
		
		System.out.println();
		
		
		
	}

}
