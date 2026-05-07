package controller.member;

import controller.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entity.Member;
import model.entity.User;
import model.enums.Address;
import model.enums.Gender;
import service.MemberService;
import util.AppUtil;
import util.FormatterUtil;
import util.MapperUtil;

import java.util.ArrayList;
import java.util.List;

public class UpdateMemberController {
    private final MemberService memberService = new MemberService();

    @FXML private TextField memberNameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<Gender> genderComboBox;
    @FXML private ComboBox<Address> addressComboBox;
    @FXML private TextField phoneNumberField;

    @FXML private Label notificationLabel;

    private MainController mainController;
    private User currentUser;
    private Member updateMember;
    private int currentPage;

    public void init(MainController mainController,User currentUser,Member updateMember,int currentPage) {
        this.mainController = mainController;
        this.currentUser = currentUser;
        this.updateMember = updateMember;
        this.currentPage = currentPage;

        setData();
    }

    public void setData(){
        memberNameField.setText(updateMember.getMemberName());
        ageField.setText(String.valueOf(updateMember.getAge()));
        genderComboBox.getSelectionModel().select(updateMember.getGender());
        addressComboBox.getSelectionModel().select(updateMember.getAddress());
        phoneNumberField.setText(updateMember.getPhoneNumber());
    }

    @FXML
    public void initialize() {
        genderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        addressComboBox.setItems(FXCollections.observableArrayList(Address.values()));
        addressComboBox.setVisibleRowCount(5);
        genderComboBox.setPromptText("Chọn giới tính");
        addressComboBox.setPromptText("Chọn địa chỉ");

        memberNameField.setTextFormatter(FormatterUtil.createNameLetterFormatter());
        ageField.setTextFormatter(FormatterUtil.createAgeFormatter());
        phoneNumberField.setTextFormatter(FormatterUtil.createPhoneNumberFormatter());
    }

    @FXML
    private void onSave() {
        List<String> fieldBlankList = new ArrayList<>();

        if (memberNameField.getText().isBlank()) fieldBlankList.add("tên sách");
        if (phoneNumberField.getText().isBlank()) fieldBlankList.add("số điện thoại");

        if (!fieldBlankList.isEmpty()) {
            String message = String.join(", ", fieldBlankList) + " không được để trống";
            notificationLabel.setText(message);
            return;
        }

        if(!memberNameField.getText().isBlank() && memberNameField.getText().length() < 2){
            notificationLabel.setText( "Tên hội viên 2-50 ký tự");
            return;
        }

        if (MapperUtil.emptyToNull(phoneNumberField.getText()) != null && phoneNumberField.getText().length() < 10 ) {
            notificationLabel.setText("Số điện thoại phải đủ 10 số");
            return;
        }

        Member existingMember = this.updateMember;
        Member updateMember = new Member(this.updateMember.getId(),this.updateMember.getMemberCode(),
                memberNameField.getText(),
                MapperUtil.parseIntOrNull(ageField.getText()),
                genderComboBox.getValue(),
                addressComboBox.getValue(),
                MapperUtil.emptyToNull(phoneNumberField.getText()),
                1);
        try {
            String error = memberService.updateMember(currentUser,updateMember,existingMember);
            if(error != null){
                notificationLabel.setText(error);
                return;
            }
            this.updateMember = updateMember;
            notificationLabel.setText("");
            AppUtil.showInformation("Thành Công","Sửa thông tin hội viên thành công");
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
        }
    }
    @FXML
    private void onQuit() {
        mainController.onNavigateToMemberManagement(currentPage);
    }
    @FXML
    private void onReset(){
        setData();
    }
}
