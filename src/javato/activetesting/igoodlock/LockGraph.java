package javato.activetesting.igoodlock;


import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Copyright (c) 2007-2008,
 * Koushik Sen    <ksen@cs.berkeley.edu>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class LockGraph {
    private TreeMap<String, LockTree> graph;

    public LockGraph() {
        graph = new TreeMap<String, LockTree>();
    }

    public Pair<Boolean, LockNode> lock(int iid, String threadId, int lockId) {
        LockTree lt = graph.get(threadId);
        if (lt == null) {
            lt = new LockTree(threadId);
            graph.put(threadId, lt);
        }
        boolean isAdded = lt.lock(lockId, iid);
        LockNode ln = lt.getCurrent();
        return new Pair<Boolean, LockNode>(isAdded, ln);
    }

    public void unlock(int iid, String threadId, int lockId) {
        LockTree lt = graph.get(threadId);
        lt.unlock(lockId, iid);
    }

    public void printGraph(ArrayList<String> iidToLineMap) {
        for (String t : graph.keySet()) {
            System.out.println("---- Thread " + t);
            graph.get(t).printTree(iidToLineMap);
        }
    }

}
