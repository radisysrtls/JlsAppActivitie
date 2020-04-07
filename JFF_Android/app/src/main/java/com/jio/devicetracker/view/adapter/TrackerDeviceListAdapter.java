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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.database.pojo.MultipleselectData;
import com.jio.devicetracker.util.Constant;


import java.util.List;

/**
 * Implementation of adapter for trackee list in Home Screen with grouping options.
 */
public class TrackerDeviceListAdapter extends RecyclerView.Adapter<TrackerDeviceListAdapter.ViewHolder> {

    private Context mContext;
    private List<HomeActivityListData> mData;
    private static RecyclerViewClickListener itemListener;
    private MultipleselectData mSelectData;

    // Constructor to add devices in home screen
    public TrackerDeviceListAdapter(Context mContext, List<HomeActivityListData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    // Binds the given View to the position
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_devicetracker_list, parent, false);

        return new ViewHolder(itemView);
    }

    //  A new ViewHolder that holds a View of the given view type
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HomeActivityListData data = mData.get(position);
        holder.phone.setText(mData.get(position).getPhoneNumber());
        holder.name.setText(mData.get(position).getGroupName());
        if (mData.get(position).isGroupMember() == true) {
            holder.mIconImage.setImageResource(R.drawable.ic_group_button);
        } else if (mData.get(position).getDeviceType().equalsIgnoreCase("People Tracker")) {
            holder.mIconImage.setImageResource(R.drawable.ic_user);
        } else if (mData.get(position).getDeviceType().equalsIgnoreCase("Pet Tracker")) {
            holder.mIconImage.setImageResource(R.drawable.ic_pet);
            holder.mConsentStatus.setVisibility(View.INVISIBLE);
        }
        // holder.mDelete.setTransformationMethod(null);
        // holder.mEdit.setTransformationMethod(null);
        holder.mConsentStatus.setTransformationMethod(null);
        if (mData.get(position).getConsentStaus() != null && mData.get(position).getConsentStaus().trim().equalsIgnoreCase(Constant.CONSENT_APPROVED_STATUS)) {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentApproved));
            holder.mConsentStatus.setText(Constant.CONSENT_APPROVED_STATUS);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approved, 0, 0, 0);
            holder.mConsentStatus.setEnabled(false);
        } else if (mData.get(position).getConsentStaus() != null && mData.get(position).getConsentStaus().trim().equals(Constant.CONSENT_PENDING)) {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentPending));
            holder.mConsentStatus.setText(Constant.CONSENT_PENDING);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pending, 0, 0, 0);
            holder.mConsentStatus.setEnabled(false);
        } else {
            holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.colorConsentNotSent));
            holder.mConsentStatus.setText(Constant.REQUEST_CONSENT);
            holder.mConsentStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notsent, 0, 0, 0);
        }

        if (!data.isGroupMember()) {
            holder.mConsentStatus.setOnClickListener(v -> {
                itemListener.consetClick(mData.get(position).getPhoneNumber());
            });
        } else {
            holder.mConsentStatus.setOnClickListener(v -> {
                itemListener.consentClickForGroup(mData.get(position).getName());
            });
        }
        holder.mConsent.setOnClickListener(v -> {
            data.setSelected(!data.isSelected());
            if (data.isGroupMember() && data.isSelected()) {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkmark);
                mSelectData = new MultipleselectData();
                mSelectData.setName(mData.get(position).getName());
                itemListener.recyclerViewListClicked(v, position, mSelectData, 1);
            } else if (data.isSelected()) {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkmark);
                mSelectData = new MultipleselectData();
                mSelectData.setPhone(mData.get(position).getPhoneNumber());
                mSelectData.setLat(mData.get(position).getLat());
                mSelectData.setLng(mData.get(position).getLng());
                mSelectData.setName(mData.get(position).getName());
                mSelectData.setImeiNumber(mData.get(position).getImeiNumber());
                itemListener.recyclerViewListClicked(v, position, mSelectData, 2);
            } else if(data.isGroupMember()){
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkboxempty);
                itemListener.recyclerViewListClicked(v, position, mSelectData, 3);
            } else {
                holder.mConsent.setBackgroundResource(R.drawable.ic_checkboxempty);
                itemListener.recyclerViewListClicked(v, position, mSelectData, 3);
            }
        });

        // holder.mEdit.setOnClickListener(v -> itemListener.recyclerviewEditList(mData.get(position).getRelation(),mData.get(position).getPhoneNumber()));
        // holder.mDelete.setOnClickListener(v -> itemListener.recyclerviewDeleteList(mData.get(position).getPhoneNumber(),position));

        holder.viewOptionMenu.setOnClickListener(v -> itemListener.onPopupMenuClicked(holder.viewOptionMenu, position, mData.get(position).getGroupName(), mData.get(position).getPhoneNumber()));

        holder.mListlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        if (data.isGroupMember()) {
            holder.mListlayout.setOnClickListener(v -> {
                itemListener.clickonListLayout(mData.get(position).getGroupName());
                return;
            });
        }
    }


    // return The total number of items in this adapter
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView phone;
        public TextView name;
        public TextView relation;
        public TextView status;
        public CardView mListlayout;
        public Button mConsent;
        public Button mConsentStatus;
        public TextView viewOptionMenu;
        public ImageView mIconImage;

        // Constructor where we find element from .xml file
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phoneNumber);
            name = itemView.findViewById(R.id.nameDeviceTracker);
            mListlayout = itemView.findViewById(R.id.listLayout);
            mConsent = itemView.findViewById(R.id.consent);
            mConsentStatus = itemView.findViewById(R.id.consentstatus);
            viewOptionMenu = itemView.findViewById(R.id.textViewOptions);
            status = itemView.findViewById(R.id.statusView);
            mIconImage = itemView.findViewById(R.id.contactImage);
        }
    }

    // Interface to override methods in Dashboard to call those methods on particular item click
    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position, MultipleselectData data, int val);

        // void recyclerviewEditList(String relation,String phoneNumber);
        // void recyclerviewDeleteList(String phoneNuber,int position);
        void clickonListLayout(String selectedGroupName);

        void consetClick(String phoneNumber);

        void consentClickForGroup(String selectedGroupName);

        void onPopupMenuClicked(View v, int position, String name, String number);
    }

    // Register the listener
    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
    }

    // Called when we remove device from home screen
    public void removeItem(int adapterPosition) {
        mData.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }
}
