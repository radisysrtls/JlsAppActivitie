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

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.AddMemberInGroupData;
import com.jio.devicetracker.database.pojo.CreateGroupData;
import com.jio.devicetracker.database.pojo.request.AddMemberInGroupRequest;
import com.jio.devicetracker.database.pojo.request.CreateGroupRequest;
import com.jio.devicetracker.database.pojo.request.GetGroupMemberRequest;
import com.jio.devicetracker.database.pojo.response.CreateGroupResponse;
import com.jio.devicetracker.database.pojo.response.GroupMemberResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.CustomAlertActivity;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.dashboard.DashboardMainActivity;
import com.jio.devicetracker.view.device.AddDeviceActivity;
import com.jio.devicetracker.view.device.DeviceNameActivity;
import com.jio.devicetracker.view.group.CreateGroupActivity;
import com.jio.devicetracker.view.people.AddPeopleActivity;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    public String memberName;
    public String memberNumber;
    private static DBManager mDbManager;
    public String createdGroupId;
    public Boolean isFromCreateGroup;
    public Boolean isGroupMember;
    public Boolean isFromDevice;
    public Boolean isNavigateToGroupsFragment;
    public String selectedIcon;
    private String memberIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbManager = new DBManager(this);
    }


    // Show custom alert with alert message
    private void showCustomAlertWithText(String alertMessage) {
        CustomAlertActivity alertActivity = new CustomAlertActivity(this);
        alertActivity.show();
        alertActivity.alertWithOkButton(alertMessage);
    }

    /**
     * Adds individual contact in Dashboard, but it adds as a member of group.
     * Group Name is hardcoded as a Individual_User
     */
    public void createGroupAndAddContactAPICall(String groupName) {
        CreateGroupData createGroupData = new CreateGroupData();
        createGroupData.setName(groupName);
        createGroupData.setType(Constant.ONE_TO_ONE);
        CreateGroupData.Session session = new CreateGroupData().new Session();
        session.setFrom(Util.getInstance().getTimeEpochFormatAfterCertainTime(1));
        session.setTo(Util.getInstance().createSessionEndDate());
        createGroupData.setSession(session);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new CreateGroupRequest(new CreateGroupSuccessListener(), new CreateGroupErrorListener(), createGroupData, mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * sets the group icon
     *
     * @param selectedIcon
     */
    public void setUserIcon(String selectedIcon) {
        memberIcon = selectedIcon;
    }

    /**
     * Create Group Success Listener
     */
    private class CreateGroupSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            CreateGroupResponse createGroupResponse = Util.getInstance().getPojoObject(String.valueOf(response), CreateGroupResponse.class);
            if (createGroupResponse.getCode() == 200) {
                mDbManager.insertIntoGroupTable(createGroupResponse);
                createdGroupId = createGroupResponse.getData().getId();
                DeviceNameActivity.createdGroupIdFromPeople = createdGroupId;
                CreateGroupActivity.groupIdFromPeopleFlow = createdGroupId;
                mDbManager.insertInToGroupIconTable(createdGroupId, selectedIcon);
                if (isFromCreateGroup) {
                    Util.progressDialog.dismiss();
                    Intent intent = new Intent(BaseActivity.this, AddDeviceActivity.class);
                    intent.putExtra(Constant.GROUP_ID, createdGroupId);
                    startActivity(intent);
                } else if (DashboardMainActivity.flowFromPeople) {
                    addIndividualUserInGroupAPICall();
                } else if (isFromDevice && !isNavigateToGroupsFragment) {
                    mDbManager.insertInToGroupIconTable(createdGroupId, memberIcon);
                    addIndividualUserInGroupAPICall();
                } else {
                    addIndividualUserInGroupAPICall();
                }
            }
        }
    }

    /**
     * Create Group error Listener
     */
    private class CreateGroupErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            if (error.networkResponse.statusCode == Constant.STATUS_CODE_409) {
                showCustomAlertWithText(Constant.ADDING_INDIVIDUAL_USER_FAILED);
            }
        }
    }


    /**
     * Adds individual member inside Group Called Individual_User
     */
    public void addIndividualUserInGroupAPICall() {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone(memberNumber);
        consents.setName(memberName);
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, createdGroupId, mDbManager.getAdminLoginDetail().getUserId()));
    }


    /**
     * Add Members in Group API Call, member will be part of group
     */
    public void addMemberInGroupAPICall() {
        AddMemberInGroupData addMemberInGroupData = new AddMemberInGroupData();
        AddMemberInGroupData.Consents consents = new AddMemberInGroupData().new Consents();
        List<AddMemberInGroupData.Consents> consentList = new ArrayList<>();
        List<String> mList = new ArrayList<>();
        mList.add(Constant.EVENTS);
        consents.setEntities(mList);
        consents.setPhone(memberNumber);
        consents.setName(memberName);
        consentList.add(consents);
        addMemberInGroupData.setConsents(consentList);
        Util.getInstance().showProgressBarDialog(this);
        GroupRequestHandler.getInstance(this).handleRequest(new AddMemberInGroupRequest(new AddMemberInGroupRequestSuccessListener(), new AddMemberInGroupRequestErrorListener(), addMemberInGroupData, createdGroupId, mDbManager.getAdminLoginDetail().getUserId()));
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
                if (isGroupMember) {
                    getAllForOneGroupAPICall();
                } else if (isFromDevice) {
                    Intent intent = new Intent(BaseActivity.this, DashboardMainActivity.class);
                    if (isNavigateToGroupsFragment != null && isNavigateToGroupsFragment) {
                        isNavigateToGroupsFragment = false;
                        intent.putExtra(Constant.Add_Device, false);
                        intent.putExtra(Constant.Add_People, false);
                    } /*else if(isNavigateToGroupsFragment != null && ){
                        intent.putExtra(Constant.Add_Device, false);
                        intent.putExtra(Constant.Add_People, false);
                    }*/ else if (!isFromCreateGroup) {
                        intent.putExtra(Constant.Add_Device, true);
                    }
                    startActivity(intent);
                } else {
                    Util.progressDialog.dismiss();
                    Intent intent = new Intent(BaseActivity.this, DashboardMainActivity.class);
                    intent.putExtra(Constant.Add_People, true);
                    startActivity(intent);
                }
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
                showCustomAlertWithText(Constant.GROUP_MEMBER_ADDITION_FAILURE);
            } else if (error.networkResponse.statusCode == Constant.STATUS_CODE_404) {
                // Make Verify and Assign call
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.DEVICE_NOT_FOUND);
            } else {
                Util.progressDialog.dismiss();
                showCustomAlertWithText(Constant.GROUP_MEMBER_ADDITION_FAILURE);
            }
        }
    }

    /**
     * Get all members of a particular group and update the database
     */
    public void getAllForOneGroupAPICall() {
        if (Util.progressDialog == null) {
            Util.getInstance().showProgressBarDialog(this);
        }
        GroupRequestHandler.getInstance(this).handleRequest(new GetGroupMemberRequest(new GetGroupMemberRequestSuccessListener(), new GetGroupMemberRequestErrorListener(), createdGroupId, mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * Get all members of a particular group success listener
     */
    public class GetGroupMemberRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();
            GroupMemberResponse groupMemberResponse = Util.getInstance().getPojoObject(String.valueOf(response), GroupMemberResponse.class);
            if (groupMemberResponse.getCode() == Constant.SUCCESS_CODE_200) {
                mDbManager.insertGroupMemberDataInTable(groupMemberResponse);
                BaseActivity baseActivity = new AddPeopleActivity();
                ((AddPeopleActivity) baseActivity).getAllMembers(groupMemberResponse.getData());
            }
        }
    }

    /**
     * Get all members of a particular group error listener
     */
    private class GetGroupMemberRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
        }
    }

}