package com.jio.devicetracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.jio.devicetracker.R;
import com.jio.devicetracker.database.pojo.HelpPagedata;

import java.util.List;

/**
 * Implementation of adapter for help page's content.
 */
public class HelpPageAdapter extends PagerAdapter {
    private Context mContext;
    private List<HelpPagedata> list;
    private LayoutInflater layoutInflater;
    private TextView mHeading;
    private ImageView mHelpImage;

    public HelpPageAdapter(Context context, List<HelpPagedata> mList)
    {
        mContext = context;
        list = mList;
        this.layoutInflater=LayoutInflater.from(mContext);

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==(object);
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.activity_help_content, container, false);
        mHeading = view.findViewById(R.id.heading);
        mHelpImage = view.findViewById(R.id.helpImage);
        mHelpImage.setImageResource(list.get(position).getImage());
        mHeading.setText(list.get(position).getTitle());

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
