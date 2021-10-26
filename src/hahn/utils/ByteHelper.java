package hahn.utils;

import java.util.ArrayList;

/**
 * Diese Klasse enthält ein paar Hilfsfunktionen um Zahlen in bytes und umgekehrt
 *  zu konvertieren. WICHTIG: Nicht kompatibel mit {@link DataInput} oder {@link DataOutput}!
 * 
 * @author Manuel Hahn
 * @since 01.06.2017
 */
public abstract class ByteHelper {
	
	/**
	 * Gibt das erste byte der angegebenen Zahl zurück.
	 * 
	 * @param x die Zahl von welchem das erste byte zurückgegeben werden soll
	 * @return das erste byte der angegebenen Zahl
	 */
	private static byte shortPart1(short x) {
		return (byte) (x >> 0);
	}
	
	/**
	 * Gibt das zweite byte der angegebenen Zahl zurück.
	 * 
	 * @param x die Zahl dessen zweites byte zurückgegeben werden soll
	 * @return das zweite byte der angegebenen Zahl
	 */
	private static byte shortPart2(short x) {
		return (byte) (x >> 8);
	}
	
	/**
	 * Gibt das dritte byte der angegebenen Zahl zurück.
	 * 
	 * @param x die Zahl, dessen drittes byte zurückgegeben werden soll
	 * @return das dritte byte der angegebenen Zahl
	 */
	private static byte intPart3(int x) {
		return (byte) (x >> 16);
	}
	
	/**
	 * Gibt das vierte byte der angegebenen Zahl zurück.
	 * 
	 * @param x die Zahl, dessen viertes byte zurückgegeben werden soll
	 * @return das vierte byte der angegebenen Zahl
	 */
	private static byte intPart4(int x) {
		return (byte) (x >> 24);
	}
	
	/**
	 * Gibt das fünfte byte der angegebenen Zahl zurück.
	 * 
	 * @param x die Zahl, dessen fünftes byte zurückgegeben werden soll
	 * @return 
	 */
	private static byte longPart5(long x) {
		return (byte) (x >> 32);
	}
	
	/**
	 * Gibt das sechste byte der angegebenen Zahl zurück.
	 * 
	 * @param x die Zahl, dessen sechstes byte zurückgegeben werden soll
	 * @return das sechste byte der angegebenen Zahl
	 */
	private static byte longPart6(long x) {
		return (byte) (x >> 40);
	}
	
	/**
	 * Gibt das siebte byte der angegebenen Zahl zurück.
	 * 
	 * @param x die Zahl, dessen siebtes byte zurÃ¼ckgegeben werden soll
	 * @return das siebte byte der angegebenen Zahl
	 */
	private static byte longPart7(long x) {
		return (byte) (x >> 48);
	}
	
	/**
	 * Gibt das achte byte der angegebenen Zahl zurück.
	 * 
	 * @param x die Zahl, dessen achtes byte zurückgegeben werden soll
	 * @return das achte byte der angegebenen Zahl
	 */
	private static byte longPart8(long x) {
		return (byte) (x >> 56);
	}

	/**
	 * Konvertiert den angegebenen Short zu einem byte-Array mit zwei byte.
	 * 
	 * @param x der zu konvertierende Short
	 * @return zwei bytes, die diesem Short entsprechen
	 */
	public static byte[] shortToBytes(short x) {
		return new byte[] {
				shortPart1(x),
				shortPart2(x)
		};
	}
	
	/**
	 * Konvertiert den angegebenen int in vier bytes. Diese werden als Array zurückgegeben.
	 * 
	 * @return ein byte-Array mit dem int
	 */
	public static byte[] intToBytes(int x) {
		return new byte[] {
				shortPart1((short) x),
				shortPart2((short) x),
				intPart3(x),
				intPart4(x)
		};
	}
	
	/**
	 * Konvertiert den angegebenen Long in ein byte-Array mit acht byte.
	 * 
	 * @param x der zu konvertierende Long
	 * @return acht bytes, die dem angegebenen Long entsprechen
	 */
	public static byte[] longToBytes(long x) {
		return new byte[] {
				shortPart1((short) x),
				shortPart2((short) x),
				intPart3((int) x),
				intPart4((int) x),
				longPart5(x),
				longPart6(x),
				longPart7(x),
				longPart8(x)
		};
	}

	/**
	 * konvertiert das angegebene byte-Array in einen short.
	 * 
	 * @param bytes die zu konvertierenden bytes
	 * @return ein short aus dem byte-Array
	 */
	public static short bytesToShort(byte[] bytes) {
		return (short) ((bytes[0] & 0xFF) | (bytes[1] & 0xFF) << 8);
	}
	
	/**
	 * konvertiert das angegebene byte-Array in einen int.
	 * 
	 * @param bytes die zu konvertierenden bytes
	 * @return ein int aus dem byte-Array
	 */
	public static int bytesToInt(byte[] bytes) {
		return (bytes[0] & 0xFF) | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) << 16 | (bytes[3] & 0xFF) << 24;
	}
	
	/**
	 * konvertiert das angegebene byte-Array in einen long.
	 * 
	 * @param bytes die zu konvertierenden bytes
	 * @return ein long aus dem byte-Array
	 */
	public static long bytesToLong(byte[] bytes) {
		return (bytes[0] & 0xFF) | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) << 16 | (bytes[3] & 0xFF) << 24
				| (bytes[4] & 0xFF) << 32 | (bytes[5] & 0xFF) << 40 | (bytes[6] & 0xFF) << 48 | (bytes[7] & 0xFF) << 56;
	}
	
	/**
	 * Castet Byte[] nach byte[].
	 * 
	 * @param bytes das Byte-Array
	 * @return ein byte-Array
	 */
	public static byte[] castToByte(Byte[] bytes) {
		byte[] toReturn = new byte[bytes.length];
		for(int i = 0; i < bytes.length; i++) {
			toReturn[i] = bytes[i];
		}
		return toReturn;
	}

	public static byte[] castArrayListToBytes(ArrayList<byte[]> list) {
		ArrayList<Byte> bytes = new ArrayList<>();
		for(byte[] bts : list) {
			for(byte b : bts) {
				bytes.add(b);
			}
		}
		return castToByte(bytes.toArray(new Byte[list.size()]));
	}
	
	/**
	 * Schneidet aus dem angegebenen byte-Array ein neues heraus, das die bytes vom {@code fromIndex} bis zum 
	 * {@code endIndex} aus dem angegebenen Array herausschneidet.
	 * 
	 * @param bytes das Array, aus welchem die bytes ausgeschnitten werden sollen
	 * @param fromIndex 
	 * @param endIndex
	 * @return
	 */
	public static byte[] subBytes(byte[] bytes, int fromIndex, int endIndex) {
		byte[] toReturn = new byte[endIndex - fromIndex];
		System.arraycopy(bytes, fromIndex, toReturn, 0, toReturn.length);
		/*for(int i = 0; fromIndex < endIndex; fromIndex++, i++) {
			toReturn[i] = bytes[fromIndex];
			//MainWindow.schleifen++;
		}*/
		return toReturn;
	}
}