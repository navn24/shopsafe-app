package com.navn.safeshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {
    private int numOfTabs;
    private InfoToPass infoObject;
    private boolean comingFromLeaveReview;
    private FragmentManager fragmentManager;
    private Bundle fragmentBundle;
    public PagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lf, int numOfTabs){
        super(fm,lf);
        fragmentManager = fm;
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        System.out.println("YO:" + position);
        switch (position){
            case 0:
                    return MainActivity.newInstance(infoObject,comingFromLeaveReview);
            case 1:
                return RatingsFragment.newInstance(infoObject);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }

    public void setInfoObject(InfoToPass infoObject) {
        this.infoObject = infoObject;
    }

    public void setComingFromLeaveReview(boolean comingFromLeaveReview) {
        this.comingFromLeaveReview = comingFromLeaveReview;
    }

}
