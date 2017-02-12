package ps.xdy.exercise;

public class MemoryDumpTest {
	
	static Object obj = null;
	
	public static void main(String[] args) {

		for(;;){	
			char[] bytes = new char[1024 * 1024];
			System.out.println(bytes.length);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	
	public void test(String test){
		
	}
}
