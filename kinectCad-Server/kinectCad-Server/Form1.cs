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
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            
        }

        public void draw(Bitmap b)
        {
            pictureBox1.Image = b;
        }


        private void Form1_Click(object sender, EventArgs e)
        {
            Program.resetTracker();
        }


    }

}
