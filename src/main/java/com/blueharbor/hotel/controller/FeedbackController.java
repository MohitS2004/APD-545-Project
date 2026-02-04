package com.blueharbor.hotel.controller;

import com.blueharbor.hotel.app.ViewKey;
import com.blueharbor.hotel.app.ViewNavigator;
import com.blueharbor.hotel.dto.FeedbackSubmission;
import com.blueharbor.hotel.service.FeedbackService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class FeedbackController {

    @FXML private TextField confirmationField;
    @FXML private Slider ratingSlider;
    @FXML private TextArea commentArea;
    @FXML private Label feedbackStatusLabel;

    private final FeedbackService feedbackService;
    private final ViewNavigator navigator;

    public FeedbackController(FeedbackService feedbackService, ViewNavigator navigator) {
        this.feedbackService = feedbackService;
        this.navigator = navigator;
    }

    @FXML
    public void submitFeedback() {
        try {
            FeedbackSubmission submission = new FeedbackSubmission(
                confirmationField.getText(),
                (int) ratingSlider.getValue(),
                commentArea.getText()
            );
            feedbackService.submit(submission);
            feedbackStatusLabel.setText("Thanks for the feedback!");
        } catch (Exception ex) {
            feedbackStatusLabel.setText(ex.getMessage());
        }
    }

    @FXML
    public void backToWelcome() {
        navigator.goTo(ViewKey.WELCOME);
    }
}
