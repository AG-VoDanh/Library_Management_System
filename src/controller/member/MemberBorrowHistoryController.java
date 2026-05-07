package controller.member;

import controller.MainController;
import controller.PaginationController;
import dto.PageResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entity.BookBorrow;
import service.BorrowService;

import java.time.LocalDate;

public class MemberBorrowHistoryController {
    private final BorrowService bookBorrowService = new BorrowService();
    private final ObservableList<BookBorrow> paginatedBookBorrowList = FXCollections.observableArrayList();
    private final int tableRowCount = 18;

    @FXML private TableView<BookBorrow> bookBorrowTableView;
    @FXML private TableColumn<BookBorrow, Void> ordinalNumberColumn;
    @FXML private TableColumn<BookBorrow, String> borrowerCodeColumn;
    @FXML private TableColumn<BookBorrow, String> borrowerNameColumn;
    @FXML private TableColumn<BookBorrow, LocalDate> borrowDateColumn;
    @FXML private TableColumn<BookBorrow, LocalDate> dueDateColumn;
    @FXML private TableColumn<BookBorrow, LocalDate> returnDateColumn;
    @FXML private TableColumn<BookBorrow,Integer> lateFeeColumn;
    @FXML private TableColumn<BookBorrow, String> statusColumn;

    @FXML private Button borrowDetailButton;
    @FXML private PaginationController paginationController;

    private MainController mainController;
    private int memberId;
    private int currentPage;

    public void init(MainController mainController,int memberId,int currentPage) {
        this.mainController = mainController;
        this.memberId = memberId;
        this.currentPage = currentPage;

        loadTableData();
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

    private void handleRowSelection() {
        bookBorrowTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((_, _, selectBookBorrow) -> borrowDetailButton.setDisable(selectBookBorrow == null));
    }

    private void loadTableData() {
        paginationController.setCurrentPage(1);
        loadPage();
    }

    private void loadPage() {
        PageResult<BookBorrow> pageResult = bookBorrowService.getBookBorrowsPageByMemberId(
                memberId,paginationController.getCurrentPage(), tableRowCount);

        paginatedBookBorrowList.setAll(pageResult.data());
        paginationController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowCount));

        bookBorrowTableView.setPrefHeight((paginatedBookBorrowList.size() + 1) * 35.0);
    }

    @FXML
    public void initialize() {
        createBookBorrowTableView();
        handleRowSelection();
        paginationController.setOnPageChange(this::loadPage);
    }
    @FXML
    private void onShowBorrowDetail(){
        mainController.onNavigateToBorrowDetail("memberBorrowHistory",
                bookBorrowTableView.getSelectionModel().getSelectedItem(),paginationController.getCurrentPage());
    }
    @FXML
    private void onQuit(){
        mainController.onNavigateToMemberManagement(currentPage);
    }
}
