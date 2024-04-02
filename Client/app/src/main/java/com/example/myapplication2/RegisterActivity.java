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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void readRegisterForm(View v) {

        EditText phone = findViewById(R.id.phone_form_input);
        EditText name = findViewById(R.id.first_name_form_input);
        EditText surname = findViewById(R.id.last_name_form_input);
        EditText password = findViewById(R.id.password_form_input);


        if (phone.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                name.getText().toString().isEmpty() || surname.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_LONG).show();
            return;
        }
        ApiManager apiManager = new ApiManager(this);

        // Вызываем метод registerUser() для регистрации пользователя
        apiManager.registerUser(phone.getText().toString(), name.getText().toString(),
                surname.getText().toString(), password.getText().toString(), new ApiManager.RegisterCallback() {
            @Override
            public void onSuccess(int userId) {
                // Регистрация прошла успешно
                Log.d("Registration", "User successfully registered. New user ID: " + userId);
                Toast.makeText(getApplicationContext(), "Вы успешно зарегистрировались", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Ошибка при регистрации
                Log.e("Registration", "Registration failed. Error: " + errorMessage);
                Toast.makeText(getApplicationContext(), "Ошибка: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}