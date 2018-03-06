/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.users;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.User;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    private Image showedImage = null;
    private Image noUser;
    private Image noPicture;

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private ImageView pboImage;

    @FXML
    private VBox vbxEdit;

    @FXML
    private Button cmdCancel;

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
        currentUser = new User();
        showUserInfo();
        cmdSave.setText("Save as new");
        editMode(true);
    }


    @FXML
    void mnuRemoveAction(ActionEvent event) {
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
    void cmdRemovePictureAction(ActionEvent event) {
        showedImage = null;
        showUserInfo();
        editMode(true);
    }

    @FXML
    void cmdReplaceAction(ActionEvent event) {
        BufferedImage bi = mainApp.chooseImage(125);
        if (bi != null) {
            showedImage = SwingFXUtils.toFXImage(bi, null);
        }
        showUserInfo();
        editMode(true);
    }

    @FXML
    void cmdCancelAction(ActionEvent event) {
        currentUser = lstUsers.getSelectionModel().getSelectedItem();
        showedImage = currentUser.getPicture();
        cmdSave.setText("Save");
        showUserInfo();
        editMode(false);
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
            currentUser.setPicture(showedImage);

            boolean newEntry;
            if (txtPassword.getText().isEmpty()) {
                newEntry = DB.save(currentUser);
            } else {
                newEntry = DB.save(currentUser, txtPassword.getText());
            }

            if (newEntry) {
                users.add(currentUser);
                lstUsers.getSelectionModel().select(currentUser);
                lstUsers.scrollTo(currentUser);
            }

            txtPassword.setText("");
            txtRepeat.setText("");
            mainApp.getRootNotification().show("User saved.");
            lstUsers.refresh();
            mainApp.refreshUser();

            cmdSave.setText("Save");
            editMode(false);
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
        // TODO: TL This
        return "Users"; //bundle.getString("users");
    }

    private void showUserInfo() {
        if (currentUser != null) {
            txtUsername.setText(currentUser.getUsername());
            txtNickname.setText(currentUser.getNickname());
            txtPassword.setText("");
            txtRepeat.setText("");
            cboAccessLevel.getSelectionModel().select(currentUser.getAccessLevel() - 1);
            cboAccessLevel.setDisable(currentUser.getID() == 1);
            vbxEdit.setDisable(false);
            if (showedImage == null) {
                pboImage.setImage(noPicture);
            } else {
                pboImage.setImage(showedImage);
            }
        } else {
            txtUsername.setText("");
            txtNickname.setText("");
            cboAccessLevel.getSelectionModel().select(-1);
            cboAccessLevel.setDisable(true);
            vbxEdit.setDisable(false);
            pboImage.setImage(noUser);
        }
    }

    private void editMode(boolean t) {
        if (t) {
            lstUsers.setDisable(true);
            cmdAdd.setDisable(true);
            cmdCancel.setVisible(true);
            cmdSave.setDisable(false);
        } else {
            lstUsers.setDisable(false);
            cmdAdd.setDisable(false);
            cmdCancel.setVisible(false);
            cmdSave.setDisable(true);
        }
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
            currentUser = newv;
            showedImage = currentUser.getPicture();
            showUserInfo();
            editMode(false);
        });

        txtUsername.textProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) editMode(true);
        });

        txtNickname.textProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) editMode(true);
        });
        txtPassword.textProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) editMode(true);
        });
        txtRepeat.textProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) editMode(true);
        });
        cboAccessLevel.valueProperty().addListener((ob, ov, nv) -> {
            if (currentUser != null) editMode(true);
        });

        noPicture = new Image(getClass().getResourceAsStream("../../res/Businessman_100px.png"));

        BufferedImage bi2 = new BufferedImage(125, 125, BufferedImage.TYPE_INT_RGB);
        Graphics g2 = bi2.createGraphics();
        g2.drawString("NO USER SELECTED", 5, 20);
        g2.dispose();
        noUser = SwingFXUtils.toFXImage(bi2, null);

        pboImage.setImage(noUser);

        editMode(false);
    }
}
