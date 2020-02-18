// (c) Copyright 2020 by Reliance Jio infocomm Ltd. All rights reserved.
package com.jio.devicetracker.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.SearchDevice;
import com.jio.devicetracker.database.pojo.Userdata;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.request.SearchDeviceRequest;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.database.pojo.response.SearchDeviceResponse;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * Implementation of Splash Screen.This class creates splash screen for JFF application
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText jioEmailEditText = null;
    private EditText jioPasswordEditText = null;
    public static LogindetailResponse logindetailResponse = null;
    public static SearchDeviceResponse searchdeviceResponse = null;
    private static final int PERMIT_ALL = 1;
    private DBManager mDbManager;
    private List<SubscriptionInfo> subscriptionInfos;
    public static boolean isReadPhoneStatePermissionGranted = false;
    public static boolean isAccessCoarsePermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.LOGIN_TITLE);
        Button loginButton = findViewById(R.id.login);
        jioEmailEditText = findViewById(R.id.jioEmailId);
        jioPasswordEditText = findViewById(R.id.jioPassword);
        TextView registerText = findViewById(R.id.registedHere);
        TextView forgetPass = findViewById(R.id.clickForget);
        registerText.setOnClickListener(this);
        forgetPass.setOnClickListener(this);
        mDbManager = new DBManager(this);
        boolean termConditionsFlag = Util.getTermconditionFlag(this);
        @SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
        String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMIT_ALL);
        }

        jioEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!jioEmailEditText.getText().toString().equals("") || !jioPasswordEditText.getText().toString().equals("")) {
                    loginButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                    loginButton.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailId = jioEmailEditText.getText().toString();
                if (emailId.isEmpty()) {
                    loginButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    loginButton.setTextColor(Color.WHITE);
                }
            }
        });

        loginButton.setOnClickListener(v -> {
            onLoginButtonClick();
        });

        checkTermandCondition(termConditionsFlag);
    }

    private void onLoginButtonClick() {
        String jioEmailIdText = jioEmailEditText.getText().toString().trim();
        String jioPasswordText = jioPasswordEditText.getText().toString().trim();
        if (jioEmailEditText.length() == 0) {
            jioEmailEditText.setError(Constant.EMAILID_VALIDATION);
            return;
        }
        if (jioPasswordText.length() == 0) {
            jioPasswordEditText.setError(Constant.PASSWORD_VALIDATION);
            return;
        }

        if (jioEmailEditText.length() != 0) {
            if (Util.isValidEmailId(jioEmailIdText)) {
                makeMQTTConnection();
                Userdata data = new Userdata();
                data.setEmailId(jioEmailIdText);
                data.setPassword(jioPasswordText);
                data.setType(Constant.SUPERVISOR);
                RequestHandler.getInstance(getApplicationContext()).handleRequest(new LoginDataRequest(new SuccessListener(), new ErrorListener(), data));
            } else {
                jioEmailEditText.setError(Constant.EMAIL_VALIDATION);
                return;
            }
        }
        Util.getInstance().showProgressBarDialog(this);
    }

    /**
     * Checks the DeepLinking URI which tracker receives from tracee
     */
    private void deepLinkingURiCheck() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.toString().contains("home")) {
            String number = data.toString().substring(data.toString().length() - 10);
            showDialog(number);
        }
    }

    public void showDialog(String number) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_display_dialog);
        dialog.setTitle(Constant.TITLE);
        dialog.getWindow().setLayout(1000, 500);
        // set the custom dialog components - text, image and button
        final Button yes = dialog.findViewById(R.id.positive);
        Button no = dialog.findViewById(R.id.negative);
        yes.setOnClickListener(v -> {
            String phoneNumber = null;
            if (subscriptionInfos != null) {
                phoneNumber = subscriptionInfos.get(0).getNumber();
            }
            new SendSMSTask().execute(number, Constant.YESJFF_SMS + phoneNumber.trim().substring(2, phoneNumber.length()));
            dialog.dismiss();
        });

        no.setOnClickListener(v -> {
            String phoneNumber = null;
            if (subscriptionInfos != null) {
                phoneNumber = subscriptionInfos.get(0).getNumber();
            }
            new SendSMSTask().execute(number, Constant.NOJFF_SMS + phoneNumber.trim().substring(2, phoneNumber.length()));
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * Creates the MQTT connection with MQTT server
     */
    private void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        mqttManager.getMQTTClient(this);
        mqttManager.connetMQTT();
    }

    public boolean hasPermissions(Context context, String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registedHere:
                gotoRegisterScreen();
                break;
            case R.id.clickForget:
                gotoForgetPassTokenScreen();
                break;
            default:
                break;
        }
    }

    private void gotoForgetPassTokenScreen() {
        Intent intent = new Intent(this, ForgetpassTokenActivity.class);
        intent.putExtra("Email", jioEmailEditText.getText().toString().trim());
        startActivity(intent);
    }

    private class SuccessListener implements Response.Listener {

        @Override
        public void onResponse(Object response) {
            logindetailResponse = Util.getInstance().getPojoObject(String.valueOf(response), LogindetailResponse.class);
            if (logindetailResponse.getUgsToken() != null) {
                new DBManager(LoginActivity.this).insertLoginData(logindetailResponse);
            }
            SearchDevice data = new SearchDevice();
            List<String> userAssignedList = new ArrayList<>();
            userAssignedList.add(logindetailResponse.getUser().getId());
            data.setUsersAssigned(userAssignedList);
            RequestHandler.getInstance(getApplicationContext()).handleRequest(new SearchDeviceRequest(new SuccessListenerSearchDevice(), new ErrorListenerSearchDevice(), logindetailResponse.getUgsToken(), data));
        }
    }

    private class SuccessListenerSearchDevice implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            List<AddedDeviceData> mlist = new ArrayList<>();
            searchdeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchDeviceResponse.class);
            AddedDeviceData data;
            List<SearchDeviceResponse.SearchDeviceData> deviceData = searchdeviceResponse.getmData();
            for (SearchDeviceResponse.SearchDeviceData devData : deviceData) {
                data = new AddedDeviceData();
                data.setPhoneNumber(devData.getPhoneNumber());
                data.setName(devData.getName());
                data.setImeiNumber(devData.getImeiNumber());
                mlist.add(data);
            }
            Util.setAutologinStatus(LoginActivity.this, true);
            mDbManager.insertInBorqsDB(mlist, jioEmailEditText.getText().toString().trim());
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            Util.progressDialog.dismiss();
        }
    }

    private class ErrorListenerSearchDevice implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.INVALID_USER) {
                Util.alertDilogBox(Constant.LOGIN_VALIDATION, Constant.ALERT_TITLE, LoginActivity.this);
            } else if (error.networkResponse.statusCode == Constant.ACCOUNT_LOCK) {
                Util.alertDilogBox(Constant.EMAIL_LOCKED, Constant.ALERT_TITLE, LoginActivity.this);
            } else {
                Util.alertDilogBox(Constant.VALID_USER, Constant.ALERT_TITLE, LoginActivity.this);
            }
            Util.progressDialog.dismiss();
        }
    }

    private void gotoRegisterScreen() {
        Intent intent = new Intent(this, RegistrationDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Checks privacy policy flag to show privacy policy screen
     */
    private void checkTermandCondition(boolean termConditionsFlag) {
        if (!termConditionsFlag) {
            checkTermConditionStatus();
        } else {
            deepLinkingURiCheck();
        }
    }

    private void checkTermConditionStatus() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(Constant.ALERT_TITLE);
        adb.setMessage(Constant.TERM_AND_CONDITION_STATUS_MSG);
        adb.setPositiveButton("OK", (dialog, which) -> {

            goToSplashnActivity();
        });

        adb.show();
    }

    private void goToSplashnActivity() {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMIT_ALL: {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    isReadPhoneStatePermissionGranted = false;
                }
                if (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    isAccessCoarsePermissionGranted = false;
                }
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    isReadPhoneStatePermissionGranted = true;
                }
                if (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    isAccessCoarsePermissionGranted = true;
                }
                subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                // If request is cancelled, the result arrays are empty.
                for (int grantResult : grantResults) {
                    if (grantResults.length > 0 && grantResult == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission is granted");
                    }
                }
                break;
            }
            default:
                break;
        }
    }
}