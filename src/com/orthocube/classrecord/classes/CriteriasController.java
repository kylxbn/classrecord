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
import com.orthocube.classrecord.util.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
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
    // </editor-fold>

    @FXML
    void cmdAddAction(ActionEvent event) {

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

    }

    @FXML
    void cmdSaveAsAction(ActionEvent event) {

    }

    @FXML
    void mnuDeleteAction(ActionEvent event) {

    }

    public void showClass(Clazz c) {
        currentClass = c;
        //try {
        if (currentClass.isSHS()) {
            chkPrelims.setDisable(true);
            chkSemis.setDisable(true);
        }
        //criterias = DB.getCriterias(currentClass);
        //tblEnrollees.setItems(enrollees);
        //} catch (SQLException e) {
        //    Dialogs.exception(e);
        //}
    }

    private void showCriteriaInfo() {
        txtName.setText(currentCriteria.getName());
        txtPercent.setText(Integer.toString(currentCriteria.getPercentage()));

        int terms = currentCriteria.getTerms();
        if (currentClass.isSHS()) {
            chkMidterms.setSelected((terms & 1) > 0);
            chkFinals.setSelected((terms & 2) > 0);
        } else {
            chkPrelims.setSelected((terms & 1) > 0);
            chkMidterms.setSelected((terms & 2) > 0);
            chkSemis.setSelected((terms & 4) > 0);
            chkFinals.setSelected((terms & 8) > 0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
        bundle = rb;

        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colPercent.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getPercentage(), "%"));

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
    }
}
