/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.users;

import com.orthocube.classrecord.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author OrthoCube
 */
public class UsersController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(UsersController.class.getName());
    private ResourceBundle bundle;
    private MainApp mainApp;

    @FXML
    private TitledPane ttlUsers;
    @FXML
    private ListView<String> lstUsers;
    @FXML
    private Label lblUsersTotal;
    @FXML
    private TitledPane ttlCredentials;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblPassword;
    @FXML
    private Label lblRepeat;
    @FXML
    private Button cmdShow;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtRepeat;
    @FXML
    private TitledPane ttlPermissions;
    @FXML
    private CheckBox chkEditUsers;
    @FXML
    private CheckBox chkEditData;
    @FXML
    private Button cmdSave;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return "Users"; //bundle.getString("users");
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

    }

}
