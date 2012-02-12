using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Research.Kinect.Nui;

namespace WindowsFormsApplication1
{
    class JointTracker
    {
        Vector[] vs;
        public const int BUFFERSIZE = 100;
        public const int BUFFERSTEP = 25;
        int currIndex;

        public JointTracker()
        {
            currIndex = 0;
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
            double t = Math.Sqrt(Math.Pow(v.X,2) + Math.Pow(v.Z,2));
            //Console.WriteLine(v.Y+" "+t);
            if (t == 0)
                t = .0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001;
            return v.Y / t;

        }

        private void checkAndSolveOverflow()
        {
            if (currIndex+1 == BUFFERSIZE)
            {
                Vector[] temp = new Vector[BUFFERSIZE];
                Array.Copy(vs, BUFFERSTEP-1, temp, 0, BUFFERSIZE - BUFFERSTEP);
                vs = temp;
                currIndex = BUFFERSIZE - BUFFERSTEP -1;
            }
        }

        public Vector averageVel(int framesBack, float decayPercent)
        {
            if (framesBack > BUFFERSIZE - BUFFERSTEP || framesBack > currIndex)
            {
                Console.WriteLine("not enough position data");
                return zeroVec();
            }


                float averageFactor = 0;
                float ratio = 1;
                Vector sum = zeroVec();
                for (int i = 0; i < framesBack; i++)
                {
                    vecAdd(ref sum, vecMult(ratio, vecSubt(vs[currIndex - i],vs[currIndex-i-1])));
                    averageFactor += ratio;
                    ratio *= decayPercent;
                }

                return vecMult(1 / averageFactor, sum);
            
        }

        public Vector lastDistance()
        {
            return vecSubt(vs[currIndex], vs[currIndex - 1]);
        }

        public static Vector vecMult(float coEffish, Vector vec)
        {
            vec.X *= coEffish;
            vec.Y *= coEffish;
            vec.Z *= coEffish;
            return vec;
        }

        public static void vecMult(float coEffish,ref  Vector vec)
        {
            vec.X *= coEffish;
            vec.Y *= coEffish;
            vec.Z *= coEffish;
        }

        public static void vecAdd(ref Vector a, Vector b)
        {
            a.X += b.X;
            a.Y += b.Y;
            a.Z += b.Z;
        }

        public static Vector vecAdd(Vector a, Vector b)
        {
            a.X += b.X;
            a.Y += b.Y;
            a.Z += b.Z;
            return a;
        }

        public static void vecSubt(ref Vector a, Vector b)
        {
            a.X -= b.X;
            a.Y -= b.Y;
            a.Z -= b.Z;
        }

        public static Vector vecSubt(Vector a, Vector b)
        {
            a.X -= b.X;
            a.Y -= b.Y;
            a.Z -= b.Z;
            return a;
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
}
