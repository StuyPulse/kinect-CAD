
package kinectcad;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.io.*;
import java.net.Inet4Address;
import java.util.Scanner;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.ListIterator;
import org.lwjgl.BufferUtils;
import java.net.InetSocketAddress;


public class KinectCAD
{
    public FloatBuffer lightAmb;
    public FloatBuffer lightDiff;
    public FloatBuffer lightPos;
    
    double turnSpeed = 50; //50
    double transSpeed = 10;
    double depthSpeed = 10; //100
    
    double angleX = 0;
    double angleY = 0;
    double transX = 0;
    double transY = 0;
    double transZ = 10;
    public KinectClient sock;
    public PhysicsSim phys;
    
    public static final String filepath = "C:\\Users\\George\\Desktop\\kinectCadfiles\\";
    public static final String file = "cube4.obj";
    public static final String altFile = "bench.obj";
    public static final boolean firstPerson = false;
    public static final boolean cameraInertia = true;
    public static final boolean loadKinect = false;
    public static final boolean limitAngleTo90 = false;
    public static final boolean is3D = true;
    
    public static float xTurnVel = 0;
    public static float yTurnVel = 0;
    
    public static final float inertia = .1f;
    public static final float unGrabbedDecayRate = .5f;
    public static final float springConst = 4f;
    public static final float grabFriction = 8f;
    public static final float spinSpeed = 10f;
    
    
    public static void main(String[] args)
    {
        KinectCAD mainDerp = new KinectCAD();
        
        mainDerp.start();
       
    }
    
    public void start()
    {
        phys = new PhysicsSim(inertia, unGrabbedDecayRate, springConst, spinSpeed,grabFriction);
        if(loadKinect){
            sock = new KinectClient(10, 0.7f);
            try {
               sock.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 20736));
            } catch (UnknownHostException ex) {
                Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(!firstPerson){
            transZ = 0;
        }
        //BitTester.init();
        //for(int i = 0; i<100000;i++)
        //BitTester.Fabricate();
        
        try {
	    Display.setDisplayMode(new DisplayMode(800,600));
	    Display.create();
	} catch (LWJGLException e) {
	    System.exit(0);
	}
        
        glEnable(GL_DEPTH_TEST); 
        glEnable(GL_NORMALIZE);
        
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
        
        //temp.offset(-75, -8, 50);
        
        DrawObject main = loadObj(filepath + file);
        DrawObject[] o;
        if(!"".equals(altFile))
        {
            DrawObject alt = loadObj(filepath + altFile);
            o = new DrawObject[]{main,alt};
        }
        else{
            o = new DrawObject[]{main};
        }
        System.out.println("Loaded in " + (System.currentTimeMillis()-x) + " milliseconds.");
	//DrawObject o =null;
        Timer timer = new Timer(500);
        timer.start();
	while (!Display.isCloseRequested()) {
	
            
	    drawScene(angleX,angleY,new double[]{transX,transY,transZ},o);
            
            double scale = turnSpeed; //50
            double scaleT = transSpeed;
            double scaleZ = depthSpeed; //100
            
            scale*=timer.getDelay();
            scaleT*=timer.getDelay();
            scaleZ*=timer.getDelay();
            angleY= angleY%360;
            angleX= angleX%360;
            
            double tempX = 0;
            double tempY = 0;
            double tempZ = 0;
            
            
            //System.out.println(angleX);
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            {
                angleX+=scale;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            {
                angleX-=scale;     
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
                
                angleY+=scale;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
                angleY-=scale;      
            }
            if(firstPerson)
            {
                if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
                {
                    tempY-=scaleT;                
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                {
                        tempY+=scaleT;                
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_A))
                {
                    tempX+=scaleT;                
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_D))
                {
                    tempX-=scaleT;                
                }
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_W))
            {
                tempZ+=scaleZ;                
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S))
            {
                tempZ-=scaleZ;
            }
            
            if(firstPerson){
                double[] temp = cameraCoordToAbsolute(tempX,tempY,tempZ,angleX,angleY);   //FPS STYLE
                transX+=temp[0];
                transY+=temp[1];
                transZ+=temp[2];
            }
            else{
                transX+=tempX;  //ORBIT STYLE
                transY+=tempY;
                transZ+=tempZ;
            }
            
            Display.setTitle("FPS: " + String.valueOf(timer.fps));
	    Display.update();
            
            
            if(loadKinect){
                float[] kinectIn = sock.getInput();
                if(cameraInertia)
                {
                    phys.update(kinectIn[0], kinectIn[1], sock.isGrabbed, (float)timer.getDelay());
                    angleX = phys.angleX;
                    angleY = phys.angleY;           
                }
                else
                {
                    if(sock.isGrabbed){
                        angleX+=50*scale*kinectIn[0];
                        angleY+=55*scale*kinectIn[1];
                    }
                    else
                    {
                        sock.flushSmoothData();
                    }
                }
            }
            
            if(limitAngleTo90){
                if(angleY<-90)
                    angleY=-90;    
                if(angleY>90)
                    angleY=90;
            }
            
            
            timer.burnExcess();
        }
		
	Display.destroy();
    }
    
    public void drawScene(double angleX,double angleY,double[] trans ,DrawObject[] o)
    {
    if(is3D)
    glColorMask(true, false, false, false);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);     // Clear The Screen And The Depth Buffer
    glLoadIdentity();     
    	
    if(firstPerson){
        glRotated(angleY,-1,0,0);
        glRotated(angleX,0,-1,0);
        glTranslated(trans[0],trans[1],trans[2]);
    }
    else{
        glTranslated(trans[0],trans[1],-6+trans[2]);
        glRotated(angleY,1,0,0);
        glRotated(angleX,0,1,0);
    }
   
    int l = o.length;
    for(int i =0; i<l;i++){
    o[i].draw();
    }
    
         
    	
    if(is3D){
        
        glColorMask(false, true, true, false);
        glClear(GL_DEPTH_BUFFER_BIT);  
        glLoadIdentity();
        
        if(firstPerson){
            glRotated(angleY,-1,0,0);
            glRotated(angleX,0,-1,0);
            glTranslated(-1*trans[0],-1*trans[1],-1*trans[2]);
        }
        else{
            double[] temp = cameraCoordToAbsoluteAllAxes(.05, 0, 0, angleX, angleY);

            glTranslated(trans[0],trans[1],-6+trans[2]);
            glRotated(angleY,1,0,0);
            glRotated(angleX,0,1,0);
            glTranslated(temp[0],temp[1],temp[2]);
            glRotated(-2,0 , 1, 0);
        }

        for(int i =0; i<l;i++){
        o[i].draw();
        }

        glLoadIdentity();
    }
    
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
            
            Vertex[] vertTemp = null;
            Vertex[] texTemp = null;
            Vertex[] normTemp = null;
            
            int[][] vertIndArray = parseFace(tS);
            if(vertIndArray!=null)
            {
                int l =vertIndArray[0].length;
                if(vertIndArray[0]!=null){
                    vertTemp = new Vertex[l];
                    for(int j = 0; j < l;j++)
                    {
                        vertTemp[j] = vertArray.get(vertIndArray[0][j]-1);
                    }
                }
                if(vertIndArray[1]!=null){
                    texTemp = new Vertex[l];
                    for(int j = 0; j < l;j++)
                    {
                        texTemp[j] = texArray.get(vertIndArray[1][j]-1);
                    }
                }
                if(vertIndArray[2]!=null){
                    normTemp = new Vertex[l];
                    for(int j = 0; j < l;j++)
                    {
                        normTemp[j] = normArray.get(vertIndArray[2][j]-1);
                    }
                }
                    //vertTemp = new Vertex[]{vertArray.get(vertIndArray[0][0]-1),vertArray.get(vertIndArray[0][1]-1),vertArray.get(vertIndArray[0][2]-1)};}
                //if(vertIndArray[1]!=null){
                //    texTemp = new Vertex[]{texArray.get(vertIndArray[1][0]-1),texArray.get(vertIndArray[1][1]-1),texArray.get(vertIndArray[1][2]-1)};}
                //if(vertIndArray[2]!=null){
                //    normTemp = new Vertex[]{normArray.get(vertIndArray[2][0]-1),normArray.get(vertIndArray[2][1]-1),normArray.get(vertIndArray[2][2]-1)};}
                    
                Face f = new Face(vertTemp,normTemp,currMtl,texTemp);
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
    
    public int[][] parseFace(String tS)
    {
        try{
            if((tS.charAt(0) == 'f'))
            {
                //Scanner s = new Scanner(tS);
            
                //s.skip("f");
            
                //s.useDelimiter(" |/");
                //int v1 = s.nextInt();
                //int vt1 = s.nextInt();
                //int vn1 = s.nextInt();
                //int v2 = s.nextInt();
                //int vt2 = s.nextInt();
                //int vn2 = s.nextInt();
                //int v3 = s.nextInt();
                //int vt3 = s.nextInt();
                //int vn3 = s.nextInt();
            
                //return new int[] {v1,v2,v3,vt1,vt2,vt3,vn1,vn2,vn3};
                String[] vertIndGroups = tS.split(" ");
                int l = vertIndGroups.length;
                int[] vertArray = new int[l-1];
                int[] normArray = new int[l-1];
                int[] texArray = new int[l-1];
                for(int i =1;i<l;i++)
                {
                    String[] temp = vertIndGroups[i].split("/");
                    int tl = temp.length;
                    vertArray[i-1] = Integer.parseInt(temp[0]);
                    if(tl>1&&!"".equals(temp[1])&&texArray!=null)
                    {
                        texArray[i-1] = Integer.parseInt(temp[1]);
                    }
                    else
                        texArray=null;
                    if(tl>2&&normArray!=null)
                    {
                        normArray[i-1] = Integer.parseInt(temp[2]);
                    }
                    else
                        normArray=null;
                }
                return new int[][]{vertArray,texArray,normArray};
            }
        }
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

    private double[] cameraCoordToAbsolute(double tempX, double tempY, double tempZ, double angleX, double angleY) {
        //Converts coords relative to a camera into absolute coords
        
        double rad = 2 * 3.141592653589793238462643383279502884 / 360;
        double[] temp= new double[3];
        temp[0]+= -1*tempX * Math.cos(rad*angleX);
        temp[0]+= tempZ * Math.cos(rad*(angleY)) * Math.cos(rad*(90-angleX));
        
        temp[1]+= -1*tempY;
        temp[1]+= -1*tempZ * Math.cos(rad*(90 - angleY));
        
        temp[2]+= tempX * Math.cos(rad*(90-angleX));
        temp[2]+= tempZ * Math.cos(rad*(angleY)) * Math.cos(rad*angleX);   
        return temp;
    }
    
    private double[] cameraCoordToAbsoluteAllAxes(double tempX, double tempY, double tempZ, double angleX, double angleY) {
        //Converts coords relative to a camera into absolute coords
        
        double rad = 2 * 3.141592653589793238462643383279502884 / 360;
        double[] temp= new double[3];
        temp[0]+= -1*tempX * Math.cos(rad*angleX);
        temp[0]+= -1*tempZ * Math.cos(rad*(angleY)) * Math.cos(rad*(90-angleX));
        temp[0]+= tempY * Math.cos(rad*(90+angleY)) * Math.cos(rad*(90-angleX));
        
        temp[1]+= tempY * Math.sin(rad*(angleY-90));
        temp[1]+= tempZ * Math.sin(rad*(angleY));
        
        temp[2]+= -1*tempX * Math.cos(rad*(90-angleX));
        temp[2]+= tempZ * Math.cos(rad*(angleY)) * Math.cos(rad*angleX);   
        temp[2]+= tempY * Math.cos(rad*(90+angleY)) * Math.cos(rad*(angleX));
        
        return temp;
    }
}