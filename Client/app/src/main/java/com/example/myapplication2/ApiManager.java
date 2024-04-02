package com.example.myapplication2;

import android.content.Context;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ApiManager {
    private Context context;

    public ApiManager(Context context) {
        this.context = context;
    }

    // Регистрация. Post.
    public void registerUser(String phoneNumber, String firstName, String lastName, String password, final RegisterCallback callback) {
        // Данные для отправки
        JsonObject json = new JsonObject();
        JsonObject userObject = new JsonObject();
        userObject.addProperty("phone_number", phoneNumber);
        userObject.addProperty("first_name", firstName);
        userObject.addProperty("last_name", lastName);
        userObject.addProperty("password", password);
        json.add("user", userObject);

        // URL для отправки запроса
        String url = "http://firstaidkitapi123.pythonanywhere.com/user";

        // Отправка POST запроса
        Ion.with(context)
                .load("POST", url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            // Обработка ошибки
                            e.printStackTrace();
                            if (callback != null) {
                                callback.onFailure(e.getMessage());
                            }
                            return;
                        }
                        // Обработка успешного ответа
                        if (result != null) {
                            if (result.has("detail")) {
                                // Пользователь с таким номером уже зарегистрирован
                                String errorDetail = result.get("detail").getAsString();
                                if (callback != null) {
                                    callback.onFailure(errorDetail);
                                }
                            } else if (result.has("new_user_id")) {
                                // Регистрация прошла успешно
                                int userId = result.get("new_user_id").getAsInt();
                                if (callback != null) {
                                    callback.onSuccess(userId);
                                }
                            }
                        }
                    }
                });
    }

    public interface RegisterCallback {
        void onSuccess(int userId);
        void onFailure(String errorMessage);
    }

    // Получение информации о пользователе. Get.
    public void getUserInfo(String authToken, final UserInfoCallback callback) {
        // URL для отправки запроса
        String url = "http://firstaidkitapi123.pythonanywhere.com/user?token=" + authToken;

        // Отправка GET запроса
        Ion.with(context)
                .load("GET", url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            // Обработка ошибки
                            e.printStackTrace();
                            if (callback != null) {
                                callback.onFailure(e.getMessage());
                            }
                            return;
                        }
                        // Обработка успешного ответа
                        if (result != null) {
                            if (result.has("detail")) {
                                // Неверный токен авторизации
                                String errorDetail = result.get("detail").getAsString();
                                if (callback != null) {
                                    callback.onFailure(errorDetail);
                                }
                            } else if (result.has("user")) {
                                // Получаем информацию о пользователе
                                JsonObject userObject = result.get("user").getAsJsonObject();
                                String firstName = userObject.get("first_name").getAsString();
                                String lastName = userObject.get("last_name").getAsString();
                                // Вызываем callback с данными о пользователе
                                if (callback != null) {
                                    callback.onSuccess(firstName, lastName);
                                }
                            }
                        }
                    }
                });
    }

    public interface UserInfoCallback {
        void onSuccess(String firstName, String lastName);
        void onFailure(String errorMessage);
    }


    // Логининг. Post.
    public void login(String phoneNumber, String password, final ApiCallback callback) {
        // Данные для отправки
        JsonObject json = new JsonObject();
        JsonObject userForm = new JsonObject();
        userForm.addProperty("phone_number", phoneNumber);
        userForm.addProperty("password", password);
        json.add("user_form", userForm);

        // URL для отправки запроса
        String url = "http://firstaidkitapi123.pythonanywhere.com/login";

        // Отправка POST запроса
        Ion.with(context)
                .load("POST", url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            // Обработка ошибки
                            e.printStackTrace();
                            if (callback != null) {
                                callback.onResult(false, e.getMessage());
                            }
                            return;
                        }
                        // Обработка успешного ответа
                        if (result != null) {
                            if (result.has("auth_token")) {
                                // Успешная авторизация
                                String authToken = result.get("auth_token").getAsString();
                                // Ваш код для обработки успешной авторизации
                                if (callback != null) {
                                    callback.onResult(true, authToken);
                                }
                            } else if (result.has("detail")) {
                                // Неуспешная авторизация
                                String errorDetail = result.get("detail").getAsString();
                                // Ваш код для обработки неуспешной авторизации
                                if (callback != null) {
                                    callback.onResult(false, errorDetail);
                                }
                            }
                        }
                    }
                });
    }

    public interface ApiCallback {
        void onResult(boolean success, String message);
    }
}
