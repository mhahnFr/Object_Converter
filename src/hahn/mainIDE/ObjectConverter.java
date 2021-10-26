package hahn.mainIDE;

import javax.swing.SwingWorker;

public class ObjectConverter extends SwingWorker<byte[], Void> {
	private Convertable object;
	
	public ObjectConverter(Convertable object) {
		this.object = object;
	}

	@Override
	protected byte[] doInBackground() {
		return object.convertToBytes();
	}
}