package sim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class Project {

	private String name;
	private File file;
	private File sourceFolder;
	private File includeFolder;
	private List<ProjectListener> listeners = new ArrayList<ProjectListener>();
	private File debugOutputFolder;
	private File releaseOutputFolder;
	public File getDebugOutputFolder() {
		return debugOutputFolder;
	}

	public File getReleaseOutputFolder() {
		return releaseOutputFolder;
	}

	private Project(){}
	
	public Project(String name,File projectFile){
		this.name = name;
		this.file = projectFile;
		if (!file.exists()){
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		sourceFolder = new File(file.getParentFile(), "src");
		sourceFolder.mkdirs();
		includeFolder = new File(file.getParentFile(),"inc");
		includeFolder.mkdirs();
		debugOutputFolder = new File(file.getParentFile(),"debug-bin");
		debugOutputFolder.mkdirs();
		releaseOutputFolder = new File(file.getParentFile(),"release-bin");
		releaseOutputFolder.mkdirs();
	}
	
	public static Project load(File file)
		throws Exception{
		
		
		Project project = new Project();
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		project.name = xpath.evaluate("/project/name", doc);
		project.file = file;
		
		String srcFolderName = xpath.evaluate("/project/src-folder",doc);
		String incFolderName = xpath.evaluate("/project/inc-folder",doc);
		String releaseFolderName = xpath.evaluate("/project/release-output-folder",doc);
		String debugFolderName = xpath.evaluate("/project/debug-output-folder",doc);

		project.sourceFolder = new File(file.getParentFile(),srcFolderName);
		project.includeFolder = new File(file.getParentFile(),incFolderName);
		project.releaseOutputFolder = new File(file.getParentFile(),releaseFolderName);
		project.debugOutputFolder = new File(file.getParentFile(),debugFolderName);
		
		return project;
	}
	
	public void save() throws IOException{
		PrintStream stream = new PrintStream(new FileOutputStream(file));
		stream.println("<project>");
		
		stream.println("\t<name>"+name+"</name>");
		stream.println("<src-folder>"+sourceFolder.getName()+"</src-folder>");
		stream.println("<inc-folder>"+includeFolder.getName()+"</inc-folder>");
		stream.println("<debug-output-folder>"+debugOutputFolder.getName()+"</debug-output-folder>");
		stream.println("<release-output-folder>"+releaseOutputFolder.getName()+"</release-output-folder>");
		stream.println("</project>");
		stream.flush();
		stream.close();
	}
	public String getName(){
		return name;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getSourceFolder(){
		return sourceFolder;
	}

	public File getIncludeFolder(){
		return includeFolder;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addFile(File folder, String name){
		File newFile = new File(folder,name);
		try {
			newFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProjectEvent event = new ProjectEvent(this,folder,name);
		for (ProjectListener listener:listeners){
			listener.fileAdded(event);
		}
	}
	
	public void addListener(ProjectListener listener){
		listeners.add(listener);
	}

	public void removeListener(ProjectListener listener){
		listeners.remove(listener);
	}
	
	public List<File> getFolders(){
		return Arrays.asList(sourceFolder,includeFolder);
	}
	
	
}
