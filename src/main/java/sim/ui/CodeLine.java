package sim.ui;

public class CodeLine {

	private int lineNumber;
	private Short address;
	private String code;
	
	public CodeLine(){
	}
	public CodeLine(int lineNumber, String code) {
		this.lineNumber = lineNumber;
		this.code = code;
	}

	public CodeLine(int lineNumber, Short address, String code) {
		this.lineNumber = lineNumber;
		this.address = address;
		this.code = code;
	}

	public Short getAddress() {
		return address;
	}

	public void setAddress(short address) {
		this.address = address;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	
}
