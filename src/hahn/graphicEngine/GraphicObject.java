package hahn.graphicEngine;

import hahn.mainIDE.Convertable;
import hahn.mainIDE.gui.ClassPropertyValue;
import hahn.utils.ByteHelper;

import java.util.ArrayList;

/**
 * Ein 3D-Objekt.
 * 
 * @author Manuel Hahn
 */
public class GraphicObject implements Convertable {
	/**
	 * Die 3D-Punkte dieses 3D-Objekts.
	 */
	private Vertex3D[] vertices;
	/**
	 * Die Indizes zur Verknüpfung der Materials und den Vertexen.
	 */
	private int[] vertexMaterialsIndex;
	/**
	 * Die Punkte auf der Textur.
	 */
	private Vertex2D[] texturePoints;
	/**
	 * Die Normalen. Sie sind eigentlich keine Punkte, sie werden nur so gespeichert.
	 */
	private Vertex3D[] normals;
	/**
	 * Der Name dieses Objektes.
	 */
	private String name;
	/**
	 * Die Klasse, die für dieses Objekt beim Rendern verwendet werden soll.
	 */
	private String klasse;
	/**
	 * Die SpecialBytes für die zusatändige Klasse.
	 */
	private ClassPropertyValue[] sby;
	
	/**
	 * Erzeugt ein 3D-Objekt mit den angegebenen Werten.
	 * 
	 * @param vertices die 3D-Punkte dieses 3D-Objektes
	 * @param matIndxs
	 * @param texturePoints die Texturpunkte
	 * @param normals die Normalen, hier als 3D-Punkte gespeichert
	 */
	public GraphicObject(Vertex3D[] vertices, int[] matIndxs, Vertex2D[] texturePoints, Vertex3D[] normals) {
		NullPointerException exception = new NullPointerException("Einer der übergebenen Werte ist null!");
		if(vertices == null) {
			throw exception;
		}
		if(matIndxs == null) {
			throw exception;
		}
		if(texturePoints == null) {
			throw exception;
		}
		if(normals == null) {
			throw exception;
		}
		exception = null;
		this.vertices = vertices;
		vertexMaterialsIndex = matIndxs;
		this.texturePoints = texturePoints;
		this.normals = normals;
	}
	
	/**
	 * Erzeugt das 3D-Objekt aus den GWO-bytes.
	 * 
	 * @param gwoBytes die bytes aus dem proprietären Format
	 */
	public GraphicObject(byte[] gwoBytes) {
		int bytePos = 0;
		int vmCount = ByteHelper.bytesToInt(ByteHelper.subBytes(gwoBytes, bytePos, (bytePos += Integer.BYTES)));
		vertices = new Vertex3D[vmCount];
		vertexMaterialsIndex = new int[vmCount];
		for(int i = 0; i < vmCount; i++) {
			vertices[i] = new Vertex3D(ByteHelper.subBytes(gwoBytes, bytePos, (bytePos += Vertex3D.BYTES)));
			vertexMaterialsIndex[i] = ByteHelper.bytesToInt(ByteHelper.subBytes(gwoBytes, bytePos, (bytePos += Integer.BYTES)));
		}
		int texPCount = ByteHelper.bytesToInt(ByteHelper.subBytes(gwoBytes, bytePos, (bytePos += Integer.BYTES)));
		texturePoints = new Vertex2D[texPCount];
		for(int i = 0; i < texPCount; i++) {
			texturePoints[i] = new Vertex2D(ByteHelper.subBytes(gwoBytes, bytePos, (bytePos += Vertex2D.BYTES)));
		}
		int norCount = ByteHelper.bytesToInt(ByteHelper.subBytes(gwoBytes, bytePos, (bytePos += Integer.BYTES)));
		normals = new Vertex3D[norCount];
		for(int i = 0; i < norCount; i++) {
			normals[i] = new Vertex3D(ByteHelper.subBytes(gwoBytes, bytePos, (bytePos += Vertex3D.BYTES)));
		}
		// Der Rest sind die SBYs... woher soll die IDE nur wissen, welche genau? Anhand der Länge!
		int sbyCount = ByteHelper.bytesToInt(ByteHelper.subBytes(gwoBytes, bytePos, bytePos += Integer.BYTES));
		sby = new ClassPropertyValue[sbyCount];
		int length;
		for(int i = 0; i < sbyCount; i++) {
			length = ByteHelper.bytesToInt(ByteHelper.subBytes(gwoBytes, bytePos, bytePos += Integer.BYTES));
			sby[i] = new ClassPropertyValue(ByteHelper.subBytes(gwoBytes, bytePos, bytePos += length));
		}
		/*int stringLength = bh.bytesToInt(bh.subBytes(gwoBytes, bytePos, (bytePos += Integer.BYTES)));
		name = new String(bh.subBytes(gwoBytes, bytePos, (bytePos += stringLength)));
		stringLength = bh.bytesToInt(bh.subBytes(gwoBytes, bytePos, (bytePos += Integer.BYTES)));
		klasse = new String(bh.subBytes(gwoBytes, bytePos, (bytePos += stringLength)));*/
		/*int vmCount = bh.bytesToInt(bh.subBytes(bytes, 0, 4));
		vertices = new HashMap<>();
		int bytePos = 4;
		byte[] vms = bh.subBytes(bytes, bytePos, bytePos += (vmCount * (Vertex3D.BYTES + Integer.BYTES)));
		GraphicMaterial[] ms = MainWindow.getMaterials();
		int bp = 0;
		for(int i = 0; i < vmCount; i++) {
			Vertex3D vertex = new Vertex3D(bh.subBytes(vms, bp, bp += Vertex3D.BYTES));
			int matNo = bh.bytesToInt(bh.subBytes(vms, bp, bp += Integer.BYTES));
			GraphicMaterial toPut = null;
			if(matNo != -1) {
				toPut = ms[matNo];
			}
			vertices.put(vertex, toPut);
		}
		int texPCount = bh.bytesToInt(bh.subBytes(bytes, bytePos, bytePos += Integer.BYTES));
		texturePoints = new Vertex2D[texPCount];
		for(int i = 0; i < texPCount; i++) {
			texturePoints[i] = new Vertex2D(bh.subBytes(bytes, bytePos, bytePos += Vertex2D.BYTES));
		}
		int norCount = bh.bytesToInt(bh.subBytes(bytes, bytePos, bytePos += Integer.BYTES));
		normals = new Vertex3D[norCount];
		for(int i = 0; i < norCount; i++) {
			normals[i] = new Vertex3D(bh.subBytes(bytes, bytePos, bytePos += Vertex3D.BYTES));
		}
		int nextStringLength = bh.bytesToInt(bh.subBytes(bytes, bytePos, bytePos += Integer.BYTES));
		name = new String(bh.subBytes(bytes, bytePos, bytePos += nextStringLength));
		nextStringLength = bh.bytesToInt(bh.subBytes(bytes, bytePos, bytePos += Integer.BYTES));
		klasse = new String(bh.subBytes(bytes, bytePos, bytePos += nextStringLength));*/
	}

	/**
	 * Gibt die Indexe der Materialen und der Vertexe zurück. Die Liste ist so lang wie die 
	 * der Vertexe, für den ersten Vertex wird das Material mit der ersten Nummer in dieser 
	 * Liste verwendet.
	 * 
	 * @return die Indexe für die Vertexe und die Materials
	 */
	public int[] getVertexMaterialIndices() {
		return vertexMaterialsIndex;
	}
	
	/**
	 * Versucht die Größe des Objektes in bytes zu erraten.
	 * 
	 * @return die vermutete Größe des Objektes
	 */
	public int sizeEstimate() {
		int byteCount = vertices.length * Vertex3D.BYTES;
		byteCount += vertexMaterialsIndex.length * Integer.BYTES;
		byteCount += texturePoints.length * Vertex2D.BYTES;
		byteCount += normals.length * Vertex3D.BYTES;
		byteCount += name.length();
		byteCount += klasse.length();
		return byteCount;
	}
	
	/**
	 * Gibt die Vertexe dieses Objektes zurück.
	 * 
	 * @return die Vertexe
	 */
	public Vertex3D[] getVertices() {
		return vertices;
	}
	
	/**
	 * Gibt die Texturpunkte der Punkte zurück.
	 * 
	 * @return die Texturpunkte
	 */
	public Vertex2D[] getTexturePoints() {
		return texturePoints;
	}
	
	/**
	 * Gibt die Normalen dieses Objektes zurück.
	 * 
	 * @return die Normalen
	 */
	public Vertex3D[] getNormals() {
		return normals;
	}
	
	/**
	 * Setzt den Namen des Objektes.
	 * 
	 * @param name der zukünftige Name des Objektes
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt den Namen des Objektes zurück.
	 * 
	 * @return den Namen des Objektes
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Setzt die Klasse, die später zum Rendern des Objektes verwendet werden soll.
	 * 
	 * @param klasse die Klasse zum Rendern
	 */
	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}
	
	/**
	 * Gibt die zugeordnete Klasse zurück.
	 * 
	 * @return der Name der zugeordneten Klasse
	 */
	public String getKlasse() {
		return klasse;
	}
	
	/**
	 * Setzt die Spezialwerte, die abhängig von der jeweiligen Klasse sind.
	 * 
	 * @param values die Werte
	 */
	public void setSpecialValues(ClassPropertyValue[] values) {
		sby = values;
	}
	
	/**
	 * Gibt die zugeordneten SBY-Werte zurück.
	 * 
	 * @return die Spezialwerte der zugeordnten Klasse
	 */
	public ClassPropertyValue[] getSBYValues() {
		return sby;
	}
	
	/**
	 * Gibt die Daten dieses Objektes als Text im Wavefrontformat zurück. Die Daten sind
	 * zeilenweise.
	 * 
	 * @return die Daten dieses Objektes
	 */
	public String[] convertToWavefront() {
		int texLength = 0;
		if(texturePoints != null) {
			texLength = texturePoints.length;
		}
		String[] toReturn = new String[vertices.length + texLength + (normals.length / 3)];
		int index = 0;
		toReturn[index] = "o " + name;
		for(Vertex3D v3D : vertices) {
			toReturn[++index] = "v " + v3D.toStringMachine();
		}
		if(texLength != 0) {
			for(Vertex2D tp : texturePoints) {
				toReturn[++index] = "vt " + tp.toStringMachine();
			}
		}
		
		return toReturn;
	}
	
	@Override
	public byte[] convertToBytes() {
		ArrayList<byte[]> list = new ArrayList<>();
		int length = 0;
		/*
		 * Format:
		 * anzahl vertexe
		 * vertex, graphicmaterialnummer
		 * x, y, z, gnr
		 * ...
		 * anzahl texturepoints
		 * vertex2d
		 * x, y
		 * ...
		 * anzahl normals
		 * nromale
		 * x, y, z
		 * ...
		 * länge name
		 * name
		 * länge klasse
		 * klasse
		 */
		list.add(ByteHelper.intToBytes(vertices.length));
		length += Integer.BYTES;
		for(int i = 0; i < vertices.length; i++) {
			list.add(vertices[i].convertToBytes());
			list.add(ByteHelper.intToBytes(vertexMaterialsIndex[i]));
			length += Vertex3D.BYTES;
			length += Integer.BYTES;
		}
		list.add(ByteHelper.intToBytes(texturePoints.length));
		length += Integer.BYTES;
		for(Vertex2D tp : texturePoints) {
			list.add(tp.convertToBytes());
			length += Vertex2D.BYTES;
		}
		list.add(ByteHelper.intToBytes(normals.length));
		length += Integer.BYTES;
		for(Vertex3D normale : normals) {
			list.add(normale.convertToBytes());
			length += Vertex3D.BYTES;
		}
		byte[][] sbyValues = convertSBYValues();
		for(byte[] bts : sbyValues) {
			list.add(bts);
			length += bts.length;
		}
		/*byte[] string = name.getBytes();
		list.add(bh.intToBytes(string.length));
		length += Integer.BYTES;
		list.add(string);
		length += string.length;
		string = klasse.getBytes();
		list.add(bh.intToBytes(string.length));
		length += Integer.BYTES;
		list.add(string);
		length += string.length;*/
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
		/*int verticesSize = vertices.size();
		bytes.add(bh.intToBytes(verticesSize));
		Vertex3D[] vs = vertices.keySet().toArray(new Vertex3D[verticesSize]);
		GraphicMaterial[] material = vertices.values().toArray(new GraphicMaterial[verticesSize]);
		for(int i = 0; i < vs.length; i++) {
			bytes.add(vs[i].convertToBytes());
			int matNo = -1;
			if(material[i] != null) {
				matNo = material[i].getNumber();
			}
			bytes.add(bh.intToBytes(matNo));
		}
		bytes.add(bh.intToBytes(texturePoints.length));
		for(Vertex2D v : texturePoints) {
			bytes.add(v.convertToBytes());
		}
		bytes.add(bh.intToBytes(normals.length));
		for(Vertex3D n : normals) {
			bytes.add(n.convertToBytes());
		}
		byte[] name = this.name.getBytes();
		bytes.add(bh.intToBytes(name.length));
		bytes.add(name);
		byte[] klasse = this.klasse.getBytes();
		bytes.add(bh.intToBytes(klasse.length));
		bytes.add(klasse);
		return bh.castArrayListToBytes(bytes);*/
	}
	
	/**
	 * Konvertiert die Spezialwerte der zugeordneten Klasse zu einem zweidimensionalen 
	 * byte-Array.
	 * 
	 * @return Rohbytes der Spezialwerte
	 */
	private byte[][] convertSBYValues() {
		final int sbyLength = 2 * sby.length;
		byte[][] toReturn = new byte[1 + sbyLength][];
		toReturn[0] = ByteHelper.intToBytes(sby.length);
		for(int i = 0, tri = 1; i < sby.length; i++, tri++) {
			byte[] s = sby[i].convertToBytes();
			toReturn[tri] = ByteHelper.intToBytes(s.length);
			toReturn[++tri] = s;
		}
		return toReturn;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			if(obj instanceof GraphicObject) {
				GraphicObject object = (GraphicObject) obj;
				if(object.klasse != null && klasse != null) {
					if(!object.klasse.equals(klasse)) {
						return false;
					}
				} else if((object.klasse == null && klasse != null) 
						|| (object.klasse != null && klasse == null)){
					return false;
				}
					
				if(!object.name.equals(name)) {
					return false;
				}
				if(normals.length != object.normals.length) {
					return false;
				}
				for(int i = 0; i < normals.length; i++) {
					if(!object.normals[i].equals(normals[i])) {
						return false;
					}
				}
				
				if(texturePoints.length != object.texturePoints.length) {
					return false;
				}
				for(int i = 0; i < texturePoints.length; i++) {
					if(!object.texturePoints[i].equals(texturePoints[i])) {
						return false;
					}
				}
				
				if(vertices.length != object.vertices.length) {
					return false;
				}
				if(vertexMaterialsIndex.length != object.vertexMaterialsIndex.length) {
					return false;
				}
				for(int i = 0; i < vertices.length; i++) {
					if(!object.vertices[i].equals(vertices[i])) {
						return false;
					}
					if(object.vertexMaterialsIndex[i] != vertexMaterialsIndex[i]) {
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
	
	@Override
	public String toString() {
		return "Name: " + name + "; Class: " + klasse;
	}
}