package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CourseAdapter extends ArrayAdapter<Course> {
    private Context context;
    private List<Course> courses;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public CourseAdapter(Context context, List<Course> courses) {
        super(context, 0, courses);
        this.context = context;
        this.courses = courses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        }
        Course course = getItem(position);

        ImageView courseIco = convertView.findViewById(R.id.course_icon);
        TextView courseProfile = convertView.findViewById(R.id.course_profile);
        TextView pillsName = convertView.findViewById(R.id.course_pills_name);
        TextView courseDates = convertView.findViewById(R.id.course_dates);
        TextView coursePercent = convertView.findViewById(R.id.course_percent);

        String[] nameArray = course.getPatient().split("\\s+");
        if (DataBase.getUser().getFirstName().equals(nameArray[0].trim()) && DataBase.getUser().getLastName().equals(nameArray[1].trim())) {
            setProfileIcon(DataBase.getUser().getProfilePhotoNumber(), courseIco);
        }
        else {
            for (FamilyMember fam : DataBase.getFamilyMembers()) {
                if (fam.getFirstName().equals(nameArray[0].trim()) && fam.getLastName().equals(nameArray[1].trim())) {
                    setProfileIcon(fam.getProfilePhotoNumber(), courseIco);
                    break;
                }
            }
        }
        courseProfile.setText(course.getPatient());
        pillsName.setText(course.getMedicine());
        courseDates.setText(course.getStartDate() + " - " + course.getEndDate());
        int completionPercentage = calculateCourseCompletionPercentage(course.getStartDate(), course.getEndDate());
        coursePercent.setText(completionPercentage + "%");

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

    // Рассчёт прохождения курса.
    public static int calculateCourseCompletionPercentage(String startDateStr, String endDateStr) {
        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            Date currentDate = new Date();

            // Если текущая дата меньше даты начала, возвращаем 0%
            if (currentDate.before(startDate)) {
                return 0;
            }

            // Если текущая дата больше даты окончания, возвращаем 100%
            if (currentDate.after(endDate)) {
                return 100;
            }

            // Вычисляем процент прохождения
            long totalDuration = endDate.getTime() - startDate.getTime();
            long passedDuration = currentDate.getTime() - startDate.getTime();

            return (int)((double) passedDuration / totalDuration * 100);

        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // В случае ошибки парсинга возвращаем 0%
        }
    }


}