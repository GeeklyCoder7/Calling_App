package com.example.callingapp.models;

public class UserModel {
    String userName;
    String userPhoneNumber;
    String userId;

    public UserModel(String userName, String userPhoneNumber, String userId) {
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userId = userId;
    }

    public UserModel(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
