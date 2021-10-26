package hahn.mainIDE.gui;

import javax.swing.JTextField;

public class ClassPropertyNumber extends ClassPropertyText {

	public ClassPropertyNumber(String name, Type type) {
		super(name, type);
	}
	
 	@Override
 	public ClassPropertyValue getValue() {
 		ClassPropertyValue v = new ClassPropertyValue(name, type);
 		String text = ((JTextField) value).getText();
 		Object value;
 		switch(type) {
 		case BYTE:
 			value = Byte.parseByte(text);
 			break;
 			
 		case SHORT:
 			value = Short.parseShort(text);
 			break;
 			
 		case INTEGER:
 			value = Integer.parseInt(text);
 			break;
 			
 		case LONG:
 			value = Long.parseLong(text);
 			break;
 			
 		case FLOAT:
 			value = Float.parseFloat(text);
 			break;
 			
 		case DOUBLE:
 			value = Double.parseDouble(text);
 			break;
 			
 		default:
 			value = null;
 		}
 		v.setValue(value);
 		return v;
 	}
}