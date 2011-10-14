package javato.activetesting.por;

public class LockInfo {
	public boolean isAcquired;
	public int lockid;
	
	public LockInfo(int lockid, boolean isAcquired) {
		this.lockid = lockid;
		this.isAcquired = isAcquired;
	}
}
