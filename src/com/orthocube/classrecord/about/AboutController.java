/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.about;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author OrthoCube
 */
public class AboutController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(AboutController.class.getName());
    @FXML
    Label lblJavaVersion;
    @FXML
    Label lblVendor;
    @FXML
    Label lblOSName;
    @FXML
    Label lblOSVersion;
    @FXML
    Label lblCPUISA;
    @FXML
    TextArea txtChangelog;
    @FXML
    TextArea txtCredits;
    @FXML
    Label lblDatabaseVersion;
    private ResourceBundle bundle;
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return "About"; //bundle.getString("about");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

        lblJavaVersion.setText(System.getProperty("java.version"));
        lblVendor.setText(System.getProperty("java.vendor"));
        lblOSName.setText(System.getProperty("os.name"));
        lblOSVersion.setText(System.getProperty("os.version"));
        lblCPUISA.setText(System.getProperty("os.arch"));

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder("");
        BufferedReader reader2 = null;
        StringBuilder sb2 = new StringBuilder("");
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("com/orthocube/classrecord/resources/changelog.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            in = getClass().getClassLoader().getResourceAsStream("com/orthocube/classrecord/resources/credits.txt");
            reader2 = new BufferedReader(new InputStreamReader(in));
            while ((line = reader2.readLine()) != null) {
                sb2.append(line);
                sb2.append("\n");
            }

        } catch (IOException e) {
            Dialogs.exception(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Dialogs.exception(ex);
                }
            }
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException ex) {
                    Dialogs.exception(ex);
                }
            }
        }

        txtChangelog.setText(sb.toString());
        txtCredits.setText(sb2.toString());

        try {
            lblDatabaseVersion.setText("Database engine revision #" + DB.getDatabaseVersion());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
