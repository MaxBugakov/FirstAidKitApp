package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void readLoginForm(View v) {

        EditText phone = findViewById(R.id.phone_form_input);
        EditText password = findViewById(R.id.password_form_input);

        if (phone.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_LONG).show();
            return;
        }
        if (!phone.getText().toString().trim().matches("^8\\d{10}$")) {
            Toast.makeText(getApplicationContext(), "Номер должен быть в формате 81234567890", Toast.LENGTH_LONG).show();
            return;
        }

        // Создаем экземпляр ApiManager, передавая контекст активити
        ApiManager apiManager = new ApiManager(this);

        apiManager.login(phone.getText().toString().trim(), password.getText().toString().trim(), new ApiManager.ApiCallback() {
            @Override
            public void onResult(boolean success, String message) {
                // Обработка результата
                if (success) {
                    // Вызываем метод getUserInfo() для получения информации о пользователе
                    apiManager.getUserInfo(message, new ApiManager.UserInfoCallback() {
                        @Override
                        public void onSuccess(String firstName, String lastName) {
                            // Обработка успешного запроса
                            Log.d("PrintLine", "First Name: " + firstName);
                            Log.d("PrintLine", "Last Name: " + lastName);

                            if (DataBase.getUser() == null) {
                                User user = new User(firstName, lastName, phone.getText().toString(), 0, message);
                                user.save();
                            }
                            else {
                                User user = DataBase.getUser();
                                user.setAuthToken(message);
                                user.save();
                            }
                            Toast.makeText(getApplicationContext(), "Вы успешно вошли", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // Обработка ошибки
                            Log.e("PrintLine", "Error: " + errorMessage);
                            Toast.makeText(getApplicationContext(), "Ошибка: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Неуспешная авторизация
                    Toast.makeText(getApplicationContext(), "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goRegister(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}