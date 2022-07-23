package com.navn.safeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class CompanyListAdapter extends ArrayAdapter<CompanyListObject> {
    private Context context;
    private int resource;
    public CompanyListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CompanyListObject> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        String company_name = getItem(position).getCompanyName();
        String company_address = getItem(position).getCompanyAddress();
        String company_distance = getItem(position).getCompanyDistance();
        CompanyListObject companyListObject = new CompanyListObject(company_name,company_address,company_distance);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource,parent,false) ;

        TextView nameText = convertView.findViewById(R.id.company_name);
        TextView addressText = convertView.findViewById(R.id.company_address);
        TextView distanceText = convertView.findViewById(R.id.company_distance) ;
        ImageView image = convertView.findViewById(R.id.location_image);

        nameText.setText(company_name);
        addressText.setText(company_address);


        if(Float.parseFloat(company_distance)<0){
            ConstraintLayout constraintLayout = (ConstraintLayout) convertView.findViewById(R.id.result_list_layout);


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(image.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 42);
            constraintSet.applyTo(constraintLayout);
            image.setScaleX(1.75f);
            image.setScaleY(1.75f);
            distanceText.setVisibility(View.INVISIBLE);
        }else{
            distanceText.setText(new DecimalFormat("0.00").format(Double.valueOf(company_distance)) + " mi");
        }
        image.setImageResource(R.drawable.logo);

        return convertView;

    }
}
