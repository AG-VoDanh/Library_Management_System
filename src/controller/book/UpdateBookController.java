package controller.book;

import controller.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.entity.Book;
import model.entity.User;
import model.enums.Category;
import service.BookService;
import util.AppUtil;
import util.FormatterUtil;
import util.MapperUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class UpdateBookController {
    private final BookService bookService = new BookService();

    @FXML private TextField bookNameField;
    @FXML private TextField authorField;
    @FXML private TextField publicationYearField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField quantityField;
    @FXML private ImageView imageView;
    @FXML private Label notificationLabel;

    @FXML private Button deleteImageButton;

    private File selectedImageFile = null;
    private String selectedImageName = null;

    private MainController mainController;
    private User currentUser;
    private Book updateBook;
    private int currentPage;
    private boolean isRemoveImage = false;

    public void init(MainController mainController,User currentUser,Book updateBook,int currentPage) {
        this.mainController = mainController;
        this.currentUser = currentUser;
        this.updateBook = updateBook;
        this.currentPage = currentPage;

        setData();
    }

    public void setData(){
        bookNameField.setText(updateBook.getBookName());
        authorField.setText(updateBook.getAuthor());
        publicationYearField.setText(String.valueOf(updateBook.getPublicationYear()));
        categoryComboBox.getSelectionModel().select(updateBook.getCategory());
        quantityField.setText(String.valueOf(updateBook.getQuantity()));
        selectedImageName = updateBook.getImage();
        if (selectedImageName != null && !selectedImageName.isBlank()) {
            Path imagePath = AppUtil.getAppImageDirectory("books").resolve(selectedImageName);
            if (Files.exists(imagePath)) {
                selectedImageFile = imagePath.toFile();
                imageView.setImage(new Image(imagePath.toUri().toString(), 88, 132, true, true));
            } else {
                selectedImageFile = null;
                imageView.setImage(null);
            }
        } else {
            selectedImageFile = null;
            imageView.setImage(null);
        }
    }

    private void handleRowSelection() {
        imageView.imageProperty().addListener((_, _, newImage) -> {
            boolean hasImage = newImage != null;
            deleteImageButton.setDisable(!hasImage);
        });
    }

    @FXML
    public void initialize() {
        categoryComboBox.setItems(FXCollections.observableArrayList(Category.values()));
        categoryComboBox.setPromptText("Chọn thể loại");
        categoryComboBox.setVisibleRowCount(5);
        quantityField.setTextFormatter(FormatterUtil.createNumberFormatter());
        publicationYearField.setTextFormatter(FormatterUtil.createPublicationYearFormatter());
        bookNameField.setTextFormatter(FormatterUtil.createBookNameFormatter());
        authorField.setTextFormatter(FormatterUtil.createNameLetterFormatter());

        handleRowSelection();
    }

    @FXML
    private void onSave() {
        List<String> fieldBlankList = new ArrayList<>();

        if (bookNameField.getText().isBlank()) fieldBlankList.add("tên sách");
        if (quantityField.getText().isBlank()) fieldBlankList.add("số lượng");

        if (!fieldBlankList.isEmpty()) {
            String message = String.join(", ", fieldBlankList) + " không được để trống";
            notificationLabel.setText(message);
            return;
        }

        if(MapperUtil.emptyToNull(authorField.getText()) != null && authorField.getText().length() < 2){
            notificationLabel.setText( "Tác gỉa 2-50 ký tự");
            return;
        }

        if (MapperUtil.parseIntOrNull(publicationYearField.getText()) != null && (Integer.parseInt(publicationYearField.getText()) < 1901 || Integer.parseInt(publicationYearField.getText()) > Year.now().getValue())) {
            notificationLabel.setText("năm xuất bản phải > 1901 và nhỏ hơn năm hiện tại");
            return;
        }
        int newQuantity = Integer.parseInt(quantityField.getText());
        int oldQuantity = this.updateBook.getQuantity();
        int borrowedQuantity = oldQuantity - this.updateBook.getAvailableQuantity();

        if (newQuantity > oldQuantity) {
            notificationLabel.setText("Chỉ được giảm số lượng. Để tăng thêm sách, vui lòng dùng chức năng Nhập Sách!");
            return;
        }

        if (newQuantity < borrowedQuantity) {
            notificationLabel.setText("Số lượng không được nhỏ hơn số sách đang có người mượn (" + borrowedQuantity + " cuốn)");
            return;
        }
        Book existingBook = this.updateBook;
        Book updateBook = new Book(this.updateBook.getId(),this.updateBook.getBookCode(),
                bookNameField.getText(),
                MapperUtil.emptyToNull(authorField.getText()),
                MapperUtil.parseIntOrNull(publicationYearField.getText()),
                categoryComboBox.getValue(),
                Integer.parseInt(quantityField.getText()),
                MapperUtil.emptyToNull(selectedImageName),
                this.updateBook.getAvailableQuantity() -
                        (this.updateBook.getQuantity() - Integer.parseInt(quantityField.getText())));
        try {
            String error = bookService.updateBook(currentUser,updateBook,existingBook,selectedImageFile,isRemoveImage);
            if(error != null){
                notificationLabel.setText(error);
                return;
            }
            this.updateBook = updateBook;
            isRemoveImage = false;
            notificationLabel.setText("");
            AppUtil.showInformation("Thành Công","Sửa thông tin sách thành công");
        } catch (SecurityException se) {
            AppUtil.showInformation("Phiên làm việc kết thúc", se.getMessage());
            mainController.onLogout();
        }
    }
    @FXML
    private void onQuit() {
        mainController.onNavigateToBookManagement(currentPage);
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
            isRemoveImage = false;

            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void onDeleteImage(){
        selectedImageFile = null;
        selectedImageName = null;

        isRemoveImage = true;

        imageView.setImage(null);
    }

    @FXML
    private void onReset(){
        setData();
    }
}
