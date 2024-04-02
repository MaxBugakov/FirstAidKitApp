package com.example.myapplication2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ActiveMedicamentsFragment extends Fragment {

    private ListView listView;
    private MedicamentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_active_medicaments, container, false);

        listView = view.findViewById(R.id.active_medicaments_list_view);

        // Загрузка данных.
        List<Medicament> allMedicaments = Medicament.listAll(Medicament.class);

        List<Medicament> validMedicaments = null;
        // Фильтрация данных.
        if (FirstAidKitActivity.medicineMode == 0) {
            validMedicaments = filterValidMedicaments0(allMedicaments);
        }
        else {
            validMedicaments = filterValidMedicaments1(allMedicaments);
        }

        if (validMedicaments.isEmpty()) {
            TextView textView = view.findViewById(R.id.null_active_medicaments_list_text);
            textView.setVisibility(View.VISIBLE);
        }
        else {
            TextView textView = view.findViewById(R.id.null_active_medicaments_list_text);
            textView.setVisibility(View.INVISIBLE);
            // Создание адаптера и настройка ListView
            adapter = new MedicamentAdapter(getContext(), validMedicaments);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Medicament selectedMedicament = (Medicament) parent.getItemAtPosition(position);
                    if (getActivity() instanceof OnMedicamentSelectedListener) {
                        ((OnMedicamentSelectedListener) getActivity()).onMedicamentSelected(selectedMedicament);
                    }
                }
            });
        }
        return view;
    }

    public interface OnMedicamentSelectedListener {
        void onMedicamentSelected(Medicament medicament);
    }

    private List<Medicament> filterValidMedicaments0(List<Medicament> medicaments) {
        List<Medicament> validMedicaments = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date today = new Date();
        for (Medicament medicament : medicaments) {
            try {
                Date expirationDate = sdf.parse(medicament.getExpirationDate());
                if (expirationDate != null && expirationDate.after(today)) {
                    validMedicaments.add(medicament);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(validMedicaments, new Comparator<Medicament>() {
            @Override
            public int compare(Medicament m1, Medicament m2) {
                return m1.getTitle().substring(0, 1).compareTo(m2.getTitle().substring(0, 1));
            }
        });
        return validMedicaments;
    }

    private List<Medicament> filterValidMedicaments1(List<Medicament> medicaments) {
        List<Medicament> validMedicaments = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date today = new Date();
        for (Medicament medicament : medicaments) {
            try {
                Date expirationDate = sdf.parse(medicament.getExpirationDate());
                if (!(expirationDate != null && expirationDate.after(today))) {
                    validMedicaments.add(medicament);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(validMedicaments, new Comparator<Medicament>() {
            @Override
            public int compare(Medicament m1, Medicament m2) {
                return m1.getTitle().substring(0, 1).compareTo(m2.getTitle().substring(0, 1));
            }
        });
        return validMedicaments;
    }
}