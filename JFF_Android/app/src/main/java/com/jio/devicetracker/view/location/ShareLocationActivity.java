package com.jio.devicetracker.view.location;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.BaseActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ShareLocationActivity extends BaseActivity implements View.OnClickListener {

    private MapFragment fragmentMap;
    private List<MapData> mapDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        fragmentMap = new MapFragment();
        initUI();
    }

    private void initUI() {
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.SHARE_LOCATION);
        Button backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        ImageView shareLocation = findViewById(R.id.share_location);
        TextView memberName = findViewById(R.id.member_name);
        TextView memberAddrss = findViewById(R.id.member_address);
        shareLocation.setOnClickListener(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map_fragment, fragmentMap).commit();
        mapDataList = getIntent().getParcelableArrayListExtra(Constant.MAP_DATA);
        if (getIntent().getStringExtra(Constant.MEMBER_NAME) != null) {
            memberName.setText(getIntent().getStringExtra(Constant.MEMBER_NAME));
        } else {
            memberName.setText("Test");
        }
        if (!mapDataList.isEmpty()) {
            memberAddrss.setText(getAddressFromLocation(mapDataList.get(0).getLatitude(), mapDataList.get(0).getLongitude()));
        } else {
            memberAddrss.setText(Constant.CONSENT_APPROVED_ADDRESS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_location:
                String geoUri = null;
                if (!mapDataList.isEmpty()) {
                    geoUri = "http://maps.google.com/maps?q=loc:" + mapDataList.get(0).getLatitude() + "," + mapDataList.get(0).getLongitude();
                } else {
                    Toast.makeText(this, "Location is not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, geoUri);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
                break;

            case R.id.back:
                finish();
                break;
            default:
                // Todo
                break;
        }
    }

    /**
     * Returns real address based on Lat and Long(Geo Coding)
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        StringBuilder strAddress = new StringBuilder();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address fetchedAddress = addresses.get(0);
                strAddress.setLength(0);
                strAddress.append(fetchedAddress.getAddressLine(0)).append(" ");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strAddress.toString();
    }
}
