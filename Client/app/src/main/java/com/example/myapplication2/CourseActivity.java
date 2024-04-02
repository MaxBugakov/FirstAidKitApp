package com.example.myapplication2;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity implements CourseFragment.OnCourseSelectedListener  {

    private ActivityResultLauncher<Intent> startActivityResultLauncher;
    final String[] daysOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
    // Массив для отслеживания выбранных элементов
    boolean[] checkedDays = new boolean[daysOfWeek.length];


    public static int mode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course);
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
                mode = 0;
                reloadFragment();
            }
        });

        noActiveMedicamentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noActiveMedicamentsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                activeMedicamentsButton.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                mode = 1;
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
                        Intent intent = new Intent(CourseActivity.this, AddMedicamentActivity.class);
                        startActivityResultLauncher.launch(intent);
                        addWidget.setVisibility(View.INVISIBLE);
                    }
                });

                Button addNewCourse = findViewById(R.id.widget_add_button_add_course);
                addNewCourse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CourseActivity.this, AddCourseActivity.class);
                        startActivityResultLauncher.launch(intent);
                        addWidget.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        Button closeButton = findViewById(R.id.add_course_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWidget.setVisibility(View.INVISIBLE);
            }
        });
    }


    // Реализация метода из интерфейса OnMedicamentSelectedListener (нажатие на list item).
    @Override
    public void onCourseSelected(Course course) {

        Button button = findViewById(R.id.button_open_dialog);
        List<String> selectedDays = new ArrayList<>(0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseActivity.this);
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
                        selectedDays.clear();
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

        // Находим элементы.
        RelativeLayout widgetWrap = findViewById(R.id.widget_course_wrap);
        widgetWrap.setVisibility(View.VISIBLE);

        Toast.makeText(getApplicationContext(), "Выбран курс" + course.getPatient(), Toast.LENGTH_SHORT).show();
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

        // Заполнение форм данными курса.
        name.setText(course.getMedicine());
        quantity.setText(String.valueOf(course.getSingleDose()));
        time.setText(course.getIntake());
        startDate.setText(course.getStartDate());
        endDate.setText(course.getEndDate());
        comment.setText(course.getComment());

        String valueToSelect1 = course.getType();
        int position1 = adapter1.getPosition(valueToSelect1);
        if (position1 >= 0) {
            medicamentType.setSelection(position1);
        }

        String valueToSelect2 = course.getIntakeRelativeToFood();
        int position2 = adapter2.getPosition(valueToSelect2);
        if (position2 >= 0) {
            food.setSelection(position2);
        }

        String valueToSelect3 = course.getPatient();
        int position3 = adapter3.getPosition(valueToSelect3);
        if (position3 >= 0) {
            patient.setSelection(position3);
        }


        for (int i = 0; i < course.getDaysOfIntake().size(); i++) {
            if (course.daysOfIntake.get(i).equals("Понедельник")) {
                checkedDays[0] = true;
            }
            if (course.getDaysOfIntake().get(i).equals("Вторник")) {
                checkedDays[1] = true;
            }
            if (course.getDaysOfIntake().get(i).equals("Среда")) {
                checkedDays[2] = true;
            }
            if (course.getDaysOfIntake().get(i).equals("Четверг")) {
                checkedDays[3] = true;
            }
            if (course.getDaysOfIntake().get(i).equals("Пятница")) {
                checkedDays[4] = true;
            }
            if (course.getDaysOfIntake().get(i).equals("Суббота")) {
                checkedDays[5] = true;
            }
            if (course.getDaysOfIntake().get(i).equals("Воскременье")) {
                checkedDays[6] = true;
            }
        }

        selectedDays.clear();
        for (int i = 0; i < checkedDays.length; i++) {
            if (checkedDays[i]) {
                selectedDays.add(daysOfWeek[i]);
            }
        }


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

                List<Course> allCourses = Course.listAll(Course.class);
                for (Course course1: allCourses) {
                    if (course1.getMedicine().equals(course.getMedicine()) &&
                            course1.getStartDate().equals(course.getStartDate()) &&
                            course1.getEndDate().equals(course.getEndDate())) {
                        Log.d("PrintLine", name.getText().toString().trim());
                        course1.setMedicine(name.getText().toString().trim());
                        course1.type = medicamentType.getSelectedItem().toString().trim();
                        course1.setSingleDose(Double.parseDouble(quantity.getText().toString().trim()));
                        course1.setIntakeRelativeToFood(food.getSelectedItem().toString().trim());
                        course1.setPatient(patient.getSelectedItem().toString().trim());
                        course1.setIntake( time.getText().toString().trim());
                        course1.setDaysOfIntake(selectedDays);
                        course1.setStartDate(startDate.getText().toString().trim());
                        course1.setEndDate(endDate.getText().toString().trim());
                        course1.setComment(comment.getText().toString().trim());
                        course1.save();
                        break;
                    }
                }
                Toast.makeText(getApplicationContext(), "Курс обновлен", Toast.LENGTH_SHORT).show();
                reloadFragment();
                widgetWrap.setVisibility(View.INVISIBLE);
            }
        });


        Button deleteButton = findViewById(R.id.delete_course_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Course> allCourses = Course.listAll(Course.class);
                for (Course course: allCourses) {
                    if (course.getMedicine().equals(name.getText().toString().trim()) &&
                            course.getStartDate().equals(startDate.getText().toString().trim()) &&
                            course.getEndDate().equals(endDate.getText().toString().trim())) {
                        course.delete();
                        break;
                    }
                }
                Toast.makeText(getApplicationContext(), "Курс удален", Toast.LENGTH_SHORT).show();
                reloadFragment();
                widgetWrap.setVisibility(View.INVISIBLE);
            }
        });

        ImageButton closeButton = findViewById(R.id.widget_course_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widgetWrap.setVisibility(View.INVISIBLE);
            }
        });
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

    private void reloadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Создание нового экземпляра вашего фрагмента
        Fragment newFragment = new CourseFragment();

        // Замена старого фрагмента на новый
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, newFragment);

        // Необязательно: Добавление транзакции в стек возврата
        // transaction.addToBackStack(null);

        // Завершение транзакции
        transaction.commit();
    }

    public void goMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Переход в раздел Моя Аптечка.
    public void goFirstAidKit(View v) {
        Intent intent = new Intent(this, FirstAidKitActivity.class);
        startActivity(intent);
    }

    // Переход в раздел Профиль.
    public void goProfile(View v) {
        Intent intent = new Intent(CourseActivity.this, ProfileActivity.class);
        startActivityResultLauncher.launch(intent);
    }

    public void goSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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

}