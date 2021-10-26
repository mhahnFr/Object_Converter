package hahn.mainIDE.gui;

import hahn.mainIDE.ClassProperty;
import hahn.utils.ByteHelper;

public class ClassPropertyConverter {
	public static ClassPropertyNGUI getClassProperty(byte[] sby) {
		int nL = ByteHelper.bytesToInt(sby);
		int bytePos = Integer.BYTES;		
		String name = new String(ByteHelper.subBytes(sby, bytePos, bytePos += nL));
		nL = ByteHelper.bytesToInt(ByteHelper.subBytes(sby, bytePos, bytePos += Integer.BYTES));
		ClassProperty.Type type = ClassProperty.Type.valueOf(new String(ByteHelper.subBytes(sby, bytePos, bytePos += nL)));
		switch(type) {
		case BOOLEAN:
			return new ClassPropertyBoolean(name);
			
		case BYTE:
		case DOUBLE:
		case FLOAT:
		case INTEGER:
		case LONG:
		case SHORT:
			return new ClassPropertyNumber(name, type);
			
		case STRING:
			return new ClassPropertyText(name, type);
			
		default:
			return null;
		}
	}
}