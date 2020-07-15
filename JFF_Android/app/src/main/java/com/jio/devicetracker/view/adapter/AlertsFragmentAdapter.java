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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.AlertHistoryData;

import java.util.List;

public class AlertsFragmentAdapter extends RecyclerView.Adapter<AlertsFragmentAdapter.ViewHolder>{

    private List<AlertHistoryData> mAlertHistoryData;

    public AlertsFragmentAdapter(List<AlertHistoryData> mAlertHistoryData) {
        this.mAlertHistoryData = mAlertHistoryData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.alerts_fragment_adapter, parent, false);
        return new AlertsFragmentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertsFragmentAdapter.ViewHolder holder, int position) {
        holder.dateTextView.setText(mAlertHistoryData.get(position).getDate());
        holder.geofenceAddress.setText(mAlertHistoryData.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return mAlertHistoryData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dateTextView;
        private TextView geofenceAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            geofenceAddress = itemView.findViewById(R.id.geofenceAddress);
        }
    }
}