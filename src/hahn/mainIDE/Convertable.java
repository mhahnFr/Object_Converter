package hahn.mainIDE;

/**
 * Wenn man dieses Interface implementiert, kann ein Objekt der implementierenden Klasse automatsich
 * in byte-Form gespeichert werden.
 * 
 * @author Manuel Hahn
 * @since 10.07.2017
 */
public interface Convertable {
	
	/**
	 * Diese Methode konvertiert dieses Objekt zu einem Array mit bytes.
	 * 
	 * @return die aus allen Feldern der Methode generierten bytes
	 */
	byte[] convertToBytes();
	
	/**
	 * Diese Methode sollte implementiert werden, um aus Rohbytes ein Objekt 
	 * dieser Klasse zu erhalten.
	 * 
	 * @param bytes die Rohbytes, aus welchen das zurückgegebene Objekt generiert wurde
	 * @return ein Objekt dieser Klasse, aus den angegebenen Rohbytes generiert
	 */
	@Deprecated
	default Convertable byteInstance(byte[] bytes) {
		return this;
	}
}