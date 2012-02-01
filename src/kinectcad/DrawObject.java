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
    
    public DrawObject(Face[] faces)
    {
        faceArray = faces;
        matLib = -1;
    }
    
    public DrawObject(Face[] faces, int lib)
    {
        faceArray = faces;
        matLib = lib;
    }
    
    public void draw()
    {
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
    }
}
