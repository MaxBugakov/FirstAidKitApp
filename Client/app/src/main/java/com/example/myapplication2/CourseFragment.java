package com.example.myapplication2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CourseFragment extends Fragment {
    private ListView listView;
    private CourseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course, container, false);

        listView = view.findViewById(R.id.course_list_view);

        // Загрузка данных.
        List<Course> allCourses = Course.listAll(Course.class);

        List<Course> validCourses = null;
        // Фильтрация данных.
        if (CourseActivity.mode == 0) {
            Log.d("PrintLine", "We in 00000");
            validCourses = filterValidCourses0(allCourses);
        }
        else {
            validCourses = filterValidCourses1(allCourses);
        }

        if (validCourses.isEmpty()) {
            TextView textView = view.findViewById(R.id.null_course_list_text);
            textView.setVisibility(View.VISIBLE);
        }
        else {
            TextView textView = view.findViewById(R.id.null_course_list_text);
            textView.setVisibility(View.INVISIBLE);
            // Создание адаптера и настройка ListView
            adapter = new CourseAdapter(getContext(), validCourses);
            listView.setAdapter(adapter);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Medicament selectedMedicament = (Medicament) parent.getItemAtPosition(position);
//                    if (getActivity() instanceof ActiveMedicamentsFragment.OnMedicamentSelectedListener) {
//                        ((ActiveMedicamentsFragment.OnMedicamentSelectedListener) getActivity()).onMedicamentSelected(selectedMedicament);
//                    }
//                }
//            });
        }
        return view;
    }

    private List<Course> filterValidCourses0(List<Course> courses) {
        List<Course> validCourses = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date today = new Date();
        for (Course course : courses) {
            try {
                Date expirationDate = sdf.parse(course.getEndDate());
                if (expirationDate != null && expirationDate.after(today)) {
                    validCourses.add(course);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return validCourses;
    }

    private List<Course> filterValidCourses1(List<Course> courses) {
        List<Course> validCourses = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date today = new Date();
        for (Course course : courses) {
            try {
                Date expirationDate = sdf.parse(course.getEndDate());
                if (!(expirationDate != null && expirationDate.after(today))) {
                    validCourses.add(course);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return validCourses;
    }
}