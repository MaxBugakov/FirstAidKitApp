package com.example.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.result.ActivityResult;

import java.util.ArrayList;
import java.util.List;

public class FirstAidKitActivity extends AppCompatActivity implements ActiveMedicamentsFragment.OnMedicamentSelectedListener {

    public static int medicineMode = 0;
    private ActivityResultLauncher<Intent> startActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_aid_kit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView userName = findViewById(R.id.user_name);
        userName.setText(DataBase.getUser().getFirstName() + " " + DataBase.getUser().getLastName());
        ImageView profileIcon = findViewById(R.id.profile_icon);
        setProfileIcon(DataBase.getUser().getProfilePhotoNumber(), profileIcon);

        Button activeMedicamentsButton = findViewById(R.id.active_medicaments_button);
        Button noActiveMedicamentsButton = findViewById(R.id.noactive_medicaments_button);
        activeMedicamentsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        reloadFragment();

        activeMedicamentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeMedicamentsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                noActiveMedicamentsButton.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                medicineMode = 0;
                reloadFragment();
            }
        });

        noActiveMedicamentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noActiveMedicamentsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                activeMedicamentsButton.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                medicineMode = 1;
                reloadFragment();
            }
        });

        // Инициализация ActivityResultLauncher.
        startActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            reloadFragment();
                            recreate();
                        }
                    }
                });


        ImageButton addButton = findViewById(R.id.addButton);
        RelativeLayout addWidget = findViewById(R.id.widget_add_wrap);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWidget.setVisibility(View.VISIBLE);
                Button addNewMedicament = findViewById(R.id.widget_add_button_add_medicament);
                addNewMedicament.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FirstAidKitActivity.this, AddMedicamentActivity.class);
                        startActivityResultLauncher.launch(intent);
                        addWidget.setVisibility(View.INVISIBLE);
                    }
                });

                Button addNewCourse = findViewById(R.id.widget_add_button_add_course);
                addNewCourse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FirstAidKitActivity.this, AddCourseActivity.class);
                        startActivityResultLauncher.launch(intent);
                        addWidget.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        ImageButton closeButton = findViewById(R.id.widget_add_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWidget.setVisibility(View.INVISIBLE);
            }
        });

    }

    // Реализация метода из интерфейса OnMedicamentSelectedListener (нажатие на list item).
    @Override
    public void onMedicamentSelected(Medicament medicament) {

        // Находим элементы.
        RelativeLayout widgetWrap = findViewById(R.id.widget_wrap1);
        widgetWrap.setVisibility(View.VISIBLE);
        EditText name = findViewById(R.id.name_form_input);
        EditText expirationDate = findViewById(R.id.expiration_date_form_input);
        EditText quantity = findViewById(R.id.quantity_form_input);

        Spinner type = findViewById(R.id.spinner);
        String[] values = {"Таблетки", "Капсулы", "Уколы", "Другое"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        EditText comment = findViewById(R.id.comment_form_input);

        // Устанавливаем значения.
        name.setText(medicament.getTitle());
        expirationDate.setText(medicament.getExpirationDate());
        quantity.setText(String.valueOf(medicament.getQuantity()));

        String valueToSelect = medicament.getType();
        int position = adapter.getPosition(valueToSelect);
        if (position >= 0) {
            type.setSelection(position);
        }

        comment.setText(medicament.getComment());

        ImageButton closeButton = findViewById(R.id.widget_fam_prof_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widgetWrap.setVisibility(View.INVISIBLE);
            }
        });

        Button saveButton = findViewById(R.id.widget_fam_prof_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Medicament medicamentToUpdate = null;
                for (Medicament med : medicaments) {
                    if (med.getTitle().equals(name.getText().toString().trim()) && !name.getText().toString().trim().equals(medicament.getTitle())) {
                        Toast.makeText(getApplicationContext(), "Лекарство с таким названием уже существует", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (med.getTitle().equals(medicament.getTitle())) {
                        medicamentToUpdate = med;
                    }

                }
                medicamentToUpdate.setTitle(name.getText().toString().trim());
                medicamentToUpdate.setExpirationDate(expirationDate.getText().toString().trim());
                medicamentToUpdate.setQuantity(Integer.parseInt(quantity.getText().toString().trim()));
                medicamentToUpdate.setType(type.getSelectedItem().toString().trim());
                medicamentToUpdate.setComment(comment.getText().toString().trim());
                medicamentToUpdate.save();
                Toast.makeText(getApplicationContext(), "Лекарство обновлено", Toast.LENGTH_SHORT).show();
                reloadFragment();
                widgetWrap.setVisibility(View.INVISIBLE);
            }
        });

        Button deleteButton = findViewById(R.id.widget_fam_prof_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PrintLine", "We hererrr");
                List<Medicament> medicaments = Medicament.listAll(Medicament.class);
                for (Medicament med : medicaments) {
                    if (med.getTitle().equals(name.getText().toString().trim())) {
                        med.delete();
                        break;
                    }
                }
                reloadFragment();
                Toast.makeText(getApplicationContext(), "Лекарство удалено", Toast.LENGTH_SHORT).show();
                widgetWrap.setVisibility(View.INVISIBLE);
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

    // Установка фрагмента.
    private void setNewFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, fragment);
        ft.commit();
    }

    private void reloadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Создание нового экземпляра вашего фрагмента
        Fragment newFragment = new ActiveMedicamentsFragment();

        // Замена старого фрагмента на новый
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, newFragment);

        // Необязательно: Добавление транзакции в стек возврата
        // transaction.addToBackStack(null);

        // Завершение транзакции
        transaction.commit();
    }

    // Переход в раздел Главная.
    public void goMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Переход в раздел Главная.
    public void goCourse(View v) {
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
    }

    // Переход в раздел Профиль.
    public void goProfile(View v) {
        Intent intent = new Intent(FirstAidKitActivity.this, ProfileActivity.class);
        startActivityResultLauncher.launch(intent);
    }

    // Установка иконки профиля пользователя.
    private void setProfileIcon(int num, ImageView imageView) {
        if (num == 0)
            imageView.setImageResource(R.drawable.profile_ico_deafult);
        if (num == 1)
            imageView.setImageResource(R.drawable.profile_ico1);
        if (num == 2)
            imageView.setImageResource(R.drawable.profile_ico2);
        if (num == 3)
            imageView.setImageResource(R.drawable.profile_ico3);
        if (num == 4)
            imageView.setImageResource(R.drawable.profile_ico4);
        if (num == 5)
            imageView.setImageResource(R.drawable.profile_ico5);

    }

    public void goSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}