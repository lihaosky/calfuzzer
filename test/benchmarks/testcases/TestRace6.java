package benchmarks.testcases;
import javato.activetesting.por.*;

public class TestRace6 {
    public static int x = 0;
    public final static Object lock = new Object();
    public final static Object lock2 = new Object();

    public static void main(String[] args) throws InterruptedException {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        Thread t1 = new Thread() {
            public void run() {
                synchronized (lock) {
                    x++;
                }
            }
        };
        t1.start();
        synchronized (lock2) {
            x++;
        }
        t1.join();
        System.out.println("x = "+x);
    }
}
