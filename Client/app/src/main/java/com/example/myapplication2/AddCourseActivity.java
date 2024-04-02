package com.example.myapplication2;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddCourseActivity extends AppCompatActivity {

    final String[] daysOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
    // Массив для отслеживания выбранных элементов
    boolean[] checkedDays = new boolean[daysOfWeek.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = findViewById(R.id.button_open_dialog);
        List<String> selectedDays = new ArrayList<>(0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCourseActivity.this);
                builder.setTitle("Выберите дни недели");
                builder.setMultiChoiceItems(daysOfWeek, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // При выборе обновляем состояние
                        checkedDays[which] = isChecked;
                    }
                });



                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedDays.length; i++) {
                            if (checkedDays[i]) {
                                selectedDays.add(daysOfWeek[i]);
                            }
                        }
                    }
                });

                builder.setNegativeButton("Отмена", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        EditText name = findViewById(R.id.medicament_form_input);
        EditText quantity = findViewById(R.id.medicament_quantity_form_input);
        EditText time = findViewById(R.id.time_form_input);
        EditText startDate = findViewById(R.id.start_date_form_input);
        EditText endDate = findViewById(R.id.end_date_form_input);
        EditText comment = findViewById(R.id.comment_form_input);

        Spinner medicamentType = findViewById(R.id.medicament_type_form_input);
        String[] values1 = {"Таблетки", "Капсулы", "Уколы", "Другое"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicamentType.setAdapter(adapter1);

        Spinner food = findViewById(R.id.food_form_input);
        String[] values2 = {"До еды", "Во время еды", "После еды", "Неважно"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        food.setAdapter(adapter2);

        Spinner patient = findViewById(R.id.patient_form_input);
        String[] peoples = new String[DataBase.getFamilyMembers().size() + 1];
        peoples[0] = DataBase.getUser().getFirstName() + " " + DataBase.getUser().getLastName();
        for (int i = 0; i < DataBase.getFamilyMembers().size(); i++) {
            FamilyMember fam = DataBase.getFamilyMembers().get(i);
            peoples[i+1] = fam.getFirstName() + " " + fam.getLastName();
        }
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, peoples);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patient.setAdapter(adapter3);

        Button addButton = findViewById(R.id.add_course_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().isEmpty() || quantity.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidDate(startDate.getText().toString().trim()) || !isValidDate(endDate.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Поля даты должны быть в формате дд.мм.гггг", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidTime(time.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Поля времени должны быть в формате чч:мм", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedDays.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Не выбраны дни недели для приёма", Toast.LENGTH_SHORT).show();
                    return;
                }
                Course newCourse = new Course(name.getText().toString().trim(),
                        medicamentType.getSelectedItem().toString().trim(),
                        Double.parseDouble(quantity.getText().toString().trim()),
                        food.getSelectedItem().toString().trim(),
                        patient.getSelectedItem().toString().trim(),
                        time.getText().toString().trim(),
                        selectedDays,
                        startDate.getText().toString().trim(),
                        endDate.getText().toString().trim(),
                        comment.getText().toString().trim());
                Log.d("PrintLine", newCourse.getDaysOfIntake().size() + "");
                newCourse.save();
                Toast.makeText(getApplicationContext(), "Курс добавлен", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });



    }

    private boolean isValidDate(String input) {
        // Проверка на соответствие формату "дд.мм.гггг"
        if (!input.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            return false;
        }

        // Разделение даты по точкам
        String[] parts = input.split("\\.");

        // Проверка на то, что длина массива parts равна 3 (дд, мм, гггг)
        if (parts.length != 3) {
            return false;
        }

        // Попытка преобразовать строки в числа и проверка на успешность преобразования
        try {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            // Проверка на корректные значения дня, месяца и года
            // (здесь могут быть дополнительные проверки в зависимости от ваших требований)
            if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900 || year > 2100) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            // Если преобразование в числа вызвало исключение, значит введенные значения не являются числами
            return false;
        }
    }

    private boolean isValidTime(String input) {
        // Проверка на соответствие формату "дд.мм.гггг"
        if (!input.matches("\\d{2}:\\d{2}")) {
            return false;
        }

        // Разделение даты по точкам
        String[] parts = input.split(":");

        // Проверка на то, что длина массива parts равна 3 (дд, мм, гггг)
        if (parts.length != 2) {
            return false;
        }

        // Попытка преобразовать строки в числа и проверка на успешность преобразования
        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Проверка на корректные значения дня, месяца и года
            // (здесь могут быть дополнительные проверки в зависимости от ваших требований)
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            // Если преобразование в числа вызвало исключение, значит введенные значения не являются числами
            return false;
        }
    }

    public void goBackk(View v) {
        finish();
    }
}