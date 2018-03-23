/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util;

import com.orthocube.classrecord.data.*;
import com.orthocube.classrecord.util.license.Info;
import com.orthocube.classrecord.util.license.KeyByteSet;
import com.orthocube.classrecord.util.license.Utils;
import com.orthocube.classrecord.util.license.verification.LicenseKeyCheck;
import com.orthocube.classrecord.util.license.verification.LicenseKeyResult;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import org.apache.commons.math3.fraction.BigFraction;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows the program to access the database
 * @author OrthoCube
 */
public class DB {
    private final static Logger LOGGER = Logger.getLogger(DB.class.getName());
    private final static String url = "jdbc:derby:database;create=true";
    private final static String DB_VERSION = "4";

    private static boolean isFirstRun = false;
    private static Connection con;

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
                s.executeUpdate("INSERT INTO Settings VALUES ('db_version', '" + DB_VERSION + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFirstRun() {
        return isFirstRun;
    }

    private static void createTables() {
        try {
            LOGGER.log(Level.INFO, "Creating tables...");
            Statement s = con.createStatement();

            try {
                s.executeUpdate("CREATE TABLE Users (UserID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                        "Username VARCHAR(64) UNIQUE, " +
                        "Nickname VARCHAR(64), " +
                        "Password VARCHAR(64) NOT NULL, " +
                        "Picture BLOB(200000), " +
                        "AccessLevel SMALLINT NOT NULL DEFAULT 0, " +
                        "PasswordHint VARCHAR(255))");
            } catch (SQLException e) {
                if (!("X0Y32".equals(e.getSQLState()))) {
                    Dialogs.exception(e);
                }
                LOGGER.log(Level.INFO, "Users table already exists so database is assumed to be all set up.");
                return;
            }

            isFirstRun = true;
            s.executeUpdate("INSERT INTO USERS (Username, Nickname, Password, AccessLevel) VALUES ('admin', 'Administrator', 'admin', 3)");

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
                        "Picture BLOB(200000))");
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
                s.executeUpdate("CREATE TABLE Criteria (CriterionID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
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
                s.executeUpdate("CREATE TABLE SHSCriteria (CriterionID BIGINT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
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
                        "ClassID BIGINT REFERENCES Classes(ClassID) ON DELETE CASCADE" +
                        ", " +
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
                        "CriterionID BIGINT REFERENCES Criteria(CriterionID) ON DELETE CASCADE, " +
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
                        "CriterionID BIGINT REFERENCES SHSCriteria(CriterionID) ON DELETE CASCADE, " +
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
                        "StartTime TIME, " +
                        "EndDate DATE NOT NULL, " +
                        "EndTime TIME, " +
                        "Title VARCHAR(64) NOT NULL, " +
                        "Notes VARCHAR(255) NOT NULL, " +
                        "Location VARCHAR(64) NOT NULL, " +
                        "IsDone SMALLINT NOT NULL)");
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

            try {
                s.executeUpdate("CREATE FUNCTION BitSet(a SMALLINT, b SMALLINT) RETURNS SMALLINT " +
                        "PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA " +
                        "EXTERNAL NAME 'com.orthocube.classrecord.util.Utils.bitSet'");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }

            try {
                s.executeUpdate("CREATE FUNCTION DaysBetween(a DATE, b DATE) RETURNS BIGINT " +
                        "PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA " +
                        "EXTERNAL NAME 'com.orthocube.classrecord.util.Utils.daysBetween'");
            } catch (SQLException e) {
                Dialogs.exception(e);
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

    // <editor-fold defaultstate="collapsed" desc="Settings">
    private static void setSetting(String key, String value) throws SQLException {
        PreparedStatement prep = con.prepareStatement("SELECT SettingKey FROM Settings WHERE SettingKey = ?");
        prep.setString(1, key);
        ResultSet r = prep.executeQuery();
        boolean keyExists = r.next();

        PreparedStatement prep2;
        if (keyExists) {
            prep2 = con.prepareStatement("UPDATE Settings SET SettingValue = ? WHERE SettingKey = ?");
            prep2.setString(1, value);
            prep2.setString(2, key);
        } else {
            prep2 = con.prepareStatement("INSERT INTO Settings (SettingKey, SettingValue) VALUES (?, ?)");
            prep2.setString(1, key);
            prep2.setString(2, value);
        }
        prep2.executeUpdate();
    }

    private static String getSetting(String key) throws SQLException {
        PreparedStatement prep = con.prepareStatement("SELECT SettingValue FROM Settings WHERE SettingKey = ?");
        prep.setString(1, key);
        ResultSet r = prep.executeQuery();
        boolean keyExists = r.next();

        if (keyExists) {
            return r.getString(1);
        } else {
            return null;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Licensing">
    public static LicenseKeyResult hasValidLicense() throws SQLException {
        String failed = getSetting("failedboot");
        if ((failed != null) && (failed.equals("true")))
            return LicenseKeyResult.NEEDSREPAIR;

        String key = getSetting("productkey");
        if (key != null) {
            LicenseKeyResult res = isValidLicense(key);
            if (res != LicenseKeyResult.GOOD) {
                setSetting("failedboot", "true");
            }
            return res;
        } else {
            return LicenseKeyResult.NOTFOUND;
        }
    }

    public static LicenseKeyResult isValidLicense(String productkey) throws SQLException {
        String failed = getSetting("failedboot");
        if ((failed != null) && (failed.equals("true"))) {
            if (Utils.formatKeyForCompare(productkey).equals(Utils.formatKeyForCompare(getSetting("productkey"))))
                return LicenseKeyResult.NEEDSREPAIR;
        }

        LicenseKeyCheck licenseKeyCheck = new LicenseKeyCheck();

        KeyByteSet[] keyBytes = new KeyByteSet[]{
                new KeyByteSet(7, 0xc4, 0x0e, 0xd1),
                new KeyByteSet(8, 0xec, 0x54, 0x91)
        };

        LicenseKeyResult result = licenseKeyCheck.checkKey(productkey, keyBytes, 8, null);
        if (result == LicenseKeyResult.GOOD) {
            Info info = licenseKeyCheck.getDecodedInfo();
            if (info.getLength() == 0)
                return LicenseKeyResult.INVALID;
            if (Info.base.plusMonths(info.getStartMonth()).compareTo(LocalDate.now()) > 0) {
                return LicenseKeyResult.TOOEARLY;
            } else if (Info.base.plusMonths(info.getStartMonth()).plusMonths(info.getLength()).compareTo(LocalDate.now()) < 0) {
                return LicenseKeyResult.EXPIRED;
            } else {
                return result;
            }
        }

        return result;
    }

    public static void setLicense(String s) throws SQLException {
        if (!Utils.formatKeyForCompare(getSetting("productkey")).equals(Utils.formatKeyForCompare(s))) {
            setSetting("productkey", s);
            setSetting("failedboot", "false");
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Students">
    public static ObservableList<Student> getStudents() throws SQLException, IOException {
        LOGGER.log(Level.INFO, "Getting students...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT Students.StudentID, Students.SID, Students.FN, Students.MN, Students.LN, Students.IsFemale, Students.Contact, Students.Address, Students.Notes, Students.Picture FROM Students ORDER BY LN ASC NULLS FIRST, FN ASC NULLS FIRST");
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
                temp.setPicture(SwingFXUtils.toFXImage(ImageIO.read(is), null));
                is.close();
            }
            students.add(temp);
        }

        return students;
    }

    private static Student getStudent(long id) throws SQLException, IOException {
        LOGGER.log(Level.INFO, "Getting student...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT Students.StudentID, Students.SID, Students.FN, Students.MN, Students.LN, Students.IsFemale, Students.Contact, Students.Address, Students.Notes, Students.Picture FROM Students WHERE Students.StudentID = ?");
        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
            Student student = new Student(r.getLong(1));
            student.setSID(r.getString(2));
            student.setFN(r.getString(3));
            student.setMN(r.getString(4));
            student.setLN(r.getString(5));
            student.setFemale(r.getInt(6) > 0);
            student.setContact(r.getString(7));
            student.setAddress(r.getString(8));
            student.setNotes(r.getString(9));
            InputStream is = r.getBinaryStream(10);
            if (is != null) {
                student.setPicture(SwingFXUtils.toFXImage(ImageIO.read(is), null));
                is.close();
            }
            return student;
        } else {
            return null;
        }
    }

    public static boolean save(Student s) throws SQLException, IOException {
        if (s.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new student...");
            byte[] imgbytes;
            if (s.getPicture() != null) {
                ByteArrayOutputStream tempimg = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(s.getPicture(), null), "png", tempimg);
                imgbytes = tempimg.toByteArray();
            } else {
                imgbytes = null;
            }
            if (imgbytes != null)
                LOGGER.log(Level.INFO, "Image size: " + imgbytes.length + " bytes");

            PreparedStatement preps = con.prepareStatement("INSERT INTO Students (Students.SID, Students.FN, Students.MN, Students.LN, Students.isFemale, Students.Contact, Students.Address, Students.Notes, Students.Picture) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preps.setString(1, s.getSID());
            preps.setString(2, s.getFN());
            preps.setString(3, s.getMN());
            preps.setString(4, s.getLN());
            preps.setInt(5, s.isFemale() ? 1 : 0);
            preps.setString(6, s.getContact());
            preps.setString(7, s.getAddress());
            preps.setString(8, s.getNotes());
            preps.setBytes(9, imgbytes);
            preps.executeUpdate();
            ResultSet res = preps.getGeneratedKeys();
            res.next();
            s.setID(res.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Updating student...");
            byte[] imgbytes;
            if (s.getPicture() != null) {
                ByteArrayOutputStream tempimg = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(s.getPicture(), null), "png", tempimg);
                imgbytes = tempimg.toByteArray();
            } else {
                imgbytes = null;
            }
            if (imgbytes != null)
                LOGGER.log(Level.INFO, "Image size: " + imgbytes.length + " bytes");

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
        PreparedStatement prep = con.prepareStatement("SELECT Classes.ClassID, Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes FROM Classes JOIN Enrollees ON Enrollees.ClassID = Classes.ClassID WHERE Enrollees.StudentID = ? ORDER BY Classes.Name ASC NULLS FIRST");
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
        PreparedStatement prep = con.prepareStatement("SELECT Classes.ClassID, Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes FROM Classes JOIN SHSEnrollees ON SHSEnrollees.ClassID = Classes.ClassID WHERE SHSEnrollees.StudentID = ? ORDER BY Classes.Name ASC NULLS FIRST");
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
        PreparedStatement prep = con.prepareStatement("SELECT Classes.ClassID, Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes FROM Classes ORDER BY Classes.Name ASC NULLS FIRST");
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

    public static ObservableList<Clazz> getClasses(int year, int sem) throws SQLException {
        LOGGER.log(Level.INFO, "Getting classes...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT Classes.ClassID, Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes FROM Classes WHERE Classes.SY = ? AND Classes.Sem = ? ORDER BY Classes.Name ASC NULLS FIRST");
        prep.setInt(1, year);
        prep.setInt(2, sem);
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

    private static Clazz getClass(long id) throws SQLException {
        LOGGER.log(Level.INFO, "Getting class...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT Classes.ClassID, Classes.Name, Classes.SY, Classes.Sem, Classes.YearLevel, Classes.Course, Classes.Room, Classes.Days, Classes.Times, Classes.IsSHS, Classes.Notes FROM Classes WHERE ClassID = ?");
        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
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
            return temp;
        } else {
            return null;
        }
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

    public static ObservableList<String> getSchoolYears() throws SQLException {
        LOGGER.log(Level.INFO, "Getting schoolyears...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT DISTINCT Classes.SY FROM Classes");
        r = prep.executeQuery();

        ObservableList<String> years = FXCollections.observableArrayList();
        while (r.next()) {
            int y = r.getInt(1);
            years.add(Integer.toString(y) + " - " + Integer.toString(y + 1));
        }
        return years;
    }

    public static ObservableList<String> getSemesters() throws SQLException {
        LOGGER.log(Level.INFO, "Getting semesters...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT DISTINCT Classes.Sem FROM Classes");
        r = prep.executeQuery();

        ObservableList<String> years = FXCollections.observableArrayList();
        while (r.next()) {
            years.add(Integer.toString(r.getInt(1)));
        }
        return years;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Enrollees">
    public static ObservableList<Enrollee> getEnrollees(Clazz c) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Enrollees...");
        ResultSet r;

        PreparedStatement prep;

        boolean shs = c.isSHS();
        if (shs)
            prep = con.prepareStatement("SELECT SHSEnrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, SHSEnrollees.Notes, SHSEnrollees.Course FROM (Students JOIN SHSEnrollees ON SHSEnrollees.StudentID = Students.StudentID) JOIN Classes ON SHSEnrollees.ClassID = Classes.ClassID WHERE SHSEnrollees.ClassID = ? ORDER BY Students.LN ASC NULLS FIRST, Students.FN ASC NULLS FIRST");
        else {
            prep = con.prepareStatement("SELECT Enrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, Enrollees.ClassCard, Enrollees.Notes, Enrollees.Course FROM (Students JOIN Enrollees ON Enrollees.StudentID = Students.StudentID) JOIN Classes ON Enrollees.ClassID = Classes.ClassID WHERE Enrollees.ClassID = ? ORDER BY Students.LN ASC NULLS FIRST, Students.FN ASC NULLS FIRST");
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

    public static Enrollee getSHSEnrollee(long id) throws SQLException {
        LOGGER.log(Level.INFO, "Getting SHS Enrollee...");
        ResultSet r;

        PreparedStatement prep;

        prep = con.prepareStatement("SELECT SHSEnrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, SHSEnrollees.Notes, SHSEnrollees.Course FROM (Students JOIN SHSEnrollees ON SHSEnrollees.StudentID = Students.StudentID) JOIN Classes ON SHSEnrollees.ClassID = Classes.ClassID WHERE SHSEnrollees.EnrolleeID = ?");

        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
            Enrollee temp = new Enrollee(r.getLong(1));
            temp.setStudent(new Student(r.getLong(2)));
            temp.getStudent().setFN(r.getString(3));
            temp.getStudent().setLN(r.getString(4));
            temp.setNotes(r.getString(5));
            temp.setCourse(r.getString(6));
            return temp;
        } else {
            return null;
        }
    }

    public static Enrollee getEnrollee(long id) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Enrollee...");
        ResultSet r;

        PreparedStatement prep;

        prep = con.prepareStatement("SELECT Enrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, Enrollees.ClassCard, Enrollees.Notes, Enrollees.Course FROM (Students JOIN Enrollees ON Enrollees.StudentID = Students.StudentID) JOIN Classes ON Enrollees.ClassID = Classes.ClassID WHERE Enrollees.EnrolleeID = ?");

        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
            Enrollee enrollee = new Enrollee(r.getLong(1));
            enrollee.setStudent(new Student(r.getLong(2)));
            enrollee.getStudent().setFN(r.getString(3));
            enrollee.getStudent().setLN(r.getString(4));
            enrollee.setClasscard(r.getInt(5));
            enrollee.setNotes(r.getString(6));
            enrollee.setCourse(r.getString(7));
            return enrollee;
        } else {

            return null;
        }
    }

    public static boolean save(Enrollee e) throws SQLException {
        if (e.getID() == -1) {
            if (e.getClazz().isSHS()) {
                LOGGER.log(Level.WARNING, "Creating new SHS enrollee");
                PreparedStatement preps = con.prepareStatement("INSERT INTO SHSEnrollees (ClassID, StudentID, Notes, Course) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                preps.setLong(1, e.getClazz().getID());
                preps.setLong(2, e.getStudent().getID());
                preps.setString(3, e.getNotes());
                preps.setString(4, e.getCourse());
                preps.executeUpdate();
                ResultSet res = preps.getGeneratedKeys();
                res.next();
                e.setID(res.getLong(1));

                // add enrollee to the days where he/she was absent
                PreparedStatement days = con.prepareStatement("SELECT AttendanceDays.DayID FROM AttendanceDays WHERE AttendanceDays.ClassID = ?");
                days.setLong(1, e.getClazz().getID());
                ResultSet daysResult = days.executeQuery();
                PreparedStatement add = con.prepareStatement("INSERT INTO SHSAttendanceList (DayID, EnrolleeID, Remarks, Notes) VALUES (?, ?, 'A', 'Late enrollee')");
                while (daysResult.next()) {
                    add.setLong(1, daysResult.getLong(1));
                    add.setLong(2, e.getID());
                    add.executeUpdate();
                }
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

                // add enrollee to the days where he/she was absent
                PreparedStatement days = con.prepareStatement("SELECT AttendanceDays.DayID FROM AttendanceDays WHERE AttendanceDays.ClassID = ?");
                days.setLong(1, e.getClazz().getID());
                ResultSet daysResult = days.executeQuery();
                PreparedStatement add = con.prepareStatement("INSERT INTO AttendanceList (DayID, EnrolleeID, Remarks, Notes) VALUES (?, ?, 'A', 'Late enrollee')");
                while (daysResult.next()) {
                    add.setLong(1, daysResult.getLong(1));
                    add.setLong(2, e.getID());
                    add.executeUpdate();
                }
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

    public static boolean enrolleeHasScoreIn(Enrollee e, Task t) throws SQLException {
        LOGGER.log(Level.INFO, "Checking if enrollee already has a score in this task...");
        ResultSet r;
        PreparedStatement ps;
        if (e.getClazz().isSHS()) {
            ps = con.prepareStatement("SELECT ScoreID FROM SHSScores WHERE EnrolleeID = ? AND TaskID = ?");
            ps.setLong(1, e.getID());
            ps.setLong(2, t.getID());
        } else {
            ps = con.prepareStatement("SELECT ScoreID FROM Scores WHERE EnrolleeID = ? AND TaskID = ?");
            ps.setLong(1, e.getID());
            ps.setLong(2, t.getID());
        }
        r = ps.executeQuery();
        return r.next();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Criteria">
    public static ObservableList<Criterion> getCriteria(Clazz c) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Criteria...");
        ResultSet r;

        PreparedStatement prep;

        boolean shs = c.isSHS();
        if (shs)
            prep = con.prepareStatement("SELECT SHSCriteria.CriterionID, SHSCriteria.Name, SHSCriteria.Percent, SHSCriteria.Terms FROM SHSCriteria WHERE SHSCriteria.ClassID = ?");
        else {
            prep = con.prepareStatement("SELECT Criteria.CriterionID, Criteria.Name, Criteria.Percent, Criteria.Terms FROM Criteria WHERE Criteria.ClassID = ?");
        }

        prep.setLong(1, c.getID());
        r = prep.executeQuery();

        ArrayList<Criterion> criteria = new ArrayList<>();
        while (r.next()) {
            Criterion temp = new Criterion(r.getLong(1));
            temp.setClass(c);
            temp.setName(r.getString(2));
            temp.setPercentage(r.getInt(3));
            temp.setTerms(r.getInt(4));
            criteria.add(temp);
        }

        return FXCollections.observableList(criteria, (Criterion cr) -> new Observable[]{cr.nameProperty(), cr.percentageProperty(), cr.termsProperty()});
    }

    private static Criterion getCriterion(long id) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Criterion...");
        ResultSet r;

        PreparedStatement prep;

        prep = con.prepareStatement("SELECT Criteria.CriterionID, Criteria.Name, Criteria.Percent, Criteria.Terms FROM Criteria WHERE Criteria.CriterionID = ? ORDER BY Criteria.Percent ASC NULLS FIRST");

        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
            Criterion temp = new Criterion(r.getLong(1));
            temp.setName(r.getString(2));
            temp.setPercentage(r.getInt(3));
            temp.setTerms(r.getInt(4));
            return temp;
        } else {
            return null;
        }

    }

    private static Criterion getSHSCriterion(long id) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Criterion...");
        ResultSet r;

        PreparedStatement prep;

        prep = con.prepareStatement("SELECT SHSCriteria.CriterionID, SHSCriteria.Name, SHSCriteria.Percent, SHSCriteria.Terms FROM SHSCriteria WHERE SHSCriteria.CriterionID = ? ORDER BY SHSCriteria.Percent ASC NULLS FIRST");

        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
            Criterion temp = new Criterion(r.getLong(1));
            temp.setName(r.getString(2));
            temp.setPercentage(r.getInt(3));
            temp.setTerms(r.getInt(4));
            return temp;
        } else {
            return null;
        }

    }

    public static boolean save(Criterion c) throws SQLException {
        if (c.getID() == -1) {
            LOGGER.log(Level.WARNING, "Creating new Criterion");
            PreparedStatement preps;
            if (c.getClazz().isSHS()) {
                preps = con.prepareStatement("INSERT INTO SHSCriteria (ClassID, Name, Percent, Terms) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            } else {
                preps = con.prepareStatement("INSERT INTO Criteria (ClassID, Name, Percent, Terms) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
            LOGGER.log(Level.INFO, "Updating Criterion...");
            PreparedStatement prep;
            if (c.getClazz().isSHS()) {
                prep = con.prepareStatement("UPDATE SHSCriteria SET ClassID = ?, Name = ?, Percent = ?, Terms = ? WHERE CriterionID = ?");
            } else {
                prep = con.prepareStatement("UPDATE Criteria SET ClassID = ?, Name = ?, Percent = ?, Terms = ? WHERE CriterionID = ?");
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

    public static void delete(Criterion c) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting Criterion...");
        PreparedStatement prep;
        if (c.getClazz().isSHS()) {
            prep = con.prepareStatement("DELETE FROM SHSCriteria WHERE CriterionID = ?");
        } else {
            prep = con.prepareStatement("DELETE FROM Criteria WHERE CriterionID = ?");
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
        prep = con.prepareStatement("SELECT DayID, Date, Notes FROM AttendanceDays WHERE ClassID = ? ORDER BY Date ASC NULLS FIRST");
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

    public static long dayAlreadyExists(AttendanceDay ad) throws SQLException {
        PreparedStatement prep;
        if (ad.getID() == -1) {
            prep = con.prepareStatement("SELECT DayID FROM AttendanceDays WHERE Date = ? AND ClassID = ?");
            prep.setDate(1, ad.getDate());
            prep.setLong(2, ad.getClazz().getID());
            ResultSet rs = prep.executeQuery();
            if (rs.next())
                return rs.getLong(1);
            else return 0;
        } else {
            return 1;
        }
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
            LOGGER.log(Level.INFO, "Saving Attendance Day...");
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
            prep = con.prepareStatement("SELECT SHSAttendanceList.AttendanceID, SHSEnrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, SHSEnrollees.Course, SHSAttendanceList.Remarks, SHSAttendanceList.Notes "
                    + "FROM (Students INNER JOIN SHSEnrollees ON Students.StudentID = SHSEnrollees.StudentID) "
                    + "INNER JOIN SHSAttendanceList ON SHSEnrollees.EnrolleeID = SHSAttendanceList.EnrolleeID "
                    + "WHERE SHSAttendanceList.DayID = ? ORDER BY Students.LN ASC NULLS FIRST, Students.FN ASC NULLS FIRST");
        } else {
            prep = con.prepareStatement("SELECT AttendanceList.AttendanceID, Enrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, Enrollees.Course, AttendanceList.Remarks, AttendanceList.Notes "
                    + "FROM (Students INNER JOIN Enrollees ON Students.StudentID = Enrollees.StudentID) "
                    + "INNER JOIN AttendanceList ON Enrollees.EnrolleeID = AttendanceList.EnrolleeID "
                    + "WHERE AttendanceList.DayID = ? ORDER BY Students.LN ASC NULLS FIRST, Students.FN ASC NULLS FIRST");
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
            al.getEnrollee().setCourse(r.getString(6));
            al.setRemarks(r.getString(7));
            al.setNotes(r.getString(8));
            list.add(al);
        }
        return list;
    }

    public static void save(AttendanceList al) throws SQLException {
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
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Tasks">
    public static ObservableList<Task> getTasks(Clazz c) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Tasks...");
        ResultSet r;

        PreparedStatement prep;
        if (c.isSHS()) {
            prep = con.prepareStatement("SELECT SHSTasks.TaskID, SHSTasks.Name, SHSTasks.Term, SHSTasks.Items, SHSCriteria.CriterionID, SHSCriteria.Name FROM SHSTasks INNER JOIN SHSCriteria ON SHSTasks.CriterionID = SHSCriteria.CriterionID WHERE SHSTasks.ClassID = ? ORDER BY SHSCriteria.Percent ASC NULLS FIRST");
        } else {
            prep = con.prepareStatement("SELECT Tasks.TaskID, Tasks.Name, Tasks.Term, Tasks.Items, Criteria.CriterionID, Criteria.Name FROM Tasks INNER JOIN Criteria ON Tasks.CriterionID = Criteria.CriterionID WHERE Tasks.ClassID = ? ORDER BY Criteria.Percent ASC NULLS FIRST");
        }
        prep.setLong(1, c.getID());
        r = prep.executeQuery();

        ObservableList<Task> tasks = FXCollections.observableArrayList();
        while (r.next()) {
            Task temp = new Task(r.getLong(1));
            temp.setName(r.getString(2));
            temp.setTerm(r.getInt(3));
            temp.setItems(r.getInt(4));
            temp.setClass(c);
            temp.setCriterion(new Criterion(r.getLong(5)));
            temp.getCriterion().setName(r.getString(6));
            temp.setScores(getScores(temp));
            tasks.add(temp);
            for (Score score : temp.getScores()) {
                score.setTask(temp);
            }
        }
        return tasks;
    }

    private static Task getTask(long id) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Task...");
        ResultSet r;

        PreparedStatement prep = con.prepareStatement("SELECT Tasks.TaskID, Tasks.Name, Tasks.Term, Tasks.Items, Tasks.ClassID, Criteria.CriterionID, Criteria.Name FROM Tasks INNER JOIN Criteria ON Tasks.CriterionID = Criteria.CriterionID WHERE Tasks.TaskID = ?");
        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
            Task temp = new Task(r.getLong(1));
            temp.setName(r.getString(2));
            temp.setTerm(r.getInt(3));
            temp.setItems(r.getInt(4));
            temp.setClass(getClass(r.getLong(5)));
            temp.setCriterion(new Criterion(r.getLong(6)));
            temp.getCriterion().setName(r.getString(7));
            temp.setScores(getScores(temp));
            for (Score score : temp.getScores()) {
                score.setTask(temp);
            }
            return temp;
        } else {
            return null;
        }
    }

    private static Task getSHSTask(long id) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Task...");
        ResultSet r;

        PreparedStatement prep;
        prep = con.prepareStatement("SELECT SHSTasks.TaskID, SHSTasks.Name, SHSTasks.Term, SHSTasks.Items, SHSTasks.ClassID, SHSCriteria.CriterionID, SHSCriteria.Name FROM SHSTasks INNER JOIN SHSCriteria ON SHSTasks.CriterionID = SHSCriteria.CriterionID WHERE SHSTasks.TaskID = ?");
        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
            Task temp = new Task(r.getLong(1));
            temp.setName(r.getString(2));
            temp.setTerm(r.getInt(3));
            temp.setItems(r.getInt(4));
            temp.setClass(getClass(r.getLong(5)));
            temp.setCriterion(new Criterion(r.getLong(6)));
            temp.getCriterion().setName(r.getString(7));
            temp.setScores(getScores(temp));
            for (Score score : temp.getScores()) {
                score.setTask(temp);
            }
            return temp;
        } else {
            return null;
        }
    }

    public static boolean save(Task t) throws SQLException {
        if (t.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new Task...");
            PreparedStatement prep;
            if (t.getClazz().isSHS()) {
                prep = con.prepareStatement("INSERT INTO SHSTasks (Name, ClassID, CriterionID, Term, Items) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            } else {
                prep = con.prepareStatement("INSERT INTO Tasks (Name, ClassID, CriterionID, Term, Items) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            }
            prep.setString(1, t.getName());
            prep.setLong(2, t.getClazz().getID());
            prep.setLong(3, t.getCriterion().getID());
            prep.setInt(4, t.getTerm());
            prep.setInt(5, t.getItems());
            prep.executeUpdate();
            ResultSet rs = prep.getGeneratedKeys();
            rs.next();
            t.setID(rs.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Saving Task...");
            PreparedStatement prep;
            if (t.getClazz().isSHS()) {
                prep = con.prepareStatement("Update SHSTasks SET Name = ?, CriterionID = ?, Term = ?, Items = ? WHERE TaskID = ?");
            } else {
                prep = con.prepareStatement("Update Tasks SET Name = ?, CriterionID = ?, Term = ?, Items = ? WHERE TaskID = ?");
            }
            prep.setString(1, t.getName());
            prep.setLong(2, t.getCriterion().getID());
            prep.setInt(3, t.getTerm());
            prep.setInt(4, t.getItems());
            prep.setLong(5, t.getID());
            prep.executeUpdate();
            return false;
        }
    }

    public static void delete(Task t) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting Task...");
        PreparedStatement prep;
        if (t.getClazz().isSHS()) {
            prep = con.prepareStatement("DELETE FROM SHSTasks WHERE TaskID = ?");
        } else {
            prep = con.prepareStatement("DELETE FROM Tasks WHERE TaskID = ?");
        }
        prep.setLong(1, t.getID());
        prep.executeUpdate();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Scores">
    private static ObservableList<Score> getScores(Task t) throws SQLException {
        LOGGER.log(Level.INFO, "Getting Scores...");
        ResultSet r;

        PreparedStatement prep;
        if (t.getClazz().isSHS()) {
            prep = con.prepareStatement("SELECT SHSScores.ScoreID, SHSEnrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, SHSScores.Score, SHSScores.Notes "
                    + "FROM (Students INNER JOIN SHSEnrollees ON Students.StudentID = SHSEnrollees.StudentID) "
                    + "INNER JOIN SHSScores ON SHSEnrollees.EnrolleeID = SHSScores.EnrolleeID "
                    + "WHERE SHSScores.TaskID = ? ORDER BY Students.LN ASC NULLS FIRST, Students.FN ASC NULLS FIRST");
        } else {
            prep = con.prepareStatement("SELECT Scores.ScoreID, Enrollees.EnrolleeID, Students.StudentID, Students.FN, Students.LN, Scores.Score, Scores.Notes "
                    + "FROM (Students INNER JOIN Enrollees ON Students.StudentID = Enrollees.StudentID) "
                    + "INNER JOIN Scores ON Enrollees.EnrolleeID = Scores.EnrolleeID "
                    + "WHERE Scores.TaskID = ? ORDER BY Students.LN ASC NULLS FIRST, Students.FN ASC NULLS FIRST");
        }
        prep.setLong(1, t.getID());
        r = prep.executeQuery();

        ObservableList<Score> scores = FXCollections.observableArrayList();
        while (r.next()) {
            Score temp = new Score(r.getLong(1));
            temp.setEnrollee(new Enrollee(r.getLong(2)));
            temp.getEnrollee().setStudent(new Student(r.getLong(3)));
            temp.getEnrollee().getStudent().setFN(r.getString(4));
            temp.getEnrollee().getStudent().setLN(r.getString(5));
            temp.setScore(r.getInt(6));
            temp.setNotes(r.getString(7));
            scores.add(temp);
        }
        return scores;
    }

    public static boolean save(Score s) throws SQLException {
        if (s.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new Score...");
            PreparedStatement prep;
            if (s.getTask().getClazz().isSHS()) {
                prep = con.prepareStatement("INSERT INTO SHSScores (EnrolleeID, TaskID, Score, Notes) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            } else {
                prep = con.prepareStatement("INSERT INTO Scores (EnrolleeID, TaskID, Score, Notes) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            }
            prep.setLong(1, s.getEnrollee().getID());
            prep.setLong(2, s.getTask().getID());
            prep.setInt(3, s.getScore());
            prep.setString(4, s.getNotes());
            prep.executeUpdate();
            ResultSet rs = prep.getGeneratedKeys();
            rs.next();
            s.setID(rs.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Saving Score...");
            PreparedStatement prep;
            if (s.getTask().getClazz().isSHS()) {
                prep = con.prepareStatement("UPDATE SHSScores SET EnrolleeID = ?, Score = ?, Notes = ? WHERE ScoreID = ?");
            } else {
                prep = con.prepareStatement("UPDATE Scores SET EnrolleeID = ?, Score = ?, Notes = ? WHERE ScoreID = ?");
            }
            prep.setLong(1, s.getEnrollee().getID());
            prep.setInt(2, s.getScore());
            prep.setString(3, s.getNotes());
            prep.setLong(4, s.getID());
            prep.executeUpdate();
            return false;
        }
    }

    public static void delete(Score s) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting Score...");
        PreparedStatement prep;
        if (s.getTask().getClazz().isSHS()) {
            prep = con.prepareStatement("DELETE FROM SHSScores WHERE ScoreID = ?");
        } else {
            prep = con.prepareStatement("DELETE FROM Scores WHERE ScoreID = ?");
        }
        prep.setLong(1, s.getID());
        prep.executeUpdate();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Users">
    public static ObservableList<User> getUsers() throws SQLException, IOException {
        LOGGER.log(Level.INFO, "Getting users...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT UserID, Username, Nickname, Picture, AccessLevel FROM Users ORDER BY Username ASC NULLS FIRST");
        r = prep.executeQuery();

        ObservableList<User> users = FXCollections.observableArrayList();

        while (r.next()) {
            User temp = new User(r.getLong(1));
            temp.setUsername(r.getString(2));
            temp.setNickname(r.getString(3));
            InputStream is = r.getBinaryStream(4);
            if (is != null) {
                temp.setPicture(SwingFXUtils.toFXImage(ImageIO.read(is), null));
                is.close();
            }
            temp.setAccessLevel(r.getInt(5));
            users.add(temp);
        }
        return users;
    }

    public static User getUser(String username, String password) throws SQLException, IOException {
        LOGGER.log(Level.INFO, "Getting users...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT UserID, Username, Nickname, Picture, AccessLevel FROM Users WHERE Username = ? AND Password = ?");
        prep.setString(1, username);
        prep.setString(2, password);
        r = prep.executeQuery();

        if (r.next()) {
            User temp = new User(r.getLong(1));
            temp.setUsername(r.getString(2));
            temp.setNickname(r.getString(3));
            InputStream is = r.getBinaryStream(4);
            if (is != null) {
                temp.setPicture(SwingFXUtils.toFXImage(ImageIO.read(is), null));
                is.close();
            }
            temp.setAccessLevel(r.getInt(5));
            return temp;
        } else {
            return null;
        }
    }

    public static User getUser(long id) throws SQLException, IOException {
        LOGGER.log(Level.INFO, "Getting user...");
        ResultSet r;
        PreparedStatement prep = con.prepareStatement("SELECT UserID, Username, Nickname, Picture, AccessLevel FROM Users WHERE UserID = ?");
        prep.setLong(1, id);
        r = prep.executeQuery();

        if (r.next()) {
            User temp = new User(r.getLong(1));
            temp.setUsername(r.getString(2));
            temp.setNickname(r.getString(3));
            InputStream is = r.getBinaryStream(4);
            if (is != null) {
                temp.setPicture(SwingFXUtils.toFXImage(ImageIO.read(is), null));
                is.close();
            }
            temp.setAccessLevel(r.getInt(5));
            return temp;
        } else {
            return null;
        }
    }

    public static boolean save(User u) throws SQLException, IOException {

        LOGGER.log(Level.INFO, "Updating user...");
        byte[] imgbytes = null;
        if (u.getPicture() != null) {
            ByteArrayOutputStream tempimg = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(u.getPicture(), null), "png", tempimg);
            imgbytes = tempimg.toByteArray();
        }

        PreparedStatement preps = con.prepareStatement("UPDATE Users SET Username = ?, Nickname = ?, Picture = ?, AccessLevel = ? WHERE UserID = ?");
        preps.setString(1, u.getUsername());
        preps.setString(2, u.getNickname());
        preps.setBytes(3, imgbytes);
        preps.setInt(4, u.getAccessLevel());
        preps.setLong(5, u.getID());
        preps.executeUpdate();

        return false;
    }

    public static boolean save(User u, String password) throws SQLException, IOException {
        if (u.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new user...");
            byte[] imgbytes = null;
            if (u.getPicture() != null) {
                ByteArrayOutputStream tempimg = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(u.getPicture(), null), "png", tempimg);
                imgbytes = tempimg.toByteArray();
            }

            PreparedStatement preps = con.prepareStatement("INSERT INTO Users (Username, Nickname, Password, Picture, AccessLevel) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preps.setString(1, u.getUsername());
            preps.setString(2, u.getNickname());
            preps.setString(3, password);
            preps.setBytes(4, imgbytes);
            preps.setInt(5, u.getAccessLevel());
            preps.executeUpdate();
            ResultSet rs = preps.getGeneratedKeys();
            rs.next();
            u.setID(rs.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Updating user with new password...");
            byte[] imgbytes = null;
            if (u.getPicture() != null) {
                ByteArrayOutputStream tempimg = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(u.getPicture(), null), "png", tempimg);
                imgbytes = tempimg.toByteArray();
            }

            PreparedStatement preps = con.prepareStatement("UPDATE Users SET Username = ?, Nickname = ?, Password = ?, Picture = ?, AccessLevel = ? WHERE UserID = ?");
            preps.setString(1, u.getUsername());
            preps.setString(2, u.getNickname());
            preps.setString(3, password);
            preps.setBytes(4, imgbytes);
            preps.setInt(5, u.getAccessLevel());
            preps.setLong(6, u.getID());
            preps.executeUpdate();
            return false;
        }
    }

    public static void delete(User u) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting user...");
        PreparedStatement prep = con.prepareStatement("DELETE FROM Users WHERE UserID = ?");
        prep.setLong(1, u.getID());
        prep.executeUpdate();
    }

    public static boolean userExists(String u) throws SQLException {
        LOGGER.log(Level.INFO, "Checking if user exists...");
        ResultSet r;
        PreparedStatement ps = con.prepareStatement("SELECT UserID FROM Users WHERE Username = ?");
        ps.setString(1, u);
        r = ps.executeQuery();
        return r.next();
    }

    public static boolean userExists(String u, String p) throws SQLException {
        LOGGER.log(Level.INFO, "Checking if user exists...");
        ResultSet r;
        PreparedStatement ps = con.prepareStatement("SELECT UserID FROM Users WHERE Username = ? AND Password = ?");
        ps.setString(1, u);
        ps.setString(2, p);
        r = ps.executeQuery();
        return r.next();
    }

    public static String getPasswordHint(String u) throws SQLException {
        LOGGER.log(Level.INFO, "Getting password hint...");
        ResultSet r;
        PreparedStatement ps = con.prepareStatement("SELECT PasswordHint FROM Users WHERE Username = ?");
        ps.setString(1, u);
        r = ps.executeQuery();
        if (r.next())
            return r.getString(1);
        else
            return null;
    }
    // </editor-fold>

    // <editor-fold defaultstatus="collapsed" desc="Grades">
    public static ObservableList<Grade> getGrades(Clazz c) throws SQLException, IOException {
        if (c.isSHS()) {
            return getSHSGrades(c);
        } else {
            return getCollegeGrades(c);
        }
    }

    public static ObservableList<Grade> getCollegeGrades(Clazz c) throws SQLException, IOException {
        ObservableList<Grade> grades = FXCollections.observableArrayList();

        ResultSet r1, r2, r3, r4;
        PreparedStatement prep1, prep2, prep3, prep4;

        prep1 = con.prepareStatement("SELECT Enrollees.EnrolleeID FROM Enrollees LEFT JOIN Students ON Enrollees.StudentID = Students.StudentID WHERE Enrollees.ClassID = ? ORDER BY Students.LN ASC NULLS FIRST, Students.FN ASC NULLS FIRST");
        prep1.setLong(1, c.getID());
        r1 = prep1.executeQuery();

        Student currentStudent;
        Enrollee currentEnrollee;
        Criterion currentCriterion;
        Task currentTask;

        ArrayList<ArrayList<com.orthocube.classrecord.util.grade.Criterion>> terms = new ArrayList<>();
        terms.add(new ArrayList<>());
        terms.add(new ArrayList<>());
        terms.add(new ArrayList<>());
        terms.add(new ArrayList<>());

        while (r1.next()) {
            // Here we get the preliminary student data.
            currentEnrollee = getEnrollee(r1.getLong(1));
            assert currentEnrollee != null;
            currentStudent = getStudent(currentEnrollee.getStudent().getID());

            // Here, we start getting the data for for each term. =======================================================
            for (int t = 0; t < 4; t++) {
                prep2 = con.prepareStatement("SELECT Criteria.CriterionID FROM Criteria WHERE ClassID = ? AND BitSet(Terms, ?) > 0");
                prep2.setLong(1, c.getID());
                prep2.setInt(2, t);
                r2 = prep2.executeQuery();
                terms.set(t, new ArrayList<>());

                while (r2.next()) {
                    currentCriterion = getCriterion(r2.getInt(1));

                    // for every criterion... we add the criterion to PRELIM
                    assert currentCriterion != null;
                    terms.get(t).add(new com.orthocube.classrecord.util.grade.Criterion(currentCriterion.getPercentage()));

                    // then we get the tasks associated with that criterion
                    prep3 = con.prepareStatement("SELECT Tasks.TaskID FROM Tasks WHERE CriterionID = ? AND BitSet(Term, ?) > 0");
                    prep3.setLong(1, currentCriterion.getID());
                    prep3.setInt(2, t);
                    r3 = prep3.executeQuery();

                    while (r3.next()) {
                        currentTask = getTask(r3.getInt(1));
                        // we look for the student's grade in that task...
                        prep4 = con.prepareStatement("SELECT Score FROM Scores WHERE EnrolleeID = ? AND TaskID = ?");
                        prep4.setLong(1, currentEnrollee.getID());
                        assert currentTask != null;
                        prep4.setLong(2, currentTask.getID());
                        r4 = prep4.executeQuery();
                        int score = 0;
                        while (r4.next()) {
                            // we get the student's score
                            score = r4.getInt(1);
                        }
                        // for every task... we add it to the last criterion in PRELIM
                        terms.get(t).get(terms.get(t).size() - 1).addTask(score, currentTask.getItems());
                    }
                }
            }

            // by this point, I HOPE that all the tasks inside the criteria inside the terms HAVE the right data,
            // so let's hope for the best and compute it.
            ArrayList<String> termStrings = new ArrayList<>();
            ArrayList<BigFraction> termFractions = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                termStrings.add("ERROR");
                termFractions.add(null);
            }
            for (int term = 0; term < 4; term++) {
                BigFraction total = BigFraction.ZERO;
                for (com.orthocube.classrecord.util.grade.Criterion criterion : terms.get(term)) {
                    total = total.add(criterion.getGrade());
                }
                //termStrings.set(term, Double.toString(total));
                termStrings.set(term, roundCollegeGrade(total));
                termFractions.set(term, roundCollegeGradeFraction(total));
            }

            Grade temp = new Grade();
            assert currentStudent != null;
            temp.setFName(currentStudent.getFN());
            temp.setLName(currentStudent.getLN());
            temp.setPrelim(termStrings.get(0));
            temp.setMidterms(termStrings.get(1));
            temp.setSemis(termStrings.get(2));
            temp.setFinals(termStrings.get(3));
            temp.setFinal(finalCollegeGrade(termFractions));
            temp.setClassCard(currentEnrollee.getClasscard());
            temp.setCourse(currentEnrollee.getCourse());
            grades.add(temp);
        }

        return grades;
    }

    private static String roundCollegeGrade(BigFraction g) {
        String r;
        if (g.compareTo(new BigFraction(1.124)) <= 0) {
            r = "1.00";
        } else if (g.compareTo(new BigFraction(1.374)) <= 0) {
            r = "1.25";
        } else if (g.compareTo(new BigFraction(1.624)) <= 0) {
            r = "1.50";
        } else if (g.compareTo(new BigFraction(1.874)) <= 0) {
            r = "1.75";
        } else if (g.compareTo(new BigFraction(2.124)) <= 0) {
            r = "2.00";
        } else if (g.compareTo(new BigFraction(2.374)) <= 0) {
            r = "2.25";
        } else if (g.compareTo(new BigFraction(2.624)) <= 0) {
            r = "2.50";
        } else if (g.compareTo(new BigFraction(2.874)) <= 0) {
            r = "2.75";
        } else if (g.compareTo(new BigFraction(3.44)) <= 0) {
            r = "3.00";
        } else {
            r = "5.00";
        }
        return r;
    }

    private static BigFraction roundCollegeGradeFraction(BigFraction g) {
        double r;
        if (g.compareTo(new BigFraction(1.124)) <= 0) {
            r = 1.00;
        } else if (g.compareTo(new BigFraction(1.374)) <= 0) {
            r = 1.25;
        } else if (g.compareTo(new BigFraction(1.624)) <= 0) {
            r = 1.50;
        } else if (g.compareTo(new BigFraction(1.874)) <= 0) {
            r = 1.75;
        } else if (g.compareTo(new BigFraction(2.124)) <= 0) {
            r = 2.00;
        } else if (g.compareTo(new BigFraction(2.374)) <= 0) {
            r = 2.25;
        } else if (g.compareTo(new BigFraction(2.624)) <= 0) {
            r = 2.50;
        } else if (g.compareTo(new BigFraction(2.874)) <= 0) {
            r = 2.75;
        } else if (g.compareTo(new BigFraction(3.44)) <= 0) {
            r = 3.00;
        } else {
            r = 5.00;
        }
        return new BigFraction(r);
    }

    private static String finalCollegeGrade(ArrayList<BigFraction> l) {
        BigFraction total = BigFraction.ZERO;
        for (BigFraction g : l) {
            total = total.add(g);
        }
        BigFraction average = total.divide(l.size());
        return roundCollegeGrade(average);
    }

    public static ObservableList<Grade> getSHSGrades(Clazz c) throws SQLException, IOException {
        ObservableList<Grade> grades = FXCollections.observableArrayList();

        ResultSet r1, r2, r3, r4;
        PreparedStatement prep1, prep2, prep3, prep4;

        prep1 = con.prepareStatement("SELECT SHSEnrollees.EnrolleeID FROM SHSEnrollees LEFT JOIN Students ON SHSEnrollees.StudentID = Students.StudentID WHERE SHSEnrollees.ClassID = ? ORDER BY Students.IsFemale ASC NULLS FIRST, Students.LN ASC NULLS FIRST, Students.FN ASC NULLS FIRST");
        prep1.setLong(1, c.getID());
        r1 = prep1.executeQuery();

        Student currentStudent;
        Enrollee currentEnrollee;
        Criterion currentCriterion;
        Task currentTask;

        ArrayList<ArrayList<com.orthocube.classrecord.util.grade.SHSCriterion>> terms = new ArrayList<>();
        terms.add(new ArrayList<>());
        terms.add(new ArrayList<>());

        while (r1.next()) {
            // Here we get the preliminary student data.
            currentEnrollee = getSHSEnrollee(r1.getLong(1));
            assert currentEnrollee != null;
            currentStudent = getStudent(currentEnrollee.getStudent().getID());

            // Here, we start getting the data for for each term. =======================================================
            for (int t = 0; t < 2; t++) {
                prep2 = con.prepareStatement("SELECT SHSCriteria.CriterionID FROM SHSCriteria WHERE ClassID = ? AND BitSet(Terms, (?*2+1)) > 0");
                prep2.setLong(1, c.getID());
                prep2.setInt(2, t);
                r2 = prep2.executeQuery();
                terms.set(t, new ArrayList<>());

                while (r2.next()) {
                    currentCriterion = getSHSCriterion(r2.getInt(1));

                    // for every criterion... we add the criterion to PRELIM
                    assert currentCriterion != null;
                    terms.get(t).add(new com.orthocube.classrecord.util.grade.SHSCriterion(currentCriterion.getPercentage()));

                    // then we get the tasks associated with that criterion
                    prep3 = con.prepareStatement("SELECT SHSTasks.TaskID FROM SHSTasks WHERE CriterionID = ? AND BitSet(Term, ?) > 0");
                    prep3.setLong(1, currentCriterion.getID());
                    prep3.setInt(2, t);
                    r3 = prep3.executeQuery();

                    while (r3.next()) {
                        currentTask = getSHSTask(r3.getInt(1));
                        // we look for the student's grade in that task...
                        prep4 = con.prepareStatement("SELECT Score FROM SHSScores WHERE EnrolleeID = ? AND TaskID = ?");
                        prep4.setLong(1, currentEnrollee.getID());
                        assert currentTask != null;
                        prep4.setLong(2, currentTask.getID());
                        r4 = prep4.executeQuery();
                        int score = 0;
                        while (r4.next()) {
                            // we get the student's score
                            score = r4.getInt(1);
                        }
                        // for every task... we add it to the last criterion in PRELIM
                        terms.get(t / 2).get(terms.get(t / 2).size() - 1).addTask(score, currentTask.getItems());
                    }
                }
            }

            // by this point, I HOPE that all the tasks inside the criteria inside the terms HAVE the right data,
            // so let's hope for the best and compute it.
            ArrayList<String> termStrings = new ArrayList<>();
            ArrayList<BigFraction> termFractions = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                termStrings.add("ERROR");
                termFractions.add(null);
            }

            for (int term = 0; term < 2; term++) {
                BigFraction total = BigFraction.ZERO;
                for (com.orthocube.classrecord.util.grade.SHSCriterion criterion : terms.get(term)) {
                    total = total.add(criterion.getGrade());
                }
                //termStrings.set(term, Double.toString(total));
                termStrings.set(term, roundSHSGrade(total));
                termFractions.set(term, new BigFraction(Integer.parseInt(termStrings.get(term)), 1));
            }

            Grade temp = new Grade();
            assert currentStudent != null;
            temp.setFName(currentStudent.getFN());
            temp.setLName(currentStudent.getLN());
            temp.setMidterms(termStrings.get(0));
            temp.setFinals(termStrings.get(1));
            temp.setFinal(finalSHSGrade(termFractions));
            temp.setCourse(currentEnrollee.getCourse());
            grades.add(temp);
        }

        return grades;
    }

    private static String roundSHSGrade(BigFraction g) {
        String r;
        if (g.compareTo(new BigFraction(100, 1)) == 0) {
            r = "100";
        } else if (g.compareTo(new BigFraction(98.40)) >= 0) {
            r = "99";
        } else if (g.compareTo(new BigFraction(96.80)) >= 0) {
            r = "98";
        } else if (g.compareTo(new BigFraction(95.20)) >= 0) {
            r = "97";
        } else if (g.compareTo(new BigFraction(93.60)) >= 0) {
            r = "96";
        } else if (g.compareTo(new BigFraction(92.00)) >= 0) {
            r = "95";
        } else if (g.compareTo(new BigFraction(90.40)) >= 0) {
            r = "94";
        } else if (g.compareTo(new BigFraction(88.80)) >= 0) {
            r = "93";
        } else if (g.compareTo(new BigFraction(87.20)) >= 0) {
            r = "92";
        } else if (g.compareTo(new BigFraction(85.60)) >= 0) {
            r = "91";
        } else if (g.compareTo(new BigFraction(84.00)) >= 0) {
            r = "90";
        } else if (g.compareTo(new BigFraction(82.40)) >= 0) {
            r = "89";
        } else if (g.compareTo(new BigFraction(80.80)) >= 0) {
            r = "88";
        } else if (g.compareTo(new BigFraction(79.20)) >= 0) {
            r = "87";
        } else if (g.compareTo(new BigFraction(77.60)) >= 0) {
            r = "86";
        } else if (g.compareTo(new BigFraction(76.00)) >= 0) {
            r = "85";
        } else if (g.compareTo(new BigFraction(74.40)) >= 0) {
            r = "84";
        } else if (g.compareTo(new BigFraction(72.80)) >= 0) {
            r = "83";
        } else if (g.compareTo(new BigFraction(71.20)) >= 0) {
            r = "82";
        } else if (g.compareTo(new BigFraction(69.60)) >= 0) {
            r = "81";
        } else if (g.compareTo(new BigFraction(68.00)) >= 0) {
            r = "80";
        } else if (g.compareTo(new BigFraction(66.40)) >= 0) {
            r = "79";
        } else if (g.compareTo(new BigFraction(64.80)) >= 0) {
            r = "78";
        } else if (g.compareTo(new BigFraction(63.20)) >= 0) {
            r = "77";
        } else if (g.compareTo(new BigFraction(61.60)) >= 0) {
            r = "76";
        } else if (g.compareTo(new BigFraction(60.00)) >= 0) {
            r = "75";
        } else if (g.compareTo(new BigFraction(56.00)) >= 0) {
            r = "74";
        } else if (g.compareTo(new BigFraction(52.00)) >= 0) {
            r = "73";
        } else if (g.compareTo(new BigFraction(48.00)) >= 0) {
            r = "72";
        } else if (g.compareTo(new BigFraction(44.00)) >= 0) {
            r = "71";
        } else if (g.compareTo(new BigFraction(40.00)) >= 0) {
            r = "70";
        } else if (g.compareTo(new BigFraction(36.00)) >= 0) {
            r = "69";
        } else if (g.compareTo(new BigFraction(32.00)) >= 0) {
            r = "68";
        } else if (g.compareTo(new BigFraction(28.00)) >= 0) {
            r = "67";
        } else if (g.compareTo(new BigFraction(24.00)) >= 0) {
            r = "66";
        } else if (g.compareTo(new BigFraction(20.00)) >= 0) {
            r = "65";
        } else if (g.compareTo(new BigFraction(16.00)) >= 0) {
            r = "64";
        } else if (g.compareTo(new BigFraction(12.00)) >= 0) {
            r = "63";
        } else if (g.compareTo(new BigFraction(8.00)) >= 0) {
            r = "62";
        } else if (g.compareTo(new BigFraction(4.00)) >= 0) {
            r = "61";
        } else {
            r = "60";
        }
        return r;
    }

    private static String finalSHSGrade(ArrayList<BigFraction> l) {
        BigFraction total = BigFraction.ZERO;
        for (BigFraction g : l) {
            total = total.add(g);
        }
        BigFraction average = total.divide(l.size());
        return Long.toString(Math.round(average.doubleValue()));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Reminders">
    public static ObservableList<Reminder> getReminders() throws SQLException {
        LOGGER.log(Level.INFO, "Getting Scores...");
        ResultSet r;

        PreparedStatement prep;
        prep = con.prepareStatement("SELECT ID, StartDate, StartTime, EndDate, EndTime, Title, Notes, Location, IsDone FROM Reminders ORDER BY StartDate ASC NULLS FIRST, StartTime ASC NULLS FIRST");
        r = prep.executeQuery();

        ObservableList<Reminder> reminders = FXCollections.observableArrayList();
        while (r.next()) {
            Reminder temp = new Reminder(r.getLong(1));
            temp.setStartDate(r.getDate(2).toLocalDate());
            temp.setStartTime(r.getTime(3));
            temp.setEndDate(r.getDate(4).toLocalDate());
            temp.setEndTime(r.getTime(5));
            temp.setTitle(r.getString(6));
            temp.setNotes(r.getString(7));
            temp.setLocation(r.getString(8));
            temp.setDone(r.getShort(9) > 0);
            reminders.add(temp);
        }
        return reminders;
    }

    public static boolean save(Reminder r) throws SQLException {
        if (r.getID() == -1) {
            LOGGER.log(Level.INFO, "Saving new Reminder...");
            PreparedStatement prep;
            prep = con.prepareStatement("INSERT INTO Reminders (StartDate, StartTime, EndDate, EndTime, Title, Notes, Location, IsDone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            prep.setDate(1, r.getStartDate());
            prep.setTime(2, r.getStartTime());
            prep.setDate(3, r.getEndDate());
            prep.setTime(4, r.getEndTime());
            prep.setString(5, r.getTitle());
            prep.setString(6, r.getNotes());
            prep.setString(7, r.getLocation());
            prep.setInt(8, r.isDone() ? 1 : 0);
            prep.executeUpdate();
            ResultSet rs = prep.getGeneratedKeys();
            rs.next();
            r.setID(rs.getLong(1));
            return true;
        } else {
            LOGGER.log(Level.INFO, "Saving Reminder...");
            PreparedStatement prep;
            prep = con.prepareStatement("UPDATE Reminders SET StartDate = ?, StartTime = ?, EndDate = ?, EndTime = ?, Title = ?, Notes = ?, Location = ?, IsDone = ? WHERE ID = ?");
            prep.setDate(1, r.getStartDate());
            prep.setTime(2, r.getStartTime());
            prep.setDate(3, r.getEndDate());
            prep.setTime(4, r.getEndTime());
            prep.setString(5, r.getTitle());
            prep.setString(6, r.getNotes());
            prep.setString(7, r.getLocation());
            prep.setInt(8, r.isDone() ? 1 : 0);
            prep.setLong(9, r.getID());
            prep.executeUpdate();
            return false;
        }
    }

    public static void delete(Reminder r) throws SQLException {
        LOGGER.log(Level.INFO, "Deleting Reminder...");
        PreparedStatement prep;
        prep = con.prepareStatement("DELETE FROM Reminders WHERE ID = ?");
        prep.setLong(1, r.getID());
        prep.executeUpdate();
    }
    // </editor-fold>
}
