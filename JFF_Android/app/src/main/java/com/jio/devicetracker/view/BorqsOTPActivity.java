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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.RegisterData;
import com.jio.devicetracker.database.pojo.request.FMSRegistrationToken;
import com.jio.devicetracker.database.pojo.request.FMSRegistrationToken.Token;
import com.jio.devicetracker.database.pojo.request.FMSRegistrationTokenRequest;
import com.jio.devicetracker.database.pojo.request.FMSVerifyToken;
import com.jio.devicetracker.database.pojo.request.FMSVerifyTokenRequest;
import com.jio.devicetracker.database.pojo.response.FMSRegistrationTokenResponse;
import com.jio.devicetracker.database.pojo.response.FMSVerifyTokenResponse;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

/**
 * Implementation of Registration OTP screen to verify the email OTP.
 */
public class BorqsOTPActivity extends AppCompatActivity implements View.OnClickListener {

    private Button verify = null;
    private EditText emailOTP = null;
    private String emailOtp = "";
    RegisterData registerData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borqs_otp);

        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.BORQS_OTP_TITLE);
        emailOTP = findViewById(R.id.emailOTP);
        verify = findViewById(R.id.verify);
        verify.setOnClickListener(this);
        getAdminDetail();

        emailOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused Method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!emailOTP.getText().toString().equals("")) {
                    verify.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.login_selector,null));
                    verify.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailId = emailOTP.getText().toString();
                if (emailId.isEmpty()) {
                    verify.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selector,null));
                    verify.setTextColor(Color.WHITE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(R.id.verify == v.getId()){
            validation();
        }
    }

    private void validation() {
        if(emailOTP.getText().toString().equals("")){
            emailOTP.setError(Constant.EMPTY_EMAIL_OTP);
            return;
        }
        verifyEmailOTP();
    }

    private void getAdminDetail() {
        DBManager dbManager = new DBManager(getApplicationContext());
        registerData = dbManager.getAdminRegistrationDetail();
    }

    private void verifyEmailOTP() {
        emailOtp = emailOTP.getText().toString();
        makeEmailVerifyAPICall(emailOtp);
    }

    private void makeEmailVerifyAPICall(String emailOTP) {
        FMSVerifyToken fmsVerifyToken = new FMSVerifyToken();
        fmsVerifyToken.setEmail(registerData.getEmail().trim());
        fmsVerifyToken.setToken(emailOTP.trim());
        fmsVerifyToken.setType(Constant.REGISTRATION);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new FMSVerifyTokenRequest(new BorqsOTPActivity.SuccessListener(), new BorqsOTPActivity.ErrorListener(), fmsVerifyToken));
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            FMSVerifyTokenResponse fmsVerifyTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), FMSVerifyTokenResponse.class);
            if (fmsVerifyTokenResponse.getMessage().equalsIgnoreCase(Constant.TOKEN_VERIFIED)) {
                FMSRegistrationToken fmsRegistrationToken = new FMSRegistrationToken();
                fmsRegistrationToken.setEmail(registerData.getEmail().trim());
                fmsRegistrationToken.setDob(registerData.getDob());
                fmsRegistrationToken.setName(registerData.getName().trim());
                fmsRegistrationToken.setPassword(registerData.getPassword());
                fmsRegistrationToken.setPhone(registerData.getPhoneNumber().trim());
                Token token = new FMSRegistrationToken().new Token();
                token.setValue(emailOtp);
                fmsRegistrationToken.setToken(token);
                RequestHandler.getInstance(getApplicationContext()).handleRequest(new FMSRegistrationTokenRequest(new BorqsOTPActivity.RegistrationTokenSuccessListener(), new BorqsOTPActivity.RegistrationTokenErrorListener(), fmsRegistrationToken));
            }else {
                Toast.makeText(getApplicationContext(), Constant.TOKEN_VERIFICATION_FAILED, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), Constant.TOKEN_VERIFICATION_FAILED, Toast.LENGTH_SHORT).show();
        }
    }

    private class RegistrationTokenSuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            FMSRegistrationTokenResponse fmsRegistrationTokenResponse = Util.getInstance().getPojoObject(String.valueOf(response), FMSRegistrationTokenResponse.class);
            if(fmsRegistrationTokenResponse.getCode() == Constant.SUCCESS_CODE_200) {
                goToLoginActivity();
                Toast.makeText(getApplicationContext(), Constant.REGISTARTION_SUCCESS_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RegistrationTokenErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), Constant.REGISTARTION_FAILED_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

}