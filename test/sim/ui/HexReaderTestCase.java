package sim.ui;

import java.io.File;

import org.junit.Test;

import sim.io.HexReader;

public class HexReaderTestCase {

	@Test
	public void testRead() throws Exception{
		HexReader reader = new HexReader();
		reader.read(new File("main.HEX"));
	}
}
