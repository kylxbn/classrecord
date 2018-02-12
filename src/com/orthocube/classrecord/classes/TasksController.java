/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.*;
import com.orthocube.classrecord.util.CriterionConverter;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
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
    FilteredList<Criterion> filteredCriteria;
    int warningShowCount = 0;
    boolean saveInProgress = false;
    boolean nullifyPending = false;
    private ObservableList<Criterion> criteria;

    private ValidationSupport validationSupport;
    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private Accordion accTasks;

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

    @FXML
    private TitledPane tpnPrelims;
    @FXML
    private TitledPane tpnMidterms;
    @FXML
    private TitledPane tpnSemis;
    @FXML
    private TitledPane tpnFinals;
    // </editor-fold>

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return "Scores for " + currentClass.getName();
    }

    public void setClass(Clazz c) {
        try {
            currentClass = c;
            tasks = DB.getTasks(c);
            criteria = DB.getCriteria(c);

            FilteredList<Task> prelimTasks = new FilteredList<>(tasks, p -> (p.getTerm() & 1) > 0);
            FilteredList<Task> midtermTasks = new FilteredList<>(tasks, p -> (p.getTerm() & 2) > 0);
            FilteredList<Task> semisTasks = new FilteredList<>(tasks, p -> (p.getTerm() & 4) > 0);
            FilteredList<Task> finalsTasks = new FilteredList<>(tasks, p -> (p.getTerm() & 8) > 0);

            tblPrelims.setItems(prelimTasks);
            tblMidterms.setItems(midtermTasks);
            tblSemis.setItems(semisTasks);
            tblFinals.setItems(finalsTasks);

            filteredCriteria = new FilteredList<>(criteria, p -> false);
            cboTCriterion.setItems(filteredCriteria);

            cboTTerm.getSelectionModel().select(0);
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    private void showScoreInfo() {
        if (currentScore != null) {
            txtSScore.setText(Integer.toString(currentScore.getScore()));
            txtSNotes.setText(currentScore.getNotes());

            txtSScore.setDisable(false);
            txtSNotes.setDisable(false);
        } else {
            txtSScore.setText("");
            txtSNotes.setText("");

            txtSScore.setDisable(true);
            txtSNotes.setDisable(true);
        }

        cmdSSave.setDisable(true);
        cmdSAdd.setDisable(false);
        cmdSSave.setText("Save");
    }

    @FXML
    void cmdSAddAction(ActionEvent event) {
        if (!cmdSSave.isDisable()) {
            if (Dialogs.confirm("New score", "You have unsaved changes. Discard changes?", "Creating another score will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }

        Enrollee chosenEnrollee = mainApp.showEnrolleeChooserDialog(currentClass, currentTask);
        if (chosenEnrollee != null) {
            currentScore = new Score();
            currentScore.setTask(currentTask);
            currentScore.setEnrollee(chosenEnrollee);
            showScoreInfo();
            cmdSSave.setDisable(false);
            cmdSSave.setText("Save as new");
            cmdSAdd.setDisable(true);
        }
    }

    @FXML
    void cmdSSaveAction(ActionEvent event) {
        try {
            currentScore.setScore(Integer.parseInt(txtSScore.getText()));
            currentScore.setNotes(txtSNotes.getText());

            boolean newentry = DB.save(currentScore);

            cmdSSave.setDisable(true);
            if (newentry) {
                scores.add(currentScore);
                tblScores.getSelectionModel().select(currentScore);
                tblScores.scrollTo(currentScore);

                cmdSSave.setText("Save");
                cmdSAdd.setDisable(false);
            }

            mainApp.getRootNotification().show("Score saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdTAddAction(ActionEvent event) {
        if (!cmdTSave.isDisable()) {
            if (Dialogs.confirm("New task", "You have unsaved changes. Discard changes?", "Creating another task will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }

        currentTask = new Task();
        currentTask.setClass(currentClass);
        showTaskInfo();
        cmdTSave.setDisable(false);
        cmdTSave.setText("Save as new");
        cmdTAdd.setDisable(true);
    }

    @FXML
    void cmdTSaveAction(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            Dialogs.error("Invalid values", "Please fix the invalid input first.", "Choosing a term and a criterion is required.");
            return;
        }

        saveInProgress = true;
        try {
            currentTask.setName(txtTName.getText());
            currentTask.setItems(Integer.parseInt(txtTItems.getText()));
            currentTask.setTerm(1 << cboTTerm.getSelectionModel().getSelectedIndex());
            currentTask.setCriterion(cboTCriterion.getSelectionModel().getSelectedItem());

            boolean newentry = DB.save(currentTask);

            cmdTSave.setDisable(true);
            if (newentry) {
                tasks.add(currentTask);
                if ((currentTask.getTerm() & 1) > 0) {
                    accTasks.setExpandedPane(tpnPrelims);
                    tblPrelims.getSelectionModel().select(currentTask);
                    tblPrelims.scrollTo(currentTask);
                } else if ((currentTask.getTerm() & 2) > 0) {
                    accTasks.setExpandedPane(tpnMidterms);
                    tblMidterms.getSelectionModel().select(currentTask);
                    tblMidterms.scrollTo(currentTask);
                } else if ((currentTask.getTerm() & 4) > 0) {
                    accTasks.setExpandedPane(tpnSemis);
                    tblSemis.getSelectionModel().select(currentTask);
                    tblSemis.scrollTo(currentTask);
                } else if ((currentTask.getTerm() & 8) > 0) {
                    accTasks.setExpandedPane(tpnFinals);
                    tblFinals.getSelectionModel().select(currentTask);
                    tblFinals.scrollTo(currentTask);
                }

                cmdTSave.setText("Save");
                cmdTAdd.setDisable(false);
            } else {
                if ((currentTask.getTerm() & 1) > 0) {
                    accTasks.setExpandedPane(tpnPrelims);
                    tblPrelims.getSelectionModel().select(currentTask);
                    tblPrelims.scrollTo(currentTask);
                } else if ((currentTask.getTerm() & 2) > 0) {
                    accTasks.setExpandedPane(tpnMidterms);
                    tblMidterms.getSelectionModel().select(currentTask);
                    tblMidterms.scrollTo(currentTask);
                } else if ((currentTask.getTerm() & 4) > 0) {
                    accTasks.setExpandedPane(tpnSemis);
                    tblSemis.getSelectionModel().select(currentTask);
                    tblSemis.scrollTo(currentTask);
                } else if ((currentTask.getTerm() & 8) > 0) {
                    accTasks.setExpandedPane(tpnFinals);
                    tblFinals.getSelectionModel().select(currentTask);
                    tblFinals.scrollTo(currentTask);
                }
            }

            mainApp.getRootNotification().show("Task saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }

        saveInProgress = false;
    }

    private void showTaskInfo() {
        if (currentTask != null) {
            txtTName.setText(currentTask.getName());
            txtTItems.setText(Integer.toString(currentTask.getItems()));
            if ((currentTask.getTerm() & 1) > 0)
                cboTTerm.getSelectionModel().select(0);
            else if ((currentTask.getTerm() & 2) > 0)
                cboTTerm.getSelectionModel().select(1);
            else if ((currentTask.getTerm() & 4) > 0)
                cboTTerm.getSelectionModel().select(2);
            else if ((currentTask.getTerm() & 8) > 0)
                cboTTerm.getSelectionModel().select(3);
            else
                cboTTerm.getSelectionModel().select(-1);

            for (Criterion c : filteredCriteria) {
                if (c.getID() == currentTask.getCriterion().getID()) {
                    cboTCriterion.getSelectionModel().select(c);
                    break;
                }
            }

            txtTName.setDisable(false);
            txtTItems.setDisable(false);
            cboTTerm.setDisable(false);
            cboTCriterion.setDisable(false);
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

        cmdTSave.setDisable(true);
        cmdTAdd.setDisable(false);
        cmdTSave.setText("Save");
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
            if (!saveInProgress) {
                if (cmdTSave.isDisable()) {
                    currentTask = newv;
                    showTaskInfo();
                    scores = currentTask.getScores();
                    tblScores.setItems(scores);
                } else {
                    if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                        currentTask = newv;
                        showTaskInfo();
                        scores = currentTask.getScores();
                        tblScores.setItems(scores);
                        cmdTAdd.setDisable(false);
                        cmdTSave.setDisable(true);
                        cmdTSave.setText("Save");
                    }
                }
            }
        });

        tblMidterms.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (!saveInProgress) {
                if (cmdTSave.isDisable()) {
                    currentTask = newv;
                    showTaskInfo();
                    scores = currentTask.getScores();
                    tblScores.setItems(scores);

                } else {
                    if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                        currentTask = newv;
                        showTaskInfo();
                        scores = currentTask.getScores();
                        tblScores.setItems(scores);

                        cmdTAdd.setDisable(false);
                        cmdTSave.setDisable(true);
                        cmdTSave.setText("Save");
                    }
                }
            }
        });

        tblSemis.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (!saveInProgress) {
                if (cmdTSave.isDisable()) {
                    currentTask = newv;
                    showTaskInfo();
                    scores = currentTask.getScores();
                    tblScores.setItems(scores);

                } else {
                    if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                        currentTask = newv;
                        showTaskInfo();
                        scores = currentTask.getScores();
                        tblScores.setItems(scores);

                        cmdTAdd.setDisable(false);
                        cmdTSave.setDisable(true);
                        cmdTSave.setText("Save");
                    }
                }
            }
        });

        tblFinals.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (!saveInProgress) {
                if (cmdTSave.isDisable()) {
                    currentTask = newv;
                    showTaskInfo();
                    scores = currentTask.getScores();
                    tblScores.setItems(scores);

                } else {
                    if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                        currentTask = newv;
                        showTaskInfo();
                        scores = currentTask.getScores();
                        tblScores.setItems(scores);

                        cmdTAdd.setDisable(false);
                        cmdTSave.setDisable(true);
                        cmdTSave.setText("Save");
                    }
                }
            }
        });

        accTasks.expandedPaneProperty().addListener((obs, oldv, newv) -> {
            // TODO: fix double warning
            if (!saveInProgress) {
                if (cmdTSave.isDisable()) {
                    if (newv != null)
                        currentTask = ((TableView<Task>) newv.getContent()).getSelectionModel().getSelectedItem();
                    else
                        currentTask = null;
                    showTaskInfo();
                    if (currentTask != null)
                        scores = currentTask.getScores();
                    else
                        scores = null;
                    tblScores.setItems(scores);

                } else {
                    if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another task will discard your current unsaved changes.") == ButtonType.OK) {
                        if (newv != null)
                            currentTask = ((TableView<Task>) newv.getContent()).getSelectionModel().getSelectedItem();
                        else
                            currentTask = null;
                        showTaskInfo();
                        if (currentTask != null)
                            scores = currentTask.getScores();
                        else
                            scores = null;
                        tblScores.setItems(scores);

                        cmdTAdd.setDisable(false);
                        cmdTSave.setDisable(true);
                        cmdTSave.setText("Save");
                    }
                }
            }
        });

        cboTTerm.getSelectionModel().selectedIndexProperty().addListener((obs, oldv, newv) -> {
            filteredCriteria.setPredicate(p -> (p.getTerms() & (1 << newv.intValue())) > 0);
        });

        cboTCriterion.setConverter(new CriterionConverter());

        //---- SCORES -----------
        colScoreName.setCellValueFactory(cv -> Bindings.concat(cv.getValue().getEnrollee().getStudent().lnProperty(), ", ", cv.getValue().getEnrollee().getStudent().fnProperty()));
        colSScore.setCellValueFactory(cv -> cv.getValue().scoreProperty());
        colSNotes.setCellValueFactory(cv -> cv.getValue().notesProperty());

        tblScores.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            if (cmdSSave.isDisable()) {
                currentScore = newv;
                showScoreInfo();
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

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        switch (month) {
            case Calendar.JULY:
            case Calendar.DECEMBER:
                if (day < 20)
                    accTasks.setExpandedPane(tpnPrelims);
                else
                    accTasks.setExpandedPane(tpnMidterms);
                break;
            case Calendar.AUGUST:
            case Calendar.JANUARY:
                if (day < 20)
                    accTasks.setExpandedPane(tpnMidterms);
                else
                    accTasks.setExpandedPane(tpnSemis);
                break;
            case Calendar.SEPTEMBER:
            case Calendar.FEBRUARY:
                if (day < 20)
                    accTasks.setExpandedPane(tpnSemis);
                else
                    accTasks.setExpandedPane(tpnFinals);
                break;
            default:
                if (day < 20)
                    accTasks.setExpandedPane(tpnFinals);
                else
                    accTasks.setExpandedPane(tpnPrelims);
                break;
        }

        validationSupport = new ValidationSupport();

        Validator<String> termValidator = (control, value) -> {
            boolean condition = value == null;

            return ValidationResult.fromMessageIf(control, "Please choose a term.", Severity.ERROR, condition);
        };
        Validator<Criterion> criterionValidator = (control, value) -> {
            boolean condition = value == null;
            return ValidationResult.fromMessageIf(control, "Please choose a criterion.", Severity.ERROR, condition);
        };

        validationSupport.registerValidator(cboTTerm, true, termValidator);
        validationSupport.registerValidator(cboTCriterion, true, criterionValidator);

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
