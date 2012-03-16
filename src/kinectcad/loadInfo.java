package kinectcad;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author George
 */
public class loadInfo {
    public String file;
    public double[] scale;// = new double[]{1,1,1};
    public double[] offset;// = new double[]{0,0,0};
    public double[] rotate;// = new double[]{0,0,0};
    
    public loadInfo(String ref)
    {
        file = ref;
        offset = new double[]{0,0,0};
        rotate = new double[]{0,0,0};
        scale = new double[]{1,1,1};
    }
    
    public loadInfo(String ref, double[] o,double[] r,double[] s)
    {
        file = ref;
        offset = o;
        rotate = r;
        scale = s;
                
    }
}
