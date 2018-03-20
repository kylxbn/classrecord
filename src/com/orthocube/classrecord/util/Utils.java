/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ResourceBundle;

import static java.time.temporal.ChronoUnit.DAYS;

public class Utils {
    private static final ResourceBundle bundle = Settings.bundle;

    public static void setupClearButtonField(CustomTextField customTextField) {
        try {
            Method m = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
            m.setAccessible(true);
            m.invoke(null, customTextField, customTextField.rightProperty());
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    // Used by Derby because it lacks proper bitwise math support. DO NOT REMOVE.
    public static short bitSet(short a, short b) {
        return (short) (a & (1 << b));
    }

    // Also used by Derby. Don't even dare.
    public static long daysBetween(Date a, Date b) {
        return DAYS.between(a.toLocalDate(), b.toLocalDate());
    }

    public static String sanitizeTime(String t) {
        String[] parts = t.split(":");
        int hour = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        return String.format("%02d:%02d", hour, min);
    }

    public static double getBrightness(Color c) {
        return (0.2126 * (c.getRed() / 255.0) + 0.7152 * (c.getGreen() / 255.0) + 0.0722 * (c.getBlue() / 255.0));
    }

    public static String toAMPM(String s) {
        String[] splitted = s.split(":");
        int hour = Integer.parseInt(splitted[0]), minute = Integer.parseInt(splitted[1]);
        String tod;
        if (hour == 0) {
            hour = 12;
            tod = bundle.getString("utils.am");
        } else if (hour < 12) {
            tod = bundle.getString("utils.am");
        } else if (hour == 12) {
            tod = bundle.getString("utils.pm");
        } else {
            hour -= 12;
            tod = bundle.getString("utils.pm");
        }
        return String.format(bundle.getString("utils.timeformat"), hour, minute, tod);
    }
}
