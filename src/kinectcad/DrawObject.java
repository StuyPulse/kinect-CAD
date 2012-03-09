/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kinectcad;

/**
 *
 * @author George
 */

import static org.lwjgl.opengl.GL11.*;

public class DrawObject {
    
    Face[] faceArray;
    int matLib;
    double[] offset;
    double[] rotate;
    double[] scale = new double[]{1,1,1};
    
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
        glScaled(scale[0], scale[1], scale[2]);
        glRotated(rotate[0], 1, 0, 0);
        glRotated(rotate[1], 0, 1, 0);
        glRotated(rotate[2], 0, 0, 1);
        int currentMat = -1;
        for(int i = 0;i<faceArray.length;i++)
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
        glRotated(rotate[2], 0, 0, 1);
        glRotated(rotate[1], 0, 1, 0);
        glRotated(rotate[0], 1, 0, 0);
        glScaled(1/scale[0], 1/scale[1], 1/scale[2]);
        glTranslated(-1*offset[0], -1*offset[1], -1*offset[2]);
    }
    
    public void offset(double x, double y, double z)
    {
        offset[0]+=x;
        offset[1]+=y;
        offset[2]+=z;
    }
    
    public void rotate(double x, double y, double z)
    {
        rotate[0]+=x;
        rotate[1]+=y;
        rotate[2]+=z;
    }
    
    public void scale(double scaleF)
    {
        scale = new double[]{scaleF,scaleF,scaleF};
    }
    
    public void scale(double scaleX, double scaleY, double scaleZ)
    {
        scale = new double[]{scaleX,scaleY,scaleZ};
    }
}
