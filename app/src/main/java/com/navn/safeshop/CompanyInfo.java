package com.navn.safeshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.platform.MaterialContainerTransform;

public class CompanyInfo extends AppCompatActivity {
    ViewPager2 viewPager ;
    InfoToPass infoObject;
    String companyName;
    PagerAdapter pagerAdapter;
    public static final String FINISH_ALERT = "finish_alert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.registerReceiver(this.finishAlert, new IntentFilter(FINISH_ALERT));

        findViewById(android.R.id.content).setTransitionName("sharedElementTest");
        getWindow().setSharedElementEnterTransition(new MaterialContainerTransform().addTarget(android.R.id.content));
        getWindow().setSharedElementReturnTransition(new MaterialContainerTransform().addTarget(android.R.id.content));

        Bundle extras  = getIntent().getExtras() ;
        infoObject = (InfoToPass) getIntent().getSerializableExtra("InfoToPassObj");

        companyName = infoObject.getCompany_name();
        String shortAddress = infoObject.getCompany_address().substring(0, infoObject.getCompany_address().indexOf(','));
        getSupportActionBar().setTitle(companyName + " - " + shortAddress);







         final TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem review_tab = findViewById(R.id.reviews_tab);
        TabItem ratings_tab = findViewById(R.id.ratings_tab);
        viewPager= findViewById(R.id.viewPager);
          pagerAdapter = new PagerAdapter(getSupportFragmentManager(),getLifecycle(),tabLayout.getTabCount());
          pagerAdapter.saveState();
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
        viewPager.setOffscreenPageLimit(2);

        if(getIntent().hasExtra("ComingFromLeaveReview")==true){
            //Pass this down to MainActivity Fragment along with infoObject
            pagerAdapter.setInfoObject(infoObject);
            pagerAdapter.setComingFromLeaveReview(true);
        }else if(getIntent().hasExtra("ComingFromLeaveReview")==false){
            //Pass this down to MainActivity Fragment along with infoObject
            pagerAdapter.setInfoObject(infoObject);
            pagerAdapter.setComingFromLeaveReview(false);
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.leave_review_menu,menu);
        return true ;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leave_a_review:
                Intent intent = new Intent(this,LeaveReviewActivity.class);
                intent.putExtra("InfoToPassObj", infoObject) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            case android.R.id.home:
                if(!isTaskRoot()){
                    onBackPressed();
                }else{
                    Intent i  = new Intent(this, MapsActivity.class);
                    i.putExtra("InfoToPassObj", infoObject);
                    startActivity(i);
                    finish();
                }
            default:
                //Default action
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    BroadcastReceiver finishAlert = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            CompanyInfo.this.finish();
        }
    };

    @Override
    public void onDestroy() {

        super.onDestroy();
        this.unregisterReceiver(finishAlert);
    }
}