/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.license;

import java.time.LocalDate;

public class Info {
    public static final LocalDate base = LocalDate.parse("2018-01-01");
    private final int user;
    private final int startMonth;
    private final int length;

    public Info(int user, int startMonth, int length) {
        this.user = user;
        this.startMonth = startMonth;
        this.length = length;
    }

    public Info(int id) {
        this.user = (id & 0x7F000000) >> 24;
        this.startMonth = (id & 0xFFF000) >> 12;
        this.length = (id & 0xFFF);
    }

    public int getID() {
        return (this.user << 24) | (this.startMonth << 12) | (this.length);
    }

    public int getUser() {
        return user;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getLength() {
        return length;
    }
}
