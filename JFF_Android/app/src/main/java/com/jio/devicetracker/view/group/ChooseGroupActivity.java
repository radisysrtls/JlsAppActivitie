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

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.BaseActivity;
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
    private Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group);
        Intent intent = getIntent();
        label = intent.getStringExtra(Constant.TITLE_NAME);
        phoneNumber = intent.getStringExtra(Constant.DEVICE_NUMBER);
        initUI();
        setMemberIcon(label);
        initDataMember();
        makeGroupInfoPerUserRequestAPICall();
    }

    // Set the memberIcon
    private void setMemberIcon(String label) {
        trackeeNameEditText.setText(label);
        if (label != null && !label.isEmpty()) {
            if (label.equalsIgnoreCase(Constant.MOM)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.mother));
            } else if (label.equalsIgnoreCase(Constant.FATHER)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.father));
            } else if (label.equalsIgnoreCase(Constant.HUSBAND)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.husband));
            } else if (label.equalsIgnoreCase(Constant.CAT)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.cat));
            } else if (label.equalsIgnoreCase(Constant.DOG)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.dog));
            } else if (label.equalsIgnoreCase(Constant.OTHER_PET)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.other_pet));
            } else if (label.equalsIgnoreCase(Constant.KID)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.kid));
            } else if (label.equalsIgnoreCase(Constant.OTHER)) {
                memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.other));
            }
            if (label.equalsIgnoreCase(Constant.WIFE)) {
                if (label != null && !label.isEmpty()) {
                    if (label.equalsIgnoreCase(Constant.WIFE)) {
                        memberIcon.setImageDrawable(getResources().getDrawable(R.drawable.wife));
                    }
                }

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
        continueBtn = findViewById(R.id.continueChooseGroup);
        continueBtn.setOnClickListener(this);
        addLater.setOnClickListener(this);
    }

    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
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
            default:
                // Todo
                break;
        }
    }

    private void gotoCreateGroupActivity() {
        Intent createGroupIntent = new Intent(this, CreateGroupActivity.class);
        createGroupIntent.putExtra(Constant.TRACKEE_NAME, trackeeNameEditText.getText().toString());
        createGroupIntent.putExtra(Constant.TRACKEE_NUMBER, phoneNumber);
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
                showCustomAlertWithText(Constant.GET_GROUP_INFO_PER_USER_ERROR);
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
            if (!(data.getGroupOwner().isEmpty())) {
                homeActivityListData.setGroupOwnerName(data.getGroupOwner().get(0).getName());
                homeActivityListData.setGroupOwnerPhoneNumber(data.getGroupOwner().get(0).getPhone());
                homeActivityListData.setGroupOwnerUserId(data.getGroupOwner().get(0).getUserId());
            }
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
                public void groupButtonClicked(HomeActivityListData homeActivityListData, String groupIconSelection) {
                    updateUIInChooseGroupActivity(homeActivityListData);
                    if (groupIconSelection != null && !groupIconSelection.equalsIgnoreCase(Constant.GROUP_SELECTED)) {
                        groupId = homeActivityListData.getGroupId();
                    } else {
                        groupId = "";
                    }

                }
            });
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

        if (chooseGroupDataList.isEmpty()) {
            groupText.setVisibility(View.VISIBLE);
            cardViewGroup.setVisibility(View.INVISIBLE);
            continueBtn.setBackground(getResources().getDrawable(R.drawable.selector));
            continueBtn.setEnabled(false);

        }
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        RecyclerView mRecyclerView = findViewById(R.id.chooseGroupRecyclerViewWithInfo);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChooseGroupListAdapter(chooseGroupDataList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void addMemberToCreatedGroup(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            showCustomAlertWithText(Constant.GROUP_CHOOSE_CONDITION);
            return;
        }
        if (trackeeNameEditText.getText() == null || trackeeNameEditText.getText().length() == 0) {
            showCustomAlertWithText(Constant.NAME_VALIDATION);
            return;
        }
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
        isNavigateToGroupsFragment = false;
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
