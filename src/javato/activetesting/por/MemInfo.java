package javato.activetesting.por;

/**
 * Class holding memory info
 * @author lihao
 *
 */
public class MemInfo {
	/**
	 * If to read
	 */
	public boolean isRead;
	/**
	 * If to write
	 */
	public boolean isWrite;
	/**
	 * If is a lock
	 */
	public boolean isLock;
	/**
	 * Memory id
	 */
	public long memid;
	/**
	 * Line number
	 */
	public int iid;
	
	/**
	 * Constructor
	 * @param memid
	 * @param isRead
	 * @param isWrite
	 * @param isLock
	 * @param iid
	 */
	public MemInfo(long memid, boolean isRead, boolean isWrite, boolean isLock, int iid) {
		this.memid = memid;
		this.isRead = isRead;
		this.isWrite = isWrite;
		this.isLock = isLock;
		this.iid = iid;
	}
}
