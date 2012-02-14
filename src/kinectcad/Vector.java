/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kinectcad;

import java.io.Console;

/**
 *
 * @author George
 */
public class Vector {
    public float X,Y,Z;
    
    public Vector()
    {}
    
    public Vector(float x, float y, float z)
    {
        X = x;
        Y = y;
        Z = z;
    }
    
    public Vector(float x, float y)
    {
        X = x;
        Y = y;
        Z = 0;
    }
    
    public float[] xyArray()
    {
        return new float[]{X,Y};
    }
    
    public void print()
    {
        System.out.println(X+"\t"+Y+"\t"+Z);
    }
    
    public boolean equals(Vector v)
    {
        return (X ==v.X)&&(Y==v.Y)&&(Z==v.Z);
    }
}
