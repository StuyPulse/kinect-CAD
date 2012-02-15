/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kinectcad;

/**
 *
 * @author George
 */
public class PhysicsSim {
    public float angleX,angleY,inertia,airRes,spinSpeed,springConst,grabFriction,velX,velY;
    public boolean isGrabbed;
    public float bounceX, bounceY, stretchX, stretchY;
    
    public PhysicsSim(float i,float a, float spr,float speed,float gfric)
    {
        inertia = i;
        grabFriction = gfric;
        airRes = a;
        springConst = spr;
        spinSpeed = speed;
        isGrabbed = false;
        bounceX = bounceY = stretchX = stretchY = 0;
    }
    
    public void applyForce(float fx,float fy, float t)
    {
        fx = fx/inertia*t;
        fy = fy/inertia*t;
        velX += fx;
        velY += fy;
    }
    
    public void grab()
    {
        isGrabbed = true;
        bounceX = angleX;
        bounceY = angleY;
        stretchX = angleX;
        stretchY = angleY;
    }
    
    public void applyAirRes(float delay)
    {
        float tempDecay = airRes * delay *velX;
        velX -= tempDecay;
        tempDecay = airRes * delay * velY;
        velY -= tempDecay;
    }
    
    public void applyGrabFric(float delay)
    {
        float tempDecay = grabFriction * delay *velX;
        velX -= tempDecay;
        tempDecay = grabFriction * delay * velY;
        velY -= tempDecay;
    }
    
    public float getLength(float x, float y)
    {
        return (float)Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }
    
    public void update(float dX, float dY,boolean grab,float delay)
    {
        
        //if(dX>0||dX<0)
        //    dX = dX+1-1;
        if(grab)
        {
            if(!isGrabbed)
            {
                grab();
            }
            bounceX += velX*delay;
            bounceY += velY*delay;
            stretchX += dX*delay*spinSpeed;
            stretchY += dY*delay*spinSpeed;
            applyForce((stretchX-bounceX)*springConst ,(stretchY-bounceY)*springConst , delay);
            applyGrabFric(delay);
            //System.out.println("input: " + dX + "\tanchor: " + bounceX + "\tend: " + stretchX);
        }
        else
        {
            if(isGrabbed)
            {
                isGrabbed = false;
            }
        }
        applyAirRes(delay);
        angleX += velX;
        angleY += velY;
        //if(dX>0||dX<0)
        //System.out.println("input: " + dX + "\tvel: " + velX + "\tangle: " + angleX);
    }
}
