
package kinectcad;

import com.sun.squawk.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.io.*;
import java.util.Scanner;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.ListIterator;
import org.lwjgl.BufferUtils;

public class KinectCAD
{
    
    public FloatBuffer lightAmb;
    public FloatBuffer lightDiff;
    public FloatBuffer lightPos;
    
    public static final String filepath = "C:\\Users\\George\\Desktop\\kinectCadfiles\\";
    public final String file = "cube4.obj";
    
    
    public static void main(String[] args)
    {
        KinectCAD mainDerp = new KinectCAD();
        
        mainDerp.start();
       
    }
    
    public void start() {
        try {
	    Display.setDisplayMode(new DisplayMode(800,600));
	    Display.create();
	} catch (LWJGLException e) {
	    System.exit(0);
	}
        
        glEnable(GL_DEPTH_TEST); 
        glEnable(GL_NORMALIZE);
        
        
        double angleX = 0;
        double angleY = 0;
        double transX = 0;
        double transY = 0;
        double transZ = 500;

	glClearColor(0.0f,0.0f,0.0f,1.0f);

        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // fovy, aspect ratio, zNear, zFar
        org.lwjgl.util.glu.GLU.gluPerspective(30f, 1.1f, .1f, 1000f);
        // return to modelview matrix
        glMatrixMode(GL_MODELVIEW);
        
        lightAmb =  BufferUtils.createFloatBuffer(4);
        lightAmb.put(new float[]{.5f,.5f,.5f,1.0f});
        lightDiff = BufferUtils.createFloatBuffer(4);
        lightDiff.put(new float[]{1.0f, 1.0f, 1.0f, .5f});
        lightPos = BufferUtils.createFloatBuffer(4);
        lightPos.put(new float[]{0.0f, 0.0f, 2.0f, 1.0f});
        
        lightAmb.rewind();
        lightDiff.rewind();
        lightPos.rewind();
        
        glEnable(GL_TEXTURE_2D);
        glLight(GL_LIGHT1, GL_AMBIENT, lightAmb);     
        glLight(GL_LIGHT2,GL_DIFFUSE,lightDiff);
        glLight(GL_LIGHT2,GL_POSITION,lightPos);
        glEnable(GL_LIGHT1);
        glEnable(GL_LIGHT2);
        glEnable(GL_LIGHTING);
        
        
        long x = System.currentTimeMillis();
        DrawObject[] o = new DrawObject[]{loadObj(filepath + file),loadObj(filepath + "cube3.obj")};
        System.out.println("Loaded in " + (System.currentTimeMillis()-x) + " milliseconds.");
	//DrawObject o =null;
        Timer timer = new Timer(500);
        timer.start();
	while (!Display.isCloseRequested()) {
	
            
	    drawScene(angleX,angleY,new double[]{transX,transY,transZ},o);
                
            double scale = 50; //50
            double scaleT = 1;
            double scaleZ = 100; //100\
            scale*=timer.getDelay();
            scaleT*=timer.getDelay();
            scaleZ*=timer.getDelay();
            angleY= angleY%360;
            angleX= angleX%360;
            
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            {
                angleY+=scale;                
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            {
                angleY-=scale;                
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
                angleX+=scale;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
                angleX-=scale;             
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_W))
            {
                transY-=scaleT;                
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S))
            {
                transY+=scaleT;                
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A))
            {
                transX+=scaleT;                
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D))
            {
                transX-=scaleT;                
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_Q))
            {
                transZ+=scaleZ;                
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_E))
            {
                transZ-=scaleZ;
            }
            Display.setTitle("FPS: " + String.valueOf(timer.fps) + " " + transX + " " + transY + " " + transZ);
	    Display.update();
            timer.burnExcess();
	}
		
	Display.destroy();
    }
	
    public void drawScene(double angleX,double angleY,double[] trans ,DrawObject[] o)
    {
        
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);     // Clear The Screen And The Depth Buffer
    glLoadIdentity();     
    
    glTranslated(trans[0],trans[1],(trans[2]/Math.abs(trans[2]))*-1*Math.pow(1.5,Math.abs(trans[2]*.02))*.1);
    	
    glRotated(angleX,1,0,0);
    glRotated(angleY,0,1,0);
   
    int l = Arrays.length(o);
    for(int i =0; i<l;i++){
    o[i].draw();
    }
    
    //glRotated(angleX,1,0,0);
    //glRotated(angleY,0,1,0);
    //glTranslated(trans[0],trans[1],trans[2]+5);
    
    glLoadIdentity();
    glFlush();
    }
    
    public DrawObject loadObj(String file)
    {
        Scanner s;
        
        try {
            s = new Scanner(new File(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        int t = 0;
        
        while(s.hasNextLine())
        {
            t++;
            String tS = s.nextLine();
            if(tS.startsWith("mtllib"))
            {
                Material.load(filepath + tS.substring(7));
            }
            
        }
        
        s.close();
        
        
        try {
            s = new Scanner(new File(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        ArrayList<Vertex> vertArray = new ArrayList<Vertex>(0);
        ArrayList<Vertex> normArray = new ArrayList<Vertex>(0);
        ArrayList<Vertex> texArray = new ArrayList<Vertex>(0);
        ArrayList<Face> faceArray = new ArrayList<Face>(0);
        
        int i = 0;
        int c = 0;
        while(s.hasNextLine())
        {
            if(c>1000){
                Display.setTitle("Parsing vertices: line " + String.valueOf(i) + "/" + t);
                c = 0;
            }
            i++;
            c++;
            String tS = s.nextLine();
            Vertex v = parseVertex(tS);
            Vertex vn = parseNormal(tS);
            Vertex vt = parseTex(tS);
            
            if(v!=null)
            {
                vertArray.add(v);
            }
            if(vn!=null)
            {
                normArray.add(vn);
            }
            if(vt!=null)
            {
                texArray.add(vt);
            }
            //System.out.println(vertArray.size());
        }
        
        s.close();
        
        try {
            s = new Scanner(new File(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        int currMtl = -1;
        
        i=0;
        c=0;
        
        int lib = Material.matLibs.size()-1;
        
        while(s.hasNextLine())
        {
            if(c>1000){
                Display.setTitle("Parsing faces: line " + String.valueOf(i) + "/" + t);
                c = 0;
            }
            c++;
            i++;
            String tS = s.nextLine();
            if(tS.startsWith("usemtl")){
                currMtl = matchMtl(tS.substring(7));
            }
            int[] vertIndArray = parseFace(tS);
                if(vertIndArray!=null)
                {
                    Vertex[] temp = new Vertex[]{vertArray.get(vertIndArray[0]-1),vertArray.get(vertIndArray[1]-1),vertArray.get(vertIndArray[2]-1)};
                    Vertex[] texTemp = new Vertex[]{texArray.get(vertIndArray[3]-1),texArray.get(vertIndArray[4]-1),texArray.get(vertIndArray[5]-1)};
                    Vertex[] normTemp = new Vertex[]{normArray.get(vertIndArray[6]-1),normArray.get(vertIndArray[7]-1),normArray.get(vertIndArray[8]-1)};
                    Face f = new Face(temp,normTemp,currMtl,texTemp);
                    //System.out.println("Face Added");
                    faceArray.add(f);
                
                }
        }
        s.close();
        Face[] fA = faceArray.toArray(new Face[0]);
        return new DrawObject(fA,lib);        
    }
    
    public Vertex parseVertex(String tS)
    {
        try{
            if((tS.charAt(0) == 'v')&&(tS.charAt(1)==' '))
            {
            //Scanner s = new Scanner(tS);
            //s.skip("v");
            //double x = s.nextDouble();
            //double y = s.nextDouble();
            //double z = s.nextDouble();
                double[] temp = getNextThreeDoubles(tS);
                return new Vertex(temp[0],temp[1],temp[2]);
            }
        }
        catch (Exception e){
            //Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public Vertex parseNormal(String tS)
    {
        try{
            if((tS.charAt(0) == 'v')&&(tS.charAt(1)=='n'))
            {
                //Scanner s = new Scanner(tS);
                //s.skip("vn");
                //double x = s.nextDouble();
                //double y = s.nextDouble();
                //double z = s.nextDouble();
            
            
                double[] temp = getNextThreeDoubles(tS);
                return new Vertex(temp[0],temp[1],temp[2]);
                //return new Vertex(x,y,z);
            }
        }
        catch (Exception e){}
        return null;
    }
    
    public Vertex parseTex(String tS)
    {
        try{if((tS.charAt(0) == 'v')&&(tS.charAt(1)=='t'))
        {
            //Scanner s = new Scanner(tS);
            //s.skip("vt");
            //double x = s.nextDouble();
            //double y = s.nextDouble();
            double[] temp = getNextTwoDoubles(tS);
            
            
            return new Vertex(temp[0],temp[1],0);
        }
        }
        catch (Exception e){
            //Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public int[] parseFace(String tS)
    {
        try{if((tS.charAt(0) == 'f'))
        {
            Scanner s = new Scanner(tS);
            
            s.skip("f");
            
            s.useDelimiter(" |/");
            int v1 = s.nextInt();
            int vt1 = s.nextInt();
            int vn1 = s.nextInt();
            int v2 = s.nextInt();
            int vt2 = s.nextInt();
            int vn2 = s.nextInt();
            int v3 = s.nextInt();
            int vt3 = s.nextInt();
            int vn3 = s.nextInt();
            
            return new int[] {v1,v2,v3,vt1,vt2,vt3,vn1,vn2,vn3};
        }}
        catch (Exception e){}
        return null;
    }
    
    
    
    public void drawCube()
    {
        glBegin(GL_QUADS);
        FloatBuffer temp = BufferUtils.createFloatBuffer(4);
        temp.put(new float[]{.0f,.3f,.3f,1f});
        temp.rewind();
        //glMaterial(GL_FRONT, GL_AMBIENT, temp);
        temp.put(new float[]{.6f,0f,0f,.1f});
        temp.rewind();
        glMaterial(GL_FRONT, GL_DIFFUSE, temp);
        
        glNormal3f( 0.0f, 0.0f, 1.0f);                  // Normal Pointing Towards Viewer
        glVertex3f(-1.0f, -1.0f,  1.0f);  // Point 1 (Front)
        glVertex3f( 1.0f, -1.0f,  1.0f);  // Point 2 (Front)
        glVertex3f( 1.0f,  1.0f,  1.0f);  // Point 3 (Front)
        glVertex3f(-1.0f,  1.0f,  1.0f);  // Point 4 (Front)
        // Back Face
        glNormal3f( 0.0f, 0.0f,-1.0f);                  // Normal Pointing Away From Viewer
        glVertex3f(-1.0f, -1.0f, -1.0f);  // Point 1 (Back)
        glVertex3f(-1.0f,  1.0f, -1.0f);  // Point 2 (Back)
        glVertex3f( 1.0f,  1.0f, -1.0f);  // Point 3 (Back)
        glVertex3f( 1.0f, -1.0f, -1.0f);  // Point 4 (Back)
        // Top Face
        glNormal3f( 0.0f, 1.0f, 0.0f);                  // Normal Pointing Up
        glVertex3f(-1.0f,  1.0f, -1.0f);  // Point 1 (Top)
        glVertex3f(-1.0f,  1.0f,  1.0f);  // Point 2 (Top)
        glVertex3f( 1.0f,  1.0f,  1.0f);  // Point 3 (Top)
        glVertex3f( 1.0f,  1.0f, -1.0f);  // Point 4 (Top)
        // Bottom Face
        glNormal3f( 0.0f,-1.0f, 0.0f);                  // Normal Pointing Down
        glVertex3f(-1.0f, -1.0f, -1.0f);  // Point 1 (Bottom)
        glVertex3f( 1.0f, -1.0f, -1.0f);  // Point 2 (Bottom)
        glVertex3f( 1.0f, -1.0f,  1.0f);  // Point 3 (Bottom)
        glVertex3f(-1.0f, -1.0f,  1.0f);  // Point 4 (Bottom)
        // Right face
        glNormal3f( 1.0f, 0.0f, 0.0f);                  // Normal Pointing Right
        glVertex3f( 1.0f, -1.0f, -1.0f);  // Point 1 (Right)
        glVertex3f( 1.0f,  1.0f, -1.0f);  // Point 2 (Right)
        glVertex3f( 1.0f,  1.0f,  1.0f);  // Point 3 (Right)
        glVertex3f( 1.0f, -1.0f,  1.0f);  // Point 4 (Right)
        // Left Face
        glNormal3f(-1.0f, 0.0f, 0.0f);                  // Normal Pointing Left
        glVertex3f(-1.0f, -1.0f, -1.0f);  // Point 1 (Left)
        glVertex3f(-1.0f, -1.0f,  1.0f);  // Point 2 (Left)
        glVertex3f(-1.0f,  1.0f,  1.0f);  // Point 3 (Left)
        glVertex3f(-1.0f,  1.0f, -1.0f);  // Point 4 (Left)
    
        glEnd();
    }

    private int matchMtl(String substring) 
    {
        for(int i = 0; i < Material.matLibs.size();i++){
            ListIterator<Material> mIt = Material.matLibs.get(i).listIterator();
            while(mIt.hasNext()){
                if(mIt.next().ref.matches(substring))
                    return mIt.previousIndex();
            }
        }
        System.out.println("Material not matched: "+substring);
        return -1;
    }
    
    static double[] getNextThreeDoubles(String string) {
            String[] parsed = string.split(" +");
            return new double[] { 
                Double.parseDouble(parsed[1]),
                Double.parseDouble(parsed[2]),
                Double.parseDouble(parsed[3]) };
    }
    
    static double[] getNextTwoDoubles(String string) {
            String[] parsed = string.split(" +");
            return new double[] { 
                Double.parseDouble(parsed[1]),
                Double.parseDouble(parsed[2])};
    }
}