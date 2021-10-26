package hahn.mainIDE.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import hahn.mainIDE.ClassProperty;

public abstract class ClassPropertyNGUI extends ClassProperty {
	protected JPanel panel;
	protected JComponent value;
	
	public ClassPropertyNGUI(String name) {
		this.name = name;
		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.add(new JLabel(name));
	}
	
	public abstract void setValue(ClassPropertyValue value);
	
	public abstract void resetValue();
	
	public JPanel getJPanel() {
		return panel;
	}
	
	public static String getTextFieldHintByType(Type type) {
		switch(type) {
		case BYTE:
			return Byte.MIN_VALUE + " bis " + Byte.MAX_VALUE;
			
		case SHORT:
			return Short.MIN_VALUE + " bis " + Short.MAX_VALUE;
			
		case INTEGER:
			return Integer.MIN_VALUE + " bis " + Integer.MAX_VALUE;
			
		case LONG:
			return Long.MIN_VALUE + " bis " + Long.MAX_VALUE;
			
		case FLOAT:
			return Float.MIN_VALUE + " bis " + Float.MAX_VALUE;
			
		case DOUBLE:
			return Double.MIN_VALUE + " bis " + Double.MAX_VALUE;
			
		case BOOLEAN:
			return Boolean.TRUE + " oder " + Boolean.FALSE;
			
		case STRING:
			return "Beliebiger Text";
			
		default:
			return "???";
		}

	}
}