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
package com.orthocube.classrecord.util.grade;

/**
 * @author OrthoCube
 */
public class Task {

    private int score, total;

    public Task(int s, int t) {
        score = s;
        total = t;
    }

    public double getGrade() {
        double p = ((double) score) / ((double) total) * 100.0;
        double r;
        if (p < 50) {
            r = 5.00;
        } else if (p < 60) {
            r = 3.00;
        } else if (p < 66) {
            r = 2.75;
        } else if (p < 70) {
            r = 2.50;
        } else if (p < 76) {
            r = 2.25;
        } else if (p < 80) {
            r = 2.00;
        } else if (p < 86) {
            r = 1.75;
        } else if (p < 90) {
            r = 1.50;
        } else if (p < 96) {
            r = 1.25;
        } else {
            r = 1.00;
        }
        return r;
    }
}
