package controller.bookborrow;

import controller.MainController;
import controller.PaginationController;
import dto.PageResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import model.entity.Book;
import model.entity.Member;
import model.entity.User;
import model.enums.Address;
import model.enums.Gender;
import service.BorrowService;
import service.MemberService;
import util.AppUtil;
import util.FormatterUtil;
import util.MapperUtil;

import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDate;
import java.util.*;

public class AddBookBorrowController {
    private final BorrowService bookBorrowService = new BorrowService();
    private final MemberService memberService = new MemberService();
    private final ObservableList<Member> paginatedMemberList = FXCollections.observableArrayList();
    private final int tableRowMemberCount = 12;
    private final ObservableList<Book> paginatedBookList = FXCollections.observableArrayList();
    private final int tableRowBookCount = 2;
    private final  ObservableList<Book> bookBorrowList = FXCollections.observableArrayList();
    private final ObservableList<Book> paginatedBookBorrowList = FXCollections.observableArrayList();
    private final int tableRowBookBorrowCount = 3;
    private final Map<Integer, Integer> borrowedQuantityMap = new HashMap<>();

    @FXML private VBox memberVBox;
    @FXML private TextField memberNameSearchField;
    @FXML private Button addMemberButton;

    @FXML private VBox addMemberVBox;
    @FXML private TextField memberNameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<Gender> genderComboBox;
    @FXML private ComboBox<Address> addressComboBox;
    @FXML private TextField phoneNumberField;
    @FXML private Label memberNotificationLabel;

    @FXML private VBox memberTableVBox;
    @FXML private TableView<Member> memberTableView;
    @FXML private TableColumn<Member, Void> memberOrdinalColumn;
    @FXML private TableColumn<Member, String> memberCodeColumn;
    @FXML private TableColumn<Member, String> memberNameColumn;
    @FXML private TableColumn<Member, Integer> ageColumn;
    @FXML private TableColumn<Member, String> genderColumn;
    @FXML private TableColumn<Member, String> addressColumn;
    @FXML private TableColumn<Member, String> phoneNumberColumn;
    @FXML private TableColumn<Member, String> statusColumn;
    @FXML private PaginationController paginationMemberController;

    @FXML private VBox bookVBox;
    @FXML private Label borrowerCodeLabel;
    @FXML private Label borrowerNameLabel;
    @FXML private Label borrowerPhoneNumberLabel;
    @FXML private TextField bookNameSearchField;
    @FXML private Button showListBorrowBookButton;
    @FXML private TableView<Book> bookTableView;
    @FXML private TableColumn<Book, Void> bookOrdinalColumn;
    @FXML private TableColumn<Book,String> bookCodeColumn;
    @FXML private TableColumn<Book, String> bookNameColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, Integer> publicationYearColumn;
    @FXML private TableColumn<Book, String> categoryColumn;
    @FXML private TableColumn<Book, Integer> quantityColumn;
    @FXML private TableColumn<Book, String> imageColumn;
    @FXML private TableColumn<Book, Integer> borrowedQuantityBookColumn;
    @FXML private TableColumn<Book, Void> addBookBorrowColumn;
    @FXML private PaginationController paginationBookController;

    @FXML private VBox bookBorrowVBox;
    @FXML private TableView<Book> bookBorrowTableView;
    @FXML private TableColumn<Book, Void> borrowOrdinalColumn;
    @FXML private TableColumn<Book, String> borrowedBookCodeColumn;
    @FXML private TableColumn<Book, String> borrowedBookNameColumn;
    @FXML private TableColumn<Book, Integer> quantityBookColumn;
    @FXML private TableColumn<Book, String> borrowedImageColumn;
    @FXML private TableColumn<Book,Integer> borrowedQuantityColumn;
    @FXML private TableColumn<Book, Void> deleteBookBorrowColumn;
    @FXML private PaginationController paginationBookBorrowController;
    @FXML private DatePicker dueDatePicker;

    private MainController mainController;
    private User currentUser;
    private int tableRowCount;
    private boolean isMemberSearching;
    private boolean isBookSearching;
    private Member selectedMember;

    public void init(MainController mainController,User currentUser) {
        this.mainController = mainController;
        this.currentUser = currentUser;
    }
    public void init(MainController mainController,User currentUser,int tableRowCount) {
        this.mainController = mainController;
        this.currentUser = currentUser;
        this.tableRowCount = tableRowCount;
    }
    private void createMemberTableView() {
        memberTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        memberTableView.setFixedCellSize(35.0);
        memberOrdinalColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                } else {
                    int stt = (paginationMemberController.getCurrentPage() - 1) * tableRowMemberCount + getIndex() + 1;
                    setText(String.valueOf(stt));
                }
            }
        });
        memberOrdinalColumn.setSortable(false);
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

    private void loadTableMemberData() {
        addMemberButton.setDisable(true);
        isMemberSearching = false;
        paginationMemberController.setCurrentPage(1);
        loadMemberPage();
    }

    private void loadMemberPage() {
        String keyword = memberNameSearchField.getText();
        PageResult<Member> pageResult = bookBorrowService.getMembersPage(isMemberSearching, keyword,
                paginationMemberController.getCurrentPage(), tableRowMemberCount);
        if (isMemberSearching && pageResult.total() == 0) {
            AppUtil.showInformation("Thông báo",
                    "Không tìm thấy tên hội viên là " +keyword);
            isMemberSearching = false;
            return;
        }

        paginatedMemberList.setAll(pageResult.data());
        paginationMemberController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowMemberCount));
        memberTableView.setPrefHeight((paginatedMemberList.size() + 1) * 35.0);
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
                setText(empty || publicationYear == 0 ? "" : publicationYear.toString());
            }
        });
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
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
        borrowedQuantityBookColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button btnMinus = new Button("-");
            private final Button btnPlus = new Button("+");
            private final Label quantityLabel = new Label();
            private final HBox box = new HBox(5, btnMinus, quantityLabel, btnPlus);

            {
                box.setAlignment(Pos.TOP_CENTER);

                btnMinus.setOnAction(_ -> {
                    Book book = getTableView().getItems().get(getIndex());
                    int current = borrowedQuantityMap.getOrDefault(book.getId(), 1);

                    if (current == 2) {
                        borrowedQuantityMap.put(book.getId(), 1);
                        updateItem( 1, false);
                    }
                });

                btnPlus.setOnAction(_ -> {
                    Book book = getTableView().getItems().get(getIndex());
                    int current = borrowedQuantityMap.getOrDefault(book.getId(), 1);
                    if(book.getAvailableQuantity() <= 1){
                        AppUtil.showInformation("Cảnh báo","Số lượng sách không đủ");
                        return;
                    }
                    if (current == 1) {
                        borrowedQuantityMap.put(book.getId(),2);
                        updateItem(2, false);
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
                    int value = borrowedQuantityMap.getOrDefault(book.getId(), 1);

                    quantityLabel.setText(String.valueOf(value));
                    setGraphic(box);
                }
            }
        });
        addBookBorrowColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button addBookBorrowButton = new Button();
            {
                addBookBorrowButton.setOnAction(_ -> {
                    Book selectedBook = getTableView().getItems().get(getIndex());

                    Optional<Book> existing = bookBorrowList.stream()
                            .filter(item -> item.getId() == selectedBook.getId())
                            .findFirst();
                    if(selectedBook.getAvailableQuantity() == 0){
                        AppUtil.showInformation("Cảnh báo","Sách đã hết hoặc đã cho mượn hết");
                        return;
                    }

                    if (existing.isPresent()) {
                        bookBorrowList.removeIf(book -> book.getId() == selectedBook.getId());
                    } else {
                        bookBorrowList.add(selectedBook);
                    }
                    showListBorrowBookButton.setDisable(bookBorrowList.isEmpty());
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

                boolean exists = bookBorrowList.stream()
                        .anyMatch(book -> book.getId() == selectedBook.getId());

                addBookBorrowButton.setText(exists ? "Hủy mượn" : "Thêm mượn");

                setGraphic(addBookBorrowButton);
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
        PageResult<Book> pageResult = bookBorrowService.getBooksPage(isBookSearching, keyword,
                paginationBookController.getCurrentPage(), tableRowBookCount);
        if (isBookSearching && pageResult.total() == 0) {
            AppUtil.showInformation("Thông báo",
                    "Không tìm thấy tên sách là " +keyword);
            isBookSearching = false;
            return;
        }

        paginatedBookList.setAll(pageResult.data());
        paginationBookController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowBookCount));
        bookTableView.setPrefHeight(paginatedBookList.size() * 130.0 + 35.0);
    }

    private void createBookBorrowTableView() {
        bookBorrowTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        bookBorrowTableView.setSelectionModel(null);
        bookBorrowTableView.setFixedCellSize(130.0);
        borrowOrdinalColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                } else {
                    int stt = (paginationBookBorrowController.getCurrentPage() - 1) * tableRowBookBorrowCount + getIndex() + 1;
                    setText(String.valueOf(stt));
                }
            }
        });
        borrowOrdinalColumn.setSortable(false);
        borrowedBookCodeColumn.setCellValueFactory(new PropertyValueFactory<>("bookCode"));
        borrowedBookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        quantityBookColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
        borrowedImageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        borrowedImageColumn.setCellFactory(_ -> new TableCell<>() {
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
        borrowedQuantityColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button btnMinus = new Button("-");
            private final Button btnPlus = new Button("+");
            private final Label quantityLabel = new Label();
            private final HBox box = new HBox(5, btnMinus, quantityLabel, btnPlus);

            {
                box.setAlignment(Pos.TOP_CENTER);

                btnMinus.setOnAction(_ -> {
                    Book book = getTableView().getItems().get(getIndex());
                    int current = borrowedQuantityMap.getOrDefault(book.getId(), 1);

                    if (current == 2) {
                        borrowedQuantityMap.put(book.getId(), 1);
                        updateItem( 1, false);
                    }
                });

                btnPlus.setOnAction(_ -> {
                    Book book = getTableView().getItems().get(getIndex());
                    int current = borrowedQuantityMap.getOrDefault(book.getId(), 1);
                    if(book.getQuantity() <= 1){
                        AppUtil.showInformation("Cảnh báo","Số lượng sách không đủ");
                        return;
                    }
                    if (current == 1) {
                        borrowedQuantityMap.put(book.getId(),2);
                        updateItem(2, false);
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
                    int value = borrowedQuantityMap.getOrDefault(book.getId(), 1);

                    quantityLabel.setText(String.valueOf(value));
                    setGraphic(box);
                }
            }
        });
        deleteBookBorrowColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteBookBorrowButton = new Button("Xóa");

            {
                deleteBookBorrowButton.setOnAction(_ -> {
                    Book book = getTableView().getItems().get(getIndex());
                    bookBorrowList.removeIf(b -> b.getId() == book.getId());
                    borrowedQuantityMap.remove(book.getId());
                    loadBookBorrowPage();
                    if(bookBorrowList.isEmpty()){
                        bookTableView.refresh();
                        showListBorrowBookButton.setDisable(true);
                        bookVBox.setVisible(true);
                        bookVBox.setManaged(true);

                        bookBorrowVBox.setVisible(false);
                        bookBorrowVBox.setManaged(false);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBookBorrowButton);
                }
            }
        });
        bookBorrowTableView.setItems(paginatedBookBorrowList);
    }
    private void loadTableBookBorrowData() {
        paginationBookBorrowController.setCurrentPage(1);
        loadBookBorrowPage();
    }
    private void loadBookBorrowPage() {
        int totalItems = bookBorrowList.size();

        int totalPages = (int) Math.ceil((double) totalItems / tableRowBookBorrowCount);
        if (totalPages == 0) totalPages = 1;

        paginationBookBorrowController.setTotalPages(totalPages);

        int currentPage = paginationBookBorrowController.getCurrentPage();

        if (currentPage > totalPages) {
            currentPage = totalPages;
            paginationBookBorrowController.setCurrentPage(currentPage);
        }

        int fromIndex = (currentPage - 1) * tableRowBookBorrowCount;
        int toIndex = Math.min(fromIndex + tableRowBookBorrowCount, totalItems);

        if (fromIndex >= totalItems) {
            paginatedBookBorrowList.clear();
        } else {
            paginatedBookBorrowList.setAll(bookBorrowList.subList(fromIndex, toIndex));
        }
        bookBorrowTableView.setPrefHeight(paginatedBookBorrowList.size() * 130.0 + 35.0);
    }

    @FXML
    public void initialize() {
        memberNameSearchField.setTextFormatter(FormatterUtil.createNameLetterFormatter());
        bookNameSearchField.setTextFormatter(FormatterUtil.createNameLetterFormatter());
        createMemberTableView();
        createBookTableView();
        createBookBorrowTableView();
        paginationMemberController.setOnPageChange(this::loadMemberPage);
        paginationBookController.setOnPageChange(this::loadBookPage);
        paginationBookBorrowController.setOnPageChange(this::loadBookBorrowPage);

        genderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        addressComboBox.setItems(FXCollections.observableArrayList(Address.values()));
        addressComboBox.setVisibleRowCount(5);
        genderComboBox.setPromptText("Chọn giới tính");
        addressComboBox.setPromptText("Chọn địa chỉ");

        memberNameField.setTextFormatter(FormatterUtil.createNameLetterFormatter());
        ageField.setTextFormatter(FormatterUtil.createAgeFormatter());
        phoneNumberField.setTextFormatter(FormatterUtil.createPhoneNumberFormatter());

        loadTableMemberData();

        memberTableVBox.setVisible(true);
        memberTableVBox.setManaged(true);

        memberNameSearchField.textProperty().
                addListener((_, _, _) -> addMemberButton.setDisable(true));
    }
    @FXML
    private void onQuit(){
        if (memberTableVBox.isVisible() || addMemberVBox.isVisible()) {
            mainController.onNavigateToBookBorrowManagement((int) Math.ceil((double) bookBorrowService.countAllBookBorrows() / tableRowCount));
        }
        if(bookVBox.isVisible()){
            borrowedQuantityMap.clear();
            dueDatePicker.setValue(null);

            memberTableView.getSelectionModel().clearSelection();
            bookNameSearchField.clear();
            bookBorrowList.clear();
            paginatedBookBorrowList.clear();
            addMemberButton.setDisable(true);
            showListBorrowBookButton.setDisable(true);
            memberTableVBox.setVisible(true);
            memberTableVBox.setManaged(true);

            bookVBox.setVisible(false);
            bookVBox.setManaged(false);
        }
        if(bookBorrowVBox.isVisible()){
            bookNameSearchField.clear();
            bookTableView.refresh();
            paginatedBookBorrowList.clear();

            bookVBox.setVisible(true);
            bookVBox.setManaged(true);

            bookBorrowVBox.setVisible(false);
            bookBorrowVBox.setManaged(false);
        }
    }
    @FXML
    private void onCheckMember(){
        String keyword = memberNameSearchField.getText();
        if(keyword.isBlank()){
            AppUtil.showInformation("Cảnh báo","Tên hội viên không được để trống");
        }
        boolean isCheckMember = bookBorrowService.checkMember(keyword);
        if(isCheckMember){
            if(!keyword.isBlank()){
                addMemberButton.setDisable(false);
            }
            isMemberSearching =  true;
            paginationMemberController.setCurrentPage(1);
            loadMemberPage();
        }else if(keyword.length() >=2){
            memberNameField.setText(keyword);
        }else {
            AppUtil.showInformation("Cảnh báo",
                    "Tên hội viên không tồn tại và Tên hội viên mới phải ít nhất 2 kí tự ");
        }
        addMemberVBox.setVisible(!isCheckMember && keyword.length() >=2);
        addMemberVBox.setManaged(!isCheckMember && keyword.length() >=2);

        memberTableVBox.setVisible(isCheckMember || keyword.length() < 2);
        memberTableVBox.setManaged(isCheckMember || keyword.length() < 2);
    }
    @FXML
    private void onAddMember(){
        memberNameField.setText(memberNameSearchField.getText());
        addMemberVBox.setVisible(true);
        addMemberVBox.setManaged(true);

        memberTableVBox.setVisible(false);
        memberTableVBox.setManaged(false);
    }

    @FXML
    private void onSaveMember(){
        List<String> fieldBlankList = new ArrayList<>();

        if (memberNameField.getText().isBlank()) fieldBlankList.add("tên hội viên");
        if (phoneNumberField.getText().isBlank()) fieldBlankList.add("số điện thoại");

        if (!fieldBlankList.isEmpty()) {
            String message = String.join(", ", fieldBlankList) + " không được để trống";
            memberNotificationLabel.setText(message);
            return;
        }

        if (phoneNumberField.getText().length() < 10) {
            memberNotificationLabel.setText("Số điện thoại phải đủ 10 số");
            return;
        }
        Member member = new Member(
                null,
                memberNameField.getText(),
                MapperUtil.parseIntOrNull(ageField.getText()),
                genderComboBox.getValue(),
                addressComboBox.getValue(),
                MapperUtil.emptyToNull(phoneNumberField.getText()),
                1);
        String error = memberService.validateAddMember(member);
        if(error != null){
            memberNotificationLabel.setText(error);
            return;
        }
        try {
            selectedMember = memberService.addMember(currentUser,member);
            AppUtil.showInformation("Thành Công","Thêm hội viên thành công");
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
            return;
        }

        borrowerCodeLabel.setText(memberNameField.getText());
        borrowerNameLabel.setText(memberNameField.getText());
        borrowerPhoneNumberLabel.setText(phoneNumberField.getText());

        memberNameField.clear();
        ageField.clear();
        genderComboBox.setPromptText("Chọn giới tính");
        addressComboBox.setPromptText("Chọn địa chỉ");
        phoneNumberField.clear();
        memberNotificationLabel.setText("");

        bookVBox.setVisible(true);
        bookVBox.setManaged(true);

        addMemberVBox.setVisible(false);
        addMemberVBox.setManaged(false);

        loadTableBookData();
    }
    @FXML
    private void onNext(){
        selectedMember = memberTableView.getSelectionModel().getSelectedItem();
        if(selectedMember == null){
            AppUtil.showInformation("Cảnh báo","Bạn phải chọn hội viên");
        } else if (selectedMember.getStatus() == 0) {
            AppUtil.showInformation("Cảnh báo","Bạn phải chọn hội viên còn hoạt động");
        }else {
            showListBorrowBookButton.setDisable(true);
            bookVBox.setVisible(true);
            bookVBox.setManaged(true);

            memberTableVBox.setVisible(false);
            memberTableVBox.setManaged(false);

            borrowerCodeLabel.setText(selectedMember.getMemberCode());
            borrowerNameLabel.setText(selectedMember.getMemberName());
            borrowerPhoneNumberLabel.setText(selectedMember.getPhoneNumber());
            loadTableBookData();
        }
    }
    @FXML
    private void onCheckBook(){
        String keyword = bookNameSearchField.getText();
        if(keyword.isBlank()){
            AppUtil.showInformation("Cảnh báo","Tên sách không được để trống");
        }
        if(bookBorrowService.checkBook(keyword)){
            isBookSearching =  true;
            paginationBookController.setCurrentPage(1);
            loadBookPage();
        }else {
            AppUtil.showInformation("Cảnh báo","Tên sách không tồn tại");
        }
    }
    @FXML
    private void onShowListBookBorrow(){
        loadTableBookBorrowData();

        bookVBox.setVisible(false);
        bookVBox.setManaged(false);

        bookBorrowVBox.setVisible(true);
        bookBorrowVBox.setManaged(true);
    }
    @FXML
    private void  onSaveBookBorrow(){
        LocalDate dueDate = dueDatePicker.getValue();
        if(dueDate == null){
            AppUtil.showInformation("Cảnh báo","Thời Gian dự kiến trả sách không được để trống");
            return;
        }
        if(dueDate.isBefore(LocalDate.now())){
            AppUtil.showInformation("Cảnh báo","Thời gian trả sách phải lớn hơn thời gian hiện tại");
            return;
        }
        try{
            bookBorrowService.addBookBorrow(
                    currentUser,
                    selectedMember,
                    bookBorrowList,
                    borrowedQuantityMap,
                    dueDate);
            AppUtil.showInformation("Thành công", "Mượn sách thành công");
        }catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
            return;
        } catch (Exception e) {
            AppUtil.showInformation("Dữ liệu không đồng bộ", e.getMessage());
        }
        borrowedQuantityMap.clear();
        dueDatePicker.setValue(null);

        memberTableView.getSelectionModel().clearSelection();
        bookBorrowList.clear();

        onRefreshMember();
        onRefreshBook();

        bookBorrowVBox.setVisible(false);
        bookBorrowVBox.setManaged(false);

        memberTableVBox.setVisible(true);
        memberTableVBox.setManaged(true);

    }

    @FXML
    private void onRefreshMember(){
        isMemberSearching = false;
        memberNameSearchField.clear();
        loadTableMemberData();

        addMemberVBox.setVisible(false);
        addMemberVBox.setManaged(false);

        memberTableVBox.setVisible(true);
        memberTableVBox.setManaged(true);
    }

    @FXML
    private void onRefreshBook(){
        isBookSearching = false;
        bookNameSearchField.clear();
        loadTableBookData();
    }
}
