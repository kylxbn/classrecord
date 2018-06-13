/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.license;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Code below from here is duplicated across both
 * private and public projects / DLLs
 */
public class Utils {
    private static final char[] myAlphabet = new char[]{'3', '4', '7', '9',
            'A', 'C', 'D', 'E', 'F', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'T', 'U', 'V', 'W', 'X', 'Y',};
    private static final char[] hexAlphabet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Strip padding chars for comparison
     */
    public static String formatKeyForCompare(String key) {
        if (key == null) {
            key = "";
        }

        // Replace -, space etc, upper case

        return key.trim().toUpperCase().replace("-", "").replace(" ", "");
    }

    /**
     * Given a seed and some input bytes, generate a single byte to return.
     * This should be used with randomised data, that can be represented to retrieve the same key.
     */
    public static byte getKeyByte(long seed, byte a, byte b, byte c) {
        int aTemp = a % 25;
        int bTemp = b % 3;

        long result;

        if ((a % 2) == 0) {
            result = ((seed >> aTemp) & 0xFF) ^ ((seed >> bTemp) | c);
        } else {
            result = ((seed >> aTemp) & 0xFF) ^ ((seed >> bTemp) & c);
        }

        return (byte) result;
    }

    /**
     * Generate a new checksum for a key
     */
    public static String getChecksum(String str) {
        int left = 0x56;
        int right = 0xAF;

        if (str.length() > 0) {
            // 0xFF hex for 255
            for (int cnt = 0; cnt < str.length(); cnt++) {
                right = (right + Integer.parseInt(str.substring(cnt, cnt + 1), 16));

                if (right > 0xFF) {
                    right -= 0xFF;
                }

                left += right;

                if (left > 0xFF) {
                    left -= 0xFF;
                }
            }
        }

        int sum = ((left << 8) + right);

        return String.format("%04X", sum); // 4 char hex
    }

    private static List<Integer> convertBase(List<Integer> num, int src_base, int dst_base) {
        List<Integer> number = new ArrayList<>(num);
        List<Integer> res = new ArrayList<>();
        List<Integer> quotient = new ArrayList<>();
        int remainder;

        while (number.size() > 0) {
            // divide successive powers of dst_base
            quotient.clear();
            remainder = 0;
            for (int aNumber : number) {
                int accumulator = aNumber + remainder * src_base;
                int digit = accumulator / dst_base; // rounding faster than Math.floor
                remainder = accumulator % dst_base;
                if ((quotient.size() > 0) || (digit > 0)) quotient.add(digit);
            }

            // the remainder of current division is the next rightmost digit
            res.add(0, remainder);

            // rinse and repeat with next power of dst_base
            number.clear();
            number.addAll(quotient);
        }

        return res;
    }

    public static String convertFromHex(String number) throws IllegalStateException {
        List<Integer> n = new ArrayList<>();
        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            if ((c >= '0') && (c <= '9'))
                n.add(c - '0');
            else if ((c >= 'A') && (c <= 'F'))
                n.add(c - 'A' + 10);
            else if ((c >= 'a') && (c <= 'f'))
                n.add(c - 'a' + 10);
            else
                throw new IllegalStateException("Invalid number input: " + c);
        }

        List<Integer> res1 = convertBase(n, 16, myAlphabet.length);
        StringBuilder res = new StringBuilder();
        for (int num : res1) {
            res.append(myAlphabet[num]);
        }

        return res.toString();
    }

    public static String convertToHex(String number) throws IllegalStateException {
        List<Integer> n = new ArrayList<>();
        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            int index = Arrays.binarySearch(myAlphabet, c);
            if (index < 0)
                throw new IllegalStateException("Invalid number input: " + c);
            else
                n.add(Arrays.binarySearch(myAlphabet, c));
        }

        List<Integer> res1 = convertBase(n, myAlphabet.length, 16);
        StringBuilder res = new StringBuilder();
        for (int num : res1) {
            res.append(hexAlphabet[num]);
        }

        return res.toString();
    }

    public static String pad(String s, int len, String r) {
        return String.format("%" + len + "s", s).replace(" ", r);
    }
}
