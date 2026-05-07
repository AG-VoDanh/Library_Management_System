package controller.user;

import controller.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.enums.Address;
import model.enums.Gender;
import model.enums.Role;
import model.entity.User;
import org.kordamp.ikonli.javafx.FontIcon;
import service.UserService;
import util.AppUtil;
import util.FormatterUtil;
import util.MapperUtil;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserController {
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
    private User updateUser;
    private int currentPage;

    public void init(MainController mainController, User currentUser, User updateUser,int currentPage) {
        this.mainController = mainController;
        this.currentUser = currentUser;
        this.updateUser = updateUser;
        this.currentPage = currentPage;
        setData();
    }

    public String getPassword() {
        return isPasswordVisible ? passwordTextField.getText() : passwordField.getText();
    }

    public void setData(){
        usernameField.setText(updateUser.getUsername());
        passwordField.setText(updateUser.getPassword());
        passwordTextField.setText(updateUser.getPassword());
        fullNameField.setText(updateUser.getFullName());
        ageField.setText(String.valueOf(updateUser.getAge()));
        genderComboBox.getSelectionModel().select(updateUser.getGender());
        addressComboBox.getSelectionModel().select(updateUser.getAddress());
        emailField.setText(updateUser.getEmail());
        phoneNumberField.setText(updateUser.getPhoneNumber());
        roleComboBox.getSelectionModel().select(updateUser.getRole());
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

        if(MapperUtil.emptyToNull(fullNameField.getText()) !=null && fullNameField.getText().length() < 2){
            notificationLabel.setText( "Tên người dùng 2-50 ký tự");
            return;
        }

        if (MapperUtil.emptyToNull(emailField.getText()) != null &&
                !emailField.getText().matches("^[a-zA-Z0-9.]+@gmail.com$")) {
            notificationLabel.setText("Email phải đúng định dạng @gmail.com và chỉ chứa chữ , số và kí tự .");
            return;
        }

        if (MapperUtil.emptyToNull(phoneNumberField.getText()) != null && phoneNumberField.getText().length() < 10) {
            notificationLabel.setText("Số điện thoại phải đủ 10 số");
            return;
        }

        User existingUser = this.updateUser;
        User updateUser = new User(this.updateUser.getId(), this.updateUser.getUserCode(), usernameField.getText(),
                getPassword(),
                MapperUtil.emptyToNull(fullNameField.getText()),
                MapperUtil.parseIntOrNull(ageField.getText()),
                genderComboBox.getValue(),
                addressComboBox.getValue(),
                MapperUtil.emptyToNull(emailField.getText()),
                MapperUtil.emptyToNull(phoneNumberField.getText()),
                roleComboBox.getValue(),1,this.updateUser.getSessionToken());
        String error = userService.validateUpdate(updateUser,existingUser);
        if(error != null){
            notificationLabel.setText(error);
            return;
        }
        boolean isRoleDowngraded = userService.hasRoleDowngraded(updateUser,existingUser,currentUser);
        if(isRoleDowngraded && !AppUtil.showConfirmation("Sửa tài khoản",
                "Bạn có chắc muốn sửa quyền tài khoản mình không ?(nếu có bạn sẽ bị đăng xuất)")
        ){
            return;
        }
        try {
            userService.updateUser(this.currentUser,updateUser);
            AppUtil.showInformation("Thành Công","Sửa thông tin tài khoản thành công");
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
            return;
        }
        if (isRoleDowngraded){
            mainController.onLogout();
            return;
        }
        this.updateUser = updateUser;
        notificationLabel.setText("");

    }
    @FXML
    private void onQuit() {
        mainController.onNavigateToUserManagement(currentPage);
    }

    @FXML
    private void onReset(){
        setData();
    }

}
