package com.example.myapplication2;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.builder.Builders;
import com.orm.SugarDb;
import com.orm.SugarRecord;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;



public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CourseAdapter adapter;

    private ActivityResultLauncher<Intent> startActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        File[] dbFiles = getApplicationContext().getDatabasePath(" ").getParentFile().listFiles();
//
//        for (File dbFile : dbFiles) {
//            if (dbFile.isFile()) {
//                // Удаляем базу данных
//                if (dbFile.delete()) {
//                    Log.d("PrintLine", "Deleted database file: " + dbFile.getName());
//                } else {
//                    Log.e("PrintLine", "Failed to delete database file: " + dbFile.getName());
//                }
//            }
//        }



        if (DataBase.getUser() == null || DataBase.getUser().getAuthToken().equals("-1")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {

            try {
//                List<String> days = new ArrayList<String>();
//                days.add("Пн");
//                days.add("Ср");
//                Course course = new Course("Кагоцел", "Таблетки", 3, "До еды", "Olga Bergazova", "7:30", days, "10.04.2024", "12.04.2024", "Comment");
//                course.save();



//                List<Course> allCourses = Course.listAll(Course.class);
//                Log.d("PrintLine", allCourses.get(0).getDaysOfIntake().get(0));
//                Log.d("PrintLine", allCourses.get(0).getDaysOfIntake().get(1));
//                Log.d("PrintLine", allCourses.get(0).getDaysOfIntake().get(2));
//                Log.d("PrintLine", allCourses.get(0).getDaysOfIntake().get(3));
//                Log.d("PrintLine", allCourses.get(0).getDaysOfIntake().get(4));
//                Log.d("PrintLine", allCourses.get(0).getDaysOfIntake().get(5));

//            SugarRecord.deleteAll(Course.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "General Notifications"; // Название канала для пользователя
                    String description = "Channel for general notifications"; // Описание канала
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("1", name, importance);
                    channel.setDescription(description);
                    // Регистрация канала в системе
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

            setDate();
            TextView userName = findViewById(R.id.user_name);
            userName.setText(DataBase.getUser().getFirstName() + " " + DataBase.getUser().getLastName());
            ImageView profileIcon = findViewById(R.id.profile_icon);
            setProfileIcon(DataBase.getUser().getProfilePhotoNumber(), profileIcon);




//            SugarRecord.deleteAll(FamilyMember.class);

//            File[] dbFiles = getApplicationContext().getDatabasePath(" ").getParentFile().listFiles();
//
//            for (File dbFile : dbFiles) {
//                if (dbFile.isFile()) {
//                    // Удаляем базу данных
//                    if (dbFile.delete()) {
//                        Log.d("PrintLine", "Deleted database file: " + dbFile.getName());
//                    } else {
//                        Log.e("PrintLine", "Failed to delete database file: " + dbFile.getName());
//                    }
//                }
//            }

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
                            Intent intent = new Intent(MainActivity.this, AddMedicamentActivity.class);
                            startActivityResultLauncher.launch(intent);
                            addWidget.setVisibility(View.INVISIBLE);
                        }
                    });

                    Button addNewCourse = findViewById(R.id.widget_add_button_add_course);
                    addNewCourse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
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
            catch (Exception e) {
                Log.e("PrintLine", e.getMessage());
            }
        }

    }
//        try {
//            if (DataBase.getUser() == null) {
//                Intent intent = new Intent(this, LoginActivity.class);
//                startActivity(intent);
//            } else {
//                try {
//                    setDate();
//                    TextView userName = findViewById(R.id.user_name);
//                    userName.setText(DataBase.user.getFirstName() + " " + DataBase.user.getLastName());
//                    ImageView profileIcon = findViewById(R.id.profile_icon);
//                    setProfileIcon(DataBase.user.getProfilePhotoNumber(), profileIcon);
//                } catch (Exception e) {
//                    Log.d("PrintLine", e.getMessage());
//                }
//            }
//        }
//        catch (Exception e) {
//            Log.d("PrintLine", e.getMessage());
//        }
//
//    }

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

    // Установка даты для календаря.
    private void setDate() {
        // Получаем текущую дату
        Calendar calendar = Calendar.getInstance();

        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("WeekNumbers", String.valueOf(dayNow));

        // Установка календаря на начало текущей недели (понедельник)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        TextView textView1 = findViewById(R.id.button1_date);
        TextView textView2 = findViewById(R.id.button2_date);
        TextView textView3 = findViewById(R.id.button3_date);
        TextView textView4 = findViewById(R.id.button4_date);
        TextView textView5 = findViewById(R.id.button5_date);
        TextView textView6 = findViewById(R.id.button6_date);
        TextView textView7 = findViewById(R.id.button7_date);
        TextView[] textViews = new TextView[] {textView1, textView2, textView3, textView4, textView5, textView6, textView7};

        ImageView imageView1 = findViewById(R.id.button1_circle);
        ImageView imageView2 = findViewById(R.id.button2_circle);
        ImageView imageView3 = findViewById(R.id.button3_circle);
        ImageView imageView4 = findViewById(R.id.button4_circle);
        ImageView imageView5 = findViewById(R.id.button5_circle);
        ImageView imageView6 = findViewById(R.id.button6_circle);
        ImageView imageView7 = findViewById(R.id.button7_circle);
        ImageView[] imageViews = new ImageView[] {imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7};


        for (int i = 0; i < 7; i++) {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Log.d("WeekNumbers", String.valueOf(dayOfMonth));
            textViews[i].setText(String.valueOf(dayOfMonth));
            if (dayOfMonth == dayNow) {

                textViews[i].setTextColor(getResources().getColor(R.color.white));
                imageViews[i].setImageResource(R.drawable.circle_button);
                TextView titleDateName = findViewById(R.id.title_date_name);
                setDateForDayOfWeek(i+1, titleDateName);

                List<Course> allCourses = Course.listAll(Course.class);

                try {
                    List<Course> validCourses = filterCourses(allCourses, i+1);
                    if (validCourses.isEmpty()) {
                        TextView textView = findViewById(R.id.null_course_now);
                        textView.setVisibility(View.VISIBLE);
                        ListView listMain = findViewById(R.id.main_list);
                        listMain.setVisibility(View.INVISIBLE);

                    }
                    else {
                        TextView textView = findViewById(R.id.null_course_now);
                        textView.setVisibility(View.INVISIBLE);
                        ListView listMain = findViewById(R.id.main_list);
                        listMain.setVisibility(View.VISIBLE);
                        CourseNowAdapter adapter = new CourseNowAdapter(this, validCourses);
                        ListView listView = findViewById(R.id.main_list);
                        listView.setAdapter(adapter);
                    }
                }
                catch (Exception e) {
                    Log.e("PrintLine", e.getMessage());
                }

            }
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
    }

    // Переход в раздел Моя Аптечка.
    public void goFirstAidKit(View v) {
        Intent intent = new Intent(this, FirstAidKitActivity.class);
        startActivity(intent);
    }

    // Переход в раздел Моя Аптечка.
    public void goCourses(View v) {
        Intent intent = new Intent(this, CourseActivity.class);
        startActivity(intent);
    }

    public void goSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    // Логика надатия на кнопку календаря.
    public void goDate(View v) {

        TextView titleDateName = findViewById(R.id.title_date_name);

        TextView textView1 = findViewById(R.id.button1_date);
        textView1.setTextColor(getResources().getColor(R.color.sea));
        TextView textView2 = findViewById(R.id.button2_date);
        textView2.setTextColor(getResources().getColor(R.color.sea));
        TextView textView3 = findViewById(R.id.button3_date);
        textView3.setTextColor(getResources().getColor(R.color.sea));
        TextView textView4 = findViewById(R.id.button4_date);
        textView4.setTextColor(getResources().getColor(R.color.sea));
        TextView textView5 = findViewById(R.id.button5_date);
        textView5.setTextColor(getResources().getColor(R.color.sea));
        TextView textView6 = findViewById(R.id.button6_date);
        textView6.setTextColor(getResources().getColor(R.color.sea));
        TextView textView7 = findViewById(R.id.button7_date);
        textView7.setTextColor(getResources().getColor(R.color.sea));

        ImageView imageView1 = findViewById(R.id.button1_circle);
        imageView1.setImageResource(R.drawable.circle_button_while);
        ImageView imageView2 = findViewById(R.id.button2_circle);
        imageView2.setImageResource(R.drawable.circle_button_while);
        ImageView imageView3 = findViewById(R.id.button3_circle);
        imageView3.setImageResource(R.drawable.circle_button_while);
        ImageView imageView4 = findViewById(R.id.button4_circle);
        imageView4.setImageResource(R.drawable.circle_button_while);
        ImageView imageView5 = findViewById(R.id.button5_circle);
        imageView5.setImageResource(R.drawable.circle_button_while);
        ImageView imageView6 = findViewById(R.id.button6_circle);
        imageView6.setImageResource(R.drawable.circle_button_while);
        ImageView imageView7 = findViewById(R.id.button7_circle);
        imageView7.setImageResource(R.drawable.circle_button_while);


        List<Course> allCourses = Course.listAll(Course.class);

        int id = v.getId();
        if (id == R.id.button1) {
            textView1.setTextColor(getResources().getColor(R.color.white));
            imageView1.setImageResource(R.drawable.circle_button);
            setDateForDayOfWeek(1, titleDateName);
            try {
                List<Course> validCourses = filterCourses(allCourses, 1);
                if (validCourses.isEmpty()) {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.VISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.INVISIBLE);

                }
                else {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.INVISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.VISIBLE);
                    CourseNowAdapter adapter = new CourseNowAdapter(this, validCourses);
                    ListView listView = findViewById(R.id.main_list);
                    listView.setAdapter(adapter);
                }
            }
            catch (Exception e) {
                Log.e("PrintLine", e.getMessage());
            }
        }
        if (id == R.id.button2) {
            textView2.setTextColor(getResources().getColor(R.color.white));
            imageView2.setImageResource(R.drawable.circle_button);
            setDateForDayOfWeek(2, titleDateName);
            try {
                List<Course> validCourses = filterCourses(allCourses, 2);
                if (validCourses.isEmpty()) {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.VISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.INVISIBLE);

                }
                else {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.INVISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.VISIBLE);
                    CourseNowAdapter adapter = new CourseNowAdapter(this, validCourses);
                    ListView listView = findViewById(R.id.main_list);
                    listView.setAdapter(adapter);
                }
            }
            catch (Exception e) {
                Log.e("PrintLine", e.getMessage());
            }
        }
        if (id == R.id.button3) {
            textView3.setTextColor(getResources().getColor(R.color.white));
            imageView3.setImageResource(R.drawable.circle_button);
            setDateForDayOfWeek(3, titleDateName);
            try {
                List<Course> validCourses = filterCourses(allCourses, 3);
                if (validCourses.isEmpty()) {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.VISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.INVISIBLE);

                }
                else {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.INVISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.VISIBLE);
                    CourseNowAdapter adapter = new CourseNowAdapter(this, validCourses);
                    ListView listView = findViewById(R.id.main_list);
                    listView.setAdapter(adapter);
                }
            }
            catch (Exception e) {
                Log.e("PrintLine", e.getMessage());
            }
        }
        if (id == R.id.button4) {
            textView4.setTextColor(getResources().getColor(R.color.white));
            imageView4.setImageResource(R.drawable.circle_button);
            setDateForDayOfWeek(4, titleDateName);
            try {
                List<Course> validCourses = filterCourses(allCourses, 4);
                if (validCourses.isEmpty()) {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.VISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.INVISIBLE);

                }
                else {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.INVISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.VISIBLE);
                    CourseNowAdapter adapter = new CourseNowAdapter(this, validCourses);
                    ListView listView = findViewById(R.id.main_list);
                    listView.setAdapter(adapter);
                }
            }
            catch (Exception e) {
                Log.e("PrintLine", e.getMessage());
            }
        }
        if (id == R.id.button5) {
            textView5.setTextColor(getResources().getColor(R.color.white));
            imageView5.setImageResource(R.drawable.circle_button);
            setDateForDayOfWeek(5, titleDateName);
            try {
                List<Course> validCourses = filterCourses(allCourses, 5);
                if (validCourses.isEmpty()) {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.VISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.INVISIBLE);

                }
                else {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.INVISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.VISIBLE);
                    CourseNowAdapter adapter = new CourseNowAdapter(this, validCourses);
                    ListView listView = findViewById(R.id.main_list);
                    listView.setAdapter(adapter);
                }
            }
            catch (Exception e) {
                Log.e("PrintLine", e.getMessage());
            }
        }
        if (id == R.id.button6) {
            textView6.setTextColor(getResources().getColor(R.color.white));
            imageView6.setImageResource(R.drawable.circle_button);
            setDateForDayOfWeek(6, titleDateName);
            try {
                List<Course> validCourses = filterCourses(allCourses, 6);
                if (validCourses.isEmpty()) {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.VISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.INVISIBLE);

                }
                else {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.INVISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.VISIBLE);
                    CourseNowAdapter adapter = new CourseNowAdapter(this, validCourses);
                    ListView listView = findViewById(R.id.main_list);
                    listView.setAdapter(adapter);
                }
            }
            catch (Exception e) {
                Log.e("PrintLine", e.getMessage());
            }
        }
        if (id == R.id.button7) {
            textView7.setTextColor(getResources().getColor(R.color.white));
            imageView7.setImageResource(R.drawable.circle_button);
            setDateForDayOfWeek(7, titleDateName);
            try {
                List<Course> validCourses = filterCourses(allCourses, 7);
                if (validCourses.isEmpty()) {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.VISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.INVISIBLE);

                }
                else {
                    TextView textView = findViewById(R.id.null_course_now);
                    textView.setVisibility(View.INVISIBLE);
                    ListView listMain = findViewById(R.id.main_list);
                    listMain.setVisibility(View.VISIBLE);
                    CourseNowAdapter adapter = new CourseNowAdapter(this, validCourses);
                    ListView listView = findViewById(R.id.main_list);
                    listView.setAdapter(adapter);
                }
            }
            catch (Exception e) {
                Log.e("PrintLine", e.getMessage());
            }
        }
    }

    public List<Course> filterCourses(List<Course> allCourses, int num) {
        List<Course> validCourses = new ArrayList<>();
        for (Course course: allCourses) {
            if (course.getDaysOfIntake()!=null && checkIfDayIsInListAndDateRange(course.getDaysOfIntake(), num,
                    course.getStartDate(), course.getEndDate())) {
                validCourses.add(course);
            }
        }
        // Сортировка списка курсов по времени начала
        Collections.sort(validCourses, (course1, course2) -> {
            String intakeTime1 = course1.getIntake();
            String intakeTime2 = course2.getIntake();

            // Преобразование строки времени в минуты для сравнения
            int minutes1 = Integer.parseInt(intakeTime1.split(":")[0]) * 60 + Integer.parseInt(intakeTime1.split(":")[1]);
            int minutes2 = Integer.parseInt(intakeTime2.split(":")[0]) * 60 + Integer.parseInt(intakeTime2.split(":")[1]);

            return Integer.compare(minutes1, minutes2);
        });
        return validCourses;
    }

    public static boolean checkIfDayIsInListAndDateRange(List<String> daysOfIntake, int num, String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();

        try {
            // Преобразование строк в объекты Date
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            // Коррекция номера дня недели под систему Java Calendar
            int correctedNum = num + 1;
            if (correctedNum == 8) correctedNum = 1; // Воскресенье в Java Calendar == 1

            // Настройка календаря для текущей даты
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            int today = cal.get(Calendar.DAY_OF_WEEK);
            int diff = correctedNum - today;

            // Коррекция, чтобы убедиться, что воскресенье берется из текущей недели
            if (correctedNum == 1 && today != Calendar.SUNDAY) {
                diff += 7;
            }
            cal.add(Calendar.DAY_OF_MONTH, diff);

            Date targetDate = cal.getTime();

            // Проверка, что целевая дата попадает в заданный диапазон
            if (targetDate.before(startDate) || targetDate.after(endDate)) {
                return false;
            }

            // Список дней недели соответствующих номерам от 1 до 7
            List<String> daysOfWeek = Arrays.asList(
                    "Понедельник", "Вторник", "Среда", "Четверг",
                    "Пятница", "Суббота", "Воскресенье"
            );

            // Получаем название дня недели
            String dayOfWeek = daysOfWeek.get(num - 1);

            // Проверяем, содержит ли список daysOfIntake данный день недели
            return daysOfIntake.contains(dayOfWeek);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }



    public void setDateForDayOfWeek(int num, TextView textView) {
        // Устанавливаем локаль для русского языка
        Locale russianLocale = new Locale("ru");

        // Коррекция номера дня недели под систему Java Calendar (Понедельник = 2, Воскресенье = 1)
        int correctedNum = num + 1;
        if (correctedNum == 8) correctedNum = 1;

        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int diff = correctedNum - today;

        // Если целевой день - воскресенье, и текущий день недели не воскресенье, корректируем разницу
        if (correctedNum == 1 && today != Calendar.SUNDAY) {
            diff += 7; // Добавляем неделю, чтобы убедиться, что воскресенье берется из текущей недели
        }

        calendar.add(Calendar.DAY_OF_MONTH, diff);

        // Создаем формат даты с учетом русской локализации
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM", russianLocale);

        // Проверяем, является ли дата сегодняшним днем
        Calendar todayCalendar = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)) {
            textView.setText("Сегодня, " + sdf.format(calendar.getTime()));
        } else {
            textView.setText(sdf.format(calendar.getTime()));
        }
    }


    // Переход к профилю.
    public void goProfile(View v) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivityResultLauncher.launch(intent);
    }

}