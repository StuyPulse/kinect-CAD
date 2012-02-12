/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kinectcad;

import java.util.Random;

/**
 *
 * @author George
 */
public class BitTester {
    public static Random rand;
    
    public static void init()
    {
        rand = new Random();
    }
    
    public static void Fabricate()
    {
        KinectClient.getXCoord(fabricateData());
    }
    
    static byte[] fabricateData()
    {
        byte[] temp = new byte[9];
        rand.nextBytes(temp);
        temp[0] = (byte)0xff;
        return temp;
    }
    
}
