package controller.member;

import controller.MainController;
import controller.PaginationController;
import dto.PageResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entity.Member;
import model.entity.User;
import model.enums.Address;
import model.enums.Gender;
import model.enums.Role;
import model.search.MemberSearchCriteria;
import service.MemberService;
import util.AppUtil;
import util.FormatterUtil;

public class MemberManagementController {
    private final MemberService memberService = new MemberService();
    private final ObservableList<Member> paginatedMemberList = FXCollections.observableArrayList();
    private final int tableRowCount = 18;

    @FXML private TableView<Member> memberTableView;
    @FXML private TableColumn<Member, Void> ordinalNumberColumn;
    @FXML private TableColumn<Member, String> memberCodeColumn;
    @FXML private TableColumn<Member, String> memberNameColumn;
    @FXML private TableColumn<Member, Integer> ageColumn;
    @FXML private TableColumn<Member, String> genderColumn;
    @FXML private TableColumn<Member, String> addressColumn;
    @FXML private TableColumn<Member, String> phoneNumberColumn;
    @FXML private TableColumn<Member, String> statusColumn;

    @FXML private ComboBox<MemberSearchCriteria> searchCriteriaComboBox;
    @FXML private ComboBox<Object> searchOptionComboBox;
    @FXML private TextField searchField;

    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button restoreButton;
    @FXML private Button borrowHistoryButton;

    @FXML private PaginationController paginationController;

    private MainController mainController;
    private User currentUser;
    private Boolean isSearching;

    public void init(MainController mainController, User currentUser) {
        this.mainController = mainController;
        this.currentUser = currentUser;

    }

    public void init(MainController mainController, User currentUser,int page) {
        this.mainController = mainController;
        this.currentUser = currentUser;

        this.paginationController.setCurrentPage(page);
        loadPage();
    }

    private void createUserTableView() {
        memberTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        memberTableView.setFixedCellSize(35.0);
        ordinalNumberColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                } else {
                    int stt = (paginationController.getCurrentPage() - 1) * tableRowCount + getIndex() + 1;
                    setText(String.valueOf(stt));
                }
            }
        });
        ordinalNumberColumn.setSortable(false);
        memberCodeColumn.setCellValueFactory(new PropertyValueFactory<>("memberCode"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Integer age, boolean empty) {
                super.updateItem(age, empty);
                setText(empty || age == 0 ? "" : age.toString());
            }
        });
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        statusColumn.setCellValueFactory(data -> {
            if (data.getValue().getStatus() == 1) {
                return new SimpleStringProperty("Hoạt động");
            }else {
                return new SimpleStringProperty("Bị xóa");
            }
        });
        memberTableView.setItems(paginatedMemberList);
    }

    private void initSearchInputs() {
        searchCriteriaComboBox.setItems(FXCollections.observableArrayList(MemberSearchCriteria.values()));
        searchCriteriaComboBox.getSelectionModel().selectFirst();

        searchField.setPromptText("Nhập mã hội viên là số VD : 1,2,...");
        searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
        searchCriteriaComboBox.valueProperty().addListener((_, _, newVal) -> {
            if (newVal == null) return;

            searchField.setTextFormatter(null);
            isSearching = false;
            searchField.clear();

            switch (newVal){
                case MEMBER_CODE -> {
                    searchField.setPromptText("Nhập mã hội viên là số VD : 1,2,...");
                    searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
                }
                case MEMBER_NAME -> {
                    searchField.setPromptText("Nhập tên hội viên (chỉ chữ) ");
                    searchField.setTextFormatter(FormatterUtil.createLetterFormatter());
                }
                case AGE -> {
                    searchField.setPromptText("Nhập số (1 < tuổi < 121)");
                    searchField.setTextFormatter(FormatterUtil.createAgeFormatter());
                }
                case PHONE_NUMBER -> {
                    searchField.setPromptText("Nhập số (bắt đầu số 0 và 10 chữ số)");
                    searchField.setTextFormatter(FormatterUtil.createPhoneNumberFormatter());
                }
            }
        });
    }

    private void handleRowSelection() {
        memberTableView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, selectedMember) -> {
                    boolean isSelected = selectedMember != null;
                    boolean isDeleted = isSelected && selectedMember.getStatus() == 0;

                    updateButton.setDisable(!isSelected);
                    if(isSelected){
                        borrowHistoryButton.setDisable(memberService.countBookBorrowsByMemberId(selectedMember.getId()) <= 0);
                    }
                    if(currentUser.getRole() == Role.QUAN_TRI){
                        deleteButton.setDisable(isDeleted || !isSelected);
                    }
                    restoreButton.setDisable(!isSelected || !isDeleted);
                }
        );
    }

    private void loadTableData() {
        isSearching = false;
        paginationController.setCurrentPage(1);
        loadPage();
    }

    private void loadPage() {
        MemberSearchCriteria criteria = isSearching ? searchCriteriaComboBox.getValue() : null;
        String keyword = isSearching ? getKeyword(criteria) : null;

        PageResult<Member> pageResult = memberService.getMembersPage(criteria, keyword,
                paginationController.getCurrentPage(), tableRowCount);

        if (isSearching && pageResult.total() == 0) {
            AppUtil.showInformation("Thông báo",
                    "Không tìm thấy " + criteria.getDisplayName() + " là " + getKeywordDisplay());
            isSearching = false;
            return;
        }

        paginatedMemberList.setAll(pageResult.data());
        paginationController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowCount));

        memberTableView.setPrefHeight((paginatedMemberList.size() + 1) * 35.0);
    }

    private void updateSearchInputs(){
        boolean statusSearch = false;
        switch (searchCriteriaComboBox.getValue()) {
            case ADDRESS -> {
                statusSearch = true;
                searchOptionComboBox.setItems(FXCollections.observableArrayList(Address.values()));
            }
            case GENDER ->  {
                statusSearch = true;
                searchOptionComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
            }
            case STATUS -> {
                statusSearch = true;
                searchOptionComboBox.setItems(FXCollections.observableArrayList("Hoạt động","Bị xóa"));
            }
        }

        if(statusSearch) {
            searchOptionComboBox.getSelectionModel().selectFirst();
        }
        searchOptionComboBox.setVisible(statusSearch);
        searchOptionComboBox.setManaged(statusSearch);

        searchField.setVisible(!statusSearch);
        searchField.setManaged(!statusSearch);
    }

    private String getKeyword(MemberSearchCriteria criteria) {
        Object value = searchOptionComboBox.getValue();

        if (value == null) return searchField.getText().trim();

        return switch (criteria) {
            case ADDRESS -> ((Address) value).name();
            case GENDER -> ((Gender) value).name();
            case STATUS -> value.equals("Hoạt động") ? "1" : "0";
            default -> searchField.getText().trim();
        };
    }

    private String getKeywordDisplay() {
        if (searchOptionComboBox.isVisible()) {
            return searchOptionComboBox.getValue().toString();
        }
        return searchField.getText().trim();
    }

    @FXML
    public void initialize() {
        createUserTableView();
        initSearchInputs();
        loadTableData();

        handleRowSelection();

        paginationController.setOnPageChange(this::loadPage);
        searchCriteriaComboBox.setOnAction(_ -> updateSearchInputs());
    }

    @FXML
    private void onSearch() {
        MemberSearchCriteria criteria = searchCriteriaComboBox.getValue();
        String keyword = getKeyword(criteria);

        if (keyword.isBlank()) {
            AppUtil.showInformation("Cảnh báo", "Nội dung tìm kiếm không để trống");
            return;
        }

        isSearching = true;
        paginationController.setCurrentPage(1);
        loadPage();
    }

    @FXML
    private void onAddMember() {
        mainController.onNavigateToAddMember(tableRowCount);

    }

    @FXML
    private void onUpdateMember() {
        mainController.onNavigateToUpdateMember(memberTableView.getSelectionModel().getSelectedItem(),paginationController.getCurrentPage());
    }

    @FXML
    private void onDeleteMember() {
        try {
            if(AppUtil.showConfirmation("Xóa hội viên","Bạn có chắc muốn xóa hội viên này?")){
                String error = memberService.deleteMember(currentUser,memberTableView.getSelectionModel().getSelectedItem().getId());
                if(error != null){
                    AppUtil.showInformation("Cảnh báo",error);
                }else {
                    loadPage();
                }
            }
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
        }
    }
    @FXML
    private void onRestoreMember(){
        try {
            if(AppUtil.showConfirmation("Khôi phục hội viên","Bạn có chắc muốn khôi phục hội viên này không?")){
                memberService.restoreMember(currentUser,memberTableView.getSelectionModel().getSelectedItem().getId());
                loadPage();
            }
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
        }
    }

    @FXML
    private void onShowBorrowHistory(){
        mainController.onNavigateToMemberBorrowHistory(memberTableView.getSelectionModel().getSelectedItem().getId(),paginationController.getCurrentPage());
    }

    @FXML private void onRefresh() {
        isSearching = false;
        searchField.clear();
        loadTableData();
    }
}
