
package kinectcad;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.io.*;
import java.util.Scanner;
import java.lang.Character;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.ListIterator;
import org.lwjgl.BufferChecks;
import org.lwjgl.BufferUtils;

public class KinectCAD
{
    
    public FloatBuffer lightAmb;
    public FloatBuffer lightDiff;
    public FloatBuffer lightPos;
    
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
	    e.printStackTrace();
	    System.exit(0);
	}
        
        glEnable(GL_DEPTH_TEST); 
        
        
        
        double angleX = 0;
        double angleY = 0;

	glClearColor(0.0f,0.0f,0.0f,1.0f);

        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // fovy, aspect ratio, zNear, zFar
        org.lwjgl.util.glu.GLU.gluPerspective(30f, 1f, 1f, 100f);
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
        
        
        glLight(GL_LIGHT1, GL_AMBIENT, lightAmb);     
        glLight(GL_LIGHT2,GL_DIFFUSE,lightDiff);
        glLight(GL_LIGHT2,GL_POSITION,lightPos);
        glEnable(GL_LIGHT1);
        glEnable(GL_LIGHT2);
        glEnable(GL_LIGHTING);
        
        
        DrawObject o = loadObj("C:\\Users\\George\\Desktop\\\\kinectCadfiles\\Bench.obj");
	//DrawObject o =null;
        
	while (!Display.isCloseRequested()) {
	
            
	    drawScene(angleX,angleY,o);
                
            int scale = 1;
            
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
	    Display.update();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
		
	Display.destroy();
    }
	
    public void drawScene(double angleX,double angleY,DrawObject o)
    {
        
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);     // Clear The Screen And The Depth Buffer
    glLoadIdentity();     
    glTranslated(0,0,-3);
    glRotated(angleY,0,1,0);
    glRotated(angleX,1,0,0);
    glScaled(.5,.5,.5);
    	
   
    
    o.draw();
    //drawCube(); 
    
    
    glLoadIdentity();
    glTranslated(0, 0, 0);
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
        
        while(s.hasNextLine())
        {
            String tS = s.nextLine();
            if(tS.startsWith("mtllib"))
            {
                Material.load("C:\\Users\\George\\Desktop\\\\kinectCadfiles\\" + tS.substring(7));
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
        ArrayList<Face> faceArray = new ArrayList<Face>(0);
        
        
        
        while(s.hasNextLine())
        {
            String tS = s.nextLine();
            Vertex v = parseVertex(tS);
            Vertex vn = parseNormal(tS);
            
            if(v!=null)
            {
                vertArray.add(v);
            }
            if(vn!=null)
            {
                normArray.add(vn);
            }
            
            
        }
        
        s.close();
        
        try {
            s = new Scanner(new File(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        int currMtl = -1;
        while(s.hasNextLine())
        {
            String tS = s.nextLine();
            if(tS.startsWith("usemtl")){
                currMtl = matchMtl(tS.substring(7));
            }
            int[] vertIndArray = parseFace(tS);
                if(vertIndArray!=null)
                {
                    Vertex[] temp = new Vertex[]{vertArray.get(vertIndArray[0]-1),vertArray.get(vertIndArray[1]-1),vertArray.get(vertIndArray[2]-1)};
                    Vertex[] normTemp = new Vertex[]{normArray.get(vertIndArray[3]-1),normArray.get(vertIndArray[4]-1),normArray.get(vertIndArray[5]-1)};
                    Face f = new Face(temp,normTemp,currMtl);
                    faceArray.add(f);
                
                }
        }
        s.close();
        Face[] fA = faceArray.toArray(new Face[0]);
        return new DrawObject(fA);        
    }
    
    public Vertex parseVertex(String tS)
    {
        try{if((tS.charAt(0) == 'v')&&(tS.charAt(1)==' '))
        {
            Scanner s = new Scanner(tS);
            s.skip("v");
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            
            
            return new Vertex(x,y,z);
        }
        }
        catch (Exception e){}
        return null;
    }
    
    public Vertex parseNormal(String tS)
    {
        try{if((tS.charAt(0) == 'v')&&(tS.charAt(1)=='n'))
        {
            Scanner s = new Scanner(tS);
            s.skip("vn");
            double x = s.nextDouble();
            double y = s.nextDouble();
            double z = s.nextDouble();
            
            
            return new Vertex(x,y,z);
        }
        }
        catch (Exception e){}
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
            s.skip("/?[0-9]*");
            int vn1 = s.nextInt();
            int v2 = s.nextInt();
            s.skip("/?[0-9]*");
            int vn2 = s.nextInt();
            int v3 = s.nextInt();
            s.skip("/?[0-9]*");
            int vn3 = s.nextInt();
            
            return new int[] {v1,v2,v3,vn1,vn2,vn3};
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
        ListIterator<Material> mIt = Material.materials.listIterator();
        while(mIt.hasNext()){
            if(mIt.next().ref.matches(substring))
                return mIt.previousIndex();
        }
        System.out.println("Material not matched");
        return -1;
    }
}