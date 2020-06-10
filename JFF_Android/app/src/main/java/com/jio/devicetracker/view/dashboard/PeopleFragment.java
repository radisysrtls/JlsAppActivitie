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

package com.jio.devicetracker.view.dashboard;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MapData;
import com.jio.devicetracker.database.pojo.SearchEventData;
import com.jio.devicetracker.database.pojo.request.GetGroupInfoPerUserRequest;
import com.jio.devicetracker.database.pojo.request.SearchEventRequest;
import com.jio.devicetracker.database.pojo.response.GetGroupInfoPerUserResponse;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.location.LocationActivity;
import com.jio.devicetracker.view.adapter.PeopleMemberListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PeopleFragment extends Fragment {

    private DBManager mDbManager;
    private ImageView instructionIcon;
    private TextView instruction;
    private static PeopleMemberListAdapter groupListAdapter;
    private List<MapData> mapDataList;
    public static List<HomeActivityListData> grpDataList;
    private int counter = 0;
    private String selectedUserName = ""; //TODO : Remove this after location flow is set

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        instruction = view.findViewById(R.id.people_instruction);
        instruction.setTypeface(Util.mTypeface(getActivity(),3));
        instructionIcon = view.findViewById(R.id.people_default_icon);
        /*instruction.setVisibility(View.VISIBLE);
        instructionIcon.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onStart() {
        super.onStart();
        mDbManager = new DBManager(getActivity());
        makeGroupInfoPerUserRequestAPICall();
        mapDataList = new ArrayList<>();
        grpDataList = new CopyOnWriteArrayList<>();

    }

    private void displayGroupDataInDashboard(View view) {
        RecyclerView groupListRecyclerView = view.findViewById(R.id.peopleListRecyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        groupListRecyclerView.setLayoutManager(mLayoutManager);

        List<HomeActivityListData> groupDetailList = mDbManager.getAllGroupDetail();
        if(groupDetailList == null){
            instruction.setVisibility(View.VISIBLE);
            instructionIcon.setVisibility(View.VISIBLE);
        }
        List<HomeActivityListData> groupList = new ArrayList<>();
        for (HomeActivityListData data : groupDetailList) {
            if (data.getCreatedBy() != null && data.getCreatedBy().equalsIgnoreCase(mDbManager.getAdminLoginDetail().getUserId()) && data.getGroupName().equals(Constant.INDIVIDUAL_USER_GROUP_NAME) ) {
                  List<GroupMemberDataList> memberDataList = mDbManager.getAllGroupMemberDataBasedOnGroupId(data.getGroupId());
                    for (GroupMemberDataList memberData : memberDataList){
                        HomeActivityListData homeActivityListData = new HomeActivityListData();
                        homeActivityListData.setGroupName(memberData.getName());
                        homeActivityListData.setPhoneNumber(memberData.getNumber());
                        homeActivityListData.setConsentStaus(memberData.getConsentStatus());
                        homeActivityListData.setConsentId(memberData.getConsentId());
                        homeActivityListData.setGroupId(data.getGroupId());
                        homeActivityListData.setStatus(data.getStatus());
                        homeActivityListData.setCreatedBy(data.getCreatedBy());
                        homeActivityListData.setUpdatedBy(data.getUpdatedBy());
                        homeActivityListData.setProfileImage(data.getProfileImage());
                        homeActivityListData.setFrom(data.getFrom());
                        homeActivityListData.setTo(data.getTo());
                        groupList.add(homeActivityListData);
                    }
            }
        }
        groupListAdapter = new PeopleMemberListAdapter(groupList, getContext());
        groupListRecyclerView.setAdapter(groupListAdapter);
        adapterEventListener();
    }

    /**
     * Adapter Listener
     */
    private void adapterEventListener() {
//        if (groupListAdapter != null) {
            groupListAdapter.setOnItemClickPagerListener(new PeopleMemberListAdapter.RecyclerViewClickListener() {
                @Override
                public void clickOnListLayout(String groupId, String name) {
                    selectedUserName = name;
                    SearchEventData searchEventData = new SearchEventData();
                    List<String> mList = new ArrayList<>();
                    mList.add(Constant.LOCATION);
                    mList.add(Constant.SOS);
                    searchEventData.setTypes(mList);
                    GroupRequestHandler.getInstance(getContext()).handleRequest(new SearchEventRequest(new SearchEventRequestSuccessListener(), new SearchEventRequestErrorListener(), searchEventData, mDbManager.getAdminLoginDetail().getUserId(), groupId, Constant.GET_LOCATION_URL));
                }
            });
//     }
    }

    /**
     * Navigates to the Map activity
     */
    private void goToMapActivity() {
        Intent intent = new Intent(getContext(), LocationActivity.class);
        intent.putParcelableArrayListExtra(Constant.MAP_DATA, (ArrayList<? extends Parcelable>) mapDataList);
        startActivity(intent);
    }

    /**
     * Get All Group info per user API Call
     */
    protected void makeGroupInfoPerUserRequestAPICall() {
        Util.getInstance().showProgressBarDialog(getActivity());
        GroupRequestHandler.getInstance(getActivity()).handleRequest(new GetGroupInfoPerUserRequest(new PeopleFragment.GetGroupInfoPerUserRequestSuccessListener(), new PeopleFragment.GetGroupInfoPerUserRequestErrorListener(), mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * GetGroupInfoPerUserRequest Success listener
     */
    private class GetGroupInfoPerUserRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            GetGroupInfoPerUserResponse getGroupInfoPerUserResponse = Util.getInstance().getPojoObject(String.valueOf(response), GetGroupInfoPerUserResponse.class);
            Util.progressDialog.dismiss();
            parseResponseStoreInDatabase(getGroupInfoPerUserResponse);
            displayGroupDataInDashboard(getView());
        }
    }

    /**
     * GetGroupInfoPerUserRequest Error listener
     */
    private class GetGroupInfoPerUserRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 409) {
                Util.alertDilogBox(Constant.GET_GROUP_INFO_PER_USER_ERROR, Constant.ALERT_TITLE, getActivity());
            }
        }
    }


    /**
     * Parse the response and store in DB(Group Table and Member table)
     */
    public void parseResponseStoreInDatabase(GetGroupInfoPerUserResponse getGroupInfoPerUserResponse) {
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
     * Search Event Request API call Success Listener
     */
    private class SearchEventRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            Util.progressDialog.dismiss();

            // TODO : Remove hardcoded values when location details are available in server
            MapData mapData = new MapData();
            mapData.setLatitude(12.917757);
            mapData.setLongitude(77.609629);
            mapData.setName(selectedUserName);
            mapDataList.add(mapData);
            counter = 0;
            goToMapActivity();

//            SearchEventResponse searchEventResponse = Util.getInstance().getPojoObject(String.valueOf(response), SearchEventResponse.class);
//            if (searchEventResponse.getMessage().equalsIgnoreCase(Constant.NO_EVENTS_FOUND_RESPONSE)) {
//                Util.alertDilogBox(Constant.LOCATION_NOT_FOUND, Constant.ALERT_TITLE, getContext());
//            } else {
//                List<SearchEventResponse.Data> mList = searchEventResponse.getData();
//                if (! grpDataList.isEmpty()) {
//                    List<GroupMemberDataList> grpMembersOfParticularGroupId = mDbManager.getAllGroupMemberDataBasedOnGroupId(grpDataList.get(0).getGroupId());
//                    for (SearchEventResponse.Data data : mList) {
//                        for (GroupMemberDataList grpMembers : grpMembersOfParticularGroupId) {
//                            if (grpMembers.getDeviceId().equalsIgnoreCase(data.getDevice()) && grpMembers.getUserId().equalsIgnoreCase(data.getUserId())) {
//                                MapData mapData = new MapData();
//                                mapData.setLatitude(data.getLocation().getLat());
//                                mapData.setLongitude(data.getLocation().getLng());
//                                mapData.setName(grpMembers.getName());
//                                mapDataList.add(mapData);
//                                if (mapDataList.size() == grpMembersOfParticularGroupId.size()) {
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    if (! mList.isEmpty()) {
//                        MapData mapData = new MapData();
//                        mapData.setLatitude(mList.get(0).getLocation().getLat());
//                        mapData.setLongitude(mList.get(0).getLocation().getLng());
//                        mapData.setName("sree");
//                        mapDataList.add(mapData);
//                    }
//                }
//                if (!mapDataList.isEmpty() ) {
//                    counter = 0;
//                    goToMapActivity();
//                } else if (!mapDataList.isEmpty() && ! grpDataList.isEmpty()) {
//                    counter = 0;
//                    goToMapActivity();
//                } else {
//                    counter++;
////                    trackDevice();
//                }
//            }
        }
    }

    /**
     * Search Event Request API Call Error listener
     */
    private class SearchEventRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.FETCH_LOCATION_ERROR, Constant.ALERT_TITLE, getContext());
        }
    }

}