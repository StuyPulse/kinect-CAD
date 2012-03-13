using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Kinect;

namespace WindowsFormsApplication1
{
    class JointTracker
    {
        SkeletonPoint[] vs;
        public const int BUFFERSIZE = 100;
        public const int BUFFERSTEP = 25;
        int currIndex;

        public JointTracker()
        {
            currIndex = 0;
            vs = new SkeletonPoint[BUFFERSIZE];
        }

        public void recPoint(SkeletonPoint v)
        {
            currIndex++;
            vs[currIndex] = v;
            checkAndSolveOverflow();
        }

        public SkeletonPoint position()
        {
            return vs[currIndex];
        }

        public static double slope(SkeletonPoint v)
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
                SkeletonPoint[] temp = new SkeletonPoint[BUFFERSIZE];
                Array.Copy(vs, BUFFERSTEP-1, temp, 0, BUFFERSIZE - BUFFERSTEP);
                vs = temp;
                currIndex = BUFFERSIZE - BUFFERSTEP -1;
            }
        }

        public SkeletonPoint averageVel(int framesBack, float decayPercent)
        {
            if (framesBack > BUFFERSIZE - BUFFERSTEP || framesBack > currIndex)
            {
                //Console.WriteLine("not enough position data");
                return zeroVec();
            }


                float averageFactor = 0;
                float ratio = 1;
                SkeletonPoint sum = zeroVec();
                for (int i = 0; i < framesBack; i++)
                {
                    vecAdd(ref sum, vecMult(ratio, vecSubt(vs[currIndex - i],vs[currIndex-i-1])));
                    averageFactor += ratio;
                    ratio *= decayPercent;
                }

                return vecMult(1 / averageFactor, sum);
            
        }

        public SkeletonPoint lastDistance()
        {
            return vecSubt(vs[currIndex], vs[currIndex - 1]);
        }

        public static SkeletonPoint vecMult(float coEffish, SkeletonPoint vec)
        {
            vec.X *= coEffish;
            vec.Y *= coEffish;
            vec.Z *= coEffish;
            return vec;
        }

        public static void vecMult(float coEffish, ref  SkeletonPoint vec)
        {
            vec.X *= coEffish;
            vec.Y *= coEffish;
            vec.Z *= coEffish;
        }

        public static void vecAdd(ref SkeletonPoint a, SkeletonPoint b)
        {
            a.X += b.X;
            a.Y += b.Y;
            a.Z += b.Z;
        }

        public static SkeletonPoint vecAdd(SkeletonPoint a, SkeletonPoint b)
        {
            a.X += b.X;
            a.Y += b.Y;
            a.Z += b.Z;
            return a;
        }

        public static void vecSubt(ref SkeletonPoint a, SkeletonPoint b)
        {
            a.X -= b.X;
            a.Y -= b.Y;
            a.Z -= b.Z;
        }

        public static SkeletonPoint vecSubt(SkeletonPoint a, SkeletonPoint b)
        {
            a.X -= b.X;
            a.Y -= b.Y;
            a.Z -= b.Z;
            return a;
        }

        public static SkeletonPoint zeroVec()
        {
            SkeletonPoint temp = new SkeletonPoint();
            temp.X = 0;
            temp.Y = 0;
            temp.Z = 0;
            return temp;
        }
    }
}
