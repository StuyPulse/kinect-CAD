/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kinectcad;

import java.util.Arrays;

/**
 *
 * @author George
 */
public class KinectSmoother {

    Vector[] vs;
    public final int BUFFERSIZE = 100;
    public final int BUFFERSTEP = 25;
    int currIndex;

    public KinectSmoother()
    {
        currIndex = -1;
        vs = new Vector[BUFFERSIZE];
    }

    public void recPoint(Vector v)
    {

        currIndex++;
        vs[currIndex] = v;
        checkAndSolveOverflow();
    }

    public Vector position()
    {
        return vs[currIndex];
    }

    public static double slope(Vector v)
    {
        double t = Math.sqrt(Math.pow(v.X, 2) + Math.pow(v.Z,2));
        //Console.WriteLine(v.Y+" "+t);
        if (t == 0)
            t = .00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001;
        return v.Y / t;

    }

    private void checkAndSolveOverflow()
    {
        if (currIndex+1 == BUFFERSIZE)
        {
            Vector[] temp = new Vector[BUFFERSIZE];
            Arrays.fill(temp, zeroVec());
            for(int i = 0; i<BUFFERSIZE-BUFFERSTEP ;i++)
            {
                temp[i] = vs[i+BUFFERSTEP-1];
            }
                //Arrays.(vs, BUFFERSTEP-1, temp, 0, BUFFERSIZE - BUFFERSTEP);
            vs = temp;
            currIndex = BUFFERSIZE - BUFFERSTEP -1;
        }
    }

    public void flushSmoothData()
    {
        Arrays.fill(vs, zeroVec());
    }
    
    public Vector averageVel(int framesBack, float decayPercent)
    {
        if (framesBack > BUFFERSIZE - BUFFERSTEP || framesBack > currIndex)
        {
            System.out.println("not enough position data");
            return zeroVec();
        }


            float averageFactor = 0;
            float ratio = 1;
            Vector sum = zeroVec();
            for (int i = 0; i < framesBack; i++)
            {
                sum = vecAdd(sum, vecMult(ratio, vecSubt(vs[currIndex - i],vs[currIndex-i-1])));
                averageFactor += ratio;
                ratio *= decayPercent;
            }

            return vecMult(1 / averageFactor, sum);

    }

    public Vector smoothDist(int framesBack, float decayPercent)
    {
        if (framesBack > BUFFERSIZE - BUFFERSTEP || framesBack > currIndex)
        {
            System.out.println("not enough position data");
            return zeroVec();
        }


            float averageFactor = 0;
            float ratio = 1;
            Vector sum = zeroVec();
            for (int i = 0; i < framesBack; i++)
            {
                //if(!vs[currIndex - 1].equals(zeroVec()))
                //vs[currIndex - i].print();
                sum = vecAdd(sum, vecMult(ratio, vs[currIndex - i]));
                //System.out.println(vs[currIndex-i].X + " \t!" + sum.X + " r:"+ ratio+" a:"+averageFactor);
                //sum.print();
                averageFactor += ratio;
                ratio *= decayPercent;
            }

            //System.out.print("*");
            //vecMult(1 / averageFactor, sum).print();
            return vecMult(1 / averageFactor, sum);

    }
    
    public Vector lastDistance()
    {
        return vecSubt(vs[currIndex], vs[currIndex - 1]);
    }

    public static Vector vecMult(float coEffish, Vector vec)
    {
        Vector temp = zeroVec();
        temp.X = vec.X * coEffish;
        temp.Y = vec.Y * coEffish;
        temp.Z = vec.Z * coEffish;
        return temp;
    }

    public static void vecMultByRef(float coEffish,Vector vec)
    {
        vec.X *= coEffish;
        vec.Y *= coEffish;
        vec.Z *= coEffish;
    }

    public static void vecAddByRef(Vector a, Vector b)
    {
        a.X += b.X;
        a.Y += b.Y;
        a.Z += b.Z;
    }

    public static Vector vecAdd(Vector a, Vector b)
    {
        Vector temp = zeroVec();
        temp.X = a.X + b.X;
        temp.Y = a.Y + b.Y;
        temp.Z = a.Z + b.Z;
        return temp;
    }

    public static void vecSubtByRef(Vector a, Vector b)
    {
        a.X -= b.X;
        a.Y -= b.Y;
        a.Z -= b.Z;
    }

    public static Vector vecSubt(Vector a, Vector b)
    {
        Vector temp = zeroVec();
        temp.X = a.X - b.X;
        temp.Y = a.Y - b.Y;
        temp.Z = a.Z - b.Z;
        return temp;
    }

    public static Vector zeroVec()
    {
        Vector temp = new Vector();
        temp.X = 0;
        temp.Y = 0;
        temp.Z = 0;
        return temp;
    }
}
