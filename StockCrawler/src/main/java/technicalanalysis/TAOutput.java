/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package technicalanalysis;

import java.util.Random;

/**
 *
 * @author gtri
 */
public class TAOutput 
{
    public double[] values;
    public String name;
    
    public TAOutput(String name, double[] dvalues)
    {
        this.name = name;
        this.values = dvalues;
    }
    
    public TAOutput(String name, int[] ivalues)
    {
        this.name = name;
        this.values = new double[ivalues.length];
        
        for(int i=0;i<ivalues.length;i++)
        {
            this.values[i] = ivalues[i];
        }
    }
	
	public boolean hasData()
	{
		if(values.length == 0)
		{
			return false;
		}
		
		boolean ret = false;
		Random rand = new Random();
		
		for(int i=0;i<100;i++)
		{
			int index = rand.nextInt();
			if(index < 0)
				index = -index;
			
			index %= values.length;
			
			if(values[index] != 0)
			{
				ret = true;
			}
		}
		
		return ret;
	}
}
