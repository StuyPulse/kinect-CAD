/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kinectcad;

/**
 *
 * @author George
 */

import java.lang.reflect.Array;
import static org.lwjgl.opengl.GL11.*;

public class DrawObject {
    
    Face[] faceArray;
    int matLib;
    double[] offset;
    double[] rotate;
    
    public DrawObject(Face[] faces)
    {
        faceArray = faces;
        matLib = -1;
        offset = new double[]{0,0,0};
        rotate = new double[]{0,0,0};
    }
    
    public DrawObject(Face[] faces, int lib)
    {
        faceArray = faces;
        matLib = lib;
        
        offset = new double[]{0,0,0};
        rotate = new double[]{0,0,0};
    }
    
    public void draw()
    {
        glTranslated(offset[0], offset[1], offset[2]);
        int currentMat = -1;
        for(int i = 0;i<Array.getLength(faceArray);i++)
        {
            if(faceArray[i].currentMat != currentMat && faceArray[i].currentMat>=0 && matLib!=-1)
            {
                glMaterial(GL_FRONT_AND_BACK,GL_AMBIENT,Material.matLibs.get(matLib).get(faceArray[i].currentMat).ABuff);
                glMaterial(GL_FRONT_AND_BACK,GL_DIFFUSE,Material.matLibs.get(matLib).get(faceArray[i].currentMat).DBuff);
                glMaterial(GL_FRONT_AND_BACK,GL_SPECULAR,Material.matLibs.get(matLib).get(faceArray[i].currentMat).SBuff);
                Material.matLibs.get(matLib).get(faceArray[i].currentMat).bindTexture();
            }
            faceArray[i].draw();
        }
        glTranslated(-1*offset[0], -1*offset[1], -1*offset[2]);
    }
    
    public void offset(double x, double y, double z)
    {
        offset[0]+=x;
        offset[1]+=y;
        offset[2]+=z;
    }
}
