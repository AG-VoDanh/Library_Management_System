package controller;

import dto.DashboardStatistics;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import service.StatisticsService;

public class StatisticsController {
    private final StatisticsService statisticsService = new StatisticsService();

    @FXML private Label monthlyBorrowedBookCountLabel;
    @FXML private Label monthlyBorrowCountLabel;
    @FXML private Label overdueBorrowCountLabel;
    @FXML private VBox topBooksBox;
    @FXML private VBox topMembersBox;

    @FXML
    private void initialize() {
        DashboardStatistics dashboardStatistics = statisticsService.getDashboardStatistics();

        monthlyBorrowedBookCountLabel.setText(String.valueOf(dashboardStatistics.monthlyBorrowedBooks()));
        monthlyBorrowCountLabel.setText(String.valueOf(dashboardStatistics.monthlyBorrows()));
        overdueBorrowCountLabel.setText(String.valueOf(dashboardStatistics.overdueBorrows()));

        topBooksBox.getChildren().clear();
        topMembersBox.getChildren().clear();

        for (String book : dashboardStatistics.topBooks()) {
            topBooksBox.getChildren().add(new Text(book));
        }

        for (String member : dashboardStatistics.topMembers()) {
            topMembersBox.getChildren().add(new Text(member));
        }
    }
}