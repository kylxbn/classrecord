/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Criterion;
import com.orthocube.classrecord.data.Score;
import com.orthocube.classrecord.data.Task;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TasksController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(ClassesController.class.getName());
    private ResourceBundle bundle;

    private MainApp mainApp;
    private Clazz currentClass;
    private Task currentTask;
    private Score currentScore;

    private ObservableList<Task> tasks;
    private ObservableList<Score> scores;

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private TableView<Task> tblPrelims;

    @FXML
    private TableColumn<Task, String> colPName;

    @FXML
    private TableColumn<Task, String> colPCriterion;

    @FXML
    private TableColumn<Task, Number> colPItems;

    @FXML
    private TableView<Task> tblMidterms;

    @FXML
    private TableColumn<Task, String> colMName;

    @FXML
    private TableColumn<Task, String> colMCriterion;

    @FXML
    private TableColumn<Task, Number> colMItems;

    @FXML
    private TableView<Task> tblSemis;

    @FXML
    private TableColumn<Task, String> colSName;

    @FXML
    private TableColumn<Task, String> colSCriterion;

    @FXML
    private TableColumn<Task, Number> colSItems;

    @FXML
    private TableView<Task> tblFinals;

    @FXML
    private TableColumn<Task, String> colFName;

    @FXML
    private TableColumn<Task, String> colFCriterion;

    @FXML
    private TableColumn<Task, Number> colFItems;

    @FXML
    private TextField txtTName;

    @FXML
    private TextField txtTItems;

    @FXML
    private ChoiceBox<String> cboTTerm;

    @FXML
    private ChoiceBox<Criterion> cboTCriterion;

    @FXML
    private TableView<Score> tblScores;

    @FXML
    private TableColumn<Score, String> colScoreName;

    @FXML
    private TableColumn<Score, Number> colSScore;

    @FXML
    private TableColumn<Score, String> colSNotes;

    @FXML
    private Button cmdTAdd;

    @FXML
    private Button cmdTSave;

    @FXML
    private TextField txtSScore;

    @FXML
    private TextField txtSNotes;

    @FXML
    private Label lblScore;

    @FXML
    private Button cmdSAdd;

    @FXML
    private Button cmdSSave;
    // </editor-fold>

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return "Scores for " + currentClass.getName();
    }

    public void setClass(Clazz c) {
//        try {
//            currentClass = c;
//            tasks = DB.getTasks(c);
//
//            tblPrelims.setItems(tasks);
//            tblMidterms.setItems(tasks);
//            tblSemis.setItems(tasks);
//            tblFinals.setItems(tasks);
//        } catch (SQLException e) {
//            Dialogs.exception(e);
//        }
    }

    @FXML
    void cmdSAddAction(ActionEvent event) {

    }

    @FXML
    void cmdSSaveAction(ActionEvent event) {

    }

    @FXML
    void cmdTAddAction(ActionEvent event) {

    }

    @FXML
    void cmdTSaveAction(ActionEvent event) {

    }

    private void showTaskInfo() {
        if (currentTask != null) {
            txtTName.setText(currentTask.getName());
            txtTItems.setText(Integer.toString(currentTask.getItems()));
            // TODO: show task info
            cboTTerm.getSelectionModel().select(-1);
            cboTCriterion.getSelectionModel().select(-1);

            txtTName.setDisable(true);
            txtTItems.setDisable(true);
            cboTTerm.setDisable(true);
            cboTCriterion.setDisable(true);
        } else {
            txtTName.setText("");
            txtTItems.setText("");
            cboTTerm.getSelectionModel().select(-1);
            cboTCriterion.getSelectionModel().select(-1);

            txtTName.setDisable(true);
            txtTItems.setDisable(true);
            cboTTerm.setDisable(true);
            cboTCriterion.setDisable(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = resources;

        colPName.setCellValueFactory(cellValue -> cellValue.getValue().nameProperty());
        colPCriterion.setCellValueFactory(cellValue -> cellValue.getValue().getCriterion().nameProperty());
        colPItems.setCellValueFactory(cellValue -> cellValue.getValue().itemsProperty());

        colMName.setCellValueFactory(cellValue -> cellValue.getValue().nameProperty());
        colMCriterion.setCellValueFactory(cellValue -> cellValue.getValue().getCriterion().nameProperty());
        colMItems.setCellValueFactory(cellValue -> cellValue.getValue().itemsProperty());

        colSName.setCellValueFactory(cellValue -> cellValue.getValue().nameProperty());
        colSCriterion.setCellValueFactory(cellValue -> cellValue.getValue().getCriterion().nameProperty());
        colSItems.setCellValueFactory(cellValue -> cellValue.getValue().itemsProperty());

        colFName.setCellValueFactory(cellValue -> cellValue.getValue().nameProperty());
        colFCriterion.setCellValueFactory(cellValue -> cellValue.getValue().getCriterion().nameProperty());
        colFItems.setCellValueFactory(cellValue -> cellValue.getValue().itemsProperty());

        tblPrelims.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (cmdTSave.isDisable()) {
                currentTask = newv;
                showTaskInfo();
            } else {
                if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                    currentTask = newv;
                    showTaskInfo();
                    cmdTAdd.setDisable(false);
                    cmdTSave.setDisable(true);
                    cmdTSave.setText("Save");
                }
            }
        });

        tblMidterms.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (cmdTSave.isDisable()) {
                currentTask = newv;
                showTaskInfo();
            } else {
                if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                    currentTask = newv;
                    showTaskInfo();
                    cmdTAdd.setDisable(false);
                    cmdTSave.setDisable(true);
                    cmdTSave.setText("Save");
                }
            }
        });

        tblSemis.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (cmdTSave.isDisable()) {
                currentTask = newv;
                showTaskInfo();
            } else {
                if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                    currentTask = newv;
                    showTaskInfo();
                    cmdTAdd.setDisable(false);
                    cmdTSave.setDisable(true);
                    cmdTSave.setText("Save");
                }
            }
        });

        tblFinals.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (cmdTSave.isDisable()) {
                currentTask = newv;
                showTaskInfo();
            } else {
                if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                    currentTask = newv;
                    showTaskInfo();
                    cmdTAdd.setDisable(false);
                    cmdTSave.setDisable(true);
                    cmdTSave.setText("Save");
                }
            }
        });


        //---- SCORES -----------
        colScoreName.setCellValueFactory(cv -> Bindings.concat(cv.getValue().getEnrollee().getStudent().lnProperty(), ", ", cv.getValue().getEnrollee().getStudent().fnProperty()));
        colSScore.setCellValueFactory(cv -> cv.getValue().scoreProperty());
        colSNotes.setCellValueFactory(cv -> cv.getValue().notesProperty());

        tblScores.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (cmdSSave.isDisable()) {
                currentScore = newv;
                //showScoreInfo();
            } else {
                if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another score will discard your current unsaved changes.") == ButtonType.OK) {
                    currentScore = newv;
                    //showScoreInfo();
                    cmdSAdd.setDisable(false);
                    cmdSSave.setDisable(true);
                    cmdSSave.setText("Save");
                }
            }
        });

        cboTTerm.setItems(FXCollections.observableArrayList("Prelim", "Midterms", "Semis", "Finals"));

        // <editor-fold defaultstate="collapsed" desc="Listeners">
        txtTName.textProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) cmdTSave.setDisable(false);
        });
        txtTItems.textProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) cmdTSave.setDisable(false);
        });
        cboTTerm.valueProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) cmdTSave.setDisable(false);
        });
        cboTCriterion.valueProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) cmdTSave.setDisable(false);
        });
        txtSScore.textProperty().addListener((obs, ov, nv) -> {
            if (currentScore != null) cmdSSave.setDisable(false);
        });
        txtSNotes.textProperty().addListener((obs, ov, nv) -> {
            if (currentScore != null) cmdSSave.setDisable(false);
        });
        // </editor-fold>
    }
}
