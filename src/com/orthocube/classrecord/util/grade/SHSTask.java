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
public class SHSTask {

    private int score, total;

    SHSTask(int s, int t) {
        score = s;
        total = t;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }
}
