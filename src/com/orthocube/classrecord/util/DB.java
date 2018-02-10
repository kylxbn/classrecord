/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util;

import com.orthocube.classrecord.data.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author OrthoCube
 */
public class DB {
    private final static Logger LOGGER = Logger.getLogger(DB.class.getName());
    private static String url = "jdbc:derby:database;create=true";
    private static boolean isFirstRun = true;
    private static Connection con = null;

    // <editor-fold defaultstate="collapsed" desc="Tools">
    static {
        try {
            Properties props = new Properties(); // connection properties
            props.put("user", "orthocube");
            String password = "nopassword";
            props.put("password", password);

            LOGGER.log(Level.INFO, "Connecting to database...");
            con = DriverManager.getConnection(url, props);
            createTables();

            ResultSet r;

            Statement s = con.createStatement();
            r = s.executeQuery("SELECT * FROM Settings WHERE SettingKey = 'db_version'");
            if (!r.next())
                s.executeUpdate("INSERT INTO Settings VALUES ('db_version', '4')");

            r = s.executeQuery("SELECT COUNT(*) FROM Users");
            r.next();
            int users = r.getInt(1);
            isFirstRun = users == 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables() {
        try {
            LOGGER.log(Level.INFO, "Creating tables...");
            Statement s = con.createStatement();

            try {
                s.executeUpdate("CREATE TABLE Users (UserID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "Username VARCHAR(64) UNIQUE, " +
                        "Password VARCHAR(64) NOT NULL, " +
                        "Caps SMALLINT NOT NULL DEFAULT 0)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
                LOGGER.log(Level.INFO, "Users table already exists so database is assumed to be all set up.");
                return;
            }

            try {
                s.executeUpdate("CREATE TABLE Students (StudentID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "SID VARCHAR(20), " +
                        "FN VARCHAR(64), " +
                        "MN VARCHAR(64), " +
                        "LN VARCHAR(64), " +
                        "isFemale SMALLINT, " +
                        "Contact VARCHAR(64), " +
                        "Address VARCHAR(255), " +
                        "Notes VARCHAR(255), " +
                        "Picture BLOB(64000))");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE Classes (ClassID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "Name VARCHAR(60), " +
                        "SY SMALLINT, " +
                        "Sem SMALLINT, " +
                        "YearLevel SMALLINT, " +
                        "Course VARCHAR(255), " +
                        "Room VARCHAR(20), " +
                        "Days SMALLINT, " +
                        "Times VARCHAR(255), " +
                        "IsSHS SMALLINT, " +
                        "Notes VARCHAR(500))");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE Criterias (CriteriaID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "ClassID BIGINT REFERENCES Classes(ClassID) ON DELETE CASCADE, " +
                        "Name VARCHAR(20), " +
                        "Percent SMALLINT, " +
                        "Terms SMALLINT)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE SHSCriterias (CriteriaID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "ClassID BIGINT REFERENCES Classes(ClassID) ON DELETE CASCADE, " +
                        "Name VARCHAR(20), " +
                        "Percent SMALLINT, " +
                        "Terms SMALLINT)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE Enrollees (EnrolleeID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "ClassID BIGINT REFERENCES Classes(ClassID) ON DELETE CASCADE, " +
                        "StudentID BIGINT REFERENCES Students(StudentID) ON DELETE CASCADE, " +
                        "ClassCard INTEGER, " +
                        "Notes VARCHAR(255), " +
                        "Course VARCHAR(20))");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE SHSEnrollees (EnrolleeID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "ClassID BIGINT REFERENCES Classes(ClassID) ON DELETE CASCADE, " +
                        "StudentID BIGINT REFERENCES Students(StudentID) ON DELETE CASCADE, " +
                        "Notes VARCHAR(255), " +
                        "Course VARCHAR(20))");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE AttendanceDays (DayID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "Date DATE NOT NULL, " +
                        "ClassID BIGINT REFERENCES Classes(ClassID), " +
                        "Notes VARCHAR(255))");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE AttendanceList (AttendanceID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "DayID BIGINT REFERENCES AttendanceDays(DayID) ON DELETE CASCADE, " +
                        "EnrolleeID BIGINT REFERENCES Enrollees(EnrolleeID) ON DELETE CASCADE, " +
                        "Remarks CHAR(1) NOT NULL, " +
                        "Notes VARCHAR(255))");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE SHSAttendanceList (AttendanceID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "DayID BIGINT REFERENCES AttendanceDays(DayID) ON DELETE CASCADE, " +
                        "EnrolleeID BIGINT REFERENCES SHSEnrollees(EnrolleeID) ON DELETE CASCADE, " +
                        "Remarks CHAR(1) NOT NULL, " +
                        "Notes VARCHAR(255))");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE Tasks (TaskID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "Name VARCHAR(20), " +
                        "ClassID BIGINT REFERENCES Classes(ClassID) ON DELETE CASCADE, " +
                        "CriteriaID BIGINT REFERENCES Criterias(CriteriaID) ON DELETE CASCADE, " +
                        "Term SMALLINT NOT NULL, " +
                        "Items SMALLINT NOT NULL)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE SHSTasks (TaskID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "Name VARCHAR(20), " +
                        "ClassID BIGINT REFERENCES Classes(ClassID) ON DELETE CASCADE, " +
                        "CriteriaID BIGINT REFERENCES SHSCriterias(CriteriaID) ON DELETE CASCADE, " +
                        "Term SMALLINT NOT NULL, " +
                        "Items SMALLINT NOT NULL)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE Scores (ScoreID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "EnrolleeID BIGINT REFERENCES Enrollees(EnrolleeID) ON DELETE CASCADE, " +
                        "TaskID BIGINT REFERENCES Tasks(TaskID) ON DELETE CASCADE, " +
                        "Score SMALLINT NOT NULL, " +
                        "Notes VARCHAR(255) NOT NULL)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE SHSScores (ScoreID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "EnrolleeID BIGINT REFERENCES SHSEnrollees(EnrolleeID) ON DELETE CASCADE, " +
                        "TaskID BIGINT REFERENCES SHSTasks(TaskID) ON DELETE CASCADE, " +
                        "Score SMALLINT NOT NULL, " +
                        "Notes VARCHAR(255) NOT NULL)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE LicenseKeys (KeyID SMALLINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "LicenseKey VARCHAR(25) NOT NULL)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE Reminders (ID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "StartDate DATE NOT NULL, " +
                        "StartTime TIME NOT NULL, " +
                        "EndDate DATE NOT NULL, " +
                        "EndTime TIME NOT NULL, " +
                        "Title VARCHAR(64) NOT NULL, " +
                        "Notes VARCHAR(255) NOT NULL, " +
                        "Location VARCHAR(64) NOT NULL)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            try {
                s.executeUpdate("CREATE TABLE Settings (SettingKey VARCHAR(64) PRIMARY KEY NOT NULL, " +
                        "SettingValue VARCHAR(255) NOT NULL)");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
            }

            LOGGER.log(Level.INFO, "Done creating tables.");
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    public static int getDatabaseVersion() throws SQLException {
        LOGGER.log(Level.INFO, "Getting database version...");
        ResultSet r;

        PreparedStatement getVersion = con.prepareStatement("SELECT SettingValue FROM Settings WHERE SettingKey = 'db_version'");
        r = getVersion.executeQuery();
        int version;
        if (r.next()) {
            version = Integer.parseInt(r.getString(1));
        } else {
            version = 0;
        }
        return version;
    }

    public static void close() {
        LOGGER.log(Level.INFO, "Closing database and shutting down Derby...");
        try {

            con.close();
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if (!((e.getErrorCode() == 50000) && ("XJ015".equals(e.getSQLState())))) {
                Dialogs.exception(e);
            } else {
                LOGGER.log(Level.INFO, "Derby successfully shut down.");
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Students">
    public static ObservableList<Student> getStudents() throws SQLException, IOException {
        LOGGER.log(Level.INFO, "Getting students...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT Students.StudentID, Students.SID, Students.FN, Students.MN, Students.LN, Students.IsFemale, Students.Contact, Students.Address, Students.Notes, Students.Picture FROM Students");
        r = prep.executeQuery();

        ObservableList<Student> students = FXCollections.observableArrayList();

        while (r.next()) {
            Student temp = new Student(r.getLong(1));
            temp.setSID(r.getString(2));
            temp.setFN(r.getString(3));
            temp.setMN(r.getString(4));
            temp.setLN(r.getString(5));
            temp.setFemale(r.getInt(6) > 0);
            temp.setContact(r.getString(7));
            temp.setAddress(r.getString(8));
            temp.setNotes(r.getString(9));
            InputStream is = r.getBinaryStream(10);
            if (is != null) {
                temp.setPicture(ImageIO.read(is));
                is.close();
            }
            students.add(temp);
        }

        return students;
    }

    public static boolean save(Student s) throws SQLException, IOException {
        if (s.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new student...");
            PreparedStatement preps = con.prepareStatement("INSERT INTO Students (Students.SID, Students.FN, Students.MN, Students.LN, Students.isFemale, Students.Contact, Students.Address, Students.Notes, Students.Picture) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preps.setString(1, s.getSID());
            preps.setString(2, s.getFN());
            preps.setString(3, s.getMN());
            preps.setString(4, s.getLN());
            preps.setInt(5, s.isFemale() ? 1 : 0);
            preps.setString(6, s.getContact());
            preps.setString(7, s.getAddress());
            preps.setString(8, s.getNotes());
            preps.setBytes(9, null);
            preps.executeUpdate();
            ResultSet res = preps.getGeneratedKeys();
            res.next();
            s.setID(res.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Updating student...");
            byte[] imgbytes = null;
            if (s.getPicture() != null) {
                ByteArrayOutputStream tempimg = new ByteArrayOutputStream();
                ImageIO.write(s.getPicture(), "png", tempimg);
                imgbytes = tempimg.toByteArray();
            }

            PreparedStatement preps = con.prepareStatement("UPDATE Students SET SID = ?, FN = ?, MN = ?, LN = ?, isFemale = ?, Contact = ?, Address = ?, Notes = ?, Picture = ? WHERE StudentID = ?");
            preps.setString(1, s.getSID());
            preps.setString(2, s.getFN());
            preps.setString(3, s.getMN());
            preps.setString(4, s.getLN());
            preps.setInt(5, s.isFemale() ? 1 : 0);
            preps.setString(6, s.getContact());
            preps.setString(7, s.getAddress());
            preps.setString(8, s.getNotes());
            preps.setBytes(9, imgbytes);
            preps.setLong(10, s.getID());
            preps.executeUpdate();

            return false;
        }
    }

    public static void delete(Student s) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting student...");
        PreparedStatement prep = con.prepareStatement("DELETE FROM Students WHERE StudentID = ?");
        prep.setLong(1, s.getID());
        prep.executeUpdate();
    }

    public static ObservableList<Clazz> getEnrolledIn(Student s) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Enrolled In list...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT Classes.ClassID, Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes FROM Classes JOIN Enrollees ON Enrollees.ClassID = Classes.ClassID WHERE Enrollees.StudentID = ?");
        prep.setLong(1, s.getID());
        r = prep.executeQuery();

        ObservableList<Clazz> classes = FXCollections.observableArrayList();
        while (r.next()) {
            Clazz temp = new Clazz(r.getLong(1));
            temp.setName(r.getString(2));
            temp.setSY(r.getInt(3));
            temp.setSem(r.getInt(4));
            temp.setYear(r.getInt(5));
            temp.setCourse(r.getString(6));
            temp.setRoom(r.getString(7));
            temp.setDays(r.getInt(8));

            ArrayList<String> times = new ArrayList<>();
            Collections.addAll(times, r.getString(9).split("\\|"));
            temp.setTimes(times);

            temp.setSHS(r.getShort(10) > 0);
            temp.setNotes(r.getString(11));
            classes.add(temp);
        }

        return classes;
    }

    public static ObservableList<Clazz> getSHSEnrolledIn(Student s) throws SQLException {
        LOGGER.log(Level.INFO, "Getting SHS Enrolled In list...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT Classes.ClassID, Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes FROM Classes JOIN SHSEnrollees ON SHSEnrollees.ClassID = Classes.ClassID WHERE SHSEnrollees.StudentID = ?");
        prep.setLong(1, s.getID());
        r = prep.executeQuery();

        ObservableList<Clazz> classes = FXCollections.observableArrayList();
        while (r.next()) {
            Clazz temp = new Clazz(r.getInt(1));
            temp.setName(r.getString(2));
            temp.setSY(r.getInt(3));
            temp.setSem(r.getInt(4));
            temp.setYear(r.getInt(5));
            temp.setCourse(r.getString(6));
            temp.setRoom(r.getString(7));
            temp.setDays(r.getInt(8));

            ArrayList<String> times = new ArrayList<>();
            Collections.addAll(times, r.getString(9).split("\\|"));

            temp.setTimes(times);

            temp.setSHS(r.getInt(10) > 0);
            temp.setNotes(r.getString(11));

            classes.add(temp);
        }

        return classes;
    }

    public static boolean studentEnrolledIn(Student s, Clazz c) throws SQLException {
        LOGGER.log(Level.INFO, "Checking if student is enrolled in class...");
        ResultSet r;
        PreparedStatement ps;
        if (c.isSHS()) {
            ps = con.prepareStatement("SELECT EnrolleeID FROM SHSEnrollees WHERE StudentID = ? AND ClassID = ?");
            ps.setLong(1, s.getID());
            ps.setLong(2, c.getID());
        } else {
            ps = con.prepareStatement("SELECT EnrolleeID FROM Enrollees WHERE StudentID = ? AND ClassID = ?");
            ps.setLong(1, s.getID());
            ps.setLong(2, c.getID());
        }
        r = ps.executeQuery();
        return r.next();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Classes">
    public static ObservableList<Clazz> getClasses() throws SQLException {
        LOGGER.log(Level.INFO, "Getting classes...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT Classes.ClassID, Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes FROM Classes");
        r = prep.executeQuery();

        ObservableList<Clazz> classes = FXCollections.observableArrayList();

        while (r.next()) {
            Clazz temp = new Clazz(r.getInt(1));
            temp.setName(r.getString(2));
            temp.setSY(r.getInt(3));
            temp.setSem(r.getInt(4));
            temp.setYear(r.getInt(5));
            temp.setCourse(r.getString(6));
            temp.setRoom(r.getString(7));
            temp.setDays(r.getInt(8));

            ArrayList<String> times = new ArrayList<>();
            Collections.addAll(times, r.getString(9).split("\\|"));

            temp.setTimes(times);

            temp.setSHS(r.getInt(10) > 0);
            temp.setNotes(r.getString(11));
            classes.add(temp);
        }

        return classes;
    }

    public static void delete(Clazz c) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting class...");
        PreparedStatement prep = con.prepareStatement("DELETE FROM Classes WHERE ClassID = ?");
        prep.setLong(1, c.getID());
        prep.executeUpdate();
    }

    public static boolean save(Clazz c) throws SQLException {
        if (c.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new class...");
            PreparedStatement preps = con.prepareStatement("INSERT INTO Classes (Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes) VALUES (?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

            preps.setString(1, c.getName());
            preps.setInt(2, c.getSY());
            preps.setInt(3, c.getSem());
            preps.setInt(4, c.getYear());
            preps.setString(5, c.getCourse());
            preps.setString(6, c.getRoom());
            preps.setInt(7, c.getDays());

            StringBuilder times = new StringBuilder("");
            for (String time : c.getTimes()) {
                times.append(time).append('|');
            }
            times.deleteCharAt(times.length() - 1);

            preps.setString(8, times.toString());
            preps.setInt(9, c.isSHS() ? 1 : 0);
            preps.setString(10, c.getNotes());
            preps.executeUpdate();
            ResultSet res = preps.getGeneratedKeys();
            res.next();
            c.setID(res.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Updating class...");
            PreparedStatement prep = con.prepareStatement("UPDATE Classes SET Name =  ?, SY = ?, Sem = ?, YearLevel = ?, Course = ?, Room = ?, Days = ?, Times = ?, IsSHS = ?, Notes = ? WHERE ClassID = ?");
            prep.setString(1, c.getName());
            prep.setInt(2, c.getSY());
            prep.setInt(3, c.getSem());
            prep.setInt(4, c.getYear());
            prep.setString(5, c.getCourse());
            prep.setString(6, c.getRoom());
            prep.setInt(7, c.getDays());

            StringBuilder ti = new StringBuilder("");
            for (String t : c.getTimes()) {
                ti.append(t).append("|");
            }
            ti.deleteCharAt(ti.length() - 1);

            prep.setString(8, ti.toString());
            prep.setInt(9, (c.isSHS() ? 1 : 0));
            prep.setString(10, c.getNotes());
            prep.setLong(11, c.getID());
            prep.executeUpdate();
            return false;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Enrollees">
    public static ObservableList<Enrollee> getEnrollees(Clazz c) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Enrollees...");
        ResultSet r;

        PreparedStatement prep;

        boolean shs = c.isSHS();
        if (shs)
            prep = con.prepareStatement("SELECT SHSEnrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, SHSEnrollees.Notes, SHSEnrollees.Course FROM (Students JOIN SHSEnrollees ON SHSEnrollees.StudentID = Students.StudentID) JOIN Classes ON SHSEnrollees.ClassID = Classes.ClassID WHERE SHSEnrollees.ClassID = ?");
        else {
            prep = con.prepareStatement("SELECT Enrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, Enrollees.ClassCard, Enrollees.Notes, Enrollees.Course FROM (Students JOIN Enrollees ON Enrollees.StudentID = Students.StudentID) JOIN Classes ON Enrollees.ClassID = Classes.ClassID WHERE Enrollees.ClassID = ?");
        }

        prep.setLong(1, c.getID());
        r = prep.executeQuery();

        ObservableList<Enrollee> enrollees = FXCollections.observableArrayList();
        while (r.next()) {
            if (shs) {
                Enrollee temp = new Enrollee(r.getLong(1));
                temp.setClass(c);
                temp.setStudent(new Student(r.getLong(2)));
                temp.getStudent().setFN(r.getString(3));
                temp.getStudent().setLN(r.getString(4));
                temp.setNotes(r.getString(5));
                temp.setCourse(r.getString(6));
                enrollees.add(temp);
            } else {
                Enrollee temp = new Enrollee(r.getLong(1));
                temp.setClass(c);
                temp.setStudent(new Student(r.getLong(2)));
                temp.getStudent().setFN(r.getString(3));
                temp.getStudent().setLN(r.getString(4));
                temp.setClasscard(r.getInt(5));
                temp.setNotes(r.getString(6));
                temp.setCourse(r.getString(7));
                enrollees.add(temp);
            }
        }

        return enrollees;
    }

    public static boolean save(Enrollee e) throws SQLException {
        if (e.getID() == -1) {
            if (e.getClazz().isSHS()) {
                LOGGER.log(Level.WARNING, "Creating new SHS enrollee");
                PreparedStatement preps = con.prepareStatement("INSERT INTO SHSEnrollees (ClassID, StudentID, Notes, Course) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                preps.setLong(1, e.getClazz().getID());
                preps.setLong(2, e.getStudent().getID());
                preps.setString(3, e.getNotes());
                preps.setString(4, e.getCourse());
                preps.executeUpdate();
                ResultSet res = preps.getGeneratedKeys();
                res.next();
                e.setID(res.getLong(1));
            } else {
                LOGGER.log(Level.WARNING, "Creating new enrollee");
                PreparedStatement preps = con.prepareStatement("INSERT INTO Enrollees (ClassID, StudentID, ClassCard, Notes, Course) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                preps.setLong(1, e.getClazz().getID());
                preps.setLong(2, e.getStudent().getID());
                preps.setInt(3, e.getClasscard());
                preps.setString(4, e.getNotes());
                preps.setString(5, e.getCourse());
                preps.executeUpdate();
                ResultSet res = preps.getGeneratedKeys();
                res.next();
                e.setID(res.getLong(1));
            }
            return true;
        } else {
            LOGGER.log(Level.INFO, "Updating enrollee...");
            if (e.getClazz().isSHS()) {
                PreparedStatement prep = con.prepareStatement("UPDATE SHSEnrollees SET ClassID = ?, StudentID = ?, Notes = ?, Course = ? WHERE EnrolleeID = ?");
                prep.setLong(1, e.getClazz().getID());
                prep.setLong(2, e.getStudent().getID());
                prep.setString(3, e.getNotes());
                prep.setString(4, e.getCourse());
                prep.setLong(5, e.getID());
                prep.executeUpdate();
            } else {
                PreparedStatement prep = con.prepareStatement("UPDATE Enrollees SET ClassID = ?, StudentID = ?, ClassCard = ?, Notes = ?, Course = ? WHERE EnrolleeID = ?");
                prep.setLong(1, e.getClazz().getID());
                prep.setLong(2, e.getStudent().getID());
                prep.setInt(3, e.getClasscard());
                prep.setString(4, e.getNotes());
                prep.setString(5, e.getCourse());
                prep.setLong(6, e.getID());
                prep.executeUpdate();
            }
            return false;
        }
    }

    public static void delete(Enrollee e) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting enrollee...");
        PreparedStatement prep;
        if (e.getClazz().isSHS()) {
            prep = con.prepareStatement("DELETE FROM SHSEnrollees WHERE EnrolleeID = ?");
        } else {
            prep = con.prepareStatement("DELETE FROM Enrollees WHERE EnrolleeID = ?");
        }
        prep.setLong(1, e.getID());
        prep.executeUpdate();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Criterias">
    public static ObservableList<Criteria> getCriterias(Clazz c) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Criterias...");
        ResultSet r;

        PreparedStatement prep;

        boolean shs = c.isSHS();
        if (shs)
            prep = con.prepareStatement("SELECT SHSCriterias.CriteriaID, SHSCriterias.Name, SHSCriterias.Percent, SHSCriterias.Terms FROM SHSCriterias WHERE SHSCriterias.ClassID = ?");
        else {
            prep = con.prepareStatement("SELECT Criterias.CriteriaID, Criterias.Name, Criterias.Percent, Criterias.Terms FROM Criterias WHERE Criterias.ClassID = ?");
        }

        prep.setLong(1, c.getID());
        r = prep.executeQuery();

        ArrayList<Criteria> criterias = new ArrayList<>();
        while (r.next()) {
            Criteria temp = new Criteria(r.getLong(1));
            temp.setClass(c);
            temp.setName(r.getString(2));
            temp.setPercentage(r.getInt(3));
            temp.setTerms(r.getInt(4));
            criterias.add(temp);
        }

        return FXCollections.observableList(criterias, (Criteria cr) -> new Observable[]{cr.nameProperty(), cr.percentageProperty(), cr.termsProperty()});
    }

    public static boolean save(Criteria c) throws SQLException {
        if (c.getID() == -1) {
            LOGGER.log(Level.WARNING, "Creating new Criteria");
            PreparedStatement preps;
            if (c.getClazz().isSHS()) {
                preps = con.prepareStatement("INSERT INTO SHSCriterias (ClassID, Name, Percent, Terms) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            } else {
                preps = con.prepareStatement("INSERT INTO Criterias (ClassID, Name, Percent, Terms) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            }
            preps.setLong(1, c.getClazz().getID());
            preps.setString(2, c.getName());
            preps.setInt(3, c.getPercentage());
            preps.setInt(4, c.getTerms());
            preps.executeUpdate();
            ResultSet res = preps.getGeneratedKeys();
            res.next();
            c.setID(res.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Updating criteria...");
            PreparedStatement prep;
            if (c.getClazz().isSHS()) {
                prep = con.prepareStatement("UPDATE SHSCriterias SET ClassID = ?, Name = ?, Percent = ?, Terms = ? WHERE CriteriaID = ?");
            } else {
                prep = con.prepareStatement("UPDATE Criterias SET ClassID = ?, Name = ?, Percent = ?, Terms = ? WHERE CriteriaID = ?");
            }
            prep.setLong(1, c.getClazz().getID());
            prep.setString(2, c.getName());
            prep.setInt(3, c.getPercentage());
            prep.setInt(4, c.getTerms());
            prep.setLong(5, c.getID());
            prep.executeUpdate();
            return false;
        }
    }

    public static void delete(Criteria c) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting criteria...");
        PreparedStatement prep;
        if (c.getClazz().isSHS()) {
            prep = con.prepareStatement("DELETE FROM SHSCriterias WHERE CriteriaID = ?");
        } else {
            prep = con.prepareStatement("DELETE FROM Criterias WHERE CriteriaID = ?");
        }
        prep.setLong(1, c.getID());
        prep.executeUpdate();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Attendance Day">
    public static ObservableList<AttendanceDay> getAttendanceDays(Clazz c) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Attendance Day...");
        ResultSet r;

        PreparedStatement prep;
        prep = con.prepareStatement("SELECT DayID, Date, Notes FROM AttendanceDays WHERE ClassID = ?");
        prep.setLong(1, c.getID());
        r = prep.executeQuery();

        ObservableList<AttendanceDay> days = FXCollections.observableArrayList();
        while (r.next()) {
            AttendanceDay temp = new AttendanceDay(r.getLong(1));
            temp.setDate(r.getDate(2).toLocalDate());
            temp.setClass(c);
            temp.setNotes(r.getString(3));
            temp.setAttendanceList(getAttendanceList(temp));
            days.add(temp);
        }
        return days;
    }

    public static boolean save(AttendanceDay ad) throws SQLException {
        PreparedStatement prep;
        if (ad.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new Attendance Day...");
            prep = con.prepareStatement("INSERT INTO AttendanceDays (Date, ClassID, Notes) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            prep.setDate(1, ad.getDate());
            prep.setLong(2, ad.getClazz().getID());
            prep.setString(3, ad.getNotes());
            prep.executeUpdate();
            ResultSet rs = prep.getGeneratedKeys();
            rs.next();
            ad.setID(rs.getLong(1));
            ad.setAttendanceList(buildAttendanceList(ad.getClazz(), ad));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Saving Attendance List...");
            prep = con.prepareStatement("UPDATE AttendanceDays SET Date = ?, Notes = ? WHERE DayID = ?");
            prep.setDate(1, ad.getDate());
            prep.setString(2, ad.getNotes());
            prep.setLong(3, ad.getID());
            prep.executeUpdate();
            return false;
        }
    }

    public static void delete(AttendanceDay ad) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting Attendance day...");
        PreparedStatement prep;
        prep = con.prepareStatement("DELETE FROM AttendanceDays WHERE DayID = ?");
        prep.setLong(1, ad.getID());
        prep.executeUpdate();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Attendance List">
    private static ObservableList<AttendanceList> buildAttendanceList(Clazz c, AttendanceDay ad) throws SQLException {
        PreparedStatement prep;
        if (c.isSHS()) {
            prep = con.prepareStatement("SELECT EnrolleeID FROM SHSEnrollees WHERE ClassID = ?");
        } else {
            prep = con.prepareStatement("SELECT EnrolleeID FROM Enrollees WHERE ClassID = ?");
        }
        prep.setLong(1, c.getID());
        ResultSet enrolleesResult = prep.executeQuery();

        PreparedStatement prep2;
        long did = ad.getID();
        if (c.isSHS()) {
            prep2 = con.prepareStatement("INSERT INTO SHSAttendanceList (DayID, EnrolleeID, Remarks, Notes) VALUES (" + did + ", ?, '', '')", Statement.RETURN_GENERATED_KEYS);
        } else {
            prep2 = con.prepareStatement("INSERT INTO AttendanceList (DayID, EnrolleeID, Remarks, Notes) VALUES (" + did + ", ?, '', '')", Statement.RETURN_GENERATED_KEYS);
        }
        ArrayList<AttendanceList> list = new ArrayList<>();
        while (enrolleesResult.next()) {
            prep2.setLong(1, enrolleesResult.getLong(1));
            prep2.executeUpdate();
        }
        return getAttendanceList(ad);
    }

    private static ObservableList<AttendanceList> getAttendanceList(AttendanceDay ad) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Attendance List...");
        ResultSet r;

        PreparedStatement prep;
        if (ad.getClazz().isSHS()) {
            prep = con.prepareStatement("SELECT SHSAttendanceList.AttendanceID, SHSEnrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, SHSAttendanceList.Remarks, SHSAttendanceList.Notes "
                    + "FROM (Students INNER JOIN SHSEnrollees ON Students.StudentID = SHSEnrollees.StudentID) "
                    + "INNER JOIN SHSAttendanceList ON SHSEnrollees.EnrolleeID = SHSAttendanceList.EnrolleeID "
                    + "WHERE SHSAttendanceList.DayID = ?");
        } else {
            prep = con.prepareStatement("SELECT AttendanceList.AttendanceID, Enrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, AttendanceList.Remarks, AttendanceList.Notes "
                    + "FROM (Students INNER JOIN Enrollees ON Students.StudentID = Enrollees.StudentID) "
                    + "INNER JOIN AttendanceList ON Enrollees.EnrolleeID = AttendanceList.EnrolleeID "
                    + "WHERE AttendanceList.DayID = ?");
        }
        prep.setLong(1, ad.getID());
        r = prep.executeQuery();

        ObservableList<AttendanceList> list = FXCollections.observableArrayList();
        while (r.next()) {
            AttendanceList al = new AttendanceList(r.getLong(1));
            al.setAttendanceDay(ad);
            al.setEnrollee(new Enrollee(r.getLong(2)));
            al.getEnrollee().setStudent(new Student(r.getLong(3)));
            al.getEnrollee().getStudent().setFN(r.getString(4));
            al.getEnrollee().getStudent().setLN(r.getString(5));
            al.setRemarks(r.getString(6));
            al.setNotes(r.getString(7));
            list.add(al);
        }
        return list;
    }

    public static boolean save(AttendanceList al) throws SQLException {
        PreparedStatement prep;
        if (al.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new Attendance List...");
            if (al.getAttendanceDay().getClazz().isSHS()) {
                prep = con.prepareStatement("INSERT INTO SHSAttendanceList (DayID, EnrolleeID, Remarks, Notes) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            } else {
                prep = con.prepareStatement("INSERT INTO AttendanceList (DayID, EnrolleeID, Remarks, Notes) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            }
            prep.setLong(1, al.getAttendanceDay().getID());
            prep.setLong(2, al.getEnrollee().getID());
            prep.setString(3, al.getRemarks());
            prep.setString(4, al.getNotes());
            prep.executeUpdate();
            ResultSet rs = prep.getGeneratedKeys();
            rs.next();
            al.setID(rs.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Saving Attendance List...");
            if (al.getAttendanceDay().getClazz().isSHS()) {
                prep = con.prepareStatement("UPDATE SHSAttendanceList SET Remarks = ?, Notes = ? WHERE AttendanceID = ?");
            } else {
                prep = con.prepareStatement("UPDATE AttendanceList SET Remarks = ?, Notes = ? WHERE AttendanceID = ?");
            }
            prep.setString(1, al.getRemarks());
            prep.setString(2, al.getNotes());
            prep.setLong(3, al.getID());
            prep.executeUpdate();
            return false;
        }
    }
    // </editor-fold>
}
