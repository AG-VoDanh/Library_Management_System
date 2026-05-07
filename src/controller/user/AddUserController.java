package controller.user;

import controller.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.entity.User;
import model.enums.Address;
import model.enums.Gender;
import model.enums.Role;
import org.kordamp.ikonli.javafx.FontIcon;
import service.UserService;
import util.AppUtil;
import util.FormatterUtil;
import util.MapperUtil;

import java.util.ArrayList;
import java.util.List;

public class AddUserController {
    private final UserService userService = new UserService();

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Label notificationLabel;
    @FXML private FontIcon iconEye;
    @FXML private TextField fullNameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<Gender> genderComboBox;
    @FXML private ComboBox<Address> addressComboBox;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private ComboBox<Role> roleComboBox;

    private boolean isPasswordVisible = false;

    private MainController mainController;
    private User currentUser;
    private int tableRowCount;

    public void init(MainController mainController,User currentUser, int tableRowCount) {
        this.mainController = mainController;
        this.currentUser = currentUser;
        this.tableRowCount = tableRowCount;
    }

    public String getPassword() {
        return isPasswordVisible ? passwordTextField.getText() : passwordField.getText();
    }

    @FXML
    public void initialize() {
        passwordField.setTextFormatter(FormatterUtil.createPasswordFormatter());
        passwordTextField.setTextFormatter(FormatterUtil.createPasswordFormatter());

        genderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        addressComboBox.setItems(FXCollections.observableArrayList(Address.values()));
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        addressComboBox.setVisibleRowCount(5);
        genderComboBox.setPromptText("Chọn giới tính");
        addressComboBox.setPromptText("Chọn địa chỉ");
        roleComboBox.getSelectionModel().selectFirst();
        usernameField.setTextFormatter(FormatterUtil.createUsernameFormatter());
        ageField.setTextFormatter(FormatterUtil.createAgeFormatter());
        fullNameField.setTextFormatter(FormatterUtil.createNameLetterFormatter());
        phoneNumberField.setTextFormatter(FormatterUtil.createPhoneNumberFormatter());
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
    private void onSave() {
        List<String> fieldBlankList = new ArrayList<>();

        if (usernameField.getText().isBlank()) fieldBlankList.add("tên đăng nhập");
        if (getPassword().isBlank()) fieldBlankList.add("mật khẩu");

        if (!fieldBlankList.isEmpty()) {
            String message = String.join(", ", fieldBlankList) + " không được để trống";
            notificationLabel.setText(message);
            return;
        }
        if (usernameField.getText().length() < 8 ) {
            notificationLabel.setText( "Tên đăng nhập 8-16 ký tự");
            return;
        }

        if (!getPassword().matches("^(?=.*\\p{L})(?=.*\\d).{8,16}$")) {
            notificationLabel.setText("Mật khẩu 8–16 ký tự, phải có chữ và số");
            return;
        }

        if(!fullNameField.getText().isBlank() && fullNameField.getText().length() < 2){
            notificationLabel.setText( "Tên người dùng 2-50 ký tự");
            return;
        }

        if (!emailField.getText().matches("^[a-zA-Z0-9.]+@gmail.com$") && !emailField.getText().isBlank()) {
            notificationLabel.setText("Email phải đúng định dạng @gmail.com và chỉ chứa chữ , số và kí tự .");
            return;
        }
        if (phoneNumberField.getText().length() < 10 && !phoneNumberField.getText().isBlank()) {
            notificationLabel.setText("Số điện thoại phải đủ 10 số");
            return;
        }
        User user = new User(
                null,
                usernameField.getText(),
                getPassword(),
                MapperUtil.emptyToNull(fullNameField.getText()),
                MapperUtil.parseIntOrNull(ageField.getText()),
                genderComboBox.getValue(),
                addressComboBox.getValue(),
                MapperUtil.emptyToNull(emailField.getText()),
                MapperUtil.emptyToNull(phoneNumberField.getText()),
                roleComboBox.getValue(),1,null);
        String error = userService.validateAdd(user);
        if(error != null){
            notificationLabel.setText(error);
            return;
        }
        try {
            userService.addUser(currentUser,user);
            AppUtil.showInformation("Thành Công","Sửa thông tin tài khoản thành công");
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
            return;
        }
        usernameField.clear();
        passwordField.clear();
        passwordTextField.clear();
        fullNameField.clear();
        ageField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        addressComboBox.getSelectionModel().clearSelection();
        genderComboBox.setPromptText("Chọn giới tính");
        addressComboBox.setPromptText("Chọn địa chỉ");
        emailField.clear();
        phoneNumberField.clear();
        roleComboBox.getSelectionModel().selectFirst();
        notificationLabel.setText("");
    }
    @FXML
    private void onQuit() {
        mainController.onNavigateToUserManagement((int) Math.ceil((double) userService.countAllUsers()/tableRowCount));
    }
}
