package javato.activetesting.por;

public class LongCounter implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long val;


    public LongCounter(long val) {
        this.val = val;
    }

    public LongCounter inc() {
        val++;
        return this;
    }
    
}

