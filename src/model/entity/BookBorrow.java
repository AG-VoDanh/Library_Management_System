package model.entity;

import model.enums.Address;
import model.enums.Gender;

import java.time.LocalDate;

public class BookBorrow {
    private int id,memberId;
    private Integer lateFee,borrowerAge;
    private String borrowCode, borrowerCode, borrowerName,borrowerPhoneNumber;
    private LocalDate borrowDate, dueDate, returnDate;
    private Gender borrowerGender;
    private Address borrowerAddress;

    public BookBorrow(int id, String borrowCode, int memberId, LocalDate borrowDate, LocalDate dueDate,
                      LocalDate returnDate, Integer lateFee,String borrowerCode, String borrowerName, Integer borrowerAge, Gender borrowerGender,
                      Address borrowerAddress, String borrowerPhoneNumber) {
        this.id = id;
        this.borrowCode = borrowCode;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.lateFee = lateFee;
        this.borrowerCode = borrowerCode;
        this.borrowerName = borrowerName;
        this.borrowerAge = borrowerAge;
        this.borrowerGender = borrowerGender;
        this.borrowerAddress = borrowerAddress;
        this.borrowerPhoneNumber = borrowerPhoneNumber;
    }

    public BookBorrow(String borrowCode, int memberId, LocalDate borrowDate, LocalDate dueDate,
                      LocalDate returnDate, Integer lateFee,String borrowerCode, String borrowerName, Integer borrowerAge, Gender borrowerGender,
                      Address borrowerAddress, String borrowerPhoneNumber) {
        this.borrowCode = borrowCode;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.lateFee = lateFee;
        this.borrowerCode = borrowerCode;
        this.borrowerName = borrowerName;
        this.borrowerAge = borrowerAge;
        this.borrowerGender = borrowerGender;
        this.borrowerAddress = borrowerAddress;
        this.borrowerPhoneNumber = borrowerPhoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBorrowCode() {
        return borrowCode;
    }

    public void setBorrowCode(String borrowCode) {
        this.borrowCode = borrowCode;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Integer getLateFee() {
        return lateFee;
    }

    public void setLateFee(Integer lateFee) {
        this.lateFee = lateFee;
    }

    public String getBorrowerCode() {
        return borrowerCode;
    }

    public void setBorrowerCode(String borrowerCode) {
        this.borrowerCode = borrowerCode;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public Integer getBorrowerAge() {
        return borrowerAge;
    }

    public void setBorrowerAge(Integer borrowerAge) {
        this.borrowerAge = borrowerAge;
    }

    public Gender getBorrowerGender() {
        return borrowerGender;
    }

    public void setBorrowerGender(Gender borrowerGender) {
        this.borrowerGender = borrowerGender;
    }

    public Address getBorrowerAddress() {
        return borrowerAddress;
    }

    public void setBorrowerAddress(Address borrowerAddress) {
        this.borrowerAddress = borrowerAddress;
    }

    public String getBorrowerPhoneNumber() {
        return borrowerPhoneNumber;
    }

    public void setBorrowerPhoneNumber(String borrowerPhoneNumber) {
        this.borrowerPhoneNumber = borrowerPhoneNumber;
    }
}
