package ps.xdy.exercise.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

@Service
public class BeanPostProcessorTestBean2 implements BeanPostProcessor,Ordered {
	
	public BeanPostProcessorTestBean2(){
		System.out.println("P2 init");
	}

	

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		System.out.println(beanName + " p2:postProcessBeforeInitialization:" );
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		System.out.println(beanName + " p2:postProcessAfterInitialization:" );
		return bean;
	}


	@Override
	public int getOrder() {
		return 11;
	}

}
