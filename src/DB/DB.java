/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.sql.*;

import Data.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author orthocube
 */
public class DB {
    private static Connection con = null;
    private static ResultSet r = null;
    private static Statement s = null;
    
    public static boolean init() throws SQLException
    {
        String url = "jdbc:sqlite:database.db";
        con = DriverManager.getConnection(url);
        s = con.createStatement();
        s.executeUpdate("CREATE TABLE IF NOT EXISTS Users (UserID INTEGER PRIMARY KEY NOT NULL, Username TEXT, Password TEXT, Caps INTEGER);");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS Students (StudentID INTEGER PRIMARY KEY NOT NULL, SID TEXT, FN TEXT, MN TEXT, LN TEXT, Sex TEXT, Contact TEXT, Address TEXT, Notes TEXT, Picture BLOB);");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS Classes (ClassID NUMBER PRIMARY KEY NOT NULL, Name TEXT, SY NUMBER, Sem NUMBER, Year NUMBER, Course TEXT, Room TEXT, Sched TEXT, Level TEXT);");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS Criterias (CriteriaID NUMBER PRIMARY KEY NOT NULL, ClassID NUMBER, Name TEXT, Percent NUMBER, Terms INT);");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS CriteriasSHS (CriteriaID NUMBER PRIMARY KEY NOT NULL, ClassID NUMBER, Name TEXT, Percent NUMBER, Terms INT);");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS Enrollees (EnrolleeID NUMBER PRIMARY KEY NOT NULL, ClassID NUMBER, StudentID NUMBER, ClassCard NUMBER, Notes TEXT);");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS SHSEnrollees (EnrolleeID NUMBER PRIMARY KEY NOT NULL, ClassID NUMBER, StudentID NUMBER, Notes TEXT);");
        
        r = s.executeQuery("SELECT COUNT(*) AS Count FROM Users;");
        r.next();
        int users = r.getInt("Count");
        if (users == 0)
        {
            s.executeUpdate("INSERT INTO Users VALUES (1, 'admin', 'admin', 7);");
        }        
        return users == 0;
    }
    
    public static int userExists(String u, String p) throws SQLException
    {
        r = s.executeQuery("SELECT UserID FROM Users WHERE Username = '" + u + "' AND Password = '" + p + "';");
        if (r.next())
        {
            return r.getInt("UserID");
        }
        return 0;
    }
    
    public static int userExists(String u) throws SQLException
    {
        r = s.executeQuery("SELECT UserID FROM Users WHERE Username = '" + u + "';");
        if (r.next())
        {
            return r.getInt("UserID");
        }
        return 0;
    }
    
    public static int userExists(int i, String u) throws SQLException
    {
        r = s.executeQuery("SELECT UserID FROM Users WHERE UserID = " + Integer.toString(i) + " AND Username = '" + u + "';");
        if (r.next())
        {
            return r.getInt("UserID");
        }
        return 0;
    }
    
    public static int userExists(int i) throws SQLException
    {
        r = s.executeQuery("SELECT UserID FROM Users WHERE UserID = " + Integer.toString(i) + ";");
        if (r.next())
        {
            return r.getInt("UserID");
        }
        return 0;
    }
    
    public static int getUserPermissions(int u) throws SQLException
    {
        r = s.executeQuery("SELECT Caps FROM Users WHERE ID = " + Integer.toString(u) + ";");
        if (r.next())
        {
            return r.getInt("Caps");
        }
        return -1;
    }
    
    public static ArrayList<User> getUsers() throws SQLException
    {
        ArrayList<User> u;
        u = new ArrayList<>();

        r = s.executeQuery("SELECT * FROM Users;");
        while (r.next())
            u.add(new User(r.getInt("UserID"), r.getString("Username"), r.getString("Password"), r.getInt("Caps")));
        return u;
    }
    
    public static User getUser(String u) throws SQLException
    {
        User us = null;
        r = s.executeQuery("SELECT * FROM Users WHERE Username = '" + u + "';");
        if (r.next())
            us = new User(r.getInt("UserID"), r.getString("Username"), r.getString("Password"), r.getInt("Caps"));
        return us;
    }
    
    public static User getUser(int i) throws SQLException
    {
        User us = null;
        r = s.executeQuery("SELECT * FROM Users WHERE UserID = " + Integer.toString(i) + ";");
        if (r.next())
            us = new User(r.getInt("UserID"), r.getString("Username"), r.getString("Password"), r.getInt("Caps"));
        return us;
    }
    
    public static boolean addUser(String u, String p, boolean eu, boolean ed) throws SQLException
    {
        if (userExists(u) > 0)
            return true;
        else
        {
            r = s.executeQuery("SELECT UserID FROM Users ORDER BY UserID DESC;");
            int next = 0;
            if (r.next())
            {
                next = r.getInt("UserID");
            }
            s.executeUpdate("INSERT INTO Users VALUES (" + Integer.toString(next+1) + ", '" + u + "', '" + p + "', " + Integer.toString((eu?4:0)+(ed?2:0)+1) + ");");
            return false;
        }
    }
    
    public static boolean updateUser(int i, String u, String p, boolean eu, boolean ed) throws SQLException
    {
        s.executeUpdate("UPDATE Users SET Username = '" + u + "', Password = '" + p + "', Caps = " + Integer.toString((eu?4:0)+(ed?2:0)+1) + " WHERE UserID = " + Integer.toString(i) + ";");
        return false;
    }
    
    public static boolean removeUser(int i) throws SQLException
    {
        r = s.executeQuery("SELECT COUNT(*) AS Count FROM Users WHERE (Caps & 4) = 4;");
        r.next();
        if (r.getInt("Count") < 2)
            return false;
        s.executeUpdate("DELETE FROM Users WHERE UserID = " + Integer.toString(i));
        return true;
    }
    
    public static DefaultTableModel getStudentsTable(String fn, String ln, String c) throws SQLException
    {
        r = s.executeQuery("SELECT StudentID AS 'Student #', SID AS 'Student ID', FN AS 'First Name', LN AS 'Last Name' FROM Students WHERE FN LIKE '%" + fn + "%' AND LN LIKE '%" + ln + "%' AND SID LIKE '%" + c + "%' ORDER BY LN ASC;");
        return buildTableModel(r);
    }
    
    public static DefaultTableModel getClassesTable(String name, int sy, int sem, String c) throws SQLException
    {
        if ((sy==0)&&(sem==0))
            r = s.executeQuery("SELECT ClassID AS 'Class #', Name, SY AS 'School Year', Sem AS 'Semester', Room, Course, Level FROM Classes WHERE Name LIKE '%" + name + "%' AND Course LIKE '%" + c + "%' ORDER BY Name ASC;");
        else if ((sy==0)&&(sem>0))
            r = s.executeQuery("SELECT ClassID AS 'Class #', Name, SY AS 'School Year', Sem AS 'Semester', Room, Course, Level FROM Classes WHERE Name LIKE '%" + name + "%' AND Sem = " + Integer.toString(sem) + " AND Course LIKE '%" + c + "%' ORDER BY Name ASC;");
        else if ((sy>0)&&(sem==0))
            r = s.executeQuery("SELECT ClassID AS 'Class #', Name, SY AS 'School Year', Sem AS 'Semester', Room, Course, Level FROM Classes WHERE Name LIKE '%" + name + "%' AND SY = " + Integer.toString(sy) + " AND Course LIKE '%" + c + "%' ORDER BY Name ASC;");
        else
            r = s.executeQuery("SELECT ClassID AS 'Class #', Name, SY AS 'School Year', Sem AS 'Semester', Room, Course, Level FROM Classes WHERE Name LIKE '%" + name + "%' AND SY = " + Integer.toString(sy) + " AND Sem = " + Integer.toString(sem) + " AND Course LIKE '%" + c + "%' ORDER BY Name ASC;");
        return buildTableModel(r);
    }
    
    public static int addClass(String name, int sy, int sem, int year, String course, String room, String sched, String level) throws SQLException
    {
        r = s.executeQuery("SELECT ClassID FROM Classes ORDER BY ClassID DESC;");
        int next;
        if (r.next())
            next = r.getInt("ClassID") + 1;
        else
            next = 0;
        
        s.executeUpdate("INSERT INTO Classes VALUES (" + Integer.toString(next) + ", '" + name + "', " + Integer.toString(sy) + ", " + Integer.toString(sem) + ", " + Integer.toString(year) + ", '" + course + "', '" + room + "', '" + sched + "', '" + level + "');");
        return next;
    }
    
    public static void editClass(int cid, String name, int sy, int sem, int year, String course, String room, String sched, String level) throws SQLException
    {
        s.executeUpdate("UPDATE Classes SET Name =  '" + name + "', SY = " + Integer.toString(sy) + ", Sem = " + Integer.toString(sem) + ", Year = " + Integer.toString(year) + ", Course = '" + course + "', Room = '" + room + "', Sched = '" + sched + "', Level = '" + level + "' WHERE ClassID = " + Integer.toString(cid) + ";");
    }
    
    public static Data.Class getClass(int cid) throws SQLException
    {
        Data.Class c = null;
        r = s.executeQuery("SELECT * FROM Classes WHERE ClassID = " + Integer.toString(cid) + ";");
        if (r.next())
        {
            c = new Data.Class(r.getInt("ClassID"), r.getString("Name"), r.getInt("SY"), r.getInt("Sem"), r.getInt("Year"), r.getString("Course"), r.getString("Room"), r.getString("Sched"), r.getString("Level"), null);
        }
        
        if (r == null)
            return null;
        
        r = s.executeQuery("SELECT * FROM Criterias WHERE ClassID = " + Integer.toString(cid) + ";");
        ArrayList<Criteria> cr = new ArrayList<>();
        while (r.next())
        {
            cr.add(new Criteria(r.getString("Name"), r.getInt("Percent"), r.getInt("Terms")));
        }
        
        c.setCriterias(cr);
        
        return c;
    }
    
    public static boolean isCollege(int cid) throws SQLException
    {
        r = s.executeQuery("SELECT Level FROM Classes WHERE ClassID = " + Integer.toString(cid) + ";");
        if (r.next())
            return "College".equals(r.getString("Level"));
        else
            throw new SQLException("Class not found LOL");
    }
    
    public static void deleteClass(int cid) throws SQLException
    {
        s.executeUpdate("DELETE FROM Classes WHERE ClassID = " + Integer.toString(cid) + ";");
    }
    
    public static String getClassName(int cid) throws SQLException
    {
        r = s.executeQuery("SELECT Name FROM Classes WHERE ClassID = " + Integer.toString(cid) + ";");
        if (r.next())
            return r.getString("Name");
        else
            return "REAL ERROR";
    }
    
    public static DefaultTableModel getCriteriaTable(int cid) throws SQLException
    {
        r = s.executeQuery("SELECT CriteriaID AS 'Criteria #', Name, Percent FROM Criterias WHERE ClassID = " + Integer.toString(cid));
        return buildTableModel(r);
    }
    
    public static DefaultTableModel getCriteriaTableSHS(int cid) throws SQLException
    {
        r = s.executeQuery("SELECT CriteriaID AS 'Criteria #', Name, Percent FROM CriteriasSHS WHERE ClassID = " + Integer.toString(cid));
        return buildTableModel(r);
    }
    
    public static void addCriteria(int cid, String name, int p, int t) throws SQLException
    {
        r = s.executeQuery("SELECT CriteriaID FROM Criterias ORDER BY CriteriaID DESC;");
        int next;
        if (r.next())
            next = r.getInt("CriteriaID") + 1;
        else
            next = 0;
        
        s.executeUpdate("INSERT INTO Criterias VALUES (" + Integer.toString(next) + ", " + Integer.toString(cid) + ", '" + name + "', " + Integer.toString(p) + ", " + Integer.toString(t) + ");");
    }
    
    public static int totalCriteriaPercent(int classid) throws SQLException
    {
        r = s.executeQuery("SELECT SUM(Percent) AS 'Sum' FROM Criterias WHERE ClassID = " + Integer.toString(classid) + ";");
        if (r.next())
            return r.getInt("Sum");
        else
            return -1;
    }
    
    public static int totalCriteriaPercentSHS(int classid) throws SQLException
    {
        r = s.executeQuery("SELECT SUM(Percent) AS 'Sum' FROM CriteriasSHS WHERE ClassID = " + Integer.toString(classid) + ";");
        if (r.next())
            return r.getInt("Sum");
        else
            return -1;
    }
    
    public static void editCriteria(int crid, String name, int p, int t) throws SQLException
    {
        s.executeUpdate("UPDATE Criterias SET Name =  '" + name + "', Percent = " + Integer.toString(p) + ", Terms = " + Integer.toString(t) + " WHERE CriteriaID = " + Integer.toString(crid) + ";");
    }
    
    public static void editCriteriaSHS(int crid, String name, int p, int t) throws SQLException
    {
        s.executeUpdate("UPDATE CriteriasSHS SET Name =  '" + name + "', Percent = " + Integer.toString(p) + ", Terms = " + Integer.toString(t) + " WHERE CriteriaID = " + Integer.toString(crid) + ";");
    }
    
    public static Criteria getCriteria(int crid) throws SQLException
    {
        r = s.executeQuery("SELECT * FROM Criterias WHERE CriteriaID = " + Integer.toString(crid) + ";");
        
        if (r.next())
        {
            return new Criteria(r.getString("Name"), r.getInt("Percent"), r.getInt("Terms"));
        }
        else
            return null;
    }
    
    public static Criteria getCriteriaSHS(int crid) throws SQLException
    {
        r = s.executeQuery("SELECT * FROM CriteriasSHS WHERE CriteriaID = " + Integer.toString(crid) + ";");
        
        if (r.next())
        {
            return new Criteria(r.getString("Name"), r.getInt("Percent"), r.getInt("Terms"));
        }
        else
            return null;
    }
    
    public static void addCriteriaSHS(int cid, String name, int p, int t) throws SQLException
    {
        r = s.executeQuery("SELECT CriteriaID FROM CriteriasSHS ORDER BY CriteriaID DESC;");
        int next;
        if (r.next())
            next = r.getInt("CriteriaID") + 1;
        else
            next = 0;
        
        s.executeUpdate("INSERT INTO CriteriasSHS VALUES (" + Integer.toString(next) + ", " + Integer.toString(cid) + ", '" + name + "', " + Integer.toString(p) + ", " + Integer.toString(t) + ");");
    }
    
    public static DefaultTableModel getEnrollees(int classid) throws SQLException
    {
        if (isCollege(classid))
            r = s.executeQuery("SELECT EnrolleeID AS 'Enrollee #', Name, ClassCard AS 'Class Card #', Notes FROM Enrollees JOIN Students ON StudentsInClass.StudentID = Students.StudentID WHERE ClassID = " + Integer.toString(classid) + ";");
        else
            r = s.executeQuery("SELECT EnrolleeID AS 'Enrollee #', Name, Notes FROM SHSEnrollees JOIN Students ON StudentsInSHSClass.StudentID = Students.StudentID WHERE ClassID = " + Integer.toString(classid) + ";");
        return buildTableModel(r);
    }
    
    public static Enrollee getEnrollee(int eid) throws SQLException
    {
        r = s.executeQuery("SELECT * FROM Enrollees WHERE EnrolleeID = " + Integer.toString(eid) + ";");
        if (r.next())
            return new Enrollee(r.getInt("EnrolleeID"), r.getInt("ClassID"), r.getInt("StudentID"), r.getInt("ClassCard"), r.getString("Notes"));
        else
            throw new SQLException("Enrollee not found");
    }
    
    public static Enrollee getSHSEnrollee(int eid) throws SQLException
    {
        r = s.executeQuery("SELECT * FROM SHSEnrollees WHERE EnrolleeID = " + Integer.toString(eid) + ";");
        if (r.next())
            return new Enrollee(r.getInt("EnrolleeID"), r.getInt("ClassID"), r.getInt("StudentID"), 0, r.getString("Notes"));
        else
            throw new SQLException("Enrollee not found");
    }
    
    public static void addEnrollee(int classid, int studentid, int classcard, String notes) throws SQLException
    {
        r = s.executeQuery("SELECT EnrolleeID FROM Enrollees ORDER BY EnrolleeID DESC;");
        int next;
        if (r.next())
            next = r.getInt("EnrolleeID") + 1;
        else
            next = 0;
        
        s.executeUpdate("INSERT INTO Enrollees VALUES (" + Integer.toString(next) + ", " + Integer.toString(classid) + ", " + Integer.toString(studentid) + ", " + Integer.toString(classcard) + ", '" + notes + "');");
    }
    
    public static void editEnrollee(int eid, int classid, int studentid, int classcard, String notes) throws SQLException
    {
        s.executeUpdate("UPDATE Enrollees SET ClassID = " + Integer.toString(classid) + ", StudentID = " + Integer.toString(studentid) + ", ClassCard = " + Integer.toString(classcard) + ", Notes = '" + notes + "' WHERE EnrolleeID = " + Integer.toString(eid) + ";");
    }

    public static void editSHSEnrollee(int eid, int classid, int studentid, String notes) throws SQLException
    {
        s.executeUpdate("UPDATE SHSEnrollees SET ClassID = " + Integer.toString(classid) + ", StudentID = " + Integer.toString(studentid) + ", Notes = '" + notes + "' WHERE EnrolleeID = " + Integer.toString(eid) + ";");
    }
    
    public static void addSHSEnrollee(int classid, int studentid, String notes) throws SQLException
    {
        r = s.executeQuery("SELECT EnrolleeID FROM SHSEnrollees ORDER BY EnrolleeID DESC;");
        int next;
        if (r.next())
            next = r.getInt("EnrolleeID") + 1;
        else
            next = 0;
        
        s.executeUpdate("INSERT INTO SHSEnrollees VALUES (" + Integer.toString(next) + ", " + Integer.toString(classid) + ", " + Integer.toString(studentid) + ", '" + notes + "');");
    }
    
    public static void deleteEnrollee(int eid) throws SQLException
    {
        s.executeUpdate("DELETE FROM Enrollees WHERE EnrolleeID = " + Integer.toString(eid) + ";");
    }
    
    public static void deleteSHSEnrollee(int eid) throws SQLException
    {
        s.executeUpdate("DELETE FROM SHSEnrollees WHERE EnrolleeID = " + Integer.toString(eid) + ";");
    }
    
    public static void deleteCriteria(int cid) throws SQLException
    {
        s.executeUpdate("DELETE FROM Criterias WHERE CriteriaID = " + Integer.toString(cid));
    }
    
    public static void deleteCriteriaSHS(int cid) throws SQLException
    {
        s.executeUpdate("DELETE FROM CriteriasSHS WHERE CriteriaID = " + Integer.toString(cid));
    }

    
    private static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException
    {
        ResultSetMetaData metaData = rs.getMetaData();
        
        // column names
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++)
        {
            columnNames.add(metaData.getColumnName(column));
        }
        
        // data of the table
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next())
        {
            Vector<Object> al = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                al.add(rs.getObject(columnIndex));
            }
            data.add(al);
        }
        
        return new DefaultTableModel(data, columnNames);
    }
    
    public static void addStudent(String SID, String FN, String MN, String LN, String gender, String contact, String address, String notes, BufferedImage bi) throws IOException, SQLException
    {
        r = s.executeQuery("SELECT StudentID FROM Students ORDER BY StudentID DESC;");
        int next = 0;
        if (r.next())
            next = r.getInt("StudentID") + 1;
        else
            next = 0;
        
        ByteArrayOutputStream tempimg = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", tempimg);
        byte[] imgbytes = tempimg.toByteArray();
        
        PreparedStatement preps = con.prepareStatement("INSERT INTO Students VALUES (?,?,?,?,?,?,?,?,?,?);");
        preps.setInt(1, next);
        preps.setString(2, SID);
        preps.setString(3, FN);
        preps.setString(4, MN);
        preps.setString(5, LN);
        preps.setString(6, gender);
        preps.setString(7, contact);
        preps.setString(8, address);
        preps.setString(9, notes);
        preps.setBytes(10, imgbytes);
        preps.executeUpdate();
    }
    
    public static void updateStudent(int ID, String SID, String FN, String MN, String LN, String gender, String contact, String address, String notes, BufferedImage bi) throws IOException, SQLException
    {
        ByteArrayOutputStream tempimg = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", tempimg);
        byte[] imgbytes = tempimg.toByteArray();

        PreparedStatement preps = con.prepareStatement("UPDATE Students SET SID = ?, FN = ?, MN = ?, LN = ?, Sex = ?, Contact = ?, Address = ?, Notes = ?, Picture = ? WHERE StudentID = ?;");
        preps.setString(1, SID);
        preps.setString(2, FN);
        preps.setString(3, MN);
        preps.setString(4, LN);
        preps.setString(5, gender);
        preps.setString(6, contact);
        preps.setString(7, address);
        preps.setString(8, notes);
        preps.setBytes(9, imgbytes);
        preps.setInt(10, ID);
        preps.executeUpdate();
    }
    
    public static Student getStudent(int id) throws SQLException, IOException
    {
        r = s.executeQuery("SELECT * FROM Students WHERE StudentID = " + Integer.toString(id) + ";");
        r.next();
        
        BufferedImage bi = ImageIO.read(r.getBinaryStream("Picture"));

        
        Student res = new Student(
            r.getInt("StudentID"),
            r.getString("SID"),
            r.getString("FN"),
            r.getString("MN"),
            r.getString("LN"),
            r.getString("Sex"),
            r.getString("Contact"),
            r.getString("Address"),
            r.getString("Notes"),
            bi
        );
        
        return res;
    }
    
    public static void deleteStudent(int id) throws SQLException
    {
        s.executeUpdate("DELETE FROM Students WHERE StudentID = " + Integer.toString(id) + ";");
    }
}
