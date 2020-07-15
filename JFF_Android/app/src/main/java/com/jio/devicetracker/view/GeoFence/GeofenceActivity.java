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

package com.jio.devicetracker.view.geofence;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.jio.devicetracker.R;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.BaseActivity;

import java.io.IOException;
import java.util.List;

public class GeofenceActivity extends BaseActivity implements View.OnClickListener {
    private GeofenceMapFragment fragmentMap;
    private EditText addressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        fragmentMap = new GeofenceMapFragment();
        initUI();
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.geofenceToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        Intent intent = getIntent();
        boolean editGeofence = intent.getBooleanExtra("EditGeofence", false);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#1C60AB"));
        actionBar.setBackgroundDrawable(colorDrawable);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.GEOFENCE);
        Button backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        RelativeLayout toolbarLayout = findViewById(R.id.toolbarlayout);
        toolbarLayout.setBackgroundColor(getResources().getColor(R.color.geofence_background));
        addressText = findViewById(R.id.address);
        ImageView searchAddress = findViewById(R.id.search);
        ImageView cancelAddress = findViewById(R.id.clear);
       /* menuOption = findViewById(R.id.menu_option);
        menuOption.setVisibility(View.VISIBLE);
        menuOption.setOnClickListener(this);*/
        searchAddress.setOnClickListener(this);
        cancelAddress.setOnClickListener(this);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map_fragment, fragmentMap).commit();

        if (editGeofence) {
            LatLng latlang = new LatLng(intent.getDoubleExtra(Constant.LATITUDE, 0.0d), intent.getDoubleExtra(Constant.LONGNITUDE, 0.0d));
            addFragment(latlang, intent.getIntExtra("Radius", 0));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                LatLng latlng = getLocationFromAddress(addressText.getText().toString());
                if (latlng != null) {
                    addFragment(latlng, 0);
                } else {
                    Toast.makeText(this, Constant.ADDRESS_MESSAGE, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clear:
                addressText.setText("");
                break;
            case R.id.back:
                finish();
                break;
            //case R.id.menu_option:
            default:
                break;
        }

    }

    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 10);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private void addFragment(LatLng latLng, int radius) {
        Bundle bundle = new Bundle();
        bundle.putDouble(Constant.LATITUDE, latLng.latitude);
        bundle.putDouble(Constant.LONGNITUDE, latLng.longitude);
        bundle.putBoolean(Constant.CREATE_GEOFENCE, true);
        if (radius != 0) {
            bundle.putInt(Constant.GEOFENCE_RADIUS, radius);
        }
        fragmentMap.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragmentMap);
        transaction.addToBackStack(null);
        transaction.add(R.id.map_fragment, fragmentMap).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.geofence_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_geofence:
                Intent intent = new Intent(this, EditGeofenceActivity.class);
                if (!addressText.getText().toString().isEmpty()) {
                    intent.putExtra(Constant.GEOFENCE_ADDRESS, addressText.getText().toString());
                }
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}