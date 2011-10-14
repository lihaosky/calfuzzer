package javato.activetesting.por;

import java.util.HashMap;
import java.util.Map;

/**
 * Class of vector clock tracker
 * @author lihao
 *
 */
public class VectorClockTracker {
    private Map<String, VectorClock> threads = new HashMap<String, VectorClock>();
    private Map<Integer, VectorClock> notifyMessages = new HashMap<Integer, VectorClock>();

    public void startBefore(String parent, String child) {
        VectorClock vc = getVectorClock(parent);
        VectorClock vc2 = new VectorClock(vc);
        vc2.set(parent, TransitionInfo.getTransitionNum(parent));
        //vc.inc(parent);
        threads.put(child, vc2);
        //vc2.inc(child);
    }

    public void joinAfter(String parent, String child) {
        VectorClock vc = getVectorClock(parent);
        VectorClock vc2 = getVectorClock(child);
        vc.updateMax(vc2);
        //vc.inc(parent);
    }

    public void notifyBefore(String thread, Integer lock) {
        VectorClock vc = getVectorClock(thread);
        notifyMessages.put(lock, new VectorClock(vc));	
        vc.set(thread, TransitionInfo.getTransitionNum(thread));
        //vc.inc(thread);
    }

    public void waitAfter(String thread, Integer lock) {
        VectorClock vc = getVectorClock(thread);
        VectorClock vc2 = notifyMessages.get(lock);
        vc.updateMax(vc2);
        //vc.inc(thread);
    }

    // make sure you make copy of this VC if you want to use in a Map
    // the returned VC changes during an execution
    public VectorClock getVectorClock(String thread) {
        VectorClock p = threads.get(thread);
        if (p == null) {
            p = new VectorClock();
            threads.put(thread, p);
        }
        return p;
    }
    
    public long getHappenBefore(String thread1, String thread2) {
    	VectorClock p = threads.get(thread1);
    	if (p == null) {
    		p = new VectorClock();
    		threads.put(thread1, p);
    	}
    	return threads.get(thread1).getValue(thread2);
    }

}

