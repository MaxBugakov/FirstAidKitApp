package com.example.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;

public class CourseActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startActivityResultLauncher;

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

        ImageButton closeButton = findViewById(R.id.widget_add_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWidget.setVisibility(View.INVISIBLE);
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

}