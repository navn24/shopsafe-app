package com.navn.safeshop;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.navn.safeshop.requests.GetAverageReviews;

import java.util.concurrent.ExecutionException;


public class RatingsFragment extends Fragment {


    private InfoToPass infoObject;
    RatingBar ratingBar1 ;
    RatingBar ratingBar2 ;
    RatingBar ratingBar3 ;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    Button leaveReviewButton;
    private GetAverageReviews getAverageReviewData;
    public RatingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment RatingsFragment.
     */
    public static RatingsFragment newInstance( InfoToPass infoObject) {
        RatingsFragment fragment = new RatingsFragment();
        Bundle args = new Bundle();
       args.putSerializable( "InfoToPassObj", infoObject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            infoObject = (InfoToPass) getArguments().getSerializable("InfoToPassObj");
        }
        getAverageReviewData = new GetAverageReviews(infoObject.getCompany_id(),getContext());
        try {
            getAverageReviewData.execute().get();
            System.out.println("Company Average Review: " + getAverageReviewData.getCompany_average_review());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView =  inflater.inflate(R.layout.fragment_ratings, container, false);
        ratingBar1 = (RatingBar) inflatedView.findViewById(R.id.ratingBar1);
        ratingBar2 = (RatingBar) inflatedView.findViewById(R.id.ratingBar2);
        ratingBar3 = (RatingBar) inflatedView.findViewById(R.id.ratingBar3);
        textView1 = inflatedView.findViewById(R.id.text_category_1);
        textView2 = inflatedView.findViewById(R.id.text_category_2);
        textView3 = inflatedView.findViewById(R.id.text_category_3);
        leaveReviewButton = inflatedView.findViewById(R.id.detailsLeaveReview);
        ratingBar1.setRating(getAverageReviewData.getAverage_review__1());
        ratingBar2.setRating(getAverageReviewData.getAverage_review__2());
        ratingBar3.setRating(getAverageReviewData.getAverage_review__3());
        textView1.setText(Float.toString(ratingBar1.getRating()));
        textView2.setText(Float.toString(ratingBar2.getRating()));
        textView3.setText(Float.toString(ratingBar3.getRating()));
        leaveReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LeaveReviewActivity.class);
                intent.putExtra("InfoToPassObj", infoObject) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return inflatedView;

    }
}