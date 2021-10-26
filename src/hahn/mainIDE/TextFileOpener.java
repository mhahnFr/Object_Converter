package hahn.mainIDE;

import java.io.File;

import javax.swing.SwingWorker;

public class TextFileOpener extends SwingWorker<String[], Void> {
	private File toOpen;
	private FileManager manager;
	
	public TextFileOpener(File toOpen) {
		this.toOpen = toOpen;
		manager = new FileManager();
	}
	
	protected String[] doInBackground() {
		return manager.openTextFile(toOpen);
	}
}