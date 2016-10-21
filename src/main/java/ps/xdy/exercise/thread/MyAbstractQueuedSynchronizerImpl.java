package ps.xdy.exercise.thread;

import java.util.LinkedList;
import java.util.concurrent.locks.LockSupport;

import sun.misc.Unsafe;

/**
 * TO understand the java's AQS,try to implement by myself
 * @author Administrator
 *
 */
public class MyAbstractQueuedSynchronizerImpl {

	private static Unsafe unsafe = Unsafe.getUnsafe();
	private static final long stateOffset;
	private static final long tailNodeOffset;
	private static final long headNodeOffset;
	
	static{
		try {
			stateOffset = unsafe.objectFieldOffset(MyAbstractQueuedSynchronizerImpl.class.getField("state"));
			tailNodeOffset = unsafe.objectFieldOffset(MyAbstractQueuedSynchronizerImpl.class.getField("tail"));
			headNodeOffset = unsafe.objectFieldOffset(MyAbstractQueuedSynchronizerImpl.class.getField("head"));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new Error("failed!",e);//System level exception
		}
	}

	private volatile int state;
	private volatile Node head;
	private volatile Node tail;
	private Thread exclusiveThread;
	
	private static class Node{
		private Thread nodeThread;
		private Node nextNode;
		private Node preNode;
		
		
		public Node(Thread thread){
			this.nodeThread = thread;
		}
		
		public Thread getNodeThread() {
			return nodeThread;
		}
		public void setNodeThread(Thread nodeThread) {
			this.nodeThread = nodeThread;
		}
		public Node getNextNode() {
			return nextNode;
		}
		public void setNextNode(Node nextNode) {
			this.nextNode = nextNode;
		}
		public Node getPreNode() {
			return preNode;
		}
		public void setPreNode(Node preNode) {
			this.preNode = preNode;
		}
		
		
		
	}
	
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public boolean compareAndSetState(int expect,int newState){
		return unsafe.compareAndSwapInt(this, stateOffset, expect, newState);
	}
	
	
	protected boolean tryAcquire(int acquire){
		throw new UnsupportedOperationException();
	}
	
	protected boolean tryRelease(int acquire){
		throw new UnsupportedOperationException();
	}
	
	public void acquire(int acquire){
		if(this.tryAcquire(acquire)){
			return;
		}
		
		//if failed put it in synchronizer queue to wait for sync
		Node node = addWaitNodeToQueue();
		
		//loop to acquire sync，if failed then sleep
		for(;;){
			if(head == node.preNode && tryAcquire(acquire)){
				exclusiveThread = Thread.currentThread();
				node.preNode = null;//help GC
				head = node;//flush to RAM
				return;
			}else{
//				if(shouldPark(node)){
//					LockSupport.park(node);
//				}
			}
		}
	}

	
	private Node addWaitNodeToQueue() {
		
		Node t = new Node(Thread.currentThread());
		// data race.should use CAS
		for(;;){
			Node preNode = tail;//copy to keep repeatable read
			if(preNode == null){
				//it means other thread get the lock and did not enter the queue,we should fix it
				
				//we should set head first because If some thread get sync ,then It will Only Change the headNode reference
				
				if(compareAndSetHead(new Node(null))){
					tail = head;//the sync semantic ensure by compareAndSetHead
				}else{
					continue;
				}
			}else{
				if(compareAndSetTail(preNode, t)){
					preNode.setNextNode(t);//the sync semantic ensure by compareAndSetHead
					t.setPreNode(preNode);
					return t;
				}else{
					continue;
				}
			}
		}
		
		
	}

	private boolean compareAndSetTail(Node previous, Node newTail) {
		return unsafe.compareAndSwapObject(this, tailNodeOffset, previous, newTail);
	}

	private boolean compareAndSetHead(Node h) {
		return unsafe.compareAndSwapObject(this, headNodeOffset, null, h);
	}
	
	
	
	
	
	
	
	
	
}
