package hahn.manuel.schnitzeljagd;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import hahn.graphicEngine.openGLES2.GraphicObject;
import hahn.graphicEngine.openGLES2.Shader;
import hahn.graphicEngine.util.Color;
import hahn.graphicEngine.util.GraphicException;
import hahn.graphicEngine.util.Vertex;
import hahn.graphicEngine.util.Vertex2D;
import hahn.graphicEngine.util.Vertex3D;
import hahn.manuel.obsolete.AnimationWay;
import hahn.manuel.obsolete.GraphicMaterial;
import hahn.manuel.obsolete.GraphicTexture;

/**
 * Diese Klasse lädt ausschließlich die Grafikobjekte. Es können ausschließlich Dateien im Wavefront -
 * Format geladen werden.
 *
 * Created by Manuel Hahn.
 * @author Manuel Hahn
 * @since 28.06.2016
 */
public class GraphicObjectLoader {
    //private static GraphicWorld world;
    private static ArrayList<Vertex3D> v;
    private static ArrayList<Vertex3D> vn;
    private static ArrayList<Vertex2D> vt;
    private static HashMap<String, GraphicMaterial> ms;
    private static int generic;
    //private static Shader vertexShader;
    //private static Shader fragmentShader;
    private static int[] glESVersion = new int[] {2, 0, 0};

    /**
     * Lädt ein großes GraphicObject aus den Assets, der Ordner wird mit file übergeben.
     *
     * @param file der Ordner mit den einzelnen Teilen des Objekts
     * @param color die Farbe des Objektes im RGBA-Standard, wird null übergeben, wird die Standardfarbe
     *              (Android-Grün) verwendet
     * @param manager der AssetsManager, mit dem Dateien aus den Assets gelesen werden können
     * @return ein GraphicObject mit einzelnen GraphicObject.Parts
     */
    /*public static GraphicObject getGraphicObjectWithParts(String file, float[] color, AssetManager manager)
    {
        GraphicObject obj = null;
        try {
            String[] files = manager.list(file);
            obj = new GraphicObject(loadGraphicShapes(file + "/main/main.off", color, manager));
            for (String f : files) {
                if (f.endsWith(".off")) {
                    obj.addPart(obj.createPart(loadGraphicShapes(file + "/" + f, color, manager)), f.substring(1, f.length() - 4));
                }
            }
        } catch(Exception e) {
            obj = null;
        }
        return obj;
    }*/

    /**
     * Lädt eine *.off-Datei aus dem Assets-Ordner, und gibt das enthaltene 3D-Objekt als ein Shape-Array
     * zurück.
     *
     * @param file die *.off-Datei, in der die 3D-Rohdaten stehen
     * @return ein Array mit Shapes, das mit den Daten aus der Datei übereinstimmt, oder null, wenn ein Fehler auftritt
     */
    /*private static Shape[] loadGraphicShapes(String file, float[] color, AssetManager manager)
    {
        try {
            int i = 2;
            BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(file)));
            reader.readLine();
            String xs = reader.readLine();
            String vs = xs.substring(0, xs.indexOf(" "));
            String fs = xs.substring(2, xs.indexOf(" ", 3));
            short verts = Short.parseShort(vs.trim());
            short faces = Short.parseShort(fs.trim());
            Vertex3D ecks[] = new Vertex3D[verts];
            int in = 0;
            while (i < verts + 2) {
                String s = reader.readLine();
                String x = s.substring(0, s.indexOf(" "));
                String y = s.substring(s.indexOf(" ") + 1, s.lastIndexOf(" "));
                String z = s.substring(s.lastIndexOf(" "));
                float xx = Float.parseFloat(x);
                float yy = Float.parseFloat(y);
                float zz = Float.parseFloat(z);
                ecks[in] = new Vertex3D(xx, yy, zz);
                i++;
                in++;
            }
            in = 0;
            Shape shapes[] = new Shape[faces];
            while (i < (verts + faces + 2)) {
                String s = reader.readLine();
                int firstLeer = s.indexOf(" ");
                int secondLeer = s.indexOf(" ", firstLeer + 1);
                int thirdLeer = s.lastIndexOf(" ");
                String anzahlEcken = s.substring(0, firstLeer);
                String eck1 = s.substring(firstLeer, secondLeer);
                String eck2 = s.substring(secondLeer, thirdLeer);
                String eck3 = s.substring(thirdLeer);
                short ce1 = Short.parseShort(eck1.trim());
                short ce2 = Short.parseShort(eck2.trim());
                short ce3 = Short.parseShort(eck3.trim());
                short aE = Short.parseShort(anzahlEcken);
                Vertex3D fv[] = new Vertex3D[aE];
                fv[0] = ecks[ce1];
                fv[1] = ecks[ce2];
                fv[2] = ecks[ce3];
                /*if(color != null) {
                    shapes[in] = new Shape(fv, color);
                }
                else
                {*/
                /*shapes[in] = new Shape(fv, null, null, null, null, null);
                //}
                i++;
                in++;
            }
            return shapes;
        } catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }*/

    /**
     * Lädt eine Welt aus den Assets. Die Rohdaten werden aus der *.obj-Datei geladen, Materialien
     * werden aus der *.mtl-Datei geladen. Sollte nun einwandfrei funktionieren.
     *
     * @param objFile die Datei mit den Rohdaten
     * @param mtlFile die Datei mit den Materialien
     * @param resources die Resourcen, in denen sich die angegebenen Daten befinden
     * @return eine Welt, die aus dieser Datei gelesen wurde
     * @throws IOException sollte beim einlesen der Daten ein Fehler auftreten
     * @throws NullPointerException sollte einer der Parameter null sein
     */
    // TODO: 20.03.2017 Nur temporär deaktiviert!
    public static GenericGraphicObject[] loadGraphicWorld(int objFile, int mtlFile, Resources resources) throws IOException {
        if (resources == null) {
            throw new NullPointerException("The resources must not be null!");
        }

        v = new ArrayList<>();
        vn = new ArrayList<>();
        vt = new ArrayList<>();
        ArrayList<GenericGraphicObject> objects = new ArrayList<>();
        ms = new HashMap<>();
        String line, name = null;
        ArrayList<String> object = new ArrayList<>();

        // Die *.mtl-Datei
        BufferedReader mtlReader = new BufferedReader(new InputStreamReader(resources.openRawResource(mtlFile)));
        while(mtlReader.ready()) {
            line = mtlReader.readLine();
            if((line.startsWith("#")) || (line.equals(""))) {
                Log.i("GraphicObjectLoader", "Unused line: Empty line or starts with '#'");
                Log.i("GraphicObjectLoader", "Line is: '" + line + "'");
            } else if(!line.startsWith("newmtl")) {
                object.add(line);
            } else {
                if(!object.isEmpty()) {
                    //world.addMaterial(getGraphicMaterial(object.toArray(new String[1]), resources), name);
                    //ms.put(name, getGraphicMaterial(object.toArray(new String[1]), resources));
                }
                name = line.substring(6).trim();
                object.clear();
                object.add(line);
            }
        }
        //world.addMaterial(getGraphicMaterial(object.toArray(new String[1]), resources), name);
        //ms.put(name, getGraphicMaterial(object.toArray(new String[1]), resources));
        object.clear();
        mtlReader.close();

        // Die *.obj-Datei
        BufferedReader objReader = new BufferedReader(new InputStreamReader(resources.openRawResource(objFile)));
        while(objReader.ready()) {
            line = objReader.readLine();
            if((line.startsWith("#")) || (line.equals("") || (line.startsWith("mtllib")))) {
                Log.i("GraphicObjectLoader", "Unused line: Empty line or starts with '#' or 'mtllib'");
                Log.i("GraphicObjectLoader", "Line is: '" + line + "'");
            } else if(!line.startsWith("o")) {
                object.add(line);
            } else {
                if(!object.isEmpty()) {
                    /*if (name != null) {
                        if (name.startsWith("Light")) {
                            world.addGraphicLight(getGraphicLight(object.toArray(new String[1])), name);
                        } else if(name.startsWith("lookat")) {
                            world.setCameraLooAn(getAnimationWay(object.toArray(new String[1])));
                        } else if(name.startsWith("camera")) {
                            world.setCameraPosAn(getAnimationWay(object.toArray(new String[1])));
                        } else {
                            world.addGraphicObject(getGraphicObject(object.toArray(new String[1])), name);
                        }
                    } else {
                        generic++;
                        world.addGraphicObject(getGraphicObject(object.toArray(new String[1])), "generic" + generic);
                    }*/
                    //if(!(name != null && name.contains("Light"))) {
                    objects.add(getGraphicObject(object.toArray(new String[object.size()])));
                    //}
                }
                name = line.substring(1).trim();
                object.clear();
                object.add(line);
            }
        }
        //world.addGraphicObject(getGraphicObject(object.toArray(new String[1])), name);
        objects.add(getGraphicObject(object.toArray(new String[object.size()])));
        objReader.close();
        return objects.toArray(new GenericGraphicObject[objects.size()]);
    }

    /**
     * Dekodiert die Daten im angegebenen Array als GraphicMaterial. Müsste eigentlich auch einwandfrei
     * funktionieren.
     *
     * @param mtlData das Quelltext-Array
     * @param resources benötigt, um eine eventuell in den Resourcen gespeicherte Texture zu laden
     * @return ein GraphicMaterial mit den angegebenen Daten
     */
    private static GraphicMaterial getGraphicMaterial(String[] mtlData, Resources resources) {
        GraphicMaterial material = new GraphicMaterial();
        for(String line : mtlData) {
            String start = line, trimmed;
            try {
                start = line.substring(0, start.indexOf(" ")).trim();
            } catch(Exception e) {
                start = "Exception";
            }
            switch(start) {
                case "Ns" :
                    trimmed = line.substring(2).trim();
                    material.setShine(Float.parseFloat(trimmed));
                    break;
                case "Ka" :
                case "Ks" :
                case "Kd" :
                    trimmed = line.substring(2).trim();
                    float r = Float.parseFloat(trimmed.substring(0, trimmed.indexOf(" ")).trim());
                    float g = Float.parseFloat(trimmed.substring(trimmed.indexOf(" ") + 1, trimmed.lastIndexOf(" ")).trim());
                    float b = Float.parseFloat(trimmed.substring(trimmed.lastIndexOf(" ")).trim());
                    Color kdkaksColor = new Color(r, g, b);
                    switch(start) {
                        case "Ka" :
                            material.setAmbientColor(kdkaksColor);
                            break;
                        case "Kd" :
                            material.setDiffuseColor(kdkaksColor);
                            break;
                        case "Ks" :
                            material.setSpecularColor(kdkaksColor);
                            break;
                    }
                    break;
                case "d" :
                case "Tr" :
                    trimmed = line.substring(2).trim();
                    material.setTransparency(Float.parseFloat(trimmed));
                    break;
                case "illum" :
                    trimmed = line.substring(5).trim();
                    material.setHasSpecularHighlights((Integer.parseInt(trimmed) == 2));
                    break;
                case "Ni" :
                    trimmed = line.substring(2).trim();
                    material.setHasRefraction((Float.parseFloat(trimmed) != 1));
                    break;
                case "map_Kd" :
                    trimmed = line.substring(6).trim();
                    try {
                        material.setDiffuseMap(new GraphicTexture(trimmed.substring(trimmed.lastIndexOf("/") + 1), resources));
                    } catch(GraphicException e) {
                        material.setDiffuseMap(null);
                    }
                    break;
            }
        }
        return material;
    }

    /**
     * Lädt ein 3D-Objekt aus Daten im Wavefront-Format.
     *
     * @param objData ein Array mit den zu dekodierenden Rohdaten
     * @return ein GraphicObject, das vollständig aus diesen Daten geladen wurde
     */
    private static GenericGraphicObject getGraphicObject(String[] objData) {
        //Object3D object;
        ArrayList<Vertex3D[]> f = new ArrayList<>();
        ArrayList<Vertex2D[]> fts = new ArrayList<>();
        ArrayList<Vertex3D[]> fns = new ArrayList<>();
        GraphicMaterial usemtl = null;
        for(String line : objData) {
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
                    v.add(new Vertex3D(xx, yy, zz));
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
        return new GenericGraphicObject(convertArrayList3D(f), null, /*GraphicObject.loadResourceToGL(0, null)*/0, null, convertArrayList(fts));
        /*switch(glESVersion[0]) {
            case 3 :
                switch(glESVersion[1]) {
                    case 1 : // Das OpenGL ES 3.1 Objekt
                        break;
                    default: // Das OpenGL ES 3.0 Objekt
                        break;
                }
            default:
                //object = new GraphicObject(f.toArray(new Shape[0]));
                break;
        }*/
        //return null;
        //return object;
    }

    private static Vertex2D[] convertArrayList(ArrayList<Vertex2D[]> list) {
        ArrayList<Vertex2D> returnList = new ArrayList<>();
        for(Vertex2D[] vertices : list) {
            for(Vertex2D vertex : vertices) {
                returnList.add(vertex);
            }
        }
        return returnList.toArray(new Vertex2D[returnList.size()]);
    }

    private static Vertex3D[] convertArrayList3D(ArrayList<Vertex3D[]> list) {
        ArrayList<Vertex3D> returnList = new ArrayList<>();
        for(Vertex3D[] vertices : list) {
            for(Vertex3D vertex : vertices) {
                returnList.add(vertex);
            }
        }
        return returnList.toArray(new Vertex3D[returnList.size()]);
    }

    /**
     * Gibt die verwendete Version von OpenGL ES zurück.
     *
     * @return die Version von OpenGL ES als int-Array, der erste Wert ist die Major-Version, der
     * zweite Wert ist die Minor-Version
     * @param reqGLESVersion die binäre OpenGL ES Version, wird 0 übergeben, wird die Version nur zurückgegeben
     */
    public static int[] checkAndReturnGLESVersion(int reqGLESVersion) {
        if(reqGLESVersion != 0) {
            glESVersion[0] = (reqGLESVersion & 0xffff0000) >> 16;
            glESVersion[1] = reqGLESVersion & 0xffff;
            glESVersion[2]++;
        }
        return glESVersion;
    }
}