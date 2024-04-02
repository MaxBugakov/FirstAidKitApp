package com.example.myapplication2;

import com.orm.SugarRecord;

public class Medicament extends SugarRecord {
    private String title;
    private String expirationDate;
    private int quantity;
    private String type;
    private String comment;

    public Medicament() {
        // Пустой конструктор обязателен для Sugar ORM
    }

    public Medicament(String title, String expirationDate, int quantity, String type, String comment) {
        this.title = title;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.type = type;
        this.comment = comment;
    }

    // Геттеры и сеттеры для всех полей

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
