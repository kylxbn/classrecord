/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import java.awt.image.BufferedImage;

/**
 *
 * @author orthocube
 */
public class Student {
    int ID;
    String SID, FN, MN, LN, sex, contact, address, notes;
    BufferedImage img;
    
    public Student(int cid, String csid, String cfn, String cmn, String cln, String csex, String ccontact, String caddress, String cnotes, BufferedImage cimg)
    {
        ID = cid;
        SID = csid;
        FN = cfn;
        MN = cmn;
        LN = cln;
        sex = csex;
        contact = ccontact;
        address = caddress;
        notes = cnotes;
        img = cimg;
    }
    
    public int getID()
    {
        return ID;
    }
    
    public String getSID()
    {
        return SID;
    }
    
    public String getFN()
    {
        return FN;
    }
    
    public String getMN()
    {
        return MN;
    }
    
    public String getLN()
    {
        return LN;
    }
    
    public String getSex()
    {
        return sex;
    }
    
    public String getContact()
    {
        return contact;
    }
    
    public String getAddress()
    {
        return address;
    }
    
    public String getNotes()
    {
        return notes;
    }
    
    public BufferedImage getImg()
    {
        return img;
    }
    
    public String getFNLN()
    {
        return FN + " " + LN;
    }
}
