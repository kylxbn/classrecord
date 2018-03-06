/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.grade;

import java.util.ArrayList;

public class Criterion {

    private final ArrayList<Task> tasks;
    private final double percent;

    public Criterion(int p) {
        tasks = new ArrayList<>();
        percent = ((double) p) / 100.0;
    }

    public void addTask(int s, int t) {
        tasks.add(new Task(s, t));
    }

    public double getGrade() {
        double total = 0;
        for (Task t : tasks) {
            total += t.getGrade() / tasks.size();
        }
        //JOptionPane.showMessageDialog(null, Double.toString(total*percent), "TEST", JOptionPane.WARNING_MESSAGE);
        return total * percent;
    }
}