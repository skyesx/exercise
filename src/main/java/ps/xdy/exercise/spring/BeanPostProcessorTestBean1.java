package ps.xdy.exercise.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

@Service
public class BeanPostProcessorTestBean1 implements BeanPostProcessor,Ordered {
	
	public BeanPostProcessorTestBean1(){
		System.out.println("P1 init");
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		System.out.println(beanName + " p1:postProcessBeforeInitialization:" );
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		System.out.println(beanName + " p1:postProcessAfterInitialization:" );
		return bean;
	}


	@Override
	public int getOrder() {
		return 11;
	}

}
