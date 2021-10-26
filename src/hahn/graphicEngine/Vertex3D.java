package hahn.graphicEngine;

import hahn.mainIDE.Convertable;
import hahn.utils.ByteHelper;

/**
 * Repräsentiert einen Punkt in einem dreidimensionalen Raum. Die {@link #x}- und die {@link #y}-Koordinate
 * werden wie gewohnt mit der Formel {@code f(x) = mx + b} berechnet, {@link #z} wird mit der Formel
 * {@code z = mx + b} berechnet. Die Werte für die {@link #z}-Formel sind allerdings bis auf {@link #x}
 * unterschiedlich.
 *
 * Created by Manuel Hahn.
 * @author Manuel Hahn
 * @since 23.05.2016
 */
public class Vertex3D extends Vertex2D implements Convertable {
    /**
     * Der Punkt auf der z-Achse.
     * @see #getZ()
     * @see #zSet
     * @see #isZSet()
     * @see #addToZ(float)
     * @see #setZ(float)
     * @see #calculateZ()
     */
    protected float z;
    /**
     * Ob die {@link #z}-Koordinate bereits gesetzt wurde oder nicht.
     * @see #z
     * @see #getZ()
     * @see #isZSet()
     * @see #addToZ(float)
     * @see #calculateZ()
     * @see #setZ(float)
     */
    protected boolean zSet;
    /**
     * Die Anzahl an bytes, die ein Vertex3D braucht.
     */
    public final static int BYTES = Vertex2D.BYTES + Float.BYTES;

    /**
     * Erzeugt einen dreidimensionalen Punkt. Keiner der angegebenen Werte kann verändert werden!
     *
     * @param x dieser Punkt auf der x-Achse
     * @param y dieser Punkt auf der y-Achse
     * @param z dieser Punkt auf der z-Achse
     */
    public Vertex3D(float x, float y, float z) {
        super(x, y);
        this.z = z;
        zSet = true;
    }

    public Vertex3D(byte[] bytes) {
    	if(bytes.length != BYTES) {
    		throw new IllegalArgumentException("Zu viele bytes �bergeben!");
    	}
    	/*super(new ByteHelper().subBytes(bytes, 0, bytes.length - 4));
    	ByteHelper bh = new ByteHelper();
    	z = Float.intBitsToFloat(bh.bytesToInt(bh.subBytes(bytes, 8, bytes.length)));
    	zSet = true;*/
    	bytesToVertex2D(new byte[] {
    			bytes[0], bytes[1], bytes[2], bytes[3],
    			bytes[4], bytes[5], bytes[6], bytes[7]
    	});
    	z = Float.intBitsToFloat(ByteHelper.bytesToInt(new byte[] { bytes[8], bytes[9], bytes[10], bytes[11] }));
    	zSet = true;
    }
    
    /**
     * Erzeugt einen Punkt. Der erste Wert im Array wird als {@link #x} verarbeitet, der zweite Wert
     * als {@link #y} und der dritte als {@link #z}. Sollte das Array mehr Objekte enthalten, werden
     * nur die ersten drei verarbeitet. Sollte kein Array angegeben werden, wird eine {@link NullPointerException}
     * geworfen. Sollte ein leeres Array übergeben werden, wird eine {@link IllegalArgumentException}
     * geworfen.
     *
     * @param values ein Array mit den Koordinaten
     * @throws NullPointerException sollte kein Array übergeben werden
     * @throws IllegalArgumentException sollte ein leeres Array übergeben werden
     */
    public Vertex3D(float[] values) {
        super(values);
        if(values.length >= 3) {
            z = values[3];
            zSet = true;
        }
    }

    /**
     * Gibt den Punkt auf der z-Achse zurück.
     *
     * @return diesen Punkt auf der z-Achse
     * @see #z
     * @see #zSet
     * @see #isZSet()
     * @see #addToZ(float)
     * @see #calculateZ()
     * @see #setZ(float)
     */
    public float getZ() {
        return z;
    }

    /**
     * Hier wird die {@link #z}-Koordinate gesetzt. Sollte das bereits geschehen sein oder die
     * {@link #z}-Koordinate bereits ausgerechnet worden sein, wird eine {@link IllegalStateException}
     * geworfen. Ob die {@link #z}-Koordinate bereits gesetzt wurde, lässt sich mit der Methode
     * {@link #isZSet()} überprüfen.
     *
     * @param z der Punkt auf der z-Achse
     * @throws IllegalStateException sollte die {@link #z}-Koordinate bereits gesetzt worden sein
     * @see #z
     * @see #zSet
     * @see #isZSet()
     * @see #getZ()
     * @see #addToZ(float)
     * @see #calculateZ()
     */
    public void setZ(float z) {
        if(zSet) {
            throw new IllegalStateException("The z-coordinate has been set already!");
        }
        this.z = z;
        zSet = true;
    }

    /**
     * Gibt diesen Punkt in einem {@link Float}-Array zurück. Das erste Objekt des Arrays ist die
     * {@link #x}-Koordinate, das zweite die {@link #y}-Koordinate, und das letzte ist die
     * {@link #z}-Koordinate.
     *
     * @return diesen Punkt als {@link Float}-Array
     * @see #toDoubleArray()
     */
    @Override
    public float[] toFloatArray() {
        return new float[] {x, y, z};
    }

    /**
     * Gibt diesen Punkt als {@link Double}-Array zurück. Das erste Objekt ist die {@link #x}-Koordinate,
     * das zweite die {@link #y}-Koordinate, das letzte die {@link #z}-Koordinate.
     *
     * @return diesen Punkt als {@link Double}-Array
     * @see #toFloatArray()
     */
    @Override
    public double[] toDoubleArray() {
        return new double[] {x, y, z};
    }

    /**
     * Gibt zurück, ob die {@link #z}-Koordinate bereits errechnet oder gesetzt wurde.
     *
     * @return ob die {@link #z}-Koordinate verwendbar ist oder nicht
     * @see #z
     * @see #zSet
     * @see #getZ()
     * @see #addToZ(float)
     * @see #calculateZ()
     * @see #setZ(float)
     */
    public boolean isZSet() {
        return zSet;
    }

    /**
     * Gibt einfach die Koordinaten dieses Punktes aus.
     * 
     * @return x Leerzeichen y Leerzeichen z
     */
    @Override
    public String toStringMachine() {
    	return super.toStringMachine() + " " + z;
    }
    
    public boolean equals(Object another) {
        if(another instanceof Vertex3D) {
            return super.equals(another) && (z == ((Vertex3D) another).z) && (zSet == ((Vertex3D) another).zSet);
        }
        return objectEquals(another);
    }

    @Override
    public byte[] convertToBytes() {
    	/*byte[] sbs = super.convertToBytes();
    	byte[] toReturn = new byte[sbs.length + 4];
    	System.arraycopy(sbs, 0, toReturn, 0, sbs.length);
    	byte[] zz = new ByteHelper().intToBytes(Float.floatToIntBits(z));
    	System.arraycopy(zz, 0, toReturn, sbs.length - 1, zz.length);*/
    	byte[] sbts = super.convertToBytes();
    	byte[] zz = ByteHelper.intToBytes(Float.floatToIntBits(z));
    	return new byte[] {
    			sbts[0], sbts[1], sbts[2], sbts[3],
    			sbts[4], sbts[5], sbts[6], sbts[7],
    			zz[0], zz[1], zz[2], zz[3]
    	};
    }
    
    public String toString() {
        return super.toString() + ", Z: " + Float.toString(z) + " set=" + Boolean.toString(zSet);
    }
}