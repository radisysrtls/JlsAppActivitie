package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Information extends AppCompatActivity {
    RecyclerView m_recyclerView;
    InfoAdapter m_bleDetailsAdapter;
    List<SettingsDetails> m_bleDetails = new ArrayList<SettingsDetails>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_recycle_view);
        m_recyclerView = (RecyclerView) findViewById(R.id.info_recycle);
        addSettingsItems();
        m_bleDetailsAdapter = new InfoAdapter(m_bleDetails, this, m_recyclerView);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_recyclerView.setAdapter(m_bleDetailsAdapter);
        ImageButton infoBack = (ImageButton) findViewById(R.id.info_back);
        infoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton infoHome = (ImageButton) findViewById(R.id.info_home);
        infoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(JioUtils.HOME_KEY, intent);
                finish();
            }
        });
    }

    public void addSettingsItems() {
        SettingsDetails infoAbout = new SettingsDetails(getResources().getString(R.string.info_aboutjio_header), getResources().getString(R.string.info_aboutjio_details));
        SettingsDetails infoFeature = new SettingsDetails(getResources().getString(R.string.info_features_header), "");
        SettingsDetails infoConn = new SettingsDetails(getResources().getString(R.string.info_connectionsetup_header), getResources().getString(R.string.info_connectionsetup_details));
        SettingsDetails infoPhoto = new SettingsDetails(getResources().getString(R.string.info_photoclick_header), getResources().getString(R.string.info_photoclick_details));
        m_bleDetails.add(infoAbout);
        m_bleDetails.add(infoFeature);
        m_bleDetails.add(infoConn);
        m_bleDetails.add(infoPhoto);
    }

}
