package com.example.myapplication2;

import com.orm.SugarRecord;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;
import java.lang.reflect.Type;
import java.util.List;

public class Course extends SugarRecord {
    String medicine; // Лекарство
    String type; // Тип лекарства
    double singleDose; // Разовая доза
    String intakeRelativeToFood; // Приём относительно пищи
    String comment; // Комментарий
    String patient; // Лечущийся
    String intake; // Приём
    // Используем transient для списка дней, чтобы Sugar ORM его игнорировал
    transient List<String> daysOfIntake;

    // Храним сериализованный в JSON список дней приёма
    String daysOfIntakeJson;
    String startDate; // Дата начала
    String endDate; // Дата окончания

    // Конструктор по умолчанию требуется для Sugar ORM
    public Course() { }

    // Конструктор для удобства
    public Course(String medicine, String type, double singleDose, String intakeRelativeToFood, String patient, String intake, List<String> daysOfIntake, String startDate, String endDate,  String comment) {
        this.medicine = medicine;
        this.type = type;
        this.singleDose = singleDose;
        this.intakeRelativeToFood = intakeRelativeToFood;
        this.comment = comment;
        this.patient = patient;
        this.intake = intake;
        this.setDaysOfIntake(daysOfIntake);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Геттер и сеттер для daysOfIntake с сериализацией и десериализацией
    public List<String> getDaysOfIntake() {
        if (daysOfIntake == null && daysOfIntakeJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            daysOfIntake = gson.fromJson(daysOfIntakeJson, type);
        }
        return daysOfIntake;
    }

    public void setDaysOfIntake(List<String> daysOfIntake) {
        this.daysOfIntake = daysOfIntake;
        if (daysOfIntake != null) {
            Gson gson = new Gson();
            daysOfIntakeJson = gson.toJson(daysOfIntake);
        } else {
            daysOfIntakeJson = null;
        }
    }

    // Геттеры
    public String getType() {
        return type;
    }
    public String getMedicine() {
        return medicine;
    }

    public double getSingleDose() {
        return singleDose;
    }

    public String getIntakeRelativeToFood() {
        return intakeRelativeToFood;
    }

    public String getComment() {
        return comment;
    }

    public String getPatient() {
        return patient;
    }

    public String getIntake() {
        return intake;
    }


    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    // Сеттеры
    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public void setSingleDose(double singleDose) {
        this.singleDose = singleDose;
    }

    public void setIntakeRelativeToFood(String intakeRelativeToFood) {
        this.intakeRelativeToFood = intakeRelativeToFood;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }


    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
