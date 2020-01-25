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
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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
    private MainApp mainApp;

    private User currentSelectedUser;
    private ObservableList<User> users;

    private Image showedImage = null;
    private Image noUser;
    private Image noPicture;

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private MenuItem mnuRemove;

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
    private ToggleButton cmdShow;

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

    private User currentUser = new User();

    public void setUser(User u) {
        currentUser = u;
        if (currentUser.getAccessLevel() < 3) {
            mnuRemove.setDisable(true);
            cmdAdd.setDisable(true);

            cmdRemove.setDisable(true);
            cmdReplace.setDisable(true);

            txtUsername.setEditable(false);
            txtNickname.setEditable(false);
            txtPassword.setEditable(false);
            txtRepeat.setEditable(false);
        }
    }

    @FXML
    void cmdAddAction(ActionEvent event) {
        currentSelectedUser = new User();
        showUserInfo();
        cmdSave.setText("Save as new");
        editMode(true);
    }


    @FXML
    void mnuRemoveAction(ActionEvent event) {
        if (currentSelectedUser == null) return;
        if (currentSelectedUser.getID() == 1) {
            Dialogs.error("Cannot delete master user", "The master user cannot be deleted.", "This is the first ever created user\nand it can't be deleted.");
            return;
        }
        if (Dialogs.confirm("Delete User", "Are you sure you want to delete this user?", currentSelectedUser.getNickname().isEmpty() ? currentSelectedUser.getUsername() : currentSelectedUser.getNickname()) == ButtonType.OK)
            try {
                DB.delete(currentSelectedUser);
                users.remove(currentSelectedUser);
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
        currentSelectedUser = lstUsers.getSelectionModel().getSelectedItem();
        showedImage = currentSelectedUser.getPicture();
        cmdSave.setText("Save");
        showUserInfo();
        editMode(false);
    }

    @FXML
    void cmdSaveAction(ActionEvent event) {
        try {
            if ((!txtUsername.getText().equals(currentSelectedUser.getUsername())) && (DB.userExists(txtUsername.getText()))) {
                Dialogs.error("Username already exists", "Username already taken.", "Please choose another username.");
                return;
            }

            if (!txtPassword.getText().equals(txtRepeat.getText())) {
                Dialogs.error("Password mismatch", "The confirmation password does not match.", "Please type the password again.");
                return;
            }

            currentSelectedUser.setUsername(txtUsername.getText());
            currentSelectedUser.setNickname(txtNickname.getText());
            currentSelectedUser.setAccessLevel(cboAccessLevel.getSelectionModel().getSelectedIndex() + 1);
            currentSelectedUser.setPicture(showedImage);

            boolean newEntry;
            if (txtPassword.getText().isEmpty()) {
                newEntry = DB.save(currentSelectedUser);
            } else {
                newEntry = DB.save(currentSelectedUser, txtPassword.getText());
            }

            if (newEntry) {
                users.add(currentSelectedUser);
                lstUsers.getSelectionModel().select(currentSelectedUser);
                lstUsers.scrollTo(currentSelectedUser);
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
        lblUsersTotal.textProperty().bind(Bindings.concat(Bindings.size(users), Bindings.when(Bindings.size(users).greaterThan(1)).then(" users").otherwise(" user")));

    }

    public String getTitle() {
        // TODO: TL This
        return "Users"; //bundle.getString("users");
    }

    private void showUserInfo() {
        if (currentSelectedUser != null) {
            txtUsername.setText(currentSelectedUser.getUsername());
            txtNickname.setText(currentSelectedUser.getNickname());
            txtPassword.setText("");
            txtRepeat.setText("");
            cboAccessLevel.getSelectionModel().select(currentSelectedUser.getAccessLevel() - 1);
            cboAccessLevel.setDisable(currentSelectedUser.getID() == 1);
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
        if (t && (currentUser.getAccessLevel() > 2)) {
            lstUsers.setDisable(true);
            cmdAdd.setDisable(true);
            cmdCancel.setVisible(true);
            cmdSave.setDisable(false);
        } else {
            lstUsers.setDisable(false);
            if (currentUser.getAccessLevel() > 2)
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

        cboAccessLevel.setItems(FXCollections.observableArrayList("Viewer", "Superuser", "Administrator"));
        cboAccessLevel.valueProperty().addListener(e -> {
            switch (cboAccessLevel.getSelectionModel().getSelectedIndex()) {
                case 0:
                    lblAccessExplanation.setText("Can only view records.");
                    break;
                case 1:
                    lblAccessExplanation.setText("Can modify records but\nunable to modify the credentials of other users");
                    break;
                case 2:
                    lblAccessExplanation.setText("Can do anything. Literally.");
                    break;
                default:
                    lblAccessExplanation.setText("Invalid access level.");
                    break;
            }
        });

        lstUsers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> p) {

                return new ListCell<User>() {
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
            currentSelectedUser = newv;
            showedImage = currentSelectedUser.getPicture();
            showUserInfo();
            editMode(false);
        });

        txtUsername.textProperty().addListener((ob, ov, nv) -> {
            if (currentSelectedUser != null) editMode(true);
        });

        txtNickname.textProperty().addListener((ob, ov, nv) -> {
            if (currentSelectedUser != null) editMode(true);
        });
        txtPassword.textProperty().addListener((ob, ov, nv) -> {
            if (currentSelectedUser != null) editMode(true);
        });
        txtRepeat.textProperty().addListener((ob, ov, nv) -> {
            if (currentSelectedUser != null) editMode(true);
        });
        cboAccessLevel.valueProperty().addListener((ob, ov, nv) -> {
            if (currentSelectedUser != null) editMode(true);
        });

        noPicture = new Image(getClass().getResourceAsStream("/com/orthocube/classrecord/res/Businessman_100px.png"));

        BufferedImage bi2 = new BufferedImage(125, 125, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi2.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NO USER SELECTED", 5, 20);
        g2.dispose();
        noUser = SwingFXUtils.toFXImage(bi2, null);

        pboImage.setImage(noUser);


        editMode(false);
    }
}
