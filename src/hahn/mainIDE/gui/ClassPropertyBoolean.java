package hahn.mainIDE.gui;

import javax.swing.JCheckBox;

public class ClassPropertyBoolean extends ClassPropertyNGUI {

	public ClassPropertyBoolean(String name) {
		super(name);
		type = Type.BOOLEAN;
		value = new JCheckBox();
		value.setToolTipText(getTextFieldHintByType(type));
		panel.add(value);
	}

	@Override
	public byte[] convertToBytes() {
		return getValue().convertToBytes();
	}

	@Override
	public void setValue(ClassPropertyValue value) {
		((JCheckBox) this.value).setSelected((boolean) value.getRawValue());
	}
	
	@Override
	public ClassPropertyValue getValue() {
		ClassPropertyValue v = new ClassPropertyValue(name, type);
		v.setValue(((JCheckBox) value).isSelected());
		return v;
	}
	
	@Override
	public void resetValue() {
		((JCheckBox) value).setSelected(false);
	}
}