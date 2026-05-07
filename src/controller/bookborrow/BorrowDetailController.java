package controller.bookborrow;

import controller.MainController;
import controller.PaginationController;
import dto.PageResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.entity.BookBorrow;
import model.entity.BorrowDetail;
import service.BorrowService;
import util.AppUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public class BorrowDetailController {
    private final BorrowService borrowService = new BorrowService();
    private final ObservableList<BorrowDetail> paginatedBorrowedBookList = FXCollections.observableArrayList();
    private final int tableRowCount = 2;

    @FXML private Label borrowCodeLabel;
    @FXML private Label borrowDateLabel;
    @FXML private Label dueDateLabel;
    @FXML private Label returnDateLabel;
    @FXML private Label lateFeeLabel;
    @FXML private Label borrowerCodeLabel;
    @FXML private Label borrowerNameLabel;
    @FXML private Label borrowerAgeLabel;
    @FXML private Label borrowerGenderLabel;
    @FXML private Label borrowerAddressLabel;
    @FXML private Label borrowerPhoneNumberLabel;

    @FXML private TableView<BorrowDetail> borrowedBookTableView;
    @FXML private TableColumn<BorrowDetail, Void> ordinalNumberColumn;
    @FXML private TableColumn<BorrowDetail, String> borrowedBookCodeColumn;
    @FXML private TableColumn<BorrowDetail, String> borrowedBookNameColumn;
    @FXML private TableColumn<BorrowDetail, String> borrowedAuthorColumn;
    @FXML private TableColumn<BorrowDetail, Integer> borrowedPublicationYearColumn;
    @FXML private TableColumn<BorrowDetail, String> borrowedCategoryColumn;
    @FXML private TableColumn<BorrowDetail, Integer> borrowedQuantityColumn;
    @FXML private TableColumn<BorrowDetail, String> borrowedImageColumn;

    @FXML private PaginationController paginationController;

    private MainController mainController;
    private String namePage;
    private BookBorrow bookBorrow;
    private int currentPage;

    public void init(MainController mainController,String namePage, BookBorrow bookBorrow,int currentPage) {
        this.mainController = mainController;
        this.namePage = namePage;
        this.bookBorrow = bookBorrow;
        this.currentPage = currentPage;

        setData();
        loadTableData();
    }

    public void setData(){
        borrowCodeLabel.setText(bookBorrow.getBorrowerCode());
        borrowDateLabel.setText(bookBorrow.getBorrowDate()+"");
        dueDateLabel.setText(bookBorrow.getDueDate()+"");
        returnDateLabel.setText(bookBorrow.getReturnDate() == null ? "Chưa trả" : bookBorrow.getReturnDate()+"");
        if (bookBorrow.getReturnDate() == null && (bookBorrow.getLateFee() == null || bookBorrow.getLateFee() == 0)) {
            lateFeeLabel.setText("Chưa có");
        } else {
            lateFeeLabel.setText(bookBorrow.getLateFee().toString());
        }
        borrowerCodeLabel.setText(bookBorrow.getBorrowerCode());
        borrowerNameLabel.setText(bookBorrow.getBorrowerName());

        borrowerAgeLabel.setText(
                bookBorrow.getBorrowerAge() == null || bookBorrow.getBorrowerAge() == 0 ?
                        "Chưa có" : String.valueOf(bookBorrow.getBorrowerAge()));
        borrowerGenderLabel.setText(bookBorrow.getBorrowerGender() == null ? "Chưa có" :bookBorrow.getBorrowerGender().toString());
        borrowerAddressLabel.setText(bookBorrow.getBorrowerAddress() == null ? "Chưa có" :bookBorrow.getBorrowerAddress().toString());
        borrowerPhoneNumberLabel.setText(bookBorrow.getBorrowerPhoneNumber());
    }

    private void createBorrowBookTableView() {
        borrowedBookTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        borrowedBookTableView.setSelectionModel(null);
        borrowedBookTableView.setFixedCellSize(132.0);
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
        borrowedBookCodeColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedBookCode"));
        borrowedBookNameColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedBookName"));
        borrowedAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedAuthor"));
        borrowedPublicationYearColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedPublicationYear"));
        borrowedPublicationYearColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Integer borrowedPublicationYear, boolean empty) {
                super.updateItem(borrowedPublicationYear, empty);
                setText(empty || borrowedPublicationYear == 0 ? "" : borrowedPublicationYear.toString());
            }
        });
        borrowedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedCategory"));
        borrowedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        borrowedImageColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedImage"));
        borrowedImageColumn.setCellFactory(_ -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String imageName, boolean empty) {
                if (empty || imageName == null) {
                    setGraphic(null);
                } else {
                    Path path = AppUtil.getAppImageDirectory("borrows").resolve(imageName);

                    if (Files.exists(path)) {

                        Image image = new Image(
                                path.toAbsolutePath().toUri().toString(),
                                88, 132, true, true
                        );

                        imageView.setImage(image);
                        setGraphic(imageView);

                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        borrowedBookTableView.setItems(paginatedBorrowedBookList);
    }

    private void loadTableData() {
        paginationController.setCurrentPage(1);
        loadPage();
    }

    private void loadPage() {
        PageResult<BorrowDetail> pageResult = borrowService.getBorrowedBookPage(
                bookBorrow.getId(), paginationController.getCurrentPage(), tableRowCount);
        paginatedBorrowedBookList.setAll(pageResult.data());
        paginationController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowCount));

        borrowedBookTableView.setPrefHeight(borrowedBookTableView.getItems().size() * 132.0 + 35.0);
    }

    @FXML
    public void initialize() {
        createBorrowBookTableView();

        paginationController.setOnPageChange(this::loadPage);
    }

    @FXML
    private void onQuit() {
        if(namePage.equals("memberBorrowHistory")){
            mainController.onNavigateToMemberBorrowHistory(bookBorrow.getMemberId(), currentPage);
        }else {
            mainController.onNavigateToBookBorrowManagement(currentPage);
        }

    }

}
