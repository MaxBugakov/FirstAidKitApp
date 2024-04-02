package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class CourseNowAdapter extends ArrayAdapter<Course> {
    private Context context;
    private List<Course> courses;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public CourseNowAdapter(Context context, List<Course> courses) {
        super(context, 0, courses);
        this.context = context;
        this.courses = courses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course_now, parent, false);
        }
        Course course = getItem(position);

        ImageView courseIco = convertView.findViewById(R.id.course_icon);
        TextView courseProfile = convertView.findViewById(R.id.course_profile);
        TextView pillsName = convertView.findViewById(R.id.course_pills_name);
        TextView courseQuantity = convertView.findViewById(R.id.course_pills_quantity);
        TextView courseEat = convertView.findViewById(R.id.course_eat);
        TextView courseTime = convertView.findViewById(R.id.course_time);

        String[] nameArray = course.getPatient().split("\\s+");
        if (DataBase.getUser().getFirstName().equals(nameArray[0].trim()) && DataBase.getUser().getLastName().equals(nameArray[1].trim())) {
            setProfileIcon(DataBase.getUser().getProfilePhotoNumber(), courseIco);
        } else {
            for (FamilyMember fam : DataBase.getFamilyMembers()) {
                if (fam.getFirstName().equals(nameArray[0].trim()) && fam.getLastName().equals(nameArray[1].trim())) {
                    setProfileIcon(fam.getProfilePhotoNumber(), courseIco);
                    break;
                }
            }
        }

        String typeQuantity = "";
        if (course.getType().equals("Таблетки")) {
            typeQuantity = "таб";
        }
        else if (course.getType().equals("Капсулы")) {
            typeQuantity = "кап";
        }
        else if (course.getType().equals("Уколы")) {
            typeQuantity = "ук";
        }
        else {
            typeQuantity = "шт";
        }

        courseProfile.setText(course.getPatient());
        pillsName.setText(course.getMedicine());
        courseQuantity.setText((int)course.getSingleDose() + " " + typeQuantity);
        courseEat.setText(course.getIntakeRelativeToFood());
        courseTime.setText(course.getIntake());

        return convertView;
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