package benchmarks.testcases;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import javato.activetesting.por.*;

public class TestRace15 {
    static float x = 23.45f;
    public static int N = 1;

    public synchronized float getX() {
        return x;
    }

    public synchronized void setX(float x) {
        TestRace15.x = x;
    }

    private void mult() {
        //System.out.print("*");
        setX(getX()*1.52f);
    }

    private void add() {
        //System.out.print("+");
        setX(getX()+12.36f);
    }

    private void div() {
        //System.out.print("/");
        setX(getX()/1.21f);
    }

    public void test2 () throws InterruptedException {


        Thread t1 = new Thread("Star1") {
            public void run() {
                for (int i=0; i<N; i++) {
                    mult();
                }
                System.out.println("end Star1");
            }
        };
        Thread t2 = new Thread("Star2") {
            public void run() {
                for (int i=0; i<N; i++) {
                    div();
                }
                System.out.println("end Star2");
            }
        };
        t1.start();

        t2.start();
        for (int i=0; i<N; i++) {
            add();
        }
        System.out.println("before join 2");
        t2.join();
        System.out.println("before join 1");
        t1.join();
        System.out.println("");
        System.out.println(x);

    }

    public static void main(String[] args) throws InterruptedException, IOException {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
        TestRace15.N = 3;
        (new TestRace15()).test2();
        PrintWriter pw = new PrintWriter(new FileWriter("error.stat",true));
        pw.println(TestRace15.x);
        pw.close();
    }
}
