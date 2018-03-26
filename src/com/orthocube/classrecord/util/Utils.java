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
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
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

    public static int[] getSYSemPeriod() {
        // get sy, sem, term;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        int sem, period;
        if ((month >= 5) && (month < 10)) {
            if (month == 5) {
                if (day < 20)
                    sem = 3;
                else
                    sem = 1;
            } else if (month == 9) {
                if (day < 20)
                    sem = 1;
                else
                    sem = 3;
            } else {
                sem = 1;
            }

            if (month == 5) {
                if (day < 20) {
                    period = 0;
                } else {
                    period = 1;
                }
            } else if (month == 6) {
                if (day < 20) {
                    period = 1;
                } else {
                    period = 2;
                }
            } else if (month == 7) {
                if (day < 20) {
                    period = 2;
                } else {
                    period = 3;
                }
            } else if (month == 8) {
                if (day < 20) {
                    period = 3;
                } else {
                    period = 4;
                }
            } else {
                if (day < 20) {
                    period = 4;
                } else {
                    period = 0;
                }
            }
        } else if (((month >= 10) && (month < 12)) || ((month >= 0) && (month < 3))) {
            if (month == 10) {
                if (day < 20)
                    sem = 1;
                else
                    sem = 2;
            } else if (month == 2) {
                if (day < 20)
                    sem = 2;
                else
                    sem = 3;
            } else {
                sem = 2;
            }

            year--;
            if (month == 10) {
                if (day < 20) {
                    period = 0;
                } else {
                    period = 1;
                }
            } else if (month == 11) {
                if (day < 20) {
                    period = 1;
                } else {
                    period = 2;
                }
            } else if (month == 0) {
                if (day < 20) {
                    period = 2;
                } else {
                    period = 3;
                }
            } else if (month == 1) {
                if (day < 20) {
                    period = 3;
                } else {
                    period = 4;
                }
            } else {
                if (day < 20) {
                    period = 4;
                } else {
                    period = 0;
                }
            }
        } else {
            sem = 3;
            year--;
            period = 0;
        }

        return new int[]{year, sem, period};
    }

    public static int getAge(LocalDate birthdate) {
        if (birthdate == null) return 0;
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}
