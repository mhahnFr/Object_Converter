package hahn.mainIDE;

import hahn.graphicEngine.Color;
import hahn.graphicEngine.GraphicMaterial;
import hahn.graphicEngine.GraphicObject;
import hahn.graphicEngine.GraphicTexture;
import hahn.graphicEngine.Vertex2D;
import hahn.graphicEngine.Vertex3D;
import hahn.mainIDE.gui.ClassPropertyConverter;
import hahn.mainIDE.gui.ClassPropertyGUI;
import hahn.mainIDE.gui.ClassPropertyNGUI;
import hahn.utils.ByteHelper;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Ein Objekt dieser Klasse kümmert sich um alle Dateitypen, die dieses Programm 
 * öffnen kann (Wavefront, GraphicWorldObjects, SpecialBYtes). Außerdem kennt es 
 * die Einstellungen.
 * 
 * @author Manuel Hahn
 */
public class FileManager {
	/**
	 * Der Schüssel für den Speicherort der *.java oder *.class Dateien.
	 */
	private static final String CLASSFILES_LOCATION_KEY = "GAME_EDITOR_CLASSES";
	/**
	 * Der Schlüssel für den Speicherort der SBY-Dateien.
	 */
	private static final String CLASS_PROPERTIES_LOCATION_KEY = "GAME_EDITOR_SBY";
	/**
	 * Die Liste mit den bereits eingelesenen Texturepunkten.
	 */
	private ArrayList<Vertex2D> vt;
	/**
	 * Eine Liste mit den bereits eingelesenen Normalen.
	 */
	private ArrayList<Vertex3D> vn;
	/**
	 * Eine Liste mit den bereits eingelesenen Verticen.
	 */
	private ArrayList<Vertex3D> v;
	/**
	 * Eine {@link HashMap} mit einem {@link Vertex3D Vertex} und dem zugehörigen 
	 * {@link GraphicMaterial}.
	 */
	private HashMap<Vertex3D, GraphicMaterial> vg;
	/**
	 * Eine {@link HashMap Liste} mit den bereits eingelesenen Materials und dessen 
	 * Namen.
	 */
	private HashMap<String, GraphicMaterial> ms;
	/**
	 * Das zuletzt verwendete {@link GraphicMaterial Material}.
	 */
	private int materialCount = -1;
	/**
	 * Die zuletzt eingelesene Klasse.
	 */
	private String lastKlasse;
	/**
	 * Der Zugriff auf die Einstellungen.
	 */
	private Preferences p;
	/**
	 * Der Cache der zuletzt eingelesenen {@link GraphicMaterial}s.
	 */
	private GraphicMaterial[] cMaterials;
	/**
	 * Der Cache der zuletzt eingelesenen {@link GraphicObject}s.
	 */
	private GraphicObject[] cObjects;
	/**
	 * Das {@link Icon}, das angezeigt wird, wenn im Hintergrund irgendwelche Aktivitäten laufen.
	 */
	private Icon loader;
	/**
	 * Das {@link Icon}, das angezeigt wird, wenn eine klickbare Information angezeigt wird.
	 */
	private Icon info;
	/**
	 * Das {@link Icon}, das angezeigt wird, wenn irgendein Fehler passiert ist.
	 */
	private Icon error;
	
	/**
	 * Erzeugt ein Objekt. Besorgt sich Zugriff auf die evtl. vorhandenen Einstellungen.
	 */
	public FileManager() {
    	p = Preferences.userRoot();
	}

	/**
	 * Interpretiert die Zeilen einer Wavefront-Materialsdatei (*.mtl). Gibt eine Liste
	 * mit dem Namen in Kombination mit dem dazugehörenden {@link GraphicMaterial}.
	 * 
	 * @param lines die Zeilen der Materialsdatei
	 * @return eine Liste mit den Namen und zugehörenden {@link GraphicMaterial}s
	 */
	public HashMap<String, GraphicMaterial> parseMTLFile(String[] lines) {
		materialCount = -1;
		HashMap<String, GraphicMaterial> toReturn = new HashMap<String, GraphicMaterial>();
		ArrayList<String> material = new ArrayList<>();
		String name = null;
		for(String line : lines) {
			if((line.startsWith("#")) || (line.equals(""))) {
				System.out.println("Unused line: Empty line or starts with '#'");
				System.out.println("Line is: " + line);
			} else if(!line.startsWith("newmtl")) {
				material.add(line);
			} else {
				if(!material.isEmpty()) {
					toReturn.put(name, getMaterial(material.toArray(new String[material.size()]), name));
				}
				name = line.substring(6).trim();
				material.clear();
				material.add(line);
			}
		}
		toReturn.put(name, getMaterial(material.toArray(new String[material.size()]), name));
		finalize();
		return toReturn;
	}
	
	protected void finalize() {
		vt = null;
		vn = null;
		v = null;
		vg = null;
		ms = null;
		cMaterials = null;
		cObjects = null;
	}
	
	/**
	 * Gibt ein {@link GraphicMaterial} zurück, das aus den angegebenen Zeilen interpretiert 
	 * werden kann. Der Name muss separat angegeben werden.
	 * 
	 * @param lines die Zeilen, aus welchen das {@link GraphicMaterial} generiert werden soll
	 * @param materialName der Name des Materials
	 * @return das aus den Zeilen interpretierbare {@link GraphicMaterial}
	 */
	private GraphicMaterial getMaterial(String[] lines, String materialName) {
		materialCount++;
		GraphicMaterial toReturn = new GraphicMaterial(materialName);
		toReturn.setNumber(materialCount);
		for(String line : lines) {
			String start = line, trimmed;
			try {
				start = line.substring(0, start.indexOf(' ')).trim();
			} catch(Exception e) {
				start = "Exception";
			}
			switch(start) {
			case "Ns":
				trimmed = line.substring(2).trim();
				toReturn.setShine(Float.parseFloat(trimmed));
				break;
			case "Ka":
			case "Ks":
			case "Kd":
				trimmed = line.substring(2).trim();
				float r = Float.parseFloat(trimmed.substring(0, trimmed.indexOf(" ")).trim());
				float g = Float.parseFloat(trimmed.substring(trimmed.indexOf(" ") + 1, trimmed.lastIndexOf(" ")).trim());
				float b = Float.parseFloat(trimmed.substring(trimmed.lastIndexOf(" ")).trim());
                Color kdkaksColor = new Color(r, g, b);
                switch(start) {
                    case "Ka" :
                        toReturn.setAmbientColor(kdkaksColor);
                        break;
                    case "Kd" :
                        toReturn.setDiffuseColor(kdkaksColor);
                        break;
                    case "Ks" :
                        toReturn.setSpecularColor(kdkaksColor);
                        break;
                }
                break;
            case "d" :
            case "Tr" :
                trimmed = line.substring(2).trim();
                toReturn.setTransparency(Float.parseFloat(trimmed));
                break;
            case "illum" :
                trimmed = line.substring(5).trim();
                toReturn.setHasSpecularHighlights((Integer.parseInt(trimmed) == 2));
                break;
            case "Ni" :
                trimmed = line.substring(2).trim();
                toReturn.setHasRefraction((Float.parseFloat(trimmed) != 1));
                break;
            case "map_Kd" :
                trimmed = line.substring(6).trim();
                toReturn.setDiffuseMap(new GraphicTexture(trimmed.substring(trimmed.lastIndexOf("/") + 1)));
                break;
                // TODO map_Ks und map_Ka implementieren!
			}
		}
		return toReturn;
	}
	
	/**
	 * Liest die angegebene Datei ein und gibt die Textzeilen zurück. Jedes byte der Datei wird
	 * als {@link String} interpretiert.
	 * 
	 * @param toOpen die Datei, die eingelesen werden soll
	 * @return ein Array mit den gelesenen Zeilen
	 */
	public String[] openTextFile(File toOpen) {
		ArrayList<String> lines = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(toOpen.toPath())) {
			while(reader.ready()) {
				lines.add(reader.readLine());
			}
		} catch(FileNotFoundException e) {
			System.err.println("Datei existiert nicht!");
		} catch(IOException e) {
			System.err.println("Fehler aufgetreten: " + e.getMessage());
			e.printStackTrace();
		}
		return lines.toArray(new String[lines.size()]);
	}	
	
	/**
	 * Gibt die Endung einer Datei ohne Punkt zurück.
	 * Beispiel: {@code getExtension("example.shad")} gibt {@code shad} zurück.
	 * 
	 * @param filename der Dateiname
	 * @return die Endung der Datei ohne Punkt
	 */
	public static String getExtension(String filename) {
		return filename.substring(filename.lastIndexOf('.') + 1);
	}

	/**
	 * Liest die angegenen Zeilen ein und gibt die daraus interpretierbaren {@link GraphicObject Objekte} 
	 * zurück.
	 * 
	 * @param lines die Zeilen, aus welchen die {@link GraphicObject Objekte} interpretiert werden sollen
	 * @param materials die {@link GraphicMaterial Material}s, die möglicherweise mit den Objekten 
	 * 					verknüpft sein könnten
	 * @return ein Array mit den aus den angegebenen Zeilen interpretierten {@link GraphicObject}s
	 */
	public GraphicObject[] parseOBJFile(String[] lines,
			HashMap<String, GraphicMaterial> materials) {
		ms = materials;
		vg = new HashMap<Vertex3D, GraphicMaterial>();
		vt = new ArrayList<>();
		vn = new ArrayList<>();
		v = new ArrayList<>();
		ArrayList<GraphicObject> toReturn = new ArrayList<>();
		ArrayList<String> object = new ArrayList<>();
		String name = null;
		for(String line : lines) {
			if((line.startsWith("#")) || (line.equals("")) || (line.startsWith("mtllib"))) {
				System.out.println("Unused line: Empty line or starts with '#' or 'mtllib'");
				System.out.println("Line is: " + line);
			} else if(!line.startsWith("o")) {
				object.add(line);
			} else {
				if(!object.isEmpty()) {
					GraphicObject toAdd = getGraphicObject(object.toArray(new String[object.size()]));
					toAdd.setName(name);
					toReturn.add(toAdd);
				}
				name = line.substring(1).trim();
				object.clear();
				object.add(line);
			}
		}
		GraphicObject toAdd = getGraphicObject(object.toArray(new String[object.size()]));
		toAdd.setName(name);
		toReturn.add(toAdd);
		finalize();
		return toReturn.toArray(new GraphicObject[toReturn.size()]);
	}

	/**
	 * Gibt ein aus den angegebenen Zeilen interpretierbares {@link GraphicObject} zurück.
	 * 
	 * @param array die Zeilen, aus welchen das {@link GraphicObject} interpretiert werden soll
	 * @return das aus den angegebenen Zeilen interpretierbare {@link GraphicObject}
	 */
	private GraphicObject getGraphicObject(String[] array) {
        ArrayList<Vertex3D[]> f = new ArrayList<>();
        ArrayList<Vertex2D[]> fts = new ArrayList<>();
        ArrayList<Vertex3D[]> fns = new ArrayList<>();
        GraphicMaterial usemtl = null;
        for(String line : array) {
            String start = line, trimmed;
            start = line.substring(0, start.indexOf(" ")).trim();
            switch(start) {
                // Ein Vertex (3D)
                case "v" :
                    trimmed = line.substring(1).trim();
                    String x = trimmed.substring(0, trimmed.indexOf(" "));
                    String y = trimmed.substring(trimmed.indexOf(" ") + 1, trimmed.lastIndexOf(" "));
                    String z = trimmed.substring(trimmed.lastIndexOf(" "));
                    float xx = Float.parseFloat(x.trim());
                    float yy = Float.parseFloat(y.trim());
                    float zz = Float.parseFloat(z.trim());
                    Vertex3D add = new Vertex3D(xx, yy, zz);
                    vg.put(add, usemtl);
                    v.add(add);
                    break;
                // Ein Vertex (2D) der Texture
                case "vt" :
                    trimmed = line.substring(2).trim();
                    x = trimmed.substring(0, trimmed.lastIndexOf(" "));
                    y = trimmed.substring(trimmed.lastIndexOf(" "));
                    xx = Float.parseFloat(x.trim());
                    yy = Float.parseFloat(y.trim());
                    vt.add(new Vertex2D(xx, yy));
                    break;
                // Eine Normale, also ein Vertex (3D)
                case "vn" :
                    trimmed = line.substring(2).trim();
                    x = trimmed.substring(0, trimmed.indexOf(" "));
                    y = trimmed.substring(trimmed.indexOf(" ") + 1, trimmed.lastIndexOf(" "));
                    z = trimmed.substring(trimmed.lastIndexOf(" "));
                    xx = Float.parseFloat(x.trim());
                    yy = Float.parseFloat(y.trim());
                    zz = Float.parseFloat(z.trim());
                    vn.add(new Vertex3D(xx, yy, zz));
                    break;
                // Das ab sofort zu benutzende Material
                case "usemtl" :
                    trimmed = line.substring(6).trim();
                    usemtl = ms.get(trimmed);//world.getMaterial(trimmed);
                    break;
                // Smooth Shading nicht unterstützt
                // TODO smooth shading
                // Eine Fläche (Shape)
                // Unterstützt werden ausschließlich Dreiecke, jede weitere Ecke wird ignoriert,
                // die erste angegebene Normale wird für das gesamte Dreieck verwendet
                case "f" :
                    trimmed = line.substring(1).trim();
                    Vertex3D vertexes[] = new Vertex3D[3];
                    Vertex3D normal = null;
                    Vertex2D textureVxt[] = new Vertex2D[3];
                    String eck1 = trimmed.substring(0, trimmed.indexOf(" ")).trim();
                    String eck2 = trimmed.substring(trimmed.indexOf(" ") + 1, trimmed.lastIndexOf(" "));
                    String eck3 = trimmed.substring(trimmed.lastIndexOf(" "));
                    int v1, v2, v3;
                    if(eck1.contains("/")) {
                        v1 = Integer.parseInt(eck1.substring(0, eck1.indexOf("/")).trim());
                        v2 = Integer.parseInt(eck2.substring(0, eck2.indexOf("/")).trim());
                        v3 = Integer.parseInt(eck3.substring(0, eck3.indexOf("/")).trim());
                        if(eck1.indexOf("/") == eck1.lastIndexOf("/")) {
                            // Vertex-Nummer, Texture-Point
                            textureVxt[0] = vt.get(Integer.parseInt(eck1.substring(eck1.indexOf("/") + 1).trim()) - 1);
                            textureVxt[1] = vt.get(Integer.parseInt(eck2.substring(eck2.indexOf("/") + 1).trim()) - 1);
                            textureVxt[2] = vt.get(Integer.parseInt(eck3.substring(eck3.indexOf("/") + 1).trim()) - 1);
                        } else if(eck1.indexOf("/") + 1 == eck1.lastIndexOf("/")) {
                            // Vertex-Nummer, Normale
                            textureVxt = null;
                            normal = vn.get(Integer.parseInt(eck1.substring(eck1.lastIndexOf("/") + 1).trim()) - 1);
                        } else {
                            // Vertex-Nummer, Texture-Point, Normale
                            normal = vn.get(Integer.parseInt(eck1.substring(eck1.lastIndexOf("/") + 1).trim()) - 1);
                            textureVxt[0] = vt.get(Integer.parseInt(eck1.substring(eck1.indexOf("/") + 1, eck1.lastIndexOf("/")).trim()) - 1);
                            textureVxt[1] = vt.get(Integer.parseInt(eck2.substring(eck2.indexOf("/") + 1, eck2.lastIndexOf("/")).trim()) - 1);
                            textureVxt[2] = vt.get(Integer.parseInt(eck3.substring(eck3.indexOf("/") + 1, eck3.lastIndexOf("/")).trim()) - 1);
                        }
                    } else {
                        v1 = Integer.parseInt(eck1.trim());
                        v2 = Integer.parseInt(eck2.trim());
                        v3 = Integer.parseInt(eck3.trim());
                        textureVxt = null;
                    }
                    vertexes[0] = v.get(v1 - 1);
                    vertexes[1] = v.get(v2 - 1);
                    vertexes[2] = v.get(v3 - 1);
                    //f.add(new Shape(vertexes, usemtl, normal, null, null, textureVxt));
                    f.add(vertexes);
                    fts.add(textureVxt);
                    fns.add(new Vertex3D[] {
                            normal, normal, normal
                    });
                    break;
            }
        }
        Vertex3D[] mainVertices = convertArrayList3D(f);
        return new GraphicObject(mainVertices,
        		/*convertArrayListToHashMap(f)*/
        		getIntsForVertices(mainVertices), convertArrayList(fts), convertArrayList3D(fns));
	}
	
	/**
	 * Gibt eine {@link ArrayList Liste} mit SpecialBYtesProperties zur Bearbeitung 
	 * durch den Nutzer zurück.
	 * 
	 * @param sby die SBY-Datei, aus welcher die Informationen eingelesen werden sollen
	 * @return eine Liste mit bearbeitbaren SpecialBYtesProperties
	 */
	public ArrayList<ClassPropertyGUI> getSpecialBytesPropertiesGUI(File sby) {
		Object[] sbyData = getSBYData(sby);
		ArrayList<ClassPropertyGUI> toReturn = new ArrayList<>();
		byte[] file = (byte[]) sbyData[0];
		int byteCount = (int) sbyData[1];
		int[] lengths = (int[]) sbyData[2];
		final int count = (int) sbyData[3];
		for(int i = 0; i < count; i++) {
			toReturn.add(new ClassPropertyGUI(ByteHelper.subBytes(file, byteCount, byteCount += lengths[i])));
		}
		return toReturn;
	}
	
	/**
	 * Auslagerung von den Methoden {@link FileManager#getSpecialBytesForUser(File)} & 
	 * {@link FileManager#getSpecialBytesPropertiesGUI(File)}. Liest aus einer SBY-Datei 
	 * die Klasse heraus, sowie die Längen der einzelnen Properties. In dem Array, welches 
	 * zurückgegeben wird, befinden sich folgende Sachen: ein Array mit den Rohbytes der 
	 * angegebenen SBY-Datei, die Position in demselben, ein Array mit den Längen der 
	 * einzelnen Properties, die Anzahl der Properties.
	 * 
	 * @param sby die Datei, die geöffnet werden soll
	 * @param bh der ByteHelper, um nicht einen neuen erzeugen zu müssen
	 * @return das oben beschriebene Array
	 */
	private Object[] getSBYData(File sby) {
		final byte[] file = bufferFileBytes(sby);
		final int kl = ByteHelper.bytesToInt(file);
		int byteCount = Integer.BYTES;
		lastKlasse = new String(ByteHelper.subBytes(file, byteCount, byteCount += kl));
		final int count = ByteHelper.bytesToInt(ByteHelper.subBytes(file, byteCount, byteCount += Integer.BYTES));
		int[] lengths = new int[count];
		for(int i = 0; i < count; i++) {
			lengths[i] = ByteHelper.bytesToInt(ByteHelper.subBytes(file, byteCount, byteCount += Integer.BYTES));
		}
		return new Object[] {
				file,
				byteCount,
				lengths,
				count
		};
	}
	
	/**
	 * Gibt die aus der angegebenen SBY-Datei eingelesenen {@link ClassPropertyNGUI Properties} 
	 * zurück. Die können im Hauptfenster angeziegt werden.
	 * 
	 * @param sby die SBY-Datei, aus welcher die Properties eingelesen werden sollen
	 * @return alle eingelesenen Properties zur Anzeige im Hauptfenster
	 */
	public ClassPropertyNGUI[] getSpecialBytesForUser(File sby) {
		Object[] sbyData = getSBYData(sby);
		byte[] file = (byte[]) sbyData[0];
		int byteCount = (int) sbyData[1];
		int[] lengths = (int[]) sbyData[2];
		final int count = (int) sbyData[3];
		ClassPropertyNGUI[] toReturn = new ClassPropertyNGUI[count];
		for(int i = 0; i < count; i++) {
			toReturn[i] = ClassPropertyConverter.getClassProperty(ByteHelper.subBytes(file, byteCount, byteCount += lengths[i]));
		}
		return toReturn;
	}
	
	/**
	 * Schreibt eine Datei im SBY-Format. Die zu schreibenden Properties sind als {@link Convertable}s
	 * anzugeben. Die Klasse gibt an, zu welcher Klasse dieses Bündel an Properties gehört, 
	 * geschreiben wird in die angegebene Datei.
	 * 
	 * @param classProps die Properties als {@link Convertable}s
	 * @param toWrite die Datei, in welche die Properties geschrieben werden sollen
	 * @param klasse die Klasse, zu welcher diese Datei gehören soll
	 */
	public void writeSBYFile(Convertable[] classProps, File toWrite, String klasse) {
		// Header:
		// länge prop 1
		// länge prop 2
		// ...
		// Body:
		// prop 1
		// prop 2
		// ...
		int length = 0;
		byte[][] cpb = new byte[classProps.length][];
		for(int i = 0; i < classProps.length; i++) {
			cpb[i] = classProps[i].convertToBytes();
			length += cpb[i].length;
		}
		ArrayList<byte[]> bytes = new ArrayList<byte[]>();
		byte[] string = klasse.getBytes();
		bytes.add(ByteHelper.intToBytes(string.length));
		length += Integer.BYTES;
		bytes.add(string);
		length += string.length;
		bytes.add(ByteHelper.intToBytes(classProps.length));
		length += Integer.BYTES;
		for(byte[] bts : cpb) {
			bytes.add(ByteHelper.intToBytes(bts.length));
			length += Integer.BYTES;
		}
		for(byte[] bts : cpb) {
			bytes.add(bts);
		}
		byte[] toReturn = new byte[length];
		byte[][] all = bytes.toArray(new byte[bytes.size()][]);
		int byteCount = 0;
		for(byte[] bts : all) {
			for(byte b : bts) {
				toReturn[byteCount] = b;
				byteCount++;
			}
		}
		try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(toWrite))) {
			os.write(toReturn);
		} catch(FileNotFoundException e) {
			System.err.println("Unmöglicher Fehler!? Datei wurde nicht gefunden!");
		} catch(IOException e) {
			System.err.println("Fehler aufgetreten: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*private HashMap<Vertex3D, GraphicMaterial> convertArrayListToHashMap(ArrayList<Vertex3D[]> list) {
		Vertex3D[] vertices = convertArrayList3D(list);
		HashMap<Vertex3D, GraphicMaterial> toReturn = new HashMap<>();
		for(Vertex3D vertex : vertices) {
			toReturn.put(vertex, vg.get(vertex));
		}
		return toReturn;
	}*/
	
	/**
	 * Verknüpft die Vertexe mit den zugehörenden Materials.
	 * 
	 * @param list die Vertexe
	 * @return die Indexe der Materials
	 */
	private int[] getIntsForVertices(Vertex3D[] list) {
		int[] toReturn = new int[list.length];
		for(int i = 0; i < list.length; i++) {
			GraphicMaterial mat = vg.get(list[i]);
			toReturn[i] = mat == null ? -1 : mat.getNumber();
		}
		return toReturn;
	}
	
	/**
	 * Konvertiert eine {@link ArrayList} mit Arrays von {@link Vertex2D} in ein
	 * großes Array mit {@link Vertex2D}.
	 * 
	 * @param list die zu konvertierende Liste
	 * @return das große Array
	 */
    private Vertex2D[] convertArrayList(ArrayList<Vertex2D[]> list) {
        ArrayList<Vertex2D> returnList = new ArrayList<>();
        for(Vertex2D[] vertices : list) {
        	if(vertices == null) {
        		continue;
        	}
            for(Vertex2D vertex : vertices) {
                returnList.add(vertex);
            }
        }
        return returnList.toArray(new Vertex2D[returnList.size()]);
    }

    /**
     * @deprecated GWM-Dateien werden nicht mehr benötigt!
     * 
     * @param gwmFile die GWM-Datei
     * @return die Materials aus der GWM-Datei
     */
    @Deprecated
    public GraphicMaterial[] getMaterialsGWO(File gwmFile) {
    	byte[] file = bufferFileBytes(gwmFile);
    	int bytesPos = 3;
    	int materialCount = ByteHelper.bytesToInt(ByteHelper.subBytes(file, 0, ++bytesPos));
    	bytesPos += materialCount * Integer.BYTES;
    	MaterialGenerator[] mtlThreads = new MaterialGenerator[materialCount];
    	for(int i = 0; i < materialCount; i++) {
    		int index = 4 + i * Integer.BYTES;
    		int mtlByteLength = ByteHelper.bytesToInt(ByteHelper.subBytes(file, index, index + Integer.BYTES));
    		MaterialGenerator thread = new MaterialGenerator(ByteHelper.subBytes(file, bytesPos, bytesPos += mtlByteLength));
    		mtlThreads[i] = thread;
    		thread.execute();
    	}
    	GraphicMaterial[] toReturn = new GraphicMaterial[materialCount];
    	for(int i = 0; i < materialCount; i++) {
    		try {
				toReturn[i] = mtlThreads[i].get();
				toReturn[i].setNumber(i);
			} catch (Exception e) {
				System.err.println("Fehler aufgetreten: " + e.getMessage());
				e.printStackTrace();
			}
    	}
    	return toReturn;
    }
    
    /**
     * Speichert die Rohbytes der angegebenen Datei zwischen.
     * 
     * @param file die zwischenzuspeichernde Datei
     * @return die Rohbytes der Datei
     */
    private byte[] bufferFileBytes(File file) {
    	byte[] toReturn = new byte[(int) file.length()];
    	try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
    		in.read(toReturn);
    	} catch(FileNotFoundException e) {
    		System.err.println("Datei existiert nicht!");
    	} catch(IOException e) {
    		System.err.println("Fehler aufgetreten: " + e.getMessage());
    		e.printStackTrace();
    		System.err.println("-------------------------------------");
    	}
    	return toReturn;
    }
    
    /**
     * Öffnet einen Datenstrom aus der angegebenen Datei.
     * 
     * @param file die lesbar zu machende Datei
     * @return den Strom aus der Datei
     */
    private BufferedInputStream getFileStream(File file) {
    	try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("Fehler aufgetreten: " + e.getMessage());
			e.printStackTrace();
			System.err.println("-------------------------------------");
			return null;
		}
    }
    
    /**
     * Nicht mehr verwendbar, da der Header nicht korrekt eingelesen wird.
     * 
     * @deprecated Ersetzt durch {@link FileManager#getObjectsGWO2(File)}
     * 
     * @param gwoFile die einzulesende Datei
     * @return die eingelesen Objekte
     */
    @Deprecated
    public GraphicObject[] getObjectsGWO(File gwoFile) {
    	byte[] file = bufferFileBytes(gwoFile);
    	int bytesPos = Integer.BYTES;
    	int objectsCount = ByteHelper.bytesToInt(ByteHelper.subBytes(file, 0, bytesPos));
    	bytesPos += ByteHelper.bytesToInt(ByteHelper.subBytes(file, bytesPos, bytesPos += Integer.BYTES));
    	ObjectGenerator[] objThreads = new ObjectGenerator[objectsCount];
		int objByteLength, stringLength, index = Integer.BYTES * 2;
		String klasse, name;
    	for(int i = 0; i < objectsCount; i++) {
    		objByteLength = ByteHelper.bytesToInt(ByteHelper.subBytes(file, index, index += Integer.BYTES));
    		stringLength = ByteHelper.bytesToInt(ByteHelper.subBytes(file, index, index += Integer.BYTES));
    		name = new String(ByteHelper.subBytes(file, index, index += stringLength));
    		stringLength = ByteHelper.bytesToInt(ByteHelper.subBytes(file, index, index += Integer.BYTES));
    		klasse = new String(ByteHelper.subBytes(file, index, index += stringLength));
    		ObjectGenerator thread = new ObjectGenerator(ByteHelper.subBytes(file, bytesPos, bytesPos += objByteLength),
    				name, klasse);
    		objThreads[i] = thread;
    		thread.execute();
    	}
    	GraphicObject[] toReturn = new GraphicObject[objectsCount];
    	for(int i = 0; i < objectsCount; i++) {
    		try {
    			toReturn[i] = objThreads[i].get();
    		} catch(Exception e) {
    			System.err.println("Fehler aufgetreten: " + e.getMessage());
    			e.printStackTrace();
    			System.err.println("-------------------------------------");
    		}
    	}
    	return toReturn;
    }
    
    @Deprecated
    public GraphicObject[] getObjectsGWO2(File gwoFile) {
		ObjectGenerator[] objThreads = null;
		int streamLength = (int) gwoFile.length(), bytesPos = 0, objectsCount = 0;
    	try (BufferedInputStream in = getFileStream(gwoFile))
    	{
    		byte[] tempFill, tempInt = new byte[Integer.BYTES];
    		bytesPos += in.read(tempInt);
    		objectsCount = ByteHelper.bytesToInt(tempInt);
    		bytesPos += in.read(tempInt);
    		int headerLength = bytesPos + ByteHelper.bytesToInt(tempInt);
    		objThreads = new ObjectGenerator[objectsCount];
    		int objByteLength, stringLength, bodyIndex = 0;
    		String name, klasse;
    		for(int i = 0; i < objectsCount; i++)
    		{
    			bytesPos += in.read(tempInt);
    			objByteLength = ByteHelper.bytesToInt(tempInt);
    			bytesPos += in.read(tempInt);
    			stringLength = ByteHelper.bytesToInt(tempInt);
    			tempFill = new byte[stringLength];
    			bytesPos += in.read(tempFill);
    			name = new String(tempFill);
    			bytesPos += in.read(tempInt);
    			stringLength = ByteHelper.bytesToInt(tempInt);
    			tempFill = new byte[stringLength];
    			bytesPos += in.read(tempFill);
    			klasse = new String(tempFill);
    			in.mark(streamLength - bytesPos);
    			in.skip(headerLength + bodyIndex - bytesPos);
    			tempFill = new byte[objByteLength];
    			bodyIndex += in.read(tempFill);
    			ObjectGenerator thread = new ObjectGenerator(tempFill, name, klasse);
    			objThreads[i] = thread;
    			thread.execute();
    			in.reset();
    		}
    	}
    	catch(IOException e) {
    		System.err.println("Fehler aufgetreten: " + e.getMessage());
    		e.printStackTrace();
    		System.err.println("-------------------------------------");
    	}
    	GraphicObject[] toReturn = new GraphicObject[objectsCount];
    	for(int i = 0; i< objectsCount; i++) {
    		try {
    			toReturn[i] = objThreads[i].get();
    		} catch(Exception e) {
    			System.err.println("Fehler aufgetreten: " + e.getMessage());
    			e.printStackTrace();
    			System.err.println("-------------------------------------");
    		}
    	}
    	return toReturn;
    }
    
    /**
     * Öffnet die angegebene GWO-Datei und speichert deren Inhalt zwischen.
     * 
     * @param gwoFile die zu öffnende Datei
     */
    public void cacheGWOFile(File gwoFile) {
    	try (BufferedInputStream in = getFileStream(gwoFile))
    	{
    		final int mCount, oCount, headerLength;
    		int objByteLength, stringLength, bodyIndex = 0, bytesPos = 0;
    		final long streamLength = gwoFile.length();
    		byte[] nextBlock, nextInt = new byte[Integer.BYTES];
    		bytesPos += in.read(nextInt);
    		mCount = ByteHelper.bytesToInt(nextInt);
    		int[] lengths = new int[mCount];
    		MaterialGenerator[] mtlThreads = new MaterialGenerator[mCount];
    		for(int i = 0; i < mCount; i++)
    		{
    			bytesPos += in.read(nextInt);
    			lengths[i] = ByteHelper.bytesToInt(nextInt);
    		}
    		cMaterials = new GraphicMaterial[mCount];
    		for(int i = 0; i < mCount; i++)
    		{
    			nextBlock = new byte[lengths[i]];
    			bytesPos += in.read(nextBlock);
    			mtlThreads[i] = new MaterialGenerator(nextBlock);
    			mtlThreads[i].execute();
    		}
    		
    		bytesPos += in.read(nextInt);
    		oCount = ByteHelper.bytesToInt(nextInt);
    		bytesPos += in.read(nextInt);
    		headerLength = ByteHelper.bytesToInt(nextInt) + bytesPos;
    		ObjectGenerator[] objThreads = new ObjectGenerator[oCount];
    		String name, klasse;
    		for(int i = 0; i < oCount; i++)
    		{
    			bytesPos += in.read(nextInt);
    			objByteLength = ByteHelper.bytesToInt(nextInt);
    			bytesPos += in.read(nextInt);
    			stringLength = ByteHelper.bytesToInt(nextInt);
    			nextBlock = new byte[stringLength];
    			bytesPos += in.read(nextBlock);
    			name = new String(nextBlock);
    			bytesPos += in.read(nextInt);
    			stringLength = ByteHelper.bytesToInt(nextInt);
    			nextBlock = new byte[stringLength];
    			bytesPos += in.read(nextBlock);
    			klasse = new String(nextBlock);
    			in.mark((int) (streamLength - bytesPos));
    			in.skip(headerLength + bodyIndex - bytesPos);
    			nextBlock = new byte[objByteLength];
    			bodyIndex += in.read(nextBlock);
    			objThreads[i] = new ObjectGenerator(nextBlock, name, klasse);
    			objThreads[i].execute();
    			in.reset();
    		}
    		
    		for(int i = 0; i < mCount; i++) {
    			try {
    				cMaterials[i] = mtlThreads[i].get();
    				cMaterials[i].setNumber(i);
    			} catch(Exception e) {
    				System.err.println("Schleifendurchgang: " + i);
    				System.err.println("Fehler aufgetreten: " + e.getMessage());
    				e.printStackTrace();
    				System.err.println("-------------------------------------");
    			}
    		}
    		
    		cObjects = new GraphicObject[oCount];
    		for(int i = 0; i < oCount; i++) {
    			try {
    				cObjects[i] = objThreads[i].get();
    			} catch(Exception e) {
    				System.err.println("Schleifendurchgang: " + i);
    				System.err.println("Fehler aufgetreten: " + e.getMessage());
    				e.printStackTrace();
    				System.err.println("-------------------------------------");
    			}
    		}
    	}
    	catch(IOException e) {
    		System.err.println("Fehler aufgetreten: " + e.getMessage());
    		e.printStackTrace();
    		System.err.println("-------------------------------------");
    	}
    }
    
    /**
     * Leert den Cache.
     */
    public void clearCache() {
    	finalize();
    }
    
    /**
     * Gibt die zwischengespeicherten Materials zurück.
     * 
     * @return die Materials
     */
    public GraphicMaterial[] getCachedMaterials() {
    	return cMaterials;
    }
    
    /**
     * Gibt die zwischengespeicherten 3D-Objekte zurück.
     * 
     * @return die 3D-Objekte
     */
    public GraphicObject[] getCachedObjects() {
    	return cObjects;
    }
    
    /**
     * Startet für jedes der angegebenen 3D-Objekte einen Konverter als Thread und
     * gibt beides verknüpft zurück.
     * 
     * @param objects die 3D-Objekte, die konvertiert werden sollen
     * @return eine {@link HashMap} mit den 3D-Objekten und deren Konvertern
     */
    private HashMap<GraphicObject, ObjectConverter> startAsyncConverter(GraphicObject[] objects) {
    	HashMap<GraphicObject, ObjectConverter> toReturn = new HashMap<>();
		for (GraphicObject o : objects) {
			ObjectConverter oc = new ObjectConverter(o);
			toReturn.put(o, oc);
			oc.execute();
		}
		return toReturn;
    }
    
    /**
     * Diese Methode schreibt die angegebene 3D-Welt in die angegebene GWO-Datei. Für 
     * Fehlerbenachrichtigungen kann eine GUI-Komponente zur Assoziation angegeben werden.
     * 
     * @param file die Datei, in die die Welt geschrieben werden soll
     * @param materials die Materials der zu schreibenden 3D-Welt
     * @param objects die 3D-Objekte der zu schreibenden 3D-Welt
     * @param errorParent die GUI-Komponente zu der die Fehlermeldungen angezeigt werden sollen
     * @param redone ob die Methode im zweiten Anlauf ausgeführt wird
     */
    public void writeGWOFile(File file, GraphicMaterial[] materials, GraphicObject[] objects,
    		Component errorParent, boolean redone) {
		try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file)))
		{
			// ------- Die Materialdatei --------
			// Create Body
			HashMap<GraphicMaterial, ObjectConverter> matsw = new HashMap<>();
			for (GraphicMaterial m : materials) {
				ObjectConverter oc = new ObjectConverter(m);
				matsw.put(m, oc);
				oc.execute();
			}
			HashMap<GraphicObject, ObjectConverter> objsw;
			/*try {*/
			objsw = startAsyncConverter(objects);
			/*} catch(OutOfMemoryError e) {
				objsw = null;
				System.out.println("Nicht genug RAM frei für Parallelisierung.");
			}*/
			out.write(ByteHelper.intToBytes(materials.length), 0, Integer.BYTES);
			byte[][] buffer = new byte[materials.length][];
			for (int i = 0; i < buffer.length; i++) {
				buffer[i] = matsw.get(materials[i]).get();
			}
			matsw = null;
			// Create Header
			for (int i = 0; i < buffer.length; i++) {
				// Objektgröße des Materials im Body
				out.write(ByteHelper.intToBytes(buffer[i].length), 0, Integer.BYTES);
			}
			for (byte[] bts : buffer) {
				out.write(bts, 0, bts.length);
			}
			// ----------------------------------
			
			/*if(objsw == null) {
				objsw = startAsyncConverter(objects);
			}*/
			out.write(ByteHelper.intToBytes(objects.length), 0, Integer.BYTES);
			buffer = new byte[objects.length][];
			try {
				objsw.get(objects[objects.length - 1]).get();
			} catch(OutOfMemoryError e) {
				objsw = startAsyncConverter(objects);
			}
			// TODO Noch höher optimieren!!!
			for (int i = 0; i < objects.length; i++) {
				buffer[i] = objsw.get(objects[i]).get();
			}
			byte[][] hBuffer = new byte[5 * objects.length][];
			int length = 0;
			for(int i = 0, bI = -1; i < objects.length; i++) {
				byte[] classe = objects[i].getKlasse().getBytes();
				byte[] name = objects[i].getName().getBytes();
				hBuffer[++bI] = ByteHelper.intToBytes(buffer[i].length);
				length += Integer.BYTES;
				hBuffer[++bI] = ByteHelper.intToBytes(name.length);
				length += Integer.BYTES;
				hBuffer[++bI] = name;
				length += name.length;
				hBuffer[++bI] = ByteHelper.intToBytes(classe.length);
				length += Integer.BYTES;
				hBuffer[++bI] = classe;
				length += classe.length;
			}
			out.write(ByteHelper.intToBytes(length), 0, Integer.BYTES);
			for(int i = 0; i < hBuffer.length; i++) {
				out.write(hBuffer[i], 0, hBuffer[i].length);
			}
			for(byte[] bts : buffer) {
				out.write(bts, 0, bts.length);
			}
			out.flush();
		}
		catch (ExecutionException e) {
			System.err.println("Fehler im Thread aufgetreten: " + e.getMessage());
			e.printStackTrace();
			System.err.println("-------------------------------------");
		}
		catch(FileNotFoundException e) {
			System.err.println("Datei kann nicht gefunden werden!");
			if(!redone) {
				if(!file.exists()) {
					boolean success = false;
					try {
						success = file.createNewFile();
					} catch(IOException ioe) {
						System.err.println("Konnte Datei nicht erzeugen.");
					}
					if(!success) {
						if(!file.mkdirs()) {
							throw new IllegalArgumentException("Datei und/oder Pfad konnte(n) nicht erzeugt werden.");
						}
					}
					System.err.println("Datei erzeugt, erneut versuchen...");
					writeGWOFile(file, materials, objects, errorParent, true);
					return;
				}
			}
			e.printStackTrace();
			System.err.println("-------------------------------------");
		}
		catch (IOException e) {
			System.err.println("Fehler aufgetreten: " + e.getMessage());
			e.printStackTrace();
			System.err.println("-------------------------------------");
		}
		catch (InterruptedException e) {
			System.err.println("Thread abgebrochen!");
			e.printStackTrace();
			System.err.println("-------------------------------------");
			JOptionPane.showMessageDialog(errorParent, "Thread konnte nicht ausgeführt werden!",
					"Programmabbruch", JOptionPane.ERROR_MESSAGE);
		}
    }
    
    /**
     * Schreibt die angegebene 3D-Welt in eine Wavefront-Datei. Waveront besteht aus zwei Dateien:
     * Aus der Materials-Datei (*.mtl) und den Objektdaten (*.obj).
     * 
     * @param objFile die zu schreibende Objektdatei
     * @param mtlFile die zu schreibende Materialsdatei
     * @param materials die Materials der 3D-Welt
     * @param objects die 3D-Objekte der 3D-Welt
     * @param parentComponent eine GUI-Komponente als Parent für Fehlermeldungen
     * @param redone ob der Aufruf der Methode der zweite ist
     */
    public void writeWavefrontFile(File objFile, File mtlFile, GraphicMaterial[] materials,
    		GraphicObject[] objects, Component parentComponent, boolean redone) {
    	HashMap<String, GraphicMaterial> ms;
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(mtlFile))) {
    		ms = writeMTLFile(writer, materials);
    	} catch(IOException e) {
    		System.err.println("Konnte Materials nicht im Wavefrontformat schreiben:");
    		System.err.println("Fehler aufgetreten: " + e.getMessage());
    		e.printStackTrace();
    		System.err.println("-------------------------------------");
    	}
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(objFile))) {
    		final String nl = System.lineSeparator();
    		writer.write("# Written with ObjectConverter" + nl);
    		writer.write("# Object Count: " + objects.length + nl);
    		writer.write("mtllib " + mtlFile.getName() + nl);
    		int vCount = 1, vtCount = 1, vnCount = 1;
    		Vertex3D[] vertices, normals;
    		Vertex2D[] texPoints;
    		for(GraphicObject object : objects) {
    			writer.write("o " + object.getName() + nl);
    			vertices = object.getVertices();
    			normals = object.getNormals();
    			texPoints = object.getTexturePoints();
    			for(Vertex3D v3D : vertices) {
    				writer.write("v " + v3D.toStringMachine() + nl);
    				
    			}
    			boolean hasTexPoints = texPoints != null && texPoints.length > 0;
    			if(hasTexPoints) {
    				for(Vertex2D tp : texPoints) {
    					writer.write("vt " + tp.toStringMachine() + nl);
    				}
    			}
    			if(normals != null) {
    				for(Vertex3D normal : normals) {
    					writer.write("vn " + normal.toStringMachine() + nl);
    				}
    			}
    			// Die Flächen sind immer drei Vertexe, beginnend bei den ersten drei
    			GraphicMaterial last = null;
    			int[] mvi = object.getVertexMaterialIndices();
    			int genI = 0;
    			writer.write("s off" + nl);
    			for(int i = 0; i < vertices.length / 3; i++) {
    				if(mvi[genI] != -1) {
    					if(!materials[genI].equals(last)) {
    						writer.write("usemtl " + materials[genI].getName() + nl);
    					}
    				}
    				writer.write("f");
    				for(int ii = 0; ii < 3; genI++, ii++) {
    					writer.write(" " + (vCount + genI) + "/");
    					if(hasTexPoints) {
    						writer.write((vtCount + genI));
    					}
    					writer.write("/" + (vnCount + genI));
    				}
    				writer.newLine();
    				vCount += vertices.length;
    				if(hasTexPoints) {
    					vtCount += texPoints.length;
    				}
    				vnCount += normals.length;
    			}
    		}
    	} catch(IOException e) {
    		System.err.println("Konnte Objekte nicht im Wavefrontformat schreiben:");
    		System.err.println("Fehler aufgetreten: " + e.getMessage());
    		e.printStackTrace();
    		System.err.println("-------------------------------------");
    	}
    }
    
    /**
     * Schreibt die angegebenen {@link GraphicMaterial}s in den angegebenen Stream im Wavefront-Format.
     * Der Stream wird nicht geschlossen!
     * 
     * @param fileWriter der {@link BufferedWriter}, mit dem die Daten geschrieben werden sollen
     * @param materials die zu schreibenden Materials
     * @return eine {@link HashMap} mit den Namen und den Materials verknüpft
     * @throws IOException falls etwas unvorhergesehenes passiert
     */
    private HashMap<String, GraphicMaterial> writeMTLFile(BufferedWriter fileWriter, GraphicMaterial[] materials)
    		throws IOException {
    	// FIXME unfertig! Spezifikationen vollständig umsetzen!
    	final String nl = System.lineSeparator();
    	fileWriter.write("# Written with ObjectConverter" + nl);
    	fileWriter.write("# Material Count: " + materials.length + nl);
    	HashMap<String, GraphicMaterial> toReturn = new HashMap<>();
    	for(GraphicMaterial material : materials) {
    		fileWriter.write("newmtl " + material.getName() + nl);
    		fileWriter.write("Ns " + material.getShine() + nl);
    		fileWriter.write("Ka " + material.getAmbientColor() + nl);
    		fileWriter.write("Ks " + material.getSpecularColor() + nl);
    		fileWriter.write("Kd " + material.getDiffuseColor() + nl);
    		fileWriter.write("illum 2" + nl);
    		fileWriter.write("d " + material.getTransparency() + nl);
    		GraphicTexture t = material.getDiffuseMap();
    		if(t != null) {
    			fileWriter.write("map_Kd " + t.getFileName() + nl);
    		}
    		fileWriter.newLine();
    		toReturn.put(material.getName(), material);
    	}
    	return toReturn;
    }
    
    /**
     * Konvertiert eine {@link ArrayList} mit Arrays von {@link Vertex3D} zu einem großen
     * Array von {@ink Vertex3D}.
     * 
     * @param list die aufzulösende Liste
     * @return das neue Array
     */
     private Vertex3D[] convertArrayList3D(ArrayList<Vertex3D[]> list) {
        ArrayList<Vertex3D> returnList = new ArrayList<>();
        for(Vertex3D[] vertices : list) {
            for(Vertex3D vertex : vertices) {
                returnList.add(vertex);
            }
        }
        return returnList.toArray(new Vertex3D[returnList.size()]);
    }

    /**
     * Speichert den Pfad zu den Klassendateien.
     * 
     * @param path der Pfad zu den Klassendateien
     */
    public void saveClassFilesLocation(String path) {
    	if(path == null) {
    		throw new NullPointerException("No class-files-path given!");
    	}
    	if(path.equals("")) {
    		throw new IllegalArgumentException("Empty class-file-path!");
    	}
    	p.put(CLASSFILES_LOCATION_KEY, path);
    }

    /**
     * Gibt die zuletzt eingelesene Klasse zurück.
     * 
     * @return die zuletzt eingelesene Klasse
     */
    public String getLastKlasse() {
    	return lastKlasse;
    }
    
    /**
     * Speichert den Pfad zu den SpecialBytes-Dateien.
     * 
     * @param path der Pfad zu den SBY-Dateien
     */
    public void saveClassSBYLocation(String path) {
    	if(path == null) {
    		throw new NullPointerException("No class:sby-path given!");
    	}
    	if(path.equals("")) {
    		throw new IllegalArgumentException("Empty class:sby-path!");
    	}
    	p.put(CLASS_PROPERTIES_LOCATION_KEY, path);
    }
    
    /**
     * Gibt den Pfad zu den SpecialBytes-Dateien für die Klassen zurück, sofern dieser 
     * zuvor gespeichert wurde.
     * 
     * @return den Pfad zu den SBY-Dateien
     */
    public String getClassSBYLocation() {
    	return p.get(CLASS_PROPERTIES_LOCATION_KEY, null);
    }
    
    /**
     * Gibt den Pfad zu den Klassendateien zurück, sofern er zuvor gespeichert wurde.
     * 
     * @return den Pfad zu den Klassendateien
     */
    public String getClassFilesLocation() {
    	return p.get(CLASSFILES_LOCATION_KEY, null);
    }

    /**
     * Gibt das Icon zur Anzeige von Informationen zurück.
     * 
     * @return das passende Icon zu Informationen
     */
    public Icon getInfoIcon() {
    	if(info == null) {
    		info = UIManager.getIcon("OptionPane.informationIcon");
    	}
    	return info;
    }
    
    /**
     * Gibt das Icon zur Anzeige eines Fehlers zurück.
     * 
     * @return das Icon für Fehler
     */
    public Icon getErrorIcon() {
    	if(error == null) {
    		error = UIManager.getIcon("OptionPane.errorIcon");
    	}
    	return error;
    }
    
    /**
     * Gibt das Icon zurück, das zur Signalisierung des Ladens angezeigt werden soll.
     * 
     * @return das optimale Icon zum Laden
     */
    public Icon getLoaderIcon() {
    	if(loader == null) {
    		loader = new ImageIcon("Y:\\Documents\\Programmierung\\Lade-Gifs\\ajax_loader_gray_32.gif");
    	}
    	return loader;
    }
}