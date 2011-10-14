package benchmarks.testcases;
import javato.activetesting.por.*;

class Thread8 extends Thread {
    Object l2;
    Object l3;

    public Thread8(Object o2, Object o3) {
        l2 = o2;
        l3 = o3;
    }

    public void run() {
        synchronized (l2) {
            synchronized (l3) {

            }
        }
    }
}

public class TestDeadlock8 {

    static Object o1 = new Object();
    static Object o2 = new Object();


    public static void main(String[] args) throws InterruptedException {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        for (int i = 0; i < 2; i++) {
            foo();
        }
    }

    public static void foo() throws InterruptedException {
        Thread8 t1 = null, t2 = null;
        for (int i = 0; i < 3; i++) {
            t1 = new Thread8(o1, o2);
            t2 = new Thread8(o2, o1);
        }
        t1.start();
        Thread.sleep(10);
        t2.start();
        Thread.sleep(10);
    }
}
