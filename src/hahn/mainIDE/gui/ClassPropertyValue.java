package hahn.mainIDE.gui;

import java.util.ArrayList;

import hahn.mainIDE.ClassProperty;
import hahn.utils.ByteHelper;

public class ClassPropertyValue extends ClassProperty {
	private Object value;
	
	public ClassPropertyValue(byte[] gwoSbyBytes) {
		int bytePos = Integer.BYTES;
		final int nameLength = ByteHelper.bytesToInt(gwoSbyBytes);
		name = new String(ByteHelper.subBytes(gwoSbyBytes, bytePos, bytePos += nameLength));
		switch(gwoSbyBytes[bytePos]) {
		case 1:
			type = Type.BOOLEAN;
			value = gwoSbyBytes[++bytePos] == 1 ? true : false;
			break;
			
		case 2:
			type = Type.STRING;
			final int tempLength = 
					ByteHelper.bytesToInt(ByteHelper.subBytes(gwoSbyBytes, ++bytePos, bytePos += Integer.BYTES));
			value = new String(ByteHelper.subBytes(gwoSbyBytes, bytePos, bytePos += tempLength));
			break;
			
		case 3:
			type = Type.INTEGER;
			value = ByteHelper.bytesToInt(ByteHelper.subBytes(gwoSbyBytes, ++bytePos, bytePos += Integer.BYTES));
			break;
			
		case 4:
			type = Type.LONG;
			value = ByteHelper.bytesToLong(ByteHelper.subBytes(gwoSbyBytes, ++bytePos, bytePos += Long.BYTES));
			break;
			
		case 5:
			type = Type.SHORT;
			value = ByteHelper.bytesToShort(ByteHelper.subBytes(gwoSbyBytes, ++bytePos, bytePos += Short.BYTES));
			break;
			
		case 6:
			type = Type.DOUBLE;
			value = Double.longBitsToDouble(ByteHelper.bytesToLong(
					ByteHelper.subBytes(gwoSbyBytes, ++bytePos, bytePos += Double.BYTES)));
			break;
			
		case 7:
			type = Type.FLOAT;
			value = Float.intBitsToFloat(ByteHelper.bytesToInt(
					ByteHelper.subBytes(gwoSbyBytes, ++bytePos, bytePos += Float.BYTES)));
			break;
			
		case 8:
			type = Type.BYTE;
			value = gwoSbyBytes[++bytePos];
			break;
		}
	}
	
	public ClassPropertyValue(String name, Type type) {
		this.type = type;
		this.name = name;
	}
	
	private ClassPropertyValue(String name, Type type, Object value) {
		this(name, type);
		this.value = value;
	}
	
	public Object getRawValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public byte[] convertToBytes() {
		ArrayList<byte[]> bytes = new ArrayList<>();
		byte[] name = this.name.getBytes();
		bytes.add(ByteHelper.intToBytes(name.length));
		bytes.add(name);
		int length = Integer.BYTES + name.length;
		switch(type) {
		case BOOLEAN:
			bytes.add(new byte[] { 1 });
			bytes.add(new byte[] {
					(byte) (((boolean) this.value) ? 1 : 0)
			});
			length += 1;
			break;
			
		case BYTE:
			bytes.add(new byte[] { 8 });
			bytes.add(new byte[] {
					((byte) this.value)
			});
			length += 1;
			break;
			
		case DOUBLE:
			bytes.add(new byte[] { 6 });
			bytes.add(ByteHelper.longToBytes(Double.doubleToLongBits((double) this.value)));
			length += Double.BYTES;
			break;
			
		case FLOAT:
			bytes.add(new byte[] { 7 });
			bytes.add(ByteHelper.intToBytes(Float.floatToIntBits((float) this.value)));
			length += Float.BYTES;
			break;
			
		case INTEGER:
			bytes.add(new byte[] { 3 });
			bytes.add(ByteHelper.intToBytes((int) this.value));
			length += Integer.BYTES;
			break;
			
		case LONG:
			bytes.add(new byte[] { 4 });
			bytes.add(ByteHelper.longToBytes((long) this.value));
			length += Long.BYTES;
			break;
			
		case SHORT:
			bytes.add(new byte[] { 5 });
			bytes.add(ByteHelper.shortToBytes((short) this.value));
			length += Short.BYTES;
			break;
			
		case STRING:
			bytes.add(new byte[] { 2 });
			byte[] string = ((String) this.value).getBytes();
			bytes.add(ByteHelper.intToBytes(string.length));
			bytes.add(string);
			length += Integer.BYTES + string.length;
			break;
			
		default:
			bytes.add(new byte[3]);
			length += 2;
			break;
		}
		length++;
		byte[] toReturn = new byte[length];
		int byteCount = 0;
		for(byte[] bts : bytes) {
			for(byte b : bts) {
				toReturn[byteCount] = b;
				byteCount++;
			}
		}
		return toReturn;
	}

	@Override
	public ClassPropertyValue getValue() {
		return new ClassPropertyValue(name, type, value);
	}
}