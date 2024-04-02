package com.example.myapplication2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startActivityResultLauncher;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView userName = findViewById(R.id.user_name);
        userName.setText(DataBase.getUser().getFirstName() + " " + DataBase.getUser().getLastName());
        ImageView profileIcon = findViewById(R.id.profile_icon);
        setProfileIcon(DataBase.getUser().getProfilePhotoNumber(), profileIcon);

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
                        Intent intent = new Intent(SettingsActivity.this, AddMedicamentActivity.class);
                        startActivityResultLauncher.launch(intent);
                        addWidget.setVisibility(View.INVISIBLE);
                    }
                });

                Button addNewCourse = findViewById(R.id.widget_add_button_add_course);
                addNewCourse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SettingsActivity.this, AddCourseActivity.class);
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



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        Switch switch1 = findViewById(R.id.switch1);
        Switch switch2 = findViewById(R.id.switch2);

        // Восстановление состояния
        switch1.setChecked(sharedPreferences.getBoolean("switch1", false));
        switch2.setChecked(sharedPreferences.getBoolean("switch2", false));

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("switch1", isChecked);
            editor.apply();

            if (isChecked) {
                checkMedicationExpirationAndNotify();
            }
        });

        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("switch2", isChecked);
            editor.apply();

            if (isChecked) {
                checkCourseIntakeAndNotify();
            }
        });


    }

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101; // Константа для кода запроса

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение было предоставлено, можно, например, включить уведомления
                // или активировать связанные функции в приложении
            } else {
                // Разрешение отклонено, информируем пользователя
                Toast.makeText(this, "Для отправки уведомлений необходимо разрешение", Toast.LENGTH_LONG).show();

                // Можно также добавить логику для отображения диалогового окна или Snackbar с предложением
                // перейти в настройки приложения для включения разрешения
                showRationaleDialog();
            }
        }
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Необходимо разрешение")
                .setMessage("Это разрешение необходимо для отправки важных уведомлений. Пожалуйста, включите его в настройках приложения.")
                .setPositiveButton("Настройки", (dialog, which) -> {
                    // Интент для перехода в настройки приложения
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    private void checkMedicationExpirationAndNotify() {
        List<Medicament> medicaments = Medicament.listAll(Medicament.class);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date today = new Date();

        for (Medicament medicament : medicaments) {
            try {
                Date expirationDate = sdf.parse(medicament.getExpirationDate());
                if (expirationDate != null && today.after(expirationDate)) {
                    // Срок годности истек
                    sendNotification("Срок годности лекарства истек", medicament.getTitle());
                    break; // Отправляем уведомление о первом просроченном лекарстве и выходим
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private void checkCourseIntakeAndNotify() {
        List<Course> courses = Course.listAll(Course.class);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        Date today = new Date();
        String todayStr = sdfDate.format(today);

        for (Course course : courses) {
            try {
                Date startDate = sdfDate.parse(course.getStartDate());
                Date endDate = sdfDate.parse(course.getEndDate());
                if (startDate == null || endDate == null) continue;

                if (!today.before(startDate) && !today.after(endDate)) {
                    // Сегодняшняя дата находится в диапазоне курса
                    String dateTimeStr = todayStr + " " + course.getIntake(); // Добавляем время приема к сегодняшней дате
                    Date intakeDateTime = sdfDateTime.parse(dateTimeStr);

                    if (intakeDateTime != null && today.compareTo(intakeDateTime) >= 0) {
                        // Если время приема уже наступило
                        sendNotification("Время принять лекарство", course.getMedicine());
                        break; // Отправляем уведомление о первом необходимом приеме и выходим
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "General Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for general notifications");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Установите иконку вашего уведомления
                .setContentTitle(title) // Заголовок уведомления
                .setContentText(content) // Текст уведомления
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Приоритет для управления насколько настойчивым должно быть уведомление
                .setAutoCancel(true); // Уведомление будет автоматически закрыто при нажатии на него

        // ID для уведомления. Каждое уведомление должно иметь уникальный ID
        int notificationId = (int) System.currentTimeMillis();

        // NotificationManagerCompat используется для совместимости на разных версиях Android
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(notificationId, builder.build());
    }


//    private void checkExpirationAndNotify() {
//        List<Medicament> allMedicaments = Medicament.listAll(Medicament.class);
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
//        Date today = new Date();
//        for (Medicament medicament : allMedicaments) {
//            try {
//                Date expirationDate = sdf.parse(medicament.getExpirationDate());
//                if (expirationDate != null && today.after(expirationDate)) {
//                    // Срок годности истек, отправляем уведомление
//                    sendNotification();
//                    break; // Прекращаем проверку после первого найденного истекшего лекарства
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void goCourses(View v) {
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
    }

    public void goMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goProfile(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void goFirstAidKit(View v) {
        Intent intent = new Intent(this, FirstAidKitActivity.class);
        startActivity(intent);
    }

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
}