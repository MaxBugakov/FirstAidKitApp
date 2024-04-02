package com.example.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private int widgetOfIconsStatus = 0;
    private FamilyMember selectedFamMember;

    private int numOfIconForNewFam = 0;

    private int numOfIconForRealFam = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Заполнение данных.
        ImageView profileIcon = findViewById(R.id.profile_icon_icon);
        setProfileIcon(DataBase.getUser().getProfilePhotoNumber(), profileIcon);

        EditText nameFormInput = findViewById(R.id.name_form_input);
        nameFormInput.setText(DataBase.getUser().getFirstName());

        EditText surnameFormInput = findViewById(R.id.surname_form_input);
        surnameFormInput.setText(DataBase.getUser().getLastName());

        EditText phoneFormInput = findViewById(R.id.phone_form_input);
        phoneFormInput.setText(DataBase.getUser().getPhoneNumber());

        widget1();

        // Заполнение списка членов семьи.
        if (DataBase.getFamilyMembers().isEmpty())
        {
            TextView nullFamListText = findViewById(R.id.null_fam_list_text);
            nullFamListText.setVisibility(View.VISIBLE);
        }
        else {
            TextView nullFamListText = findViewById(R.id.null_fam_list_text);
            nullFamListText.setVisibility(View.INVISIBLE);
            FamilyMemberAdapter adapter = new FamilyMemberAdapter(this, R.layout.item_family_member, new ArrayList<>(DataBase.getFamilyMembers()));
            ListView listView = findViewById(R.id.family_members_list_view);
            listView.setAdapter(adapter);
            widget2();
        }

        widget3();

        Button logoutButton = findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = DataBase.getUser();
                user.setAuthToken("-1");
                user.save();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Виджет 1.
    private void widget1() {
        ImageView button = findViewById(R.id.profile_icon_icon);
        RelativeLayout relativeLayout = findViewById(R.id.widget_wrap);
        ImageButton closeButton = findViewById(R.id.close_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // При нажатии на кнопку делаем виджет видимым
                relativeLayout.setVisibility(View.VISIBLE);
                widgetOfIconsStatus = 0;
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // При нажатии на кнопку делаем виджет видимым
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    // Виджет 2.
    private void widget2() {
        RelativeLayout relativeLayout = findViewById(R.id.widget_wrap);
        ListView listView = findViewById(R.id.family_members_list_view);

        RelativeLayout relativeLayout1 = findViewById(R.id.widget_wrap1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Получаем объект FamilyMember, соответствующий позиции в списке
                FamilyMember selectedFamilyMember = (FamilyMember) parent.getItemAtPosition(position);
                selectedFamMember = selectedFamilyMember;

                widgetOfIconsStatus = 1;

                relativeLayout1.setVisibility(View.VISIBLE);
                ImageView famProf = findViewById(R.id.widget_fam_prof_profile_icon);
                setProfileIcon(selectedFamilyMember.getProfilePhotoNumber(), famProf);

                EditText famName = findViewById(R.id.widget_fam_prof_name_form_input);
                famName.setText(selectedFamilyMember.getFirstName());

                EditText famSurmame = findViewById(R.id.widget_fam_prof_surname_form_input);
                famSurmame.setText(selectedFamilyMember.getLastName());

                ImageButton closeButton = findViewById(R.id.widget_fam_prof_close_button);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        relativeLayout1.setVisibility(View.INVISIBLE);
                    }
                });

                ImageView famWidgetProf = findViewById(R.id.widget_fam_prof_profile_icon);
                famWidgetProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // При нажатии на кнопку делаем виджет видимым
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                });

                Button saveButton = findViewById(R.id.widget_fam_prof_save);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText name = findViewById(R.id.widget_fam_prof_name_form_input);
                        EditText surname = findViewById(R.id.widget_fam_prof_surname_form_input);

                        if (name.getText().toString().isEmpty() || surname.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (FamilyMember member : DataBase.getFamilyMembers()) {
                            if (member.getFirstName().equals(selectedFamMember.getFirstName()) &&
                                    member.getLastName().equals(selectedFamMember.getLastName())) {
                                member.setFirstName(name.getText().toString());
                                member.setLastName(surname.getText().toString());
                                member.setProfilePhotoNumber(numOfIconForRealFam);
                                member.save();
                                FamilyMemberAdapter adapter = new FamilyMemberAdapter(ProfileActivity.this,
                                        R.layout.item_family_member, new ArrayList<>(DataBase.getFamilyMembers()));
                                ListView listView = findViewById(R.id.family_members_list_view);
                                listView.setAdapter(adapter);
                                relativeLayout1.setVisibility(View.INVISIBLE);
                                break;
                            }
                        }
                    }
                });

                Button deleteButton = findViewById(R.id.widget_fam_prof_delete);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (FamilyMember member : DataBase.getFamilyMembers()) {
                            if (member.getFirstName().equals(selectedFamMember.getFirstName()) &&
                                    member.getLastName().equals(selectedFamMember.getLastName())) {
                                member.delete();
                                if (DataBase.getFamilyMembers().isEmpty()) {
                                    TextView nullFamListText = findViewById(R.id.null_fam_list_text);
                                    nullFamListText.setVisibility(View.VISIBLE);
                                }
                                FamilyMemberAdapter adapter = new FamilyMemberAdapter(ProfileActivity.this,
                                        R.layout.item_family_member, new ArrayList<>(DataBase.getFamilyMembers()));
                                ListView listView = findViewById(R.id.family_members_list_view);
                                listView.setAdapter(adapter);

                                relativeLayout1.setVisibility(View.INVISIBLE);
                                break;
                            }
                        }
                    }
                });
//                Toast.makeText(getApplicationContext(), "Выбран член семьи: " + selectedFamilyMember.getFirstName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Виджет 3.
    private void widget3() {
        Button addFamButton = findViewById(R.id.add_fam_button);
        RelativeLayout addFamWidgetWrap = findViewById(R.id.add_fam_widget_wrap);
        addFamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamWidgetWrap.setVisibility(View.VISIBLE);
                widgetOfIconsStatus = 2;
                ImageView addFamWidgetProfileIcon = findViewById(R.id.add_fam_widget_profile_icon);
                EditText name = findViewById(R.id.add_fam_widget_name_form_input);
                EditText surname = findViewById(R.id.add_fam_widget_surname_form_input);
                setProfileIcon(0, addFamWidgetProfileIcon);
                name.setText("");
                surname.setText("");


                RelativeLayout widgetWrap = findViewById(R.id.widget_wrap);
                addFamWidgetProfileIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        widgetWrap.setVisibility(View.VISIBLE);
                        widgetWrap.setElevation(100);
                    }
                });

                ImageButton closeButton = findViewById(R.id.close_button);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        widgetWrap.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        ImageButton closeButton = findViewById(R.id.add_fam_widget_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamWidgetWrap.setVisibility(View.INVISIBLE);
            }
        });
        Button addButton = findViewById(R.id.add_fam_widget_save);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = findViewById(R.id.add_fam_widget_name_form_input);
                EditText surname = findViewById(R.id.add_fam_widget_surname_form_input);
                if (name.getText().toString().isEmpty() || surname.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_LONG).show();
                    return;
                }
                FamilyMember newFamMember = new FamilyMember(name.getText().toString(), surname.getText().toString(), numOfIconForNewFam);
                newFamMember.save();
                FamilyMemberAdapter adapter = new FamilyMemberAdapter(ProfileActivity.this,
                        R.layout.item_family_member, new ArrayList<>(DataBase.getFamilyMembers()));
                ListView listView = findViewById(R.id.family_members_list_view);
                listView.setAdapter(adapter);
                TextView nullFamListText = findViewById(R.id.null_fam_list_text);
                nullFamListText.setVisibility(View.INVISIBLE);
                addFamWidgetWrap.setVisibility(View.INVISIBLE);
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

    // Кнопка назад.
    public void goMain(View v) {
        EditText nameFormInput = findViewById(R.id.name_form_input);
        EditText surnameFormInput = findViewById(R.id.surname_form_input);
        if (nameFormInput.getText().toString().isEmpty() || surnameFormInput.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Поля не могут быть путыми", Toast.LENGTH_LONG).show();
            return;
        }
        User user = DataBase.getUser();
        user.setFirstName(nameFormInput.getText().toString());
        user.setLastName(surnameFormInput.getText().toString());
        user.save();
        setResult(Activity.RESULT_OK);
        finish();
    }

    // Выбор иконки.
    public void selectProfileIcon(View v) {
        if (widgetOfIconsStatus == 0) {
            User user = DataBase.getUser();
            if (v.getId() == R.id.profile_button1) {
                user.setProfilePhotoNumber(0);
            }
            if (v.getId() == R.id.profile_button2) {
                user.setProfilePhotoNumber(1);
            }
            if (v.getId() == R.id.profile_button3) {
                user.setProfilePhotoNumber(2);
            }
            if (v.getId() == R.id.profile_button4) {
                user.setProfilePhotoNumber(3);
            }
            if (v.getId() == R.id.profile_button5) {
                user.setProfilePhotoNumber(4);
            }
            if (v.getId() == R.id.profile_button6) {
                user.setProfilePhotoNumber(5);
            }
            user.save();
            ImageView profileIcon = findViewById(R.id.profile_icon_icon);
            setProfileIcon(user.getProfilePhotoNumber(), profileIcon);
        }
        if (widgetOfIconsStatus == 1) {

            int num = 0;
            if (v.getId() == R.id.profile_button1) {
                num = 0;
            }
            if (v.getId() == R.id.profile_button2) {
                num = 1;
            }
            if (v.getId() == R.id.profile_button3) {
                num = 2;
            }
            if (v.getId() == R.id.profile_button4) {
                num = 3;
            }
            if (v.getId() == R.id.profile_button5) {
                num = 4;
            }
            if (v.getId() == R.id.profile_button6) {
                num = 5;
            }
            numOfIconForRealFam = num;
            ImageView famWidgetProf = findViewById(R.id.widget_fam_prof_profile_icon);
            setProfileIcon(numOfIconForRealFam, famWidgetProf);

        }
        if (widgetOfIconsStatus == 2) {
            if (v.getId() == R.id.profile_button1) {
                numOfIconForNewFam = 0;
            }
            if (v.getId() == R.id.profile_button2) {
                numOfIconForNewFam = 1;
            }
            if (v.getId() == R.id.profile_button3) {
                numOfIconForNewFam = 2;
            }
            if (v.getId() == R.id.profile_button4) {
                numOfIconForNewFam = 3;
            }
            if (v.getId() == R.id.profile_button5) {
                numOfIconForNewFam = 4;
            }
            if (v.getId() == R.id.profile_button6) {
                numOfIconForNewFam = 5;
            }
            ImageView iconWidget = findViewById(R.id.add_fam_widget_profile_icon);
            setProfileIcon(numOfIconForNewFam, iconWidget);
        }
        RelativeLayout relativeLayout = findViewById(R.id.widget_wrap);
        relativeLayout.setVisibility(View.INVISIBLE);

    }

}






