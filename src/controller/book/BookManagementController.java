package controller.book;

import controller.MainController;
import controller.PaginationController;
import dto.PageResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.entity.Book;
import model.enums.Category;
import model.search.BookSearchCriteria;
import service.BookService;
import util.AppUtil;
import util.FormatterUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public class BookManagementController {
    private final BookService bookService = new BookService();
    private final ObservableList<Book> paginatedBookList = FXCollections.observableArrayList();
    private final int tableRowCount = 5;

    @FXML private TableView<Book> bookTableView;
    @FXML private TableColumn<Book, Void> ordinalNumberColumn;
    @FXML private TableColumn<Book,String> bookCodeColumn;
    @FXML private TableColumn<Book, String> bookNameColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, Integer> publicationYearColumn;
    @FXML private TableColumn<Book, String> categoryColumn;
    @FXML private TableColumn<Book, Integer> quantityColumn;
    @FXML private TableColumn<Book, String> imageColumn;

    @FXML private ComboBox<BookSearchCriteria> searchCriteriaComboBox;
    @FXML private ComboBox<Object> searchOptionComboBox;
    @FXML private TextField searchField;

    @FXML private Button updateButton;

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



    private void createBookTableView() {
        bookTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        bookTableView.setFixedCellSize(130.0);
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
        bookCodeColumn.setCellValueFactory(new PropertyValueFactory<>("bookCode"));
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publicationYearColumn.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        publicationYearColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Integer publicationYear, boolean empty) {
                super.updateItem(publicationYear, empty);
                setText(empty || publicationYear == null || publicationYear == 0 ? "" : publicationYear.toString());
            }
        });
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        imageColumn.setCellFactory(_ -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String imageName, boolean empty) {
                super.updateItem(imageName, empty);
                if (empty || imageName == null) {
                    setGraphic(null);
                } else {

                    Path path = AppUtil.getAppImageDirectory("books").resolve(imageName);

                    if (Files.exists(path)) {

                        Image image = new Image(
                                path.toAbsolutePath().toUri().toString(),
                                88, 130, true, true
                        );

                        imageView.setImage(image);
                        setGraphic(imageView);

                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        bookTableView.setItems(paginatedBookList);
    }

    private void initSearchInputs() {
        searchCriteriaComboBox.setItems(FXCollections.observableArrayList(BookSearchCriteria.values()));
        searchCriteriaComboBox.getSelectionModel().selectFirst();
        searchField.setPromptText("Nhập mã sách là số VD : 1,2,...");
        searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
        searchCriteriaComboBox.valueProperty().addListener((_, _, newVal) -> {
            if (newVal == null) return;

            searchField.setTextFormatter(null);
            isSearching = false;
            searchField.clear();
            switch (newVal){
                case BOOK_CODE -> {
                    searchField.setPromptText("Nhập mã sách là số VD : 1,2,...");
                    searchField.setTextFormatter(FormatterUtil.createNumberFormatter());
                }
                case BOOK_NAME -> {
                    searchField.setPromptText("Nhập tên sách");
                    searchField.setTextFormatter(FormatterUtil.createBookNameFormatter());
                }
                case AUTHOR -> {
                    searchField.setPromptText("Nhập tác giả (chỉ chữ)");
                    searchField.setTextFormatter(FormatterUtil.createLetterFormatter());
                }
                case PUBLICATION_YEAR -> {
                    searchField.setPromptText("Nhập năm xuất bản (từ 1901 đến 2155)");
                    searchField.setTextFormatter(FormatterUtil.createPublicationYearFormatter());
                }
            }
        });
    }

    private void handleRowSelection() {
        bookTableView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, selectedBook) -> {
                    boolean isSelected = selectedBook != null;
                    updateButton.setDisable(!isSelected);
                }
        );
    }

    private void loadTableData() {
        isSearching = false;
        paginationController.setCurrentPage(1);
        loadPage();
    }

    private void loadPage() {
        BookSearchCriteria criteria = isSearching ? searchCriteriaComboBox.getValue() : null;
        String keyword = isSearching ? getKeyword(criteria) : null;

        PageResult<Book> pageResult = bookService.getBooksPage(criteria, keyword,
                paginationController.getCurrentPage(), tableRowCount);

        if (isSearching && pageResult.total() == 0) {
            AppUtil.showInformation("Thông báo",
                    "Không tìm thấy " + criteria.getDisplayName() + " là " + getKeywordDisplay());
            isSearching = false;
            return;
        }
        paginatedBookList.setAll(pageResult.data());
        paginationController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowCount));

        bookTableView.setPrefHeight(paginatedBookList.size() * 130.0 + 35.0);
    }

    private void updateSearchInputs(){
        boolean statusSearch = false;
        if(searchCriteriaComboBox.getValue() == BookSearchCriteria.CATEGORY){
            statusSearch = true;
            searchOptionComboBox.setItems(FXCollections.observableArrayList(Category.values()));
        }
        if(statusSearch) {
            searchOptionComboBox.getSelectionModel().selectFirst();
        }
        searchOptionComboBox.setVisible(statusSearch);
        searchOptionComboBox.setManaged(statusSearch);

        searchField.setVisible(!statusSearch);
        searchField.setManaged(!statusSearch);
    }

    private String getKeyword(BookSearchCriteria criteria) {
        Object value = searchOptionComboBox.getValue();

        if (value == null) return searchField.getText().trim();

        if (criteria == BookSearchCriteria.CATEGORY) {
            return ((Category) value).name();
        }
        return searchField.getText().trim();
    }

    private String getKeywordDisplay() {
        if (searchOptionComboBox.isVisible()) {
            return searchOptionComboBox.getValue().toString();
        }
        return searchField.getText().trim();
    }

    @FXML
    public void initialize() {
        createBookTableView();
        initSearchInputs();
        handleRowSelection();

        loadTableData();

        paginationController.setOnPageChange(this::loadPage);
        searchCriteriaComboBox.setOnAction(_ -> updateSearchInputs());
    }
    @FXML
    private void onSearch() {
        BookSearchCriteria criteria = searchCriteriaComboBox.getValue();
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
    private void onUpdateBook() {
        mainController.onNavigateToUpdateBook(bookTableView.getSelectionModel().getSelectedItem(),paginationController.getCurrentPage());
    }

    @FXML private void onRefresh () {
        isSearching = false;
        searchField.clear();
        loadTableData();
    }
}
