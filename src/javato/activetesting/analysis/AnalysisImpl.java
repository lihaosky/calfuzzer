package javato.activetesting.analysis;

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
abstract public class AnalysisImpl extends Thread implements Analysis {
    public AnalysisImpl() {
        initialize();
        Runtime.getRuntime().addShutdownHook(this);
    }

    public void run() {
        finish();
    }

    /* Default implementations. */
    public void initialize() { }
    public void lockBefore(Integer iid, String thread, Integer lock, Object actualLock) { }
    public void unlockAfter(Integer iid, String thread, Integer lock) { }
    public void newExprAfter(Integer iid, Integer object, Integer objOnWhichMethodIsInvoked) { }
    public void methodEnterBefore(Integer iid, String thread) { }
    public void methodExitAfter(Integer iid, String thread) { }
    public void startBefore(Integer iid, String parent, String child) { }
    public void startAfter(Integer iid, String parent, Object child) { }
    public void waitBefore(Integer iid, String thread, Integer lock) { }
    public void waitAfter(Integer iid, String thread, Integer lock) { }
    public void notifyBefore(Integer iid, String thread, Integer lock) { }
    public void notifyAllBefore(Integer iid, String thread, Integer lock) { }
    public void joinAfter(Integer iid, String parent, String child) { }
    public void readBefore(Integer iid, String thread, Long memory, boolean isVolatile) { }
    public void writeBefore(Integer iid, String thread, Long memory, boolean isVolatile) { }
    public void writeAfter(Integer iid, Thread thread, String local, Object value, String type) { }
    public void openDeterministicBlock(Integer bid) { }
    public void closeDeterministicBlock(Integer bid) { }
    public void requireDeterministic(String thread, Object invariant) { }
    public void assertDeterministic(String thread, Object invariant) { }
    public void finish() { }
}
