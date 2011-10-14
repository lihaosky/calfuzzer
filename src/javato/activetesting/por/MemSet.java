package javato.activetesting.por;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Class holding memory set
 * @author lihao
 *
 */
public class MemSet {
	/**
	 * Store the memory accessed by each thread
	 */
	public HashMap<String, ArrayList<MemInfo>> memSet = new HashMap<String, ArrayList<MemInfo>>();
	
	/**
	 * Put access memory to thread
	 * @param tid
	 * @param mem
	 */
	public synchronized void put(String tid, MemInfo mem) {
		ArrayList<MemInfo> memlist = memSet.get(tid);
		if (memlist == null) {
			memlist = new ArrayList<MemInfo>();
			memlist.add(mem);
			memSet.put(tid, memlist);
		} else {
			memlist.add(mem);
		}
	}
	
	/**
	 * Remove memory from thread
	 * @param tid
	 * @param mem
	 */
	public synchronized void remove(String tid, Long mem) {
		ArrayList<MemInfo> memlist = memSet.get(tid);
		
		if (memlist == null) {
			return;
		}
		for (int i = 0; i < memlist.size(); i++) {
			if (memlist.get(i).memid == mem) {
				memlist.remove(i);
			}
		}
	}

	/**
	* Get key set
	* @return key set
	*/
	public synchronized Set<String> getKeySet() {
		return memSet.keySet();
	}
}
