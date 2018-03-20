/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util.excel;

import com.orthocube.classrecord.data.Clazz;
import javafx.concurrent.Task;

public class SHSExporter extends Task<Void> {

    private Clazz currentClass;

    public SHSExporter(Clazz c) {
        currentClass = c;
    }

    @Override
    protected Void call() {

        return null;
    }
}
