package benchmarks.testcases;
import javato.activetesting.por.*;

public class TestRace2 {
    public static int x = 0;

    public static void main(String[] args) throws InterruptedException {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        x++;
        Thread t1 = new Thread() {
            public void run() {
                x++;
            }
        };
        t1.start();
        t1.join();
        x++;
        System.out.println("x = "+x);
    }
}
