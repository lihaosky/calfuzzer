package benchmarks.testcases;
import javato.activetesting.por.*;

public class TestRace8 {
    public static int x = 0;
    public final static Object lock = new Object();
    public static boolean cond = true;

    public static void main(String[] args) throws InterruptedException {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        Thread t1 = new Thread() {
            public void run() {
                synchronized (lock) {
                    if (cond) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                x++;
            }
        };
        t1.start();
        Thread.sleep(10);        
        x++;
        synchronized (lock) {
            lock.notify();
            cond = false;
        }
        t1.join();
        System.out.println("x = "+x);
    }
}
