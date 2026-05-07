package controller.bookborrow;

import controller.MainController;
import controller.PaginationController;
import dto.PageResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.entity.BookBorrow;
import model.entity.User;
import model.search.BorrowSearchCriteria;
import service.BorrowService;
import util.AppUtil;
import util.FormatterUtil;

import java.time.LocalDate;

public class BookBorrowManagementController {
    private final BorrowService bookBorrowService = new BorrowService();
    private final ObservableList<BookBorrow> paginatedBookBorrowList = FXCollections.observableArrayList();
    private final int tableRowCount = 18;

    @FXML private HBox searchTimeBox;
    @FXML private DatePicker fromDayDatePicker;
    @FXML private DatePicker toDayDatePicker;
    @FXML private TableView<BookBorrow> bookBorrowTableView;
    @FXML private TableColumn<BookBorrow, Void> ordinalNumberColumn;
    @FXML private TableColumn<BookBorrow, String> borrowerCodeColumn;
    @FXML private TableColumn<BookBorrow, String> borrowCodeColumn;
    @FXML private TableColumn<BookBorrow, String> borrowerNameColumn;
    @FXML private TableColumn<BookBorrow, LocalDate> borrowDateColumn;
    @FXML private TableColumn<BookBorrow, LocalDate> dueDateColumn;
    @FXML private TableColumn<BookBorrow, LocalDate> returnDateColumn;

    @FXML private TableColumn<BookBorrow,Integer> lateFeeColumn;
    @FXML private TableColumn<BookBorrow, String> statusColumn;

    @FXML private ComboBox<BorrowSearchCriteria> searchCriteriaComboBox;
    @FXML private ComboBox<Object> searchOptionComboBox;
    @FXML private TextField searchField;

    @FXML private Button borrowDetailButton;
    @FXML private Button returnBookButton;

    @FXML private PaginationController paginationController;

    private MainController mainController;
    private User currentUser;
    private Boolean isSearching;

    public void init(MainController mainController,User currentUser) {
        this.mainController = mainController;
        this.currentUser = currentUser;
    }

    public void init(MainController mainController,User currentUser,int page) {
        this.mainController = mainController;
        this.currentUser = currentUser;

        this.paginationController.setCurrentPage(page);
        loadPage();
    }

    private void createBookBorrowTableView() {
        bookBorrowTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        bookBorrowTableView.setFixedCellSize(35.0);
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
        borrowCodeColumn.setCellValueFactory(new PropertyValueFactory<>("borrowCode"));
        borrowerCodeColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerCode"));
        borrowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        lateFeeColumn.setCellValueFactory(new PropertyValueFactory<>("lateFee"));
        lateFeeColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Integer lateFee, boolean empty) {
                super.updateItem(lateFee, empty);
                if (empty) {
                    setText("");
                    return;
                }

                BookBorrow borrow = getTableView().getItems().get(getIndex());
                if (borrow != null && borrow.getReturnDate() == null && (lateFee == null || lateFee == 0)) {
                    setText("");
                } else {
                    setText(lateFee.toString());
                }
            }
        });
        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(bookBorrowService.getBookBorrowStaus(data.getValue())));

        bookBorrowTableView.setItems(paginatedBookBorrowList);
    }

    private void initSearchInputs() {
        searchCriteriaComboBox.setItems(FXCollections.observableArrayList(BorrowSearchCriteria.values()));
        searchCriteriaComboBox.getSelectionModel().selectFirst();

        searchField.setPromptText("Nhập mã lượt mượn là số VD : 1,2,...");
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
                case BOOK_CODE -> {
                    searchField.setPromptText("Nhập mã sách là số VD : 1,2,...");
                    searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
                }
                case BOOK_NAME -> {
                    searchField.setPromptText("Nhập tên sách ");
                    searchField.setTextFormatter(FormatterUtil.createBookNameFormatter());
                }
                case PHONE_NUMBER -> {
                    searchField.setPromptText("Nhập số (bắt đầu số 0 và 10 chữ số)");
                    searchField.setTextFormatter(FormatterUtil.createPhoneNumberFormatter());
                }
            }
        });
    }

    private void handleRowSelection() {
        bookBorrowTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, selectBookBorrow) -> {

                    boolean isSelected = selectBookBorrow != null;
                    borrowDetailButton.setDisable(!isSelected);
                    if (!isSelected) {
                        returnBookButton.setDisable(true);
                        return;
                    }
                    returnBookButton.setDisable(selectBookBorrow.getReturnDate() != null);
                });
    }

    private void loadTableData() {
        isSearching = false;
        paginationController.setCurrentPage(1);

        loadPage();
    }

    private void loadPage() {
        BorrowSearchCriteria criteria = isSearching ? searchCriteriaComboBox.getValue() : null;
        Object[] keyword = isSearching ? getKeyword(criteria) : null;

        PageResult<BookBorrow> pageResult = bookBorrowService.getBookBorrowsPage(criteria,
                paginationController.getCurrentPage(), tableRowCount,keyword);

        if (isSearching && pageResult.total() == 0) {
            AppUtil.showInformation("Thông báo",
                    "Không tìm thấy " + criteria.getDisplayName() + " là " + getKeywordDisplay());
            isSearching = false;
            return;
        }

        paginatedBookBorrowList.setAll(pageResult.data());
        paginationController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowCount));

        bookBorrowTableView.setPrefHeight((paginatedBookBorrowList.size() + 1) * 35.0);
    }

    private void updateSearchInputs() {
        BorrowSearchCriteria criteria = searchCriteriaComboBox.getValue();

        boolean isStatus = criteria == BorrowSearchCriteria.STATUS;
        boolean isDate = criteria == BorrowSearchCriteria.BORROW_DATE ||
                criteria == BorrowSearchCriteria.DUE_DATE || criteria == BorrowSearchCriteria.RETURN_DATE;

        if (isStatus) {
            searchOptionComboBox.setItems(FXCollections.observableArrayList("Đang mượn", "Trả trễ hạn", "Trả đúng hạn"));
            searchOptionComboBox.getSelectionModel().selectFirst();
        }

        if (isDate) {
            fromDayDatePicker.setValue(null);
            toDayDatePicker.setValue(null);
        }
        searchOptionComboBox.setVisible(isStatus);
        searchOptionComboBox.setManaged(isStatus);

        searchTimeBox.setVisible(isDate);
        searchTimeBox.setManaged(isDate);

        searchField.setVisible(!isStatus && !isDate);
        searchField.setManaged(!isStatus && !isDate);
    }

    private Object[] getKeyword(BorrowSearchCriteria criteria) {
        if (criteria == null) return null;

        if (criteria == BorrowSearchCriteria.STATUS) {
            return new Object[]{searchOptionComboBox.getValue()};
        }

        if (criteria == BorrowSearchCriteria.BORROW_DATE ||
                criteria == BorrowSearchCriteria.DUE_DATE ||
                criteria == BorrowSearchCriteria.RETURN_DATE) {

            if (fromDayDatePicker.getValue() == null || toDayDatePicker.getValue() == null) {
                return null;
            }

            return new Object[]{fromDayDatePicker.getValue(), toDayDatePicker.getValue()};
        }

        return new Object[]{searchField.getText().trim()};
    }

    private String getKeywordDisplay() {
        if (searchOptionComboBox.isVisible()) {
            return searchOptionComboBox.getValue().toString();
        }
        if(searchTimeBox.isVisible()){
            return "từ ngày :"+fromDayDatePicker.getValue().toString()+" đến ngày : "+toDayDatePicker.getValue().toString();
        }
        return searchField.getText().trim();
    }

    @FXML
    public void initialize() {
        createBookBorrowTableView();
        initSearchInputs();
        loadTableData();

        handleRowSelection();

        paginationController.setOnPageChange(this::loadPage);

        searchCriteriaComboBox.setOnAction(_ -> updateSearchInputs());
    }

    @FXML
    private void onSearch() {
        BorrowSearchCriteria criteria = searchCriteriaComboBox.getValue();

        if (criteria == BorrowSearchCriteria.BORROW_DATE || criteria == BorrowSearchCriteria.DUE_DATE ||
                criteria == BorrowSearchCriteria.RETURN_DATE) {
            if (fromDayDatePicker.getValue() == null || toDayDatePicker.getValue() == null) {
                AppUtil.showInformation("Cảnh báo", "Không được để trống ngày");
                return;
            }

            if (fromDayDatePicker.getValue().isAfter(toDayDatePicker.getValue())) {
                AppUtil.showInformation("Cảnh báo", "Ngày bắt đầu phải trước ngày kết thúc");
                return;
            }
        } else if(criteria != BorrowSearchCriteria.STATUS && searchField.getText().isBlank()){
            AppUtil.showInformation("Cảnh báo", "Nội dung tìm kiếm không được để trống");
            return;
        }

        isSearching = true;
        paginationController.setCurrentPage(1);
        loadPage();
    }

    @FXML
    private void onShowBorrowDetail() {
        mainController.onNavigateToBorrowDetail("bookBorrow",
                bookBorrowTableView.getSelectionModel().getSelectedItem(),paginationController.getCurrentPage());
    }

    @FXML
    private void borrowBook() {
        mainController.onNavigateToAddBookBorrowManagement(tableRowCount);
    }

    @FXML
    private void returnBook() {
        try {
            if(AppUtil.showConfirmation("Cảnh báo","Bạn có chắc chắc muốn trả sách")){
                bookBorrowService.returnBook(currentUser,bookBorrowTableView.getSelectionModel().getSelectedItem());
                onRefresh();
            }
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
        }
    }

    @FXML private void onRefresh () {
        isSearching = false;
        searchField.clear();
        loadTableData();
    }
}
