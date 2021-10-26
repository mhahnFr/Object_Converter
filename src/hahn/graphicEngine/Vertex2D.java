package hahn.graphicEngine;

import hahn.mainIDE.Convertable;
import hahn.utils.ByteHelper;

/**
 * Repräsentiert einen Punkt in einem zweidimensionalen Koordinatensystem. Die Berechnungen beruhen
 * auf den gewohnten mathematischen Formeln ({@code f(x) = mx + b}). Wichtig: ist ein Wert einmal
 * gesetzt bzw. ausgerechnet, kann er nicht mehr geändert werden!
 *
 * Created by Manuel Hahn.
 * @author Manuel Hahn
 * @since 26.07.2016
 */
public class Vertex2D implements Convertable {
    /**
     * Der Punkt auf der x-Achse. Lässt sich mit der Methode {@link #getX()} abfragen.
     * @see #xSet
     * @see #isXSet()
     * @see #addToX(float)
     * @see #calculateX()
     * @see #getX()
     * @see #setX(float)
     */
    protected float x;
    /**
     * Der Punkt auf der y-Achse. Lässt sich mit der Methode {@link #getY()} abfragen.
     * @see #ySet
     * @see #getY()
     * @see #calculateY()
     * @see #setY(float)
     * @see #addToY(float)
     * @see #isYSet()
     */
    protected float y;
    /**
     * Ob die {@link #x}-Koordinate bereits bekannt ist oder nicht. Lässt sich mit der Methode
     * {@link #isXSet()} abfragen.
     * @see #isXSet()
     * @see #addToX(float)
     * @see #calculateX()
     * @see #getX()
     * @see #setX(float)
     * @see #x
     */
    protected boolean xSet;
    /**
     * Ob die {@link #y}-Koordinate bereits bekannt ist oder nicht. Lässt sich mit der Methode
     * {@link #isYSet()} abfragen.
     * @see #calculateY()
     * @see #setY(float)
     * @see #addToY(float)
     * @see #getY()
     * @see #isYSet()
     * @see #y
     */
    protected boolean ySet;
    /**
     * Ein boolean, der signalisiert, ob neue Funktionen gesetzt oder ignoriert werden.
     */
    protected boolean ignoreNewFunctions;
    /**
     * Dieser boolean signalisiert, ob die {@link Graph Funktion} ersetzt werden kann oder nicht.
     */
    protected boolean replacing;
    /**
     * Die Anzahl an bytes, die ein Vertex2D braucht.
     */
    public final static int BYTES = 2 * Float.BYTES;

    /**
     * Erzeugt einen {@link Vertex2D}. Ein {@link Vertex2D} besteht aus einer {@link #x}-Koordinate
     * und einer {@link #y}-Koordinate.
     *
     * @param x ein Punkt auf der x-Achse
     * @param y ein Punkt auf der y-Achse
     */
    public Vertex2D(float x, float y) {
        this.x = x;
        this.y = y;
        xSet = true;
        ySet = true;
    }

    public Vertex2D(byte[] bytes) {
    	bytesToVertex2D(bytes);
    }
    
    protected Vertex2D() {}
    
    /**
     * Erzeugt einen {@link Vertex2D} in einem Koordinatensystem. Der erste Wert im Array ist {@link #x},
     * der zweite {@link #y}. Das Array darf allerdings nicht {@code null} sein, sonst wird eine
     * {@link NullPointerException} geworfen. Das Array darf nicht leer sein, sonst wird eine
     * {@link IllegalArgumentException} geworfen.
     *
     * @param values ein Array mit den Koordinaten, es werden, wenn es zu viele Werte sind, nur die
     *               ersten zwei Koordinaten verwendet
     * @throws NullPointerException sollte kein Array übergeben werden
     * @throws IllegalArgumentException sollte ein leeres Array übergeben werden
     */
    public Vertex2D(float[] values) {
        if (values == null) {
            throw new NullPointerException("No Array given!");
        }
        if(values.length < 1) {
            throw new IllegalArgumentException("Empty arrays are not allowed!");
        }
        if(values.length > 0) {
            x = values[0];
            xSet = true;
            if(values.length >= 2) {
                y = values[1];
                ySet = true;
            }
        }
    }

    protected void bytesToVertex2D(byte[] bytes) {
    	if(bytes.length != BYTES) {
    		throw new IllegalArgumentException("Zu viele bytes �bergeben!");
    	}
    	byte[] x = new byte[] {
    			bytes[0], bytes[1], bytes[2], bytes[3]
    	};
    	byte[] y = new byte[] {
    			bytes[4], bytes[5], bytes[6], bytes[7]
    	};
    	//x = Float.intBitsToFloat(bh.bytesToInt(bh.subBytes(bytes, 0, 4)));
    	this.x = Float.intBitsToFloat(ByteHelper.bytesToInt(x));
    	//y = Float.intBitsToFloat(bh.bytesToInt(bh.subBytes(bytes, 4, bytes.length)));
    	this.y = Float.intBitsToFloat(ByteHelper.bytesToInt(y));
    	xSet = true;
    	ySet = true;
    }
    
    /**
     * Gibt den Punkt auf der x-Achse zurück. Sollte die {@link #x}-Koordinate bereits gesetzt oder
     * errechnet worden sein, wird eine {@link IllegalStateException} geworfen. Ob die
     * {@link #x}-Koordinate bereits gesetzt oder errechnet wurde, lässt sich mit der Methode
     * {@link #isXSet()} überprüfen.
     *
     * @return den Punkt auf der x-Achse
     * @throws IllegalStateException sollte die {@link #x}-Koordinate noch nicht gesetzt oder errechnet worden sein
     * @see #addToX(float)
     * @see #calculateX()
     * @see #setX(float)
     * @see #isXSet()
     * @see #x
     * @see #xSet
     */
    public float getX() {
        if(!xSet) {
            throw new IllegalStateException("The x-coordinate has not been set!");
        }
        return x;
    }

    /**
     * Gibt den Punkt auf der y-Achse zurück. Sollte dieser noch nicht gesetzt oder errechnet worden
     * sein, wird ein {@link IllegalStateException} geworfen. Ob die {@link #y}-Koordinate bereits
     * gesetzt oder errechnet wurde, lässt sich mit der Methode {@link #isYSet()} überprüfen.
     *
     * @return den Punkt auf der y-Achse
     * @throws IllegalStateException sollte die {@link #y}-Koordinate noch nicht gesetzt oder errechnet worden sein
     * @see #calculateY()
     * @see #setY(float)
     * @see #addToY(float)
     * @see #isYSet()
     * @see #y
     * @see #ySet
     */
    public float getY() {
        if(!ySet) {
            throw new IllegalStateException("The y-coordinate has not been set!");
        }
        return y;
    }

    /**
     * Ersetzt den Punkt auf der x-Achse durch den angegebenen Wert. Sollte die {@link #x}-Koordinate
     * bereits übergeben oder ausgerechnet worden sein, wird ein {@link IllegalStateException} geworfen.
     * Ob die {@link #x}-Koordinate bereits gesetzt oder errechnet wurde, lässt sich mit der Methode
     * {@link #isXSet()} überprüfen.
     *
     * @param x der neue Punkt auf der x-Achse
     * @throws IllegalStateException sollte die {@link #x}-Koordinate bereits berechnet oder gesetzt worden sein
     * @see #getX()
     * @see #addToX(float)
     * @see #calculateX()
     * @see #isXSet()
     * @see #x
     * @see #xSet
     */
    public void setX(float x) {
        if(xSet) {
            throw new IllegalStateException("The x-coordinate has been set already!");
        }
        this.x = x;
        xSet = true;
    }

    /**
     * Ersetzt den Punkt auf der y-Achse mit dem angegebenen Wert. Sollte die {@link #y}-Koordinate
     * bereits ausgerechnet oder gesetzt worden sein, wird ein {@link IllegalStateException} geworfen.
     * Ob die {@link #y}-Koordinate bereits gesetzt oder errechnet wurde, lässt sich mit der Methode
     * {@link #isYSet()} überprüfen.
     *
     * @param y der neue Punkt auf der y-Achse
     * @throws IllegalStateException sollte die {@link #y}-Koordinate bereits gesetzt oder berechnet worden sein
     * @see #getY()
     * @see #addToY(float)
     * @see #calculateY()
     * @see #isYSet()
     * @see #y
     * @see #ySet
     */
    public void setY(float y) {
        if(ySet) {
            throw new IllegalStateException("The y-coordinate has been set already!");
        }
        this.y = y;
        ySet = true;
    }

    /**
     * Gibt diesen {@link Vertex2D} in einem Array zurück, das erste Objekt ist die {@link #x}-Koordinate,
     * das zweite Objekt ist die {@link #y}-Koordinate.
     *
     * @return diesen Punkt als {@link Float}-Array
     * @see #toDoubleArray()
     */
    public float[] toFloatArray() {
        return new float[] {x, y};
    }

    /**
     * Gibt diesen {@link Vertex2D} als {@link Double}-Array zurück. Das erste Objekt ist die
     * {@link #x}-Koordinate, das zweite ist die {@link #y}-Koordinate.
     *
     * @return diesen Punkt als ein {@link Double}-Array
     * @see #toFloatArray()
     */
    public double[] toDoubleArray() {
        return new double[] {x, y};
    }

    /**
     * Gibt zurück, ob die {@link #x}-Koordinate bereits gesetzt oder errechnet wurde, also ob sie
     * verwendbar ist oder nicht.
     *
     * @return ob die {@link #x}-Koordinate verwendbar ist oder nicht
     * @see #getX()
     * @see #addToX(float)
     * @see #calculateX()
     * @see #setX(float)
     * @see #x
     * @see #xSet
     */
    public boolean isXSet() {
        return xSet;
    }

    /**
     * Gibt zurück, ob die {@link #y}-Koordinate bereits gesetzt oder errechnet wurde.
     *
     * @return ob die {@link #y}-Koordinate verwendbar ist oder nicht
     * @see #getY()
     * @see #calculateY()
     * @see #setY(float)
     * @see #addToY(float)
     * @see #y
     * @see #ySet
     */
    public boolean isYSet() {
        return ySet;
    }

    /**
     * Sollte das angegebene {@link Object} ein {@link Vertex2D} sein, wird überprüft, ob die Werte
     * gleich sind oder nicht. Ist das angegebene {@link Object} von einem anderen Typ, gilt
     * {@link Object#equals(Object)}.
     *
     * @param another das zu überprüfende {@link Object}
     * @return wenn das {@link Object} ein {@link Vertex2D} ist, ob die Werte gleich sind oder nicht
     */
    public boolean equals(Object another) {
        if(another instanceof Vertex2D) {
            return (x == ((Vertex2D) another).x) && (y == ((Vertex2D) another).y)
                    && (xSet == ((Vertex2D) another).xSet) && (ySet == ((Vertex2D) another).ySet)
                    /*&& (function == ((Vertex2D) another).function)*/;
        }
        return super.equals(another);
    }

    /**
     * Nur für Subklassen, die die normale Methode wegen Rückkopplungen nicht verwenden können. Gibt
     * {@link Object#equals(Object)} zurück.
     *
     * @param another das zu vergleichende {@link Object}
     * @return ob dieses und das angegebene {@link Object} gleich sind oder nicht
     */
    protected boolean objectEquals(Object another) {
        return super.equals(another);
    }

    /**
     * Gibt einfach die Koordinaten dieses Punktes zur�ck.
     * 
     * @return x Leerzeichen y
     */
    public String toStringMachine() {
    	return x + " " + y;
    }
    
    public String toString() {
        String functionString = "Not available";
        /*if(function != null) {
            functionString = function.getEquation();
        }*/
        return getClass().getName() + ": equation: " + functionString + "; X: "
                + Float.toString(x) + " set=" + Boolean.toString(xSet) + "; Y: " + Float.toString(y)
                + " set=" + Boolean.toString(ySet);
    }

    /**
     * Hiermit lässt sich einstellen, ob die {@link #function} ersetzt werden kann oder nicht.
     *
     * @param flag ob die {@link #function} ersetzbar ist oder nicht
     */
    public void enableReplacing(boolean flag) {
        replacing = flag;
        if(replacing) {
            ignoreNewFunctions = false;
        }
    }

    @Override
    public byte[] convertToBytes() {
    	//ArrayList<byte[]> bytes = new ArrayList<>();
    	byte[] xx = ByteHelper.intToBytes(Float.floatToIntBits(x));
    	byte[] yy = ByteHelper.intToBytes(Float.floatToIntBits(y));
    	//byte[] toReturn = new byte[xx.length + yy.length];
    	//System.arraycopy(xx, 0, toReturn, 0, xx.length);
    	//System.arraycopy(yy, 0, toReturn, xx.length - 1, yy.length);
    	return new byte[] {
    			xx[0], xx[1], xx[2], xx[3],
    			yy[0], yy[1], yy[2], yy[3]
    	};
    }
    
    /**
     * Konvertiert ein ganzes Array aus Vertexen zu einem float-Array. Dieses Array hat dann das folgende
     * Format: X, Y, X, Y, ... bzw.: X, Y, Z, X, Y, Z, ...
     *
     * Unterschiedliche Typen - 2D UND 3D - werden nicht unterstützt.
     *
     * @param vertices ein Array mit den zu konvertierenden Vertexen
     * @return ein float-Array mit den Positionsdaten der Vertexe, in der Reihenfolge der originalen Vertexen
     * @throws NullPointerException sollten keine Objekte übergeben werden
     * @throws IllegalArgumentException sollte das Array leer sein
     */
    public static float[] arrayToFloatArray(Vertex2D[] vertices) {
        if (vertices == null) {
            throw new NullPointerException("No vertices given!");
        }
        if(vertices.length == 0) {
            throw new IllegalArgumentException("Empty Array!");
        }
        float[] array = new float[vertices.length * (vertices instanceof Vertex3D[] ? 3 : 2)];
        int i = 0;
        for(Vertex2D vertex : vertices) {
            array[i] = vertex.getX();
            i++;
            array[i] = vertex.getY();
            i++;
            if(vertex instanceof Vertex3D) {
                array[i] = ((Vertex3D) vertex).getZ();
                i++;
            }
        }
        return array;
    }
}