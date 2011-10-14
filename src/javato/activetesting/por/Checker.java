package javato.activetesting.por;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javato.activetesting.analysis.Observer;
import javato.activetesting.common.Parameters;
import javato.activetesting.activechecker.ActiveChecker;

/**
 * This class check data race and deadlock and find next path to explore
 * @author lihao
 */
public class Checker {
	/**
	 * Memory set
	 */
	private MemSet ms;
	/**
	 * Lock set
	 */
	private LockSet ls;
	/**
	 * Vector clock tracker
	 */
	private VectorClockTracker vcTracker;
	
	/**
	 * Constructor
	 * @param ms
	 * @param ls
	 * @param vcTracker
	 */
	public Checker(MemSet ms, LockSet ls, VectorClockTracker vcTracker) {
		this.ms = ms;
		this.ls = ls;
		this.vcTracker = vcTracker;
	}
	
	/**
	* Get current time
	*/
	private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
	
    /**
     * Check race and deadlock condition
     * If a deadlock is found, stop the execution
     */
    public synchronized void checkRaceAndDeadlock() {
    	String curThread = Trace.getCurrent();
    	if (curThread == null) {
    		//Wait for all the thread to reach transition point
    		while (!ThreadInfo.isAllStop()) {
		  		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    		}
	    	PrintWriter pw;
		    if (Parameters.isDebug) {
		    	System.out.println("**********************************************");
		    	System.out.println("****************CHECK DEADLOCK****************");
		    	System.out.println("**********************************************");
		    }
	    	//Check deadlock
	    	ArrayList<String> al = findEnable();
	    	//If no thread is enabled, then a deadlock is resulted
	    	if (al.size() == 0) {
	    		System.out.println("Deadlock found! The path is " + Trace.originalTrace + " at " + getDateTime());
	    		try {
					pw = new PrintWriter(new FileOutputStream(Parameters.ERROR_FILE, true));
					pw.println("Deadlock found! The path is " + Trace.originalTrace + " at " + getDateTime());
					pw.close();
					pw = new PrintWriter(new FileOutputStream(Parameters.DONE_FILE, true));
					pw.println(Trace.originalTrace);
					pw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	    		System.exit(0);
	    	}
	    	
		    if (Parameters.isDebug) {
		      	System.out.println("**********************************************");
		    	System.out.println("*****************CHECK RACE*******************");
		    	System.out.println("**********************************************");
		    }

		    synchronized (ActiveChecker.lock2) {
	    	//Check race
	    	for (String t1 : ms.getKeySet()) {
	    		for (String t2 : ms.getKeySet()) {
	    			if (!t1.equals(t2)) {
	    				ArrayList<MemInfo> t1al = ms.memSet.get(t1);
	    				ArrayList<MemInfo> t2al = ms.memSet.get(t2);
	    				
	    				for (int i = 0; i < t1al.size(); i++) {
	    					for (int j = 0; j < t2al.size(); j++) {
	    						MemInfo m1 = t1al.get(i);
	    						MemInfo m2 = t2al.get(j);
	    						if (!m1.isLock && !m2.isLock) {
		    						if (m1.memid == m2.memid && (m1.isWrite || m2.isWrite)) {
		    							System.out.println("Data race detected! At line: " + Observer.getIidToLine(m1.iid) + " And line " + Observer.getIidToLine(m2.iid) + " at " + getDateTime());
		    				    		try {
		    								pw = new PrintWriter(new FileOutputStream(Parameters.ERROR_FILE, true));
		    								pw.println("Data race detected! At line: " + Observer.getIidToLine(m1.iid) + " And line " + Observer.getIidToLine(m2.iid) + " at " + getDateTime());
		    								pw.close();
		    							} catch (FileNotFoundException e) {
		    								e.printStackTrace();
		    							}
		    						}
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
	    	}
	    	
	    	
	    	findNextPath();
    	}
    }
    
    /**
     * Find a list of thread ids which are enabled in current state
     * @return A list of enabled thread at current state
     */
    public ArrayList<String> findEnable() {
       if (Parameters.isDebug) {
    	   System.out.println("********************************************");
    	   System.out.println("***************FIND ENABLED*****************");
    	   System.out.println("********************************************");
       }
    	ArrayList<String> al = new ArrayList<String>();

    	synchronized (ActiveChecker.lock2) {
    	for (String tid : ThreadInfo.getKeySet()) {
    		Thread t = ThreadInfo.getThread(tid);
    	    if (Parameters.isDebug) {
	    		if (t != null) {
	    			System.out.println(tid + "'s state is " + t.getState());
	    		}
    		}
    		//If this thread is new, waiting or terminated, it is not enabled
    		if (t == null || t.getState() == Thread.State.BLOCKED || t.getState() == Thread.State.TERMINATED || t.getState() == Thread.State.WAITING || t.getState() == Thread.State.NEW) {
    			continue;
    		}
    		ArrayList<LockInfo> alli = ls.lockset.get(tid);       //Get the locks this thread accessed
    		boolean isEnabled = true;
    		if (alli != null) {
    			for (int i = 0; i < alli.size(); i++) {
    				LockInfo li = alli.get(i);                    
    				//If the lock is needed by this thread and it is already acquired by other thread, this thread is not enabled
    				if (li != null && li.isAcquired == false) {
    					int lockid = li.lockid;
    					if (ls.lockThread.containsKey(lockid)) {
    						isEnabled = false;
    					}
    				}
    			}
    		}
    		if (isEnabled) {
    			al.add(tid);
    		}
    	}
    	}
    	
    	if (Parameters.isDebug) {
    		System.out.println("Enabled threads are:");
	    	for (int i = 0; i < al.size(); i++) {
	    		System.out.println(al.get(i));
	    	}
    	}
    	return al;
    }
    
    /**
     * Find next path that can be explored
     */
    public void findNextPath() {
    	if (Parameters.isDebug) {
    		System.out.println("**************************************************");
    		System.out.println("**************FIND NEXT PATH**********************");
    		System.out.println("**************************************************");
    	}
    	PrintWriter pw = null;
    	
    	if (Trace.trace != null) {
	    	//Dynamic Partial Order Reduction algorithm 
	    	for (String tid : ThreadInfo.threads.keySet()) {
	    		ArrayList<MemInfo> meList = ms.memSet.get(tid);
		    		if (meList != null) {
			    		//Consider each transition
			    		for (int i = Trace.trace.length - 1; i >= 0; i--) {
			    			//Happens before
			    			if (Trace.trace[i].equals(tid)) {
			    				continue;
			    			}
			    			//Is it dependent with tid?
			    			Long me = TransitionInfo.accessedMem.get(i);
			    			int j;
			    			for (j = 0; j < meList.size(); j++) {
			    				if (meList.get(j).memid == me) {
			    					break;
			    				}
			    			}
			    			//Not dependent, check transition before
			    			if (j == meList.size()) {
			    				continue;
			    			}
			    			//Is it happens before?
			    			int k = 0;
			    			for (j = 0; j <= i; j++) {
			    				if (Trace.trace[j].equals(Trace.trace[i])) {
			    					k++;
			    				}
			    			}
			    			//i happens before tid, decide using vector clock
			    			if (k <= vcTracker.getHappenBefore(tid, Trace.trace[i])) {
			    				continue;
			    			}
			    			//Can they be co-enabled? Decide by checking their transition lock list
			    			boolean enabled = true;
			    			ArrayList<LockInfo> lockList = ls.lockset.get(tid); //Lock list of thread tid
			    			ArrayList<Integer> tranlockList = TransitionInfo.locksNeed.get(i);
			    			if (lockList != null) {
				    			for (j = 0; j < lockList.size(); j++) {
				    				if (lockList.get(j).isAcquired) {
				    					if (tranlockList != null) {
					    					for (k = 0; k < tranlockList.size(); k++) {
					    						if (lockList.get(j).lockid == tranlockList.get(k)) {
					    							enabled = false;
					    							break;
					    						}
					    					}
				    					}
				    				}
				    				if (enabled == false) {
				    					break;
				    				}
				    			}
			    			}
			    			//Can not be co-enabled
			    			if (enabled == false) {
			    				continue;
			    			}
			    			
			    			if (Parameters.isDebug) {
			    				System.out.println("Backtrack point found!");
			    				System.out.println("Thread " + tid + "can be add before transition " + (i + 1));
			    			}
			    			
			    			ArrayList<String> enabledThread = TransitionInfo.enabledThread.get(i - 1);
			    			StringBuffer sb = new StringBuffer();
			    			for (j = 0; j < i; j++) {
			    				sb.append(Trace.trace[j]);
			    				sb.append("#");
			    			}
			    			
			    			//This thread is enabled, add it to backtrack point
			    			if (enabledThread.contains(tid)) {
			    				String toExplore = sb + tid;
			    				try {
									pw = new PrintWriter(new FileOutputStream(Parameters.EXPLORE_FILE, true));
									pw.println(toExplore);
									if (Parameters.isDebug) {
										System.out.println("Backtrack path is " + toExplore);
										try {
											Thread.sleep(2000);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} finally {
									pw.close();
								}
			    			} 
			    			//This thread is not enabled, add all enabled thread to backtracking point
			    			else {
			    				try {
									pw = new PrintWriter(new FileOutputStream(Parameters.EXPLORE_FILE, true));
				    				for (j = 0; j < enabledThread.size(); j++) {
				    					String toExplore = sb + enabledThread.get(j);
				    					pw.println(toExplore);
				    				}
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} finally {
									pw.close();
								}
			    			}
			    			break;
			    		}
	    		}
	    	}
    	}
    	
    	ArrayList<String> enabled = null;
    	enabled = findEnable();

    	if (enabled.size() != 0) {
    		Random rand = new Random();
    		String next = enabled.get(rand.nextInt(enabled.size()));
    		Trace.append(next);
    		if (Parameters.isDebug) {
				System.out.println("Next is " + Trace.originalTrace);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	} else {
			try {
				pw = new PrintWriter(new FileOutputStream(Parameters.DONE_FILE, true));
				String toExplore = Trace.originalTrace;
				pw.println(toExplore);
				if (Parameters.isDebug) {
					System.out.println("Thread to explore is " + toExplore);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				pw.close();
			}
    	}
    }
}
