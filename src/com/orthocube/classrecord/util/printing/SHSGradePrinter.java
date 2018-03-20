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
package com.orthocube.classrecord.util.printing;

import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Grade;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.printing.data.SHSGradeRow;
import com.orthocube.classrecord.util.printing.layouts.SHSGradeLayout;
import com.orthocube.classrecord.util.printing.util.GraphicPrinter;
import javafx.collections.ObservableList;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author OrthoCube
 */
public class SHSGradePrinter implements Printable {
    private int dpi;
    private ArrayList<SHSGradeRow> mt;

    private boolean drawInfo = true;
    private boolean drawName = true;
    private boolean drawFinal = false;

    private Clazz currentClass;

    public SHSGradePrinter(int d) {
        dpi = d;
    }

    public void setClass(Clazz c) {
        currentClass = c;
        try {
            mt = new ArrayList<>();
            ObservableList<Grade> grades = DB.getGrades(c);

            int rows = grades.size();

            String current, rem;
            int mid, fin;
            long ave;

            for (int i = 0; i < rows; i++) {
                current = grades.get(i).getLName() + ", " + grades.get(i).getFName();

                mid = Integer.parseInt(grades.get(i).getMidterms());
                fin = Integer.parseInt(grades.get(i).getFinals());
                ave = Integer.parseInt(grades.get(i).getFinal());

                rem = ave >= 75 ? "PASSED" : "FAILED";

                mt.add(new SHSGradeRow(current, Integer.toString(mid), Integer.toString(fin), Long.toString(ave), rem));

                if (i + 1 < rows) {
                    if (current.compareToIgnoreCase((grades.get(i + 1).getLName() + ", " + grades.get(i + 1).getFName())) > 0) {
                        mt.add(new SHSGradeRow("", "", "", "", ""));
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            Dialogs.exception(ex);
        }
    }

    public void setDrawInfo(boolean i) {
        drawInfo = i;
    }

    public void setDrawName(boolean i) {
        drawName = i;
    }

    public void setDrawFinal(boolean i) {
        drawFinal = i;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) {
        if ((page * 25) >= mt.size()) {
            return NO_SUCH_PAGE;
        }

        try {
            Image bg = ImageIO.read(new File("C:\\Users\\OrthoCube\\Pictures\\img001.bmp"));
            AffineTransform at = new AffineTransform();
            at.scale(0.24, 0.24);
            ((Graphics2D) g).drawImage(bg, at, null);
        } catch (IOException e) {
            Dialogs.exception(e);
        }

        Font norm = new Font("Arial", Font.PLAIN, 12);
        Font large = new Font("Arial", Font.PLAIN, 20);
        Font small = new Font("Arial", Font.PLAIN, 8);
        g.setFont(large);

        SHSGradeLayout l = new SHSGradeLayout();

        GraphicPrinter d = new GraphicPrinter(g, 72);

        if (drawInfo) {
            d.write("Baliuag", l.branch);

            d.setFont(norm);
            d.write("Academic", l.track);
            d.write("STEM", l.strand);
            d.write(currentClass.getCourse(), l.specialization);
            d.write(Integer.toString(currentClass.getYear()), l.grade);
            d.write(currentClass.getSem() == 1 ? "1st" : "2nd", l.semester);
            d.writeleft("17", l.sy1);
            d.writeleft("18", l.sy2);
            d.write(currentClass.getName(), l.subject);
            //d.write("Ewan", l.hours);
            //d.write("N/A", l.section);
            d.write("Kyle Alexander Buan", l.printedname);
        }

        d.setFont(large);

        for (int i = 25 * page; (i < (page + 1) * 25) && (i < mt.size()); i++) {
            if (drawName) {
                d.writeleftindexed(mt.get(i).getName(), l.student, l.offset, i % 25);
                d.writeindexed(mt.get(i).getMidterms(), l.midterm, l.offset, i % 25);
            }
            if (drawFinal) {
                d.writeindexed(mt.get(i).getFinals(), l.finals, l.offset, i % 25);
                d.writeindexed(mt.get(i).getAverage(), l.average, l.offset, i % 25);
                d.writeindexed(mt.get(i).getRemarks(), l.remarks, l.offset, i % 25);
            }
        }

        d.setFont(small);

        if (drawInfo) {
            d.write(Integer.toString(page + 1), l.page1);
            d.write(Integer.toString(mt.size() / 25 + 1), l.page2);
        }

        return PAGE_EXISTS;
    }
}
