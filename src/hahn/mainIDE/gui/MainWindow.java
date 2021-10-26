package hahn.mainIDE.gui;

import hahn.graphicEngine.GraphicMaterial;
import hahn.graphicEngine.GraphicObject;
import hahn.mainIDE.FileManager;
import hahn.mainIDE.TextFileOpener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Diese Klasse repräsentiert das Hauptfenster der Anwendeung. Sie bietet die main()-Methode,
 * sowie einen Standard-{@link JFileChooser}.
 * 
 * @author Manuel Hahn
 * @version 0.9
 */
public class MainWindow extends JFrame implements ActionListener {
	/**
	 * Der Code zum Beenden des Programms.
	 */
	private final String EXIT = "exit";
	private final String W = "ddff";
	/**
	 * Der Code zum Öffnen von Dateien.
	 */
	private final String OPEN = "open";
	/**
	 * Der Code zum Anzeigen der Einstellungen.
	 */
	private final String SETTINGS = "settings";
	/**
	 * Der Code zum Konvertieren der geöffneten Dateien.
	 */
	private final String CONVERT = "konvertieren";
	/**
	 * Der Code zum Anzeigen des vorherigen Objektes in der Liste.
	 */
	private final String PREVIOUS_OBJECT = "probj";
	/**
	 * Der Code zum Anzeigen des nächsten Objektes in der Liste.
	 */
	private final String NEXT_OBJECT = "neobj";
	/**
	 * Der Code zum Vergleichen von Dateien.
	 */
	private final String CHECK = "testingFiles";
	/**
	 * Der Code zum Öffnen vom SBY-Editor.
	 */
	private final String OPEN_C_P_EDITOR = "openClassPropsEditor";
	/**
	 * Der Code zum Sichern der aktuellen Datei(en).
	 */
	private final String SAVE = "sichern";
	/**
	 * Der Knopf zum Konvertieren der geöffneten Dateien.
	 */
	private JButton convert;
	/**
	 * Das {@link JLabel}, das den Dateinamen anzeigt.
	 */
	private JLabel fileName;
	/**
	 * Das {@link JLabel}, das den Dateinamen der eventuellen Materialsdatei anzeigt.
	 */
	private JLabel matName;
	/**
	 * Das universelle Label zur Anzeige, was das Programm den gerade so macht.
	 */
	private ProgressLabel doing;
	/**
	 * Das {@link JLabel} mit dem Namen des aktuell angezeigten 3D-Objekt.
	 */
	private JLabel currentObjName;
	/**
	 * Die Liste im Hauptfenster mit den Namen der Objekte.
	 */
	private JList<String> objNames;
	/**
	 * Die {@link ComboBox} mit den Klassennamen.
	 */
	private JComboBox<String> klasse;
	/**
	 * Die derzeit geöffneten 3D-Objekte.
	 */
	private GraphicObject[] objects;
	/**
	 * Die derzeit geöffneten Materials.
	 */
	private GraphicMaterial[] materials;
	/**
	 * Der Index des gerade angezeigten 3D-Objektes.
	 */
	private int objectCount = -1;
	/**
	 * Die gerade geöffneten Dateien.
	 */
	private File[] opened;
	/**
	 * Der zentrale {@link FileManager}.
	 */
	private FileManager fileManager;
	/**
	 * GUI-Kompnenten für die SBY-Werte.
	 */
	private HashMap<String, ClassPropertyNormalGUI> classPanels;
	/**
	 * Das Panel mit den SBY-Werten
	 */
	private JPanel classPropPanel;

	/**
	 * Erzeugt das Hauptfenster der Anwendung. Wenn die angegebenen {@link String}s nicht 
	 * {@code null} sind, werden sie gleich geöffnet.
	 * 
	 * @param file die GWO- oder *.obj-Datei
	 * @param second die optionale *.mtl-Datei
	 */
	public MainWindow(String file, String second) {
		super("Game IDE");
		fileManager = new FileManager();
		final int shortcut = Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMaskEx();
		JMenuBar bar = new JMenuBar();
		JMenu datei = new JMenu("Datei");

		JMenuItem exit = new JMenuItem("Beenden");
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcut));
		exit.addActionListener(this);
		exit.setActionCommand(EXIT);

		JMenuItem open = new JMenuItem("Öffnen...");
		open.addActionListener(this);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcut));
		open.setActionCommand(OPEN);

		JMenuItem settings = new JMenuItem("Einstellungen");
		settings.addActionListener(this);
		settings.setActionCommand(SETTINGS);
		settings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,
				shortcut));

		JMenuItem check = new JMenuItem("Dateien vergleichen...");
		check.addActionListener(this);
		check.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcut));
		check.setActionCommand(CHECK);
		
		JMenuItem save = new JMenuItem("Sichern");
		save.addActionListener(this);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut));
		save.setActionCommand(SAVE);
		
		JMenuItem saveWavefront = new JMenuItem("Wavefront sichern");
		saveWavefront.addActionListener(this);
		save.setActionCommand(W);
		
		JMenuItem saveAs = new JMenuItem("Sichern unter...");
		saveAs.addActionListener(this);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut + KeyEvent.SHIFT_DOWN_MASK));
		saveAs.setActionCommand(CONVERT);
		
		datei.add(open);
		datei.addSeparator();
		datei.add(save);
		datei.add(saveWavefront);
		datei.add(saveAs);
		datei.addSeparator();
		datei.add(check);
		datei.addSeparator();
		datei.add(settings);
		datei.addSeparator();
		datei.add(exit);
		bar.add(datei);
		
		JMenu objs = new JMenu("Objekt");
		
		JMenuItem pr = new JMenuItem("Zurück");
		pr.addActionListener(this);
		pr.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, shortcut));
		pr.setActionCommand(PREVIOUS_OBJECT);
		
		JMenuItem ne = new JMenuItem("Nächstes");
		ne.addActionListener(this);
		ne.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, shortcut));
		ne.setActionCommand(NEXT_OBJECT);
		
		objs.add(pr);
		objs.add(ne);
		
		JMenu window = new JMenu("Fenster");
		JMenuItem cpEditor = new JMenuItem("Klassen-Prop-Editor öffnen");
		cpEditor.addActionListener(this);
		cpEditor.setActionCommand(OPEN_C_P_EDITOR);
		cpEditor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, shortcut));
		
		window.add(cpEditor);
		
		bar.add(objs);
		bar.add(window);
		if(Desktop.isDesktopSupported()) {
			if(Desktop.getDesktop().isSupported(Desktop.Action.APP_MENU_BAR)) {
				Desktop.getDesktop().setDefaultMenuBar(bar);
			}
		}
		setJMenuBar(bar);

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		objNames = new JList<String>();
		objNames.addListSelectionListener(event -> displayObject(false, objNames.getSelectedValue()));
		JPanel content = new JPanel();
		JScrollPane objNamesScroll = new JScrollPane(objNames);
		objNamesScroll.getVerticalScrollBar().setUnitIncrement(5);
		objNamesScroll.getHorizontalScrollBar().setUnitIncrement(5);
		sp.add(objNamesScroll);
		sp.add(content);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		getContentPane().add(sp);
		fileName = new JLabel("Objektdatei: ");
		fileName.setFont(fileName.getFont().deriveFont(Font.BOLD));
		matName = new JLabel("Materialsdatei: ");
		doing = new ProgressLabel(fileManager.getLoaderIcon(), fileManager.getErrorIcon(), fileManager.getInfoIcon());
		doing.setVisible(false);
		convert = new JButton("Datei konvertieren");
		convert.setActionCommand(CONVERT);
		convert.addActionListener(this);
		JPanel objectProps = new JPanel();
		objectProps.setLayout(new BorderLayout(5, 5));
		JPanel objChos = new JPanel();
		objChos.setLayout(new BorderLayout());
		JButton prev = new JButton("<<");
		JButton next = new JButton(">>");
		currentObjName = new JLabel("Objekt auswählen");
		currentObjName.setFont(fileName.getFont());
		currentObjName.setHorizontalAlignment(SwingConstants.CENTER);
		prev.addActionListener(this);
		prev.setActionCommand(PREVIOUS_OBJECT);
		next.addActionListener(this);
		next.setActionCommand(NEXT_OBJECT);
		objChos.add(prev, BorderLayout.WEST);
		objChos.add(currentObjName, BorderLayout.CENTER);
		objChos.add(next, BorderLayout.EAST);
		JPanel opNorth = new JPanel();
		opNorth.setLayout(new BoxLayout(opNorth, BoxLayout.Y_AXIS));
		opNorth.add(objChos);
		JPanel kl = new JPanel();
		kl.setLayout(new BoxLayout(kl, BoxLayout.X_AXIS));
		objectProps.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		klasse = new JComboBox<>();
		klasse.setEditable(false);
		fillClassList(klasse, fileManager, this);
		fillPanels();
		klasse.setSelectedItem(null);
		klasse.addItemListener(event -> saveClass());
		JLabel kn = new JLabel("Klasse: ");
		kl.add(kn);
		kl.add(klasse);
		opNorth.add(kl);
		objectProps.add(opNorth, BorderLayout.NORTH);
		classPropPanel = new JPanel();
		classPropPanel.setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane(classPropPanel);
		scroll.getVerticalScrollBar().setUnitIncrement(5);
		scroll.getHorizontalScrollBar().setUnitIncrement(5);
		objectProps.add(scroll, BorderLayout.CENTER);
		objectProps.add(convert, BorderLayout.SOUTH);
		content.add(fileName);
		content.add(matName);
		content.add(objectProps);
		content.add(doing);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		if (file != null) {
			if(FileManager.getExtension(file).equalsIgnoreCase("obj") || 
					FileManager.getExtension(file).equalsIgnoreCase("mtl")) {
				openWavefront(new File[] { new File(file), new File(second) }, false);
			} else {
				openGWO(new File(file), false);
			}
		} else {
			open.doClick();
		}
	}
	
	/**
	 * Sucht die *.SBY-Dateien zusammen, fragt den Nutzer nach dem Ort bei Bedarf.
	 */
	private void fillPanels() {
		String p = fileManager.getClassSBYLocation();
		File path = p == null ? null : new File(p);
		classPanels = new HashMap<>();
		if(path == null || !path.exists()) {
			JFileChooser chooser = getChooser(new FileFilter[] { 
					new FileNameExtensionFilter("Klassen-Properties (*.sby)", "sby") 
			});
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				path = chooser.getSelectedFile();
				fileManager.saveClassSBYLocation(path.getAbsolutePath());
			} else {
				System.exit(0);
			}
		}
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.sby", "sby");
		File[] files = path.listFiles(file -> filter.accept(file));
		for(File file : files) {
			if(file.isFile()) {
				ClassPropertyNGUI[] props = fileManager.getSpecialBytesForUser(file);
				ClassPropertyNormalGUI prop = new ClassPropertyNormalGUI(props);
				classPanels.put(fileManager.getLastKlasse(), prop);
			}
		}
	}

	/**
	 * Füllt die angegebene {@link JComboBox} mit den gefundenen Klassennamen.
	 * 
	 * @param list die Liste, welcher die Namen hinzugefügt werden sollen
	 * @param fileManager der {@link FileManager} mit dem Ort der Dateien
	 * @param parent ein übergeordnetes {@link Window}, für den Fall, dass der Nutzer
	 * 				 den Ort der Klassen auswählen muss
	 */
	public static void fillClassList(JComboBox<String> list, FileManager fileManager, Window parent) {
		String p = fileManager.getClassFilesLocation();
		File path = p == null ? null : new File(p);
		list.removeAllItems();
		if(path == null || !path.exists()) {
			JFileChooser chooser = new JFileChooser("Ordner mit GraphicObject-Klassen suchen");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setFileHidingEnabled(true);
			if(chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				path = chooser.getSelectedFile();
				fileManager.saveClassFilesLocation(path.getAbsolutePath());
			} else {
				System.exit(0);
			}
		}
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.class oder *.java", "java", "class");
		File[] files = path.listFiles(file -> filter.accept(file));
		for(File file : files) {
			String fileName = file.getName();
			if(file.isFile() && !file.isHidden() && !fileName.equalsIgnoreCase("package-info.java")
					&& !fileName.equalsIgnoreCase("package-info.class")) {
				String toAdd;
				if(FileManager.getExtension(fileName).endsWith("s")) {
					toAdd = fileName.substring(0, fileName.length() - 6);
				} else {
					toAdd = fileName.substring(0, fileName.length() - 5);
				}
				list.addItem(toAdd);
			}
		}
		list.setSelectedItem(null);
	}

	/**
	 * Die Eingangsmethode. Setzt das Look & Feel auf das native, öffnet evtl. 
	 * angegebene Dateien.
	 * 
	 * @param args die Startargumente, in der sich bis zu zwei Dateien zum öffnen befinden können
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new MainWindow(args.length > 0 ? args[0] : null,
				args.length > 1 ? args[1] : null);
	}

	/**
	 * Fragt den Nutzer, ob die geöffnete Datei gesichert werden soll. Gibt zurück, 
	 * ob der Nutzer das Fragefenster geschlossen oder abgebrochen hat oder nicht.
	 * 
	 * @return {@code true}, wenn der Nutzer den Prozess abbrechen möchte
	 */
	private boolean abort() {
		int option = JOptionPane.showConfirmDialog(this, "Möchten Sie eventuell ungesicherte Datein sichern?",
				"Ungesicherte Änderungen", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		switch(option) {
		case JOptionPane.CANCEL_OPTION:
		case JOptionPane.CLOSED_OPTION:
			return true;
			
		case JOptionPane.YES_OPTION:
			saveToFile();
			break;
		}
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
		case EXIT:
			if(!abort()) {
				System.exit(0);
			}
			break;

		case OPEN:
			setSpecialBytesToCurrentDisplayedObject();
			if(!abort()) {
				openChooser();
			}
			break;

		case SAVE:
			if(objects != null) {
				setSpecialBytesToCurrentDisplayedObject();
				String extension = FileManager.getExtension(opened[0].getName());
				if(extension.equalsIgnoreCase("gwm") || extension.equalsIgnoreCase("gwo")) {
					int saveIndex;
					if(extension.equalsIgnoreCase("gwo")) {
						saveIndex = 0;
					} else {
						saveIndex = 1;
					}
					try {
						saveToGWO(opened[saveIndex]);
					} catch (Exception e) {
						System.err.println("Fehler aufgetreten: " + e.getMessage());
						e.printStackTrace();
					}
					break;
				}
			}
			
		case CONVERT:
			if (objects != null) {
				setSpecialBytesToCurrentDisplayedObject();
				saveToFile();
			}
			break;

		case PREVIOUS_OBJECT:
			if (objects != null) {
				displayObject(false, null);
			}
			break;

		case NEXT_OBJECT:
			if (objects != null) {
				displayObject(true, null);
			}
			break;
			
		case CHECK:
			setSpecialBytesToCurrentDisplayedObject();
			checkFiles();
			break;
			
		case SETTINGS:
			showSettings();
			break;
			
		case OPEN_C_P_EDITOR:
			new ClassProperties(this, fileManager).setVisible(true);
			break;
			
		case "Wavefront sichern":
			fileManager.writeWavefrontFile(new File("C:/Users/manuel/Desktop/test.obj"), new File("C:/Users/manuel/Desktop/test.mtl"), materials, objects, this, false);
			break;
		}
	}
	
	/**
	 * Erstellt und zeigt das Einstellungsfenster.
	 */
	private void showSettings() {
		JDialog settings = new JDialog(this, "Einstellungen", true);
		Container contentPane = settings.getContentPane();
		contentPane.setLayout(new GridLayout(2, 1));
		JPanel classPath = new JPanel();
		classPath.setLayout(new BorderLayout());
		classPath.add(new JLabel("Pfad zu den Klassen: "), BorderLayout.WEST);
		String location = fileManager.getClassFilesLocation();
		JLabel ppp = new JLabel(location);
		ppp.setFont(ppp.getFont().deriveFont(Font.BOLD));
		JButton choose = new JButton("Ändern...");
		classPath.add(ppp, BorderLayout.CENTER);
		choose.addActionListener(event -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(location));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle("Ordner mit Klassen auswälen");
			chooser.setFileHidingEnabled(true);
			if(chooser.showOpenDialog(settings) == JFileChooser.APPROVE_OPTION) {
				File selected = chooser.getSelectedFile();
				String llocation = selected.getAbsolutePath();
				ppp.setText(llocation);
				settings.pack();
			}
		});
		JPanel sbyPath = new JPanel();
		sbyPath.setLayout(new BorderLayout());
		sbyPath.add(new JLabel("Pfad zu den SBY-Dateien: "), BorderLayout.WEST);
		String sbyLocation = fileManager.getClassSBYLocation();
		JLabel sss = new JLabel(sbyLocation);
		sss.setFont(sss.getFont().deriveFont(Font.BOLD));
		JButton chooseSby = new JButton("Ändern...");
		sbyPath.add(sss, BorderLayout.CENTER);
		chooseSby.addActionListener(event -> {
			JFileChooser chooser = getChooser(null);
			chooser.setCurrentDirectory(new File(sbyLocation));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle("Ordner mit SBY-Dateien auswählen");
			if(chooser.showOpenDialog(settings) == JFileChooser.APPROVE_OPTION) {
				File selected = chooser.getSelectedFile();
				String sbyLlocation = selected.getAbsolutePath();
				sss.setText(sbyLlocation);
				settings.pack();
			}
		});
		settings.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				fileManager.saveClassFilesLocation(ppp.getText());
				fillClassList(klasse, fileManager, null);
			}
		});
		classPath.add(choose, BorderLayout.EAST);
		classPath.setBorder(new EtchedBorder());
		sbyPath.setBorder(new EtchedBorder());
		contentPane.add(classPath);
		sbyPath.add(chooseSby, BorderLayout.EAST);
		contentPane.add(sbyPath);
		settings.pack();
		settings.setLocationRelativeTo(this);
		settings.setVisible(true);
	}
	
	/**
	 * Fragt den Nutzer nach zu öffnenden Welten, um sie mit der bereits geöffneten zu vergleichen.
	 */
	private void checkFiles() {
		if(objects != null && objects.length > 0) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileHidingEnabled(true);
			FileNameExtensionFilter wavefront = new FileNameExtensionFilter("Wavefront (*.obj, *.mtl)", "obj", "mtl");
			chooser.addChoosableFileFilter(wavefront);
			FileNameExtensionFilter gwo = new FileNameExtensionFilter("GWO-Welten (*.gwo, *.gwm)", "gwo", "gwm");
			chooser.setFileFilter(gwo);
			chooser.setMultiSelectionEnabled(true);
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File[] toOpen = chooser.getSelectedFiles();
				FileFilter selected = chooser.getFileFilter();
				String ext = FileManager.getExtension(toOpen[0].getName());
				if(selected.equals(gwo)) {
					openGWO(toOpen[0], true);
				} else if(selected.equals(wavefront)) {
					openWavefront(toOpen, true);
				} else if(ext.equalsIgnoreCase("obj") || ext.equalsIgnoreCase("mtl")) {
					openWavefront(toOpen, true);
				} else if(ext.equalsIgnoreCase("gwo")/* || ext.equalsIgnoreCase("gwm")*/) {
					openGWO(toOpen[0], true);
				} else {
					JOptionPane.showMessageDialog(this, "Unbekanntes Dateiformat!\nWavefront oder GWO verwenden!",
							"Dateiformat", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "3D-Welt öffnen, bevor sie mit einer anderen verglichen werden kann!",
					"Keine 3D-Welt geöffnet", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Füllt die KomboBox zur Auswahl der 3D-Objekte mit den geöffneten 3D-Objekten.
	 */
	private void fillObjectGUIList() {
		String[] listData = new String[objects.length];
		for(int i = 0; i < objects.length; i++) {
			//objName.addItem(objects[i].getName());
			listData[i] = objects[i].getName();
		}
		objNames.setListData(listData);
	}
	
	/**
	 * Speichert die für das aktuell angezeigte 3D-Objekt ausgewählte Klasse.
	 */
	private void saveClass() {
		if(objectCount < 0) {
			return;
		}
		String klass = (String) klasse.getSelectedItem();
		// Kümmert sich um das propPanel
		classPropPanel.removeAll();
		ClassPropertyNormalGUI temp = classPanels.get(klass);
		if(temp != null) {
			classPropPanel.add(temp.getComponent());
			temp.setValues(null);
		}
		getContentPane().validate();
		repaint();
		objects[objectCount].setKlasse(klass);
	}
	
	/**
	 * Zeigt die Sondereinstellungsmöglichkeiten für das gerade angezeigte 3D-Objekt an.
	 */
	private void setSpecialBytesToCurrentDisplayedObject() {
		ClassPropertyNormalGUI temp = classPanels.get(objects[objectCount].getKlasse());
		if(temp != null) {
			objects[objectCount].setSpecialValues(temp.getValues());
		}
	}
	
	/**
	 * Zeigt das angeforderte 3D-Objekt im Hauptfenster an. Sollte kein spezifisches
	 * Objekt per Name angefordert werden, wird entweder das nächste oder das
	 * vorhergehende 3D-Objekt angezeigt.
	 * 
	 * @param next ob das nächste ({@code true}) oder vorhergehende 3D-Objekt angezeigt werden soll
	 * @param specific ob und wenn ja welches 3D-Objekt angezeigt werden soll
	 */
	private void displayObject(boolean next, String specific) {
		if(objectCount > -1) {
			setSpecialBytesToCurrentDisplayedObject();
		}
		if(specific == null) {
			if (next) {
				objectCount++;
				if (objectCount == objects.length) {
					objectCount = 0;
				}
			} else {
				objectCount--;
				if (objectCount < 0) {
					objectCount = objects.length - 1;
				}
			}
		} else {
			objectCount = objNames.getSelectedIndex();
		}
		String objClass = objects[objectCount].getKlasse();
		currentObjName.setText(objects[objectCount].getName());
		if (objClass != null) {
			klasse.setSelectedItem(objClass);
			currentObjName.setForeground(Color.BLACK);
			// kümmert sich um die Werte
			ClassPropertyNormalGUI temp = classPanels.get(objClass);
			if(temp != null) {
				temp.setValues(objects[objectCount].getSBYValues());
			}
		} else {
			klasse.setSelectedItem(null);
			currentObjName.setForeground(Color.RED);
		}
	}

	/**
	 * Sichert die geöffnete Datei als GWO, auch wenn sie eigentlich im Wavefront-Format ist.
	 * Sollte die Datei, als die der Nutzer die geöffnete Welt sichern möchte, bereits existieren,
	 * wird er gefragt, ob sie überschreiben werden soll. Die Datei wird allerdings nicht mit
	 * dieser Methode geschrieben, siehe dazu {@link #saveToGWO(File)}.
	 * 
	 * @see #saveToGWO(File)
	 */
	private void saveToFile() {
		for(GraphicObject o : objects) {
			if(o.getKlasse() == null) {
				JOptionPane.showMessageDialog(this, "Bitte allen 3D-Objekten Grafikklasse zuweisen!", 
						"Fehlende Konfiguration", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setFileHidingEnabled(true);
		chooser.setFileFilter(new FileNameExtensionFilter("3D-Welten (*.gwo)",
				"gwo"));
		switch (chooser.showSaveDialog(this)) {
		case JFileChooser.APPROVE_OPTION:
			File toOpen = chooser.getSelectedFile();
			if (!FileManager.getExtension(toOpen.getName()).equalsIgnoreCase(
					"gwo")) {
				toOpen = new File(toOpen.getAbsolutePath() + ".gwo");
			}
			if(toOpen.exists()) {
				int option = JOptionPane.showConfirmDialog(this, "Die ausgewählte Datei existiert bereits.\nMöchten Sie sie überschreiben?",
						"Datei überschreiben", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				switch(option) {
				case JOptionPane.CANCEL_OPTION:
				case JOptionPane.CLOSED_OPTION:
					return;
				}
			}
			try {
				saveToGWO(toOpen);
			} catch (Exception e) {
				System.err.println("Fehler aufgetreten: " + e.getMessage());
				e.printStackTrace();
				System.err.println("-------------------------------------");
				return;
			}
		}
	}

	/**
	 * Schreibt die Datei im GWO-Format.
	 * 
	 * @param file die Datei, in die geschrieben werden soll
	 */
	private void saveToGWO(File file) {
		doing.setVisible(true);
		doing.setText("Datei schreiben: " + file.getName());
		fileManager.writeGWOFile(file, materials, objects, this, false);
		doing.setVisible(false);
	}
	
	/**
	 * Öffnet die angegebene GWO-Datei und vergleicht sie mit der bereits geöffneten Welt
	 * auf Anforderung.
	 * 
	 * @param gwoFile die zu öffnende Datei
	 * @param vergleichen ob die Datei mit der geöffneten Welt verglichen werden soll
	 */
	private void openGWO(File gwoFile, boolean vergleichen) {
		opened[0] = gwoFile;
		fileManager.cacheGWOFile(gwoFile);
		GraphicMaterial[] tempM = fileManager.getCachedMaterials();
		GraphicObject[] tempO = fileManager.getCachedObjects();
		fileManager.clearCache();
		if(!vergleichen) {
			matName.setVisible(false);
			matName.setText("Materialsdatei: -");
			fileName.setText("Objektdatei: " + gwoFile.getPath());
			fileName.setToolTipText(gwoFile.getName());
		}
		pack();
		if(!vergleichen) {
			materials = tempM;
			objects = tempO;
			fillObjectGUIList();
			setTitle(fileName.getToolTipText() + " - Game IDE");
		} else {
			if(vergleichen(tempM, tempO)) {
				JOptionPane.showMessageDialog(this, "Die beiden Welten sind identisch!",
						"Dateivergleich", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Zwei unterschiedliche Welten geöffnet!",
						"Dateivergleich", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Öffnet die angegebenen Dateien, diese müssen eine Objektdatei und eine Materialsdatei
	 * im Wavefront-Format sein. Vergleicht diese auf Anforderung mit der bereits geöffneten
	 * Welt.
	 * 
	 * @param toOpen die zwei zu öffnenden Dateien
	 * @param vergleichen ob die Dateien mit der bereits geöffneten Welt verglichen werden sollen
	 */
	private void openWavefront(File[] toOpen, boolean vergleichen) {
		if (toOpen.length != 2) {
			return;
		}
		opened = toOpen;
		doing.setVisible(true);
		doing.showWorkInProgress("Wavefront-Dateien öffnen: " + toOpen[0].getName() + ", " + toOpen[1].getName());
		TextFileOpener mtlOpener;
		TextFileOpener objOpener;
		if (FileManager.getExtension(toOpen[0].getName()).equalsIgnoreCase(
				"mtl")) {
			mtlOpener = new TextFileOpener(toOpen[0]);
			objOpener = new TextFileOpener(toOpen[1]);
			if(!vergleichen) {
				matName.setText("Materialsdatei: " + toOpen[0].getPath());
				fileName.setText("Objektdatei: " + toOpen[1].getPath());
				fileName.setToolTipText(toOpen[1].getName());
			}
		} else {
			mtlOpener = new TextFileOpener(toOpen[1]);
			objOpener = new TextFileOpener(toOpen[0]);
			if(!vergleichen) {
				matName.setText("Materialsdatei: " + toOpen[1].getPath());
				fileName.setText("Objektdatei: " + toOpen[0].getPath());
				fileName.setToolTipText(toOpen[0].getName());
			}
		}
		pack();
		mtlOpener.execute();
		objOpener.execute();
		try {
			HashMap<String, GraphicMaterial> mtls = fileManager
					.parseMTLFile(mtlOpener.get());
			Collection<GraphicMaterial> mmm = mtls.values();
			GraphicMaterial[] tempM = mmm.toArray(new GraphicMaterial[mmm.size()]);
			GraphicObject[] tempO = fileManager.parseOBJFile(objOpener.get(), mtls);
			if(!vergleichen) {
				materials = tempM;
				objects = tempO;
				fillObjectGUIList();
				setTitle(fileName.getToolTipText() + " - Game IDE");
			} else {
				if(vergleichen(tempM, tempO)) {
					JOptionPane.showMessageDialog(this, "Die beiden Welten sind identisch!",
							"Dateivergleich", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, "Zwei unterschiedliche Welten geöffnet!",
							"Dateivergleich", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			System.err.println("Fehler aufgetreten: " + e.getMessage());
			e.printStackTrace();
			System.err.println("-------------------------------------");
			doing.showError("Fehler aufgetreten!", true);
			return;
		}
		doing.setVisible(false);
	}
	
	/**
	 * Vergleicht die angegebenen Materials und die angegebenen 3D-Objekte mit den
	 * bereits geladenen. Gibt zurück, ob alles an beiden Welten gleich ist oder nicht.
	 * 
	 * @param tcMaterials die zu vergleichenden Materials
	 * @param tcObjects die zu vergleichenden 3D-Objekte
	 * @return ob die Welten gleich sind oder nicht
	 */
	private boolean vergleichen(GraphicMaterial[] tcMaterials, GraphicObject[] tcObjects) {
		if(tcMaterials.length != materials.length) {
			return false;
		}
		if(tcObjects.length != objects.length) {
			return false;
		}
		boolean found;
		for(GraphicMaterial m : tcMaterials) {
			found = false;
			for(GraphicMaterial om : materials) {
				if(!found) {
					found = om.equals(m);
				} else {
					break;
				}
			}
			if(!found) {
				return false;
			}
		}
		for(int i = 0; i < objects.length; i++) {
			if(!objects[i].equals(tcObjects[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Generiert einen Chooser mit den angegebene Filtern. Der erste Filter
	 * wird direkt gesetzt.
	 * 
	 * @param filters die Dateifilter
	 * @return einen unsichtbaren JFileChooser
	 */
	static JFileChooser getChooser(FileFilter[] filters) {
		JFileChooser toReturn = new JFileChooser();
		toReturn.setFileHidingEnabled(true);
		if(filters != null && filters.length > 0) {
			toReturn.setFileFilter(filters[0]);
			if(filters.length > 1) {
				for(int i = 1; i < filters.length; i++) {
					toReturn.addChoosableFileFilter(filters[i]);
				}
			}
		}
		return toReturn;
	}

	/**
	 * Fragt den Nutzern nach zu öffnenden Dateien.
	 */
	private void openChooser() {
		FileFilter[] filters = new FileFilter[] {
				new FileNameExtensionFilter("GWO-Welten (*.gwo, *.gwm)", "gwo", "gwm"),
				new FileNameExtensionFilter("Wavefront (*.obj, *.mtl)", "obj", "mtl")
		};
		JFileChooser chooser = getChooser(filters);
		chooser.setMultiSelectionEnabled(true);
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File[] toOpen = chooser.getSelectedFiles();
			FileFilter selected = chooser.getFileFilter();
			String ext = FileManager.getExtension(toOpen[0].getName());
			if(selected.equals(filters[0])) {
				openGWO(toOpen[0], false);
			} else if(selected.equals(filters[1])) {
				openWavefront(toOpen, false);
			} else if(ext.equalsIgnoreCase("gwo")/* || ext.equalsIgnoreCase("gwm")*/) {
				openGWO(toOpen[0], false);
			} else if(ext.equalsIgnoreCase("obj") || ext.equalsIgnoreCase("mtl")) {
				openWavefront(toOpen, false);
			} else {
				JOptionPane.showMessageDialog(this, "Unbekanntes Dateiformat!\nWavefront oder GWO verwenden!",
						"Dateiformat", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}