/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.dashboard.provider;

import com.orthocube.classrecord.util.DB;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;

public class DefaultCredentials implements DashboardProvider {
    @Override
    public String getTitle() {
        return "You are using the default created login password!";
    }

    @Override
    public Node getContent() {
        try {
            if (DB.getUser("admin", "admin") != null) {
                return new Label("For security reasons, we recommend changing the default password.");
            } else {
                return null;
            }
        } catch (SQLException | IOException e) {
            return null;
        }
    }

    @Override
    public int getAlertLevel() {
        return 0;
    }
}
