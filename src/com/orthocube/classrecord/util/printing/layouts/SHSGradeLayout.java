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
package com.orthocube.classrecord.util.printing.layouts;

import com.orthocube.classrecord.util.printing.data.Field;

/**
 * @author OrthoCube
 */
public class SHSGradeLayout {
    public final Field branch = new Field(2.6875, 1.875, 2.75);

    public final Field track = new Field(1.1875, 3.0625, 1.125);
    public final Field strand = new Field(3.0, 3.0625, 0.9375);
    public final Field specialization = new Field(5.1875, 3.0625, 1.25);
    public final Field grade = new Field(7.0625, 3.0625, 0.5);

    public final Field semester = new Field(3.5625, 3.5, 1.3125);
    public final Field sy1 = new Field(6.3125, 3.5, 0.5);
    public final Field sy2 = new Field(7.03125, 3.5, 0.5);

    public final Field subject = new Field(5.6875, 3.6875, 1.875);

    public final Field hours = new Field(4.125, 3.875, 1.0);
    public final Field section = new Field(5.6875, 3.875, 1.875);

    public final Field printedname = new Field(5.0625, 4.65625, 2.5);

    public final Field student = new Field(0.5625, 6.0, 2.875);
    public final Field midterm = new Field(3.4375, 6.0, 1.0);
    public final Field finals = new Field(4.4375, 6.0, 1.0);
    public final Field average = new Field(5.4375, 6.0, 1.0625);
    public final Field remarks = new Field(6.5, 6.0, 1.15625);

    public final Field page1 = new Field(0.96875, 11.71875, 0.125);
    public final Field page2 = new Field(1.3125, 11.71875, 0.125);

    public final double offset = 0.2325;
}
