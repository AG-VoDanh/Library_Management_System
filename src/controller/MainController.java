package controller;

import controller.book.BookManagementController;
import controller.book.UpdateBookController;
import controller.bookborrow.AddBookBorrowController;
import controller.bookborrow.BookBorrowManagementController;
import controller.bookborrow.BorrowDetailController;
import controller.bookimport.AddBookImportController;
import controller.bookimport.BookImportManagementController;
import controller.bookimport.ImportDetailController;
import controller.member.AddMemberController;
import controller.member.MemberBorrowHistoryController;
import controller.member.MemberManagementController;
import controller.member.UpdateMemberController;
import controller.user.AddUserController;
import controller.user.UpdateUserController;
import controller.user.UserController;
import controller.user.UserManagementController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.entity.*;
import model.enums.Role;
import service.UserService;
import util.AppUtil;

import java.util.concurrent.CompletableFuture;

public class MainController {
    private final UserService userService = new UserService();
    @FXML private StackPane mainContent;
    @FXML private Button userManagementButton;
    @FXML private Button userButton;

    private Timeline sessionTracker;
    private User currentUser;

    public void startSessionTracker() {
        sessionTracker = new Timeline(new KeyFrame(Duration.seconds(30), _ -> CompletableFuture.runAsync(() -> {
            try {
                userService.checkUserLogin(currentUser);
            } catch (SecurityException se) {
                Platform.runLater(() -> {
                    sessionTracker.stop();
                    AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
                    onLogout();
                });

            } catch (Exception _) {

            }
        })));

        sessionTracker.setCycleCount(Timeline.INDEFINITE);
        sessionTracker.play();
    }

    private <T> T loadView(String fxmlView) {
        mainContent.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/"+ fxmlView));
            Parent view = loader.load();
            mainContent.getChildren().setAll(view);
            return loader.getController();
        } catch (Exception _) {
            return null;
        }
    }

    public void setUserLogin(User currentUser){
        this.currentUser = currentUser;

        boolean isQuanTri = Role.QUAN_TRI.equals(currentUser.getRole());

        userManagementButton.setVisible(isQuanTri);
        userManagementButton.setManaged(isQuanTri);

        userButton.setVisible(!isQuanTri);
        userButton.setManaged(!isQuanTri);
    }

    @FXML
    public void onNavigateToUserManagement() {
        UserManagementController userManagementController = loadView("user/UserManagementView.fxml");
        if (userManagementController != null) {
            userManagementController.init(this,currentUser);
        }
    }

    public void onNavigateToUserManagement(int page) {
        UserManagementController userManagementController = loadView("user/UserManagementView.fxml");
        if (userManagementController != null) {
            userManagementController.init(this,currentUser,page);
        }
    }

    public void onNavigateToAddUser(int tableRowCount) {
        AddUserController addUserController = loadView("user/AddUserView.fxml");
        if (addUserController != null) {
            addUserController.init(this,currentUser,tableRowCount);
        }
    }

    public void onNavigateToUpdateUser(User updateUser,int currentPage) {
        UpdateUserController updateUserController = loadView("user/UpdateUserView.fxml");
        if (updateUserController != null) {
            updateUserController.init(this,currentUser,updateUser,currentPage);
        }
    }

    @FXML
    public void onNavigateToUser() {
        UserController userController = loadView("user/UserView.fxml");
        if (userController != null) {
            userController.init(this,currentUser);
        }
    }

    @FXML
    public void onNavigateToBookManagement() {
        BookManagementController bookManagementController = loadView("book/BookManagementView.fxml");
        if (bookManagementController != null) {
            bookManagementController.init(this);
        }
    }

    public void onNavigateToBookManagement(int page) {
        BookManagementController bookManagementController = loadView("book/BookManagementView.fxml");
        if (bookManagementController != null) {
            bookManagementController.init(this,page);
        }
    }

    public void onNavigateToUpdateBook(Book updateBook, int currentPage) {
        UpdateBookController updateBookController = loadView("book/UpdateBookView.fxml");
        if (updateBookController != null) {
            updateBookController.init(this,currentUser,updateBook,currentPage);
        }
    }

    @FXML
    public void onNavigateToMemberManagement() {
        MemberManagementController memberManagementController = loadView("member/MemberManagementView.fxml");
        if (memberManagementController != null) {
            memberManagementController.init(this,currentUser);
        }
    }

    public void onNavigateToMemberManagement(int page) {
        MemberManagementController memberManagementController = loadView("member/MemberManagementView.fxml");
        if (memberManagementController != null) {
            memberManagementController.init(this,currentUser,page);
        }
    }

    public void onNavigateToAddMember(int tableRowCount) {
        AddMemberController addMemberController = loadView("member/AddMemberView.fxml");
        if (addMemberController != null) {
            addMemberController.init(this,currentUser,tableRowCount);
        }
    }

    public void onNavigateToUpdateMember(Member updateMember, int currentPage) {
        UpdateMemberController updateMemberController = loadView("member/UpdateMemberView.fxml");
        if (updateMemberController != null) {
            updateMemberController.init(this,currentUser,updateMember,currentPage);
        }
    }

    public void onNavigateToMemberBorrowHistory(int memberId, int currentPage) {
        MemberBorrowHistoryController memberBorrowHistoryController = loadView("member/MemberBorrowHistoryView.fxml");
        if (memberBorrowHistoryController != null){
            memberBorrowHistoryController.init(this,memberId,currentPage);
        }
    }

    @FXML
    public void onNavigateToBookBorrowManagement() {
        BookBorrowManagementController bookBorrowManagementController = loadView("bookborrow/BookBorrowManagementView.fxml");
        if (bookBorrowManagementController != null){
            bookBorrowManagementController.init(this,currentUser);
        }
    }

    public void onNavigateToBookBorrowManagement(int page) {
        BookBorrowManagementController bookBorrowManagementController = loadView("bookborrow/BookBorrowManagementView.fxml");
        if (bookBorrowManagementController != null){
            bookBorrowManagementController.init(this,currentUser,page);
        }
    }

    public void onNavigateToBorrowDetail(String namePage, BookBorrow bookBorrow, int currentPage) {
        BorrowDetailController borrowDetailController = loadView("bookborrow/BorrowDetailView.fxml");
        if (borrowDetailController != null){
            borrowDetailController.init(this,namePage,bookBorrow,currentPage);
        }
    }

    public void onNavigateToAddBookBorrowManagement(int tableRowCount) {
        AddBookBorrowController addBookBorrowController = loadView("bookborrow/AddBookBorrowView.fxml");
        if (addBookBorrowController != null){
            addBookBorrowController.init(this,currentUser,tableRowCount);
        }
    }

    @FXML
    public void onNavigateToBookImportManagement() {
        BookImportManagementController bookImportManagementController = loadView("bookimport/BookImportManagementView.fxml");
        if (bookImportManagementController != null){
            bookImportManagementController.init(this);
        }
    }

    public void onNavigateToBookImportManagement(int page) {
        BookImportManagementController bookImportManagementController = loadView("bookimport/BookImportManagementView.fxml");
        if (bookImportManagementController != null){
            bookImportManagementController.init(this,page);
        }
    }

    public void onNavigateToImportDetail(BookImport bookImport, int page) {
        ImportDetailController importDetailController = loadView("bookimport/ImportDetailView.fxml");
        if (importDetailController != null){
            importDetailController.init(this,bookImport,page);
        }
    }

    public void onNavigateToAddBookImportManagement(int tableRowCount) {
        AddBookImportController addBookImportController = loadView("bookimport/AddBookImportView.fxml");
        if (addBookImportController != null){
            addBookImportController.init(this,currentUser,tableRowCount);
        }
    }

    @FXML
    public void onOpenStatistics() {
         loadView("StatisticsView.fxml");
    }

    @FXML
    public void onLogout() {
        if (sessionTracker != null) {
            sessionTracker.stop();
        }
        currentUser = null;
        try {
            Stage stage = (Stage) mainContent.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Scene scene = new Scene(new FXMLLoader(
                    getClass().getResource("/view/user/LoginView.fxml")).load());
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setScene(scene);

        } catch (Exception _) {
        }
    }
}
