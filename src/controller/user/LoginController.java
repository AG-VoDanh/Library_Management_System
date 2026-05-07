package controller.user;

import controller.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.enums.Role;
import model.entity.User;
import org.kordamp.ikonli.javafx.FontIcon;
import service.UserService;
import util.FormatterUtil;

public class LoginController {
    private final UserService userService = new UserService();

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Label notificationLabel;
    @FXML private FontIcon iconEye;

    private boolean isPasswordVisible = false;

    public String getPassword() {
        return isPasswordVisible ? passwordTextField.getText() : passwordField.getText();
    }

    @FXML
    private void initialize(){
        usernameField.setTextFormatter(FormatterUtil.createUsernameFormatter());
        passwordField.setTextFormatter(FormatterUtil.createPasswordFormatter());
        passwordTextField.setTextFormatter(FormatterUtil.createPasswordFormatter());
    }

    @FXML
    private void onTogglePasswordVisibility(){
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            passwordTextField.setText(passwordField.getText());
        } else {
            passwordField.setText(passwordTextField.getText());
        }

        passwordTextField.setVisible(isPasswordVisible);
        passwordTextField.setManaged(isPasswordVisible);

        passwordField.setVisible(!isPasswordVisible);
        passwordField.setManaged(!isPasswordVisible);

        iconEye.setIconLiteral(isPasswordVisible ? "fas-eye" : "fas-lock");
    }

    @FXML
    private void onLogin(){
        notificationLabel.setText("");
        String username = usernameField.getText().trim();
        String password = getPassword();

        if (username.isBlank() || password.isBlank()) {
            notificationLabel.setText("Tài khoản và mật khẩu không để trống");
            return;
        }
        if(username.length() < 8){
            notificationLabel.setText("Tài khoản phải từ 8 đến 16 ký tự");
            return;
        }
        if(password.length() < 8){
            notificationLabel.setText("Mật khẩu phải từ 8 đến 16 ký tự");
            return;
        }

        User currentUser = userService.login(username,password);

        if (currentUser == null) {
            notificationLabel.setText("Sai tên đăng nhập hoặc mật khẩu");
            return;
        }

        navigateToMainView(currentUser);
    }

    private void navigateToMainView(User currentUser){
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Scene scene = new Scene(loader.load());

            MainController mainController = loader.getController();
            mainController.setUserLogin(currentUser);
            mainController.startSessionTracker();
            if (Role.QUAN_TRI.equals(currentUser.getRole())){
                mainController.onNavigateToUserManagement();
            }else{
                mainController.onNavigateToUser();
            }
            stage.setScene(scene);
            stage.setTitle("Quản Lý Thư Viện");
        } catch (Exception _) {

        }
    }
}
