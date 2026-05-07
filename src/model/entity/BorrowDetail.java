package model.entity;

import model.enums.Category;

public class BorrowDetail {
    private int id, borrowId, bookId, quantity;
    private Integer borrowedPublicationYear;
    private String borrowedBookCode,borrowedBookName, borrowedAuthor, borrowedImage;
    private Category borrowedCategory;

    public BorrowDetail(int id,int borrowId, int bookId, int quantity,String borrowedBookCode, String borrowedBookName,
                        String borrowedAuthor, Integer borrowedPublicationYear, Category borrowedCategory, String borrowedImage) {
        this.id = id;
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.borrowedBookCode = borrowedBookCode;
        this.borrowedBookName = borrowedBookName;
        this.borrowedAuthor = borrowedAuthor;
        this.borrowedPublicationYear = borrowedPublicationYear;
        this.borrowedCategory = borrowedCategory;
        this.borrowedImage = borrowedImage;
    }

    public BorrowDetail(int borrowId, int bookId, int quantity,String borrowedBookCode, String borrowedBookName,
                        String borrowedAuthor, Integer borrowedPublicationYear, Category borrowedCategory, String borrowedImage) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.borrowedBookCode = borrowedBookCode;
        this.borrowedBookName = borrowedBookName;
        this.borrowedAuthor = borrowedAuthor;
        this.borrowedPublicationYear = borrowedPublicationYear;
        this.borrowedCategory = borrowedCategory;
        this.borrowedImage = borrowedImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
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

    public String getBorrowedBookCode() {
        return borrowedBookCode;
    }

    public void setBorrowedBookCode(String borrowedBookCode) {
        this.borrowedBookCode = borrowedBookCode;
    }

    public String getBorrowedBookName() {
        return borrowedBookName;
    }

    public void setBorrowedBookName(String borrowedBookName) {
        this.borrowedBookName = borrowedBookName;
    }

    public String getBorrowedAuthor() {
        return borrowedAuthor;
    }

    public void setBorrowedAuthor(String borrowedAuthor) {
        this.borrowedAuthor = borrowedAuthor;
    }

    public Integer getBorrowedPublicationYear() {
        return borrowedPublicationYear;
    }

    public void setBorrowedPublicationYear(Integer borrowedPublicationYear) {
        this.borrowedPublicationYear = borrowedPublicationYear;
    }

    public Category getBorrowedCategory() {
        return borrowedCategory;
    }

    public void setBorrowedCategory(Category borrowedCategory) {
        this.borrowedCategory = borrowedCategory;
    }

    public String getBorrowedImage() {
        return borrowedImage;
    }

    public void setBorrowedImage(String borrowedImage) {
        this.borrowedImage = borrowedImage;
    }
}
