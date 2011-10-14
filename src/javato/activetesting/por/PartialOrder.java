package javato.activetesting.por;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javato.activetesting.activechecker.ActiveChecker;
import javato.activetesting.analysis.AnalysisImpl;
import javato.activetesting.common.Parameters;
import javato.activetesting.reentrant.IgnoreRentrantLock;

/**
 * This class handles transition 
 * @author lihao
 *
 */
public class PartialOrder extends AnalysisImpl {
    /**
     * Vector clock tracker
     */
    private VectorClockTracker vcTracker;
    private IgnoreRentrantLock ignoreRentrantLock;
    /**
     * Memory set
     */
    private MemSet ms;
    /**
     * Lock set
     */
    private LockSet ls;
    /**
     * Checker to check race and deadlock
     */
    private Checker ck;
    
    public void initialize() {
        synchronized (ActiveChecker.lock2) {
            vcTracker = new VectorClockTracker();
            ms = new MemSet();
            ls = new LockSet();
            ck = new Checker(ms, ls, vcTracker);
            ignoreRentrantLock = new IgnoreRentrantLock();
        }
    }
    
    /**
     * Callback function before a lock
     */
    public void lockBefore(Integer iid, String thread, Integer lock, Object actualLock) {
    	if (Parameters.isStarted) {
    		if (Parameters.isDebug) {
    			System.out.println("Thread " + thread + " wants to acquire lock " + lock + " at line " + javato.activetesting.analysis.Observer.getIidToLine(iid));
    		}
    		LockInfo li = null;
    		synchronized (ActiveChecker.lock2) {
		  		li = new LockInfo(lock, false);
			  	ls.put(thread, li);
			  	MemInfo mi = new MemInfo(lock, true, false, true, iid);
			  	ms.put(thread, mi);
			  	ThreadInfo.setStop(thread, true);
        }
        
	    	while (true) {
	    		synchronized (ActiveChecker.lock1) {
			  		String curThread = Trace.getCurrent();
			  		if (curThread == null) {
			  			if (Parameters.isDebug) {
			  				System.out.println("No one to execute");
			  			}
				  		ck.checkRaceAndDeadlock();                //Check race and deadlock and find next path that can be explored
			  		} else {
			  			if (curThread.equals(thread)) {
			  				if (Parameters.isDebug) {
			  					System.out.println("Next thread to run is " + thread);
			  				}
			  				break;
			  			}
			  		}
			  		}
			  		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	}
	    	
	    	while (!ThreadInfo.isAllStop());                        //Wait for all the thread to reach transition point
        	ArrayList<String> al2 = ck.findEnable();                //Find enabled thread after this transition
        	TransitionInfo.enabledThread(Trace.curIndex - 1, al2);  //Store enabled threads after this transition
        	
        	if (ignoreRentrantLock.lockBefore(thread, lock)) {
            	
            	//Find the locks needed by this transition
            	ArrayList<LockInfo> al = ls.lockset.get(thread);
            	ArrayList<Integer> al1 = new ArrayList<Integer>();
            	if (al != null) {
	            	for (int i = 0; i < al.size(); i++) {
	            		LockInfo li1 = al.get(i);
	            		if (li1.lockid != lock) {
	            			al1.add(li1.lockid);
	            		}
	            	}
            	}
            	TransitionInfo.locksNeed(Trace.curIndex, al1);      //Store the locks needed by this transition
            	
            	synchronized (ActiveChecker.lock2) {
		          	li.isAcquired = true;                               //Set this lock to be already acquired
		          	ls.acquire(lock, thread);                           //Map it to thread
            	}
            	TransitionInfo.incr(thread);                        //Increment the transition number of the thread
            	TransitionInfo.accessMem(Trace.curIndex, new Long(lock)); //Store memory accessed in this transition
            	ThreadInfo.setStop(thread, false);                  //This thread hasn't reach next transition point
            	Trace.next();                                       //Go to next transition
	    	}
    	}
    }
    
    /**
     * Callback function after a lock
     * Don't need to stop before unlock
     */
    public void unlockAfter(Integer iid, String thread, Integer lock) {
    	if (Parameters.isStarted) {
            if (ignoreRentrantLock.unlockAfter(thread, lock)) {
            	if (Parameters.isDebug) {
            		System.out.println("Thread " + thread + " released lock " + lock + " at line " + javato.activetesting.analysis.Observer.getIidToLine(iid));
            	}
		          	synchronized (ActiveChecker.lock2) {
				        	ls.release(lock);                           //This lock is released
				        	ls.remove(thread, lock);                    //This lock is removed from lock list of that thread
				        	ms.remove(thread, lock.longValue());        //Remove lock
            	}
            }
    	}
    }
    
    /**
     * Callback function before start
     */
    public void startBefore(Integer iid, String parent, String child) {
        synchronized (ActiveChecker.lock2) {
            vcTracker.startBefore(parent, child);
        }
    }
    
    /**
     * Callback function after join
     */
    public void joinAfter(Integer iid, String parent, String child) {
        synchronized (ActiveChecker.lock2) {
            vcTracker.joinAfter(parent, child);
        }
    }
    
    /**
     * Callback function before read of memory
     */
    public void readBefore(Integer iid, String thread, Long memory, boolean isVolatile) {
    	if (Parameters.isStarted) {
    		if (Parameters.isDebug) {
    			System.out.println("Thread " + thread + " want to read memory " + memory + " at line " + javato.activetesting.analysis.Observer.getIidToLine(iid));
    		}

    		System.out.println("Thread " + thread + " read at line " + javato.activetesting.analysis.Observer.getIidToLine(iid));
    		
    		synchronized (ActiveChecker.lock2) {
		  		MemInfo mi = new MemInfo(memory, true, false, false, iid);
			  	ms.put(thread, mi);
			  	ThreadInfo.setStop(thread, true);
        }
        
	    	while (true) {
	    		synchronized (ActiveChecker.lock1) {
			  		String curThread = Trace.getCurrent();
			  		if (curThread == null) {
			  			if (Parameters.isDebug) {
			  				System.out.println("No one to execute");
			  			}
				  		ck.checkRaceAndDeadlock();                //Check race and deadlock and find next path that can be explored
			  		} else {
			  			if (curThread.equals(thread)) {
			  				if (Parameters.isDebug) {
			  					System.out.println("Next thread to run is " + thread);
			  				}
			  				break;
			  			}
			  		}
			  		}
			  		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	}

	    	while (!ThreadInfo.isAllStop());                    //Wait for all the thread to reach transition point
        	ArrayList<String> al2 = ck.findEnable();               //Find enabled thread after this transition
        	TransitionInfo.enabledThread(Trace.curIndex - 1, al2);  //Store enabled threads after this transition
        	
        	//Find the locks needed by this transition
        	ArrayList<LockInfo> al = ls.lockset.get(thread);
        	ArrayList<Integer> al1 = new ArrayList<Integer>();
        	if (al != null) {
	        	for (int i = 0; i < al.size(); i++) {
	        		LockInfo li1 = al.get(i);
	    			al1.add(li1.lockid);
	        	}
        	}
        	TransitionInfo.locksNeed(Trace.curIndex, al1);      //Store the locks needed by this transition

        	synchronized (ActiveChecker.lock2) {
        		ms.remove(thread, memory);                          //Remove the memory from memory set
        	}
        	
        	TransitionInfo.incr(thread);                        //Increment the transition number of the thread
        	TransitionInfo.accessMem(Trace.curIndex, new Long(memory)); //Store memory accessed in this transition
        	ThreadInfo.setStop(thread, false);                  //This thread hasn't reach next transition point
        	Trace.next();                                       //Go to next transition
    	}
    }

    /**
     * Callback function before write of memory
     */
    public void writeBefore(Integer iid, String thread, Long memory, boolean isVolatile) {
    	if (Parameters.isStarted) {
    		if (Parameters.isDebug) {
    			System.out.println("Thread " + thread + " wants to write " + memory + " at line " + javato.activetesting.analysis.Observer.getIidToLine(iid));
    		}

    		MemInfo mi = new MemInfo(memory, false, true, false, iid);
    		synchronized (ActiveChecker.lock2) {
	    		ms.put(thread, mi);
	    	}
	    	ThreadInfo.setStop(thread, true);
        	
	    	while (true) {
	    		synchronized (ActiveChecker.lock1) {
			  		String curThread = Trace.getCurrent();
			  		if (curThread == null) {
			  			if (Parameters.isDebug) {
			  				System.out.println("No one to execute");
			  			}
				  		ck.checkRaceAndDeadlock();                //Check race and deadlock and find next path that can be explored
			  		} else {
			  			if (curThread.equals(thread)) {
			  				if (Parameters.isDebug) {
			  					System.out.println("Next thread to run is " + thread);
			  				}
			  				break;
			  			}
			  		}
			  		}
			  		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	}
	    	
	    	while (!ThreadInfo.isAllStop());                    //Wait for all the thread to reach transition point
        	ArrayList<String> al2 = ck.findEnable();               //Find enabled thread after this transition
        	TransitionInfo.enabledThread(Trace.curIndex - 1, al2);  //Store enabled threads after this transition
        	
        	//Find the locks needed by this transition
        	ArrayList<LockInfo> al = ls.lockset.get(thread);
        	ArrayList<Integer> al1 = new ArrayList<Integer>();
        	if (al != null) {
	        	for (int i = 0; i < al.size(); i++) {
	        		LockInfo li1 = al.get(i);
	    			al1.add(li1.lockid);
	        	}
        	}
        	TransitionInfo.locksNeed(Trace.curIndex, al1);      //Store the locks needed by this transition

        	synchronized (ActiveChecker.lock2) {
        		ms.remove(thread, memory);                          //Remove the memory from memory set
        	}
        	
        	TransitionInfo.incr(thread);                        //Increment the transition number of the thread
        	TransitionInfo.accessMem(Trace.curIndex, new Long(memory)); //Store memory accessed in this transition
        	ThreadInfo.setStop(thread, false);                  //This thread hasn't reach next transition point
        	Trace.next();                                       //Go to next transition
    	}
    }
    
    /**
     * Callback function before wait
     */
    public void waitBefore(Integer iid, String thread, Integer lock) {
    	if (Parameters.isStarted) {
    		if (Parameters.isDebug) {
    			System.out.println("Thread " + thread + " wants to wait before at lock " + lock + " at line " + javato.activetesting.analysis.Observer.getIidToLine(iid));
    		}
    		MemInfo mi = new MemInfo(lock, false, true, false, iid);
    		synchronized (ActiveChecker.lock2) {
	    		ms.put(thread, mi);
	    	}
	    	ThreadInfo.setStop(thread, true);
	    	
	    	while (true) {
	    		synchronized (ActiveChecker.lock1) {
			  		String curThread = Trace.getCurrent();
			  		if (curThread == null) {
			  			if (Parameters.isDebug) {
			  				System.out.println("No one to execute");
			  			}
				  		ck.checkRaceAndDeadlock();                //Check race and deadlock and find next path that can be explored
			  		} else {
			  			if (curThread.equals(thread)) {
			  				if (Parameters.isDebug) {
			  					System.out.println("Next thread to run is " + thread);
			  				}
			  				break;
			  			}
			  		}
			  		}
			  		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	}
	    	
	    	while (!ThreadInfo.isAllStop());                    //Wait for all the thread to reach transition point
        	ArrayList<String> al2 = ck.findEnable();               //Find enabled thread after this transition
        	TransitionInfo.enabledThread(Trace.curIndex - 1, al2);  //Store enabled threads after this transition
        	
        	//Find the locks needed by this transition
        	ArrayList<LockInfo> al = ls.lockset.get(thread);
        	ArrayList<Integer> al1 = new ArrayList<Integer>();
        	if (al != null) {
	        	for (int i = 0; i < al.size(); i++) {
	        		LockInfo li1 = al.get(i);
	    			al1.add(li1.lockid);
	        	}
        	}
        	TransitionInfo.locksNeed(Trace.curIndex, al1);      //Store the locks needed by this transition

        	synchronized (ActiveChecker.lock2) {
        		ms.remove(thread, (long)lock);                          //Remove the memory from memory set
        	}
        	TransitionInfo.incr(thread);                        //Increment the transition number of the thread
        	TransitionInfo.accessMem(Trace.curIndex, new Long(lock)); //Store memory accessed in this transition
        	ThreadInfo.setStop(thread, false);                  //This thread hasn't reach next transition point
        	Trace.next();                                       //Go to next transition
    	}
    }

    /**
     * Callback function after wait
     */
    public void waitAfter(Integer iid, String thread, Integer lock) {
//        if (!Parameters.trackLockRaces) {
//            synchronized (ActiveChecker.lock) {
//                vcTracker.waitAfter(thread, lock);
//            }
//        }
    }

    /**
     * Callback before notify
     */
    public void notifyBefore(Integer iid, String thread, Integer lock) {
    	if (Parameters.isStarted) {
    		if (Parameters.isDebug) {
    			System.out.println("Thread " + thread + " wants to notify at " + lock + " at line " + javato.activetesting.analysis.Observer.getIidToLine(iid));
    		}
    		MemInfo mi = new MemInfo(lock, false, true, false, iid);
    		synchronized (ActiveChecker.lock2) {
	    		ms.put(thread, mi);
	    	}
	    	ThreadInfo.setStop(thread, true);

        	
	    	while (true) {
	    		synchronized (ActiveChecker.lock1) {
			  		String curThread = Trace.getCurrent();
			  		if (curThread == null) {
			  			if (Parameters.isDebug) {
			  				System.out.println("No one to execute");
			  			}
				  		ck.checkRaceAndDeadlock();                //Check race and deadlock and find next path that can be explored
			  		} else {
			  			if (curThread.equals(thread)) {
			  				if (Parameters.isDebug) {
			  					System.out.println("Next thread to run is " + thread);
			  				}
			  				break;
			  			}
			  		}
			  		}
			  		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	}

	    	while (!ThreadInfo.isAllStop());                    //Wait for all the thread to reach transition point
        	ArrayList<String> al2 = ck.findEnable();               //Find enabled thread after this transition
        	TransitionInfo.enabledThread(Trace.curIndex - 1, al2);  //Store enabled threads after this transition
        	
        	//Find the locks needed by this transition
        	ArrayList<LockInfo> al = ls.lockset.get(thread);
        	ArrayList<Integer> al1 = new ArrayList<Integer>();
        	if (al != null) {
	        	for (int i = 0; i < al.size(); i++) {
	        		LockInfo li1 = al.get(i);
	    			al1.add(li1.lockid);
	        	}
        	}
        	TransitionInfo.locksNeed(Trace.curIndex, al1);      //Store the locks needed by this transition
        	synchronized (ActiveChecker.lock2) {
        		ms.remove(thread, (long)lock);                          //Remove the memory from memory set
        	}
        	TransitionInfo.incr(thread);                        //Increment the transition number of the thread
        	TransitionInfo.accessMem(Trace.curIndex, new Long(lock)); //Store memory accessed in this transition
        	ThreadInfo.setStop(thread, false);                  //This thread hasn't reach next transition point
        	Trace.next();                                       //Go to next transition
    	}
    }

    /**
     * Callback function before notifyAll
     */
    public void notifyAllBefore(Integer iid, String thread, Integer lock) {
    	if (Parameters.isStarted) {
    		if (Parameters.isDebug) {
    			System.out.println("Thread " + thread + " wants to notify all at lock " + lock + " at line " + javato.activetesting.analysis.Observer.getIidToLine(iid));
    		}
    		
	    	MemInfo mi = new MemInfo(lock, false, true, false, iid);
	    	synchronized (ActiveChecker.lock2) {
	    		ms.put(thread, mi);
	    	}
	    	ThreadInfo.setStop(thread, true);
        	
	    	while (true) {
	    		synchronized (ActiveChecker.lock1) {
			  		String curThread = Trace.getCurrent();
			  		if (curThread == null) {
			  			if (Parameters.isDebug) {
			  				System.out.println("No one to execute");
			  			}
				  		ck.checkRaceAndDeadlock();                //Check race and deadlock and find next path that can be explored
			  		} else {
			  			if (curThread.equals(thread)) {
			  				if (Parameters.isDebug) {
			  					System.out.println("Next thread to run is " + thread);
			  				}
			  				break;
			  			}
			  		}
			  	}
			  		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	}

	    	while (!ThreadInfo.isAllStop());                    //Wait for all the thread to reach transition point
        	ArrayList<String> al2 = ck.findEnable();               //Find enabled thread after this transition
        	TransitionInfo.enabledThread(Trace.curIndex - 1, al2);  //Store enabled threads after this transition
        	
        	//Find the locks needed by this transition
        	ArrayList<LockInfo> al = ls.lockset.get(thread);
        	ArrayList<Integer> al1 = new ArrayList<Integer>();
        	if (al != null) {
	        	for (int i = 0; i < al.size(); i++) {
	        		LockInfo li1 = al.get(i);
	    			al1.add(li1.lockid);
	        	}
        	}
        	TransitionInfo.locksNeed(Trace.curIndex, al1);      //Store the locks needed by this transition
        	synchronized (ActiveChecker.lock2) {
        		ms.remove(thread, (long)lock);                          //Remove the memory from memory set
        	}
        	TransitionInfo.incr(thread);                        //Increment the transition number of the thread
        	TransitionInfo.accessMem(Trace.curIndex, new Long(lock)); //Store memory accessed in this transition
        	ThreadInfo.setStop(thread, false);                  //This thread hasn't reach next transition point
        	Trace.next();                                       //Go to next transition
    	}
    }

    public void finish() {
        synchronized (ActiveChecker.lock2) {
        	PrintWriter pw = null;
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
			System.out.println("Finished");
        }
    }
}
