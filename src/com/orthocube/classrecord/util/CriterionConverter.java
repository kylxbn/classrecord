/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util;

import com.orthocube.classrecord.data.Criterion;
import javafx.util.StringConverter;

public class CriterionConverter extends StringConverter<Criterion> {
    @Override
    public String toString(Criterion c) {
        return c.getName();
    }

    @Override
    public Criterion fromString(String string) {
        return null;
    }
}
