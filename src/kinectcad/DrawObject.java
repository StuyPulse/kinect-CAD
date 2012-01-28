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
import org.lwjgl.util.Display;
import static org.lwjgl.opengl.GL11.*;
import java.util.Arrays;
import org.lwjgl.util.glu.GLU;

public class DrawObject {
    
    Face[] faceArray;
    
    public DrawObject(Face[] faces)
    {
        faceArray = faces;
    }
    
    public void draw()
    {
        int currentMat = -1;
        for(int i = 0;i<Array.getLength(faceArray);i++)
        {
            if(faceArray[i].currentMat != currentMat && faceArray[i].currentMat>=0)
            {
                glMaterial(GL_FRONT,GL_AMBIENT,Material.materials.get(faceArray[i].currentMat).ABuff);
                glMaterial(GL_FRONT,GL_DIFFUSE,Material.materials.get(faceArray[i].currentMat).DBuff);
                glMaterial(GL_FRONT,GL_SPECULAR,Material.materials.get(faceArray[i].currentMat).SBuff);
                
            }
            faceArray[i].draw();
        }
    }
}
