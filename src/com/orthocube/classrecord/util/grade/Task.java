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

import org.apache.commons.math3.fraction.BigFraction;

/**
 * @author OrthoCube
 */
class Task {

    private final int score;
    private final int total;

    public Task(int s, int t) {
        score = s;
        total = t;
    }

    public BigFraction getGrade() {
        BigFraction p = new BigFraction(score, total).multiply(100);
        BigFraction r;
        if (p.compareTo(new BigFraction(50, 1)) < 0) {
            r = new BigFraction(5.00);
        } else if (p.compareTo(new BigFraction(60, 1)) < 0) {
            r = new BigFraction(3.00);
        } else if (p.compareTo(new BigFraction(66, 1)) < 0) {
            r = new BigFraction(2.75);
        } else if (p.compareTo(new BigFraction(70, 1)) < 0) {
            r = new BigFraction(2.50);
        } else if (p.compareTo(new BigFraction(76, 1)) < 0) {
            r = new BigFraction(2.25);
        } else if (p.compareTo(new BigFraction(80, 1)) < 0) {
            r = new BigFraction(2.00);
        } else if (p.compareTo(new BigFraction(86, 1)) < 0) {
            r = new BigFraction(1.75);
        } else if (p.compareTo(new BigFraction(90, 1)) < 0) {
            r = new BigFraction(1.50);
        } else if (p.compareTo(new BigFraction(96, 1)) < 0) {
            r = new BigFraction(1.25);
        } else {
            r = new BigFraction(1.00);
        }
        return r;
    }
}
