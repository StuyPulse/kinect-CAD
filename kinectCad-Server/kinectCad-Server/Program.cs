using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using Microsoft.Kinect;
using System.Drawing;
using System.Drawing.Imaging;
using System.Threading;
using System.Runtime.InteropServices;
using System.Net.Sockets;
using System.Net;
namespace WindowsFormsApplication1
{
    class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        /// 
        static cameraForm f;
        static Bitmap vid;
        static DepthImageFrame depth;
        static Bitmap b;
        static KinectSensor myK;
        static JointTracker hand, elbow;
        static Socket listenS, clientS;
        static bool socketOpen = false;
        static bool imageSyncBlocked = false;
        static int currSkelly;
        static int PORT = 20736;

        

        [STAThread]
        static void Main()
        {
            b = new Bitmap(640, 480, PixelFormat.Format32bppArgb);

            hand = new JointTracker();
            elbow = new JointTracker();
            myK = KinectSensor.KinectSensors[0];
            myK.ColorStream.Enable(ColorImageFormat.RgbResolution640x480Fps30);
            myK.SkeletonStream.Enable();
            myK.DepthStream.Enable(DepthImageFormat.Resolution320x240Fps30);
            
            myK.Start();
            
            myK.SkeletonFrameReady += new EventHandler<SkeletonFrameReadyEventArgs>(SkeletonFrameReady);
            myK.ColorFrameReady += new EventHandler<ColorImageFrameReadyEventArgs>(ColorImageFrameReady);
            myK.DepthFrameReady += new EventHandler<DepthImageFrameReadyEventArgs>(DepthFrameReady);


            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            f = new cameraForm();

            beginListening();
            f.setConnectionL("Listening on port " + PORT);

            Application.Run(f);
        }

        public static void beginListening()
        {
            listenS = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            listenS.Bind(new IPEndPoint(IPAddress.Any, PORT));
            listenS.Listen(4);
            listenS.BeginAccept(new AsyncCallback(OnCallAccept), null);
        }

        public static void OnCallAccept(IAsyncResult async)
        {
            try
            {
                clientS = listenS.EndAccept(async);
                f.setConnectionL("Call Accepted");
                socketOpen = true;
            }

            catch (Exception e)
            {
                f.setConnectionL(e.ToString());
            }

        }

        static void SkeletonFrameReady(object sender, SkeletonFrameReadyEventArgs e)
        {
            if ((object)vid == null)
            {
                return;
            }
            while (imageSyncBlocked) ;
            imageSyncBlocked = true;
            b = PImageToBitmap(vid);
            imageSyncBlocked = false;

            SkeletonFrame skellyIn = e.OpenSkeletonFrame();
            Skeleton[] skellies = new Skeleton[skellyIn.SkeletonArrayLength];
            skellyIn.CopySkeletonDataTo(skellies);
            //if(skellies.Skeletons[0].TrackingState.ToString()!="NotTracked")
            //Console.WriteLine(skellies.Skeletons[0].TrackingState.ToString());
            JointCollection j;
            bool isReady = false;
            if (skellies[currSkelly].TrackingState == SkeletonTrackingState.PositionOnly || skellies[currSkelly].TrackingState == SkeletonTrackingState.Tracked)
            {
                isReady = true;
            }
            else
            {
                f.setTrackingL("Seeking target...");
                for (int i = 0; i < 6; i++)
                {
                    currSkelly++;
                    currSkelly = i;
                    if (skellies[currSkelly].TrackingState == SkeletonTrackingState.PositionOnly || skellies[currSkelly].TrackingState == SkeletonTrackingState.Tracked)
                    {
                        isReady = true;
                        f.setTrackingL("Target Acquired");
                        break;
                    }
                }
            }

            if(isReady)
            {
                j = skellies[currSkelly].Joints;
                Joint h;
                float xd, yd;

                h = j[JointType.ElbowRight];
                findJoint(h.Position, out xd, out yd);
                drawPoint((int)xd, (int)yd);
                //elbow.recPoint(h.Position);

                h = j[JointType.HandRight];
                findJoint(h.Position, out xd, out yd);
                drawPoint((int)xd, (int)yd);
                hand.recPoint(h.Position);


                SkeletonPoint temp = hand.averageVel(50, .6f);
                xd = temp.X;
                yd = temp.Y;
                sendData();
            }
            draw(f);
            skellyIn.Dispose();
        }

        static void draw(cameraForm f)
        {
            Bitmap temp;
            temp = b;
            short[] data = new short[depth.PixelDataLength];
            depth.CopyPixelDataTo(data);
            for (int y = 0; y < depth.Height; y++)
            {
                for (int x = 0; x < depth.Width; x++)
                {
                    short s = data[(depth.Width*y+x)];
                    Color col = temp.GetPixel(2*x,2*y);
                    if((s&0x0007)!=0)
                    {
                        col = Color.FromArgb(col.R,col.G,(col.B+255)/2);
                        temp.SetPixel(2 * x, 2 * y, col);
                    }
                }
            }
            f.draw(temp);
        }

        public static void processArm(JointTracker hand, JointTracker elbow, out SkeletonPoint output)
        {
            output = JointTracker.zeroVec();
            double s = JointTracker.slope(JointTracker.vecSubt(hand.position(),elbow.position()));
            
            if(s>-.4 && s<.19)
            {
                JointTracker.vecAdd(ref output, hand.lastDistance());
            }
            //Console.WriteLine(s);
        }

        static void DepthFrameReady(object sender,DepthImageFrameReadyEventArgs e)
        {
            depth = e.OpenDepthImageFrame();
        }

        public static void sendData()
        {
            if (clientS != null)
            {
                double s = JointTracker.slope(JointTracker.vecSubt(hand.position(), elbow.position()));
                bool grab = false;
                if (s > -1.5 && s < .19)
                {
                    grab = true;
                }

                int handX, handZ;

                handX = (int)(hand.lastDistance().X*1000000);
                handZ = (int)(hand.lastDistance().Z*1000000);
                //Console.WriteLine(handX);
                byte[] temp = new byte[9];
                temp[0] = 0;
                if(grab)
                temp[0] = 0x80;
                temp[1] = (byte)(handX );
                temp[2] = (byte)(handX >> 8);
                temp[3] = (byte)(handX >> 16);
                temp[4] = (byte)(handX >> 24);
                temp[5] = (byte)(handZ );
                temp[6] = (byte)(handZ >> 8);
                temp[7] = (byte)(handZ >> 16);
                temp[8] = (byte)(handZ >> 24);
                //Console.WriteLine(getBits(temp[4])+getBits(temp[3])+getBits(temp[2])+getBits(temp[1])+handX);
                if(socketOpen)
                {
                    try
                    {
                        clientS.Send(temp);
                    }
                    catch (SocketException)
                    {
                        f.setConnectionL("Connection lost, listening on port "+PORT);
                        socketOpen = false;
                        //listenS.Bind(new IPEndPoint(IPAddress.Any, 20736));
                        listenS.Listen(4);
                        listenS.BeginAccept(new AsyncCallback(OnCallAccept), null);
                    }
                }
            }

        }

        public static String getBits(byte b)
        {
            String[] c = new String[8];
            for (int i = 0; i < 8; i++)
            {
                c[7-i] = char.ToString( (char)(( (b >> i) & 0x1 )+48) );
            }
            return String.Concat(c)+" ";
        }

        public static void findJoint(SkeletonPoint v,out float xd, out float yd)
        {
            ColorImagePoint temp = myK.MapSkeletonPointToColor(v, ColorImageFormat.RgbResolution640x480Fps30);
            xd = temp.X;
            yd = temp.Y;
            //xd = Math.Max(2, Math.Min(xd * 640, 637));
            //yd = Math.Max(2, Math.Min(yd * 480, 477));
        }


        public static void drawPoint(int xd, int yd)
        {
            Graphics g = Graphics.FromImage(b);
            g.FillEllipse(Brushes.Red, xd - 5, yd - 5, 10, 10);
            g.FillEllipse(Brushes.LimeGreen, xd-3, yd-3, 6, 6);
        }

        public static void drawPoint(int xd, int yd,Brush c)
        {
            Graphics g = Graphics.FromImage(b);
            g.FillEllipse(Brushes.Black, xd - 5, yd - 5, 10, 10);
            g.FillEllipse(c, xd - 3, yd - 3, 6, 6);
        }

        static void ColorImageFrameReady(object sender, ColorImageFrameReadyEventArgs e)
        {
            while (imageSyncBlocked);
            imageSyncBlocked = true;
            ColorImageFrame temp = e.OpenColorImageFrame();
            vid = frameToImage(temp);
            imageSyncBlocked = false;
            temp.Dispose();
        }

        public static Bitmap frameToImage(ColorImageFrame f)
        {
            byte[] pixeldata =
                     new byte[f.PixelDataLength];
            f.CopyPixelDataTo(pixeldata);
            Bitmap bmap = new Bitmap(
                   f.Width,
                   f.Height,
                   PixelFormat.Format32bppRgb);
            BitmapData bmapdata = bmap.LockBits(
              new Rectangle(0, 0,
                         f.Width, f.Height),
              ImageLockMode.WriteOnly,
              bmap.PixelFormat);
            IntPtr ptr = bmapdata.Scan0;
            Marshal.Copy(pixeldata, 0, ptr,
                       f.PixelDataLength);
            bmap.UnlockBits(bmapdata);
            return bmap;
        }

        public static Bitmap intToImage(int[] depthA, int[] playerA, int w, int h)
        {
            Bitmap bT = new Bitmap(w, h, PixelFormat.Format32bppPArgb);


            for(int ih = 0;ih<h;ih++)
            {
                for (int iw = 0; iw < w; iw++)
                {
                    int temp = depthA[ih * w + iw];
                    int tempP = playerA[ih * w + iw];
                    if (temp != 0 || tempP!=0)
                    {
                        bT.SetPixel(iw, ih, Color.FromArgb(255 - 255 * Math.Max(1, temp) / 8192, 0, tempP * 32));
                    }
                    else
                    {
                        bT.SetPixel(iw, ih, Color.FromArgb(100,255,100));
                    }
                }
            }

            return bT;
        }

        static Bitmap PImageToBitmap(Bitmap input)
        {
            return input;
        }
    }
}
