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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupData;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.database.pojo.HomeActivityListData;
import com.jio.devicetracker.util.Constant;
import com.jio.devicetracker.util.Util;
import com.jio.devicetracker.view.EditMemberActivity;

import java.util.List;

/**
 * Display the group member inside the list
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private List<HomeActivityListData> mList;
    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public GroupListAdapter(List<HomeActivityListData> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_group_list_adapter, parent, false);
        return new GroupListAdapter.ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(@NonNull GroupListAdapter.ViewHolder holder, int position) {
        HomeActivityListData data = mList.get(position);
        holder.groupName.setTypeface(Util.mTypeface(mContext, 5));
        holder.groupName.setText(data.getGroupName());
        holder.mListlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.clickonListLayout(data);
            }
        });
    }

    /**
     * Interface to override methods in Groups Fragment to call these methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void clickonListLayout(HomeActivityListData homeActivityListData);
    }

    /**
     * Register the listener
     *
     * @param mItemClickListener
     */
    public void setOnItemClickPagerListener(RecyclerViewClickListener mItemClickListener) {
        this.itemListener = mItemClickListener;
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

        private TextView groupName;
        private ImageView menuIcon;
        private RelativeLayout groupOptLayout;
        private ImageView close;
        private ImageView icon1;
        private ImageView icon2;
        private ImageView icon3;
        private ImageView icon4;
        private TextView editOpt;
        private TextView addNewOpt;
        private TextView deleteOpt;
        public CardView mListlayout;

        /**
         * Constructor where we find element from .xml file
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            menuIcon = itemView.findViewById(R.id.operationStatus);
            close = itemView.findViewById(R.id.close);
            editOpt = itemView.findViewById(R.id.edit);
            addNewOpt = itemView.findViewById(R.id.add);
            deleteOpt = itemView.findViewById(R.id.delete);
            groupOptLayout = itemView.findViewById(R.id.oprationLayout);
            mListlayout = itemView.findViewById(R.id.groupListLayout);
            menuIcon.setOnClickListener(this);
            close.setOnClickListener(this);
            editOpt.setOnClickListener(this);
            addNewOpt.setOnClickListener(this);
            deleteOpt.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edit:
                    gotoEditMemberActivity();
                    break;
                case R.id.close:
                    groupOptLayout.setVisibility(View.GONE);
                    break;
                case R.id.operationStatus:
                    groupOptLayout.setVisibility(View.VISIBLE);
                    break;

            }
        }
    }

    private void gotoEditMemberActivity() {
        Intent intent = new Intent(mContext, EditMemberActivity.class);
        mContext.startActivity(intent);
    }

}