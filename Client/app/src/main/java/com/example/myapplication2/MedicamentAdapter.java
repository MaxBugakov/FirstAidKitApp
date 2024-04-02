package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class MedicamentAdapter extends ArrayAdapter<Medicament> {
    private Context context;
    private List<Medicament> medicaments;

    public MedicamentAdapter(Context context, List<Medicament> medicaments) {
        super(context, 0, medicaments);
        this.context = context;
        this.medicaments = medicaments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_medicament, parent, false);
        }

        Medicament medicament = getItem(position);

        TextView nameTextView = convertView.findViewById(R.id.medicament_name);
        TextView expirationDateTextView = convertView.findViewById(R.id.medicament_expiration_date);
        TextView quantityTextView = convertView.findViewById(R.id.medicament_quantity);

        nameTextView.setText(medicament.getTitle());
        expirationDateTextView.setText(String.format("Годен до: %s", medicament.getExpirationDate()));
        String typeQuantity = "";
        if (medicament.getType().equals("Таблетки")) {
            typeQuantity = "таб";
        }
        else if (medicament.getType().equals("Капсулы")) {
            typeQuantity = "кап";
        }
        else if (medicament.getType().equals("Уколы")) {
            typeQuantity = "ук";
        }
        else {
            typeQuantity = "шт";
        }
        quantityTextView.setText(String.valueOf(medicament.getQuantity()) + " " + typeQuantity);

        return convertView;
    }
}
