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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.ExitRemovedGroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.request.ExitRemovedGroupRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.adapter.ActiveMemberListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActiveMemberActivity extends AppCompatActivity {

    private String groupId;
    private DBManager mDbManager;
    private ActiveMemberListAdapter mAdapter;
    private String consentId;
    private int position;
    private String errorMessage;
    private String createdBy;
    private String userId;
    private List<GroupMemberDataList> memberList;
    private RecyclerView mRecyclerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_member_list);
        mDbManager = new DBManager(this);
        userId = mDbManager.getAdminLoginDetail().getUserId();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        toolbarTitle.setText(intent.getStringExtra(Constant.GROUPNAME));
        groupId = intent.getStringExtra(Constant.GROUP_ID);
        createdBy = intent.getStringExtra(Constant.CREATED_BY);
        mRecyclerList = findViewById(R.id.trackerList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerList.setLayoutManager(linearLayoutManager);
        mAdapter = new ActiveMemberListAdapter(addDataInList());
        mRecyclerList.setAdapter(mAdapter);
        adapterEventListener();
        isAnyMemberActive();
    }

    /**
     * Adapter Listener
     */
    private void adapterEventListener() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickPagerListener(new ActiveMemberListAdapter.RecyclerViewClickListener() {
                @Override
                public void onPopupMenuClicked(View v, int position, String groupId, boolean isGroupAdmin, String phoneNumber, String consentId) {
                    PopupMenu popup = new PopupMenu(ActiveMemberActivity.this, v);
                    ActiveMemberActivity.this.consentId = consentId;
                    ActiveMemberActivity.this.position = position;
                    if (createdBy != null && createdBy.equalsIgnoreCase(userId)) { // Check through updated by not by isGroupAdmin
                        popup.getMenu().add(Menu.NONE, 1, 1, Constant.REMOVE);
                        errorMessage = Constant.REMOVE_FROM_GROUP_FAILURE;
                    } else {
                        popup.getMenu().add(Menu.NONE, 2, 2, Constant.EXIT);
                        errorMessage = Constant.EXIT_FROM_GROUP_FAILURE;
                    }
                    popup.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case 1:
                                ActiveMemberActivity.this.makeRemoveAPICall(phoneNumber);
                                break;
                            case 2:
                                ActiveMemberActivity.this.makeExitAPICall(phoneNumber);
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

    /**
     * make an Exit API Call
     */
    private void makeExitAPICall(String phoneNumber) {
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(phoneNumber);
        consent.setStatus(Constant.EXITED);
        exitRemovedGroupData.setConsent(consent);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(getApplicationContext()).handleRequest(new ExitRemovedGroupRequest(new ExitFromGroupRequestSuccessListener(), new ExitFromGroupRequestErrorListener(), exitRemovedGroupData, groupId, userId));
    }

    /**
     * Make a Remove API Call
     *
     * @param phoneNumber
     */
    private void makeRemoveAPICall(String phoneNumber) {
        ExitRemovedGroupData exitRemovedGroupData = new ExitRemovedGroupData();
        ExitRemovedGroupData.Consent consent = new ExitRemovedGroupData().new Consent();
        consent.setPhone(phoneNumber);
        consent.setStatus(Constant.REMOVED);
        exitRemovedGroupData.setConsent(consent);
        Util.getInstance().showProgressBarDialog(ActiveMemberActivity.this);
        GroupRequestHandler.getInstance(getApplicationContext()).handleRequest(new ExitRemovedGroupRequest(new RemovedFromGroupRequestSuccessListener(), new RemovedFromGroupRequestErrorListener(), exitRemovedGroupData, groupId, userId));
    }

    /**
     * Exit API Call Success Listener
     */
    private class ExitFromGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            mDbManager.deleteSelectedDataFromGroup(consentId);
            mAdapter.removeItem(position);
            addDataInList();
        }
    }

    /**
     * Exit API Call Error Listener
     */
    private class ExitFromGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(errorMessage, Constant.ALERT_TITLE, ActiveMemberActivity.this);
        }
    }

    /**
     * Exit API Call Success Listener
     */
    private class RemovedFromGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            mDbManager.deleteSelectedDataFromGroup(consentId);
            mAdapter.removeItem(position);
            addDataInList();
        }
    }

    /**
     * Exit API Call Error Listener
     */
    private class RemovedFromGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(errorMessage, Constant.ALERT_TITLE, ActiveMemberActivity.this);
        }
    }

    /**
     * Checks if any member inside the group is active if not display no active member found
     */
    private void isAnyMemberActive() {
        TextView instructionOnActiveMember = findViewById(R.id.activeMemberInGroupPresent);
        if (memberList.isEmpty()) {
            mRecyclerList.setVisibility(View.INVISIBLE);
            instructionOnActiveMember.setVisibility(View.VISIBLE);
        } else {
            instructionOnActiveMember.setVisibility(View.GONE);
            mRecyclerList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Displays group member data in list
     *
     * @return List<GroupMemberDataList>
     */
    private List<GroupMemberDataList> addDataInList() {
        List<GroupMemberDataList> mList = mDbManager.getAllGroupMemberDataBasedOnGroupId(groupId);
        memberList = new ArrayList<>();
        for (GroupMemberDataList data : mList) {
            if (!data.getConsentStatus().equalsIgnoreCase(Constant.EXITED)) {
                GroupMemberDataList groupMemberDataList = new GroupMemberDataList();
                groupMemberDataList.setName(data.getName());
                groupMemberDataList.setNumber(data.getNumber());
                groupMemberDataList.setConsentStatus(data.getConsentStatus());
                groupMemberDataList.setGroupId(data.getGroupId());
                groupMemberDataList.setGroupAdmin(data.isGroupAdmin());
                groupMemberDataList.setProfileImage(R.drawable.ic_tracee_list);
                memberList.add(groupMemberDataList);
            }
        }
        return memberList;
    }
}
