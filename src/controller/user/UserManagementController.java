package controller.user;

import controller.MainController;
import controller.PaginationController;
import dto.PageResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entity.User;
import model.enums.Address;
import model.enums.Gender;
import model.enums.Role;
import model.search.UserSearchCriteria;
import service.UserService;
import util.AppUtil;
import util.FormatterUtil;

public class UserManagementController {
    private final UserService userService = new UserService();
    private final ObservableList<User> paginatedUserList = FXCollections.observableArrayList();
    private final int tableRowCount = 18;

    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, Void> ordinalNumberColumn;
    @FXML private TableColumn<User, String> userCodeColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, Integer> ageColumn;
    @FXML private TableColumn<User, String> genderColumn;
    @FXML private TableColumn<User, String> addressColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneNumberColumn;
    @FXML private TableColumn<User,String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;

    @FXML private ComboBox<UserSearchCriteria> searchCriteriaComboBox;
    @FXML private ComboBox<Object> searchOptionComboBox;
    @FXML private TextField searchField;

    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button restoreButton;

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
        userTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        userTableView.setFixedCellSize(35.0);
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
        userCodeColumn.setCellValueFactory(new PropertyValueFactory<>("userCode"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
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
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(data -> {
            if (data.getValue().getStatus() == 1) {
                return new SimpleStringProperty("Hoạt động");
            }else {
                return new SimpleStringProperty("Bị xóa");
            }
        });

        userTableView.setItems(paginatedUserList);
    }

    private void initSearchInputs() {
        searchCriteriaComboBox.setItems(FXCollections.observableArrayList(UserSearchCriteria.values()));
        searchCriteriaComboBox.getSelectionModel().selectFirst();
        searchField.setPromptText("Nhập mã tài khoản là số VD : 1,2,...");
        searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
        searchCriteriaComboBox.valueProperty().addListener((_, _, newVal) -> {
            if (newVal == null) return;

            searchField.setTextFormatter(null);
            isSearching = false;
            searchField.clear();
            switch (newVal){
                case USER_CODE -> {
                    searchField.setPromptText("Nhập mã tài khoản là số VD : 1,2,...");
                    searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
                }
                case USERNAME -> {
                    searchField.setPromptText("Nhập tên đăng nhập (Tối đa 16 kí tự) ");
                    searchField.setTextFormatter(FormatterUtil.createUsernameFormatter());
                }
                case FULL_NAME -> {
                    searchField.setPromptText("Nhập chữ (Tối đa 50 kí tự) ");
                    searchField.setTextFormatter(FormatterUtil.createNameLetterFormatter());
                }
                case AGE -> {
                    searchField.setPromptText("Nhập số (1 < tuổi < 121)");
                    searchField.setTextFormatter(FormatterUtil.createAgeFormatter());
                }
                case EMAIL -> searchField.setPromptText("Nhập example@gmail.com");
                case PHONE_NUMBER -> {
                    searchField.setPromptText("Nhập số (bắt đầu số 0 và 10 chữ số)");
                    searchField.setTextFormatter(FormatterUtil.createPhoneNumberFormatter());
                }
            }
        });
    }

    private void handleRowSelection() {
        userTableView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, selectedUser) -> {
                    boolean isSelected = selectedUser != null;
                    boolean isAdmin = isSelected && selectedUser.getId() == 1;
                    boolean isDeleted = isSelected && selectedUser.getStatus() == 0;

                    updateButton.setDisable(!isSelected  || isAdmin );
                    deleteButton.setDisable(isDeleted || !isSelected || isAdmin);
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
        UserSearchCriteria criteria = isSearching ? searchCriteriaComboBox.getValue() : null;
        String keyword = isSearching ? getKeyword(criteria) : null;

        PageResult<User> pageResult = userService.getUsersPage(criteria, keyword,
                paginationController.getCurrentPage(), tableRowCount);

        if (isSearching && pageResult.total() == 0) {
            AppUtil.showInformation("Thông báo",
                    "Không tìm thấy " + criteria.getDisplayName() + " là " + getKeywordDisplay());
            isSearching = false;
            return;
        }

        paginatedUserList.setAll(pageResult.data());
        paginationController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowCount));

        userTableView.setPrefHeight((paginatedUserList.size() + 1) * 35.0);
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
            case ROLE -> {
                statusSearch = true;
                searchOptionComboBox.setItems(FXCollections.observableArrayList(Role.values()));
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

    private String getKeyword(UserSearchCriteria criteria) {
        Object value = searchOptionComboBox.getValue();

        if (value == null) return searchField.getText().trim();

        return switch (criteria) {
            case ADDRESS -> ((Address) value).name();
            case GENDER -> ((Gender) value).name();
            case STATUS -> value.equals("Hoạt động") ? "1" : "0";
            case ROLE -> ((Role) value).name();
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
        handleRowSelection();

        loadTableData();

        paginationController.setOnPageChange(this::loadPage);
        searchCriteriaComboBox.setOnAction(_ -> updateSearchInputs());
    }

    @FXML
    private void onSearch() {
        UserSearchCriteria criteria = searchCriteriaComboBox.getValue();
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
    private void onAddUser() {
        mainController.onNavigateToAddUser(tableRowCount);
    }

    @FXML
    private void onUpdateUser() {
        mainController.onNavigateToUpdateUser(userTableView.getSelectionModel().getSelectedItem(),paginationController.getCurrentPage());
    }

    @FXML
    private void onDeleteUser() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;

        boolean isSelf = selectedUser.getId() == currentUser.getId();

        if (!AppUtil.showConfirmation("Xóa tài khoản", isSelf ?
                "Bạn có chắc muốn xóa tài khoản của mình không? (bạn sẽ bị đăng xuất)"
                : "Bạn có chắc muốn xóa tài khoản này?")) {
            return;
        }
        try {
            userService.deleteUser(currentUser,selectedUser.getId());
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
            return;
        }
        if (isSelf) {
            mainController.onLogout();
        } else {
            loadPage();
        }
    }

    @FXML
    private void onRestoreUser(){
        try {
            if(AppUtil.showConfirmation("Khôi phục tài khoản","Bạn có chắc muốn khôi phục tài khoàn này không?")){
                userService.restoreUser(currentUser,userTableView.getSelectionModel().getSelectedItem().getId());
                loadPage();}
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
        }
    }
    @FXML private void onRefresh() {
        isSearching = false;
        searchField.clear();
        loadTableData();
    }
}
