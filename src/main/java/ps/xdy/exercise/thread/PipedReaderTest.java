package ps.xdy.exercise.thread;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

public class PipedReaderTest {

	public static boolean exit = false;
	
	public static void main(String[] args) {
		// main thread write, other thread read
		
		try(PipedWriter writer = new PipedWriter();PipedReader reader = new PipedReader()) {
			writer.connect(reader);
			new WriterThread(reader, "WriterThread").start();
			
			int recived = 0;
			while((recived = System.in.read()) != -1){
				writer.write(recived);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public static class WriterThread extends Thread{
		
		private PipedReader reader;
		
		public WriterThread(PipedReader reader,String threadName) {
			this.reader = reader;
			this.setName(threadName);
		}
		
		@Override
		public void run(){
			
			int readed;
			try {
				while((readed = reader.read()) != -1){
					System.out.println((char)readed);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
