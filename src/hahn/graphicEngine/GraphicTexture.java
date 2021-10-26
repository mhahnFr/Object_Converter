package hahn.graphicEngine;

import hahn.mainIDE.Convertable;
import hahn.utils.ByteHelper;

/**
 * Eine Textur, also ein Bild mit Pfad.
 * 
 * @author Manuel Hahn
 */
public class GraphicTexture implements Convertable {
	/**
	 * Die Datei zum Bild dieser Textur.
	 */
	private final String fileName;
	
	/**
	 * Erzeugt die Textur.
	 * 
	 * @param fileName die Datei des Bildes
	 */
	public GraphicTexture(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Gibt den Dateinamen des Bildes der Textur zurück.
	 * 
	 * @return den Dateinamen des Bildes
	 */
	public String getFileName() {
		return fileName;
	}

	@Override
	public byte[] convertToBytes() {
		byte[] fileNameb = fileName.getBytes();
		byte[] toReturn = new byte[fileNameb.length + Integer.BYTES];
		byte[] length = ByteHelper.intToBytes(fileNameb.length);
		for(int i = 0; i < Integer.BYTES; i++) {
			toReturn[i] = length[i];
		}
		for(int i = 0; i < fileNameb.length; i++) {
			toReturn[i + Integer.BYTES] = fileNameb[i];
		}
		return toReturn;
	}
}