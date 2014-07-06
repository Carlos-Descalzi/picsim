package sim.mcu.p16f84a;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import sim.HexUtil;
import sim.mcu.AbstractDataMemory;
import sim.mcu.AbstractMicrocontroller;
import sim.mcu.AbstractProgramMemory;
import sim.mcu.DataMemory;
import sim.mcu.Port;
import sim.mcu.Program;
import sim.mcu.Symbol;
import sim.mcu.Port.Type;
import sim.mcu.PortEvent;
import sim.mcu.PortImpl;
import sim.mcu.PortListener;
import sim.mcu.ProgramMemory;

public class Pic16f84a extends AbstractMicrocontroller {

	public static final int BIT_C = 0;
	public static final int MASK_C = 1 << BIT_C;
	public static final int BIT_DC = 1;
	public static final int MASK_DC = 1 << BIT_DC;
	public static final int BIT_Z = 2;
	public static final int MASK_Z = 1 << BIT_Z;
	
	public static final int BIT_RP0 = 5;
	public static final int MASK_RP0 = 1 << BIT_RP0;
	public static final int BIT_PSA = 3;
	public static final int MASK_PSA = 1 << BIT_PSA;
	public static final int BIT_PS2 = 2;
	public static final int MASK_PS2 = 1 << BIT_PS2;
	public static final int BIT_PS1 = 1;
	public static final int MASK_PS1 = 1 << BIT_PS1;
	public static final int BIT_PS0 = 0;
	public static final int MASK_PS0 = 1 << BIT_PS0;
	private static final int INDR = 0x00;
	private static final int TMR0 = 0x01;
	private static final int PCL = 0x02;
	public static final int STATUS = 0x03;
	private static final int FSR = 0x04;
	private static final int PORTA = 0x05;
	private static final int PORTB = 0x06;
	private static final int PCLATH = 0x0a;
	private static final int INTCON = 0x0b;
	private static final int TRISA = 0x85;
	private static final int TRISB = 0x86;
	private static final int OPTION_REG = 0x81;
	
	private final int[] stack = new int[8];
	private int stackPointer = 0;
	private boolean returned = false;

	final Set<Integer> fixedAddresses = new HashSet<Integer>(
		Arrays.asList(INDR,TMR0,PCL,STATUS,FSR,PCLATH,INTCON,OPTION_REG));

	private PortImpl[] ports = new PortImpl[]{
		new PortImpl("RB0",0,Port.Type.OUTPUT),	
		new PortImpl("RB1",1,Port.Type.OUTPUT),
		new PortImpl("RB2",2,Port.Type.OUTPUT),
		new PortImpl("RB3",3,Port.Type.OUTPUT),
		new PortImpl("RB4",4,Port.Type.OUTPUT),
		new PortImpl("RB5",5,Port.Type.OUTPUT),
		new PortImpl("RB6",6,Port.Type.OUTPUT),
		new PortImpl("RB7",7,Port.Type.OUTPUT),
		new PortImpl("RA0",0,Port.Type.OUTPUT),	
		new PortImpl("RA1",1,Port.Type.OUTPUT),
		new PortImpl("RA2",2,Port.Type.OUTPUT),
		new PortImpl("RA3",3,Port.Type.OUTPUT),
		new PortImpl("RA4",4,Port.Type.OUTPUT)
	};
	
	private Set<Symbol> symbols = new HashSet<Symbol>();
	
	private Set<Symbol> registerNames = new HashSet<Symbol>(Arrays.asList(
		new Symbol(0x00,"INDR"),
		new Symbol(0x01,"TMR0"),
		new Symbol(0x02,"PCL"),
		new Symbol(0x03,"STATUS"),
		new Symbol(0x04,"FSR"),
		new Symbol(0x05,"PORTA"),
		new Symbol(0x06,"PORTB"),
		new Symbol(0x08,"EEDATA"),
		new Symbol(0x09,"EEADDR"),
		new Symbol(0x0A,"PCLATH"),
		new Symbol(0x0A,"INTCON"),
		new Symbol(0x80,"INDR"),
		new Symbol(0x81,"OPTREG"),
		new Symbol(0x82,"PCL"),
		new Symbol(0x83,"STATUS"),
		new Symbol(0x84,"FSR"),
		new Symbol(0x85,"TRISA"),
		new Symbol(0x86,"TRISB")
	));
	
	private PortListener portInputListener = new PortListener() {
		
		@Override
		public void portStateChanged(PortEvent event) {
			PortImpl port = (PortImpl)event.getSource();
			boolean portA = port.getName().startsWith("RA");
			int portAddress = portA ? PORTA : PORTB;
			int portValue = dataMemory.getAbsoluteAddressValue(portAddress);
			if (port.getState()){
				portValue |= 1 << port.getPin();
			} else {
				portValue &= (~(1 << port.getPin())) & 0xFF;
			}
			dataMemory.setAbsoluteAddressValue(portAddress,(byte)portValue);
			
		}
		
		@Override
		public void portDirectionChanged(PortEvent event) {}
	};

	public Pic16f84a(){
		for (PortImpl port:ports){
			port.addInputListener(portInputListener);
		}
	}
	
	public Port[] getPorts(){
		return ports;
	}
	
	@Override
	public String[] getStatusFlags() {
		return new String[]{
				"X","X","RP0","TO","PD","Z","DC","C"
		};
	}

	@Override
	public Set<Symbol> getRegisterNames() {
		return registerNames;
	}

	@Override
	public Set<Symbol> getSymbols() {
		return symbols;
	}

	private final AbstractDataMemory dataMemory = new AbstractDataMemory(){
		private final byte[] rawMemory = new byte[0x100];

		{
			rawMemory[PCL]=0;
		}
		@Override
		public int getLength() {
			return rawMemory.length;
		}

		@Override
		public byte getAbsoluteAddressValue(int address) {
			if (address == INDR){
				int indirectAddress = rawMemory[FSR] & 0xFF;
				return rawMemory[indirectAddress];
			}

			return rawMemory[address];
		}

		@Override
		public void setAbsoluteAddressValue(int address, byte value) {
			byte oldValue;
			if (address == INDR){
				int indirectAddress = rawMemory[FSR] & 0xFF;
				oldValue = rawMemory[indirectAddress];
				rawMemory[indirectAddress] = value;
			} else {
				oldValue = rawMemory[address];
				rawMemory[address] = value;
			}
			fireAddressValueChanged(address,oldValue,value);
		}

		@Override
		public byte getValue(int address) {
			
			if (address == INDR){
				int indirectAddress = rawMemory[FSR] & 0xFF;
				return rawMemory[indirectAddress];
			}

			if (address < 0x80 && !fixedAddresses.contains(address) && isBank1()){
				address |= 0x80;
			}
			
			
			return rawMemory[address];
		}

		@Override
		public void setValue(int address, byte value) {
			

			
			if (address == INDR){
				
				int indirectAddress = rawMemory[FSR] & 0xFF;
				
				byte oldValue = rawMemory[indirectAddress];
				rawMemory[indirectAddress] = value;
				fireAddressValueChanged(indirectAddress,oldValue,value);
			} else {
				if (address < 0x80 && !fixedAddresses.contains(address) && isBank1()){
					address |= 0x80;
				}

				byte oldValue = rawMemory[address];
				rawMemory[address] = value;
				fireAddressValueChanged(address,oldValue,value);
				
				if (address == PORTA){
					int trisA = rawMemory[TRISA];
					for (int i=0;i<5;i++){
						if ((trisA & (1 << i)) == 0){
							ports[8+i].setOutput((value & (1 << i)) != 0);
						}
					}
					
				} else if (address == PORTB){
					int trisB = rawMemory[TRISB];
					for (int i=0;i<8;i++){
						if ((trisB & (1 << i)) == 0){
							ports[i].setOutput((value & (1 << i)) != 0);
						}
					}
				} else if (address == TRISA){
					updatePortBDirections();
				} else if (address == TRISB){
					updatePortADirections();
				}
			}
		}

		private void updatePortADirections() {
			byte portDirection = rawMemory[TRISB];
			for (PortImpl port:ports){
				if (port.getName().startsWith("RB")){
					if ((portDirection & (1 << port.getPin())) != 0){
						port.setType(Type.INPUT);
					} else {
						port.setType(Type.OUTPUT);
					}
					
				}
			}
		}

		private void updatePortBDirections() {
			byte portDirection = rawMemory[TRISA];
			for (PortImpl port:ports){
				if (port.getName().startsWith("RA")){
					
					if ((portDirection & (1 << port.getPin())) != 0){
						port.setType(Type.INPUT);
					} else {
						port.setType(Type.OUTPUT);
					}
					
					
				}
			}
		}

		private boolean isBank1() {
			return (rawMemory[STATUS] & (1 << BIT_RP0))!=0;
		}

		@Override
		public int getPc() {
			return (int)(((rawMemory[PCLATH] << 8) &0xFFFF) | (rawMemory[PCL] &0x00FF));
		}

		@Override
		public void setPc(int pc) {
			byte pcl = rawMemory[PCL];
			byte pclath = rawMemory[PCLATH];
			rawMemory[PCL] = (byte)(pc &0xFF);
			rawMemory[PCLATH] = (byte)((pc &0xF00) >> 8);
			fireAddressValueChanged(PCL,pcl,rawMemory[PCL]);
			fireAddressValueChanged(PCL,pclath,rawMemory[PCLATH]);
		}

		@Override
		public void incPc() {
			if (rawMemory[PCL] == 0xff){
				byte pcl = rawMemory[PCL];
				byte pclath = rawMemory[PCLATH];
				rawMemory[PCL] = 0;
				rawMemory[PCLATH]++;
				fireAddressValueChanged(PCL,pcl,rawMemory[PCL]);
				fireAddressValueChanged(PCL,pclath,rawMemory[PCLATH]);
			} else {
				byte pcl = rawMemory[PCL];
				rawMemory[PCL]++;
				fireAddressValueChanged(PCL,pcl,rawMemory[PCL]);
			}
		}

	};
	
	private final AbstractProgramMemory programMemory = new AbstractProgramMemory(){
		
		private int[] rawMemory = new int[0x400];
		private String[] code = new String[0x400];
		@Override
		public int getLenght() {
			return rawMemory.length;
		}

		@Override
		public int getWord(int address) {
			return rawMemory[address];
		}
		
		public String getCode(int address){
			return code[address];
		}

		@Override
		public void setProgram(Program program) {
			int[] raw = program.getRawProgram();
			for (int i = 0;i<rawMemory.length;rawMemory[i++]=0);
			System.arraycopy(raw, 0, rawMemory, 0, raw.length);
			fireMemoryChanged();
		}
		
	};

	public void reset(){
		clock = 0;
		dataMemory.setPc(0);
		stackPointer = 0;
		fireRegisterChanged();
	}
	
	protected boolean isReturnFromCall(){
		return returned;
	}


	@Override
	protected void runStep() {
		int instruction = programMemory.getWord(getPc());
		
		Result result = execute(instruction);
		
		if (!result.branch){
			dataMemory.incPc();
		}
		
		clock += result.ticks;
		
		int division = getTimerDivision();
		
		dataMemory.setValue(TMR0, (byte)((clock >> division) & 0xFF));
		
		fireRegisterChanged();
	}
	

	protected boolean isNextLineBranch(){
		int instruction = programMemory.getWord(getPc());
		
		int opcodeH = (instruction & 0x3000) >> 12;
		int opcodeL = (instruction & 0x0F00) >> 8;
					
		return opcodeH == 0x02 && (opcodeL & 0x08)==0;
	}


	protected void doSetEventsEnabled(boolean enabled){
		dataMemory.setEventsEnabled(enabled);
		programMemory.setEventsEnabled(enabled);
	}

	protected void notifyMcuStateChanged(){
		dataMemory.fireMemoryCleared();
		fireRegisterChanged();
	}
	
	
	private int getTimerDivision() {
		int optReg = dataMemory.getValue(OPTION_REG);
		
		if ((optReg & (1 << BIT_PSA)) != 0){
			return 1;
		}
		
		int division = optReg & ((1 << BIT_PS2)|(1<< BIT_PS1)|(1 << BIT_PS0));
		
		return division+1;
	}

	private class Result {
		public boolean branch;
		public int ticks = 1;
	}

	private Program program;
	@Override
	public void setProgram(Program program) {
		this.program = program;
		programMemory.setProgram(program);
		
		if (program.isSymbolsAvailable()){
			symbols.addAll(program.getSymbols());
		}
		
		fireProgramLoaded(program);
	}

	public Program getProgram() {
		return program;
	}

	@Override
	public ProgramMemory getProgramMemory() {
		return programMemory;
	}

	@Override
	public DataMemory getDataMemory() {
		return dataMemory;
	}

	@Override
	public int getPc() {
		return dataMemory.getPc();
	}

	@Override
	public short getStatus() {
		return dataMemory.getValue(STATUS);
	}

	@Override
	public int[] getStack() {
		return stack;
	}
	
	private void push(int value){
		
		if (stackPointer == stack.length){
			fireStackOverflowDetected();
			stop();
		} else {
			stack[stackPointer++] = value;
		}
	}

	private int pop(){
		int value = stack[--stackPointer];
		stack[stackPointer] = 0;
		returned = true;
		return value;
	}
	
	protected void setStatusBit(int bit,boolean set){
		byte status = dataMemory.getValue(STATUS);
		if (set){
			status |= 1 << bit;
		} else {
			status &= ~(1 << bit);
		}
		dataMemory.setValue(STATUS,status);
	}
	
	public String getMnemonic(int instruction){
		
		int opcodeH = (instruction & 0x3000) >> 12;
			int opcodeL = (instruction & 0x0F00) >> 8;
			int operand = (instruction & 0x00FF);
			
			if (opcodeH == 0x00){
				if (opcodeL == 0x00){
					// control operations
					if (operand == 0x08){
						return "RETURN";
					} else if (operand == 0x09){
						return "RETFIE";
					} else if (operand == 0x63){
						return "SLEEP";
					} else if (operand == 0x64){
						return "CLRWDT";
					} else if ((operand & 0x9f) == 0){
						return "NOP";
						// nop;
					} else {
						return "MOVWF "+HexUtil.toHex(operand & 0x7f,2);
					}
				} else {
					switch(opcodeL){
					case 0x00:
					case 0x07: 
						return "ADDWF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x05: 
						return "ANDWF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x01: 
						return ((operand & 0x80) != 0 ? "CLRF "+HexUtil.toHex(operand & 0x7f,2) : "CLRW");
					case 0x09: 
						return "COMF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x03: 
						return "DECF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x0B:
						return "DECFSZ "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x0A:
						return "INCF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x0F:
						return "INCFSZ "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x08:
						return "MOVF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x0D:
						return "RLF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x0C:
						return "RRF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x02:
						return "SUBWF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x0E:
						return "SWAPF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x06:
						return "XORWF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					case 0x04:
						return "IORWF "+HexUtil.toHex(operand & 0x7f,2)+","+((operand & 0x80) != 0 ? "1" : "0");
					}
				}
			} else if (opcodeH == 0x01){
				int bit = ((operand &0x80) >> 7) | ((opcodeL & 0x03) << 1);
				int operation = opcodeL >> 2;
				int address = (operand & 0x7f);
				switch (operation){
				case 0: // BCF f,b
					return "BCF "+HexUtil.toHex(address,2)+","+bit;
				case 1: // BSF f,b
					return "BSF "+HexUtil.toHex(address,2)+","+bit;
				case 2: // BTFSC f,b
					return "BTFSC "+HexUtil.toHex(address,2)+","+bit;
				case 3: // BTFSS f,b
					return "BTFSS "+HexUtil.toHex(address,2)+","+bit;
				}
			} else if (opcodeH == 0x02){
				int pc = (((opcodeL & 0x7) << 8) | operand);
				
				if ((opcodeL & 0x08)==0x08){
					return "GOTO "+HexUtil.toHex(pc,4);
				} else {
					return "CALL "+HexUtil.toHex(pc,4);
				}
			} else {
				if ((opcodeL & 0x0E) == 0x0E){
					return "ADDLW "+HexUtil.toHex(operand,2);
				} else if (opcodeL == 0x09){
					return "ANDLW "+HexUtil.toHex(operand,2);
				} else if (opcodeL == 0x08){
					return "IORLW "+HexUtil.toHex(operand,2);
				} else if ((opcodeL & 0x0C) == 0){
					return "MOVLW "+HexUtil.toHex(operand,2);
				} else if ((opcodeL & 0x0E) == 0x0C){
					return "SUBLW "+HexUtil.toHex(operand,2);
				} else if (opcodeL == 0x0A){
					return "XORLW "+HexUtil.toHex(operand,2);
				}
			}
		System.out.println(Integer.toHexString(operand));
		return "XXX";
	}
	private Result execute(int instruction){
		returned = false;
		Result result = new Result();
		int opcodeH = (instruction & 0x3000) >> 12;
		int opcodeL = (instruction & 0x0F00) >> 8;
		int operand = (instruction & 0x00FF);
		
		if (opcodeH == 0x00){
			if (opcodeL == 0x00){
				doControlOperation(operand,result);
			} else {
				doByteOrientedFileOperation(opcodeL, operand,result);
			}
		} else if (opcodeH == 0x01){
			doBitOrientedOperation(opcodeL, operand,result);
		} else if (opcodeH == 0x02){
			doBranchOperation(opcodeL, operand,result);
		} else {
			doLiteralOrientedOperation(opcodeL, operand,result);
		}
		return result;
	}

	private void doControlOperation(int operand, Result result) {
		// control operations
		if (operand == 0x08){
			opReturn(result);
		} else if (operand == 0x09){
			retfie(operand,result);
		} else if (operand == 0x63){
			sleep(operand,result);
		} else if (operand == 0x64){
			clrwdt(operand,result);
		} else if ((operand & 0x9f) == 0x0){
				// nop;
		} else if ((operand & 0x80) == 0x80){
			movwf(operand);
		}
	}

	private void opReturn(Result result) {
		dataMemory.setPc(pop());
		result.ticks = 2;
	}

	private void retfie(int operand,Result result) {
		result.ticks = 2;
		// RETFIE
	}

	private void clrwdt(int operand,Result result) {
	}

	private void sleep(int operand,Result result) {
	}

	private void doLiteralOrientedOperation(int opcodeL, int operand, Result result) {
		if ((opcodeL & 0x0E) == 0x0E){
			addlw(operand);
		} else if (opcodeL == 0x09){
			andlw(operand);
		} else if (opcodeL == 0x08){
			iorlw(operand);
		} else if ((opcodeL & 0x0C) == 0){
			movlw(operand);
		} else if ((opcodeL & 0x0E) == 0x0C){
			sublw(operand);
		} else if (opcodeL == 0x0A){
			xorlw(operand);
		}
	}

	private void doByteOrientedFileOperation(int opcodeL, int operand, Result result) {
		switch(opcodeL){
		
		case 0x07: 
			addwf(operand);
			break;
		case 0x05: 
			andwf(operand);
			break;
		case 0x01: 
			clr(operand);
			break;
		case 0x09: 
			comf(operand);
			break;
		case 0x03: 
			decf(operand);
			break;
		case 0x0B:
			decfsz(operand,result);
			break;
		case 0x0A:
			incf(operand);
			break;
		case 0x0F:
			incfsz(operand,result);
			break;
		case 0x08:
			movf(operand);
			break;
		case 0x0D:
			rlf(operand);
			break;
		case 0x0C:
			rrf(operand);
			break;
		case 0x02:
			subwf(operand);
			break;
		case 0x0E:
			swapf(operand);
			break;
		case 0x06:
			xorwf(operand);
			break;
		case 0x04:
			iorwf(operand);
			break;
		}
	}

	private void addlw(int operand) {
		
		int result = w + operand;
		
		w = (byte)(result & 0xFF);
		
		setStatusBit(BIT_C, (result & 0xF00) != 0);
		setStatusBit(BIT_Z, w == 0);
	}

	private void andlw(int operand) {
		int result = w & operand;
		
		w = (byte)(result & 0xFF);
		
		setStatusBit(BIT_Z, w == 0);
	}

	private void iorlw(int operand) {
		int result = w | operand;
		
		w = (byte)(result & 0xFF);
		
		setStatusBit(BIT_Z, w == 0);
	}

	private void movlw(int operand) {
		w = (byte)(operand & 0xFF);
	}

	private void xorlw(int operand) {
		int result = w ^ operand;
		
		w = (byte)(result & 0xFF);
		
		setStatusBit(BIT_Z, w == 0);
	}

	private void xorwf(int operand) {
		
		byte data = dataMemory.getValue(operand & 0x7F);
		
		data = (byte)(data ^ w);
		
		if ((operand & 0x80) == 0x80){
			dataMemory.setValue(operand & 0x7F,data);
		} else {
			w = data;
		}
	}
	private void iorwf(int operand) {
		
		byte data = dataMemory.getValue(operand & 0x7F);
		
		data = (byte)(data | w);
		
		if ((operand & 0x80) == 0x80){
			dataMemory.setValue(operand & 0x7F,data);
		} else {
			w = data;
		}
	}

	private void swapf(int operand) {
		
		byte data = dataMemory.getValue(operand & 0x7F);
		
		int high = data & 0xF0;
		int low = data & 0x0F;
		
		data = (byte)((low << 4) + (high >> 4));
		
		if ((operand & 0x80) == 0x80){
			dataMemory.setValue(operand & 0x7F,data);
		} else {
			w = data;
		}
	}

	public void subwf(int operand) {
		int result = dataMemory.getValue(operand & 0x7F) - w;
		
		setStatusBit(BIT_C, (result & 0xF0000000) == 0);
		setStatusBit(BIT_Z, (result & 0xFF) == 0);
		
		if ((operand &0x80) == 0x80){
			dataMemory.setValue(operand & 0x7F, (byte)(result & 0xFF));
		} else {
			w = (byte)(result & 0xFF);
		}
	}

	public void sublw(int operand) {
		int result = operand - w;
		
		setStatusBit(BIT_C, (result & 0xF0000000) == 0);
		setStatusBit(BIT_Z, (result & 0xFF) == 0);
		
		w = (byte)(result & 0xFF);
	}

	private void rrf(int operand) {
		int result = dataMemory.getValue(operand & 0x7F) >> 1;
		
		setStatusBit(BIT_C, (result & 0xF0000000) != 0);
		setStatusBit(BIT_Z, (result & 0xFF) == 0);
		
		if ((operand &0x80) == 0x80){
			dataMemory.setValue(operand & 0x7F, (byte)(result & 0xFF));
		} else {
			w = (byte)(result & 0xFF);
		}
	}

	private void rlf(int operand) {
		
		int result = dataMemory.getValue(operand & 0x7F) << 1;
		
		setStatusBit(BIT_C, (result & 0xF00) != 0);
		setStatusBit(BIT_Z, (result & 0xFF) == 0);
		
		if ((operand &0x80) == 0x80){
			dataMemory.setValue(operand & 0x7F, (byte)(result & 0xFF));
		} else {
			w = (byte)(result & 0xFF);
		}
	}

	private void movf(int operand) {
		byte data = dataMemory.getValue(operand & 0x7f);
		
		setStatusBit(BIT_Z, data == 0);
		
		if ((operand & 0x80) == 0){
			w = data;
		}
	}

	private void movwf(int operand) {
		dataMemory.setValue(operand & 0x7f,w);
	}

	private void addwf(int operand) {
		int result = w + dataMemory.getValue(operand & 0x7f);
		
		if (result == 0){
			setStatusBit(BIT_Z,result == 0);
		} 
		if ((result & 0xf00)!=0){
			setStatusBit(BIT_C,result == 1);
		}
		
		if ((operand & 0x80) != 0){
			dataMemory.setValue(operand & 0x7f, (byte)(result & 0xff));
		} else {
			w = (byte)(result & 0xff);
		}
	}
	
	private void andwf(int operand) {
		int result = w & dataMemory.getValue(operand & 0x7f);
		
		if (result == 0){
			setStatusBit(BIT_Z,result == 0);
		} 
		
		if ((operand & 0x80) != 0){
			dataMemory.setValue(operand & 0x7f, (byte)(result & 0xff));
		} else {
			w = (byte)(result & 0xff);
		}
	}
	
	private void clr(int operand){
		if ((operand & 0x80)!=0){
			dataMemory.setValue(operand & 0x7f,(byte)0x00);
		} else {
			w = 0;
		}
	}

	private void comf(int operand){
		byte data = dataMemory.getValue(operand & 0x7f);
		data = (byte)~data;
		
		setStatusBit(BIT_Z, data == 0);

		if ((operand & 0x80) != 0){
			dataMemory.setValue(operand & 0x7f, data);
		} else {
			w = data;
		}
	}

	private void decf(int operand){
		byte data = dataMemory.getValue(operand & 0x7f);
		data--;
		
		setStatusBit(BIT_Z,data == 0);
		
		if ((operand & 0x80) != 0){
			dataMemory.setValue(operand & 0x7f, data);
		} else {
			w = data;
		}
		
		
	}
	
	private void decfsz(int operand, Result result){
		byte data = dataMemory.getValue(operand & 0x7f);
		data--;
		
		if ((operand & 0x80) != 0){
			dataMemory.setValue(operand & 0x7f, data);
		} else {
			w = data;
		}
		
		if (data == 0){
			dataMemory.incPc();
			result.ticks=2;
		}
	}
	
	private void incf(int operand){
		byte data = dataMemory.getValue(operand & 0x7f);
		data++;
		data = (byte)(data & 0xFF);
		
		setStatusBit(BIT_Z,data == 0);
		
		if ((operand & 0x80) != 0){
			dataMemory.setValue(operand & 0x7f, data);
		} else {
			w = data;
		}
	}
	
	private void incfsz(int operand, Result result){
		byte data = dataMemory.getValue(operand & 0x7f);
		data++;
		data = (byte)(data & 0xFF);
		
		if ((operand & 0x80) != 0){
			dataMemory.setValue(operand & 0x7f, data);
		} else {
			w = data;
		}
		
		if (data == 0){
			dataMemory.incPc();
			result.ticks=2;
		}
	}
	
	private void doBranchOperation(int opcodeL, int operand, Result result) {
		int pc = (int)(((opcodeL & 0x7) << 8) | operand);
		
		if ((opcodeL & 0x08)==0x08){
			// GOTO
			dataMemory.setPc(pc);
		} else {
			// CALL
			int currentPc = dataMemory.getPc();
			push(currentPc);
			dataMemory.setPc(pc);
		}
		result.ticks=2;
		result.branch = true;
	}

	private void doBitOrientedOperation(int opcodeL, int operand, Result result) {
		// bit oriented file operations
		int bit = ((operand &0x80) >> 7) | ((opcodeL & 0x03) << 1);
		int address = operand & 0x7f;
		int operation = opcodeL >> 2;
		
		byte data = dataMemory.getValue(address);
		switch (operation){
		case 0: // BCF f,b
			data &= ((byte)(~(1 << bit)));
			dataMemory.setValue(address,data);
			break;
		case 1: // BSF f,b
			data |= ((byte)(1 << bit));
			dataMemory.setValue(address,data);
			break;
		case 2: // BTFSC f,b
			if ((data & (1 << bit))==0){
				dataMemory.incPc();
				result.ticks=2;
			}
			break;
		case 3: // BTFSS f,b
			if ((data & (1 << bit))!=0){
				dataMemory.incPc();
				result.ticks=2;
			}
			break;
		}
	}
	
	private void setDc(byte oldValue,byte newValue, boolean add){
	}
}
