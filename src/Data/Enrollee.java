/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

/**
 *
 * @author OrthoCube
 */
public class Enrollee {
    int eid;
    int classid;
    int studentid;
    int classcard;
    String notes;
    
    public Enrollee(int e, int c, int s, int cc, String n)
    {
        eid = e;
        classid = c;
        studentid = s;
        classcard = cc;
        notes = n;
    }
    
    public int getEnrolleeID()
    {
        return eid;
    }
    
    public int getClassID()
    {
        return classid;
    }
    
    public int getStudentID()
    {
        return studentid;
    }
    
    public int getClassCard()
    {
        return classcard;
    }
    
    public String getNotes()
    {
        return notes;
    }
}
