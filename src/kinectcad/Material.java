/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kinectcad;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author George
 */
public class Material {

    
    public double[] Ka;
    public double[] Kd;
    public double[] Ks;
    public FloatBuffer ABuff;
    public FloatBuffer DBuff;
    public FloatBuffer SBuff;
    
    public double d;
    public String ref;
    public ByteBuffer texBuff;
    public Texture texture;
    
    public static ArrayList<Material> materials = new ArrayList<Material>(0);
    
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
        
        String refTemp = null;
        double[] KaTemp = null;
        double[] KdTemp = null;
        double[] KsTemp = null;
        double dTemp = 0;
        String KdFile = null;        
        Material temp = null;
        
        
        while(s.hasNext())
        {
            String tS = s.nextLine();
            if(tS.startsWith("newmtl"))
            {
                if(refTemp!=null){
                    temp = new Material(KaTemp, KdTemp, KsTemp, dTemp ,refTemp);
                    temp.loadImage(KdFile);
                    materials.add(temp);
                }
                
                refTemp = tS.substring(7);
                System.out.println(refTemp);
            }
            
            if(tS.startsWith("Ka")){
                Scanner sl = new Scanner(tS);
                sl.skip("Ka");
                KaTemp = new double[]{sl.nextDouble(),sl.nextDouble(),sl.nextDouble()};
                System.out.println(KaTemp[0] + " " + KaTemp[0] + " " + KaTemp[0]);
            }
             
            if(tS.startsWith("Kd")){
                Scanner sl = new Scanner(tS);
                sl.skip("Kd");
                KdTemp = new double[]{sl.nextDouble(),sl.nextDouble(),sl.nextDouble()};
                System.out.println(KdTemp[0] + " " + KdTemp[0] + " " + KdTemp[0]);
            }
            
            if(tS.startsWith("Ks")){
                Scanner sl = new Scanner(tS);
                sl.skip("Ks");
                KsTemp = new double[]{sl.nextDouble(),sl.nextDouble(),sl.nextDouble()};
                System.out.println(KsTemp[0] + " " + KsTemp[0] + " " + KsTemp[0]);
            }
            
            if(tS.startsWith("d")){
                Scanner sl = new Scanner(tS);
                sl.skip("d");
                dTemp = sl.nextDouble();
                System.out.println(dTemp);
            }
                
            if(tS.startsWith("map_Kd"))
            {
                KdFile = tS.substring(7);
            }
        }
        
        temp = new Material(KaTemp, KdTemp, KsTemp, dTemp ,refTemp);
        temp.loadImage(KdFile);
        materials.add(temp);
        
    }
    
    
    
    
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
    
    public void loadImage(String file)
    {
        if(file == null){
            System.out.println("Image path not specified");
            texture = null;
            return;
        }
        try 
        {texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(KinectCAD.filepath + file),true, GL_NEAREST );}
        catch (IOException ex)
        {Logger.getLogger(Material.class.getName()).log(Level.SEVERE, null, ex);}
        System.out.println("Texture loaded: "+texture);
        System.out.println(">> Image width: "+texture.getImageWidth());
        System.out.println(">> Image height: "+texture.getImageHeight());
        System.out.println(">> Texture width: "+texture.getTextureWidth());
        System.out.println(">> Texture height: "+texture.getTextureHeight());
        
       
    }
    
    public void bindTexture()
    {
        if(texture == null){
            return;
        }
        texture.bind();
    }
    
}
