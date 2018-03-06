/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.classes;

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
import javafx.scene.layout.VBox;
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
    int warningShowCount = 0;
    boolean nullifyPending = false;
    private ResourceBundle bundle;
    private MainApp mainApp;
    private Clazz currentClass;
    private Task currentTask;
    private Score currentScore;
    private ObservableList<Task> tasks;
    private ObservableList<Score> scores;
    private FilteredList<Criterion> filteredCriteria;
    private ValidationSupport taskValidationSupport;
    // TODO: Implement proper score validation
    private ValidationSupport scoreValidationSupport;

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private VBox vbxTEdit;
    @FXML
    private VBox vbxSEdit;

    @FXML
    private TitledPane tpnTInfo;
    @FXML
    private TitledPane tpnSInfo;


    @FXML
    private Button cmdTCancel;

    @FXML
    private Button cmdSCancel;

    @FXML
    private MenuItem cmdDeleteScore;

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
            ObservableList<Criterion> criteria = DB.getCriteria(c);

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
            tpnSInfo.setDisable(false);
        } else {
            txtSScore.setText("");
            txtSNotes.setText("");
            tpnSInfo.setDisable(true);
        }
    }

    @FXML
    void cmdSAddAction(ActionEvent event) {
        Enrollee chosenEnrollee = mainApp.showEnrolleeChooserDialog(currentClass, currentTask);
        if (chosenEnrollee != null) {
            currentScore = new Score();
            currentScore.setTask(currentTask);
            currentScore.setEnrollee(chosenEnrollee);
            showScoreInfo();
            cmdSSave.setText("Save as new");
            scoreEditMode(true);
            txtSScore.requestFocus();
        }
    }

    @FXML
    void cmdSSaveAction(ActionEvent event) {
        try {
            currentScore.setScore(Integer.parseInt(txtSScore.getText()));
            currentScore.setNotes(txtSNotes.getText());

            boolean newentry = DB.save(currentScore);

            if (newentry) {
                scores.add(currentScore);
                tblScores.getSelectionModel().select(currentScore);
                tblScores.scrollTo(currentScore);

                cmdSSave.setText("Save");
            }

            mainApp.getRootNotification().show("Score saved.");
            scoreEditMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdTAddAction(ActionEvent event) {
        currentTask = new Task();
        currentTask.setClass(currentClass);
        showTaskInfo();
        cmdTSave.setText("Save as new");
        taskEditMode(true);
    }

    @FXML
    void cmdTSaveAction(ActionEvent event) {
        if (taskValidationSupport.isInvalid()) {
            Dialogs.error("Invalid values", "Please fix the invalid input first.", "Choosing a term and a criterion is required.");
            return;
        }

        try {
            currentTask.setName(txtTName.getText());
            currentTask.setItems(Integer.parseInt(txtTItems.getText()));
            currentTask.setTerm(1 << cboTTerm.getSelectionModel().getSelectedIndex());

            Criterion selectedCriterion = cboTCriterion.getSelectionModel().getSelectedItem();
            currentTask.setCriterion(selectedCriterion);

            boolean newentry = DB.save(currentTask);

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

            taskEditMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
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

            scores = currentTask.getScores();
            tblScores.setItems(scores);
            lblScore.setText("/" + currentTask.getItems());
            tpnTInfo.setDisable(false);
        } else {
            txtTName.setText("");
            txtTItems.setText("");
            cboTTerm.getSelectionModel().select(-1);
            cboTCriterion.getSelectionModel().select(-1);

            scores = null;
            tblScores.setItems(null);
            lblScore.setText("/ ???");
            tpnTInfo.setDisable(true);
        }
    }

    @FXML
    void cmdDeleteScoreAction(ActionEvent event) {
        if (Dialogs.confirm("Remove Score", "Are you sure you want to delete this score?", currentScore.getEnrollee().getStudent().getLN() + ", " + currentScore.getEnrollee().getStudent().getFN()) == ButtonType.OK)
            try {
                DB.delete(currentScore);
                scores.remove(currentScore);
                mainApp.getRootNotification().show("Score removed from task.");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    @FXML
    private void cmdTCancelAction(ActionEvent event) {
        if (accTasks.getExpandedPane() != null)
            currentTask = ((TableView<Task>) accTasks.getExpandedPane().getContent()).getSelectionModel().getSelectedItem();
        else
            currentTask = null;
        cmdTSave.setText("Save");
        showTaskInfo();
        taskEditMode(false);
    }

    @FXML
    private void cmdSCancelAction(ActionEvent event) {
        currentScore = tblScores.getSelectionModel().getSelectedItem();
        cmdTSave.setText("Save");
        showScoreInfo();
        scoreEditMode(false);
    }

    private void taskEditMode(boolean t) {
        if (t) {
            accTasks.setDisable(true);
            cmdTAdd.setDisable(true);
            cmdTCancel.setVisible(true);
            cmdTSave.setDisable(false);
            vbxSEdit.setDisable(true);
        } else {
            accTasks.setDisable(false);
            cmdTAdd.setDisable(false);
            cmdTCancel.setVisible(false);
            cmdTSave.setDisable(true);
            vbxSEdit.setDisable(currentTask == null);
        }
    }

    private void scoreEditMode(boolean t) {
        if (t) {
            vbxTEdit.setDisable(true);
            tblScores.setDisable(true);
            cmdSAdd.setDisable(true);
            cmdSCancel.setVisible(true);
            cmdSSave.setDisable(false);
        } else {
            vbxTEdit.setDisable(false);
            tblScores.setDisable(false);
            cmdSAdd.setDisable(false);
            cmdSCancel.setVisible(false);
            cmdSSave.setDisable(true);
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
            currentTask = newv;
            showTaskInfo();
            taskEditMode(false);
        });

        tblMidterms.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            currentTask = newv;
            showTaskInfo();
            taskEditMode(false);
        });

        tblSemis.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            currentTask = newv;
            showTaskInfo();
            taskEditMode(false);
        });

        tblFinals.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            currentTask = newv;
            showTaskInfo();
            taskEditMode(false);
        });

        accTasks.expandedPaneProperty().addListener((obs, oldv, newv) -> {
            if (newv != null)
                currentTask = ((TableView<Task>) newv.getContent()).getSelectionModel().getSelectedItem();
            else
                currentTask = null;
            showTaskInfo();
            taskEditMode(false);
        });

        cboTTerm.getSelectionModel().selectedIndexProperty().addListener((obs, oldv, newv) ->
                filteredCriteria.setPredicate(p -> (p.getTerms() & (1 << newv.intValue())) > 0));

        cboTCriterion.setConverter(new CriterionConverter());

        colPName.prefWidthProperty().bind(tblPrelims.widthProperty().divide(2.0).subtract(30));
        colPCriterion.prefWidthProperty().bind(tblPrelims.widthProperty().divide(2.0).subtract(30));
        colPItems.setPrefWidth(50);

        colMName.prefWidthProperty().bind(tblMidterms.widthProperty().divide(2.0).subtract(30));
        colMCriterion.prefWidthProperty().bind(tblMidterms.widthProperty().divide(2.0).subtract(30));
        colMItems.setPrefWidth(50);

        colSName.prefWidthProperty().bind(tblSemis.widthProperty().divide(2.0).subtract(30));
        colSCriterion.prefWidthProperty().bind(tblSemis.widthProperty().divide(2.0).subtract(30));
        colSItems.setPrefWidth(50);

        colFName.prefWidthProperty().bind(tblFinals.widthProperty().divide(2.0).subtract(30));
        colFCriterion.prefWidthProperty().bind(tblFinals.widthProperty().divide(2.0).subtract(30));
        colFItems.setPrefWidth(50);

        //---- SCORES -----------
        colScoreName.setCellValueFactory(cv -> Bindings.concat(cv.getValue().getEnrollee().getStudent().lnProperty(), ", ", cv.getValue().getEnrollee().getStudent().fnProperty()));
        colSScore.setCellValueFactory(cv -> cv.getValue().scoreProperty());
        colSNotes.setCellValueFactory(cv -> cv.getValue().notesProperty());

        tblScores.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newv) -> {
            currentScore = newv;
            showScoreInfo();
            scoreEditMode(false);
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

        taskValidationSupport = new ValidationSupport();

        Validator<String> termValidator = (control, value) -> {
            boolean condition = value == null;

            return ValidationResult.fromMessageIf(control, "Please choose a term.", Severity.ERROR, condition);
        };
        Validator<Criterion> criterionValidator = (control, value) -> {
            boolean condition = value == null;
            return ValidationResult.fromMessageIf(control, "Please choose a criterion.", Severity.ERROR, condition);
        };

        taskValidationSupport.registerValidator(cboTTerm, true, termValidator);
        taskValidationSupport.registerValidator(cboTCriterion, true, criterionValidator);


        scoreValidationSupport = new ValidationSupport();
        Validator<String> scoreValidator = (control, value) -> {
            boolean condition = false;
            try {
                int v = Integer.parseInt(value);
                if ((v < 0) || (v > currentTask.getItems()))
                    condition = true;
            } catch (Exception e) {
                condition = true;
            }
            return ValidationResult.fromMessageIf(control, "Invalid score.", Severity.ERROR, condition);
        };
        scoreValidationSupport.registerValidator(txtSScore, true, scoreValidator);

        ContextMenu mnuTasks = new ContextMenu();
        MenuItem mnuDeleteTask = new MenuItem("Delete task");
        mnuDeleteTask.setOnAction(ev -> {
            if (Dialogs.confirm("Remove Task", "Are you sure you want to delete this task?", currentTask.getName()) == ButtonType.OK)
                try {
                    DB.delete(currentTask);
                    tasks.remove(currentTask);
                    mainApp.getRootNotification().show("Task removed from class.");
                } catch (SQLException e) {
                    Dialogs.exception(e);
                }
        });
        mnuTasks.getItems().add(mnuDeleteTask);
        tblPrelims.setContextMenu(mnuTasks);
        tblSemis.setContextMenu(mnuTasks);
        tblMidterms.setContextMenu(mnuTasks);
        tblFinals.setContextMenu(mnuTasks);

        colScoreName.prefWidthProperty().bind(tblScores.widthProperty().divide(2.0).subtract(30));
        colSScore.setPrefWidth(50);
        colSNotes.prefWidthProperty().bind(tblScores.widthProperty().divide(2.0).subtract(30));

        // <editor-fold defaultstate="collapsed" desc="Listeners">
        txtTName.textProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) taskEditMode(true);
        });
        txtTItems.textProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) taskEditMode(true);
        });
        cboTTerm.valueProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) taskEditMode(true);
        });
        cboTCriterion.valueProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) taskEditMode(true);
        });
        txtSScore.textProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) scoreEditMode(true);
        });
        txtSNotes.textProperty().addListener((obs, ov, nv) -> {
            if (currentTask != null) scoreEditMode(true);
        });
        // </editor-fold>

        taskEditMode(false);
        scoreEditMode(false);
    }
}
