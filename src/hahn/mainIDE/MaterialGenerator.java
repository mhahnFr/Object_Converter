package hahn.mainIDE;

import hahn.graphicEngine.GraphicMaterial;

import javax.swing.SwingWorker;

public class MaterialGenerator extends SwingWorker<GraphicMaterial, Void> {
	private byte[] matBytes;
	
	public MaterialGenerator(byte[] materialBytes) {
		matBytes = materialBytes;
	}
	
	@Override
	protected GraphicMaterial doInBackground() {
		return new GraphicMaterial(matBytes);
	}
}