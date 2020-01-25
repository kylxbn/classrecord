/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import java.util.ArrayList;

/**
 *
 * @author orthocube
 */

//ID     NUMBER PRIMARY KEY NOT NULL
//    Name   TEXT
//    SY     NUMBER
//    Sem    NUMBER
//    Course TEXT
//    Room   TEXT
//    Sched  TEXT
//    Level  TEXT
//    C1     TEXT
//    P1     NUMBER


public class Class {
    int id;
    String name;
    int sy;
    int sem;
    int year;
    String course;
    String room;
    String sched;
    String level;
    ArrayList<Criteria> criterias;
    
    public Class(int i, String n, int csy, int csem, int cyear, String ccourse, String croom, String csched, String clevel, ArrayList<Criteria> c)
    {
        id = i;
        name = n;
        sy = csy;
        sem = csem;
        year = cyear;
        course = ccourse;
        room = croom;
        sched = csched;
        level = clevel;
        criterias = c;
    }
    
    public int getID()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getSchoolYear()
    {
        return sy;
    }
    
    public int getSem()
    {
        return sem;
    }
    
    public int getYear()
    {
        return year;
    }
    
    public String getCourse()
    {
        return course;
    }
    
    public String getRoom()
    {
        return room;
    }
    
    public String getSched()
    {
        return sched;
    }
    
    public String getLevel()
    {
        return level;
    }
    
    public ArrayList<Criteria> getCriterias()
    {
        return criterias;
    }
    
    public void setCriterias(ArrayList<Criteria> c)
    {
        criterias = c;
    }
}
