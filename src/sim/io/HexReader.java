package sim.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import sim.mcu.Program;
import sim.mcu.Symbol;
import sim.ui.CodeLine;

public class HexReader 
	implements ProgramReader{

	public Program read(File file) throws IOException, ReaderException {
		
		final String fileName = file.getName(); 
		
		File[] lstFiles = file.getParentFile().listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName.substring(0,fileName.indexOf('.')))
					&& name.toLowerCase().endsWith(".lst");
			}
			
		});
		
		ProgramImpl program = new ProgramImpl();
		
		readHex(file,program);
		
		if (lstFiles.length > 0){
			readLst(lstFiles[0],program);
		}
		
		return program;
	}
	
	private void readLst(File file,ProgramImpl program) throws IOException, ReaderException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line;
		
		Set<Symbol> symbols = new LinkedHashSet<Symbol>();
		List<CodeLine> code = new ArrayList<CodeLine>();
		int fileLine = 0;
		while ((line = reader.readLine()) != null){
			try {
				if (line.startsWith("MPASM") 
					|| line.startsWith("LOC  OBJECT CODE")
					|| line.startsWith("  VALUE")
					|| line.trim().length() == 0){
					continue;
				}
				if (line.startsWith("SYMBOL TABLE")){
					break;
				}
				LineTokenizer tokenizer = new LineTokenizer(line);
				
				String firstToken = tokenizer.nextToken();
				
				if (isDataSymbol(firstToken)){
					int address = Integer.decode("0x"+firstToken);
	
					String lineNumber = tokenizer.nextToken();
					
					int position = tokenizer.getPosition();
					String symbolName = tokenizer.nextToken();
					
					Symbol symbol = new Symbol(address, symbolName);
					
					symbols.add(symbol);
					
					code.add(new CodeLine(fileLine,tokenizer.getRemainder(position)));
				} else if (isLineNumber(firstToken)){
					code.add(new CodeLine(fileLine,tokenizer.getRemainder()));
				} else if (isCodeLine(firstToken)){
					
					String nextToken = tokenizer.nextToken();
					
					int position = tokenizer.getPosition();
					if (isCodeLine(nextToken)){
						int lineNumber = Integer.parseInt(tokenizer.nextToken());
						code.add(new CodeLine(fileLine,new Short(Short.decode("0x"+firstToken)),tokenizer.getRemainder()));
					} else {
						code.add(new CodeLine(fileLine,new Short(Short.decode("0x"+firstToken)),tokenizer.getRemainder(position)));
					}
				}
			} catch (Throwable ex){
				System.out.println("Line number:"+fileLine+":\""+line+"\"");
				ex.printStackTrace();
				break;
			}
			fileLine++;
		}
		
		program.setSymbols(symbols);
		program.setCode(code);
	}
	
	private boolean isCodeLine(String token) {
		if (token.length() != 4) return false;
		for (char c:token.toCharArray()){
			if (!(Character.isDigit(c) || (c >= 'A' && c <= 'F'))){
				return false;
			}
		}

		return true;
	}

	private boolean isLineNumber(String token) {
		
		if (token.length() != 5) return false;
		
		for (char c:token.toCharArray()){
			if (!(Character.isDigit(c))){
				return false;
			}
		}
		return true;
	}

	private boolean isDataSymbol(String token){
		if (token.length() != 8) return false;
		
		for (char c:token.toCharArray()){
			if (!(Character.isDigit(c) || (c >= 'A' && c <= 'F'))){
				return false;
			}
		}
		return true;
	}

	private void readHex(File file, ProgramImpl program) throws FileNotFoundException,
			IOException, ReaderException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line;
		
		ByteBuffer buffer = ByteBuffer.allocate(8192);
		
		while ((line = reader.readLine()).length() > 0){
			
			if (!line.startsWith(":")){
				continue;
			}
			
			line = line.substring(1);
			int sum = 0;
			int length = Integer.decode("0x"+line.substring(0,2));
			sum+=length;
			int address = Integer.decode("0x"+line.substring(2,6));
			sum+=Integer.decode("0x"+line.substring(2,4));
			sum+=Integer.decode("0x"+line.substring(4,6));
			int recordType = Integer.decode("0x"+line.substring(6,8));
			sum+=recordType;
			
			int currentPos = 8;
			if (recordType == 0){
				for (int i=0,pos=8;i<length;i++,pos+=2,currentPos+=2){
					int byteData = Integer.decode("0x"+line.substring(pos,pos+2));
					buffer.put((byte)byteData);
					sum+=byteData;
				}
			} else if (recordType == 2){
				byte[] data = new byte[length];
				for (int i=0,pos=8;i<length;i++,pos+=2,currentPos+=2){
					int byteData = Integer.decode("0x"+line.substring(pos,pos+2));
					data[i] = (byte)byteData;
					sum+=byteData;
				}
			} else if (recordType == 4){
				byte[] data = new byte[length];
				for (int i=0,pos=8;i<length;i++,pos+=2,currentPos+=2){
					int byteData = Integer.decode("0x"+line.substring(pos,pos+2));
					data[i] = (byte)byteData;
					sum+=byteData;
				}
			}
			int checksum = Integer.decode("0x"+line.substring(currentPos,currentPos+2));

			int byteSum = (byte)((~sum)+1) & 0xFF;
			
			if (byteSum != checksum){
				throw new ReaderException("checksum failed");
			}
			
			if (recordType == 1){
				break;
			}
		}
		
		reader.close();
		
		buffer.flip();
		
		ShortBuffer shortBuffer = buffer.asShortBuffer();
		final int[] programCode = new int[shortBuffer.capacity()];
		for (int i=0;i<programCode.length;i++){
			
			short word = (short)(shortBuffer.get(i) & 0xFFFF);
			
			programCode[i] = (short)(((word & 0xFF00) >> 8) + ((word & 0xFF) << 8));
		}
		program.setRawProgram(programCode);
	}
	
	private class ProgramImpl
		implements Program{
		
		private int[] rawProgram;
		private Set<Symbol> symbols;
		private List<CodeLine> code;

		@Override
		public boolean isSymbolsAvailable() {
			return symbols != null;
		}

		@Override
		public boolean isCodeAvailable() {
			return code != null;
		}

		@Override
		public Set<Symbol> getSymbols() {
			return symbols;
		}

		@Override
		public List<CodeLine> getCode() {
			return code;
		}

		@Override
		public int[] getRawProgram() {
			return rawProgram;
		}

		public void setRawProgram(int[] rawProgram) {
			this.rawProgram = rawProgram;
		}

		public void setSymbols(Set<Symbol> symbols) {
			this.symbols = symbols;
		}

		public void setCode(List<CodeLine> code) {
			this.code = code;
		}
		
	}
	
	private class LineTokenizer {
		private String line;
		private int position;
		
		public LineTokenizer(String line){
			this.line = line;
			this.position = 0;
		}
		
		public String nextToken(){
			while (position < line.length() && line.charAt(position)==' '){
				position++;
			}
			StringBuilder builder = new StringBuilder();
			while (position < line.length() && line.charAt(position)!=' '){
				builder.append(line.charAt(position));
				position++;
			}
			return builder.toString();
		}
		
		public int getPosition(){
			return position;
		}
		
		public String getRemainder(){
			return line.substring(position);
		}
		public String getRemainder(int position){
			return line.substring(position);
		}
	}
}
