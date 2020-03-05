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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.TrackerdeviceData;
import com.jio.devicetracker.database.pojo.request.TrackdeviceRequest;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;


import java.io.Serializable;
import java.util.List;

/**
 * Implementation of  Screen.This class creates splash screen for JFF application
 */
public class TrackDeviceActivity extends AppCompatActivity implements View.OnClickListener {
    private TrackerdeviceResponse mtrackerresponse;
    private String userToken =null;
    private EditText phoneNumberEditText = null;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_device);
        toolbar = findViewById(R.id.trackeeToolbar);
        setSupportActionBar(toolbar);
        Intent bundle = getIntent();
        userToken = bundle.getStringExtra(Constant.USER_TOKEN);
        phoneNumberEditText = findViewById(R.id.trackeeDeviceNumber);
        Button trackDevice = findViewById(R.id.trackDevice);
        trackDevice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (phoneNumberEditText.getText().toString().length() == 0) {
            phoneNumberEditText.setError(Constant.PHONENUMBER_VALIDATION);
        }
        else{
            getServicecall();
        }
    }

    private void getServicecall() {
        TrackerdeviceData data =new TrackerdeviceData();
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new TrackdeviceRequest(new SuccessListener(), new ErrorListener(),userToken,data));
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            mtrackerresponse = Util.getInstance().getPojoObject(String.valueOf(response), TrackerdeviceResponse.class);
            List<TrackerdeviceResponse.Data> data= mtrackerresponse.getmData();
            gotoDashboard(data);
        }
    }

    private void gotoDashboard(List<TrackerdeviceResponse.Data> phoneList) {
        Intent intent = new Intent(this,DashboardActivity.class);
        intent.putExtra("PhoneList",(Serializable)phoneList);
        startActivity(intent);
    }

    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Unused empty method
        }
    }
}