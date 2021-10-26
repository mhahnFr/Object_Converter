package hahn.mainIDE;

import hahn.graphicEngine.GraphicObject;

import javax.swing.SwingWorker;

public class ObjectGenerator extends SwingWorker<GraphicObject, Void> {
	private byte[] objBytes;
	private String name;
	private String klasse;
	
	public ObjectGenerator(byte[] objectBytes, String name, String klasse) {
		objBytes = objectBytes;
		this.name = name;
		this.klasse = klasse;
	}

	@Override
	protected GraphicObject doInBackground() {
		GraphicObject o = new GraphicObject(objBytes);
		o.setName(name);
		o.setKlasse(klasse);
		return o;
	}
}