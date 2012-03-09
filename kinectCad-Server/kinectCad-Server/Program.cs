using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using Microsoft.Research.Kinect.Nui;
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
        static PlanarImage vid;
        static Bitmap b;
        static Runtime nui;
        static JointTracker hand, elbow;
        static Socket listenS, clientS;
        static bool socketOpen = false;
        static int currSkelly;
        static int PORT = 20736;

        

        [STAThread]
        static void Main()
        {
            b = new Bitmap(640, 480, PixelFormat.Format32bppArgb);

            hand = new JointTracker();
            elbow = new JointTracker();
            nui = Runtime.Kinects[0];
            nui.Initialize(RuntimeOptions.UseSkeletalTracking|RuntimeOptions.UseColor);


            
            nui.SkeletonFrameReady += new EventHandler<SkeletonFrameReadyEventArgs>(SkeletonFrameReady);
            nui.VideoFrameReady += new EventHandler<ImageFrameReadyEventArgs>(FrameReady);


            //nui.DepthStream.Open(ImageStreamType.Depth, 4, ImageResolution.Resolution80x60, ImageType.DepthAndPlayerIndex);
            nui.VideoStream.Open(ImageStreamType.Video, 4, ImageResolution.Resolution640x480, ImageType.Color);

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
            if ((object)vid == null || vid.Bits == null)
            {
                return;
            }
            b = PImageToBitmap(vid);

            SkeletonFrame skellies = e.SkeletonFrame;
            //if(skellies.Skeletons[0].TrackingState.ToString()!="NotTracked")
            //Console.WriteLine(skellies.Skeletons[0].TrackingState.ToString());
            SkeletonData[] skelly = skellies.Skeletons;
            JointsCollection j;
            bool isReady = false;
            if (skelly[currSkelly].TrackingState == SkeletonTrackingState.PositionOnly || skelly[currSkelly].TrackingState == SkeletonTrackingState.Tracked)
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
                    if (skelly[currSkelly].TrackingState == SkeletonTrackingState.PositionOnly || skelly[0].TrackingState == SkeletonTrackingState.Tracked)
                    {
                        isReady = true;
                        f.setTrackingL("Target Acquired");
                        break;
                    }
                }
            }

            if(isReady)
            {
                j = skelly[currSkelly].Joints;
                Joint h;
                float xd, yd;

                h = j[JointID.ElbowRight];
                findJoint(h.Position, out xd, out yd);
                drawPoint((int)xd, (int)yd);
                elbow.recPoint(h.Position);

                h = j[JointID.HandRight];
                findJoint(h.Position, out xd, out yd);
                drawPoint((int)xd, (int)yd);
                hand.recPoint(h.Position);


                Vector temp = hand.averageVel(50, .6f);
                xd = temp.X;
                yd = temp.Y;

                //findJoint(temp, out xd, out yd,false);
                Vector output;

                processArm(hand, elbow, out output);
                sendData();
            }
            f.draw(b);
        }

        public static void processArm(JointTracker hand, JointTracker elbow,out Vector output)
        {
            output = JointTracker.zeroVec();
            double s = JointTracker.slope(JointTracker.vecSubt(hand.position(),elbow.position()));
            
            if(s>-.4 && s<.19)
            {
                //Console.Write("Slope good: ");
                JointTracker.vecAdd(ref output, hand.lastDistance());
            }
            //Console.WriteLine(s);
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

        public static void findJoint(Vector v,out float xd, out float yd)
        {
            

            nui.SkeletonEngine.SkeletonToDepthImage(
                      v, out xd, out yd);
            xd = Math.Max(2, Math.Min(xd * 640, 637));
            yd = Math.Max(2, Math.Min(yd * 480, 477));
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

        static void FrameReady(object sender, ImageFrameReadyEventArgs e)
        {
            vid = e.ImageFrame.Image;

        }

        static void nui_DepthFrameReady(object sender, ImageFrameReadyEventArgs e)
        {
            
            //if ((object)vid == null || vid.Bits == null) return;
            //b = PImageToBitmap(vid);
            if (b == null) return;


            PlanarImage image = e.ImageFrame.Image;
            int[] depth = new int[image.Width * image.Height];
            int[] player = new int[image.Width * image.Height];
            for (int i = 0; i < depth.Length; i++)
            {
                player[i] = image.Bits[i * 2] & 0x07;
                depth[i] = (image.Bits[i * 2 + 1] << 5) | (image.Bits[i * 2] >> 3);
                //player[i] = 0;
                //depth[i] = (image.Bits[i * 2 + 1] << 8) | (image.Bits[i * 2]);
            }

            Bitmap overlay = intToImage(depth, player, image.Width, image.Height);
            for (int ih = 0; ih < overlay.Height; ih++)
            {
                for (int iw = 0; iw < overlay.Width; iw++)
                {
                    Color c = overlay.GetPixel(iw, ih);
                    b.SetPixel(iw, ih, c);
                    
                   
                }
            }

            

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

        static Bitmap PImageToBitmap(PlanarImage PImage)
        {
            Bitmap bmap = new Bitmap(
                 PImage.Width,
                 PImage.Height,
                 PixelFormat.Format32bppRgb);
            BitmapData bmapdata = bmap.LockBits(
                 new Rectangle(0, 0, PImage.Width,
                                   PImage.Height),
                 ImageLockMode.WriteOnly,
                 bmap.PixelFormat);
            IntPtr ptr = bmapdata.Scan0;
            Marshal.Copy(PImage.Bits,0,ptr,PImage.Width *PImage.BytesPerPixel *PImage.Height);
            bmap.UnlockBits(bmapdata);
            return bmap;
        }
    }
}
