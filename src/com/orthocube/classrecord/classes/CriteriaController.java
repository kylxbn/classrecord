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
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CriteriaController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(EnrolleesController.class.getName());

    private ResourceBundle bundle;

    private MainApp mainApp;
    private ObservableList<Criterion> criteria;
    private Clazz currentClass;
    private Criterion currentCriterion;

    ValidationSupport validationSupport;

    // <editor-fold defaultstate="collapsed" desc="controls">
    @FXML
    private TableView<Criterion> tblCriteria;

    @FXML
    private TableColumn<Criterion, String> colName;

    @FXML
    private TableColumn<Criterion, String> colPercent;

    @FXML
    private MenuItem mnuDelete;

    @FXML
    private TitledPane tpnPercentages;

    @FXML
    private Label lblTPrelims;

    @FXML
    private Label lblTMiderms;

    @FXML
    private Label lblTSemis;

    @FXML
    private Label lblTFinals;

    @FXML
    private Label lblRPrelims;

    @FXML
    private Label lblRMidterms;

    @FXML
    private Label lblRSemis;

    @FXML
    private Label lblRFinals;

    @FXML
    private TitledPane tpnPresets;

    @FXML
    private Button cmdAppend;

    @FXML
    private Button cmdReplace;

    @FXML
    private Button cmdSaveAs;

    @FXML
    private Button cmdDelete;

    @FXML
    private Label lblCount;

    @FXML
    private Button cmdAdd;

    @FXML
    private TitledPane tpnInfo;

    @FXML
    private Label lblName;

    @FXML
    private Label lblPercent;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPercent;

    @FXML
    private TitledPane tpnTerms;

    @FXML
    private CheckBox chkPrelims;

    @FXML
    private CheckBox chkMidterms;

    @FXML
    private CheckBox chkSemis;

    @FXML
    private CheckBox chkFinals;

    @FXML
    private Button cmdSave;

    @FXML
    private Label lblPrelims;
    @FXML
    private Label lblMidterms;
    @FXML
    private Label lblSemis;
    @FXML
    private Label lblFinals;

    @FXML
    private ChoiceBox<String> cboTerms;
    @FXML
    private PieChart chtPie;


    // </editor-fold>

    @FXML
    void cmdAddAction(ActionEvent event) {
        if (!cmdSave.isDisable()) {
            if (Dialogs.confirm("New criterion", "You have unsaved changes. Discard changes?", "Creating another criterion will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }

        currentCriterion = new Criterion();
        currentCriterion.setClass(currentClass);
        showCriterionInfo();
        cmdSave.setDisable(false);
        cmdSave.setText("Save as new");
        cmdAdd.setDisable(true);
    }

    @FXML
    void cmdAppendAction(ActionEvent event) {

    }

    @FXML
    void cmdDeleteAction(ActionEvent event) {

    }

    @FXML
    void cmdReplaceAction(ActionEvent event) {

    }

    @FXML
    void cmdSaveAction(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            mainApp.getRootNotification().show("Please fix invalid values first.");
            return;
        }

        try {
            currentCriterion.setName(txtName.getText());
            currentCriterion.setPercentage(Integer.parseInt(txtPercent.getText()));

            int terms = 0;
            terms |= chkPrelims.isSelected() ? 1 : 0;
            terms |= chkMidterms.isSelected() ? 2 : 0;
            terms |= chkSemis.isSelected() ? 4 : 0;
            terms |= chkFinals.isSelected() ? 8 : 0;
            currentCriterion.setTerms(terms);

            boolean newEntry = DB.save(currentCriterion);

            cmdSave.setDisable(true);
            if (newEntry) {
                criteria.add(currentCriterion);
                tblCriteria.getSelectionModel().select(currentCriterion);
                tblCriteria.scrollTo(currentCriterion);

                cmdSave.setText("Save");
                cmdAdd.setDisable(false);
            }

            mainApp.getRootNotification().show("Criterion saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdSaveAsAction(ActionEvent event) {

    }

    @FXML
    void mnuDeleteAction(ActionEvent event) {
        if (Dialogs.confirm("Delete Criterion", "Are you sure you want to delete this criterion?", currentCriterion.getName()) == ButtonType.OK)
            try {
                DB.delete(currentCriterion);
                criteria.remove(currentCriterion);
                mainApp.getRootNotification().show("Criterion deleted.");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    public void setClass(Clazz c) {
        currentClass = c;
        try {
            if (currentClass.isSHS()) {
                chkPrelims.setDisable(true);
                chkSemis.setDisable(true);
                lblPrelims.setDisable(true);
                lblSemis.setDisable(true);
                lblTPrelims.setDisable(true);
                lblTSemis.setDisable(true);
                lblRPrelims.setDisable(true);
                lblRSemis.setDisable(true);
            }
            criteria = DB.getCriteria(currentClass);
            criteria.addListener((ListChangeListener<Criterion>) change -> {
                computePercentages();
                updateChart();
            });
            computePercentages();

            tblCriteria.setItems(criteria);

            if (c.isSHS()) {
                cboTerms.setItems(FXCollections.observableArrayList("Midterms", "Finals"));
            } else {
                cboTerms.setItems(FXCollections.observableArrayList("Prelims", "Midterms", "Semis", "Finals"));
            }
            cboTerms.getSelectionModel().select(0);

        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    private void computePercentages() {
        int ptv = 0, mtv = 0, stv = 0, ftv = 0;
        for (Criterion criterion : criteria) {
            if (((criterion.getTerms() & 1)) > 0) {
                ptv += criterion.getPercentage();
            }
            if (((criterion.getTerms() & 2)) > 0) {
                mtv += criterion.getPercentage();
            }
            if (((criterion.getTerms() & 4)) > 0) {
                stv += criterion.getPercentage();
            }
            if (((criterion.getTerms() & 8)) > 0) {
                ftv += criterion.getPercentage();
            }
        }

        lblTPrelims.setText(Integer.toString(ptv) + "%");
        lblTMiderms.setText(Integer.toString(mtv) + "%");
        lblTSemis.setText(Integer.toString(stv) + "%");
        lblTFinals.setText(Integer.toString(ftv) + "%");

        lblRPrelims.setText(Integer.toString(100 - ptv) + "%");
        lblRMidterms.setText(Integer.toString(100 - mtv) + "%");
        lblRSemis.setText(Integer.toString(100 - stv) + "%");
        lblRFinals.setText(Integer.toString(100 - ftv) + "%");

        if (currentClass.isSHS()) {
            lblMidterms.setStyle(mtv < 100 ? "-fx-text-fill: #f00;" : null);
            lblFinals.setStyle(ftv < 100 ? "-fx-text-fill: #f00;" : null);
        } else {
            lblPrelims.setStyle(ptv < 100 ? "-fx-text-fill: #f00;" : null);
            lblMidterms.setStyle(mtv < 100 ? "-fx-text-fill: #f00;" : null);
            lblSemis.setStyle(stv < 100 ? "-fx-text-fill: #f00;" : null);
            lblFinals.setStyle(ftv < 100 ? "-fx-text-fill: #f00;" : null);
        }
    }

    private void showCriterionInfo() {
        txtName.setText(currentCriterion.getName());
        txtPercent.setText(Integer.toString(currentCriterion.getPercentage()));

        int terms = currentCriterion.getTerms();

        chkPrelims.setSelected((terms & 1) > 0);
        chkMidterms.setSelected((terms & 2) > 0);
        chkSemis.setSelected((terms & 4) > 0);
        chkFinals.setSelected((terms & 8) > 0);

        txtName.setDisable(false);
        txtPercent.setDisable(false);
        chkPrelims.setDisable(false);
        chkMidterms.setDisable(false);
        chkSemis.setDisable(false);
        chkFinals.setDisable(false);

        cmdSave.setDisable(true);
        cmdAdd.setDisable(false);
        cmdSave.setText("Save");
    }

    private void updateChart() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        if (currentClass.isSHS()) {
            switch (cboTerms.getSelectionModel().getSelectedIndex()) {
                case 0:
                    for (Criterion c : criteria) {
                        if ((c.getTerms() & 2) > 0)
                            data.add(new PieChart.Data(c.getName(), c.getPercentage()));
                    }
                    break;
                case 1:
                    for (Criterion c : criteria) {
                        if ((c.getTerms() & 8) > 0)
                            data.add(new PieChart.Data(c.getName(), c.getPercentage()));
                    }
                    break;
            }
        } else {
            switch (cboTerms.getSelectionModel().getSelectedIndex()) {
                case 0:
                    for (Criterion c : criteria) {
                        if ((c.getTerms() & 1) > 0)
                            data.add(new PieChart.Data(c.getName(), c.getPercentage()));
                    }
                    break;
                case 1:
                    for (Criterion c : criteria) {
                        if ((c.getTerms() & 2) > 0)
                            data.add(new PieChart.Data(c.getName(), c.getPercentage()));
                    }
                    break;
                case 2:
                    for (Criterion c : criteria) {
                        if ((c.getTerms() & 4) > 0)
                            data.add(new PieChart.Data(c.getName(), c.getPercentage()));
                    }
                    break;
                case 3:
                    for (Criterion c : criteria) {
                        if ((c.getTerms() & 8) > 0)
                            data.add(new PieChart.Data(c.getName(), c.getPercentage()));
                    }
                    break;
            }
        }

        chtPie.setData(data);
        for (final PieChart.Data item : chtPie.getData()) {
            item.getNode().setOnMouseClicked(e -> {
                for (Criterion c : criteria) {
                    if (currentClass.isSHS()) {
                        if (c.getName().equals(item.getName())) {
                            switch (cboTerms.getSelectionModel().getSelectedIndex()) {
                                case 0:
                                    if ((c.getTerms() & 2) > 0)
                                        tblCriteria.getSelectionModel().select(c);
                                    break;
                                case 1:
                                    if ((c.getTerms() & 8) > 0)
                                        tblCriteria.getSelectionModel().select(c);
                                    break;
                            }
                        }
                    } else {
                        if (c.getName().equals(item.getName())) {
                            switch (cboTerms.getSelectionModel().getSelectedIndex()) {
                                case 0:
                                    if ((c.getTerms() & 1) > 0)
                                        tblCriteria.getSelectionModel().select(c);
                                    break;
                                case 1:
                                    if ((c.getTerms() & 2) > 0)
                                        tblCriteria.getSelectionModel().select(c);
                                    break;
                                case 2:
                                    if ((c.getTerms() & 4) > 0)
                                        tblCriteria.getSelectionModel().select(c);
                                    break;
                                case 3:
                                    if ((c.getTerms() & 8) > 0)
                                        tblCriteria.getSelectionModel().select(c);
                                    break;
                            }
                        }
                    }
                }
            });
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        //colPercent.setCellValueFactory(cellData -> cellData.getValue().percentageProperty().asString());
        colPercent.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().percentageProperty(), "%"));

        tblCriteria.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                    if (cmdSave.isDisable()) {
                        currentCriterion = newV;
                        showCriterionInfo();
                    } else {
                        if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another criterion will discard your current unsaved changes.") == ButtonType.OK) {
                            currentCriterion = newV;
                            showCriterionInfo();
                            cmdAdd.setDisable(false);
                            cmdSave.setDisable(true);
                            cmdSave.setText("Save");
                        }
                    }
                }
        );

        txtName.textProperty().addListener((obs, ov, nv) -> {
            if (currentCriterion != null) cmdSave.setDisable(false);
        });
        txtPercent.textProperty().addListener((obs, ov, nv) -> {
            if (currentCriterion != null) cmdSave.setDisable(false);
        });
        chkPrelims.selectedProperty().addListener((obs, ov, nv) -> {
            if (currentCriterion != null) cmdSave.setDisable(false);
        });
        chkMidterms.selectedProperty().addListener((obs, ov, nv) -> {
            if (currentCriterion != null) cmdSave.setDisable(false);
        });
        chkSemis.selectedProperty().addListener((obs, ov, nv) -> {
            if (currentCriterion != null) cmdSave.setDisable(false);
        });
        chkFinals.selectedProperty().addListener((obs, ov, nv) -> {
            if (currentCriterion != null) cmdSave.setDisable(false);
        });

        validationSupport = new ValidationSupport();

        Validator<String> validator = (control, value) -> {
            boolean condition = false;

            try {
                int v = Integer.parseInt(value);
                if ((v < 0) || (v > 100))
                    condition = true;
            } catch (Exception e) {
                condition = true;
            }

            return ValidationResult.fromMessageIf(control, "Invalid percentage", Severity.ERROR, condition);
        };

        validationSupport.registerValidator(txtPercent, true, validator);

        cboTerms.valueProperty().addListener(e -> updateChart());
        //chtPie.setLabelLineLength(10);
        //chtPie.setLegendSide(Side.LEFT);
        chtPie.setLegendVisible(false);
        chtPie.setTitle("Criteria percentages");
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return "Criteria for " + currentClass.getName();
    }
}
