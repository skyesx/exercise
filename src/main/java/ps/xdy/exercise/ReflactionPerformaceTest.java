package ps.xdy.exercise;
import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

public class ReflactionPerformaceTest {

    static class B {

    }

    public static long timeDiff(long old) {
        return System.nanoTime() - old;
    }

    public static void main(String args[]) throws Exception {

        int numTrials = 10000000;
        B[] bees = new B[numTrials];
        Class<B> c = B.class;
        
        
        
        Constructor<?>[] constructors = c.getDeclaredConstructors();
        @SuppressWarnings("unchecked")
		Constructor<B> constructor = (Constructor<B>)constructors[0];
        for (int i = 0; i < numTrials; i++) {
            bees[i] = constructor.newInstance();
        }
        for (int i = 0; i < numTrials; i++) {
            bees[i] = new B();
        }

        long nanos;

        
        nanos = System.nanoTime();
        for (int i = 0; i < numTrials; i++) {
        	bees[i] = new B();
        }
        System.out.println("Normal instaniation took: " + TimeUnit.NANOSECONDS.toMillis(timeDiff(nanos)) + "ms");

        nanos = System.nanoTime();
        for (int i = 0; i < numTrials; i++) {
            bees[i] = constructor.newInstance();
        }
        System.out.println("Reflecting instantiation took:" + TimeUnit.NANOSECONDS.toMillis(timeDiff(nanos)) + "ms");

    }


}