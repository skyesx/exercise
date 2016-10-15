package ps.xdy.exercise;

public class ProfilerTest {	
	
	
	public static void main(String[] args) {
		while(true){
			parentMehod();
		}
	}

	private static void parentMehod() {
		childMethodFast();
		childMethodSLow();
	}

	/**
	 * 
	 */
	private static void childMethodSLow() {
		try {
			Thread.sleep(1000l);
			System.out.println("Slow。。。。");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private static void childMethodFast() {
		try {
			Thread.sleep(500l);
			System.out.println("Fast。。。");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
