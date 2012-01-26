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
    String material;
    
    public Face(Vertex[] v)
    {
        vertices = v;
        normal = null;
    }
    
    public Face(Vertex[] v, Vertex[] Normal)
    {
        vertices = v;
        normal = Normal;
    }
    
    public Face(Vertex[] v, Vertex[] Normal, String m)
    {
        vertices = v;
        normal = Normal;
        material = m;
    }
    
    public void draw()
    {
        glBegin(GL_POLYGON);
        
        for(int i = 0;i<Array.getLength(vertices);i++)
        {
            if(normal!=null)
        glNormal3d(normal[i].x, normal[i].y, normal[i].z);
            glVertex3d(vertices[i].x, vertices[i].y, vertices[i].z);
        }
        glEnd();
    }
    
}
