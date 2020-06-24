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

package com.jio.devicetracker.view.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddMemberInGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;
import com.jio.devicetracker.view.adapter.ChooseGroupListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChooseGroupActivity extends BaseActivity implements View.OnClickListener {

    private DBManager mDbManager;
    private ChooseGroupListAdapter mAdapter;
    private EditText trackeeNameEditText;
    private String userId;
    private ImageView memberIcon;
    private TextView groupText;
    private CardView cardViewGroup;
    private String phoneNumber;
    private String groupId;
    private List<HomeActivityListData> chooseGroupDataList;
    private String label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group);
        Intent intent = getIntent();
        label = intent.getStringExtra("Title");
        phoneNumber = intent.getStringExtra(Constant.DEVICE_NUMBER);
        initUI();
        setMemberIcon(label);
        initDataMember();
        makeGroupInfoPerUserRequestAPICall();
    }

    // Set the memberIcon
    private void setMemberIcon(String label) {
        if (label != null && !label.isEmpty()) {
            if (label.equalsIgnoreCase("Woman")) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
            } else if (label.equalsIgnoreCase("Man")) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
            } else if (label.equalsIgnoreCase("Boy")) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
            } else if (label.equalsIgnoreCase(Constant.CAT)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
            } else if (label.equalsIgnoreCase(Constant.DOG)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
            } else if (label.equalsIgnoreCase("OtherPet")) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
            } else if (label.equalsIgnoreCase(Constant.KID)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
            } else if (label.equalsIgnoreCase(Constant.OTHER)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
            }
            if (label.equalsIgnoreCase("Girl")) {
                if (label != null && !label.isEmpty()) {
                    if (label.equalsIgnoreCase(Constant.WIFE)) {
                        memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                    }
                }
                trackeeNameEditText.setText(label);
            }
        }
    }


    /**
     * Initialize data members
     */
    private void initDataMember() {
        mDbManager = new DBManager(this);
        userId = mDbManager.getAdminLoginDetail().getUserId();
    }

    /**
     * Initialize UI component
     */
    private void initUI() {
        memberIcon = findViewById(R.id.userIcon);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(Constant.Choose_Group);
        ImageView createGroup = findViewById(R.id.createGroup);
        createGroup.setVisibility(View.VISIBLE);
        createGroup.setOnClickListener(this);
        Button backBtn = findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        groupText = findViewById(R.id.group_detail_text);
        cardViewGroup = findViewById(R.id.cardViewList);

        title.setTypeface(Util.mTypeface(this, 5));
        trackeeNameEditText = findViewById(R.id.trackeeNameEditText);
        trackeeNameEditText.setTypeface(Util.mTypeface(this, 5));
        Button chooseGroupButton = findViewById(R.id.continueChooseGroup);
        chooseGroupButton.setTypeface(Util.mTypeface(this, 5));
        Button addLater = findViewById(R.id.addLater);
        Button continueBtn = findViewById(R.id.continueChooseGroup);
        continueBtn.setOnClickListener(this);
        addLater.setOnClickListener(this);
    }

    /**
     * To do event handling
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        // To do
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.createGroup:
                gotoCreateGroupActivity();
                break;
            case R.id.addLater:
                createGroupAndAddContactDetails();
                break;
            case R.id.continueChooseGroup:
                addMemberToCreatedGroup(groupId);
                break;
        }
    }

    private void gotoCreateGroupActivity() {
        Intent createGroupIntent = new Intent(this, CreateGroupActivity.class);
        createGroupIntent.putExtra("TrackeeName", trackeeNameEditText.getText().toString());
        createGroupIntent.putExtra("TrackeeNumber", phoneNumber);
        startActivity(createGroupIntent);
    }

    /**
     * Get All Group info per user API Call
     */
    protected void makeGroupInfoPerUserRequestAPICall() {
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupInfoPerUserRequest(new GetGroupInfoPerUserRequestSuccessListener(), new GetGroupInfoPerUserRequestErrorListener(), userId));
    }

    /**
     * GetGroupInfoPerUserRequest Success listener
     */
    private class GetGroupInfoPerUserRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetGroupInfoPerUserResponse getGroupInfoPerUserResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetGroupInfoPerUserResponse.class);
            parseResponseStoreInDatabase(getGroupInfoPerUserResponse);
            addDatainList();
            adapterEventListener();
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GET_GROUP_INFO_PER_USER_ERROR, Constant.ALERT_TITLE, ChooseGroupActivity.this);
            }
        }
    }

    /**
     * Parse the response and store in DB(Group Table and Member table)
     */
    public void parseResponseStoreInDatabase(GetGroupInfoPerUserResponse
                                                     getGroupInfoPerUserResponse) {
        List<HomeActivityListData> groupList = new ArrayList<>();
        List<GroupMemberDataList> mGroupMemberDataLists = new ArrayList<>();
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            HomeActivityListData homeActivityListData = new HomeActivityListData();
            homeActivityListData.setGroupName(data.getGroupName());
            homeActivityListData.setCreatedBy(data.getCreatedBy());
            homeActivityListData.setGroupId(data.getId());
            homeActivityListData.setStatus(data.getStatus());
            homeActivityListData.setUpdatedBy(data.getUpdatedBy());
            homeActivityListData.setFrom(data.getSession().getFrom());
            homeActivityListData.setTo(data.getSession().getTo());
            homeActivityListData.setGroupOwnerName(data.getGroupOwner().get(0).getName());
            homeActivityListData.setGroupOwnerPhoneNumber(data.getGroupOwner().get(0).getPhone());
            homeActivityListData.setGroupOwnerUserId(data.getGroupOwner().get(0).getUserId());
            groupList.add(homeActivityListData);
        }
        for (GetGroupInfoPerUserResponse.Data data : getGroupInfoPerUserResponse.getData()) {
            if (!data.getStatus().equalsIgnoreCase(Constant.CLOSED)) {
                for (GetGroupInfoPerUserResponse.Consents mConsents : data.getConsents()) {
                    GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
                    groupMemberDataList.setConsentId(mConsents.getConsentId());
                    groupMemberDataList.setNumber(mConsents.getPhone());
                    groupMemberDataList.setGroupAdmin(mConsents.isGroupAdmin());
                    groupMemberDataList.setGroupId(data.getId());
                    groupMemberDataList.setConsentStatus(mConsents.getStatus());
                    groupMemberDataList.setName(mConsents.getName());
                    groupMemberDataList.setUserId(mConsents.getUserId());
                    mGroupMemberDataLists.add(groupMemberDataList);
                }
            }
        }
        mDbManager.insertAllDataIntoGroupTable(groupList);
        mDbManager.insertGroupMemberDataInListFormat(mGroupMemberDataLists);
    }

    /**
     * Adapter Listener
     */
    private void adapterEventListener() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickPagerListener(new ChooseGroupListAdapter.RecyclerViewClickListener() {
                @Override
                public void groupButtonClicked(HomeActivityListData homeActivityListData) {
                    groupId = homeActivityListData.getGroupId();
                    updateUIInChooseGroupActivity(homeActivityListData);
                }
            });
        }
    }

    /**
     * Add Members in Group API Call, member will be part of group
     */
    public void addMemberInGroupAPICall(HomeActivityListData homeActivityListData) {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone("8088422893");
        consents.setName(trackeeNameEditText.getText().toString().trim());
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, homeActivityListData.getGroupId(), userId));
    }

    /**
     * Add Member in group Success Listener
     */
    private class AddMemberInGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
                startActivity(new Intent(ChooseGroupActivity.this, DashboardMainActivity.class));
            }
        }
    }

    /**
     * Add Member in Group Error Listener
     */
    private class AddMemberInGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.GROUP_MEMBER_ADDITION_FAILURE, Constant.ALERT_TITLE, ChooseGroupActivity.this);
            } else if (error.networkResponse.statusCode == Constant.STATUS_CODE_404) {
                // Make Verify and Assign call
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.DEVICE_NOT_FOUND, Constant.ALERT_TITLE, ChooseGroupActivity.this);
            } else {
                Util.progressDialog.dismiss();
                Util.alertDilogBox(Constant.GROUP_MEMBER_ADDITION_FAILURE, Constant.ALERT_TITLE, ChooseGroupActivity.this);
            }
        }
    }


    /**
     * Displays created group in recycler view
     */
    private void addDatainList() {
        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        List<HomeActivityListData> mGroupIconList = mDbManager.getAllGroupIconTableData();
        chooseGroupDataList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId())) {
                if (!data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_USER_GROUP_NAME) && !data.getGroupName().equalsIgnoreCase(Constant.INDIVIDUAL_DEVICE_GROUP_NAME) && (data.getStatus().equalsIgnoreCase("Active") || data.getStatus().equalsIgnoreCase("Scheduled"))) {
                    HomeActivityListData homeActivityListData = new HomeActivityListData();
                    homeActivityListData.setGroupName(data.getGroupName());
                    homeActivityListData.setGroupId(data.getGroupId());
                    homeActivityListData.setStatus(data.getStatus());
                    homeActivityListData.setCreatedBy(data.getCreatedBy());
                    homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                    homeActivityListData.setFrom(data.getFrom());
                    homeActivityListData.setTo(data.getTo());
                    for (HomeActivityListData mHomeActivityListData : mGroupIconList) {
                        if (mHomeActivityListData.getGroupId().equalsIgnoreCase(data.getGroupId())) {
                            homeActivityListData.setGroupIcon(mHomeActivityListData.getGroupIcon());
                        }
                    }
                    chooseGroupDataList.add(homeActivityListData);
                }
            }
        }

        if (chooseGroupDataList.size() == 0) {
            groupText.setVisibility(View.VISIBLE);
            cardViewGroup.setVisibility(View.INVISIBLE);
        }
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        RecyclerView mRecyclerView = findViewById(R.id.chooseGroupRecyclerViewWithInfo);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChooseGroupListAdapter(chooseGroupDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void addMemberToCreatedGroup(String groupId) {
        this.createdGroupId = groupId;
        this.memberName = trackeeNameEditText.getText().toString();
        this.memberNumber = phoneNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = true;
        this.isNavigateToGroupsFragment = true;
        addMemberInGroupAPICall();
    }

    private void createGroupAndAddContactDetails() {
        this.memberName = trackeeNameEditText.getText().toString();
        this.memberNumber = phoneNumber;
        this.isFromCreateGroup = false;
        this.isGroupMember = false;
        this.isFromDevice = true;
        setUserIcon(label);
        createGroupAndAddContactAPICall(Constant.INDIVIDUAL_DEVICE_GROUP_NAME);
    }

    private void updateUIInChooseGroupActivity(HomeActivityListData mData) {
        List<HomeActivityListData> mHomeActivityListData = new ArrayList<>();
        for (HomeActivityListData data : chooseGroupDataList) {
            HomeActivityListData homeActivityListData = new HomeActivityListData();
            homeActivityListData.setGroupName(data.getGroupName());
            homeActivityListData.setGroupId(data.getGroupId());
            homeActivityListData.setStatus(data.getStatus());
            homeActivityListData.setCreatedBy(data.getCreatedBy());
            homeActivityListData.setUpdatedBy(data.getUpdatedBy());
            homeActivityListData.setFrom(data.getFrom());
            homeActivityListData.setTo(data.getTo());
            if (mData.getGroupIcon() != null && mData.getGroupIcon().equalsIgnoreCase(Constant.GROUP_SELECTED)) {
                homeActivityListData.setGroupIcon(data.getGroupIcon());
            } else if (mData.getGroupId().equalsIgnoreCase(data.getGroupId())) {
                homeActivityListData.setGroupIcon("groupSelected");
            } else {
                homeActivityListData.setGroupIcon(data.getGroupIcon());
            }
            mHomeActivityListData.add(homeActivityListData);
        }
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        RecyclerView mRecyclerView = findViewById(R.id.chooseGroupRecyclerViewWithInfo);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChooseGroupListAdapter(mHomeActivityListData, this);
        mRecyclerView.setAdapter(mAdapter);
    }

}

