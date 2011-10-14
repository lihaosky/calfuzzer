package javato.activetesting.por;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Store the locks accessed by threads
 * @author lihao
 *
 */
public class LockSet {
	/**
	 * Stores locks acquired or to be acquired by each thread
	 * ThreadId --> LockInfo list
	 */
	public HashMap<String, ArrayList<LockInfo>> lockset = new HashMap<String, ArrayList<LockInfo>>();
	/**
	 * Store the thread that acquired a lock
	 * LockId --> ThreadId
	 */
	public HashMap<Integer, String> lockThread = new HashMap<Integer, String>();
	
	/**
	 * Put a lock into a lock list accessed by thread
	 * @param tid thread id
	 * @param lock lock information
	 */
	public synchronized void put(String tid, LockInfo lock) {
		ArrayList<LockInfo> locklist = lockset.get(tid);
		if (locklist == null) {
			locklist = new ArrayList<LockInfo>();
			locklist.add(lock);
			lockset.put(tid, locklist);
		} else {
			locklist.add(lock);
			lockset.put(tid, locklist);
		}
	}
	
	/**
	 * Remove a lock from the lock list accessed by thread
	 * @param tid thread id
	 * @param lockid lock id
	 */
	public synchronized void remove(String tid, int lockid) {
		ArrayList<LockInfo> locklist = lockset.get(tid);
		if (locklist != null) {
			for (int i = 0; i < locklist.size(); i++) {
				if (locklist.get(i).lockid == lockid) {
					locklist.remove(i);
				}
			}
		}
	}
	
	/**
	 * A lock is acquired by a thread
	 * @param lockid lock id
	 * @param tid thread id
	 */
	public synchronized void acquire(Integer lockid, String tid) {
		String t = lockThread.get(lockid);
		if (t != null) {
			t = tid;
		} else {
			lockThread.put(lockid, tid);
		}
	}
	
	/**
	 * A lock is released by a thread
	 * @param lockid lock id
	 */ 
	public synchronized void release(Integer lockid) {
		String t = lockThread.get(lockid);
		if (t != null) {
			lockThread.remove(lockid);
		}
	}
}
