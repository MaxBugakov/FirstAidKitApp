package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class FamilyMemberAdapter extends ArrayAdapter<FamilyMember> {

    private Context mContext;
    private int mResource;

    public FamilyMemberAdapter(Context context, int resource, ArrayList<FamilyMember> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FamilyMember familyMember = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        // Lookup view for data population
        ImageView imageView = convertView.findViewById(R.id.imageViewFamProf);
        setProfileIcon(familyMember.getProfilePhotoNumber(), imageView);

        TextView textViewFirstName = convertView.findViewById(R.id.textViewFirstName);

        // Populate the data into the template view using the data object
//        imageView.setImageResource(familyMember.getImageResource());
        textViewFirstName.setText(familyMember.getFirstName() + " " + familyMember.getLastName());

        // Return the completed view to render on screen
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
