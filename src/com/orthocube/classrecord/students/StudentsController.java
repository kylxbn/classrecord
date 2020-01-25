/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.students;

/**
 * Controller for the Students display.
 * @author OrthoCube
 */

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Student;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.CustomTextField;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentsController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(StudentsController.class.getName());
    private Student currentStudent = null;
    private ObservableList<Student> students;
    private ResourceBundle bundle;

    private MainApp mainApp;

    private static Image noStudent;
    private static Image noPicture;

    private Image showedImage = null;

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private VBox vbxEdit;
    @FXML
    private Button cmdCancel;
    @FXML
    private Button cmdChangeImage;

    @FXML
    private Button cmdRemoveImage;

    @FXML
    private Button cmdAdd;

    @FXML
    private Label lblShowID;

    @FXML
    private Label lblShowName;

    @FXML
    private ImageView pboPicture;

    @FXML
    private CustomTextField txtSearch;

    @FXML
    private TableView<Student> tblStudents;

    @FXML
    private TableColumn<Student, String> colID;

    @FXML
    private TableColumn<Student, String> colName;

    @FXML
    private Label lblTotalStudents;

    @FXML
    private TitledPane ttlStudentInfo;

    @FXML
    private Label lblStudentID;

    @FXML
    private Label lblLastName;

    @FXML
    private Label lblFirstName;

    @FXML
    private Label lblMiddleName;

    @FXML
    private Label lblGender;

    @FXML
    private Label lblContactNumber;

    @FXML
    private Label lblAddress;

    @FXML
    private Label lblNotes;

    @FXML
    private TextField txtSID;

    @FXML
    private TextField txtLN;

    @FXML
    private TextField txtFN;

    @FXML
    private TextField txtMN;

    @FXML
    private ChoiceBox<String> cboGender;

    @FXML
    private TextField txtContact;

    @FXML
    private TextArea txtAddress;

    @FXML
    private TextArea txtNotes;

    @FXML
    private Button cmdSave;

    // </editor-fold>

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    void cmdCancelAction(ActionEvent event) {
        currentStudent = tblStudents.getSelectionModel().getSelectedItem();
        showedImage = currentStudent.getPicture();
        cmdSave.setText("Save");
        showStudentInfo();
        editMode(false);
    }

    @FXML
    void cmdSaveAction(ActionEvent event) {
        try {
            currentStudent.setSID(txtSID.getText());
            currentStudent.setFN(txtFN.getText());
            currentStudent.setMN(txtMN.getText());
            currentStudent.setLN(txtLN.getText());
            currentStudent.setFemale(cboGender.getSelectionModel().getSelectedIndex() > 0);
            currentStudent.setContact(txtContact.getText());
            currentStudent.setAddress(txtAddress.getText());
            currentStudent.setNotes(txtNotes.getText());
            currentStudent.setPicture(showedImage);
            boolean newentry = DB.save(currentStudent);

            if (newentry) {
                students.add(currentStudent);
                tblStudents.getSelectionModel().select(currentStudent);
                tblStudents.scrollTo(currentStudent);

                cmdSave.setText("Save");
            }

            mainApp.getRootNotification().show("Student saved.");

            editMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    private void mnuSearchOnFacebookAction(ActionEvent event) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("https://www.facebook.com/search/top/?q=" + (currentStudent.getFN() + " " + currentStudent.getLN()).replace(" ", "%20")));
            } else {
                Dialogs.error("Error", "Desktop Not Supported", "This computer is not able to open a web browser.");
            }
        } catch (IOException | URISyntaxException e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdNewStudentAction(ActionEvent event) {
        currentStudent = new Student();
        showedImage = null;
        showStudentInfo();
        cmdSave.setText("Save as new");
        editMode(true);
    }

    @FXML
    void cmdEnrolledClassesAction(ActionEvent event) {
        try {
            mainApp.showEnrolledClasses(currentStudent);
        } catch (IOException e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdChangeImageAction(ActionEvent event) {
        BufferedImage bi = mainApp.chooseImage(200);
        if (bi != null)
            showedImage = SwingFXUtils.toFXImage(bi, null);
        showStudentInfo();
        editMode(true);
    }

    @FXML
    void cmdRemoveImageAction(ActionEvent event) {
        showedImage = null;
        showStudentInfo();
        editMode(true);
    }

    @FXML
    void cmdDeleteAction(ActionEvent event) {
        if (Dialogs.confirm("Delete Student", "Are you sure you want to delete this student?", currentStudent.getLN() + ", " + currentStudent.getFN()) == ButtonType.OK)
            try {
                DB.delete(currentStudent);
                students.remove(currentStudent);
                mainApp.getRootNotification().show("Student deleted.");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    public void showStudent(Student s) {
        for (Student test : students) {
            if (test.getID() == s.getID()) {
                tblStudents.getSelectionModel().select(test);
                tblStudents.scrollTo(test);
                break;
            }
        }
        tblStudents.requestFocus();
    }

    private void showStudentInfo() {
        if (currentStudent != null) {
            txtSID.setText(currentStudent.getSID());
            txtFN.setText(currentStudent.getFN());
            txtMN.setText(currentStudent.getMN());
            txtLN.setText(currentStudent.getLN());
            cboGender.getSelectionModel().select(currentStudent.isFemale() ? 1 : 0);
            txtContact.setText(currentStudent.getContact());
            txtAddress.setText(currentStudent.getAddress());
            txtNotes.setText(currentStudent.getNotes());
            if (showedImage != null)
                pboPicture.setImage(showedImage);
            else
                pboPicture.setImage(noPicture);
            vbxEdit.setDisable(false);
        } else {
            txtSID.setText("");
            txtFN.setText("");
            txtMN.setText("");
            txtLN.setText("");
            cboGender.getSelectionModel().select(-1);
            txtContact.setText("");
            txtAddress.setText("");
            txtNotes.setText("");
            pboPicture.setImage(noStudent);
            vbxEdit.setDisable(true);
        }
    }

    public String getTitle() {
        return "Students";//bundle.getString("students");
    }

    public void setModel(ObservableList<Student> model) {

        students = model;

        FilteredList<Student> filteredStudents = new FilteredList<>(model, p -> true);
        txtSearch.textProperty().addListener((obs, oldv, newv) -> filteredStudents.setPredicate(student -> {
            if (newv == null || newv.isEmpty())
                return true;
            String lowercasefilter = newv.toLowerCase();
            return student.getFN().toLowerCase().contains(lowercasefilter) || student.getLN().toLowerCase().contains(lowercasefilter);
        }));

        SortedList<Student> sortedStudents = new SortedList<>(filteredStudents);
        sortedStudents.comparatorProperty().bind(tblStudents.comparatorProperty());
        tblStudents.setItems(sortedStudents);
    }

    private void editMode(boolean t) {
        if (t) {
            txtSearch.setDisable(true);
            tblStudents.setDisable(true);
            cmdAdd.setDisable(true);
            cmdSave.setDisable(false);
            cmdCancel.setVisible(true);
        } else {
            txtSearch.setDisable(false);
            tblStudents.setDisable(false);
            cmdAdd.setDisable(false);
            cmdSave.setDisable(true);
            cmdCancel.setVisible(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;


        colID.setCellValueFactory(
                cellData -> cellData.getValue().sidProperty()
        );
        colName.setCellValueFactory(
                cellData -> Bindings.concat(cellData.getValue().lnProperty(), ", "/*bundle.getString("nameseparator")*/, cellData.getValue().fnProperty())
        );

        tblStudents.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                        currentStudent = newV;
                        showedImage = currentStudent.getPicture();
                        showStudentInfo();
                        editMode(false);
                }
        );

        cboGender.setItems(FXCollections.observableArrayList("Male", "Female"));//bundle.getString("male"), bundle.getString("female")));

        colID.setPrefWidth(135);
        colName.prefWidthProperty().bind(tblStudents.widthProperty().subtract(155));

        lblShowID.textProperty().bind(txtSID.textProperty());
        lblShowName.textProperty().bind(Bindings.concat(txtFN.textProperty(), " ", txtLN.textProperty()));

        Utils.setupClearButtonField(txtSearch);

        BufferedImage bi1 = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics g1 = bi1.createGraphics();
        g1.drawString("NO PICTURE SET", 40, 40);
        g1.dispose();
        noPicture = SwingFXUtils.toFXImage(bi1, null);

        BufferedImage bi2 = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics g2 = bi2.createGraphics();
        g2.drawString("NO STUDENT SELECTED", 40, 40);
        g2.dispose();
        noStudent = SwingFXUtils.toFXImage(bi2, null);

        pboPicture.setImage(noStudent);

        // <editor-fold defaultstate="collapsed" desc="change listeners">
        txtSID.textProperty().addListener((ob, ov, nv) -> {
            if (currentStudent != null) editMode(true);
        });
        txtLN.textProperty().addListener((ob, ov, nv) -> {
            if (currentStudent != null) editMode(true);
        });
        txtFN.textProperty().addListener((ob, ov, nv) -> {
            if (currentStudent != null) editMode(true);
        });
        txtMN.textProperty().addListener((ob, ov, nv) -> {
            if (currentStudent != null) editMode(true);
        });
        cboGender.valueProperty().addListener((ob, ov, nv) -> {
            if (currentStudent != null) editMode(true);
        });
        txtContact.textProperty().addListener((ob, ov, nv) -> {
            if (currentStudent != null) editMode(true);
        });
        txtAddress.textProperty().addListener((ob, ov, nv) -> {
            if (currentStudent != null) editMode(true);
        });
        txtNotes.textProperty().addListener((ob, ov, nv) -> {
            if (currentStudent != null) editMode(true);
        });
        // </editor-fold>

        editMode(false);
    }

}

