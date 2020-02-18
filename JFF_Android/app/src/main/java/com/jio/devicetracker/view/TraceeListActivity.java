package com.jio.devicetracker.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.TraceeListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.view.adapter.TraceeListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TraceeListActivity extends Activity {
    private List<TraceeListData> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_tracee);
        mList = new ArrayList<>();
        addDataInList();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Constant.TRACEE_TITLE);
        RecyclerView mRecyclerList = findViewById(R.id.traceeList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        TraceeListAdapter mAdapter = new TraceeListAdapter(mList);
        mRecyclerList.setAdapter(mAdapter);


    }

    private void addDataInList() {
        for (int i =0; i < 5 ; i++){
            TraceeListData data = new TraceeListData();
            data.setName("Test");
            data.setNumber("1234567890");
            data.setDurationTime("15 min");
            data.setExpiryTime("05 min");
            data.setProfileImage(R.drawable.ic_tracee_list);
            mList.add(data);
        }
    }
}
