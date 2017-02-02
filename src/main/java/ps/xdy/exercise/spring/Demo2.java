package ps.xdy.exercise.spring;

import javax.annotation.Resource;

import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Service;

@Service
public class Demo2 implements Lifecycle{
	
	@Resource
	private Demo demo;
	
	public Demo2(){
		System.out.println("Demo2 initing");
	}

	@Override
	public void start() {
		System.out.println("Demo2 start~");
	}

	@Override
	public void stop() {
		System.out.println("Demo2 ends~");
	}

	@Override
	public boolean isRunning() {
		return true;
	}
	
	public void printHello(){
		System.out.println("hello Demo2");
	}
}
