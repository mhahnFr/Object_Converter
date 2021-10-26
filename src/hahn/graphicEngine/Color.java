package hahn.graphicEngine;

/**
 * Diese Klasse repr�sentiert eine Farbe.
 * 
 * @author Manuel Hahn
 */
public class Color {
	/**
	 * Die Anzahl an bytes, die eine Farbe braucht.
	 */
	public static final int BYTES = 4 * Float.BYTES;
	/**
	 * Die floats f�r Red, Green, Blue, Alpha (Transparenz).
	 */
	private float r, g, b, a;
	
	/**
	 * Erzeugt diese Farbe mit den angegebenen Werten.
	 * 
	 * @param r Rot
	 * @param g Gr�n
	 * @param b Blau
	 * @param a Alpha (Transparenz)
	 */
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	/**
	 * Erzeugt eine Farbe mit den angegebenen Werten, hier nicht transparent.
	 * 
	 * @param r Rot
	 * @param g Gr�n
	 * @param b Blau
	 */
	public Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		a = 1;
	}
	
	/**
	 * Gibt den Alphawert (die Deckkraft) der Farbe zur�ck.
	 * 
	 * @return den Alphawert (die Transparenz)
	 */
	public float getA() {
		return a;
	}
	
	/**
	 * Gibt den Blauanteil der Farbe zur�ck.
	 * 
	 * @return den Blauanteil
	 */
	public float getB() {
		return b;
	}
	
	/**
	 * Gibt den Gr�nanteil der Farbe zur�ck.
	 * 
	 * @return den Gr�nanteil
	 */
	public float getG() {
		return g;
	}
	
	/**
	 * Gibt den Rotanteil der Farbe zur�ck.
	 * 
	 * @return der Rotanteil
	 */
	public float getR() {
		return r;
	}
	
	/**
	 * Setzt den Alphawert (Transparenz) der Farbe.
	 * 
	 * @param a der Alphawert
	 */
	public void setA(float a) {
		this.a = a;
	}
	
	/**
	 * Setzt den Blauanteil der Farbe.
	 * 
	 * @param b der Blauanteil
	 */
	public void setB(float b) {
		this.b = b;
	}
	
	/**
	 * Setzt den Gr�nanteil der Farbe.
	 * 
	 * @param g der Gr�nanteil
	 */
	public void setG(float g) {
		this.g = g;
	}
	
	/**
	 * Setzt den Rotanteil der Farbe.
	 * 
	 * @param r der Rotanteil
	 */
	public void setR(float r) {
		this.r = r;
	}
	
	public String toString() {
		return r + " " + g + " " + b + " " + a;
	}
	
	/**
	 * Gibt diese Farbe als String zur�ck, ohne Alphawert.
	 * Beispiel: 1 1 1, in der Form Rot, Gr�n, Blau.
	 * 
	 * @return die Farbe
	 */
	public String toStringNoAlpha() {
		return r + " " + g + " " + b;
	}
}