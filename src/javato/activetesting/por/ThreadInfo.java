package javato.activetesting.por;

import java.util.HashMap;
import java.util.Set;

/**
 * Class handle thread info
 * @author lihao
 *
 */
public class ThreadInfo {
	/**
	 * Store thread id and thread object
	 * threadID --> Thread object
	 */
	public static HashMap<String, Thread> threads = new HashMap<String, Thread>();
	
	/**
	 * Store if a thread is stopped at a transition point
	 */
	public static HashMap<String, Boolean> isStopped = new HashMap<String, Boolean>();
	
	/**
	 * Put thread id and object into map
	 * @param tid thread id
	 * @param t thread object
	 */
	public synchronized static void put(String tid, Thread t) {
		Thread thread = threads.get(tid);
		if (thread == null) {
			threads.put(tid, t);
		}
	}
	
	/**
	 * Get thread object from thread id
	 * @param tid
	 * @return
	 */
	public synchronized static Thread getThread(String tid) {
		return threads.get(tid);
	}
	
	/**
	 * Set the state of a thread
	 * @param tid
	 * @param stop
	 */
	public synchronized static void setStop(String tid, boolean stop) {
		Boolean b = isStopped.get(tid);
		if (b == null) {
			isStopped.put(tid, new Boolean(stop));
		} else {
			isStopped.remove(tid);
			isStopped.put(tid, new Boolean(stop));
		}
	}
	
	/**
	 * Check if all thread is stopped now
	 * @return
	 */
	public synchronized static boolean isAllStop() {
		for (String tid : isStopped.keySet()) {
			if (isStopped.get(tid).booleanValue() == false) {
				Thread thread = threads.get(tid);
				if (thread.getState() != Thread.State.NEW && thread.getState() != Thread.State.BLOCKED && thread.getState() != Thread.State.WAITING && thread.getState() != Thread.State.TERMINATED) {
			//		System.out.println("Thread " + tid + " is not stopped!");
		//			System.out.println("Thread " + tid + "'s state is " + thread.getState());
					return false;
				}
			}
		}
		return true;
	}

	/**
	* Get key set
	*/
	public synchronized static Set<String> getKeySet() {
		return threads.keySet();
	}
}
