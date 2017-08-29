package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.R;

import java.util.List;

public class AddFoundsFragment extends BaseFragment {

    View mView;
    BottomNavActivity bottomNavActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_add_funds, container, false);

        bottomNavActivity = (BottomNavActivity) getActivity();

        return mView;
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
            bottomNavActivity.changeToolbar(8);
            return true;
        }
        else {
            return false;
        }
    }

}
