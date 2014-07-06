package sim.io;

import java.io.File;
import java.io.IOException;

import sim.mcu.Program;

public interface ProgramReader {
	public Program read(File file) throws IOException, ReaderException;
}
