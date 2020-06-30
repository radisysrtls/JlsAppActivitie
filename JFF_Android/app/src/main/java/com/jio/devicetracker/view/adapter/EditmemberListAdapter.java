package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.GroupMemberDataList;
import com.jio.devicetracker.util.Util;

import java.util.List;


public class EditmemberListAdapter extends RecyclerView.Adapter<EditmemberListAdapter.ViewHolder> {
    private List<GroupMemberDataList> mList;
    private Context mContext;
    private RecyclerViewClickListener itemListener;

    /**
     * Constructor to add devices inside group
     * @param mList
     */
    public EditmemberListAdapter(List<GroupMemberDataList> mList,Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    /**
     * Binds the given View to the position
     * @param parent
     * @param viewType
     * @return View Holder object
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_edit_member_list, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * A new ViewHolder that holds a View of the given view type
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(mList.get(position).getName());
        holder.name.setTypeface(Util.mTypeface(mContext, 5));
        holder.deleteIcon.setOnClickListener(v -> itemListener.clickonDeleteButton(mList.get(position), position));
    }

    /**
     * Interface to override methods in Edit Member Activity to call these methods on particular item click
     */
    public interface RecyclerViewClickListener {
        void clickonDeleteButton(GroupMemberDataList homeActivityListData, int position);
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
     * @return size
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView deleteIcon;

        /**
         * Constructor where we find element from .xml file
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.memberName);
            name.setTypeface(Util.mTypeface(mContext,5));
            deleteIcon = itemView.findViewById(R.id.delete_icon);
        }
    }

    /**
     * Called when we remove device from active member screen
     * @param adapterPosition
     */
    public void removeItem(int adapterPosition) {
        mList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyDataSetChanged();
    }

}