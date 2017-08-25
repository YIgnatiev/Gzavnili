package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.adapters.OfficesPagerAdapter;

public class OfficesFragment extends Fragment{

    View mView;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_offices, container, false);

        mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);

        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) mView.findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);

        return mView;
    }

    private void setupViewPager(ViewPager viewPager) {
        OfficesPagerAdapter adapter = new OfficesPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new UsaOfficesListFragment(), "USA");
        adapter.addFragment(new GeorgiaOfficesListFragment(), "GEORGIA");
        viewPager.setAdapter(adapter);
    }
}
