package hahn.mainIDE.gui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class ClassPropertyText extends ClassPropertyNGUI {
	protected String hint;

	public ClassPropertyText(String name, Type type) {
		super(name);
		this.type = type;
		hint = getTextFieldHintByType(type);
		value = new JTextField(hint);
		value.setToolTipText(hint);
		value.setForeground(Color.LIGHT_GRAY);
		value.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent event) {
				if(((JTextField) value).getText().equals(hint)) {
					((JTextField) value).setText("");
					((JTextField) value).setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent event) {
				if(((JTextField) value).getText().equals("")) {
					((JTextField) value).setText(hint);
					((JTextField) value).setForeground(Color.LIGHT_GRAY);
				}
			}
		});
		panel.add(value);
	}
	
	@Override
	public byte[] convertToBytes() {
		return getValue().convertToBytes();
	}
	
	@Override
	public void setValue(ClassPropertyValue value) {
		Object v = value.getRawValue();
		String rv = "" + v;
		if(v != null) {
			this.value.setForeground(Color.BLACK);
			((JTextField) this.value).setText(rv);
		}
	}

	@Override
	public ClassPropertyValue getValue() {
		ClassPropertyValue v = new ClassPropertyValue(name, type);
		v.setValue(((JTextField) value).getText());
		return v;
	}
	
	@Override
	public void resetValue() {
		((JTextField) value).setText(hint);
	}
}