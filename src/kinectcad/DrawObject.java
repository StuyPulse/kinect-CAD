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

public class DrawObject {
    
    Face[] faceArray;
    
    public DrawObject(Face[] faces)
    {
        faceArray = faces;
    }
    
    public void draw()
    {
        for(int i = 0;i<Array.getLength(faceArray);i++)
        {
            faceArray[i].draw();
        }
    }
}
