package sim;

import java.util.EventListener;

public interface ProjectListener extends EventListener {

	public void fileAdded(ProjectEvent event);
}
