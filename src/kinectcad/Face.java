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

public class Face {
    
    Vertex[] vertices; 
    Vertex[] normal;
    Vertex[] tex;
    public int currentMat;
    
    public Face(Vertex[] v)
    {
        vertices = v;
        normal = null;
        currentMat = -1;
        tex = null;
    }
    
    public Face(Vertex[] v, Vertex[] Normal)
    {
        vertices = v;
        normal = Normal;
        currentMat = -1;
        tex = null;
    }
    
    public Face(Vertex[] v, Vertex[] Normal, int m)
    {
        vertices = v;
        normal = Normal;
        currentMat = m;
        tex = null;
    }
    
    public Face(Vertex[] v, Vertex[] Normal, int m, Vertex[] textureCoords)
    {
        vertices = v;
        normal = Normal;
        currentMat = m;
        tex = textureCoords;
    }
    
    public void draw(boolean flag)
    {
        glBegin(GL_POLYGON);
        
        
        
        for(int i = 0;i<Array.getLength(vertices);i++)
        {
            //int x;
            //if(i == 3)
            //    x = 1;
            if(normal!=null)
                glNormal3d(normal[i].x, normal[i].y, normal[i].z);
            if(tex != null&&flag)
            {
                glTexCoord2d(tex[i].x, tex[i].y);
            }
            glVertex3d(vertices[i].x, vertices[i].y, vertices[i].z);
        }
        glEnd();
    }
    
}















