package benchmarks.testcases;
import javato.activetesting.por.*;

class Thread7 extends Thread {
    Object l2;
    Object l3;

    public Thread7(Object o2, Object o3) {
        l2 = o2;
        l3 = o3;
    }

    public void run() {
        synchronized (l2) {
            synchronized (l3) {
                System.out.println("x");
            }
        }
    }
}

public class TestDeadlock7 {

    static Object o1 = new Object();
    static Object o2 = new Object();


    public static void main(String[] args) {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        Thread t1 = new Thread7(o1, o2);
        Thread t2 = new Thread7(o2, o1);

        t1.start();
        try {
            Thread.sleep(5);
        } catch (Exception e) {
        }

        t2.start();

        try {
            t1.join();
            t2.join();
        }
        catch (Exception e) {
            System.err.println("Exception occurred while waiting for threads " + e.toString());
        }

    }

}

