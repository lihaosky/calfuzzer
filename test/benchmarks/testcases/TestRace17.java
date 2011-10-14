import javato.activetesting.por.*;

public class TestRace17 {
	public static Object lock = new Object();
	public static Object lock1 = new Object();
	
	public static void main(String[] args) {
    		if (args.length == 0) {
    			Trace.parse(null);
    		} else {
    			Trace.parse(args[0]);
    		}
		synchronized(lock) {
			synchronized(lock1) {
			System.out.println("abcd");
			System.out.println("abcd");
			System.out.println("abcd");
			}
		}
	}
}
	
