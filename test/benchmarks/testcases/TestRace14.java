package benchmarks.testcases;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import javato.activetesting.por.*;

public class TestRace14 {
    public static void main(String[] args) throws InterruptedException, IOException {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        TestRace15.N = 100;
        (new TestRace15()).test2();
        PrintWriter pw = new PrintWriter(new FileWriter("error.stat",true));
        pw.println(TestRace15.x);
        pw.close();
    }
}
