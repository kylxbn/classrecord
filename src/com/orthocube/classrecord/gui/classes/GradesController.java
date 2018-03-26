/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.classes;

import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Grade;
import com.orthocube.classrecord.gui.students.StudentEnrolledInController;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.printing.SHSGradePrinter;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.PrinterResolution;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GradesController implements Initializable {
    private final static Logger LOGGER = Logger.getLogger(StudentEnrolledInController.class.getName());
    private Clazz currentClass;
    ObservableList<Grade> grades;

    @FXML
    private TableView<Grade> tblGrades;

    @FXML
    private MenuItem mnuIncomplete;

    @FXML
    private MenuItem mnuDropped;

    @FXML
    private MenuItem mnuRemoveOverride;

    @FXML
    void mnuDroppedAction(ActionEvent event) {
//        try {
//            if (tblGrades.getSelectionModel().getSelectedItem() != null)
//                if (currentClass.isSHS()) {
//                    DB.setSHSDropped(tblGrades.getSelectionModel().getSelectedItem().getEnrolleeID());
//                } else {
//                    DB.setDropped(tblGrades.getSelectionModel().getSelectedItem().getEnrolleeID());
//                }
//        } catch (SQLException e) {
//            Dialogs.exception(e);
//        }
    }

    @FXML
    void mnuIncompleteAction(ActionEvent event) {
//        try {
//            if (tblGrades.getSelectionModel().getSelectedItem() != null)
//                if (currentClass.isSHS()) {
//                    DB.setSHSIncomplete(tblGrades.getSelectionModel().getSelectedItem().getEnrolleeID());
//                } else {
//                    DB.setIncomplete(tblGrades.getSelectionModel().getSelectedItem().getEnrolleeID());
//                }
//        } catch (SQLException e) {
//            Dialogs.exception(e);
//        }
    }

    @FXML
    void mnuRemoveOverrideAction(ActionEvent event) {
//        try {
//            if (tblGrades.getSelectionModel().getSelectedItem() != null)
//                if (currentClass.isSHS()) {
//                    DB.clearSHSOverride(tblGrades.getSelectionModel().getSelectedItem().getEnrolleeID());
//                } else {
//                    DB.clearOverride(tblGrades.getSelectionModel().getSelectedItem().getEnrolleeID());
//                }
//        } catch (SQLException e) {
//            Dialogs.exception(e);
//        }
    }

    public void setClass(Clazz c) {
        currentClass = c;
        try {
            if (currentClass.isSHS()) {
                grades = DB.getSHSGrades(c);
                TableColumn<Grade, String> colStudent = new TableColumn<>("Student");
                TableColumn<Grade, String> colMidterms = new TableColumn<>("Midterms");
                TableColumn<Grade, String> colFinals = new TableColumn<>("Finals");
                TableColumn<Grade, String> colFinal = new TableColumn<>("Final Grade");
                TableColumn<Grade, String> colRemarks = new TableColumn<>("Remarks");
                colStudent.setCellValueFactory(cellD -> Bindings.concat(cellD.getValue().lnameProperty(), ", ", cellD.getValue().fnameProperty()));
                colMidterms.setCellValueFactory(cellD -> cellD.getValue().midtermsProperty());
                colFinals.setCellValueFactory(cellD -> cellD.getValue().finalsProperty());
                colFinal.setCellValueFactory(cellD -> cellD.getValue().finalProperty());
                colRemarks.setCellValueFactory(cellD -> cellD.getValue().remarksProperty());
                tblGrades.getColumns().addAll(colStudent, colMidterms, colFinals, colFinal, colRemarks);
                mnuIncomplete.setDisable(true);
            } else {
                grades = DB.getCollegeGrades(c);
                TableColumn<Grade, String> colStudent = new TableColumn<>("Student");
                TableColumn<Grade, String> colPrelims = new TableColumn<>("Prelims");
                TableColumn<Grade, String> colMidterms = new TableColumn<>("Midterms");
                TableColumn<Grade, String> colSemis = new TableColumn<>("Semis");
                TableColumn<Grade, String> colFinals = new TableColumn<>("Finals");
                TableColumn<Grade, String> colFinal = new TableColumn<>("Final Grade");
                TableColumn<Grade, String> colRemarks = new TableColumn<>("Remarks");
                TableColumn<Grade, Number> colClassCard = new TableColumn<>("Class Card");
                colStudent.setCellValueFactory(cellD -> Bindings.concat(cellD.getValue().lnameProperty(), ", ", cellD.getValue().fnameProperty()));
                colPrelims.setCellValueFactory(cellD -> cellD.getValue().prelimProperty());
                colMidterms.setCellValueFactory(cellD -> cellD.getValue().midtermsProperty());
                colSemis.setCellValueFactory(cellD -> cellD.getValue().semisProperty());
                colFinals.setCellValueFactory(cellD -> cellD.getValue().finalsProperty());
                colFinal.setCellValueFactory(cellD -> cellD.getValue().finalProperty());
                colClassCard.setCellValueFactory(cellD -> cellD.getValue().classCardProperty());
                colRemarks.setCellValueFactory(cellD -> cellD.getValue().remarksProperty());
                tblGrades.getColumns().addAll(colStudent, colPrelims, colMidterms, colSemis, colFinals, colFinal, colRemarks, colClassCard);
            }

            tblGrades.setItems(grades);
        } catch (SQLException | IOException e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void cmdPrintAction(ActionEvent event) {
        int dpi = 86;

        PrinterJob job = PrinterJob.getPrinterJob();
        Paper paper = new Paper();
        paper.setSize(8.5 * 72.0, 13 * 72.0);
        paper.setImageableArea(0, 0, 8.5 * 72.0, 13 * 72.0);

        PageFormat pf = new PageFormat();

        pf.setPaper(paper);
        SHSGradePrinter printer = new SHSGradePrinter(dpi);
        printer.setClass(currentClass);
        job.setPrintable(printer, pf);

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
                PrinterResolution pr = new PrinterResolution(dpi, dpi, ResolutionSyntax.DPI);
                set.add(pr);
                job.setJobName("SHS Grades");
                job.print(set);
            } catch (PrinterException ex) {
                Dialogs.exception(ex);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        LOGGER.log(Level.INFO, "Initializing...");
    }

    public String getTitle() {
        return currentClass.getName() + " Grades";
    }
}
