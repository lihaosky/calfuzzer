package benchmarks.testcases;
import javato.activetesting.por.*;

public class TestRace13 {
    public static volatile int x = 0, y, z;

    public static void main(String[] args) throws InterruptedException {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        Thread t1 = new Thread() {
            public void run() {
                x= x + 5;
                y=0;
            }
        };
        Thread t2 = new Thread() {
            public void run() {
                x = x + 17;
                z=0;
            }
        };
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("x = "+x);
    }
}
