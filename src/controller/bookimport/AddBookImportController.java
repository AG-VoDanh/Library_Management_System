package controller.bookimport;

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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.entity.Book;
import model.enums.Category;
import model.entity.User;
import service.ImportService;
import util.AppUtil;
import util.FormatterUtil;
import util.MapperUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.*;

public class AddBookImportController {
    private final ImportService bookImportService = new ImportService();
    private final ObservableList<Book> paginatedBookList = FXCollections.observableArrayList();
    private final int tableRowBookCount = 3;
    private final  ObservableList<Book> bookImportList = FXCollections.observableArrayList();
    private final ObservableList<Book> paginatedBookImportList = FXCollections.observableArrayList();
    private final int tableRowBookImportCount = 3;
    private final Map<Integer, Integer> importedQuantityMap = new HashMap<>();

    @FXML private VBox bookVBox;
    @FXML private TextField bookNameSearchField;
    @FXML private Button addBookButton;
    @FXML private Button showListImportBookButton;

    @FXML private VBox addBookVBox;
    @FXML private TextField bookNameField;
    @FXML private TextField authorField;
    @FXML private TextField publicationYearField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField quantityField;
    @FXML private Label bookNotificationLabel;
    @FXML private ImageView imageView;
    @FXML private Button deleteImageButton;

    @FXML private VBox bookTableVBox;
    @FXML private TableView<Book> bookTableView;
    @FXML private TableColumn<Book,Void> bookOrdinalColumn;
    @FXML private TableColumn<Book,String> bookCodeColumn;
    @FXML private TableColumn<Book,String> bookNameColumn;
    @FXML private TableColumn<Book,String> authorColumn;
    @FXML private TableColumn<Book,Integer> publicationYearColumn;
    @FXML private TableColumn<Book,String> categoryColumn;
    @FXML private TableColumn<Book,Integer> quantityColumn;
    @FXML private TableColumn<Book,String> imageColumn;
    @FXML private TableColumn<Book,Integer> importedQuantityBookColumn;
    @FXML private TableColumn<Book,Void> addBookImportColumn;
    @FXML private PaginationController paginationBookController;

    @FXML private VBox bookImportVBox;
    @FXML private TableView<Book> bookImportTableView;
    @FXML private TableColumn<Book,Void> importOrdinalColumn;
    @FXML private TableColumn<Book,String> importedBookCodeColumn;
    @FXML private TableColumn<Book,String> importedBookNameColumn;
    @FXML private TableColumn<Book,String> importedImageColumn;
    @FXML private TableColumn<Book,Integer> importedQuantityColumn;
    @FXML private TableColumn<Book,Void> deleteBookImportColumn;
    @FXML private PaginationController paginationBookImportController;

    private MainController mainController;
    private User currentUser;
    private File selectedImageFile = null;
    private String selectedImageName = null;
    private int tableRowCount;
    private boolean isBookSearching;
    private Integer tempBookId = -1;


    public void init(MainController mainController,User currentUser) {
        this.mainController = mainController;
        this.currentUser = currentUser;
    }
    public void init(MainController mainController,User currentUser,int tableRowCount) {
        this.mainController = mainController;
        this.currentUser = currentUser;
        this.tableRowCount = tableRowCount;
    }

    private void createBookTableView() {
        bookTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        bookTableView.setSelectionModel(null);
        bookTableView.setFixedCellSize(130.0);
        bookOrdinalColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                } else {
                    int stt = (paginationBookController.getCurrentPage() - 1) * tableRowBookCount + getIndex() + 1;
                    setText(String.valueOf(stt));
                }
            }
        });
        bookOrdinalColumn.setSortable(false);
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
            private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
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
        importedQuantityBookColumn.setCellFactory(_ -> new TableCell<>() {
            private final TextField textField = new TextField();

            {
                textField.setTextFormatter(FormatterUtil.createNumberFormatter());

                textField.textProperty().addListener((_, _, newVal) -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {

                        Book book = getTableView().getItems().get(getIndex());

                        importedQuantityMap.put(book.getId(), newVal.isEmpty() ? null : Integer.parseInt(newVal));

                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());

                    Integer value = importedQuantityMap.get(book.getId());

                    textField.setText(value == null ? "" : String.valueOf(value));
                    boolean isAlreadyAdded = bookImportList.stream()
                            .anyMatch(b -> b.getId() == book.getId());
                    textField.setDisable(isAlreadyAdded);
                    setGraphic(textField);
                }
            }
        });
        addBookImportColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button addBookImportButton = new Button();
            {
                addBookImportButton.setOnAction(_ -> {
                    Book selectedBook = getTableView().getItems().get(getIndex());

                    Optional<Book> existing = bookImportList.stream()
                            .filter(item -> item.getId() == selectedBook.getId())
                            .findFirst();
                    if (existing.isPresent()) {
                        bookImportList.removeIf(book -> book.getId() == selectedBook.getId());
                    } else {
                        Integer value = importedQuantityMap.get(selectedBook.getId());
                        if(value == null){
                            AppUtil.showInformation("Cảnh bảo","Bạn phải nhập số lượng ");
                        }else if(value > Integer.MAX_VALUE - selectedBook.getQuantity()){
                            AppUtil.showInformation("Cảnh bảo",
                                    "Bạn phải nhập số lượng nhỏ hơn giới hạn hiện tai là "+(Integer.MAX_VALUE - selectedBook.getQuantity()));
                        }else if(value == 0){
                            AppUtil.showInformation("Cảnh bảo","Bạn phải nhập số lượng > 0");
                        }else {
                            bookImportList.add(selectedBook);
                        }
                    }
                    showListImportBookButton.setDisable(bookImportList.isEmpty());
                    getTableView().refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Book selectedBook = getTableView().getItems().get(getIndex());

                boolean exists = bookImportList.stream()
                        .anyMatch(book -> book.getId() == selectedBook.getId());

                addBookImportButton.setText(exists ? "Hủy nhập" : "Thêm nhập");

                setGraphic(addBookImportButton);
            }
        });
        bookTableView.setItems(paginatedBookList);
    }
    private void loadTableBookData() {
        isBookSearching = false;
        paginationBookController.setCurrentPage(1);
        loadBookPage();
    }
    private void loadBookPage() {
        String keyword = bookNameSearchField.getText();
        PageResult<Book> pageResult = bookImportService.getBooksPage(isBookSearching, keyword,
                paginationBookController.getCurrentPage(), tableRowBookCount);
        if (isBookSearching && pageResult.total() == 0) {
            AppUtil.showInformation("Thông báo", "Không tìm thấy tên sách là " +keyword);
            isBookSearching = false;
            return;
        }

        paginatedBookList.setAll(pageResult.data());
        paginationBookController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowBookCount));
        bookTableView.setPrefHeight(paginatedBookList.size() * 130.0 + 35.0);
    }

    private void createBookImportTableView() {
        bookImportTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        bookImportTableView.setSelectionModel(null);
        bookImportTableView.setFixedCellSize(130.0);
        importOrdinalColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                } else {
                    int stt = (paginationBookImportController.getCurrentPage() - 1) * tableRowBookImportCount + getIndex() + 1;
                    setText(String.valueOf(stt));
                }
            }
        });
        importOrdinalColumn.setSortable(false);
        importedBookCodeColumn.setCellValueFactory(new PropertyValueFactory<>("bookCode"));
        importedBookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        importedImageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        importedImageColumn.setCellFactory(_ -> new TableCell<>() {
            private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
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
        importedQuantityColumn.setCellFactory(_ -> new TableCell<>() {
            private final TextField textField = new TextField();

            {
                textField.setTextFormatter(FormatterUtil.createNumberFormatter());

                textField.textProperty().addListener((_, _, newVal) -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {

                        Book book = getTableView().getItems().get(getIndex());

                        importedQuantityMap.put(book.getId(), newVal.isEmpty() ? null : Integer.parseInt(newVal));
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());

                    Integer value = importedQuantityMap.get(book.getId());
                    textField.setText(value == null ? "" : String.valueOf(value));

                    setGraphic(textField);
                }
            }
        });
        deleteBookImportColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteBookImportButton = new Button("Xóa");

            {
                deleteBookImportButton.setOnAction(_ -> {
                    Book book = getTableView().getItems().get(getIndex());
                    bookImportList.removeIf(b -> b.getId() == book.getId());
                    importedQuantityMap.remove(book.getId());
                    loadBookImportPage();
                    if(bookImportList.isEmpty()){
                        bookTableView.refresh();
                        showListImportBookButton.setDisable(true);
                        bookVBox.setVisible(true);
                        bookVBox.setManaged(true);
                        addBookVBox.setVisible(false);
                        addBookVBox.setManaged(false);
                        bookTableVBox.setVisible(true);
                        bookTableVBox.setManaged(true);

                        bookImportVBox.setVisible(false);
                        bookImportVBox.setManaged(false);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBookImportButton);
                }
            }
        });
        bookImportTableView.setItems(paginatedBookImportList);
    }
    private void loadTableBookImportData() {
        paginationBookImportController.setCurrentPage(1);
        loadBookImportPage();
    }
    private void loadBookImportPage() {
        int totalItems = bookImportList.size();

        int totalPages = (int) Math.ceil((double) totalItems / tableRowBookImportCount);
        if (totalPages == 0) totalPages = 1;

        paginationBookImportController.setTotalPages(totalPages);

        int currentPage = paginationBookImportController.getCurrentPage();

        if (currentPage > totalPages) {
            currentPage = totalPages;
            paginationBookImportController.setCurrentPage(currentPage);
        }

        int fromIndex = (currentPage - 1) * tableRowBookImportCount;
        int toIndex = Math.min(fromIndex + tableRowBookImportCount, totalItems);

        if (fromIndex >= totalItems) {
            paginatedBookImportList.clear();
        } else {
            paginatedBookImportList.setAll(bookImportList.subList(fromIndex, toIndex));
        }
        bookImportTableView.setPrefHeight(paginatedBookImportList.size() * 130.0 + 35.0);
    }
    @FXML
    public void initialize() {
        bookNameSearchField.setTextFormatter(FormatterUtil.createBookNameFormatter());
        createBookTableView();
        createBookImportTableView();
        paginationBookController.setOnPageChange(this::loadBookPage);
        paginationBookImportController.setOnPageChange(this::loadBookImportPage);

        categoryComboBox.setItems(FXCollections.observableArrayList(Category.values()));
        categoryComboBox.setPromptText("Chọn thể loại");
        categoryComboBox.setVisibleRowCount(5);
        quantityField.setTextFormatter(FormatterUtil.createNumberFormatter());
        publicationYearField.setTextFormatter(FormatterUtil.createPublicationYearFormatter());
        bookNameField.setTextFormatter(FormatterUtil.createBookNameFormatter());
        authorField.setTextFormatter(FormatterUtil.createNameLetterFormatter());

        bookNameField.setDisable(true);

        loadTableBookData();

        bookTableVBox.setVisible(true);
        bookTableVBox.setManaged(true);

        bookNameSearchField.textProperty().
                addListener((_, _, _) -> addBookButton.setDisable(true));
        imageView.imageProperty().addListener((_, _, newImage) -> {
            boolean hasImage = newImage != null;
            deleteImageButton.setDisable(!hasImage);
        });
    }

    @FXML
    private void onQuit(){
        if (bookVBox.isVisible()) {
            mainController.onNavigateToBookImportManagement((int) Math.ceil((double) bookImportService.countAllBookImports() / tableRowCount));
        }else {
            bookNameSearchField.clear();
            bookTableView.refresh();
            paginatedBookImportList.clear();

            bookVBox.setVisible(true);
            bookVBox.setManaged(true);
            addBookVBox.setVisible(false);
            addBookVBox.setManaged(false);
            bookTableVBox.setVisible(true);
            bookTableVBox.setManaged(true);

            bookImportVBox.setVisible(false);
            bookImportVBox.setManaged(false);
        }
    }

    @FXML
    private void onCheckBook(){
        String keyword = bookNameSearchField.getText();
        if(keyword.isBlank()){
            AppUtil.showInformation("Cảnh báo","Tên sách không được để trống");
        }
        boolean isCheckBook = bookImportService.checkBook(keyword);
        if(isCheckBook){
            if(!keyword.isBlank()){
                addBookButton.setDisable(false);
            }
            isBookSearching =  true;
            paginationBookController.setCurrentPage(1);
            loadBookPage();
        }else if(keyword.length() >=2){
            bookNameField.setText(keyword);
        }else {
            AppUtil.showInformation("Cảnh báo", "Tên sách không tồn tại" );
        }
        addBookVBox.setVisible(!isCheckBook && keyword.length() >=2);
        addBookVBox.setManaged(!isCheckBook && keyword.length() >=2);

        bookTableVBox.setVisible(isCheckBook || keyword.length() < 2);
        bookTableVBox.setManaged(isCheckBook || keyword.length() < 2);
    }

    @FXML
    private void onAddBook(){
        bookNameField.setText(bookNameSearchField.getText());
        addBookVBox.setVisible(true);
        addBookVBox.setManaged(true);

        bookTableVBox.setVisible(false);
        bookTableVBox.setManaged(false);
    }
    @FXML
    private void onSaveBook(){
        List<String> fieldBlankList = new ArrayList<>();

        if (bookNameField.getText().isBlank()) fieldBlankList.add("tên sách");
        if (quantityField.getText().isBlank()) fieldBlankList.add("số lượng");

        if (!fieldBlankList.isEmpty()) {
            String message = String.join(", ", fieldBlankList) + " không được để trống";
            bookNotificationLabel.setText(message);
            return;
        }

        if(MapperUtil.emptyToNull(authorField.getText()) != null && authorField.getText().length() < 2){
            bookNotificationLabel.setText( "Tác gỉa 2-50 ký tự");
            return;
        }
        if(quantityField.getText().isBlank()){
            AppUtil.showInformation("Cảnh báo","Bạn phải nhập số lượng");
            return;
        }

        if(Integer.parseInt(quantityField.getText()) == 0){
            AppUtil.showInformation("Cảnh báo","Bạn phải nhập số lượng > 0");
            return;
        }

        if (MapperUtil.parseIntOrNull(publicationYearField.getText()) != null && (Integer.parseInt(publicationYearField.getText()) < 1901 || Integer.parseInt(publicationYearField.getText()) > Year.now().getValue())) {
            bookNotificationLabel.setText("năm xuất bản phải > 1901 và nhỏ hơn năm hiện tại");
            return;
        }

        Book addbook = new Book(null,
                bookNameField.getText(),
                MapperUtil.emptyToNull(authorField.getText()),
                MapperUtil.parseIntOrNull(publicationYearField.getText()),
                categoryComboBox.getValue(),
                Integer.parseInt(quantityField.getText()),
                MapperUtil.emptyToNull(selectedImageName),
                Integer.parseInt(quantityField.getText()));
        addbook = bookImportService.addBook(addbook,selectedImageFile);

        loadTableBookData();
        addbook.setId(tempBookId);
        tempBookId--;
        bookImportList.add(addbook);
        showListImportBookButton.setDisable(false);
        importedQuantityMap.put(addbook.getId(),Integer.parseInt(quantityField.getText()));

        AppUtil.showInformation("Thành Công","Thêm sách thành công");

        bookNameField.clear();
        authorField.clear();
        publicationYearField.clear();
        categoryComboBox.setPromptText("Chọn thể loại");
        quantityField.clear();
        imageView.setImage(null);
        bookNotificationLabel.setText("");

        bookTableVBox.setVisible(true);
        bookTableVBox.setManaged(true);

        addBookVBox.setVisible(false);
        addBookVBox.setManaged(false);


        loadBookPage();
    }

    @FXML
    private void onSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());

        if (file != null) {
            selectedImageFile = file;
            selectedImageName = file.getName();

            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void onDeleteImage(){
        selectedImageFile = null;
        selectedImageName = null;

        imageView.setImage(null);
    }

    @FXML
    private void onShowListBookImport(){
        loadTableBookImportData();

        bookVBox.setVisible(false);
        bookVBox.setManaged(false);
        addBookVBox.setVisible(false);
        addBookVBox.setManaged(false);
        bookTableVBox.setVisible(false);
        bookTableVBox.setManaged(false);

        bookImportVBox.setVisible(true);
        bookImportVBox.setManaged(true);
    }

    @FXML
    private void onRefreshBook(){
        isBookSearching = false;
        bookNameSearchField.clear();
        loadTableBookData();

        addBookVBox.setVisible(false);
        addBookVBox.setManaged(false);

        bookTableVBox.setVisible(true);
        bookTableVBox.setManaged(true);
    }

    @FXML
    private void onSaveBookImport(){
        String error = bookImportService.validateBookImport(bookImportList,importedQuantityMap);
        if(error != null){
            AppUtil.showInformationBookImport("Cảnh báo",error);
            return;
        }
        try {
            bookImportService.addBookImport(
                    currentUser,
                    bookImportList,
                    importedQuantityMap);

            AppUtil.showInformation("Thành công", "Nhập sách thành công");
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
            return;
        }


        importedQuantityMap.clear();
        bookImportList.clear();
        onRefreshBook();

        bookImportVBox.setVisible(false);
        bookImportVBox.setManaged(false);

        bookTableVBox.setVisible(true);
        bookTableVBox.setManaged(true);
        addBookVBox.setVisible(false);
        addBookVBox.setManaged(false);
        bookTableVBox.setVisible(true);
        bookTableVBox.setManaged(true);

        mainController.onNavigateToBookImportManagement((int) Math.ceil((double) bookImportService.countAllBookImports() / tableRowCount));



    }

}
