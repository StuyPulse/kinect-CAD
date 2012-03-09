namespace WindowsFormsApplication1
{
    partial class cameraForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.trackingL = new System.Windows.Forms.Label();
            this.timer1 = new System.Windows.Forms.Timer(this.components);
            this.connectionL = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // pictureBox1
            // 
            this.pictureBox1.Location = new System.Drawing.Point(12, 12);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(640, 480);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.pictureBox1.TabIndex = 0;
            this.pictureBox1.TabStop = false;
            // 
            // trackingL
            // 
            this.trackingL.AutoSize = true;
            this.trackingL.Location = new System.Drawing.Point(33, 513);
            this.trackingL.Name = "trackingL";
            this.trackingL.Size = new System.Drawing.Size(82, 13);
            this.trackingL.TabIndex = 1;
            this.trackingL.Text = "Tracking Status";
            // 
            // timer1
            // 
            this.timer1.Enabled = true;
            this.timer1.Interval = 50;
            this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
            // 
            // connectionL
            // 
            this.connectionL.AutoSize = true;
            this.connectionL.Location = new System.Drawing.Point(278, 513);
            this.connectionL.Name = "connectionL";
            this.connectionL.Size = new System.Drawing.Size(94, 13);
            this.connectionL.TabIndex = 2;
            this.connectionL.Text = "Connection Status";
            // 
            // cameraForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(96F, 96F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi;
            this.ClientSize = new System.Drawing.Size(667, 550);
            this.Controls.Add(this.connectionL);
            this.Controls.Add(this.trackingL);
            this.Controls.Add(this.pictureBox1);
            this.Name = "cameraForm";
            this.ShowInTaskbar = false;
            this.Text = "KinectCad Server";
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Label trackingL;
        private System.Windows.Forms.Timer timer1;
        private System.Windows.Forms.Label connectionL;

    }
}

