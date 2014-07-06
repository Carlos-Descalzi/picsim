package sim.mcu;

public abstract class Instruction {

	protected int word;
	private String mnemonic;
	private int clockTicks;
	private boolean branchDone;
	
	public boolean isBranchDone() {
		return branchDone;
	}

	public void setBranchDone(boolean branchDone) {
		this.branchDone = branchDone;
	}

	public int getWord() {
		return word;
	}

	public void setWord(int word) {
		this.word = word;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	public int getClockTicks() {
		return clockTicks;
	}

	public void setClockTicks(int clockTicks) {
		this.clockTicks = clockTicks;
	}


	public abstract void execute();
}
