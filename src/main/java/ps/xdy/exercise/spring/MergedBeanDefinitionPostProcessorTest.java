package ps.xdy.exercise.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Service;

@Service
public class MergedBeanDefinitionPostProcessorTest implements MergedBeanDefinitionPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		System.out.println(beanName + " MP:postProcessBeforeInitialization:");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		System.out.println(beanName + " MP:postProcessAfterInitialization:" );
		return bean;
	}

	@Override
	public void postProcessMergedBeanDefinition(
			RootBeanDefinition beanDefinition, Class<?> beanType,
			String beanName) {
		
		
		System.out.print(beanName + " MP:postProcessMergedBeanDefinition:");
		System.out.println(beanDefinition + "  propertyValues:" + beanDefinition.getPropertyValues().toString());
	}



}
