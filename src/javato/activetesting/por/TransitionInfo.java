package javato.activetesting.por;
import java.util.*;

/**
 * Class handle transition info
 * @author lihao
 *
 */
public class TransitionInfo {
	/**
	 * Current transition of a thread
	 * ThreadId --> Transition number
	 */
	public static HashMap<String, LongCounter> threadTransition = new HashMap<String, LongCounter>();
	/**
	 * Locks needed by Current transition, used for compute co-enabled transitions
	 * Transition --> locks
	 */
	public static HashMap<Integer, ArrayList<Integer>> locksNeed = new HashMap<Integer, ArrayList<Integer>>();
	/**
	 * Threads enabled at current transition
	 * Transition --> threads
	 */
	public static HashMap<Integer, ArrayList<String>> enabledThread = new HashMap<Integer, ArrayList<String>>();
	/**
	 * Store the memory accessed by each transition
	 * Integer --> Long
	 */
	public static HashMap<Integer, Long> accessedMem = new HashMap<Integer, Long>();
	/**
	 * Increment transition number
	 * @param thread thread id
	 */
	public synchronized static void incr(String thread) {
		LongCounter tid = threadTransition.get(thread);
		if (tid == null) {
			threadTransition.put(thread, new LongCounter(1));
		} else {
			threadTransition.get(thread).inc();
		}
	}
	
	/**
	 * Get current transition number of a thread
	 * @param thread
	 * @return
	 */
	public synchronized static long getTransitionNum(String thread) {
		LongCounter tid = threadTransition.get(thread);
		if (tid == null) {
			threadTransition.put(thread, new LongCounter(0));
			tid = new LongCounter(0);
		}
		return tid.val;
	}
	
	/**
	 * Find the locks needed by this transition
	 * @param tranid transition id
	 * @param locks locks
	 */
	public synchronized static void locksNeed(int tranid, ArrayList<Integer> locks) {
		ArrayList<Integer> l = locksNeed.get(tranid);
		if (l == null) {
			locksNeed.put(tranid, locks);
		}
	}
	
	/**
	 * Store enabled thread in each transition
	 * @param tranid transition number
	 * @param thread enabled thread list
	 */
	public synchronized static void enabledThread(int tranid, ArrayList<String> thread) {
		ArrayList<String> t = enabledThread.get(tranid);
		if (t == null) {
			enabledThread.put(tranid, thread);
		}
	}
	
	/**
	 * Store memory accessed in each transition
	 * @param tranid transition number
	 * @param mem memory accessed
	 */
	public synchronized static void accessMem(int tranid, Long mem) {
		Long m = accessedMem.get(tranid);
		if (m == null) {
			accessedMem.put(tranid, mem);
		}
	}
}
