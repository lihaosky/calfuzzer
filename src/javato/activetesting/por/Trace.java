package javato.activetesting.por;

import javato.activetesting.common.Parameters;

/**
 * Trace operation class
 * @author lihao
 *
 */
public class Trace {
	/**
	 * Original argument
	 */
	public static String originalTrace;
	/**
	 * Store trace
	 */
	public static String[] trace;
	/**
	 * Current executed thread
	 */
	public static int curIndex = 0;
	/**
	 * No one to be executed
	 */
	public static final int Finished = 0;
	/**
	 * This one to be executed
	 */
	public static final int execute = 1;
	
	/**
	 * Parse trace argument
	 * @param arg trace argument
	 */
	public synchronized static void parse(String arg) {
		if (arg == null) {
			trace = null;
			originalTrace = null;
			return;
		}
		originalTrace = arg;
		trace = arg.split("#");
		curIndex = 0;
		if (Parameters.isDebug) {
			System.out.println("Trace is " + arg);
			System.out.println("Trace is:");
			for (int i = 0; i < trace.length; i++) {
				System.out.println(trace[i]);
			}
		}
	}
	
	/**
	 * Get current thread to be executed
	 * @return current thread to be executed, null if all executed
	 */
	public synchronized static String getCurrent() {
		if (trace == null) {
			return null;
		}
		if (curIndex >= trace.length) {
			return null;
		}
		return trace[curIndex];
	}
	
	/**
	 * Move to next trace
	 */
	public synchronized static void next() {
		curIndex++;
	}
	
	/**
	 * Get trace length
	 * @return trace length
	 */
	public synchronized static int getLength() {
		return trace.length;
	}
	
	/**
	 * Get current index of transition
	 * @return current transition's index
	 */
	public synchronized static int getCurIndex() {
		return curIndex;
	}
	
	public synchronized static void append(String s) {
		if (originalTrace == null) {
			originalTrace = s;
			curIndex = 0;
		} else {
			originalTrace = originalTrace + "#" + s;
		}
		trace = originalTrace.split("#");
	}
}
