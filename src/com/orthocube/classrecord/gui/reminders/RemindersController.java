/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.reminders;

import com.orthocube.classrecord.MainApp;
import com.orthocube.classrecord.data.Clazz;
import com.orthocube.classrecord.data.Reminder;
import com.orthocube.classrecord.data.User;
import com.orthocube.classrecord.util.DB;
import com.orthocube.classrecord.util.Dialogs;
import com.orthocube.classrecord.util.Utils;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.ResourceBundle;

import static com.orthocube.classrecord.util.Utils.toAMPM;
import static java.time.temporal.ChronoUnit.DAYS;

public class RemindersController implements Initializable {
    private static final Color[][] classColors = {
            {Color.decode("0xa30000"), Color.decode("0xc43c00"), Color.decode("0xc67c00"), Color.decode("0xc7a500"), Color.decode("0x79b700"), Color.decode("0x1faa00"), Color.decode("0x009624"), Color.decode("0x008e76"), Color.decode("0x0088a3"), Color.decode("0x0064b7"), Color.decode("0x0039cb"), Color.decode("0x0026ca"), Color.decode("0x0a00b6"), Color.decode("0x7200ca"), Color.decode("0x8e0038"), Color.decode("0x9b0000")},
            {Color.decode("0xff6434"), Color.decode("0xff9e40"), Color.decode("0xffdd4b"), Color.decode("0xffff52"), Color.decode("0xe4ff54"), Color.decode("0x9cff57"), Color.decode("0x5efc82"), Color.decode("0x5df2d6"), Color.decode("0x62ebff"), Color.decode("0x64c1ff"), Color.decode("0x768fff"), Color.decode("0x7a7cff"), Color.decode("0x9d46ff"), Color.decode("0xe254ff"), Color.decode("0xfd558f"), Color.decode("0xff5131")}};
    private MainApp mainApp;
    private Reminder currentReminder;
    private ObservableList<Reminder> reminders;
    private FilteredList<Reminder> filteredReminders;
    private String savedStartTime, savedEndTime;
    private ValidationSupport validationSupport;
    private ObservableList<Clazz> classes;
    private boolean isDark = true;

    private User currentUser = new User();

    // <editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private VBox vbxShow;

    @FXML
    private CheckBox chkAll;

    @FXML
    private TableView<Reminder> tblReminders;

    @FXML
    private TableColumn<Reminder, Date> colDate;

    @FXML
    private TableColumn<Reminder, String> colTitle;

    @FXML
    private MenuItem mnuRemove;

    @FXML
    private Button cmdAdd;

    @FXML
    private VBox vbxEdit;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtLocation;

    @FXML
    private DatePicker dpkStartDate;

    @FXML
    private DatePicker dpkEndDate;

    @FXML
    private TextField txtStartTime;

    @FXML
    private TextField txtEndTime;

    @FXML
    private CheckBox chkWholeDay;

    @FXML
    private TextArea txtNotes;

    @FXML
    private CheckBox chkDone;

    @FXML
    private Button cmdCancel;

    @FXML
    private Button cmdSave;

    @FXML
    private ChoiceBox<String> cboYear;

    @FXML
    private ChoiceBox<String> cboSem;

    @FXML
    private Pane pnlSchedule;

    @FXML
    private ImageView pboSchedule;

    @FXML
    private Button cmdRefresh;
    // </editor-fold>

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setUser(User u) {
        currentUser = u;
        if (currentUser.getAccessLevel() < 2) {
            cmdAdd.setDisable(true);
            mnuRemove.setDisable(true);

            txtTitle.setEditable(false);
            txtStartTime.setEditable(false);
            txtEndTime.setEditable(false);
            txtLocation.setEditable(false);
            txtNotes.setEditable(false);
        }
    }

    public void setModel(ObservableList<Reminder> model) {
        reminders = model;

        filteredReminders = new FilteredList<>(model, p -> true);
        chkAll.selectedProperty().addListener((obs, oldv, newv) -> updateFilters());
        updateFilters();

        SortedList<Reminder> sortedReminder = new SortedList<>(filteredReminders);
        sortedReminder.comparatorProperty().bind(tblReminders.comparatorProperty());
        tblReminders.setItems(sortedReminder);
    }

    private void updateFilters() {
        filteredReminders.setPredicate(reminder -> chkAll.isSelected() || !reminder.isDone() && (DAYS.between(LocalDate.now(), reminder.getEndDate().toLocalDate()) <= 31));
    }

    public String getTitle() {
        // TODO: Fix this
        return "Reminders";
    }

    @FXML
    void cmdAddAction(ActionEvent event) {
        currentReminder = new Reminder();
        savedStartTime = null;
        savedEndTime = null;
        showReminderInfo();
        cmdSave.setText("Save as new");
        editMode(true);
    }

    @FXML
    void cmdRefreshAction(ActionEvent event) {
        refreshSchedule();
    }

    @FXML
    void cmdCancelAction(ActionEvent event) {
        currentReminder = tblReminders.getSelectionModel().getSelectedItem();
        cmdSave.setText("Save");
        showReminderInfo();
        editMode(false);
    }

    @FXML
    void cmdSaveAction(ActionEvent event) {
        if (!chkWholeDay.isSelected()) {
            if (txtStartTime.getText().equals("") || txtEndTime.getText().equals("")) {
                Dialogs.error("Invalid input", "Time is required.", "If a reminder does not cover a whole day,\nits start and end times must be specified.");
                return;
            } else if (validationSupport.isInvalid()) {
                Dialogs.error("Invalid input", "The time you entered is invalid.", "Please correct any errors before saving.");
                return;
            } else if ((DAYS.between(dpkStartDate.getValue(), dpkEndDate.getValue()) < 1) && (txtStartTime.getText().compareTo(txtEndTime.getText()) > 0)) {
                Dialogs.error("Invalid input", "End time is earlier than start time", "If in the same day, the end time can't be earlier\nthan the start time.");
                return;
            }
        }

        if (DAYS.between(dpkStartDate.getValue(), dpkEndDate.getValue()) < 0) {
            Dialogs.error("Invalid input", "End date is earlier than start time", "That's just not possible.");
            return;
        }

        try {
            currentReminder.setTitle(txtTitle.getText());
            currentReminder.setStartDate(dpkStartDate.getValue());
            currentReminder.setEndDate(dpkEndDate.getValue());
            if (chkWholeDay.isSelected()) {
                currentReminder.setStartTime(null);
                currentReminder.setEndTime(null);
            } else {
                txtStartTime.setText(Utils.sanitizeTime(txtStartTime.getText()));
                txtEndTime.setText(Utils.sanitizeTime(txtEndTime.getText()));
                currentReminder.setStartTime(Time.valueOf(txtStartTime.getText() + ":00"));
                currentReminder.setEndTime(Time.valueOf(txtEndTime.getText() + ":00"));
            }
            currentReminder.setLocation(txtLocation.getText());
            currentReminder.setNotes(txtNotes.getText());
            currentReminder.setDone(chkDone.isSelected());

            boolean newentry = DB.save(currentReminder);

            if (newentry) {
                reminders.add(currentReminder);
                tblReminders.getSelectionModel().select(currentReminder);
                tblReminders.scrollTo(currentReminder);

                cmdSave.setText("Save");
            }

            mainApp.getRootNotification().show("Reminder saved.");
            updateFilters();

            editMode(false);
        } catch (Exception e) {
            Dialogs.exception(e);
        }
    }

    @FXML
    void mnuRemoveAction(ActionEvent event) {
        if (currentReminder == null) return;
        if (Dialogs.confirm("Delete Reminder", "Are you sure you want to delete this reminder?", currentReminder.getTitle()) == ButtonType.OK)
            try {
                DB.delete(currentReminder);
                reminders.remove(currentReminder);
                mainApp.getRootNotification().show("Reminder deleted.");
            } catch (SQLException e) {
                Dialogs.exception(e);
            }
    }

    private void showReminderInfo() {
        if (currentReminder != null) {
            vbxEdit.setDisable(false);
            txtTitle.setText(currentReminder.getTitle());

            // if the reminder is for the whole day
            if ((currentReminder.getStartTime() == null) && (currentReminder.getEndTime() == null)) {
                chkWholeDay.setSelected(true);
                txtStartTime.setText("");
                txtEndTime.setText("");
                txtStartTime.setDisable(true);
                txtEndTime.setDisable(true);
            } else {
                chkWholeDay.setSelected(false);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                txtStartTime.setText(currentReminder.getStartTime().toLocalTime().format(dtf));
                txtEndTime.setText(currentReminder.getEndTime().toLocalTime().format(dtf));
                txtStartTime.setDisable(false);
                txtEndTime.setDisable(false);
            }

            dpkStartDate.setValue(currentReminder.getStartDate().toLocalDate());
            dpkEndDate.setValue(currentReminder.getEndDate().toLocalDate());

            txtLocation.setText(currentReminder.getLocation());
            txtNotes.setText(currentReminder.getNotes());

            chkDone.setSelected(currentReminder.isDone());
        } else {
            vbxEdit.setDisable(true);

            txtTitle.setText("");

            chkWholeDay.setSelected(false);
            txtStartTime.setText("");
            txtEndTime.setText("");

            dpkStartDate.setValue(null);
            dpkEndDate.setValue(null);

            txtLocation.setText("");
            txtNotes.setText("");

            chkDone.setSelected(false);
        }
    }

    private void editMode(boolean t) {
        if (t && (currentUser.getAccessLevel() > 1)) {
            vbxShow.setDisable(true);
            cmdSave.setDisable(false);
            cmdCancel.setVisible(true);
            cmdAdd.setDisable(true);
        } else {
            vbxShow.setDisable(false);
            cmdSave.setDisable(true);
            cmdCancel.setVisible(false);
            if (currentUser.getAccessLevel() > 1)
                cmdAdd.setDisable(false);
        }
    }

    private void drawCenteredString(Graphics g, String s, int x, int y, int w, int h) {
        String[] lines = s.split("\n");

        FontMetrics metrics = g.getFontMetrics();
        int oneLineHeight = (metrics.getHeight() + metrics.getAscent()) / 2;
        int centerY = y + ((h - metrics.getHeight()) / 2) + metrics.getAscent();
        int startY = centerY - (oneLineHeight * lines.length / 2) + metrics.getAscent() / 2;

        for (int i = 0; i < lines.length; i++) {
            int rx = x + (w - metrics.stringWidth(lines[i])) / 2;
            int ry = startY + oneLineHeight * i;
            g.drawString(lines[i], rx, ry);
        }
    }

    private void drawCenteredString2(Graphics g, String s, int x, int y, int w, int h) {
        g.setFont(new Font("default", Font.BOLD, 12));

        String[] lines = s.split("\n");

        FontMetrics metrics = g.getFontMetrics();
        int oneLineHeight = (metrics.getHeight() + metrics.getAscent()) / 2;
        int centerY = y + ((h - metrics.getHeight()) / 2) + metrics.getAscent();
        int startY = centerY - (oneLineHeight * lines.length / 2) + metrics.getAscent() / 2;

        for (int i = 0; i < lines.length; i++) {
            if (i > 0)
                g.setFont(new Font("default", Font.PLAIN, 12));

            StringBuilder line = new StringBuilder(lines[i]);
            if (metrics.stringWidth(line.toString()) > w) {
                line.append("…");
                while (metrics.stringWidth(line.toString()) > w)
                    line.deleteCharAt(line.length() - 2);
            }

            int rx = x + (w - metrics.stringWidth(line.toString())) / 2;
            int ry = startY + oneLineHeight * i;
            g.drawString(line.toString(), rx, ry);
        }
    }

    private String getDay(int i) {
        switch (i) {
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            default:
                return "INVALID";
        }
    }

    private String toRoman(int y) {
        switch (y) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return "ERR";
        }
    }

    private void redrawSchedule() {
        int w = (int) (pnlSchedule.getWidth());
        int h = (int) (pnlSchedule.getHeight());
        if ((w <= 0) || (h <= 0)) return;
        pboSchedule.setFitWidth(w);
        pboSchedule.setFitHeight(h);
        BufferedImage s = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) s.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(isDark ? Color.BLACK : Color.WHITE);
        g.fillRect(0, 0, w, h);

        double cellwidth = ((w - 1) / 8.0);
        double cellheight = ((h - 1) / 13.0);

        // draw backgrounds
        for (int i = 0; i < 13; i++) {
            if (i == 0)
                g.setColor(isDark ? Color.decode("0x404040") : Color.decode("0xE4E4E4"));
            else
                g.setColor(i % 2 == 1 ? (isDark ? Color.decode("0x141414") : Color.WHITE) : (isDark ? Color.decode("0x171717") : Color.decode("0xF9F9F9")));
            int y = (int) (cellheight * i);
            g.fillRect(0, y, w, (int) cellheight);
        }
        g.setColor(isDark ? Color.decode("0x404040") : Color.decode("0xE4E4E4"));
        g.fillRect(0, 0, (int) cellwidth, h);

        // show current date
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        g.setColor(Color.decode("0x808080"));
        g.fillRect((int) (cellwidth * day), (int) cellheight, (int) cellwidth, h);

        // draw current time
        LocalTime lt = LocalTime.now();
        int hour = lt.getHour();
        int minute = lt.getMinute();
        double time = hour + (minute / 60.0);
        int timey = (int) ((time - 7) * cellheight + cellheight);
        g.setColor(Color.RED);
        g.drawLine(0, timey - 1, w, timey - 1);
        g.drawLine(0, timey, w, timey);
        g.drawLine(0, timey + 1, w, timey + 1);

        // draw grids
        g.setColor(isDark ? Color.decode("0x444444") : Color.decode("0xEDEDED"));
        for (int i = 0; i < 9; i++) {
            int x = (int) (cellwidth * i);
            g.drawLine(x, 0, x, h);
        }
        for (int i = 0; i < 14; i++) {
            int y = (int) (cellheight * i);
            g.drawLine(0, y, w, y);
        }

        // draw days
        g.setColor(isDark ? Color.decode("0xDBDBDB") : Color.decode("0x323232"));
        g.setFont(new Font("default", Font.BOLD, 16));
        for (int i = 0; i < 7; i++) {
            int x = (int) (cellwidth * (i + 1));
            int y = 0;
            drawCenteredString(g, getDay(i), x, y, (int) cellwidth, (int) cellheight);
        }
        // draw times
        for (int i = 0; i < 12; i++) {
            int x = 0;
            int y = (int) ((cellheight * (i + 1)) - (cellheight / 2));
            drawCenteredString(g, toAMPM(Utils.sanitizeTime(Integer.toString((i + 7)) + ":00")), x, y, (int) cellwidth, (int) cellheight);
        }

        if (classes == null) {
            g.dispose();
            pboSchedule.setImage(SwingFXUtils.toFXImage(s, null));
            return;
        }

        // draw subjects
        int classnum = 0;
        for (Clazz c : classes) {
            for (int i = 0; i < 7; i++) {
                if ((c.getDays() & (1 << i)) > 0) {
                    String timeStartString = c.getTimes().get(i * 2);
                    String timeEndString = c.getTimes().get(i * 2 + 1);
                    double timeStart = (Double.parseDouble(timeStartString.split(":")[0]) + (Double.parseDouble(timeStartString.split(":")[1]) / 60.0));
                    double timeEnd = (Double.parseDouble(timeEndString.split(":")[0]) + (Double.parseDouble(timeEndString.split(":")[1]) / 60.0));

                    int x = (int) (cellwidth + cellwidth * i);
                    int y = (int) ((timeStart - 7) * cellheight + cellheight);
                    int h2 = (int) ((timeEnd - 7) * cellheight + cellheight) - y;

                    g.setColor(classColors[isDark ? 0 : 1][classnum % classColors[0].length]);
                    g.fillRect(x, y, (int) cellwidth + 1, h2);
                    g.setColor(isDark ? Color.decode("0x444444") : Color.decode("0xEDEDED"));
                    g.drawRect(x, y, (int) cellwidth + 1, h2);
                    g.setColor(Utils.getBrightness(classColors[isDark ? 0 : 1][classnum % classColors[0].length]) > 0.5 ? Color.BLACK : Color.WHITE);
                    drawCenteredString2(g, c.getName() + "\n" +
                                    toAMPM(timeStartString) + "-" + toAMPM(timeEndString) + "\n" +
                                    (c.getRoom().trim().length() > 0 ? "Room " + c.getRoom() + "\n" : "") +
                                    (c.getCourse().trim().length() > 0 ? c.getCourse() : "") +
                                    (c.isSHS() ?
                                            (c.getYear() > 10 ? " Grade " + Integer.toString(c.getYear()) : "") :
                                            (c.getYear() > 0 ? " " + toRoman(c.getYear()) : "")
                                    )
                            , x, y, (int) cellwidth, h2);
                }
            }
            classnum++;
        }

        g.dispose();

        pboSchedule.setImage(SwingFXUtils.toFXImage(s, null));
    }

    private void initReminders() {
        colDate.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        colTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());

        tblReminders.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> {
                    currentReminder = newV;
                    showReminderInfo();
                    editMode(false);
                }
        );

        chkWholeDay.selectedProperty().addListener((obs, oldv, newv) -> {
            if (newv) {
                savedStartTime = txtStartTime.getText();
                savedEndTime = txtEndTime.getText();
                txtStartTime.setText("");
                txtEndTime.setText("");
                txtStartTime.setDisable(true);
                txtEndTime.setDisable(true);
                chkWholeDay.setSelected(true);
            } else {
                txtStartTime.setText(savedStartTime == null ? "00:00" : savedStartTime);
                txtEndTime.setText(savedEndTime == null ? "23:59" : savedEndTime);
                txtStartTime.setDisable(false);
                txtEndTime.setDisable(false);
                chkWholeDay.setSelected(false);
            }
        });

        dpkStartDate.valueProperty().addListener((obs, oldv, newv) -> {
            if (DAYS.between(newv, dpkEndDate.getValue()) < 0) dpkEndDate.setValue(newv);
        });

        validationSupport = new ValidationSupport();
        Validator<String> timeValidator = (control, value) -> {
            boolean condition = false;

            if (!value.equals("")) {
                if (value.length() < 3) {
                    condition = true;
                } else if (value.split(":").length != 2) {
                    condition = true;
                } else {
                    try {
                        String[] split = value.split(":");
                        int hour = Integer.parseInt(split[0]);
                        int minute = Integer.parseInt(split[1]);
                        if ((hour < 0) || (hour > 23))
                            condition = true;
                        else if ((minute < 0) || (minute > 59))
                            condition = true;
                    } catch (Exception e) {
                        condition = true;
                    }
                }
            }

            return ValidationResult.fromMessageIf(control, "Invalid time format.", Severity.ERROR, condition);
        };

        validationSupport.registerValidator(txtStartTime, false, timeValidator);
        validationSupport.registerValidator(txtEndTime, false, timeValidator);

        // <editor-fold defaultstate="collapsed" desc="Listeners">
        txtTitle.textProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });
        dpkStartDate.valueProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });
        txtStartTime.textProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });
        dpkEndDate.valueProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });
        txtEndTime.textProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });
        chkWholeDay.selectedProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });
        chkDone.selectedProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });
        txtLocation.textProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });
        txtNotes.textProperty().addListener((obs, ov, nv) -> {
            if (currentReminder != null) editMode(true);
        });

        // </editor-fold>
    }

    private String interpretSemester(String s) {
        switch (s) {
            // TODO: Properly TL this
            case "1":
                return "1st";
            case "2":
                return "2nd";
            case "3":
                return "Summer";
            default:
                return "UNDEFINED";
        }
    }

    private int interpretSemesterToInt(String o) {
        if (o.equals("1st")) return 1;
        else if (o.equals("2nd")) return 2;
        else if (o.equals("Summer")) return 3;
        else return 0;
    }


    private void chooseAgain(ChoiceBox<String> cbo, String o) {
        if (o == null) {
            cbo.getSelectionModel().select(null);
            return;
        }

        for (int i = 0; i < cbo.getItems().size(); i++) {
            if (cbo.getItems().get(i).contains(o)) {
                cbo.getSelectionModel().select(i);
                return;
            }
        }

        cbo.getSelectionModel().select(null);
    }

    private void reloadSchedule() {
        try {
            if ((cboYear.getSelectionModel().getSelectedItem() != null) && (cboSem.getSelectionModel().getSelectedItem() != null)) {
                classes = DB.getClasses(Integer.parseInt(cboYear.getSelectionModel().getSelectedItem().split(" - ")[0]), interpretSemesterToInt(cboSem.getSelectionModel().getSelectedItem()));
            }
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    public void refreshSchedule() {
        try {
            ObservableList<String> sys = DB.getSchoolYears();
            ObservableList<String> sems = DB.getSemesters();

            String origYear = cboYear.getValue();
            String origSem = cboSem.getValue();

            for (int i = 0; i < sems.size(); i++)
                sems.set(i, interpretSemester(sems.get(i)));

            cboYear.setItems(sys);
            cboSem.setItems(sems);

            chooseAgain(cboYear, origYear);
            chooseAgain(cboSem, origSem);

            reloadSchedule();
            redrawSchedule();
        } catch (SQLException e) {
            Dialogs.exception(e);
        }
    }

    private void initSchedule() {
        pnlSchedule.widthProperty().addListener((obs, oldv, newv) -> redrawSchedule());
        pnlSchedule.heightProperty().addListener((obs, oldv, newv) -> redrawSchedule());

        cboYear.valueProperty().addListener((obs, oldv, newv) -> {
            reloadSchedule();
            redrawSchedule();
        });
        cboSem.valueProperty().addListener((obs, oldv, newv) -> {
            reloadSchedule();
            redrawSchedule();
        });

        refreshSchedule();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        String sem;
        if ((month >= 5) && (month < 9)) {
            sem = "1st";
        } else if ((month == 3) || (month == 4)) {
            sem = "Summer";
            year--;
        } else {
            sem = "2nd";
            year--;
        }

        chooseAgain(cboYear, Integer.toString(year) + " - " + Integer.toString(year + 1));
        chooseAgain(cboSem, sem);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResourceBundle bundle = resources;

        initSchedule();
        initReminders();
    }

    public void setDark() {
        isDark = true;
        redrawSchedule();
    }

    public void setLight() {
        isDark = false;
        redrawSchedule();
    }
}
