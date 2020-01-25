/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.license;

public class KeyByteSet {
    private final int keyByteNo;
    private final byte keyByteA;
    private final byte keyByteB;
    private final byte keyByteC;

    public KeyByteSet(int keyByteNo, int keyByteA, int keyByteB, int keyByteC) {
        this.keyByteNo = keyByteNo;
        this.keyByteA = (byte) keyByteA;
        this.keyByteB = (byte) keyByteB;
        this.keyByteC = (byte) keyByteC;
    }

    public int getKeyByteNo() {
        return keyByteNo;
    }

    public byte getKeyByteA() {
        return keyByteA;
    }


    public byte getKeyByteB() {
        return keyByteB;
    }

    public byte getKeyByteC() {
        return keyByteC;
    }
}
