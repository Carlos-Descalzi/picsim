package sim.ui.p16f84a;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		byte value = (byte)0xFB;
		value++;
		System.out.println(value+","+Integer.toHexString(value));
		value++;
		System.out.println(value+","+Integer.toHexString(value));

	}

}
