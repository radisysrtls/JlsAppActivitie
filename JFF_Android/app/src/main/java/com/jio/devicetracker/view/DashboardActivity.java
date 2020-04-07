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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.jio.devicetracker.R;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AdminLoginData;
import com.jio.devicetracker.database.pojo.GroupData;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.database.pojo.SearchDeviceStatusData;
import com.jio.devicetracker.database.pojo.request.SearchDeviceStatusRequest;
import com.jio.devicetracker.database.pojo.response.SearchDeviceStatusResponse;
import com.jio.devicetracker.database.pojo.response.TrackerdeviceResponse;
import com.jio.devicetracker.network.MQTTManager;
import com.jio.devicetracker.network.RequestHandler;
import com.jio.devicetracker.network.SendSMSTask;
import com.jio.devicetracker.util.ConsentTimeUpdate;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.TrackerDeviceListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Implementation of Dashboard Screen to show the trackee list and hamburger menu.
 */
public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView listView;
//    private static String userToken = null;
    public static List<MultipleselectData> selectedData;
    public static List<TrackerdeviceResponse.Data> data;
    private TrackerDeviceListAdapter adapter;
    public static List<String> consentListPhoneNumber = null;
    private static DBManager mDbManager;
    public static Map<String, Map<Double, Double>> namingMap = null;
    private static Context context = null;
//    public static String adminEmail;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView user_account_name = null;
    private List<SubscriptionInfo> subscriptionInfos;
    private Locale locale = Locale.ENGLISH;
    private static int batteryLevel;
    private static FusedLocationProviderClient client;
    private DrawerLayout drawerLayout;
    private static Double latitude;
    private static Double longitude;
    private static int signalStrengthValue;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private WorkManager mWorkManager = null;
    private static Thread thread = null;
    public static List<GroupData> specificGroupMemberData = null;
    public static List<HomeActivityListData> listOnHomeScreens;
    public static String groupName = "";
    public static boolean isAddIndividual = false;
    public static boolean isComingFromGroupList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = getIntent();
        //consentRequestBox(intent.getBooleanExtra("flag", false), intent.getStringExtra("name"));
        //isPermissionGranted();
        setLayoutData();
        setNavigationData();
        initializeDataMember();
        checkPermission();
        Util.getAdminDetail(this);
        setConstaint();
//        startService();
        registerReceiver();
        deepLinkingURICheck();
        addDataInHomeScreen();
        adapterEventListener();
    }

   /* private void isPermissionGranted() {
        if (LoginActivity.isAccessCoarsePermissionGranted == false) {
            Util.alertDilogBox(Constant.ACCESS_COARSE_PERMISSION_ALERT, Constant.ALERT_TITLE, this);
        }
    }*/


    private void adapterEventListener() {
        if (adapter != null) {
            adapter.setOnItemClickPagerListener(new TrackerDeviceListAdapter.RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position, MultipleselectData data, int val) {
                    if (val == 2) {
                        selectedData.add(data);
                    } else if (val == 3) {
                        Iterator<MultipleselectData> iterator = selectedData.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getPhone().equalsIgnoreCase(data.getPhone())) {
                                iterator.remove();
                            }
                        }
                    } else {
                        List<MultipleselectData> groupData = mDbManager.getGroupLatLongdata(data.getName());
                        for (MultipleselectData groupDataLatLon : groupData) {
                            selectedData.add(groupDataLatLon);
                        }
                    }
                }

                @Override
                public void clickonListLayout(String selectedGroupName) {
                    groupName = selectedGroupName;
                    startActivity(new Intent(DashboardActivity.this, GroupListActivity.class));
                }

                @Override
                public void consetClick(String phoneNumber) {
                    showSpinnerforConsentTime(phoneNumber);
                }

                @Override
                public void consentClickForGroup(String selectedGroupName) {
                    groupName = selectedGroupName;
                    List<HomeActivityListData> data = mDbManager.getGroupdata(groupName);
                    showSpinnerforGroupConsentTime(data);
                }

                @Override
                public void onPopupMenuClicked(View v, int position, String name, String number) {
                    PopupMenu popup = new PopupMenu(DashboardActivity.this, v);
                    popup.inflate(R.menu.options_menu);
                    popup.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.editOnCardView:
                                gotoEditScreen(name, number);
                                break;
                            case R.id.deleteOnCardView:
                                alertDilogBoxWithCancelbtn(Constant.DELETC_DEVICE, Constant.ALERT_TITLE, number, position);
                                break;
                            default:
                                break;
                        }
                        return false;
                    });
                    popup.show();
                }

            });
        }
    }

    private void initializeDataMember() {
        selectedData = new ArrayList<>();
        mWorkManager = WorkManager.getInstance();
        consentListPhoneNumber = new LinkedList<>();
        namingMap = new HashMap<>();
        Util.getInstance().getIMEI(this);
        mDbManager = new DBManager(this);
        thread = new Thread(new RefreshMap());
        //thread.start();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(broadcastreceiver, intentFilter);
        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        client = LocationServices.getFusedLocationProviderClient(this);
        new Thread(new SendLocation()).start();
        if (specificGroupMemberData == null) {
            specificGroupMemberData = new ArrayList<>();
        }
        if (listOnHomeScreens == null) {
            listOnHomeScreens = new ArrayList<>();
        }
    }

    private void setNavigationData() {
        NavigationView navigationView = findViewById(R.id.nv);
        View header = navigationView.getHeaderView(0);
        user_account_name = header.findViewById(R.id.user_account_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.profile:
                    gotoProfileActivity();
                    break;
                case R.id.settings:
                    goToRefreshIntervalSettingActivity();
                    break;
                case R.id.helpPrivacy:
//                        Toast.makeText(DashboardActivity.this, "Help & Support", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.activeSessions:
                    gotoActiveSessionActivity();
                    break;
                case R.id.home:
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    break;
                case R.id.logout:
                    updateLogoutData();
                    break;
                default:
                    return true;
            }
            return true;
        });
    }


    private void gotoActiveSessionActivity() {
        Intent intent = new Intent(this, ActiveSessionActivity.class);
        startActivity(intent);
    }
    /*private void gotoTrackerListScreen() {
        startActivity(new Intent(this, TrackerListActivity.class));
    }*/

    private void setLayoutData() {
        Toolbar toolbar = findViewById(R.id.customToolbar);
        listView = findViewById(R.id.listView);
        FloatingActionButton fabCreateGroup = findViewById(R.id.createGroup);
        FloatingActionButton fabAddDevice = findViewById(R.id.addDevice);
        FloatingActionButton fabAddContact = findViewById(R.id.addContact);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(linearLayoutManager);
        Button trackBtn = toolbar.findViewById(R.id.track);
        trackBtn.setVisibility(View.VISIBLE);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.DASHBOARD_TITLE);
        trackBtn.setOnClickListener(this);
        fabCreateGroup.setOnClickListener(this);
        fabAddDevice.setOnClickListener(this);
        fabAddContact.setOnClickListener(this);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        context = getApplicationContext();
    }

    private void goToRefreshIntervalSettingActivity() {
        startActivity(new Intent(this, RefreshIntervalSettingActivity.class));
    }

   /* private void startService() {
        Intent serviceIntent = new Intent(this, SendLocationService.class);
        serviceIntent.putExtra("inputExtra", getString(R.string.notification_subtitle));
        ContextCompat.startForegroundService(this, serviceIntent);
    }*/

    public void alertDilogBoxWithCancelbtn(String message, String title, String phoneNumber, int position) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", (dialog, which) -> {
            mDbManager.deleteSelectedData(phoneNumber);
            adapter.removeItem(position);
            isDevicePresent();
        });
        adb.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        adb.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastreceiver != null) {
            unregisterReceiver(broadcastreceiver);
        }
        if (mRecevier != null) {
            unregisterReceiver(mRecevier);
        }
    }

    // Broadcast to calculate battery strength
    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryLevel = (int) (((float) level / (float) scale) * 100.0f);
        }
    };

    /****** To get the lat and long of the current device*******/
    private void getLocation() {
        if (client != null) {
            client.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            });
        }
    }

    private void gotoProfileActivity() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    private void updateLogoutData() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(Constant.ALERT_TITLE);
        adb.setMessage(Constant.LOGOUT_CONFIRMATION_MESSAGE);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", (dialog, which) -> {
            Util.clearAutologinstatus(this);
            mDbManager.updateLogoutData();
            goToLoginActivity();
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adb.show();
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            user_account_name.setText(Util.userName.substring(0, 1).toUpperCase(locale) + Util.userName.substring(1));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***** Publish the MQTT message along with battery level, signal strength and time format*********/
    public void publishMessage() {
        getLocation();
        String message = "{\"imi\":\"" + Util.imeiNumber + "\",\"evt\":\"GPS\",\"dvt\":\"JioDevice_g\",\"alc\":\"0\",\"lat\":\"" + latitude + "\",\"lon\":\"" + longitude + "\",\"ltd\":\"0\",\n" +
                "\"lnd\":\"0\",\"dir\":\"0\",\"pos\":\"A\",\"spd\":\"" + 12 + "\",\"tms\":\"" + Util.getInstance().getMQTTTimeFormat() + "\",\"odo\":\"0\",\"ios\":\"0\",\"bat\":\"" + batteryLevel + "\",\"sig\":\"" + signalStrengthValue + "\"}";
        String topic = "jioiot/svcd/jiophone/" + Util.imeiNumber + "/uc/fwd/locinfo";
        new MQTTManager().publishMessage(topic, message);
    }

    // Navigates to the Edit screen by carrying name and number
    private void gotoEditScreen(String name, String phonenumber) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(Constant.NAME, name);
        intent.putExtra(Constant.NUMBER_CARRIER, phonenumber);
        startActivity(intent);
    }

    // Checks device is present or not, if not present show the help text otherwise display the added devices
    private void isDevicePresent() {
        TextView devicePresent = findViewById(R.id.devicePresent);
        List<HomeActivityListData> alldata = mDbManager.getAlldata(Util.adminEmail);
        if (alldata.isEmpty()) {
            listView.setVisibility(View.INVISIBLE);
            devicePresent.setVisibility(View.VISIBLE);
        } else {
            devicePresent.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    // Gets called when we click on button
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createGroup:
                checkNumberOfGroups();
                break;
            case R.id.track:
                trackDevice();
                break;
            case R.id.addDevice:
                gotoQRScannerScreen();
                break;
            case R.id.addContact:
                checkNumberOfIndividualUser();
                break;
            default:
//                Log.d("TAG", "Some other button is clicked");
                break;
        }
    }

    // Count number of individual aaded user, If it is more than 10 then display alert dialog
    private void checkNumberOfIndividualUser() {
        int individualUserCount = 1;
        List<HomeActivityListData> allDevicedata = mDbManager.getAllDevicedata(Util.adminEmail);
        for (HomeActivityListData homeActivityListData : allDevicedata) {
            if (homeActivityListData.isGroupMember() == false) {
                individualUserCount++;
            }
        }
        if (individualUserCount > 10) {
            Util.alertDilogBox(Constant.USER_LIMITATION, "Jio Alert", this);
        } else {
            gotoContactsDetailsActivity();
        }
    }

    // Count number of created groups, If it is more than 2 then display alert dialog
    private void checkNumberOfGroups() {
        List<HomeActivityListData> allDevicedata = mDbManager.getAlldata(Util.adminEmail);
        int groupCount = 1;
        for (HomeActivityListData homeActivityListData : allDevicedata) {
            if (homeActivityListData.isGroupMember() == true && homeActivityListData.getImeiNumber() == null) {
                groupCount++;
            }
        }

        if (groupCount > 2) {
            Util.alertDilogBox(Constant.GROUP_LIMITATION, "Jio Alert", this);
            return;
        } else {
            gotoGroupNameActivity();
        }
    }

    // Navigates to the Group name acivity
    private void gotoGroupNameActivity() {
        isAddIndividual = false;
        isComingFromGroupList = true;
        startActivity(new Intent(this, GroupNameActivity.class));
    }

    // Navigates to the ContactDetailsActivity activity by counting Add device screen
    private void gotoQRScannerScreen() {
        isAddIndividual = false;
        isComingFromGroupList = false;
        groupName = null;
        int individualUserCount = 1;

        List<HomeActivityListData> allDevicedata = mDbManager.getAllDevicedata(Util.adminEmail);
        for (HomeActivityListData homeActivityListData : allDevicedata) {
            if (homeActivityListData.isGroupMember() == false && homeActivityListData.getImeiNumber() != null) {
                individualUserCount++;
            }
        }
        if (individualUserCount > 10) {
            Util.alertDilogBox(Constant.USER_LIMITATION, "Jio Alert", this);
        } else {
            startActivity(new Intent(this, ContactDetailsActivity.class));
        }
    }

    private void gotoContactsDetailsActivity() {
        isAddIndividual = true;
        isComingFromGroupList = false;
        groupName = null;
        startActivity(new Intent(this, ContactDetailsActivity.class));
    }

    /***** Track the device ***********/
    private void trackDevice() {
        if (selectedData.isEmpty()) {
            Util.alertDilogBox(Constant.CHOOSE_DEVICE, Constant.ALERT_TITLE, this);
        } else {
            /*AdminLoginData adminLoginDetail = mDbManager.getAdminLoginDetail();
            List<String> data = new ArrayList<>();
            data.add(adminLoginDetail.getUserId());*/

            /*SearchDeviceStatusData searchDeviceStatusData = new SearchDeviceStatusData();
            SearchDeviceStatusData.Device device = searchDeviceStatusData.new Device();
            device.setUsersAssigned(data);
            searchDeviceStatusData.setDevice(device);*/

            Util.getInstance().showProgressBarDialog(this);
            List<HomeActivityListData> hmActivityListData = mDbManager.getAlldata(Util.adminEmail);
            boolean isConsentApproved = false;
            for (HomeActivityListData homeActivityListData : hmActivityListData) {
                for (MultipleselectData multipleselectData : selectedData) {
                    if (multipleselectData.getPhone().equalsIgnoreCase(homeActivityListData.getPhoneNumber()) && homeActivityListData.getDeviceType().equalsIgnoreCase("People Tracker")) {
                        if (homeActivityListData.getConsentStaus().equalsIgnoreCase("") || homeActivityListData.getConsentStaus().equalsIgnoreCase(Constant.CONSENT_PENDING) || homeActivityListData.getConsentStaus().equalsIgnoreCase(Constant.REQUEST_CONSENT)) {
                            Util.alertDilogBox(Constant.CONSENT_NOTAPPROVED, Constant.ALERT_TITLE, this);
                            Util.progressDialog.dismiss();
                            return;
                        }
                    }
                    if (homeActivityListData.getConsentStaus().equalsIgnoreCase(Constant.CONSENT_APPROVED_STATUS) &&
                            multipleselectData.getPhone().equalsIgnoreCase(homeActivityListData.getPhoneNumber()) ||
                            multipleselectData.getPhone().equalsIgnoreCase(homeActivityListData.getPhoneNumber()) && homeActivityListData.getDeviceType().equalsIgnoreCase("Pet Tracker")) {
                        isConsentApproved = true;
                    }
                }
            }

            if (isConsentApproved) {
                for (MultipleselectData multipleselectData : selectedData) {
                    Map<Double, Double> latLngMap = new HashMap<>();
                    if (multipleselectData.getLat() != null) {
                        latLngMap.put(Double.valueOf(multipleselectData.getLat()),
                                Double.valueOf(multipleselectData.getLng()));
                        namingMap.put(multipleselectData.getName(), latLngMap);
                    }
                }
                Util.progressDialog.dismiss();
                if (!namingMap.isEmpty()) {
                    goToMapActivity();
                }
            } else {
                Util.alertDilogBox(Constant.CONSENT_NOTAPPROVED, Constant.ALERT_TITLE, this);
                Util.progressDialog.dismiss();
            }
        }
    }

    // Get latest lat-long from server after particular time interval
    private void trackDeviceAfterTimeInterval() {
        AdminLoginData adminLoginDetail = mDbManager.getAdminLoginDetail();
        List<String> data = new ArrayList<>();
        if (adminLoginDetail != null) {
            data.add(adminLoginDetail.getUserId());
            SearchDeviceStatusData searchDeviceStatusData = new SearchDeviceStatusData();
            SearchDeviceStatusData.Device device = searchDeviceStatusData.new Device();
            device.setUsersAssigned(data);
            searchDeviceStatusData.setDevice(device);
            RequestHandler.getInstance(this).handleRequest(new SearchDeviceStatusRequest(new SearchDeviceStatusAfterTimeIntervalSuccessListener(), new SearchDeviceStatusAfterTimeIntervalErrorListener(), Util.userToken, searchDeviceStatusData));
        }
    }

    // Success Listener of Search device status request
    private class SearchDeviceStatusAfterTimeIntervalSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            SearchDeviceStatusResponse searchDeviceStatusResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchDeviceStatusResponse.class);
            if (!selectedData.isEmpty()) {
                namingMap.clear();
                for (SearchDeviceStatusResponse.Data data : searchDeviceStatusResponse.getData()) {
                    for (MultipleselectData multipleselectData : selectedData) {
                        if (data.getDevice().getImei() != null) {
                            if (multipleselectData.getImeiNumber().equalsIgnoreCase(data.getDevice().getImei())) {
                                Map<Double, Double> latLngMap = new HashMap<>();
                                if (data.getLocation() != null) {
                                    latLngMap.put(data.getLocation().getLat(), data.getLocation().getLng());
                                    namingMap.put(data.getDevice().getName(), latLngMap);
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    // Error Listener of Search device status request
    private class SearchDeviceStatusAfterTimeIntervalErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.alertDilogBox(Constant.LOCATION_NOT_FOUND, Constant.ALERT_TITLE, DashboardActivity.this);
            }
        }
    }

    /**************** Refresh map thread scheduler **************************/
    public void startTheScheduler() {
        if (thread != null) {
            thread.interrupt();
            thread = new Thread(new RefreshMap());
            //thread.start();
        }
    }

    // Refresh Map thread
    public class RefreshMap implements Runnable {
        public void run() {
            while (true) {
                DashboardActivity.this.runOnUiThread(() -> {
                    trackDeviceAfterTimeInterval();
                    new MapsActivity().showMapOnTimeInterval();
                });
                try {
                    Thread.sleep(RefreshIntervalSettingActivity.refreshIntervalTime * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /******* Connect to the MQTT ************/
    public void makeMQTTConnection() {
        MQTTManager mqttManager = new MQTTManager();
        mqttManager.getMQTTClient(this);
        mqttManager.connetMQTT();
    }

    // Navigates to the Map activity
    private void goToMapActivity() {
        Util.setLocationFlagStatus(this, true);
        Util.clearAutologinstatus(this);
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    // Sends SMS and update the Consent to the Pending state for the particular device
    private void sendSMS(String phoneNumber) {
        String userName = mDbManager.getAdminDetail();
        String imei = Util.getInstance().getIMEI(this);
        String consentStatus = mDbManager.getConsentStatusBorqs(phoneNumber);
        if (Constant.CONSENT_APPROVED_STATUS.equalsIgnoreCase(consentStatus)) {
            Util.alertDilogBox(Constant.CONSENT_APPROVED, Constant.ALERT_TITLE, this);
        } else {
            String phoneNumber1 = null;
            if (subscriptionInfos != null) {
                phoneNumber1 = subscriptionInfos.get(0).getNumber();
            }
            new SendSMSTask().execute(phoneNumber, userName + Constant.CONSENT_MSG_TO_TRACKEE + phoneNumber1.trim().substring(2, phoneNumber1.length()) + "&" + userName + "&" + imei);
            Toast.makeText(this, Constant.CONSENT_MSG_SENT, Toast.LENGTH_SHORT).show();
            mDbManager.updatependingConsent(phoneNumber);
            addDataInHomeScreen();
        }
    }

    // Called when you press back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Called when you come back to the activity
    @Override
    protected void onResume() {
        super.onResume();
        isDevicePresent();
    }

    /************  Class to calculate the signal strength ***********************/
    class MyPhoneStateListener extends PhoneStateListener {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission not granted");
                }
            }
            List<CellInfo> cellInfoList = mTelephonyManager.getAllCellInfo();
            if (cellInfoList != null) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte) {
                        signalStrengthValue = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                    }
                }
            }
        }
    }

    // Permissions check
    public void checkPermission() {
        int fineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int fineLocationCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int readphoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (readphoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (fineLocationCoarse != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {
            subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                subscriptionInfos = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                // If request is cancelled, the result arrays are empty.
                for (int grantResult : grantResults) {
                    if (grantResults.length > 0 && grantResult == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission granted");
                    }
                }
            }
            default:
//                Log.d("TAG", "Something went wrong");
                break;
        }
    }

    // Send location to the borqs every 15 seconds
    public class SendLocation implements Runnable {
        public void run() {
            while (true) {
                try {
                    makeMQTTConnection();
                    Thread.sleep(15000);
                    publishMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Store consent time in database
    private void storeConsentTime(String phoneNumber, int approvalTime) {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(currentTime);
        mDbManager.updateConsentTime(phoneNumber, currentDateandTime.trim(), approvalTime);
    }

    public void setConstaint() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresStorageNotLow(false)
                .build();

        PeriodicWorkRequest refreshWork =
                new PeriodicWorkRequest.Builder(ConsentTimeUpdate.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();
        mWorkManager.enqueue(refreshWork);
    }

    public void registerReceiver() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(getString(R.string.customintent));
        registerReceiver(mRecevier, intent);
    }

    public BroadcastReceiver mRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            showDatainList();
            boolean flag = intent.getBooleanExtra("TokenFlag", false);
            if (flag) {
                Util.clearAutologinstatus(DashboardActivity.this);
                mDbManager.updateLogoutData();
                goToLoginActivity();
            }
        }
    };

    // Deep Link URI Check, gets called when you click on Deep link URI and it comes back to the Home screen, not login page
    private void deepLinkingURICheck() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.toString().contains(getString(R.string.yesjff))) {
            String number = data.toString().substring(data.toString().length() - 10);
            mDbManager.updateConsentInDeviceBors(number, Constant.CONSENT_APPROVED_STATUS);
            addDataInHomeScreen();
        } else if (data != null && data.toString().contains(getString(R.string.nojff))) {
            String number = data.toString().substring(data.toString().length() - 10);
            mDbManager.updateConsentInDeviceBors(number, Constant.REQUEST_CONSENT);
            addDataInHomeScreen();
        }
    }

    // Displays Consent time for URI check
    private void showSpinnerforConsentTime(String phoneNumber) {
        String time[] = {Constant.MIN_15, Constant.MIN_25, Constant.MIN_30, Constant.MIN_40};
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_consent_time);
        dialog.setTitle(Constant.DIALOG_TITLE);
        dialog.getWindow().setLayout(1000, 500);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, time);
        Spinner spinner = dialog.findViewById(R.id.consentSpinner);
        Button close = dialog.findViewById(R.id.close);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int consentTimeApproval;
                String value = (String) parent.getItemAtPosition(position);
                if (value.contains("15")) {
                    consentTimeApproval = 14;
                } else if (value.contains("25")) {
                    consentTimeApproval = 23;
                } else if (value.contains("30")) {
                    consentTimeApproval = 28;

                } else {
                    consentTimeApproval = 38;
                }
                storeConsentTime(phoneNumber, consentTimeApproval);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // To do
            }
        });
        close.setOnClickListener(v -> {
            sendSMS(phoneNumber);
            dialog.dismiss();
        });
        dialog.show();
    }

    // Displays spinner for Group Consent time
    private void showSpinnerforGroupConsentTime(List<HomeActivityListData> data) {
        String time[] = {Constant.MIN_15, Constant.MIN_25, Constant.MIN_30, Constant.MIN_40};
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_consent_time);
        dialog.setTitle(Constant.DIALOG_TITLE);
        dialog.getWindow().setLayout(1000, 500);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, time);
        Spinner spinner = dialog.findViewById(R.id.consentSpinner);
        Button close = dialog.findViewById(R.id.close);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*int consentTimeApproval;
                String value = (String) parent.getItemAtPosition(position);
                if (value.contains("15")) {
                    consentTimeApproval = 14;
                } else if (value.contains("25")) {
                    consentTimeApproval = 23;
                } else if (value.contains("30")) {
                    consentTimeApproval = 28;

                } else {
                    consentTimeApproval = 38;
                }*/
                //storeConsentTime(phoneNumber, consentTimeApproval);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // To do
            }
        });
        close.setOnClickListener(v -> {
            for (HomeActivityListData homeActivityListData : data) {
                sendSMS(homeActivityListData.getPhoneNumber());
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    // Adds data in Home screen using Recycler view, It is the main method to display devices in Home screen
    private void addDataInHomeScreen() {
        if (!listOnHomeScreens.isEmpty()) {
            for (HomeActivityListData listOnHomeScreenData : listOnHomeScreens) {
                if (listOnHomeScreenData.isCreated == 1) {
                    HomeActivityListData data = new HomeActivityListData();
                    data.setName(listOnHomeScreenData.getName());
                    data.setPhoneNumber(listOnHomeScreenData.getPhoneNumber());
                    data.setGroupMember(listOnHomeScreenData.isGroupMember());
                    data.setIsCreated(listOnHomeScreenData.isCreated);
                    data.setGroupName(listOnHomeScreenData.getGroupName());
                    mDbManager.insertInBorqsDB(data, Util.adminEmail);
                }
            }
        }
        listOnHomeScreens.clear();
        List<HomeActivityListData> listOnDashBoard = new ArrayList<>();
        List<HomeActivityListData> alldata = mDbManager.getAlldata(Util.adminEmail);
        for (HomeActivityListData homeActivityListData : alldata) {
            if (homeActivityListData.isGroupMember() == true && homeActivityListData.getImeiNumber() == null) {
                listOnDashBoard.add(homeActivityListData);
            } else if (homeActivityListData.isGroupMember() == false && homeActivityListData.getImeiNumber() != null) {
                listOnDashBoard.add(homeActivityListData);
            }
        }
        adapter = new TrackerDeviceListAdapter(this, listOnDashBoard);
        listView.setAdapter(adapter);
    }
}