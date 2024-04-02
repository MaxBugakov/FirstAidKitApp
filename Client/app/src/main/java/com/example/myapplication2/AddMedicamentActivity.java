package com.example.myapplication2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;


public class AddMedicamentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner = findViewById(R.id.spinner);
        String[] values = {"Таблетки", "Капсулы", "Уколы", "Другое"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = findViewById(R.id.name_form_input);
                EditText expirationDate = findViewById(R.id.expiration_date_form_input);
                EditText quantity = findViewById(R.id.quantity_form_input);
                EditText comment = findViewById(R.id.comment_form_input);

                if (name.getText().toString().trim().isEmpty() || expirationDate.getText().toString().trim().isEmpty() ||
                        quantity.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidDate(expirationDate.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Поле \"Годен до\" должно быть в формате дд.мм.гггг", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Medicament> medicaments = Medicament.listAll(Medicament.class);
                for (Medicament med : medicaments) {
                    if (med.getTitle().equals(name.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "Лекарство с таким названием уже существует", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Medicament newMedicament = new Medicament(name.getText().toString().trim(), expirationDate.getText().toString().trim(),
                        Integer.parseInt(quantity.getText().toString().trim()) , spinner.getSelectedItem().toString().trim(),
                        comment.getText().toString().trim());
                newMedicament.save();
                Toast.makeText(getApplicationContext(), "Лекарство добавлено", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }
    public void goBack(View v) {
        finish();
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
}