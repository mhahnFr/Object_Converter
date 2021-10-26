package hahn.mainIDE.gui;

import hahn.mainIDE.Convertable;
import hahn.mainIDE.FileManager;
import hahn.mainIDE.NotSavedFileInterface;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ClassProperties extends JFrame implements ActionListener, NotSavedFileInterface {
	private JLabel current;
	private final String PLUS = "plus";
	private final String MINUS = "minus";
	private final String OPEN_FILE = "oeffnen";
	private final String SAVE_FILE = "sichern";
	private final String SAVE_AS = "sichernUnter";
	private final String CLOSE_WINDOW = "fensterSchliessen";
	private final String QUIT_APPLICATION = "beenden";
	private final String DELETE = "dateiLoeschen";
	private JPanel props;
	private ArrayList<ClassPropertyGUI> properties;
	private FileManager fileManager;
	private File opened;
	private JComboBox<String> classes;
	private JMenuItem delete;
	private volatile boolean saved = true;
	
	public ClassProperties(Window relative, FileManager fileManager, File toOpen) {
		this(relative, fileManager);
		opened = toOpen;
		delete.setEnabled(true);
		properties = this.fileManager.getSpecialBytesPropertiesGUI(toOpen);
		for(ClassPropertyGUI prop : properties) {
			props.add(prop.getJPanel());
			prop.setFileSaverInterface(this);
		}
		classes.setSelectedItem(this.fileManager.getLastKlasse());
		pack();
	}
	
	public ClassProperties(Window relative, FileManager fileManager) {
		super("Klassen-Properties Editor");
		this.fileManager = fileManager;
		properties = new ArrayList<>();
		final int shortcut = Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask();
		JMenuBar bar = new JMenuBar();
		JMenu datei = new JMenu("Datei");
		JMenuItem open = new JMenuItem("Öffnen...");
		open.addActionListener(this);
		open.setActionCommand(OPEN_FILE);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcut));
		
		JMenuItem saveAs = new JMenuItem("Sichern unter...");
		saveAs.addActionListener(this);
		saveAs.setActionCommand(SAVE_AS);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut + KeyEvent.SHIFT_DOWN_MASK));
		
		JMenuItem save = new JMenuItem("Sichern");
		save.addActionListener(this);
		save.setActionCommand(SAVE_FILE);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut));
		
		delete = new JMenuItem("Löschen");
		delete.addActionListener(this);
		delete.setActionCommand(DELETE);
		delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, shortcut));
		
		JMenuItem exit = new JMenuItem("Beenden");
		exit.addActionListener(this);
		exit.setActionCommand(QUIT_APPLICATION);
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcut));
		
		datei.add(open);
		datei.addSeparator();
		datei.add(save);
		datei.add(saveAs);
		datei.addSeparator();
		datei.add(delete);
		datei.addSeparator();
		datei.add(exit);
		bar.add(datei);
		
		JMenu window = new JMenu("Fenster");
		JMenuItem close = new JMenuItem("Schließen");
		close.addActionListener(this);
		close.setActionCommand(CLOSE_WINDOW);
		close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, shortcut));
		
		window.add(close);
		bar.add(window);
		setJMenuBar(bar);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(5, 5));
		current = new JLabel("Keine Datei geöffnet!");
		current.setFont(current.getFont().deriveFont(Font.BOLD));
		JPanel kus = new JPanel();
		kus.setLayout(new GridLayout(2, 0));
		kus.add(current);
		classes = new JComboBox<>();
		classes.setEditable(true);
		classes.addItemListener(event -> saved = false);
		if(classes.getItemCount() == 0) {
			MainWindow.fillClassList(classes, fileManager, this);
		}
		kus.add(classes);
		contentPane.add(kus, BorderLayout.NORTH);
 		JButton plus = new JButton("+");
		JButton minus = new JButton("-");
		plus.addActionListener(this);
		minus.addActionListener(this);
		plus.setActionCommand(PLUS);
		minus.setActionCommand(MINUS);
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(minus);
		buttons.add(plus);
		contentPane.add(buttons, BorderLayout.SOUTH);
		props = new JPanel();
		props.setLayout(new GridLayout(0, 1));
		JScrollPane sp = new JScrollPane(props);
		sp.getVerticalScrollBar().setUnitIncrement(10);
		sp.getHorizontalScrollBar().setUnitIncrement(10);
		contentPane.add(sp, BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		delete.setEnabled(false);
		pack();
		setLocationRelativeTo(relative);
	}

	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case PLUS:
			addProperty();
			break;
			
		case MINUS:
			deleteProperty();
			break;
			
		case QUIT_APPLICATION:
			exit();
			break;
			
		case SAVE_AS:
			saveToFile(true);
			break;
			
		case SAVE_FILE:
			saveToFile(false);
			break;
			
		case OPEN_FILE:
			open();
			break;
			
		case CLOSE_WINDOW:
			dispose();
			break;
			
		case DELETE:
			deleteCurrentFile();
			break;
		}
	}
	
	private void deleteCurrentFile() {
		if(opened == null) {
			JOptionPane.showMessageDialog(this, "Keine Datei geöffnet!",
					"Aktion nicht möglich", JOptionPane.ERROR_MESSAGE);
		}
		int option = JOptionPane.showConfirmDialog(this, "Soll die geöffnete Datei wirklich endgültig gelöscht werden?",
				"Löschen bestätigen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if(option == JOptionPane.OK_OPTION) {
			if(!opened.delete()) {
				try {
					Files.delete(opened.toPath());
				} catch (IOException e) {
					String message = "Datei kann nicht gelöscht werden:\n" + e.getMessage();
					JOptionPane.showMessageDialog(this, message, "Datei kann nicht gelöscht werden",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					System.err.println("------------------------");
				}
			}
		}
	}
	
	private void open() {
		FileFilter[] filters = new FileFilter[] {
				new FileNameExtensionFilter("Klassen-Properties (*.sby)", "sby")
		};
		JFileChooser chooser = MainWindow.getChooser(filters);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			ClassProperties newP = null;
			if(chooser.getFileFilter().equals(filters[0])) {
				newP = new ClassProperties(this, fileManager, selected);
			} else if(FileManager.getExtension(selected.getName()).equalsIgnoreCase("sby")) {
				newP = new ClassProperties(this, fileManager, selected);
			}
			if(newP != null) {
				newP.setVisible(true);
				newP.displayCurrentFile();
				newP.saved = true;
			}
		}
	}
	
	public void dispose() {
		if(!abort()) {
			super.dispose();
		}
	}
	
	private void exit() {
		if(!abort()) {
			System.exit(0);
		}
	}
	
	private boolean abort() {
		if(!saved) {
			int option = JOptionPane.showConfirmDialog(this, "Möchten Sie ihre Änderungen speichern?",
					"Ungesicherte Änderungen", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			switch(option) {
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:
				return true;
				
			case JOptionPane.YES_OPTION:
				saveToFile(false);
				break;
			}
		}
		return false;
	}
	
	public void setSaved(boolean saved) {
		this.saved = saved;
	}
	
	private void saveToFile(boolean newFile) {
		String s = (String) classes.getSelectedItem();
		if(s == null) {
			JOptionPane.showMessageDialog(this, "Bitte die Eigenschaften einer Klasse zuordnen!",
					"Keine Klasse ausgewählt", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(opened == null) {
			newFile = true;
		}
		File toWrite = opened;
		if(newFile) {
			JFileChooser chooser = MainWindow.getChooser(new FileFilter[] { 
					new FileNameExtensionFilter("Klassen-Properties (*.sby)", "sby") 
			});
			if(opened != null) {
				chooser.setCurrentDirectory(new File(opened.getPath()));
			}
			if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File selected = chooser.getSelectedFile();
				if(selected.exists()) {
					int choice = JOptionPane.showConfirmDialog(this, 
							"Die gewählte Datei existiert bereits.\nMöchten Sie sie überschreiben?", 
							"Datei überschreiben", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					switch(choice) {
					case JOptionPane.CANCEL_OPTION:
					case JOptionPane.CLOSED_OPTION:
						return;
					}
				}
				if(!FileManager.getExtension(selected.getName()).equalsIgnoreCase("sby")) {
					selected = new File(selected.getAbsolutePath() + ".sby");
				}
				toWrite = selected;
			} else {
				return;
			}
		}
		Convertable[] ps = new Convertable[properties.size()];
		for(int i = 0; i < ps.length; i++) {
			ps[i] = properties.get(i);
		}
		fileManager.writeSBYFile(ps, toWrite, s);
		if(opened == null) {
			opened = toWrite;
			displayCurrentFile();
			delete.setEnabled(true);
		}
		saved = true;
	}

	private void displayCurrentFile() {
		current.setText("Datei: " + opened.getName());
		setTitle(opened.getName() + " - Klassenproperties-Editor");
	}
	
	private void addProperty() {
		ClassPropertyGUI p = new ClassPropertyGUI();
		p.setFileSaverInterface(this);
		properties.add(p);
		props.add(p.getJPanel());
		props.validate();
	}
	
	private void deleteProperty() {
		ClassPropertyGUI cp;
		Iterator<ClassPropertyGUI> iterator = properties.iterator();
		while(iterator.hasNext()) {
			cp = iterator.next();
			if(cp.isSelected()) {
				props.remove(cp.getJPanel());
				iterator.remove();
			}
		}
		props.validate();
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new ClassProperties(null, new FileManager()).setVisible(true);
	}
}