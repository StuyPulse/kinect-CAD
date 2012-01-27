/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kinectcad;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;

/**
 *
 * @author George
 */
public class Material {

    static void load(String string) {
        Scanner s;
    
        try
        {
            s = new Scanner(new File (string));
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        while(s.hasNext())
        {
            String tS = s.nextLine();
            if(tS.startsWith("newmtl"))
            {
                String refTemp = tS.substring(7);
                s.next();
                double[] KaTemp = new double[]{s.nextDouble(),s.nextDouble(),s.nextDouble()};
                s.next();
                double[] KdTemp = new double[]{s.nextDouble(),s.nextDouble(),s.nextDouble()};
                s.next();
                double[] KsTemp = new double[]{s.nextDouble(),s.nextDouble(),s.nextDouble()};
                s.next();
                double dTemp = s.nextDouble();
                tS = s.nextLine();
                String KdFile;
                if(tS.startsWith("map_Kd"))
                KdFile = tS.substring(7);
                
                System.out.println(refTemp);
                System.out.println(KaTemp[0] + " " + KaTemp[0] + " " + KaTemp[0]);
                System.out.println(KdTemp[0] + " " + KdTemp[1] + " " + KdTemp[1]);
                System.out.println(KsTemp[0] + " " + KsTemp[2] + " " + KsTemp[2]);
                System.out.println(dTemp);
                
                
                materials.add(new Material(KaTemp, KdTemp, KsTemp, dTemp ,refTemp));
                
            }
        }
        
        
    }
    
    public double[] Ka;
    public double[] Kd;
    public double[] Ks;
    public FloatBuffer ABuff;
    public FloatBuffer DBuff;
    public FloatBuffer SBuff;
    
    public double d;
    public String ref;
    
    public static ArrayList<Material> materials = new ArrayList<Material>(0);
    
    public Material(double[] ka, double[] kd, double[] ks, double D, String Ref)
    {
        if(Array.getLength(ka)>3|Array.getLength(kd)>3|Array.getLength(ks)>3)
            System.out.println("Material initialized with improperly sized array");
        
        Ka = ka;
        Kd = kd;
        Ks = ks;
        
        d = D;
        ref = Ref;
        
        ABuff = getAmbient();
        DBuff = getDiffuse();
        SBuff = getSpecular();
    }
    
    public FloatBuffer getAmbient()
    {
        FloatBuffer temp = BufferUtils.createFloatBuffer(4);
        temp.put(new float[]{(float)Ka[0],(float)Ka[1],(float)Ka[2],(float)d});
        temp.rewind();
        return temp;
    }
    
    public FloatBuffer getDiffuse()
    {
        FloatBuffer temp = BufferUtils.createFloatBuffer(4);
        temp.put(new float[]{(float)Kd[0],(float)Kd[1],(float)Kd[2],(float)d});
        temp.rewind();
        return temp;
    }
    
    public FloatBuffer getSpecular()
    {
        FloatBuffer temp = BufferUtils.createFloatBuffer(4);
        temp.put(new float[]{(float)Ks[0],(float)Ks[1],(float)Ks[2],(float)d});
        temp.rewind();
        return temp;
    }
    
}
