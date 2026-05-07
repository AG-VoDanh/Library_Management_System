package controller.bookimport;

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
import model.entity.BookImport;
import model.entity.ImportDetail;
import service.ImportService;
import util.AppUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public class ImportDetailController {
    private final ImportService importService = new ImportService();
    private final ObservableList<ImportDetail> paginatedImportedBookList = FXCollections.observableArrayList();
    private final int tableRowCount = 2;

    @FXML private Label importCodeLabel;
    @FXML private Label importDateLabel;

    @FXML private Label importerCodeLabel;
    @FXML private Label importerUsernameLabel;
    @FXML private Label importerFullNameLabel;
    @FXML private Label importerAgeLabel;
    @FXML private Label importerGenderLabel;
    @FXML private Label importerAddressLabel;
    @FXML private Label importerPhoneNumberLabel;
    @FXML private Label importerRoleLabel;

    @FXML private TableView<ImportDetail> importedBookTableView;
    @FXML private TableColumn<ImportDetail, Void> ordinalNumberColumn;
    @FXML private TableColumn<ImportDetail, String> importedBookCodeColumn;
    @FXML private TableColumn<ImportDetail, String> importedBookNameColumn;
    @FXML private TableColumn<ImportDetail, String> importedAuthorColumn;
    @FXML private TableColumn<ImportDetail, Integer> importedPublicationYearColumn;
    @FXML private TableColumn<ImportDetail, String> importedCategoryColumn;
    @FXML private TableColumn<ImportDetail, Integer> importedQuantityColumn;
    @FXML private TableColumn<ImportDetail, String> importedImageColumn;

    @FXML private PaginationController paginationController;

    private MainController mainController;
    private BookImport bookImport;
    private int currentPage;

    public void init(MainController mainController,BookImport bookImport,int currentPage) {
        this.mainController = mainController;
        this.bookImport = bookImport;
        this.currentPage = currentPage;

        setData();
        loadTableData();
    }

    public void setData(){
        importDateLabel.setText(bookImport.getImportDate()+"");

        importCodeLabel.setText(bookImport.getImportCode());
        importerCodeLabel.setText(bookImport.getImporterCode());
        importerUsernameLabel.setText(bookImport.getImporterUsername());
        importerFullNameLabel.setText(bookImport.getImporterFullName() == null || bookImport.getImporterFullName().isBlank() ?
                "Chưa có" : bookImport.getImporterFullName());
        importerAgeLabel.setText(bookImport.getImporterAge() == null || bookImport.getImporterAge() == 0 ?
                "Chưa có" : String.valueOf(bookImport.getImporterAge()));
        importerPhoneNumberLabel.setText(bookImport.getImportPhoneNumber());
        importerGenderLabel.setText(bookImport.getImporterGender() == null ? "Chưa có" : bookImport.getImporterGender().toString());
        importerAddressLabel.setText(bookImport.getImporterAddress() == null ? "Chưa có" : bookImport.getImporterAddress().toString());
        importerRoleLabel.setText(bookImport.getImporterRole().toString());
    }

    private void createImportBookTableView() {
        importedBookTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        importedBookTableView.setSelectionModel(null);
        importedBookTableView.setFixedCellSize(132.0);
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
        importedBookCodeColumn.setCellValueFactory(new PropertyValueFactory<>("importedBookCode"));
        importedBookNameColumn.setCellValueFactory(new PropertyValueFactory<>("importedBookName"));
        importedAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("importedAuthor"));
        importedPublicationYearColumn.setCellValueFactory(new PropertyValueFactory<>("importedPublicationYear"));
        importedPublicationYearColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Integer borrowedPublicationYear, boolean empty) {
                super.updateItem(borrowedPublicationYear, empty);
                setText(empty || borrowedPublicationYear == 0 ? "" : borrowedPublicationYear.toString());
            }
        });
        importedCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("importedCategory"));
        importedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        importedImageColumn.setCellValueFactory(new PropertyValueFactory<>("importedImage"));
        importedImageColumn.setCellFactory(_ -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String imageName, boolean empty) {
                if (empty || imageName == null) {
                    setGraphic(null);
                } else {
                    Path path = AppUtil.getAppImageDirectory("imports").resolve(imageName);

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

        importedBookTableView.setItems(paginatedImportedBookList);
    }

    private void loadTableData() {
        paginationController.setCurrentPage(1);
        loadPage();
    }

    private void loadPage() {
        PageResult<ImportDetail> pageResult = importService.getImportedBookPage(
                bookImport.getId(), paginationController.getCurrentPage(), tableRowCount);
        paginatedImportedBookList.setAll(pageResult.data());
        paginationController.setTotalPages((int) Math.ceil((double) pageResult.total() / tableRowCount));

        importedBookTableView.setPrefHeight(importedBookTableView.getItems().size() * 132.0 + 35.0);
    }

    @FXML
    public void initialize() {
        createImportBookTableView();

        paginationController.setOnPageChange(this::loadPage);
    }

    @FXML
    private void onQuit() {
        mainController.onNavigateToBookImportManagement(currentPage);
    }
}
