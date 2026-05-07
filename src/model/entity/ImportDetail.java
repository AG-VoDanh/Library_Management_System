package model.entity;

import model.enums.Category;

public class ImportDetail {
    private int id, importId, bookId, quantity;
    private Integer importedPublicationYear;
    private String importedBookCode,importedBookName, importedAuthor, importedImage;
    private Category importedCategory;

    public ImportDetail(int id, int importId, int bookId, int quantity,String importedBookCode, String importedBookName,
                        String importedAuthor, Integer importedPublicationYear, Category importedCategory, String importedImage) {
        this.id = id;
        this.importId = importId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.importedBookCode = importedBookCode;
        this.importedBookName = importedBookName;
        this.importedAuthor = importedAuthor;
        this.importedPublicationYear = importedPublicationYear;
        this.importedCategory = importedCategory;
        this.importedImage = importedImage;
    }

    public ImportDetail(int importId, int bookId, int quantity,String importedBookCode, String importedBookName,
                        String importedAuthor, Integer importedPublicationYear, Category importedCategory, String importedImage) {
        this.importId = importId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.importedBookCode = importedBookCode;
        this.importedBookName = importedBookName;
        this.importedAuthor = importedAuthor;
        this.importedPublicationYear = importedPublicationYear;
        this.importedCategory = importedCategory;
        this.importedImage = importedImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImportedBookCode() {
        return importedBookCode;
    }

    public void setImportedBookCode(String importedBookCode) {
        this.importedBookCode = importedBookCode;
    }

    public String getImportedBookName() {
        return importedBookName;
    }

    public void setImportedBookName(String importedBookName) {
        this.importedBookName = importedBookName;
    }

    public String getImportedAuthor() {
        return importedAuthor;
    }

    public void setImportedAuthor(String importedAuthor) {
        this.importedAuthor = importedAuthor;
    }

    public Integer getImportedPublicationYear() {
        return importedPublicationYear;
    }

    public void setImportedPublicationYear(Integer importedPublicationYear) {
        this.importedPublicationYear = importedPublicationYear;
    }

    public Category getImportedCategory() {
        return importedCategory;
    }

    public void setImportedCategory(Category importedCategory) {
        this.importedCategory = importedCategory;
    }

    public String getImportedImage() {
        return importedImage;
    }

    public void setImportedImage(String importedImage) {
        this.importedImage = importedImage;
    }
}
