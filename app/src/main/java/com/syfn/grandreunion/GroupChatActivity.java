package com.syfn.grandreunion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class GroupChatActivity extends AppCompatActivity {

    private TextView hubLabel, fundLabel, membersLabel, eventLabel;
    private ViewPager mMainPager;
    private PagerViewAdapter mPagerViewAdapter;
    private String currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentGroupName = getIntent().getStringExtra("groupName");
        getSupportActionBar().setTitle(currentGroupName);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hubLabel = findViewById(R.id.hubMenuOption);
        fundLabel = findViewById(R.id.fundMenuOption);
        membersLabel = findViewById(R.id.membersMenuOption);
        eventLabel = findViewById(R.id.eventsMenuOption);
        mMainPager = findViewById(R.id.mainPager);
        mPagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapter);
        mMainPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        hubLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(0);
            }
        });
        fundLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(1);
            }
        });
        eventLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(2);
            }
        });
        membersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPager.setCurrentItem(3);
            }
        });
    }

    private void changeTabs(int position) {
        if(position == 0){
            hubLabel.setTextColor(Color.BLACK);
            hubLabel.setTextSize(20);
            fundLabel.setTextColor(Color.DKGRAY);
            fundLabel.setTextSize(15);
            eventLabel.setTextColor(Color.DKGRAY);
            eventLabel.setTextSize(15);
            membersLabel.setTextColor(Color.DKGRAY);
            membersLabel.setTextSize(15);
        }
        if(position == 1){
            hubLabel.setTextColor(Color.DKGRAY);
            hubLabel.setTextSize(15);
            fundLabel.setTextColor(Color.BLACK);
            fundLabel.setTextSize(20);
            eventLabel.setTextColor(Color.DKGRAY);
            eventLabel.setTextSize(15);
            membersLabel.setTextColor(Color.DKGRAY);
            membersLabel.setTextSize(15);
        }
        if(position == 2){
            hubLabel.setTextColor(Color.DKGRAY);
            hubLabel.setTextSize(15);
            fundLabel.setTextColor(Color.DKGRAY);
            fundLabel.setTextSize(15);
            eventLabel.setTextColor(Color.BLACK);
            eventLabel.setTextSize(20);
            membersLabel.setTextColor(Color.DKGRAY);
            membersLabel.setTextSize(15);
        }
        if(position == 3){
            hubLabel.setTextColor(Color.DKGRAY);
            hubLabel.setTextSize(15);
            fundLabel.setTextColor(Color.DKGRAY);
            fundLabel.setTextSize(15);
            eventLabel.setTextColor(Color.DKGRAY);
            eventLabel.setTextSize(15);
            membersLabel.setTextColor(Color.BLACK);
            membersLabel.setTextSize(20);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            Intent gcToGroupList = new Intent(GroupChatActivity.this, LoggedInActivity.class);
            startActivity(gcToGroupList);
        }
        return super.onOptionsItemSelected(item);
    }
}
