package sim.mcu;

import java.util.List;
import java.util.Set;

import sim.ui.CodeLine;

public interface Program {

	public boolean isSymbolsAvailable();
	public boolean isCodeAvailable();
	public Set<Symbol> getSymbols();
	public List<CodeLine> getCode();
	public int[] getRawProgram();
}
