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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutapp.util.JioConstant;

public class JioTagDeviceInfoActivity extends AppCompatActivity {
    TextView deviceName;
    TextView deviceNumber;
    TextView deviceImei;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_from_qrcode);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(JioConstant.DEVICE_INFO_TITLE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        Button attachDevice = findViewById(R.id.attach_btn);
        title.setTypeface(JioUtils.mTypeface(this, 5));
        deviceName = findViewById(R.id.jiotag_device_name);
        deviceNumber = findViewById(R.id.jiotag_device_number);
        deviceImei = findViewById(R.id.jiotag_device_imei);
        Intent intent = getIntent();
        String qrValue = intent.getStringExtra("QRCodeValue");
        setNameNumberImei(qrValue);
        attachDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JioUtils.setQRScanflag(JioTagDeviceInfoActivity.this,true);
                Intent intent = new Intent(JioTagDeviceInfoActivity.this,MainActivity.class);
                //intent.putExtra("ScanningProcess","Yes");
                startActivity(intent);
            }
        });

    }
    private void setNameNumberImei(String qrValue) {
        if(qrValue != null){
            String []splitString = qrValue.split("\n");
            String name = splitString[0];
            String number = splitString[1];
            String imeiNumber = splitString[2];
            deviceName.setText(name);
            deviceNumber.setText(number);
            deviceImei.setText(imeiNumber);

        }

    }
}
