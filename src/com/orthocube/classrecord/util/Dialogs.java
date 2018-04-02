/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.util;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class Dialogs {

    private static final Image[] icons;

    static {
        icons = new Image[]{
                new Image(Dialogs.class.getClassLoader().getResourceAsStream("com/orthocube/classrecord/res/Dossier_16px.png")),
                new Image(Dialogs.class.getClassLoader().getResourceAsStream("com/orthocube/classrecord/res/Dossier_30px.png")),
                new Image(Dialogs.class.getClassLoader().getResourceAsStream("com/orthocube/classrecord/res/Dossier_40px.png")),
                new Image(Dialogs.class.getClassLoader().getResourceAsStream("com/orthocube/classrecord/res/Dossier_80px.png"))
        };
    }

    private static void addIcons(Alert a) {
        ((Stage) a.getDialogPane().getScene().getWindow()).getIcons().addAll(icons);
    }

    private static void addIcons(Dialog a) {
        ((Stage) a.getDialogPane().getScene().getWindow()).getIcons().addAll(icons);
    }

    // TODO: Fix null css

    public static void info(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //if (Settings.isDark) alert.getDialogPane().getStylesheets().add(Dialogs.class.getClassLoader().getResource("../res/modena_dark.css").toExternalForm());
        addIcons(alert);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static ButtonType warning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        //if (Settings.isDark) alert.getDialogPane().getStylesheets().add(Dialogs.class.getClassLoader().getResource("../res/modena_dark.css").toExternalForm());
        addIcons(alert);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert.showAndWait().get();
    }

    public static void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //if (Settings.isDark) alert.getDialogPane().getStylesheets().add(Dialogs.class.getClassLoader().getResource("../res/modena_dark.css").toExternalForm());
        addIcons(alert);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static void exception(Exception ex) {
        ex.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //if (Settings.isDark) alert.getDialogPane().getStylesheets().add(Dialogs.class.getClassLoader().getResource("../res/modena_dark.css").toExternalForm());
        addIcons(alert);
        alert.setTitle("Internal Error");
        alert.setHeaderText("An internal error has occurred!");
        alert.setContentText(ex.getLocalizedMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The program will try its best to recover from the \nerror but to be safe, please save all work and close the program.\nHere is the stacktrace in case you want to report this error,\nand you are encouraged to.");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static ButtonType confirm(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        //if (Settings.isDark) alert.getDialogPane().getStylesheets().add(Dialogs.class.getClassLoader().getResource("../res/modena_dark.css").toExternalForm());
        addIcons(alert);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert.showAndWait().get();
    }

    public static String textInput(String title, String header, String prompt, String defaultText) {
        TextInputDialog dialog = new TextInputDialog(defaultText);
        addIcons(dialog);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(prompt);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static String textInput(String title, String header, String prompt) {
        return textInput(title, header, prompt, "");
    }
}
