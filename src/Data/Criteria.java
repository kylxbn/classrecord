/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

/**
 *
 * @author orthocube
 */
public class Criteria {
    String name;
    int    percentage;
    int    terms;
    
    public Criteria(String n, int p, int t)
    {
        name = n;
        percentage = p;
        terms = t;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getPercentage()
    {
        return percentage;
    }
    
    public boolean appears1()
    {
        return (terms&1)>0;
    }
    
    public boolean appears2()
    {
        return (terms&2)>0;
    }
    
    public boolean appears3()
    {
        return (terms&4)>0;
    }
    
    public boolean appears4()
    {
        return (terms&8)>0;
    }
}
