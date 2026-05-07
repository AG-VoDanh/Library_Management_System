package model.entity;

import model.enums.Address;
import model.enums.Gender;

public class Member {
    private int id, status;
    private Integer age;
    private String memberCode, memberName, phoneNumber;
    private Gender gender;
    private Address address;

    public Member(int id, String memberCode, String memberName, Integer age, Gender gender,
                  Address address, String phoneNumber, int status) {
        this.id = id;
        this.memberCode = memberCode;
        this.memberName = memberName;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public Member(String memberCode, String memberName, Integer age, Gender gender,
                  Address address, String phoneNumber, int status) {
        this.memberCode = memberCode;
        this.memberName = memberName;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
