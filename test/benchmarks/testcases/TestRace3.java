package benchmarks.testcases;
import javato.activetesting.por.*;

public class TestRace3 {
    public static int x = 0;
//    public static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        Thread t1 = new Thread() {
            public void run() {
                x++;
            }
        };
        t1.start();
        x++;
        t1.join();
        System.out.println("x = "+x);
    }
}
