package javato.activetesting.atominfer;

import javato.activetesting.common.Parameters;
import javato.activetesting.lockset.LockSet;
import javato.activetesting.vc.VectorClock;
import java.util.*;

/**
 * Race tracker for the atomicity inference algorithm.
 * We track races on unique events and not on iids.
 * In addition to tracking memory races, we also
 * track races on locks.  However, we only track
 * lock-lock races, not lock-unlock or unlock-unlock
 * races.
 *
 * To implement this, we simply convert the integer lock to a
 * long representing memory.  This is guaranteed to be unique
 * since normal memory locations always have the upper bits set.
 */
public class AtomicRaceTracker {

    private LinkedHashSet<CommutativePair> alreadySeenRaces;

    // m -> t -> list(vc,ls->Set(iid))
    private Map<Long, Map<String, LinkedList<VCLockPair>>> readMap;
    private Map<Long, Map<String, LinkedList<VCLockPair>>> writeMap;
    private Map<Long, Map<String, LinkedList<VCLockPair>>> lockMap;

    public AtomicRaceTracker() {
        readMap = new TreeMap<Long, Map<String, LinkedList<VCLockPair>>>();
        writeMap = new TreeMap<Long, Map<String, LinkedList<VCLockPair>>>();
	lockMap = new TreeMap<Long, Map<String, LinkedList<VCLockPair>>>();

	alreadySeenRaces = new LinkedHashSet<CommutativePair>();
    }

    public void checkRace(Integer iid, String t, Long m, boolean isRead, VectorClock vc, LockSet ls, UniqueEvent e) {
        Map<String, LinkedList<VCLockPair>> threadLists1;
        Map<String, LinkedList<VCLockPair>> threadLists2 = null;
        if (isRead) {
            threadLists1 = writeMap.get(m);
        } else {
            threadLists1 = writeMap.get(m);
            threadLists2 = readMap.get(m);
        }
        if (threadLists1 != null) {
            checkRaceAux(iid, threadLists1, t, vc, ls, m, e);
        }
        if (threadLists2 != null) {
            checkRaceAux(iid, threadLists2, t, vc, ls, m, e);
        }
    }

    protected void checkRaceAux(Integer iid, Map<String, LinkedList<VCLockPair>> threadLists, String t, VectorClock vc, LockSet ls, Long m, UniqueEvent uniqueEvent) {
        for (String t2 : threadLists.keySet()) {
            if (!t2.equals(t)) {
                Long c2 = vc.getValue(t2);
                LinkedList<VCLockPair> vcs = threadLists.get(t2);
                for (VCLockPair c : vcs) {
                    if (c2 < c.getClock()) {
                        Map<LockSet, TreeSet<UniqueEvent>> lockSets = c.getLockSets();
                        for (LockSet ls2 : lockSets.keySet()) {
                            if (!ls2.intersects(ls)) {
                                TreeSet<UniqueEvent> uniqueEvents = lockSets.get(ls2);
                                CommutativePair cp;
                                for (UniqueEvent uniqueEvent2 : uniqueEvents) {
                                    if (!alreadySeenRaces.contains(cp = new CommutativePair(uniqueEvent, uniqueEvent2))) {
                                        alreadySeenRaces.add(cp);
					if (Utilities.shouldPrint()) {
					    printLocation();
					    System.out.println("Race between "
							       + javato.activetesting.analysis.Observer.getIidToLine(uniqueEvent2.getIid()) + " and "
							       + javato.activetesting.analysis.Observer.getIidToLine(uniqueEvent.getIid()) + " (" + uniqueEvent2 + " and " + uniqueEvent + ").");
					}
                                    }
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }

            }
        }
    }

    public void addEvent(Integer iid, String t, Long m, boolean isRead, VectorClock vc, LockSet ls, UniqueEvent e) {
        Map<Long, Map<String, LinkedList<VCLockPair>>> currentMap = isRead ? readMap : writeMap;
        Map<String, LinkedList<VCLockPair>> threadLists = currentMap.get(m);
        if (threadLists == null) {
            threadLists = new TreeMap<String, LinkedList<VCLockPair>>();
            currentMap.put(m, threadLists);
        }
	addEventAux(iid, threadLists, t, vc, ls, e);
    }

    protected void addEventAux(Integer iid, Map<String, LinkedList<VCLockPair>> threadLists, String t, VectorClock vc, LockSet ls, UniqueEvent e) {
        LinkedList<VCLockPair> vcLists = threadLists.get(t);
        if (vcLists == null) {
            vcLists = new LinkedList<VCLockPair>();
            threadLists.put(t, vcLists);
        }
        long c = vc.getValue(t);
        VCLockPair cPair;
        if (vcLists.isEmpty() || vcLists.getFirst().getClock() < c) {
            cPair = new VCLockPair(c);
            vcLists.addFirst(cPair);
            if (vcLists.size() > Parameters.N_VECTOR_CLOCKS_WINDOW) {
                vcLists.removeLast();
            }
        } else {
            cPair = vcLists.getFirst();
        }
        HashMap<LockSet, TreeSet<UniqueEvent>> lockSets = cPair.getLockSets();
        TreeSet<UniqueEvent> uniqueEvents = lockSets.get(ls);
        if (uniqueEvents == null) {
            uniqueEvents = new TreeSet<UniqueEvent>();
            lockSets.put(new LockSet(ls), uniqueEvents);
        }
        uniqueEvents.add(e);
    }


    public void printLocation() {
        Throwable t = new Throwable();
        StackTraceElement[] elems = t.getStackTrace();
        for (int i = 0; i < elems.length; i++) {
            if (!elems[i].getClassName().startsWith("javato.") && (elems[i].getLineNumber() != -1)) {
                System.out.print(elems[i].getClassName());
                System.out.print(":");
                System.out.print(elems[i].getFileName());
                System.out.print(":");
                System.out.println(elems[i].getLineNumber());
                return;
            }
        }
    }

    public LinkedHashSet<CommutativePair> getRaces() {
	return alreadySeenRaces;
    }

    /* Lock versions */

    public void checkRace(Integer iid, String t, Integer l, boolean isLock, VectorClock vc, LockSet ls, UniqueEvent e) {
	Long m = l.longValue();
	Map<String, LinkedList<VCLockPair>> threadLists = lockMap.get(m);
	if (threadLists != null)
	    checkRaceAux(iid, threadLists, t, vc, ls, m, e);
    }

    public void addEvent(Integer iid, String t, Integer l, boolean isLock, VectorClock vc, LockSet ls, UniqueEvent e) {
	Long m = l.longValue();
	Map<String, LinkedList<VCLockPair>> threadLists = lockMap.get(m);
        if (threadLists == null) {
            threadLists = new TreeMap<String, LinkedList<VCLockPair>>();
            lockMap.put(m, threadLists);
        }
	addEventAux(iid, threadLists, t, vc, ls, e);
    }
}