/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

/*
 * This program is (c) 2017 to present, Kyle Alexander Buan
 * This program is NOT open source, but proprietary.
 * That means, if you are in possession of this source code now,
 * you are mistreating the rights of the programmer.
 * Delete these all right now if you are.
 */
package com.orthocube.classrecord.util.printing.data;

/**
 * @author OrthoCube
 */
public class SHSGradeRow {
    private final String name;
    private final String midterms;
    private final String finals;
    private final String average;
    private final String remarks;

    public SHSGradeRow(String n, String m, String f, String a, String r) {
        name = n;
        midterms = m;
        finals = f;
        average = a;
        remarks = r;
    }

    public String getName() {
        return name;
    }

    public String getMidterms() {
        return midterms;
    }

    public String getFinals() {
        return finals;
    }

    public String getAverage() {
        return average;
    }

    public String getRemarks() {
        return remarks;
    }
}
