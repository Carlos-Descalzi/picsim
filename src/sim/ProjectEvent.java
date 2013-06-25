package sim;

import java.io.File;
import java.util.EventObject;

public class ProjectEvent extends EventObject {

	private static final long serialVersionUID = -3726595885965818109L;

	private File folder;
	private String file;
	
	public ProjectEvent(Object source,File folder, String file) {
		super(source);
		this.folder = folder;
		this.file = file;
	}

	public File getFolder() {
		return folder;
	}

	public String getFile() {
		return file;
	}

}
