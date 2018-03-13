/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.grade;

import org.apache.commons.math3.fraction.BigFraction;

import java.util.ArrayList;

public class Criterion {

    private final ArrayList<Task> tasks;
    private final BigFraction percent;

    public Criterion(int p) {
        tasks = new ArrayList<>();
        percent = new BigFraction(p, 100);
    }

    public void addTask(int s, int t) {
        tasks.add(new Task(s, t));
    }

    public BigFraction getGrade() {
        BigFraction total = BigFraction.ZERO;
        for (Task t : tasks) {
            total = total.add(t.getGrade()).divide(tasks.size());
        }
        //JOptionPane.showMessageDialog(null, Double.toString(total*percent), "TEST", JOptionPane.WARNING_MESSAGE);
        return total.multiply(percent);
    }
}