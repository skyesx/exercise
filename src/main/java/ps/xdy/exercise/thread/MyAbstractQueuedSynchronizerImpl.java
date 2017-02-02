package ps.xdy.exercise.thread;

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
	private static final long nodeWaitStatusOffset;
	
	static{
		try {
			stateOffset = unsafe.objectFieldOffset(MyAbstractQueuedSynchronizerImpl.class.getField("state"));
			tailNodeOffset = unsafe.objectFieldOffset(MyAbstractQueuedSynchronizerImpl.class.getField("tail"));
			headNodeOffset = unsafe.objectFieldOffset(MyAbstractQueuedSynchronizerImpl.class.getField("head"));
			nodeWaitStatusOffset = unsafe.objectFieldOffset(Node.class.getField("waitStatus"));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new Error("failed!",e);//System level exception
		}
	}

	private volatile int state;
	private volatile Node head;
	private volatile Node tail;
	private Thread exclusiveThread;
	
	private static class Node{
		private boolean shared = false;
		private Thread nodeThread;
		private volatile Node nextNode;
		private volatile Node preNode;
		private volatile int waitStatus;
		/**
		 * node's successor need signal
		 */
		private static final int SIGNAL = -1;
		private static final int CANCELLED = 1;
		
		public Node(Thread thread,boolean shared){
			this.nodeThread = thread;
			this.shared = shared;
		}
		
		public Node(Thread thread){
			this.nodeThread = thread;
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
	
	public int tryAcquireShared(int acquire){
		throw new UnsupportedOperationException();
	}
	
	public final void acquireShared(int acquire) {
		if (tryAcquireShared(acquire) < 0)
			doAcquireShared(acquire);
	}
	
	private void doAcquireShared(int acquire) {
		//enter queue sharedNode
		Node node = addWaitNodeToQueue(true);
		
		//loop to acquire shareLock,if failed then park,if success and have successor,then notice successor.
		for(;;){
			if(node.preNode == head && tryAcquireShared(acquire) >= 0){
				//become head
				setHead(node);
				//try to notify wating shareNodes,check in the queue
				releaseSuccessors();
				
				//if success then next sharedNodes maybe success too,need notify
			}else{
				//failed to acquire shared,check whether park or not
			}
		}
		
		
	}

	private void releaseSuccessors() {
		// TODO Auto-generated method stub
		
	}

	private void setHead(Node node) {
		head = node;//do not need CAS,cause when this method is calling, head have already initial.only the lock owner will try to change head.
		node.nodeThread = null;
		node.preNode = null;//help GC
	}

	public void acquire(int acquire){
		if(this.tryAcquire(acquire)){
			return;
		}
		
		//if failed put it in synchronizer queue to wait for sync
		Node node = addWaitNodeToQueue(false);
		
		//loop to acquire sync, if failed then sleep
		boolean interupted = false;
		for(;;){
			if(head == node.preNode && tryAcquire(acquire)){
				exclusiveThread = Thread.currentThread();
				node.preNode = null;//help GC
				head = node;//flush to RAM
				if(interupted){
					selfInterrupt();
				}
				return;
			}else{
				if(shouldParkAfterFailure(node) && parkAndDetectInterrupt()){
					interupted = true;//do not handle interrupt here,let method caller to handle it
				}
			}
		}
	}
	
	public boolean release(int release){
		if(tryRelease(release)){
			//fully released
			Node node = head;
			
			if(node != null && node.waitStatus == Node.SIGNAL){
				//if node is null,then there's impossible that successor needs SIGNAL,cause of initial head(volatile) is before set it's waitStatus to SIGNAL
				unparkSuccessor(node);
			}
			
			return true;
		}else{
			return false;
		}
		
	}

	
	private void unparkSuccessor(Node node) {
		Node successor = node.nextNode;
		if(successor == null || successor.waitStatus > 0){
			//if nextNode do not need notify,then find reverse from tail
			successor = tail;
			while(successor.preNode != node && successor.waitStatus <= 0){
				successor = successor.preNode;
			}
		}
		
		LockSupport.unpark(successor.nodeThread);
	}

	private void selfInterrupt() {
		Thread.currentThread().interrupt();
	}

	private boolean parkAndDetectInterrupt() {
		LockSupport.park(this);
		return Thread.interrupted();
	}

	/**
	 * before park ,thread should make sure the predecessor will signal
	 * @param node
	 * @return
	 */
	private boolean shouldParkAfterFailure(Node node) {
		
		Node pred = node.preNode;
		int ws = pred.waitStatus;
		
		if(ws == Node.SIGNAL){
			//predecessor will notify when release
			return true;
		}else{
			if(ws > 0){
				//predecessor cancelled,should find the valid one
				do{
					pred = node.preNode = pred.preNode;//can't be null.
				}while(pred.waitStatus > 0);
				pred.nextNode = node;//flush to ram,and loop again
				
				//the operation change preNode first nextNode latter means that to travel a valid shortest queue should begin from tail
			}else{
				//set predecessor's waitStatus to SIGNAL,caller will check whether success in next loop 
				compareAndSwapNodeStatus(pred, ws,Node.SIGNAL);
			}
		}
		
		return false;
	}

	private void compareAndSwapNodeStatus(Node pred, int orign,int newStatus) {
		unsafe.compareAndSwapInt(pred, nodeWaitStatusOffset, orign, Node.SIGNAL);
	}

	private Node addWaitNodeToQueue(boolean shared) {
		
		Node t = new Node(Thread.currentThread(),shared);
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
					preNode.nextNode = t;//the sync semantic ensure by compareAndSetHead
					t.preNode = preNode;
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
