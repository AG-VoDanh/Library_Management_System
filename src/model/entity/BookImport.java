package model.entity;

import model.enums.Address;
import model.enums.Gender;
import model.enums.Role;

import java.time.LocalDate;

public class BookImport {
    private int id, userId;
    private Integer importerAge;
    private String importCode, importerCode, importerUsername, importerFullName, importerEmail, importPhoneNumber;
    private LocalDate importDate;
    private Gender importerGender;
    private Address importerAddress;
    private Role importerRole;

    public BookImport(int id, String importCode, int userId, LocalDate importDate,
                      String importerCode, String importerUsername, String importerFullName, Integer importerAge, Gender importerGender,
                      Address importerAddress, String importerEmail, String importPhoneNumber,Role importerRole) {
        this.id = id;
        this.importCode = importCode;
        this.userId = userId;
        this.importDate = importDate;
        this.importerCode = importerCode;
        this.importerUsername = importerUsername;
        this.importerFullName = importerFullName;
        this.importerAge = importerAge;
        this.importerGender = importerGender;
        this.importerAddress = importerAddress;
        this.importerEmail = importerEmail;
        this.importPhoneNumber = importPhoneNumber;
        this.importerRole = importerRole;
    }

    public BookImport(String importCode, int userId, LocalDate importDate,String importerCode,
                      String importerUsername, String importerFullName, Integer importerAge, Gender importerGender,
                      Address importerAddress, String importerEmail, String importPhoneNumber,Role importerRole) {
        this.importCode = importCode;
        this.userId = userId;
        this.importDate = importDate;
        this.importerCode = importerCode;
        this.importerUsername = importerUsername;
        this.importerFullName = importerFullName;
        this.importerAge = importerAge;
        this.importerGender = importerGender;
        this.importerAddress = importerAddress;
        this.importerEmail = importerEmail;
        this.importPhoneNumber = importPhoneNumber;
        this.importerRole = importerRole;
    }

    public Role getImporterRole() {
        return importerRole;
    }

    public void setImporterRole(Role importerRole) {
        this.importerRole = importerRole;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImportCode() {
        return importCode;
    }

    public void setImportCode(String importCode) {
        this.importCode = importCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDate importDate) {
        this.importDate = importDate;
    }

    public String getImporterCode() {
        return importerCode;
    }

    public void setImporterCode(String importerCode) {
        this.importerCode = importerCode;
    }

    public String getImporterUsername() {
        return importerUsername;
    }

    public void setImporterUsername(String importerUsername) {
        this.importerUsername = importerUsername;
    }

    public String getImporterFullName() {
        return importerFullName;
    }

    public void setImporterFullName(String importerFullName) {
        this.importerFullName = importerFullName;
    }

    public Integer getImporterAge() {
        return importerAge;
    }

    public void setImporterAge(Integer importerAge) {
        this.importerAge = importerAge;
    }

    public Gender getImporterGender() {
        return importerGender;
    }

    public void setImporterGender(Gender importerGender) {
        this.importerGender = importerGender;
    }

    public Address getImporterAddress() {
        return importerAddress;
    }

    public void setImporterAddress(Address importerAddress) {
        this.importerAddress = importerAddress;
    }

    public String getImporterEmail() {
        return importerEmail;
    }

    public void setImporterEmail(String importerEmail) {
        this.importerEmail = importerEmail;
    }

    public String getImportPhoneNumber() {
        return importPhoneNumber;
    }

    public void setImportPhoneNumber(String importPhoneNumber) {
        this.importPhoneNumber = importPhoneNumber;
    }
}
