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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.AddDeviceData;
import com.jio.devicetracker.database.pojo.RegisterRequestData;
import com.jio.devicetracker.database.pojo.request.AddDeviceRequest;
import com.jio.devicetracker.database.pojo.request.RegistrationTokenrequest;
import com.jio.devicetracker.database.pojo.response.RegistrationResponse;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

/**
 * Implementation of Registration screen for admin registration.
 */
public class RegistrationActivity extends Activity implements View.OnClickListener, MessageListener {

    private EditText mName;
    private EditText mPhone;
    private int permissionRequestCode = 100;
    private Button mRegister;
    private String phoneNumber;
    private String countryCode;
    private static EditText tokenEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initializeDataMember();
        changeButtonColorAfterTextEnter();
        requestPermission();
    }

    /**
     * Use to initialize datamember
     */
    private void initializeDataMember() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.REGISTRATION_TITLE);
        mName = findViewById(R.id.memberName);
        mPhone = findViewById(R.id.deviceNumber);
        mRegister = findViewById(R.id.register);
        tokenEditText = findViewById(R.id.tokenEditText);
        mRegister.setOnClickListener(this);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        countryCode = intent.getStringExtra("countryCode");
        mPhone.setText(countryCode+phoneNumber);
        MessageListener messageListener = new RegistrationActivity();
        MessageReceiver.bindListener(messageListener);
    }

    /**
     * Change Button color after entring value in edittext
     */
    private void changeButtonColorAfterTextEnter() {
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mName.getText().toString().equals("")) {
                    mRegister.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    mRegister.setTextColor(Color.WHITE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String emailId = mName.getText().toString();
                if (emailId.isEmpty()) {
                    mRegister.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    mRegister.setTextColor(Color.WHITE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.register) {
            validate();
        }
    }

    /**
     * Validation check for the fields
     */
    private void validate() {
        if (mName.getText().toString().length() == 0) {
            mName.setError(Constant.NAME_EMPTY);
            return;
        }
        if (mPhone.getText().toString().length() == 0) {
            mPhone.setError(Constant.MOBILE_NUMBER_EMPTY);
            return;
        }

        if (mPhone.getText().toString().length() < 10) {
            mPhone.setError(Constant.VALID_PHONE_NUMBER);
            return;
        }

        getServicecall();
    }

    /**
     * Register API call
     */
    private void getServicecall() {
        String name = mName.getText().toString();
        RegisterRequestData registerRequestData = new RegisterRequestData();
        RegisterRequestData.Token token = new RegisterRequestData().new Token();
        token.setValue(tokenEditText.getText().toString().trim());
        RegisterRequestData.MetaProfile metaProfile = new RegisterRequestData().new MetaProfile();
        metaProfile.setName(name);
        registerRequestData.setName(name);
        registerRequestData.setPhone(phoneNumber);
        registerRequestData.setPhoneCountryCode(countryCode);
        registerRequestData.setMetaprofile(metaProfile);
        registerRequestData.setToken(token);
        Util.getInstance().showProgressBarDialog(this, Constant.LOADING_DATA);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new RegistrationTokenrequest(new SuccessListener(), new ErrorListener(), registerRequestData));
    }

    /**
     * To receive token
     * @param message
     * @param phoneNum
     */
    public void messageReceived(String message, String phoneNum) {
        if(message.contains(Constant.TOKEN_SMS) && tokenEditText != null) {
            String[] splitMessage = message.split(":");
            tokenEditText.setText(splitMessage[1].substring(1, 6));
        }
    }

    /**
     * Register API call success listener
     */
    private class SuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            RegistrationResponse registrationResponse = Util.getInstance().getPojoObject(String.valueOf(response), RegistrationResponse.class);
            if(registrationResponse.getCode() == Constant.SUCCESS_CODE_200 && registrationResponse.getMessage().equalsIgnoreCase(Constant.REGISTARTION_SUCCESS_MESSAGE)) {
                Toast.makeText(RegistrationActivity.this, Constant.REGISTARTION_SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        }
    }

    /**
     * Register API call error listener
     */
    private class ErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.alertDilogBox(Constant.REGISTRAION_ALERT_409, Constant.ALERT_TITLE, RegistrationActivity.this);
            } else {
                Util.alertDilogBox(Constant.REGISTRAION_FAILED, Constant.ALERT_TITLE, RegistrationActivity.this);
            }
        }
    }

    /**
     * Request for permission
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, permissionRequestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }
}
