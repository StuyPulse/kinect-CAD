using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace WindowsFormsApplication1
{
    public partial class cameraForm : Form
    {

        public String trackingLText = "";
        public String connectionLText = "";

        public cameraForm()
        {
            InitializeComponent();
        }

        public void draw(Bitmap b)
        {
            try
            {
                pictureBox1.Image = b;
            }
            catch (Exception)
            {
            }
        }


        public void setTrackingL(String text)
        {
            trackingLText = text;
        }

        public void setConnectionL(String text)
        {
            connectionLText = text;
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            trackingL.Text = trackingLText;
            connectionL.Text = connectionLText;
        }


    }

}
