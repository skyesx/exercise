package ps.xdy.exercise.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;

public class Demo {
	
	public Demo(){
		System.out.println("Demo initing");
	}
	
	private static class Temp{
	}
	
	public static void main(String[] args) {
		
//		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("xml/applicationContext.xml");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"xml/applicationContext.xml"}, false);
		
		ctx.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
				System.out.println("Before beanDefinition load");
			}
		});
		
//		try {
//			ctx.getEnvironment().getPropertySources().addFirst(new ResourcePropertySource("properties/config.properties", Demo.class.getClassLoader()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		ApplicationListener<?> listener = new ApplicationListener<ApplicationEvent>() {
			
			@Override
			public void onApplicationEvent(ApplicationEvent event) {
				
				System.out.println(event.getClass().getSimpleName());
				
				if(event instanceof ContextRefreshedEvent){
					ctx.getBeanFactory().registerSingleton("test", new Temp());
				}
			}
		};
		ctx.addApplicationListener(listener);
		
		ctx.refresh();
		checkExist(ctx);
		
		
		Environment environment = ctx.getEnvironment();
		
		System.out.println("environment property:" + environment.getProperty("name"));
		
		
		Demo demo  = ctx.getBean(Demo.class);
		demo.printHello();
		
		Demo2 demo2  = ctx.getBean(Demo2.class);
		demo2.printHello();
		
		System.out.println("Resolver:" + ctx.getBeanFactory().resolveEmbeddedValue("I am the resolved value of name :${name}"));
		
		ctx.close();
	}

	private static void checkExist(ClassPathXmlApplicationContext ctx) {
		Temp bean = ctx.getBean(Temp.class);
		if(bean != null){
			System.out.println("exist!");
		}else{
			System.out.println("not exist!");
		}
	}
	
	public void printHello(){
		System.out.println("hello " + name);
	}
	
	
	private String name;
	
	private String name2;

	public String getName() {
		return name;
	}
	
	@Value("${name}")
	public void setName(String name) {
		this.name = name;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}
	
	

}
