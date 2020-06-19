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

package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jio.devicetracker.R;
import com.jio.devicetracker.database.db.DBManager;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.request.DeleteGroupRequest;
import com.jio.devicetracker.network.GroupRequestHandler;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.dashboard.DeviceFragment;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private Context mContext;
    private List<HomeActivityListData> mList;
    private String groupId;
    private DBManager mDbManager;
    private int position;
    private RelativeLayout devicesOperationLayout;

    public DeviceListAdapter(List<HomeActivityListData> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        this.mDbManager = new DBManager(mContext);
    }

    /**
     * Binds the given View to the position
     *
     * @param parent
     * @param viewType
     * @return View Holder object
     */
    @NonNull
    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout_adapter, parent, false);
        return new DeviceListAdapter.ViewHolder(itemView);
    }


    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(@NonNull DeviceListAdapter.ViewHolder holder, int position) {
        HomeActivityListData data = mList.get(position);
        holder.trackingDeviceName.setTypeface(Util.mTypeface(mContext, 5));
        holder.trackingDeviceName.setText(data.getGroupName());
    }


    /**
     * return The total number of items in this adapter
     *
     * @return size
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RelativeLayout devicesOperationLayout;
        private ImageView devicesCloseOperation;
        private TextView shareDevices;
        private TextView devicesDelete;
        private TextView trackingDeviceName;
        private ImageView deviceMenubar;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackingDeviceName = itemView.findViewById(R.id.trackingDeviceName);
            devicesCloseOperation = itemView.findViewById(R.id.devicesCloseOperation);
            shareDevices = itemView.findViewById(R.id.shareDevices);
            devicesDelete = itemView.findViewById(R.id.devicesDelete);
            deviceMenubar = itemView.findViewById(R.id.deviceMenubar);
            devicesOperationLayout = itemView.findViewById(R.id.devicesOperationLayout);
            deviceMenubar.setOnClickListener(this);
            shareDevices.setOnClickListener(this);
            devicesDelete.setOnClickListener(this);
            devicesCloseOperation.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deviceMenubar:
                    devicesOperationLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.devicesCloseOperation:
                    devicesOperationLayout.setVisibility(View.GONE);
                    break;
                case R.id.devicesDelete:
                    DeviceListAdapter.this.position = getAdapterPosition();
                    DeviceListAdapter.this.devicesOperationLayout = devicesOperationLayout;
                    makeDeleteGroupAPICall(mList.get(getAdapterPosition()).getGroupId());
                    break;
            }
        }
    }

    /**
     * Delete the Group and update the database
     */
    private void makeDeleteGroupAPICall(String groupId) {
        this.groupId = groupId;
        Util.getInstance().showProgressBarDialog(mContext);
        GroupRequestHandler.getInstance(mContext).handleRequest(new DeleteGroupRequest(new DeleteGroupRequestSuccessListener(), new DeleteGroupRequestErrorListener(), groupId, mDbManager.getAdminLoginDetail().getUserId()));
    }

    /**
     * Delete Group Request API Call Success Listener and create new group if Session time is completed and Request Consent button is clicked
     */
    private class DeleteGroupRequestSuccessListener implements Response.Listener {
        @Override
        public void onResponse(Object response) {
            mDbManager.deleteSelectedDataFromGroup(groupId);
            mDbManager.deleteSelectedDataFromGroupMember(groupId);
            Util.progressDialog.dismiss();
            removeItem(position);
            devicesOperationLayout.setVisibility(View.GONE);
            DeviceFragment.checkMemberPresent();
        }
    }

    /**
     * Delete Group Request API Call Error Listener
     */
    private class DeleteGroupRequestErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Util.progressDialog.dismiss();
            Util.alertDilogBox(Constant.GROUP_DELETION_FAILURE, Constant.ALERT_TITLE, mContext);
        }
    }

    /**
     * Called when we delete group
     *
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }

}
