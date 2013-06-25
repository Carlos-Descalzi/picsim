package sim;

public class HexUtil {

	private static final String FILL = "000000000000";

	public static String toHex(short number){
		return toHex(number, 4);
	}

	public static String toHex(byte number){
		return toHex(number, 2);
	}
	
	public static String toHex(int number, int size){
		
		String hex = Integer.toHexString(number & getMask(size));
		
		if (hex.length() < size){
			hex = FILL.substring(0,size-hex.length())+hex;
		}
		
		return "0x"+hex.toUpperCase();
	}

	private static int getMask(int size) {
		int mask = 0;
		
		for (int i=0;i<size;i++){
			mask |= 0xF << (4*i);
		}
		return mask;
	}
}
