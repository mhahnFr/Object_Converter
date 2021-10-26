package hahn.mainIDE.gui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class ClassPropertyNormalGUI {
	private final ClassPropertyNGUI[] properties;
	private JPanel component;
	
	public ClassPropertyNormalGUI(ClassPropertyNGUI[] properties) {
		this.properties = properties;
		component = new JPanel();
		component.setLayout(new GridLayout(this.properties.length, 1));
		for(ClassPropertyNGUI prop : properties) {
			JPanel toAdd = prop.getJPanel();
			toAdd.setBorder(new EtchedBorder());
			component.add(toAdd);
		}
	}
	
	public void setValues(ClassPropertyValue[] values) {
		if(values != null) {
			if(values.length == properties.length) {
				for(int i = 0; i < properties.length; i++) {
					properties[i].setValue(values[i]);
				}
			}
		} else {
			for(ClassPropertyNGUI cp : properties) {
				cp.resetValue();
			}
		}
	}
	
	public ClassPropertyValue[] getValues() {
		ClassPropertyValue[] v = new ClassPropertyValue[properties.length];
		for(int i = 0; i < properties.length; i++) {
			v[i] = properties[i].getValue();
		}
		return v;
	}
	
	public Component getComponent() {
		return component;
	}
}