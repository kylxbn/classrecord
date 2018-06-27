/*
 * Copyright © Kyle Alexander Buan - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Kyle Alexander Buan <orthocube@protonmail.com>, 2018.
 */

package com.orthocube.classrecord.gui.dashboard;

import com.orthocube.classrecord.gui.dashboard.provider.ConsecutiveAbsences;
import com.orthocube.classrecord.gui.dashboard.provider.DashboardProvider;
import com.orthocube.classrecord.gui.dashboard.provider.DefaultCredentials;
import com.orthocube.classrecord.gui.dashboard.provider.NoTasks;
import com.orthocube.classrecord.util.Settings;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

class DashboardPoller extends Task<List<Node>> {

    private Node noNotifications;

    public DashboardPoller() {
        Node content = new Label("There are no notifications for now.");
        noNotifications = wrap("Hello, there.", content);
        noNotifications.setOpacity(0);
    }

    private List<DashboardProvider> getProviders() {
        List<DashboardProvider> dashboardProviders = new ArrayList<>();
        dashboardProviders.add(new NoTasks());
        dashboardProviders.add(new DefaultCredentials());
        dashboardProviders.add(new ConsecutiveAbsences());

        return dashboardProviders;
    }

    @Override
    protected List<Node> call() {
        List<Node> notifications = new ArrayList<>();

        for (DashboardProvider provider : getProviders()) {
            Node content = provider.getContent();
            if (content != null) {
                Node wrapped = wrap(provider.getTitle(), content);
                wrapped.setOpacity(0);
                notifications.add(wrapped);
            }
        }

        // if there are no notifications, just add a default "No notifications" message.
        if (notifications.size() == 0) {
            notifications.add(noNotifications);
        }
        return notifications;
    }

    private Node wrap(String t, Node notification) {
        VBox result = new VBox(8.0);
        result.setPadding(new Insets(8.0));
        if (Settings.isDark)
            result.setStyle("-fx-background-color: #444; -fx-background-radius: 8;");
        else
            result.setStyle("-fx-background-color: #CCC; -fx-background-radius: 8;");
        result.setMinHeight(100);
        result.setPrefHeight(100);
        result.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Label title = new Label(t);
        title.setStyle("-fx-font-size: 16");

        Pane spring = new Pane();
        spring.setMinSize(1, 1);
        spring.setPrefSize(1, 1);
        spring.setMaxSize(Double.MAX_VALUE, 1);

        Button expandButton = new Button("Expand");
        expandButton.setOnAction(e -> {
            if (result.getHeight() > 100) {
                expandButton.setText("Expand");
                Duration duration = Duration.millis(250);
                Timeline timeline = new Timeline(
                        new KeyFrame(duration,
                                new KeyValue(result.minHeightProperty(), 100, Interpolator.EASE_BOTH)));
                timeline.play();
            } else {
                expandButton.setText("Contract");
                Duration duration = Duration.millis(250);
                Timeline timeline = new Timeline(
                        new KeyFrame(duration,
                                new KeyValue(result.minHeightProperty(), 250, Interpolator.EASE_BOTH)));
                timeline.play();
            }
        });

        HBox topPart = new HBox(title, spring, expandButton);
        HBox.setHgrow(spring, Priority.ALWAYS);
        topPart.setAlignment(Pos.CENTER);

        result.getChildren().addAll(topPart, notification);
        VBox.setVgrow(notification, Priority.ALWAYS);
        return result;
    }
}
