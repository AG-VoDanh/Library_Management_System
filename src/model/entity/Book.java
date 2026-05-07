package model.entity;

import model.enums.Category;

public class Book {
    private int id, quantity,availableQuantity;
    private Integer publicationYear;
    private String bookCode, bookName, author,image;
    private Category category;

    public Book(int id, String bookCode, String bookName, String author, Integer publicationYear,
                Category category, int quantity, String image,int availableQuantity) {
        this.id = id;
        this.bookCode = bookCode;
        this.bookName = bookName;
        this.author = author;
        this.publicationYear = publicationYear;
        this.category = category;
        this.quantity = quantity;
        this.image = image;
        this.availableQuantity = availableQuantity;
    }

    public Book(String bookCode, String bookName, String author, Integer publicationYear,
                Category category, int quantity, String image,int availableQuantity) {
        this.bookCode = bookCode;
        this.bookName = bookName;
        this.author = author;
        this.publicationYear = publicationYear;
        this.category = category;
        this.quantity = quantity;
        this.image = image;
        this.availableQuantity = availableQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
