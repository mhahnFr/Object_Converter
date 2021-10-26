package hahn.mainIDE.gui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import hahn.mainIDE.ClassProperty;
import hahn.mainIDE.NotSavedFileInterface;
import hahn.utils.ByteHelper;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ClassPropertyGUI extends ClassProperty {
	private JPanel mainPanel;
	private JTextField name;
	private JCheckBox checkBox;
	private JComboBox<Type> typeBox;
	private String readName;
	private Type readType;
	private NotSavedFileInterface fileSaver;
	
	public ClassPropertyGUI(byte[] sbyBytes) {
		this();
		int nL = ByteHelper.bytesToInt(sbyBytes);
		int bytePos = Integer.BYTES;
		readName = new String(ByteHelper.subBytes(sbyBytes, bytePos, bytePos += nL));
		nL = ByteHelper.bytesToInt(ByteHelper.subBytes(sbyBytes, bytePos, bytePos += Integer.BYTES));
		readType = Type.valueOf(new String(ByteHelper.subBytes(sbyBytes, bytePos, bytePos += nL)));
		typeBox.setSelectedItem(readType);
		name.setText(readName);
		name.setForeground(Color.BLACK);
	}
	
	public ClassPropertyGUI() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		name = new JTextField("Name der Eigenschaft");
		name.setForeground(Color.LIGHT_GRAY);
		name.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent event) {
				if(name.getText().equals("Name der Eigenschaft")) {
					name.setText("");
					name.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent event) {
				if(name.getText().equals("")) {
					name.setText("Name der Eigenschaft");
					name.setForeground(Color.LIGHT_GRAY);
				}
			}
		});
		name.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent event) {
				checkForSaved();
			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				checkForSaved();
			}

			@Override
			public void removeUpdate(DocumentEvent event) {
				checkForSaved();
			}
			
			private void checkForSaved() {
				if(fileSaver != null) {
					if(name.getText().equals(readName)) {
						if(readType == type) {
							fileSaver.setSaved(true);
						} else {
							fileSaver.setSaved(false);
						}
					} else {
						fileSaver.setSaved(false);
					}
				}
			}
		});
		JLabel typName = new JLabel(" Typ: ");
		typeBox = new JComboBox<>();
		for(Type t : Type.values()) {
			typeBox.addItem(t);
		}
		typeBox.setSelectedItem(null);
		typeBox.addItemListener(event -> {
			type = (Type) typeBox.getSelectedItem();
			if(fileSaver != null) {
				if(readType == type) {
					if(name.getText().equals(readName)) {
						fileSaver.setSaved(true);
					} else {
						fileSaver.setSaved(false);
					}
				} else {
					fileSaver.setSaved(false);
				}
			}
		});
		checkBox = new JCheckBox();
		mainPanel.add(checkBox);
		mainPanel.add(name);
		mainPanel.add(typName);
		mainPanel.add(typeBox);
		mainPanel.setBorder(new EtchedBorder());
	}
	
	public JPanel getJPanel() {
		return mainPanel;
	}
	
	public void setFileSaverInterface(NotSavedFileInterface i) {
		fileSaver = i;
	}
	
	public boolean isSelected() {
		return checkBox.isSelected();
	}

	@Deprecated
	public ClassPropertyValue getValue() {
		return null;
	}
	
	@Override
	public byte[] convertToBytes() {
		readName = name.getText();
		readType = type;
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		int length = 0;
		byte[] b = name.getText().getBytes();
		list.add(ByteHelper.intToBytes(b.length));
		length += Integer.BYTES;
		length += b.length;
		list.add(b);
		b = type.name().getBytes();
		list.add(ByteHelper.intToBytes(b.length));
		list.add(b);
		length += Integer.BYTES;
		length += b.length;
		byte[] toReturn = new byte[length];
		byte[][] all = list.toArray(new byte[list.size()][]);
		int byteCount = 0;
		for(byte[] bts : all) {
			for(byte bb : bts) {
				toReturn[byteCount] = bb;
				byteCount++;
			}
		}
		return toReturn;
	}
}