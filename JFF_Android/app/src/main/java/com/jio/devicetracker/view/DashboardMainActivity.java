/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.
 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.DashboardAdapter;

public class DashboardMainActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DashboardAdapter dashboardAdapter;
    private TabItem tabPeople;
    private TabItem tabGroups;
    private TabItem tabDevices;
    private ImageView addGroupInDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_main);
        initUI();
    }

    private void initUI() {
        tabLayout = findViewById(R.id.tablayout);
        tabPeople = findViewById(R.id.tabPeople);
        tabGroups = findViewById(R.id.tabGroups);
        tabDevices = findViewById(R.id.tabDevices);
        viewPager = findViewById(R.id.viewPager);
        addGroupInDashboard = findViewById(R.id.addGroupInDashboard);
        addGroupInDashboard.setOnClickListener(this);
        dashboardAdapter = new DashboardAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(dashboardAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        onTabClicked();
    }

    private void onTabClicked() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setText(Constant.GROUP_WITH_DOT);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(1).setText(Constant.PEOPLE_WITH_DOT);
                } else {
                    tabLayout.getTabAt(2).setText(Constant.DEVICES_WITH_DOT);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setText(Constant.GROUP_WITHOUT_DOT);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(1).setText(Constant.PEOPLE_WITHOUT_DOT);
                } else {
                    tabLayout.getTabAt(2).setText(Constant.DEVICES_WITHOUT_DOT);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addGroupInDashboard) {
        switch (tabLayout.getSelectedTabPosition()){
            case 0 :
                startActivity(new Intent(this, CreateGroupActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, AddPeopleActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, QRReaderInstruction.class));
                break;
        }
        }
    }
}
