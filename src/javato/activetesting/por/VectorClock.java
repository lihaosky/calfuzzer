package javato.activetesting.por;


import java.util.Map;
import java.util.TreeMap;

public class VectorClock implements java.io.Serializable {
    public Map<String, LongCounter> vc;

    public VectorClock(VectorClock vc) {
        //PALLAVI : we need a deep copy of the map here; clone all LongCounter objects too
        //and not just duplicate the mappings
        //otherwise the vectorClocks added in the database would change as the vectorClocks
        //for the corresponding threads would change

        this.vc = new TreeMap<String, LongCounter>(vc.vc);
        for (String tid : vc.vc.keySet()) {
            LongCounter ctr = vc.vc.get(tid);
            this.vc.put(tid, new LongCounter(ctr.val));
        }
    }

    public VectorClock() {
        vc = new TreeMap<String, LongCounter>();
    }

    public long getValue(String thread) {
        LongCounter l = vc.get(thread);
        if (l == null) return 0;
        return l.val;
    }

    public void inc(String parent) {
        LongCounter l = vc.get(parent);
        if (l == null) {
            l = new LongCounter(0);
            vc.put(parent, l);
        }
        l.inc();
    }

    public void set(String parent, long val) {
        LongCounter l = vc.get(parent);
        if (l == null) {
            l = new LongCounter(val);
            vc.put(parent, l);
        } else {
            l.val = val;
        }
    }

    public void updateMax(VectorClock vc2) {
        for (String t : vc2.vc.keySet()) {
            long l = vc2.getValue(t);
            if (l > getValue(t)) {
                set(t, l);
            }
        }
    }


    public static boolean areVecClocksEqual(VectorClock vc1, VectorClock vc2) {
        if ((vc1 == null) && (vc2 == null)) {
            return true;
        }
        if ((vc1 == null) || (vc2 == null)) {
            return false;
        }
        if (vc1.vc.size() != vc2.vc.size()) {
            return false;
        }
        for (String tid : vc1.vc.keySet()) {
            LongCounter lc1 = vc1.vc.get(tid);
            LongCounter lc2 = vc2.vc.get(tid);
            if ((lc1 != null) && (lc2 != null)) {
                if (lc1.val != lc2.val) {
                    return false;
                }
            }
            if ((lc1 != null) && (lc2 == null)) {
                return false;
            }
            if ((lc1 == null) && (lc2 != null)) {
                return false;
            }

        }
        return true;
    }

    public static boolean isVC1LessThanOrEqualToVC2(VectorClock vc1, VectorClock vc2) {

        for (String tid : vc1.vc.keySet()) {
            long vc1val = vc1.getValue(tid);
            long vc2val = vc2.getValue(tid);
            if (vc1val > vc2val) {
                return false;
            }
        }
        return true;
    }

    public static boolean isVC1LessThanVC2(VectorClock vc1, VectorClock vc2) {

        for (String tid : vc1.vc.keySet()) {
            long vc1val = vc1.getValue(tid);
            long vc2val = vc2.getValue(tid);
            if (vc1val >= vc2val) {
                return false;
            }
        }
        return true;
    }

    /**
     * this function compares two vector clocks
     *
     * @param vc1
     * @param vc2
     * @return -1 when vc1 <= vc2, 0 when vc1 not comparable to vc2, 1 when vc1 > vc2
     * @author Christos Stergiou
     */
    public int compareVectorClocks(VectorClock vc1, VectorClock vc2) {
        boolean less = false;
        boolean notcomp = false;
        boolean greater = false;

        for (String tid : vc1.vc.keySet()) {
            /* if vc2 does not contain corresponding thread id, getValue returns 0 */
            long vc1val = vc1.getValue(tid);
            long vc2val = vc2.getValue(tid);

            if (vc1val > vc2val) {
                greater = true;
                if (less)
                    notcomp = true;
            } else {
                less = true;
                if (greater)
                    notcomp = true;
            }
            if (notcomp)
                break;
        }
        if (notcomp)
            return 0;
        else if (less)
            return -1;

        /* check if vc1 is indeed greater than vc2 */
        for (String tid : vc2.vc.keySet()) {
            long vc1val = vc1.getValue(tid);
            long vc2val = vc2.getValue(tid);

            if (vc1val < vc2val)
                return 0;
        }
        return 1;
    }

    public void print() {
        for (String tid : vc.keySet()) {
            LongCounter lc = vc.get(tid);
            System.out.println(tid + " " + lc.val);
        }
    }
    
}

