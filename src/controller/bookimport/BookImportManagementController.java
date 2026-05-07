package controller.bookimport;

import controller.MainController;
import controller.PaginationController;
import dto.PageResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.entity.BookImport;
import model.search.ImportSearchCriteria;
import service.ImportService;
import util.AppUtil;
import util.FormatterUtil;

import java.time.LocalDate;

public class BookImportManagementController {
    private final ImportService bookImportService = new ImportService();
    private final ObservableList<BookImport> paginatedBookImportList = FXCollections.observableArrayList();
    private final int tableRowCount = 18;

    @FXML private HBox searchTimeBox;
    @FXML private DatePicker fromDayDatePicker;
    @FXML private DatePicker toDayDatePicker;
    @FXML private TableView<BookImport> bookImportTableView;
    @FXML private TableColumn<BookImport, Void> ordinalNumberColumn;
    @FXML private TableColumn<BookImport, String> importCodeColumn;
    @FXML private TableColumn<BookImport, String> importerCodeColumn;
    @FXML private TableColumn<BookImport, String> importerUsernameColumn;
    @FXML private TableColumn<BookImport, LocalDate> importDateColumn;

    @FXML private ComboBox<ImportSearchCriteria> searchCriteriaComboBox;
    @FXML private TextField searchField;

    @FXML private Button importDetailButton;

    @FXML private PaginationController paginationController;

    private MainController mainController;
    private Boolean isSearching;

    public void init(MainController mainController) {
        this.mainController = mainController;
    }

    public void init(MainController mainController,int page) {
        this.mainController = mainController;

        this.paginationController.setCurrentPage(page);
        loadPage();
    }

    private void createBookImportTableView() {
        bookImportTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        bookImportTableView.setFixedCellSize(35.0);
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
        importCodeColumn.setCellValueFactory(new PropertyValueFactory<>("importCode"));
        importerCodeColumn.setCellValueFactory(new PropertyValueFactory<>("importerCode"));
        importerUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("importerUsername"));
        importDateColumn.setCellValueFactory(new PropertyValueFactory<>("importDate"));

        bookImportTableView.setItems(paginatedBookImportList);
    }

    private void initSearchInputs() {
        searchCriteriaComboBox.setItems(FXCollections.observableArrayList(ImportSearchCriteria.values()));
        searchCriteriaComboBox.getSelectionModel().selectFirst();

        searchField.setPromptText("Nhập mã phiếu nhập là số VD : 1,2,...");
        searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
        searchCriteriaComboBox.valueProperty().addListener((_, _, newVal) -> {
            if (newVal == null) return;

            searchField.setTextFormatter(null);
            isSearching = false;
            searchField.clear();
            switch (newVal){
                case IMPORT_CODE -> {
                    searchField.setPromptText("Nhập mã phiếu nhập là số VD : 1,2,...");
                    searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
                }
                case USER_CODE -> {
                    searchField.setPromptText("Nhập mã tài khoản là số VD : 1,2,...");
                    searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
                }
                case USERNAME -> {
                    searchField.setPromptText("Nhập tên tài khoản ");
                    searchField.setTextFormatter(FormatterUtil.createUsernameFormatter());
                }
                case BOOK_CODE -> {
                    searchField.setPromptText("Nhập mã sách là số VD : 1,2,...");
                    searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
                }
                case BOOK_NAME -> {
                    searchField.setPromptText("Nhập tên sách");
                    searchField.setTextFormatter(FormatterUtil.createBookNameFormatter());
                }
            }
        });
    }

    private void handleRowSelection() {
        bookImportTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, selectBookBorrow) -> {
                    boolean isSelected = selectBookBorrow != null;
                    importDetailButton.setDisable(!isSelected);
                });
    }

    private void loadTableData() {
        isSearching = false;
        paginationController.setCurrentPage(1);

        loadPage();
    }

    private void loadPage() {
        ImportSearchCriteria criteria = isSearching ? searchCriteriaComboBox.getValue() : null;
        Object[] keyword = isSearching ? getKeyword(criteria) : null;

        PageResult<BookImport> pageResult = bookImportService.getBookImportsPage(criteria,
                paginationController.getCurrentPage(), tableRowCount,keyword);

        if (isSearching && pageResult.total() == 0) {
            AppUtil.showInformation("Thông báo",
                    "Không tìm thấy " + criteria.getDisplayName() + " là " + getKeywordDisplay());
            isSearching = false;
            return;
        }

        paginatedBookImportList.setAll(pageResult.data());
        paginationController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowCount));

        bookImportTableView.setPrefHeight((paginatedBookImportList.size() + 1) * 35.0);
    }

    private void updateSearchInputs() {
        ImportSearchCriteria criteria = searchCriteriaComboBox.getValue();
        boolean isDate = criteria == ImportSearchCriteria.IMPORT_DATE;
        if (isDate) {
            fromDayDatePicker.setValue(null);
            toDayDatePicker.setValue(null);
        }

        searchTimeBox.setVisible(isDate);
        searchTimeBox.setManaged(isDate);

        searchField.setVisible(!isDate);
        searchField.setManaged(!isDate);
    }

    private Object[] getKeyword(ImportSearchCriteria criteria) {
        if (criteria == null) return null;

        if (criteria == ImportSearchCriteria.IMPORT_DATE) {

            if (fromDayDatePicker.getValue() == null || toDayDatePicker.getValue() == null) {
                return null;
            }

            return new Object[]{fromDayDatePicker.getValue(), toDayDatePicker.getValue()};
        }

        return new Object[]{searchField.getText().trim()};
    }

    private String getKeywordDisplay() {
        if(searchTimeBox.isVisible()){
            return "từ ngày :"+fromDayDatePicker.getValue().toString()+" đến ngày : "+toDayDatePicker.getValue().toString();
        }
        return searchField.getText().trim();
    }

    @FXML
    public void initialize() {
        createBookImportTableView();
        initSearchInputs();
        loadTableData();

        handleRowSelection();

        paginationController.setOnPageChange(this::loadPage);

        searchCriteriaComboBox.setOnAction(_ -> updateSearchInputs());
    }

    @FXML
    private void onSearch() {
        ImportSearchCriteria criteria = searchCriteriaComboBox.getValue();

        if (criteria == ImportSearchCriteria.IMPORT_DATE) {
            if (fromDayDatePicker.getValue() == null || toDayDatePicker.getValue() == null) {
                AppUtil.showInformation("Cảnh báo", "Không được để trống ngày");
                return;
            }

            if (fromDayDatePicker.getValue().isAfter(toDayDatePicker.getValue())) {
                AppUtil.showInformation("Cảnh báo", "Ngày bắt đầu phải trước ngày kết thúc");
                return;
            }
        }else if(searchField.getText().isBlank()){
            AppUtil.showInformation("Cảnh báo", "Nội dung tìm kiếm không được để trống");
            return;
        }

        isSearching = true;
        paginationController.setCurrentPage(1);
        loadPage();
    }
    @FXML
    private void onShowImportDetail() {
        mainController.onNavigateToImportDetail(
                bookImportTableView.getSelectionModel().getSelectedItem(),paginationController.getCurrentPage());
    }
    @FXML
    private void importBook() {
        mainController.onNavigateToAddBookImportManagement(tableRowCount);
    }

    @FXML private void onRefresh () {
        isSearching = false;
        searchField.clear();
        loadTableData();
    }
}
