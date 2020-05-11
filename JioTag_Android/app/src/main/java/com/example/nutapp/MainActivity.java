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

package com.example.nutapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    boolean m_isLocOn=false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case JioUtils.REQUEST_CHECK_SETTINGS_MAIN:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        m_isLocOn=true;
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        m_isLocOn=false;
                        break;
                    default:
                        m_isLocOn=false;
                        break;
                }
                displayLocationToast(m_isLocOn);
              break;

            default:
                break;
        }
    }

    public void displayLocationToast(boolean isLocOn){
        Intent intent2 = new Intent();
        intent2.setAction("com.nutapp.notifications_maps_home_key");
        sendBroadcast(intent2);
        if(isLocOn) {
            Toast.makeText(getApplicationContext(), "Location Turned On", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(), "Location Turned Off", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
/*        finishAffinity();
        finish();*/
    moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        setNavigationData();
        String scanprocess = intent.getStringExtra("ScanningProcess");
        /*if(!scanprocess.equalsIgnoreCase("Yes")) {*/


            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            /*getSupportActionBar().setCustomView(R.layout.toolbar);*/
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);

       // }
    }

    private void setNavigationData() {
        NavigationView navigationView = findViewById(R.id.nv);
        View header = navigationView.getHeaderView(0);
        //user_account_name = header.findViewById(R.id.user_account_name);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.notification:
                        gotoNotificationActivity();
                        break;
                    case R.id.slientmode:
                        gotoSlientModeActivity();
                        break;
                    case R.id.settings:
                        gotoSettingsActivity();
                        break;
                    case R.id.howtoadd:
                         goToHowtoAddActivity();
                        break;
                    case R.id.information:
                        goToInformationActivity();
                        break;
                    case R.id.feedback:
                        goToFeedbackActivity();
                        break;
                    case R.id.logout:
                        //updateLogoutData();
                        break;


                    default:
                        return true;
                }
                return true;
            }
        });
    }

    private void gotoNotificationActivity() {
        Intent intent = new Intent(this,NotificationActivity.class);
        startActivity(intent);
    }

    private void goToFeedbackActivity() {
        Intent intent = new Intent(this,Feedback.class);
        startActivity(intent);
    }

    private void goToInformationActivity() {
        Intent intent = new Intent(this,Information.class);
        startActivity(intent);
    }

    private void gotoSettingsActivity() {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    private void goToHowtoAddActivity() {

        Intent intent = new Intent(this,Howtoadd.class);
        startActivity(intent);
    }

    public void gotoSlientModeActivity(){
        Intent intent = new Intent(this,SlientModeActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        JioUtils.clearQRScanflag(this);
    }
}
