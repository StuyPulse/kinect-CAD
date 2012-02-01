/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kinectcad;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;

/**
 *
 * @author George
 */
class Timer {
    long frameTime;
    long lastTime;
    int frames;
    long timeSinceFPS;
    public int fps;
    
    public Timer(int f)
    {
        frameTime = (long)(1/(double)f*1000000000);
        timeSinceFPS = 0;
        fps = 0;
    }
    
    public void start()
    {
        lastTime = System.nanoTime();
    }
    
    public double getDelay()
    {
        if(fps!=0)
        return 1/(double)fps;
        return 0;
    }
    
    public void burnExcess()
    {
        long elapsed = System.nanoTime() - lastTime;
        if(elapsed<frameTime)
            try {
            Thread.sleep((frameTime-elapsed)/1000000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
        }
        else
        {
            //lastTime += elapsed - frameTime;
        }
        
        start();
        
        frames+=1;
        timeSinceFPS+=Math.max(elapsed,frameTime);
        if(timeSinceFPS>=1000000000){
            timeSinceFPS-=1000000000;
            fps=frames;
            frames = 0;
        }   
    }
}
