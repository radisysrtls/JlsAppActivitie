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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DeviceAlertSetting extends AppCompatActivity {

    RecyclerView m_recyclerView;
    DeviceAlertAdapter m_deviceAlertAdapter;
    List<SettingsDetails> m_bleDetails = new ArrayList<SettingsDetails>();
    public static String HEXADDRRESS;


    public String getHexAddress(){
        return HEXADDRRESS;
    }

    public void addDeviceAlertItems() {
        SettingsDetails mainAlert = new SettingsDetails(getResources().getString(R.string.devicealert_header),"");
        SettingsDetails alertSettingsHeader = new SettingsDetails(getResources().getString(R.string.devicealert_alerts_header),"");
        SettingsDetails duration = new SettingsDetails(getResources().getString(R.string.devicealert_header_duration), getResources().getString(R.string.devicealert_header_duration_details));
        SettingsDetails repeat = new SettingsDetails(getResources().getString(R.string.devicealert_repeat), getResources().getString(R.string.devicealert_repeat_details));
        SettingsDetails alertNotificationHeader = new SettingsDetails(getResources().getString(R.string.devicealert_notifications_header),"");
        SettingsDetails reminder = new SettingsDetails(getResources().getString(R.string.devicealert_reminder_for_connection), getResources().getString(R.string.devicealert_reminder_for_connection_details));
        SettingsDetails alertOnReconnection = new SettingsDetails(getResources().getString(R.string.devicealert_alert_on_reconnection), getResources().getString(R.string.devicealert_alert_on_reconnection_details));
        m_bleDetails.add(mainAlert);
        m_bleDetails.add(alertSettingsHeader);
        m_bleDetails.add(duration);
        m_bleDetails.add(repeat);
        m_bleDetails.add(alertNotificationHeader);
        m_bleDetails.add(reminder);
        m_bleDetails.add(alertOnReconnection);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("ONCREATE","PHONEALERT");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_alert_recycle);
        Intent receiveIntent = this.getIntent();
        HEXADDRRESS = receiveIntent.getStringExtra("HEXADDR");

        m_recyclerView = (RecyclerView) findViewById(R.id.devicealert_recycle);
        addDeviceAlertItems();
        m_deviceAlertAdapter = new DeviceAlertAdapter(m_bleDetails, this, m_recyclerView);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_recyclerView.setAdapter(m_deviceAlertAdapter);
        ImageButton attachBack = (ImageButton) findViewById(R.id.devicealert_back);
        attachBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton deviceHome = (ImageButton) findViewById(R.id.devicealert_home);
        deviceHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //setResult(RESULT_CANCELED, intent);
                setResult(JioUtils.HOME_KEY, intent);
                finish();
            }
        });
    }
}

