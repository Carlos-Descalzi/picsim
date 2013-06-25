package sim.ui.p16f84a;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sim.mcu.p16f84a.Pic16f84a;

public class Pic16f84aTestCase {

	private Pic16f84a mcu;
	
	@Before
	public void setup(){
		mcu = new Pic16f84a();
	}
	
	@Test
	public void setSubwfToWNegative(){
		mcu.setW((byte)0x08);
		mcu.getDataMemory().setAbsoluteAddressValue(0x20, (byte)0x7);
		mcu.subwf(0x20);
		
		byte result = mcu.getW();// mcu.getDataMemory().getAbsoluteAddressValue(0x20);
		
		Assert.assertEquals(-1,result);
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_Z));
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_C));
	}
	@Test
	public void setSubwfToWNegative2(){
		mcu.setW((byte)0x01);
		mcu.getDataMemory().setAbsoluteAddressValue(0x20, (byte)0x0);
		mcu.subwf(0x20);
		
		byte result = mcu.getW();// mcu.getDataMemory().getAbsoluteAddressValue(0x20);
		
		Assert.assertEquals(-1,result);
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_Z));
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_C));
	}
	@Test
	public void setSubwfToWPositive(){
		mcu.setW((byte)0x07);
		mcu.getDataMemory().setAbsoluteAddressValue(0x20, (byte)0x8);
		mcu.subwf(0x20);
		
		byte result = mcu.getW();
		
		Assert.assertEquals(1,result);
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_Z));
		Assert.assertEquals(Pic16f84a.MASK_C,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_C));
	}
	@Test
	public void setSubwfToWZero(){
		mcu.setW((byte)0x01);
		mcu.getDataMemory().setAbsoluteAddressValue(0x20, (byte)0x1);
		mcu.subwf(0x20);
		
		byte result = mcu.getW();
		
		Assert.assertEquals(0,result);
		Assert.assertEquals(Pic16f84a.MASK_Z,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_Z));
		Assert.assertEquals(Pic16f84a.MASK_C,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_C));
	}
	@Test
	public void setSubwfToFNegative(){
		mcu.setW((byte)0x08);
		mcu.getDataMemory().setAbsoluteAddressValue(0x20, (byte)0x7);
		mcu.subwf((0x20 | 0x80));
		
		byte result = mcu.getDataMemory().getAbsoluteAddressValue(0x20);
		
		Assert.assertEquals(-1,result);
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_Z));
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_C));
	}
	@Test
	public void setSubwfToFPositive(){
		mcu.setW((byte)0x07);
		mcu.getDataMemory().setAbsoluteAddressValue(0x20, (byte)0x8);
		mcu.subwf((0x20 | 0x80));
		
		byte result = mcu.getDataMemory().getAbsoluteAddressValue(0x20);
		
		Assert.assertEquals(1,result);
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_Z));
		Assert.assertEquals(Pic16f84a.MASK_C,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_C));
	}
	@Test
	public void setSublwNegative(){
		mcu.setW((byte)0x08);
		mcu.sublw(0x07);
		
		byte result = mcu.getW();// mcu.getDataMemory().getAbsoluteAddressValue(0x20);
		
		Assert.assertEquals(-1,result);
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_Z));
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_C));
	}
	@Test
	public void setSublwPositive(){
		mcu.setW((byte)0x07);
		mcu.sublw(0x08);
		
		byte result = mcu.getW();// mcu.getDataMemory().getAbsoluteAddressValue(0x20);
		
		Assert.assertEquals(1,result);
		Assert.assertEquals(0,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_Z));
		Assert.assertEquals(Pic16f84a.MASK_C,(mcu.getDataMemory().getAbsoluteAddressValue(Pic16f84a.STATUS) & Pic16f84a.MASK_C));
	}
}
