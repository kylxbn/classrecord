/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.dashboard.provider;

import javafx.scene.Node;

public interface DashboardProvider {

    String getTitle();

    Node getContent();

    int getAlertLevel();
}
