/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.license.verification;

import com.orthocube.classrecord.util.license.Info;
import com.orthocube.classrecord.util.license.KeyByteSet;
import com.orthocube.classrecord.util.license.Utils;

/**
 * Provides methods for verifying a licence key.
 */
public class LicenseKeyCheck {
    private Info info = null;

    public Info getDecodedInfo() {
        return info;
    }

    /**
     * Check a given key for validity
     *
     * @param key                The full key
     * @param keyByteSetsToCheck The KeyBytes that are to be tested in this check
     * @param totalKeyByteSets   The total number of KeyBytes used to make the key
     * @param blackListedSeeds   Any seed values (hex string representation) that should be banned
     */
    public LicenseKeyResult checkKey(String key, KeyByteSet[] keyByteSetsToCheck, int totalKeyByteSets, String[] blackListedSeeds) {
        try {
            key = Utils.pad(Utils.convertToHex(Utils.formatKeyForCompare(key)), 8 + 4 + (2 * totalKeyByteSets), "0");
        } catch (IllegalStateException e) {
            return LicenseKeyResult.INVALID;
        }

        LicenseKeyResult result = LicenseKeyResult.INVALID;

        boolean checksumPass = checkKeyChecksum(key, totalKeyByteSets);

        if (checksumPass) {
            if (blackListedSeeds != null && blackListedSeeds.length > 0) {
                // Test key against our black list

                // Example black listed seed: 111111 (Hex val). Producing keys with the same
                // seed and key bytes will produce the same key, so using a seed such as a user id
                // can provide a mechanism for tracking the source of any keys that are found to
                // be used out of licence terms.

                for (String blackListedSeed : blackListedSeeds) {
                    if (key.startsWith(blackListedSeed)) {
                        result = LicenseKeyResult.BLACKLISTED;
                    }
                }
            }

            if (result != LicenseKeyResult.BLACKLISTED) {
                // At this point, the key is either valid or forged,
                // because a forged key can have a valid checksum.
                // We now test the "bytes" of the key to determine if it is
                // actually valid.

                // When building your release application, use conditional defines
                // or comment out most of the byte checks!  This is the heart
                // of the partial key verification system. By not compiling in
                // each check, there is no way for someone to build a keygen that
                // will produce valid keys.  If an invalid keygen is released, you
                // simply change which byte checks are compiled in, and any serial
                // number built with the fake keygen no longer works.

                // Note that the parameters used for PKV_GetKeyByte calls MUST
                // MATCH the values that PKV_MakeKey uses to make the key in the
                // first place!

                result = LicenseKeyResult.FAKE;

                try {
                    long seed = Long.parseLong(key.substring(0, 8), 16);
                    this.info = new Info((int) seed);

                    String keyBytes;
                    byte b;
                    int keySubstringStart;

                    // The using of conditional compilation for the key byte checks
                    // means that we define the portions of the key that are checked
                    // at runtime. The advantage of this is that if someone creates
                    // a keygen using this source code, we can change the key bytes that
                    // are checked, so subsequent releases will not work with keys generated by
                    // the key generator.

                    for (KeyByteSet keyByteSet : keyByteSetsToCheck) {
                        keySubstringStart = getKeySubstringStart(keyByteSet.getKeyByteNo());

                        if (keySubstringStart - 1 > key.length()) {
                            throw new IllegalStateException("The KeyByte check position is out of range. You may have specified a check KeyByteNo that did not exist in the original key generation.");
                        }

                        keyBytes = key.substring(keySubstringStart, keySubstringStart + 2);
                        b = Utils.getKeyByte(seed, keyByteSet.getKeyByteA(), keyByteSet.getKeyByteB(), keyByteSet.getKeyByteC());

                        if (!keyBytes.equals(String.format("%02X", b))) {
                            // If true, then it means the key is either good, or was made
                            // with a keygen derived from "this" release.

                            return result; // Return result in failed state
                        }
                    }

                    result = LicenseKeyResult.GOOD;
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        }

        return result;
    }

    /**
     * Short hand way of creating pattern 8, 10, 12, 14
     */
    private int getKeySubstringStart(int keyByteNo) {
        return (keyByteNo * 2) + 6;
    }

    /**
     * Indicate if the check sum portion of the key is valid
     */
    private boolean checkKeyChecksum(String key, int totalKeyByteSets) {
        boolean result = false;

        key = Utils.pad(key, 8 + 4 + (2 * totalKeyByteSets), "0");

        if (key.length() == (8 + 4 + (2 * totalKeyByteSets))) // First 8 are seed, 4 for check sum, plus 2 for each KeyByte
        {
            int keyLessChecksumLength = key.length() - 4;

            String checkSum = key.substring(keyLessChecksumLength, keyLessChecksumLength + 4); // Last 4 chars are checksum

            String keyWithoutChecksum = key.substring(0, keyLessChecksumLength);

            result = Utils.getChecksum(keyWithoutChecksum).equals(checkSum);
        }

        return result;
    }
}
