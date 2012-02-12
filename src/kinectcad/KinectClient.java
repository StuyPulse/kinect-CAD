/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kinectcad;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.*;

/**
 *
 * @author George
 */
public class KinectClient {
    
    
    Socket s;
    BufferedInputStream dIn;
    BufferedOutputStream dOut;
    public boolean isGrabbed;
    
    public KinectClient()
    {
            s = new Socket();
            isGrabbed = false;
    }
    
    public void connect(InetSocketAddress ip)
    {
        try{
            System.out.println("Connecting...");
            s.connect(ip,2000);
            dIn = new BufferedInputStream(s.getInputStream());
            //dOut = s.getOutputStream();
            
            System.out.println("Successful.");
        }
        catch (IOException ex)
        {
            Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE,"Sockets not connecting", ex);
        }
    }
    
    public boolean isAvailable()
    {
        try {
            return dIn.available()>=9;
        } catch (IOException ex) {
            Logger.getLogger(KinectClient.class.getName()).log(Level.WARNING, null, ex);
            return false;
        }
    }
    
    public float[] getInput()
    {
        float x = 0;
        float y = 0;
        int pro = 0;
        int con = 0;
        
        byte[] temp = null;
        while((temp = read()) != null)
        {
            //System.out.println(getXCoord(temp));
            x += getXCoord(temp);
            y += getYCoord(temp);
            System.out.println(printBits(temp[0]));
            System.out.println(printBits((byte)(temp[0]|0x7f))+" "+ printBits((byte)0xFF));
            if((temp[0]&0x80)==0x80)
            {
                pro++;
            }
            else
            {
                con++;
            }
        }
        isGrabbed = pro>=con;
        return new float[]{x,y};
    }
    
    static String printBits(byte x)
    {
        char[] c = new char[8];
            for (int i = 0; i < 8; i++)
            {
                c[7-i] = String.valueOf((x >> i) & 0x1).charAt(0);
            }
            return String.copyValueOf(c) +" ";
    }
    
    static String printBits(int x)
    {
        String[] s = new String[4];
            for (int i = 0; i < 4; i++)
            {
                s[3-i] = printBits((byte)(x >> (i*8)));
            }
            return s[0]+s[1]+s[2]+s[3]+'*';
    }
    
    public byte[] read()
    {
        byte[] temp = new byte[9];
        if(!isAvailable())
            return null;
        try {
            dIn.read(temp);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE,"Sockets not readin\'", ex);
            return null;
        }
        return temp;
    }
    
    public static float getXCoord(byte[] data)
    {
        //System.out.println(
        //   printBits(data[4])+
        //   printBits(data[3])+
        //   printBits(data[2])+
        //   printBits(data[1]));
        //int p = parseByte(data, 1);
        
        //System.out.print(
        //    printBits((byte)(p>>24))+
        //    printBits((byte)(p>>16))+
        //    printBits((byte)(p>>8))+
        //    printBits((byte)p));
        //System.out.println(String.valueOf(p));
        //return (float)p/100000;
        return (float)parseByte(data, 1)/100000;
    }
    
    public static float getYCoord(byte[] data)
    {
        return (float)parseByte(data, 5)/100000;
    }
    
    public static int parseByte(byte[] b, int start)
    {
        int temp;
        int a1 =  b[start]          &0xff;
        int a2 = (b[start+1] << 8)  &0xff00;
        int a3 = (b[start+2] << 16) &0xff0000;
        int a4 = (b[start+3] << 24) &0xff000000;
        //System.out.println(printBits(a1));
        //System.out.println(printBits(a2));
        //System.out.println(printBits(a3));
        //System.out.println(printBits(a4));
        temp = a1|a2|a3|a4;
        return temp;
    }
    
    public void close()
    {
        try {
            dIn.close();
            dOut.close();
            s.close();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(KinectCAD.class.getName()).log(Level.SEVERE,"Sockets not closin\'",ex);
        }
    }
    
}
