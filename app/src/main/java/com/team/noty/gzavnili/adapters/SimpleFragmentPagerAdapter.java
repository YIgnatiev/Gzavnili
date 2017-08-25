package com.team.noty.gzavnili.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.fragment.ParcelListFragment;


/**
 * Created by copch on 01.08.2017.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    String[] mTitle, mStatus;
    ParcelListFragment fragment;

    public SimpleFragmentPagerAdapter(FragmentManager Fm, Context context, String[] mTitle, String[] mStatus) {
        super(Fm);
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mTitle = mTitle;
        this.mStatus = mStatus;
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }


    @Override
    public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        arguments.putString("status", mStatus[position]);
        fragment = new ParcelListFragment();
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }


}