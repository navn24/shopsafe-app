package com.navn.safeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserReviewAdapter extends ArrayAdapter<UserReviewObject> {
    private Context context;
    private int resource;
    public UserReviewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<UserReviewObject> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String user_name = getItem(position).getUser_name();
        String comment_text = getItem(position).getComment_text();
        float user_average_review = getItem(position).getUser_average_review();
        String date = getItem(position).getDate();
        UserReviewObject userReviewObject = new UserReviewObject(user_name,comment_text,user_average_review,date);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource,parent,false) ;

        TextView nameText = convertView.findViewById(R.id.user_name_text);
        TextView commentText = convertView.findViewById(R.id.user_comment_text);
        RatingBar userRating = convertView.findViewById(R.id.user_review_rating) ;
        TextView dateText = convertView.findViewById(R.id.date);

        nameText.setText(user_name);
        commentText.setText(comment_text);
        userRating.setRating(user_average_review);
        dateText.setText(date);

        return convertView;

    }
}
