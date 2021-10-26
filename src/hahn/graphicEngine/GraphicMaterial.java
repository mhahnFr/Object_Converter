package hahn.graphicEngine;

import java.util.ArrayList;

import hahn.mainIDE.Convertable;
import hahn.utils.ByteHelper;

/**
 * Diese Klasse repräsentiert ein bestimmtes Material.
 *
 * Created by Manuel Hahn on 21.07.2016.
 * @author Manuel Hahn
 * @since 21.07.2016
 */
public class GraphicMaterial implements Convertable {
	/**
	 * Die Farbe des Materials.
	 */
    private Color color;
    /**
     * Die ambiente Farbe des Materials.
     */
    private Color ambientColor;
    /**
     * Die diffuse Farbe des Materials.
     */
    private Color diffuseColor;
    /**
     * Die aufblitzende Farbe.
     */
    private Color specularColor;
    /**
     * Die transparente Farbe.
     */
    private Color transparencyColor;
    /**
     * Wie stark das Material "scheint".
     */
    private float shine;
    /**
     * Wie transparent das Material ist.
     */
    private float transparency;
    /**
     * Die Textur, die diffus auf dieses Material gelegt wird.
     */
    private GraphicTexture diffuseMap;
    /**
     * Ob das Material glänzt.
     */
    private boolean specularHighlights;
    private boolean refraction;
    /**
     * Die Nummer dieses Materials.
     */
    private int number;
    /**
     * Der Name dieses Materials.
     */
    // FIXME Muss auch in GWO!
    private final String name;
    
    // TODO Bilder als Bitmaps in GWM speichern!

    /**
     * Erzeugt ein Material für Shapes.
     *
     * @param name der Name des Materials
     */
    public GraphicMaterial(String name) {
    	this.name = name;
    }
    
    /**
     * Erzeugt das Material aus den angegebenen GWM-bytes.
     * 
     * @param gwmBytes
     */
    public GraphicMaterial(byte[] gwmBytes) {
    	// Die ersten vier bytes sind die Länge des Namens
    	int bIndex = 0;
    	int lengthNextString = ByteHelper.bytesToInt(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Integer.BYTES)));
    	name = new String(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += lengthNextString)));
    	lengthNextString = ByteHelper.bytesToInt(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Integer.BYTES)));
    	String map = new String(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += lengthNextString)));
    	if(map.equals("Not available")) {
    		diffuseMap = null;
    	} else {
    		diffuseMap = new GraphicTexture(map);
    	}
    	color = bytesToColor(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Color.BYTES)));
    	ambientColor = bytesToColor(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Color.BYTES)));
    	diffuseColor = bytesToColor(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Color.BYTES)));
    	specularColor = bytesToColor(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Color.BYTES)));
    	transparencyColor = bytesToColor(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Color.BYTES)));
    	shine = byteToFloat(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Float.BYTES)));
    	transparency = byteToFloat(ByteHelper.subBytes(gwmBytes, bIndex, (bIndex += Float.BYTES)));
    	specularHighlights = gwmBytes[gwmBytes.length - 2] == 1;
    	refraction = gwmBytes[gwmBytes.length - 1] == 1;
    }
    
    /**
     * Gibt den Namen dieses Materials zurück.
     * 
     * @return
     */
    public String getName() {
		return name;
	}
    
    /**
     * Gibt die Farbe, falls gesetzt, zurück.
     *
     * @return die Farbe
     */
    public Color getColor() {
        return color;
    }

    /**
     * Setzt die Farbe. Änderungen sind allerdings manchmal nicht wirksam!
     *
     * @param color die neue Farbe
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Gibt die Farbe des Ambient-lighting zurück.
     *
     * @return die Ambient-lighting Farbe
     */
    public Color getAmbientColor() {
        return ambientColor;
    }

    /**
     * Gibt die Diffuse-lighting Farbe zurück.
     *
     * @return die Diffuse-lighting Farbe
     */
    public Color getDiffuseColor() {
        return diffuseColor;
    }

    /**
     * Gibt die Specular-lighting Farbe zurück.
     *
     * @return die Specular-lighting Farbe
     */
    public Color getSpecularColor() {
        return specularColor;
    }

    /**
     * Setzt die Farbe für die Ambient-lighting.
     *
     * @param ambientColor die Farbe
     */
    public void setAmbientColor(Color ambientColor) {
        this.ambientColor = ambientColor;
    }

    /**
     * Setzt die Farbe für das Diffuse-lighting.
     *
     * @param diffuseColor die Farbe
     */
    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    /**
     * Setzt die Farbe für das Specular-highlighting.
     *
     * @param specularColor die Farbe
     */
    public void setSpecularColor(Color specularColor) {
        this.specularColor = specularColor;
    }

    /**
     * Gibt die transparente Farbe zurück.
     *
     * @return die Farbe
     */
    public Color getTransparencyColor() {
        return transparencyColor;
    }

    /**
     * Setzt die transparente Farbe.
     *
     * @param transparencyColor die Farbe
     */
    public void setTransparencyColor(Color transparencyColor) {
        this.transparencyColor = transparencyColor;
    }

    /**
     * Gibt die Stärke des Glanzes zurück.
     *
     * @return die Stärke des Glanzes
     */
    public float getShine() {
        return shine;
    }

    /**
     * Setzt die Stärke des Glanzes. Der angegebene Wert muss zwischen 0 und 1000 liegen (einschließlich).
     *
     * @param shine die Stärke des Glanzes
     * @throws IllegalArgumentException sollte shine kleiner als 0 oder größer als 1000 sein
     */
    public void setShine(float shine) {
        if((shine < 0) || (shine > 1000)) {
            throw new IllegalArgumentException("The value of shine must be in the range of 0 and 1000!");
        }
        this.shine = shine;
    }

    /**
     * Gibt zurück, ob Specular-Highlights vorhanden sind oder nicht.
     *
     * @return ob Specular-Highlights vorhanden sind
     */
    public boolean hasSpecularHighlights() {
        return specularHighlights;
    }

    /**
     * Gibt die Map mit der Texture zurück.
     *
     * @return die Map
     */
    public GraphicTexture getDiffuseMap() {
        return diffuseMap;
    }

    /**
     * Setzt die Texture für das Diffuse-Lighting-Map
     *
     * @param diffuseMap die Texture
     */
    public void setDiffuseMap(GraphicTexture diffuseMap) {
        this.diffuseMap = diffuseMap;
    }

    /**
     * Setzt, ob Specular-Highlights gerendert werden sollen oder nicht.
     *
     * @param specularHighlights ob Specular-Highlights gerendert werden
     */
    public void setHasSpecularHighlights(boolean specularHighlights) {
        this.specularHighlights = specularHighlights;
    }

    /**
     * Gibt die Stärke der Transparenz zurück. Eine 1 bedeutet nicht transparent.
     *
     * @return die Stärke der Transparenz, zwischen 0 und 1
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Setzt die Stärke der Transparenz. Muss zwischen 0 und 1 sein.
     *
     * @param transparency die Deckkraft
     * @throws IllegalArgumentException sollte der angegebene Wert nicht zwischen 0 und 1 liegen
     */
    public void setTransparency(float transparency) {
        if((transparency < 0) || (transparency > 1)) {
            throw new IllegalArgumentException("transparency must be in the range of 0 to +1!");
        }
        this.transparency = transparency;
    }

    /**
     * Setzt, ob es eine Brechung gibt oder nicht.
     *
     * @param refraction Brechung oder nicht
     */
    public void setHasRefraction(boolean refraction) {
        this.refraction = refraction;
    }

    /**
     * Gibt zurück, ob gebrochen wird oder nicht.
     *
     * @return Brechung oder nicht
     */
    public boolean hasRefraction() {
        return refraction;
    }

    /**
     * Gibt die Nummer des Materials zurück.
     * 
     * @return die Nummer
     */
    public int getNumber() {
		return number;
	}
    
    /**
     * Setzt die Nummer des Materials.
     * 
     * @param number die Nummer
     */
    public void setNumber(int number) {
		this.number = number;
	}
    
	@Override
	public byte[] convertToBytes() {
		ArrayList<byte[]> list = new ArrayList<>();
		int length = 0;
		byte[] nameb = name.getBytes();
		list.add(ByteHelper.intToBytes(nameb.length));
		length += Integer.BYTES;
		list.add(nameb);
		length += nameb.length;
		byte[] map;
		if(diffuseMap == null) {
			byte[] string = "Not available".getBytes();
			byte[] lengths = ByteHelper.intToBytes(string.length);
			map = new byte[string.length + lengths.length];
			for(int i = 0; i < lengths.length; i++) {
				map[i] = lengths[i];
			}
			for(int i = 0; i < string.length; i++) {
				map[i + lengths.length] = string[i];
			}
		} else {
			map = diffuseMap.convertToBytes();
		}
		list.add(map);
		length += map.length;
		list.add(colorToBytes(color));
		length += 16;
		list.add(colorToBytes(ambientColor));
		length += 16;
		list.add(colorToBytes(diffuseColor));
		length += 16;
		list.add(colorToBytes(specularColor));
		length += 16;
		list.add(colorToBytes(transparencyColor));
		length += 16;
		list.add(floatToByte(shine));
		length += 4;
		list.add(floatToByte(transparency));
		length += 4;
		list.add(new byte[] {
				(byte) (specularHighlights ? 1 : 0),
				(byte) (refraction ? 1 : 0)
		});
		length += 2;
		byte[] toReturn = new byte[length];
		byte[][] all = list.toArray(new byte[list.size()][]);
		int byteCount = 0;
		for(byte[] bts : all) {
			for(byte b : bts) {
				toReturn[byteCount] = b;
				byteCount++;
			}
		}
		return toReturn;
	}
	
	/**
	 * Kleine Hilfsmethode, die floats zu einem byte-Array konvertiert.
	 * 
	 * @param value der zu konvertierende float
	 * @return das Array mit den bytes
	 */
	private byte[] floatToByte(float value) {
		return ByteHelper.intToBytes(Float.floatToIntBits(value));
	}
	
	/**
	 * Konvertiert die angegebene Farbe in ein byte-Array.
	 * 
	 * @param color die zu konvertierende Farbe
	 * @return das Array mit den bytes
	 */
	private byte[] colorToBytes(Color color) {
		if(color == null) {
			return "nullnullnullnull".getBytes();
		}
		byte[] r = floatToByte(color.getR());
		byte[] g = floatToByte(color.getG());
		byte[] b = floatToByte(color.getB());
		byte[] a = floatToByte(color.getA());
		return new byte[] {
			r[0], r[1], r[2], r[3],
			g[0], g[1], g[2], g[3],
			b[0], b[1], b[2], b[3],
			a[0], a[1], a[2], a[3],
		};
	}
	
	/**
	 * Hilfsmethode, die ein Array von bytes in einen float umwandelt.
	 * 
	 * @param bytes das byte-Array
	 * @return der float au den bytes
	 */
	private float byteToFloat(byte[] bytes) {
		return Float.intBitsToFloat(ByteHelper.bytesToInt(bytes));
	}
	
	/**
	 * Liest aus den angegebenen bytes die entsprechende Farbe heraus.
	 * 
	 * @param bytes das byte-Array
	 * @return die Farbe
	 */
	private Color bytesToColor(byte[] bytes) {
		if(new String(bytes).equalsIgnoreCase("nullnullnullnull")) {
			// Dann existierte keine solche Farbe
			return null;
		}
		float r = byteToFloat(ByteHelper.subBytes(bytes, 0, 4));
		float g = byteToFloat(ByteHelper.subBytes(bytes, 4, 8));
		float b = byteToFloat(ByteHelper.subBytes(bytes, 8, 12));
		float a = byteToFloat(ByteHelper.subBytes(bytes, 12, bytes.length));
		return new Color(r, g, b, a);
	}

	public String toString() {
		return "Name: " + name + ", No: " + number;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			if(obj instanceof GraphicMaterial) {
				GraphicMaterial object = (GraphicMaterial) obj;
				if(object.refraction != refraction) {
					return false;
				}
				if(object.specularHighlights != specularHighlights) {
					return false;
				}
				boolean noTh = diffuseMap == null;
				boolean noEq = object.diffuseMap == null;
				if(noTh != noEq) {
					return false;
				}
				if(!noTh) {
					if(!object.diffuseMap.getFileName().equals(diffuseMap.getFileName())) {
						return false;
					}
				}
				if(object.transparency != transparency) {
					return false;
				}
				if(object.shine != shine) {
					return false;
				}
				
				boolean colNul = transparencyColor == null;
				boolean objColNul = object.transparencyColor == null;
				if(colNul != objColNul) {
					return false;
				}
				if(!colNul) {
					if(object.transparencyColor.getA() != transparencyColor.getA()) {
						return false;
					}
					if(object.transparencyColor.getB() != transparencyColor.getB()) {
						return false;
					}
					if(object.transparencyColor.getR() != transparencyColor.getR()) {
						return false;
					}
					if(object.transparencyColor.getG() != transparencyColor.getG()) {
						return false;
					}
				}
				
				colNul = specularColor == null;
				objColNul = object.specularColor == null;
				if(colNul != objColNul) {
					return false;
				}
				if(!colNul) {
					if(object.specularColor.getA() != specularColor.getA()) {
						return false;
					}
					if(object.specularColor.getR() != specularColor.getR()) {
						return false;
					}
					if(object.specularColor.getG() != specularColor.getG()) {
						return false;
					}
					if(object.specularColor.getB() != specularColor.getB()) {
						return false;
					}
				}
				
				colNul = diffuseColor == null;
				objColNul = object.diffuseColor == null;
				if(colNul != objColNul) {
					return false;
				}
				if(!colNul) {
					if(object.diffuseColor.getA() != diffuseColor.getA()) {
						return false;
					}
					if(object.diffuseColor.getR() != diffuseColor.getR()) {
						return false;
					}
					if(object.diffuseColor.getG() != diffuseColor.getG()) {
						return false;
					}
					if(object.diffuseColor.getB() != diffuseColor.getB()) {
						return false;
					}
				}
				
				colNul = ambientColor == null;
				objColNul = object.ambientColor == null;
				if(colNul != objColNul) {
					return false;
				}
				if(!colNul) {
					if(object.ambientColor.getA() != ambientColor.getA()) {
						return false;
					}
					if(object.ambientColor.getR() != ambientColor.getR()) {
						return false;
					}
					if(object.ambientColor.getG() != ambientColor.getG()) {
						return false;
					}
					if(object.ambientColor.getB() != ambientColor.getB()) {
						return false;
					}
				}
				
				colNul = color == null;
				objColNul = object.color == null;
				if(colNul != objColNul) {
					return false;
				}
				if(!colNul) {
					if(object.color.getA() != color.getA()) {
						return false;
					}
					if(object.color.getR() != color.getR()) {
						return false;
					}
					if(object.color.getG() != color.getG()) {
						return false;
					}
					if(object.color.getB() != color.getB()) {
						return false;
					}
				}
				
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
}