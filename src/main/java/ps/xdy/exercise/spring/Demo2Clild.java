package ps.xdy.exercise.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Demo2Clild extends Demo2{
	
	@Value("${name}")
	private String property;
	
	public Demo2Clild(){
		System.out.println("Demo2 Child initing");
	}

	@Override
	public void start() {
		System.out.println("Demo2 Child start~");
	}

	@Override
	public void stop() {
		System.out.println("Demo2 Child ends~");
	}

	@Override
	public boolean isRunning() {
		return true;
	}
	
	public void printHello(){
		System.out.println("hello Demo2 Child " + property);
	}
}
