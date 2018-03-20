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
package com.orthocube.classrecord.util.printing.util;

import com.orthocube.classrecord.util.printing.data.Field;

import java.awt.*;

/**
 * Enhances the {@code Graphics} class to easily draw printable pages.
 * This class wraps the {@code Graphics} class together with a DPI setting
 * to enable easy placing of text on pixel coordinates.
 * This class relies on the {@code Field} class to determine placement
 * of strings.
 *
 * @author OrthoCube
 */
public class GraphicPrinter {
    private Graphics g;
    private int dpi;
    private double xoffset, yoffset;

    /**
     * Creates a {@code GraphicPrinter} object.
     *
     * @param gr The Graphics to draw in
     * @param d  The DPI of the page
     * @param x  The X Offset
     * @param y  The Y Offset
     */
    public GraphicPrinter(Graphics gr, int d, double x, double y) {
        g = gr;
        dpi = d;
        xoffset = x;
        yoffset = y;
    }

    /**
     * Creates a {@code GraphicPrinter} object.
     *
     * @param gr THe Graphics to draw in
     * @param d  The DPI of the page
     */
    public GraphicPrinter(Graphics gr, int d) {
        g = gr;
        dpi = d;
        xoffset = 0;
        yoffset = 0;
    }

    /**
     * Sets the Offset of the {@code GraphicPrinter}
     *
     * @param x X Offset
     * @param y Y Offset
     */
    public void setOffset(double x, double y) {
        xoffset = x;
        yoffset = y;
    }

    /**
     * Draws a {@code String} centered in the specified {@code Field}.
     *
     * @param s The {@code String} to draw
     * @param f The {@code Field} to draw in
     */
    public void write(String s, Field f) {
        int width = g.getFontMetrics().stringWidth(s) / 2;
        g.drawString(s, (int) ((f.getX() + xoffset) * dpi + (f.getW() / 2.0) * dpi - width), (int) ((f.getY() + yoffset) * dpi));
    }

    /**
     * Draws a {@code String} left-aligned in the specified {@code Field}.
     *
     * @param s The {@code String} to draw
     * @param f The {@code Field} to draw in
     */
    public void writeleft(String s, Field f) {
        g.drawString(s, (int) ((f.getX() + xoffset) * dpi), (int) ((f.getY() + yoffset) * dpi));
    }

    /**
     * Draws a {@code String} centered in the specified {@code Field}
     * with a specified Index and Offset.
     * This draws the {@code String} in a {@code Field}, but the Y coordinate
     * is shifted certain units down.
     * This can be used to easily draw tables.
     *
     * @param s      The {@code String} to draw
     * @param f      The {@code Field} to draw in
     * @param offset How much inches to move down per unit
     * @param index  How many units to move down
     */
    public void writeindexed(String s, Field f, double offset, int index) {
        int width = g.getFontMetrics().stringWidth(s) / 2;
        g.drawString(s, (int) ((f.getX() + xoffset) * dpi + (f.getW() / 2.0) * dpi - width), (int) ((f.getY() + yoffset) * dpi + offset * index * dpi));
    }

    /**
     * Draws a {@code String} left-aligned in the specified {@code Field}
     * with a specified Index and Offset.
     * This draws the {@code String} in a {@code Field}, but the Y coordinate
     * is shifted certain units down.
     * This can be used to easily draw tables.
     *
     * @param s      The {@code String} to draw
     * @param f      The {@code Field} to draw in
     * @param offset How much inches to move down per unit
     * @param index  How many units to move down
     */
    public void writeleftindexed(String s, Field f, double offset, int index) {
        g.drawString(s, (int) ((f.getX() + xoffset) * dpi), (int) ((f.getY() + yoffset) * dpi + offset * index * dpi));
    }

    /**
     * Sets the font of the underlying {@code Graphics} object.
     *
     * @param f The {@code} Font object to use.
     */
    public void setFont(Font f) {
        g.setFont(f);
    }
}
