package javato.activetesting.analysis;

import javato.activetesting.common.Parameters;
import javato.activetesting.common.IIDAccessCounter;
import javato.activetesting.por.ThreadInfo;
import javato.activetesting.activechecker.ActiveChecker;

import java.util.LinkedList;

public class ObserverForActiveTesting extends Observer {
    private static SyncMethodCache cache = new SyncMethodCache();
    public static Analysis analysis;
    //private static AtomicLong counter = new AtomicLong(0);
    //private static boolean stopRW = false;
    private static IIDAccessCounter counters = new IIDAccessCounter();

    static {
        //if (Parameters.analysisClass != null) {
            try {
              Class t = Class.forName("javato.activetesting.por.PartialOrder");
	      // Class t = Class.forName("javato.activetesting.HybridAnalysis");
	    //     Class t = Class.forName("javato.activetesting.RaceFuzzerAnalysis");
                analysis = (Analysis) t.newInstance();
            } catch (Exception e) {
                System.err.println("Cannot find or instantiate Analysis class: " + Parameters.analysisClass + Thread.currentThread());
                e.printStackTrace();
                System.exit(1);
            }
        //}
    }


    public static java.lang.ThreadLocal lockStack = new java.lang.ThreadLocal() {
        protected synchronized Object initialValue() {
            return new LinkedList();
        }
    };

    public static java.lang.ThreadLocal iidStack = new java.lang.ThreadLocal() {
        protected synchronized Object initialValue() {
            return new LinkedList<Integer>();
        }
    };

    public static void myMethodEnterBefore(int iid) {
        analysis.methodEnterBefore(new Integer(iid), uniqueThreadId(Thread.currentThread(), true, null, iid));
    }

    public static void myMethodExitAfter(int iid) {
        analysis.methodExitAfter(new Integer(iid), uniqueThreadId(Thread.currentThread(), true, null, iid));
    }


    public static void myLockBefore(int iid, Object lock, String sig) {
        boolean isSynchronized = cache.isSynchronized(iid, lock, sig);
        if (isSynchronized) {
            ((LinkedList) lockStack.get()).addFirst(lock);
            analysis.lockBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock),lock);
        } else {
            ((LinkedList) lockStack.get()).addFirst(null);
        }
        ((LinkedList<Integer>) iidStack.get()).addFirst(iid);
    }

    public static void myLockBefore(int iid, int oid, String className) {
        Class c = null;
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        analysis.lockBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), oid, c);
    }

    public static void myLockBefore(int iid, Object lock) {
        analysis.lockBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock),lock);
    }

    public static void myUnlockAfter(int iid) {
        LinkedList ls = ((LinkedList) lockStack.get());
        LinkedList<Integer> is = ((LinkedList<Integer>) iidStack.get());
        Object lock = ls.removeFirst();
        int entryIid = is.removeFirst();
        while (iid != entryIid + 1) { // this is a hack; needs better handling in future
            if (lock != null) {
                analysis.unlockAfter(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock));
            }
            lock = ls.removeFirst();
            entryIid = is.removeFirst();
        }
        if (iid != entryIid + 1) {
            System.out.println("thread " + uniqueId(Thread.currentThread()));
        }
        assert iid == entryIid + 1;
        if (lock != null) {
            analysis.unlockAfter(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock));
        }
    }

    public static void myUnlockAfter(int iid, int oid) {
        analysis.unlockAfter(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), oid);
    }

    public static void myUnlockAfter(int iid, Object lock) {
        analysis.unlockAfter(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock));
    }

    public static void myNewExprInANonStaticMethodAfter(int iid, Object o, Object objOnWhichMethodIsInvoked) {
        analysis.newExprAfter(iid, uniqueId(o), uniqueId(objOnWhichMethodIsInvoked));
    }

    public static void myNewExprInAStaticMethodAfter(int iid, Object o) {
        analysis.newExprAfter(iid, uniqueId(o), 0);
    }

    public static void myStartBefore(int iid, Object t) {
//    	System.out.println("Child is" + t);
//   	System.out.println("Parent is " + Thread.currentThread());
//    	System.out.println("Line number is " + getIidToLine(iid));
    	
    	String parentId = uniqueThreadId(Thread.currentThread(), true, null, iid);
    	String childId = uniqueThreadId(t, false, parentId, iid);

    	synchronized (ActiveChecker.lock2) {
		  	ThreadInfo.put(parentId, Thread.currentThread());
		  	ThreadInfo.put(childId, (Thread)t);
		  	ThreadInfo.setStop(parentId, false);
		  	ThreadInfo.setStop(childId, false);
    	}
       analysis.startBefore(iid, parentId, uniqueThreadId(t, false, childId, iid));
        
       Parameters.isStarted = true;
        
//        System.out.println("Parent is " + uniqueThreadId(Thread.currentThread(), true, null, iid));
//        System.out.println("Child is " + uniqueThreadId(t, false, parentId, iid));
    }

    public static void myStartAfter(int iid, Object t) {
        analysis.startAfter(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), t);
    }


    public static void myWaitBefore(int iid, Object lock) {
        analysis.waitBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock));
    }

    public static void myWaitAfter(int iid, Object lock) {
        analysis.waitAfter(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock));
    }

    public static void myNotifyBefore(int iid, Object lock) {
        analysis.notifyBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock));
    }

    public static void myNotifyAllBefore(int iid, Object lock) {
        analysis.notifyAllBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueId(lock));
    }

    public static void myJoinAfter(int iid, Object thread) {
        analysis.joinAfter(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), uniqueThreadId(thread, false, null, iid));
    }

    public static void myReadBefore(int iid, Object o, int field) {
        if (counters.needToIgnore(iid)) return;
        analysis.readBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), id(o, field), false);
    }

    public static void myReadBefore(int iid, int clss, int field) {
        if (counters.needToIgnore(iid)) return;
        analysis.readBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), idInt(clss, field), false);
    }

    public static void myVReadBefore(int iid, Object o, int field) {
        if (counters.needToIgnore(iid)) return;
        analysis.readBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), id(o, field), true);
    }

    public static void myVReadBefore(int iid, int clss, int field) {
        if (counters.needToIgnore(iid)) return;
        analysis.readBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), idInt(clss, field), true);
    }

    public static void myWriteBefore(int iid, Object o, int field) {
        if (counters.needToIgnore(iid)) return;
        analysis.writeBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), id(o, field), false);
    }

    public static void myWriteBefore(int iid, int clss, int field) {
        if (counters.needToIgnore(iid)) return;
        analysis.writeBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), idInt(clss, field), false);
    }

    public static void myVWriteBefore(int iid, Object o, int field) {
        if (counters.needToIgnore(iid)) return;
        analysis.writeBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), id(o, field), true);
    }

    public static void myVWriteBefore(int iid, int clss, int field) {
        if (counters.needToIgnore(iid)) return;
        analysis.writeBefore(iid, uniqueThreadId(Thread.currentThread(), true, null, iid), idInt(clss, field), true);
    }


    public static void myWriteAfter(int iid, String local, Object value, String type) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, type);
    }

    public static void myWriteAfter(int iid, String local, byte value) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, "java.lang.Byte");
    }

    public static void myWriteAfter(int iid, String local, char value) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, "java.lang.Character");
    }

    public static void myWriteAfter(int iid, String local, short value) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, "java.lang.Short");
    }

    public static void myWriteAfter(int iid, String local, int value) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, "java.lang.Integer");
    }

    public static void myWriteAfter(int iid, String local, long value) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, "java.lang.Long");
    }

    public static void myWriteAfter(int iid, String local, float value) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, "java.lang.Float");
    }

    public static void myWriteAfter(int iid, String local, double value) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, "java.lang.Double");
    }

    public static void myWriteAfter(int iid, String local, boolean value) {
        analysis.writeAfter(iid, Thread.currentThread(), local, value, "java.lang.Boolean");
    }

    public static void myOpenDeterministicBlock(int iid) {
        analysis.openDeterministicBlock(uniqueId(Thread.currentThread()));
    }

    public static void myCloseDeterministicBlock(int iid) {
        analysis.closeDeterministicBlock(uniqueId(Thread.currentThread()));
    }

    /** Parameter 'invariant' must be serializable. */
    public static void requireDeterministic(Object invariant) {
        analysis.requireDeterministic(uniqueThreadId(Thread.currentThread(), true, null, 1), invariant);
    }

    /** Parameter 'invariant' must be serializable. */
    public static void assertDeterministic(Object invariant) {
        analysis.assertDeterministic(uniqueThreadId(Thread.currentThread(), true, null, 1), invariant);
    }
}
