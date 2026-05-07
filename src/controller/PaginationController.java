package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class PaginationController {
    @FXML private TextField pageField;
    @FXML private Label totalPagesLabel;

    private int currentPage = 1;
    private int totalPages = 1;

    private Runnable onPageChange;

    public void setTotalPages(int totalPages){
        if (totalPages == 0) {
            totalPages = 1;
        }
        this.totalPages = totalPages;
        updatePageText();
    }

    @FXML
    public void initialize() {
        pageField.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (!text.matches("\\d*")) return null;
            if (text.length() > 4) return null;

            return change;
        }));

        pageField.setOnAction(_ -> goToPage());

        pageField.focusedProperty().addListener((_, _, newV) -> {
            if (!newV) goToPage();
        });

    }

    private void goToPage() {
        if (pageField.getText().isBlank()) {
            pageField.setText(String.valueOf(currentPage));
            return;
        }

        int inputPage = Integer.parseInt(pageField.getText());

        if (inputPage < 1 || inputPage > totalPages) {
            pageField.setText(String.valueOf(currentPage));
            return;
        }

        currentPage = inputPage;
        changeNotification();
    }

    public void setOnPageChange(Runnable onPageChange){
        this.onPageChange = onPageChange;
    }

    public int getCurrentPage(){
        return currentPage;
    }

    public void setCurrentPage(int currentPage){
        this.currentPage = currentPage;
    }

    private void updatePageText(){
        pageField.setText(String.valueOf(currentPage));
        totalPagesLabel.setText("/ " + totalPages);
    }

    @FXML
    private void onFirstPage(){
        currentPage = 1;
        changeNotification();
    }

    @FXML
    private void onPreviousPage(){
        if(currentPage > 1){
            currentPage--;
            changeNotification();
        }
    }

    @FXML
    private void onNextPage(){
        if(currentPage < totalPages){
            currentPage++;
            changeNotification();
        }
    }

    @FXML
    private void onLastPage(){
        currentPage = totalPages;
        changeNotification();
    }

    private void changeNotification(){
        updatePageText();
        if(onPageChange != null){
            onPageChange.run();
        }
    }
}
