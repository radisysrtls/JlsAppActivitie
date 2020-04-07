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

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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

import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddedDeviceData;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.GetDeviceLocationData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.SearchDevice;
import com.jio.devicetracker.database.pojo.Userdata;
import com.jio.devicetracker.database.pojo.request.GetDeviceLocationRequest;
import com.jio.devicetracker.database.pojo.request.LoginDataRequest;
import com.jio.devicetracker.database.pojo.request.SearchDeviceRequest;
import com.jio.devicetracker.database.pojo.response.GetDeviceLocationResponse;
import com.jio.devicetracker.database.pojo.response.LogindetailResponse;
import com.jio.devicetracker.database.pojo.response.SearchDeviceResponse;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.MessageListener;
import com.jio.devicetracker.network.MessageReceiver;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of Splash Screen.This class creates splash screen for JFF application
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {

    private EditText jioMobileNumberEditText = null;
    private EditText jioUserNameEditText = null;
    private static EditText jioMobileOtp = null;
    private AdminLoginData adminData;
    public static String phoneNumber = null;
    public static LogindetailResponse logindetailResponse = null;
    public static SearchDeviceResponse searchdeviceResponse = null;
    private static final int PERMIT_ALL = 1;
    private String name;
    private String mbNumber;
    private String imei;
    private String number;
    public static String userName;
    private DBManager mDbManager;
    private List<SubscriptionInfo> subscriptionInfos;
    public static boolean isReadPhoneStatePermissionGranted = false;
    public static boolean isAccessCoarsePermissionGranted = false;
    private Button loginButton;
    private Locale locale = Locale.ENGLISH;
    public static GetDeviceLocationResponse getDeviceLocationResponse = null;
    private Integer otpGeneratedValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        setContentView(R.layout.activity_login);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.LOGIN_TITLE);
        loginButton = findViewById(R.id.login);
        jioUserNameEditText = findViewById(R.id.userName);
        jioMobileNumberEditText = findViewById(R.id.jioNumber);
        jioMobileNumberEditText.setEnabled(false);
        jioMobileNumberEditText.setOnClickListener(this);
        adminData = new AdminLoginData();
        mDbManager = new DBManager(this);
        boolean termConditionsFlag = Util.getTermconditionFlag(this);
        jioMobileOtp = findViewById(R.id.otpDetail);
        loginButton.setOnClickListener(this);
        MessageListener messageListener = new LoginActivity();
        MessageReceiver.bindListener(messageListener);
        loginButton.setOnClickListener(v -> {
            onLoginButtonClick();
        });
        fetchMobileNumber();
        checkTermandCondition(termConditionsFlag);
    }

    /**
     * Change the button color, when user enters something
     */
    private void fetchMobileNumber() {
        jioMobileNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused empty method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.login_selector, null));
                loginButton.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                number = jioMobileNumberEditText.getText().toString();
                if (number.isEmpty()) {
                    loginButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector, null));
                    loginButton.setTextColor(Color.WHITE);
                } else {
                    jioMobileNumberEditText.setError(null);
                }
                generateOTP();
            }
        });
    }

    /**
     * Generates Random number(OTP)
     */
    private void generateOTP() {
        otpGeneratedValue = (int) (Math.random() * 9000) + 1000;
        sendSMSMessage(otpGeneratedValue);
    }

    /**
     * Sends OTP to the user mobile number
     *
     * @param randomNumberForOTP
     */
    protected void sendSMSMessage(int randomNumberForOTP) {
        String phoneNo = number;
        String message = Constant.OTP_MESSAGE + randomNumberForOTP;

        if (0 == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), Constant.OTP_SENT,
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    Constant.SMS_SEND_FAILED, Toast.LENGTH_LONG).show();
            return;
        }

    }

    /**
     * Gets called when you click on login button
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            validateNumber();
        }
    }

    /*
     * Validate mobile number
     */
    private void validateNumber() {
        if (!(jioMobileOtp.getText().toString().equals(String.valueOf(otpGeneratedValue)))) {
            jioMobileOtp.setError("Invalid OTP Provided");
        }
        if (jioMobileNumberEditText.getText().toString().equals("")) {
            jioMobileNumberEditText.setError(Constant.PHONE_VALIDATION);
        } else {
            getssoToken();
        }
    }

    private void getssoToken() {
        boolean isAvailable = Util.isMobileNetworkAvailable(this);
        if (isAvailable) {
            checkJiooperator();
        } else {
            Util.alertDilogBox(Constant.MOBILE_NETWORKCHECK, Constant.ALERT_TITLE, this);
        }
    }

    /**
     * Checks the Jio SIM in slot 1 when user enters the mobile number.
     */
    private void checkJiooperator() {
        phoneNumber = jioMobileNumberEditText.getText().toString();
        if (subscriptionInfos != null) {
            String carierName = subscriptionInfos.get(0).getCarrierName().toString();
            String number = subscriptionInfos.get(0).getNumber();

            if (number != null
                    && (number.equals(phoneNumber) || number.equals("91" + phoneNumber))) {
                if (!carierName.contains(Constant.JIO)) {
                    Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
                }
            } else if (subscriptionInfos.size() == 2 && subscriptionInfos.get(1).getNumber() != null) {
                if (subscriptionInfos.get(1).getNumber().equals("91" + phoneNumber) || subscriptionInfos.get(1).getNumber().equals(phoneNumber)) {
                    Util.alertDilogBox(Constant.NUMBER_VALIDATION, Constant.ALERT_TITLE, this);
                } else {
                    Util.alertDilogBox(Constant.DEVICE_JIONUMBER, Constant.ALERT_TITLE, this);
                }
            } else {
                Util.alertDilogBox(Constant.DEVICE_JIONUMBER, Constant.ALERT_TITLE, this);
            }
        }
    }


    /**
     * Checks the Jio SIM in slot 1 automatically
     */
    public void checkJioSIMSlot1() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
        if (subscriptionInfos != null) {
            String carrierNameSlot1 = subscriptionInfos.get(0).getCarrierName().toString();
            if (!carrierNameSlot1.toLowerCase(locale).contains(Constant.JIO)) {
                Util.alertDilogBox(Constant.SIM_VALIDATION, Constant.ALERT_TITLE, this);
            } else {
                jioMobileNumberEditText.setText(subscriptionInfos.get(0).getNumber());
            }
        }
    }

    // Request for SMS and Phone Permissions
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS}, PERMIT_ALL);
        }
    }

    /**
     * Called when you click on Login button
     */
    private void onLoginButtonClick() {
        String compareOtp = String.valueOf(otpGeneratedValue);
        if (!jioMobileOtp.getText().toString().equals(compareOtp)) {
            jioMobileOtp.setError(Constant.INVALID_OTP);
            return;
        }
        if (jioUserNameEditText.length() == 0) {
            jioUserNameEditText.setError(Constant.NAME_VALIDATION);
            return;
        }
        userName = jioUserNameEditText.getText().toString();
//        String jioEmailIdText = jioEmailEditText.getText().toString().trim();
//        String jioPasswordText = jioPasswordEditText.getText().toString().trim();

//        if (jioUserNameEditText.length() == 0) {
//            jioUserNameEditText.setError(Constant.NAME_VALIDATION);
//        }

//        if (jioEmailEditText.length() == 0) {
//            jioEmailEditText.setError(Constant.EMAILID_VALIDATION);
//            return;
//        }
//        if (jioPasswordText.length() == 0) {
//            jioPasswordEditText.setError(Constant.PASSWORD_VALIDATION);
//            return;
//        }

//        if (jioEmailEditText.length() != 0) {
//            if (Util.isValidEmailId(jioEmailIdText)) {
        makeMQTTConnection();
        Userdata data = new Userdata();
        data.setEmailId(Constant.ADMIN_EMAIL_ID);
        data.setPassword(Constant.ADMIN_PASSWORD);
        data.setType(Constant.SUPERVISOR);
        RequestHandler.getInstance(getApplicationContext()).handleRequest(new LoginDataRequest(new SuccessListener(), new ErrorListener(), data));
        Util.getInstance().showProgressBarDialog(this);
    }

    /**
     * Login successful Listener
     */
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

    /**
     * Login unsuccessful Listener
     */
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

    /**
     * Checks the DeepLinking URI which tracker receives from tracee
     */
    private void deepLinkingURiCheck() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.toString().contains("home")) {
            String[] splitStr = data.toString().split("=");
            String[] splitNamenumber = splitStr[1].split("&");
            mbNumber = splitNamenumber[0];
            name = splitNamenumber[1];
            imei = splitNamenumber[2];
            showDialog(mbNumber);
        }
    }

    /**
     * Display Dialog when you click on Deep link URI
     */
    public void showDialog(String number) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_display_dialog);
        dialog.setTitle(Constant.TITLE);
        dialog.getWindow().setLayout(1000, 500);
        // set the custom dialog components - text, image and button
        final Button yes = dialog.findViewById(R.id.positive);
        Button no = dialog.findViewById(R.id.negative);
        yes.setOnClickListener(v -> {
            //serviceCallLogin();
            String phoneNumber = null;
            if (subscriptionInfos != null) {
                phoneNumber = subscriptionInfos.get(0).getNumber();
            }
            new SendSMSTask().execute(mbNumber, Constant.YESJFF_SMS + phoneNumber.trim().substring(2, phoneNumber.length()));
            mDbManager.updateConsentInDeviceBors(mbNumber, Constant.CONSENT_APPROVED_STATUS);
            dialog.dismiss();
        });

        no.setOnClickListener(v -> {
            String phoneNumber = null;
            if (subscriptionInfos != null) {
                phoneNumber = subscriptionInfos.get(0).getNumber();
            }
            new SendSMSTask().execute(number, Constant.NOJFF_SMS + phoneNumber.trim().substring(2, phoneNumber.length()));
            mDbManager.updateConsentInDeviceBors(mbNumber, Constant.REQUEST_CONSENT);
            dialog.dismiss();
        });
        dialog.show();
    }

    // Gets called when app receives message
    @Override
    public void messageReceived(String message, String phoneNum) {
        String[] splitmessage = message.split(":");
        if (message.contains(Constant.OTP_MESSAGE) && jioMobileOtp != null) {
            jioMobileOtp.setText(splitmessage[1]);
        }
    }

    /**
     * Creates the MQTT connection with MQTT server
     */
    private void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        mqttManager.getMQTTClient(this);
        mqttManager.connetMQTT();
    }

    /*
     * Search device Success Listener(Gets all the device available under that particular user)
     */
    private class SuccessListenerSearchDevice implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            List<HomeActivityListData> mlist = new ArrayList<>();
            searchdeviceResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchDeviceResponse.class);
            List<SearchDeviceResponse.SearchDeviceData> deviceData = searchdeviceResponse.getmData();
            for (SearchDeviceResponse.SearchDeviceData devData : deviceData) {
                HomeActivityListData data = new HomeActivityListData();
                data.setPhoneNumber(devData.getPhoneNumber());
                data.setName(devData.getName());
                String consentStatus = mDbManager.getConsentStatusBorqs(devData.getPhoneNumber());
                data.setConsentStaus(consentStatus);
                data.setImeiNumber(devData.getImeiNumber());
                data.setDeviceId(devData.getDeviceId());
                mlist.add(data);
            }
            Util.setAutologinStatus(LoginActivity.this, true);
            adminData = mDbManager.getAdminLoginDetail();
            mDbManager.insertInBorqsDB(mlist, adminData.getEmail());
            List<HomeActivityListData> getallDeviceData = mDbManager.getAllDevicedata(adminData.getEmail());
            for (HomeActivityListData getDeviceId : getallDeviceData) {
                RequestHandler.getInstance(getApplicationContext()).handleRequest(new GetDeviceLocationRequest(new SuccessListenerDeviceLocation(), new ErrorListenerDeviceLocation(), logindetailResponse.getUgsToken(), getDeviceId.getDeviceId()));
            }
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            Util.getInstance().dismissProgressBarDialog();
        }
    }

    /**
     * this class is handling the getDevice location api's success response
     */
    private class SuccessListenerDeviceLocation implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            getDeviceLocationResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetDeviceLocationResponse.class);
            if (getDeviceLocationResponse != null && getDeviceLocationResponse.getData().getDeviceStatus().getLocation() != null) {
                GetDeviceLocationData latlangdata = new GetDeviceLocationData();
                latlangdata.setDeviceId(getDeviceLocationResponse.getData().getPhoneNumber());
                latlangdata.setLat(getDeviceLocationResponse.getData().getDeviceStatus().getLocation().getLat());
                latlangdata.setLang(getDeviceLocationResponse.getData().getDeviceStatus().getLocation().getLng());
                mDbManager.updateLatLangInBorqsDB(getDeviceLocationResponse.getData().getPhoneNumber(), latlangdata);
            }
        }
    }

    /**
     * this class is handling the getDevice location api's error response
     */
    private class ErrorListenerDeviceLocation implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.getInstance().dismissProgressBarDialog();
        }
    }

    /**
     * this class is handling the search device api's error response
     */
    private class ErrorListenerSearchDevice implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.getInstance().dismissProgressBarDialog();
        }
    }

    /*private void gotoRegisterScreen() {
        Intent intent = new Intent(this, RegistrationDetailActivity.class);
        startActivity(intent);
    }*/

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
        if (PERMIT_ALL == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
            checkJioSIMSlot1();
        }
        switch (requestCode) {
            case PERMIT_ALL: {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    isReadPhoneStatePermissionGranted = false;
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    isAccessCoarsePermissionGranted = false;
                }
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    isReadPhoneStatePermissionGranted = true;
                }

                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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