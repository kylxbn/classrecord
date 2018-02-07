/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Criteria;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CriteriasController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(EnrolleesController.class.getName());

    private ResourceBundle bundle;

    private MainApp mainApp;
    private ObservableList<Criteria> criterias;
    private Clazz currentClass;
    private Criteria currentCriteria;

    ValidationSupport validationSupport;

    // <editor-fold defaultstate="collapsed" desc="controls">
    @FXML
    private TableView<Criteria> tblCriterias;

    @FXML
    private TableColumn<Criteria, String> colName;

    @FXML
    private TableColumn<Criteria, String> colPercent;

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

    // </editor-fold>

    @FXML
    void cmdAddAction(ActionEvent event) {
        if (!cmdSave.isDisable()) {
            if (Dialogs.confirm("New criteria", "You have unsaved changes. Discard changes?", "Creating another criteria will discard\nyour current unsaved changes.") == ButtonType.CANCEL) {
                return;
            }
        }

        currentCriteria = new Criteria();
        currentCriteria.setClass(currentClass);
        showCriteriaInfo();
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
            currentCriteria.setName(txtName.getText());
            currentCriteria.setPercentage(Integer.parseInt(txtPercent.getText()));

            int terms = 0;
            terms |= chkPrelims.isSelected() ? 1 : 0;
            terms |= chkMidterms.isSelected() ? 2 : 0;
            terms |= chkSemis.isSelected() ? 4 : 0;
            terms |= chkFinals.isSelected() ? 8 : 0;
            currentCriteria.setTerms(terms);

            boolean newEntry = DB.save(currentCriteria);

            cmdSave.setDisable(true);
            if (newEntry) {
                criterias.add(currentCriteria);
                tblCriterias.getSelectionModel().select(currentCriteria);
                tblCriterias.scrollTo(currentCriteria);

                cmdSave.setText("Save");
                cmdAdd.setDisable(false);
            }

            mainApp.getRootNotification().show("Criteria saved.");
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdSaveAsAction(ActionEvent event) {

    }

    @FXML
    void mnuDeleteAction(ActionEvent event) {
        if (Dialogs.confirm("Delete Criteria", "Are you sure you want to delete this criteria?", currentCriteria.getName()) == ButtonType.OK)
            try {
                DB.delete(currentCriteria);
                criterias.remove(currentCriteria);
                mainApp.getRootNotification().show("Criteria deleted.");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    public void showClass(Clazz c) {
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
            criterias = DB.getCriterias(currentClass);
            criterias.addListener((ListChangeListener<Criteria>) change -> {
                computePercentages();
            });
            computePercentages();

            tblCriterias.setItems(criterias);
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    private void computePercentages() {
        int ptv = 0, mtv = 0, stv = 0, ftv = 0;
        for (Criteria criteria : criterias) {
            if (((criteria.getTerms() & 1)) > 0) {
                ptv += criteria.getPercentage();
            }
            if (((criteria.getTerms() & 2)) > 0) {
                mtv += criteria.getPercentage();
            }
            if (((criteria.getTerms() & 4)) > 0) {
                stv += criteria.getPercentage();
            }
            if (((criteria.getTerms() & 8)) > 0) {
                ftv += criteria.getPercentage();
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

    private void showCriteriaInfo() {
        txtName.setText(currentCriteria.getName());
        txtPercent.setText(Integer.toString(currentCriteria.getPercentage()));

        int terms = currentCriteria.getTerms();

        chkPrelims.setSelected((terms & 1) > 0);
        chkMidterms.setSelected((terms & 2) > 0);
        chkSemis.setSelected((terms & 4) > 0);
        chkFinals.setSelected((terms & 8) > 0);

        cmdSave.setDisable(true);
        cmdSave.setText("Save");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        //colPercent.setCellValueFactory(cellData -> cellData.getValue().percentageProperty().asString());
        colPercent.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().percentageProperty(), "%"));

        tblCriterias.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                    if (cmdSave.isDisable()) {
                        currentCriteria = newV;
                        showCriteriaInfo();
                    } else {
                        if (Dialogs.confirm("Change selection", "You have unsaved changes. Discard changes?", "Choosing another enrollee will discard your current unsaved changes.") == ButtonType.OK) {
                            currentCriteria = newV;
                            showCriteriaInfo();
                            cmdAdd.setDisable(false);
                            cmdSave.setDisable(true);
                            cmdSave.setText("Save");
                        }
                    }
                }
        );

        txtName.textProperty().addListener((obs, ov, nv) -> cmdSave.setDisable(false));
        txtPercent.textProperty().addListener((obs, ov, nv) -> cmdSave.setDisable(false));
        chkPrelims.selectedProperty().addListener((obs, ov, nv) -> cmdSave.setDisable(false));
        chkMidterms.selectedProperty().addListener((obs, ov, nv) -> cmdSave.setDisable(false));
        chkSemis.selectedProperty().addListener((obs, ov, nv) -> cmdSave.setDisable(false));
        chkFinals.selectedProperty().addListener((obs, ov, nv) -> cmdSave.setDisable(false));

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
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public String getTitle() {
        return "Criterias for " + currentClass.getName();
    }
}
