/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.users;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.User;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
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

    private User currentUser;
    private ObservableList<User> users;

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private Button cmdAdd;

    @FXML
    private TitledPane ttlUsers;

    @FXML
    private ListView<User> lstUsers;

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
    private Button cmdReplace;

    @FXML
    private Button cmdRemove;

    @FXML
    private TextField txtNickname;

    @FXML
    private TitledPane ttlPermissions;

    @FXML
    private ChoiceBox<String> cboAccessLevel;

    @FXML
    private Label lblAccessExplanation;

    @FXML
    private Button cmdSave;
    // </editor-fold>

    @FXML
    void cmdAddAction(ActionEvent event) {
        if (!cmdSave.isDisable()) {
            if (Dialogs.confirm("New user", "You have unsaved changes. Discard changes?", "Creating another user will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }

        currentUser = new User();
        showUserInfo();
        cmdSave.setDisable(false);
        cmdSave.setText("Save as new");
        cmdAdd.setDisable(true);
    }


    @FXML
    void cmdRemoveAction(ActionEvent event) {
        if (currentUser.getID() == 1) {
            Dialogs.error("Cannot delete master user", "The master user cannot be deleted.", "This is the first ever created user\nand it can't be deleted.");
            return;
        }
        if (Dialogs.confirm("Delete User", "Are you sure you want to delete this user?", currentUser.getNickname().isEmpty() ? currentUser.getUsername() : currentUser.getNickname()) == ButtonType.OK)
            try {
                DB.delete(currentUser);
                users.remove(currentUser);
                mainApp.getRootNotification().show("User deleted.");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    @FXML
    void cmdReplaceAction(ActionEvent event) {

    }

    @FXML
    void cmdSaveAction(ActionEvent event) {
        try {
            if ((!txtUsername.getText().equals(currentUser.getUsername())) && (DB.userExists(txtUsername.getText()))) {
                Dialogs.error("Username already exists", "Username already taken.", "Please choose another username.");
                return;
            }

            if (!txtPassword.getText().equals(txtRepeat.getText())) {
                Dialogs.error("Password mismatch", "The confirmation password does not match.", "Please type the password again.");
                return;
            }

            currentUser.setUsername(txtUsername.getText());
            currentUser.setNickname(txtNickname.getText());
            currentUser.setAccessLevel(cboAccessLevel.getSelectionModel().getSelectedIndex() + 1);

            boolean newentry = false;
            if (txtPassword.getText().isEmpty()) {
                newentry = DB.save(currentUser);
            } else {
                newentry = DB.save(currentUser, txtPassword.getText());

            }

            //currentStudent.setPicture(ImageIO.read(r.getBinaryStream(10)));

            if (newentry) {
                users.add(currentUser);
                cmdSave.setDisable(true);
                lstUsers.getSelectionModel().select(currentUser);
                lstUsers.scrollTo(currentUser);
            }

            txtPassword.setText("");
            txtRepeat.setText("");
            mainApp.getRootNotification().show("User saved.");
            lstUsers.refresh();

            cmdSave.setDisable(true);
            cmdSave.setText("Save");
            cmdAdd.setDisable(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setModel(ObservableList<User> users) {
        this.users = users;
        lstUsers.setItems(users);
    }

    public String getTitle() {
        return "Users"; //bundle.getString("users");
    }

    private void showUserInfo() {
        if (currentUser != null) {
            txtUsername.setText(currentUser.getUsername());
            txtNickname.setText(currentUser.getNickname());
            txtPassword.setText("");
            txtRepeat.setText("");
            cboAccessLevel.getSelectionModel().select(currentUser.getAccessLevel() - 1);

            cmdReplace.setDisable(false);
            cmdRemove.setDisable(false);
            txtUsername.setDisable(false);
            txtNickname.setDisable(false);
            txtPassword.setDisable(false);
            txtRepeat.setDisable(false);
            cmdShow.setDisable(false);
            if (currentUser.getID() != 1)
                cboAccessLevel.setDisable(false);
            else
                cboAccessLevel.setDisable(true);
        } else {
            txtUsername.setText("");
            txtNickname.setText("");
            cboAccessLevel.getSelectionModel().select(-1);

            cmdReplace.setDisable(true);
            cmdRemove.setDisable(true);
            txtUsername.setDisable(true);
            txtNickname.setDisable(true);
            txtPassword.setDisable(true);
            txtRepeat.setDisable(true);
            cmdShow.setDisable(true);
            cboAccessLevel.setDisable(true);
        }

        cmdSave.setDisable(true);
        cmdAdd.setDisable(false);
        cmdSave.setText("Save");
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

        cboAccessLevel.setItems(FXCollections.observableArrayList("Superuser", "Administrator"));
        cboAccessLevel.valueProperty().addListener(e -> {
            if (cboAccessLevel.getSelectionModel().getSelectedIndex() == 0) {
                lblAccessExplanation.setText("Can modify records but\nunable to modify the credentials of other users");
            } else {
                lblAccessExplanation.setText("Can do anything. Literally.");
            }
        });

        lstUsers.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> p) {

                return new ListCell<>() {
                    @Override
                    protected void updateItem(User u, boolean bln) {
                        super.updateItem(u, bln);
                        if (u != null) {
                            if (u.getNickname().isEmpty()) {
                                setText(u.getUsername());
                            } else {
                                setText(u.getNickname() + " (" + u.getUsername() + ")");
                            }
                        }
                    }
                };
            }
        });

        lstUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
                    if (cmdSave.isDisable()) {
                        currentUser = newv;
                        showUserInfo();
                    } else {
                        if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another user will discard your current unsaved changes.") == ButtonType.OK) {
                            currentUser = newv;
                            showUserInfo();
                            cmdAdd.setDisable(false);
                            cmdSave.setDisable(true);
                            cmdSave.setText("Save");
                        }
                    }
                }
        );

        txtUsername.textProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) cmdSave.setDisable(false);
        });

        txtNickname.textProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) cmdSave.setDisable(false);
        });
        txtPassword.textProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) cmdSave.setDisable(false);
        });
        txtRepeat.textProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) cmdSave.setDisable(false);
        });
        cboAccessLevel.valueProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) cmdSave.setDisable(false);
        });
    }
}
