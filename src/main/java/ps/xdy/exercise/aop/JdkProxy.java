package ps.xdy.exercise.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class JdkProxy {
	
	
	public static interface SayHello{
		void say();
	}
	
	
	public static class SayHelloImpl implements SayHello{

		@Override
		public void say() {
			System.out.println("hello!");
		}
		
		public void sayHi(){
			System.out.println("hi!");
		}
		
	}
	
	public static class SayHelloIJdknterceptor implements InvocationHandler{
		
		private Object orign;
		
		public void setOrign(Object orign) {
			this.orign = orign;
		}


		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			System.out.println("before say Hello!");
			Object result = method.invoke(orign, args);
			System.out.println("after say Hello!");
			return result;
		}
	}
	
public static class SayHelloCglibInterceptor implements MethodInterceptor{
		
		private Object orign;
		
		public void setOrign(Object orign) {
			this.orign = orign;
		}

		@Override
		public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {

			System.out.println("before say Hello!");
			Object result = arg1.invoke(orign, arg2);
			System.out.println("after say Hello!");
			return result;
		
		}
		
		
	    //返回目标对象的代理对象  
	    public Object newProxy()  
	    {  
	        Enhancer enhancer = new Enhancer();  
	        enhancer.setSuperclass(orign.getClass());  
	        enhancer.setCallback(this);  
	        enhancer.setClassLoader(orign.getClass().getClassLoader());  
	        return enhancer.create();  
	    }  
	}
	
	
	public static void main(String[] args) {
		
		SayHelloImpl impl = new SayHelloImpl();
		SayHelloIJdknterceptor sayHelloInterceptor = new SayHelloIJdknterceptor();
		sayHelloInterceptor.setOrign(impl);
//		SayHelloImpl newProxyInstance = (SayHelloImpl)Proxy.newProxyInstance(impl.getClass().getClassLoader(), impl.getClass().getInterfaces(), sayHelloInterceptor);
//		newProxyInstance.say();
//		newProxyInstance.sayHi();
		
		SayHello newProxyInstance = (SayHello)Proxy.newProxyInstance(impl.getClass().getClassLoader(), impl.getClass().getInterfaces(), sayHelloInterceptor);
		newProxyInstance.say();
		
		SayHelloCglibInterceptor cglibInterceptor = new SayHelloCglibInterceptor();
		cglibInterceptor.setOrign(impl);
		
		SayHelloImpl implProxy = (SayHelloImpl)cglibInterceptor.newProxy();
		
		implProxy.sayHi();
		
	}

}
