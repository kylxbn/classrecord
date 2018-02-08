/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.classes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AttendanceController implements Initializable {

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private TableView<?> tblAttendanceDays;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colDaysNotes;

    @FXML
    private DatePicker dpkDate;

    @FXML
    private TextField txtDaysNotes;

    @FXML
    private Button cmdSaveDay;

    @FXML
    private Button cmdAddDay;

    @FXML
    private Button cmdQuickAdd;

    @FXML
    private TableView<?> tblAttendanceList;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colRemarks;

    @FXML
    private TableColumn<?, ?> colListNotes;

    @FXML
    private ChoiceBox<?> cboRemarks;

    @FXML
    private TextField txtListNotes;

    @FXML
    private Label lblLastMeeting;

    @FXML
    private Button cmdSaveList;

    @FXML
    private StackedBarChart<?, ?> chtHistory;
    // </editor-fold>

    @FXML
    void cmdAbsentAction(ActionEvent event) {

    }

    @FXML
    void cmdAddDayAction(ActionEvent event) {

    }

    @FXML
    void cmdLateAction(ActionEvent event) {

    }

    @FXML
    void cmdOthersAction(ActionEvent event) {

    }

    @FXML
    void cmdPresentAction(ActionEvent event) {

    }

    @FXML
    void cmdQuickAddAction(ActionEvent event) {

    }

    @FXML
    void cmdSaveDayAction(ActionEvent event) {

    }

    @FXML
    void cmdSaveListAction(ActionEvent event) {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
