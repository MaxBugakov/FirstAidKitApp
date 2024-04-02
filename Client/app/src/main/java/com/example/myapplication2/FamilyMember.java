package com.example.myapplication2;

import com.orm.SugarRecord;
public class FamilyMember extends SugarRecord {
    private String firstName;
    private String lastName;
    private int profilePhotoNumber;

    // Конструкторы.
    public FamilyMember() { }
    public FamilyMember(String firstName, String lastName, int profilePhotoNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePhotoNumber = profilePhotoNumber;
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

    // Геттеры и сеттеры для поля profilePhotoNumber.
    public int getProfilePhotoNumber() {
        return profilePhotoNumber;
    }
    public void setProfilePhotoNumber(int profilePhotoNumber) {
        this.profilePhotoNumber = profilePhotoNumber;
    }
}
