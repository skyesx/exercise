package ps.xdy.exercise.spring;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Service;

@Service
public class Demo {
	
	public static void main(String[] args) {
//		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("xml/applicationContext.xml");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"xml/applicationContext.xml"}, false);
		ctx.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
				System.out.println("Before bean init");
			}
		});
		
		try {
			ctx.getEnvironment().getPropertySources().addFirst(new ResourcePropertySource("properties/config.properties", Demo.class.getClassLoader()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ctx.refresh();
		
		
		Environment environment = ctx.getEnvironment();
		System.out.println(environment.getProperty("name"));
		
		
		Demo demo  = ctx.getBean(Demo.class);
		demo.printHello();
		
		Demo2 demo2  = ctx.getBean(Demo2.class);
		demo2.printHello();
		
		ctx.close();
	}
	
	public void printHello(){
		System.out.println("hello " + name);
	}
	
	@Value("${name}")
	private String name;

}
