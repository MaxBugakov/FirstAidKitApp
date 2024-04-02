package com.example.myapplication2;

import com.orm.SugarRecord;
import java.util.List;

public class User extends SugarRecord {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int profilePhotoNumber;
    private String authToken;

    // Конструкторы.
    public User() { }
    public User(String firstName, String lastName, String phoneNumber, int profilePhotoNumber, String authToken) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profilePhotoNumber = profilePhotoNumber;
        this.authToken = authToken;
    }

    // Геттеры и сеттеры для поля firstName.
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Геттеры и сеттеры для поля lastName.
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Геттеры и сеттеры для поля phoneNumber.
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Геттеры и сеттеры для поля profilePhotoNumber.
    public int getProfilePhotoNumber() {
        return profilePhotoNumber;
    }
    public void setProfilePhotoNumber(int profilePhotoNumber) {
        this.profilePhotoNumber = profilePhotoNumber;
    }

    // Геттер и сеттер для нового поля
    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
