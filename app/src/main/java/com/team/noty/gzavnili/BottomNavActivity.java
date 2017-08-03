package com.team.noty.gzavnili;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.noty.gzavnili.fragment.CalculatorFragment;
import com.team.noty.gzavnili.fragment.NotificationsFragment;
import com.team.noty.gzavnili.fragment.OfficesFragment;
import com.team.noty.gzavnili.fragment.ParcelsFragment;
import com.team.noty.gzavnili.fragment.SettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class BottomNavActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_parcels:
                    replaceFragment(new ParcelsFragment());
                    mToolbar.setTitle(getString(R.string.title_parcels));
                    changeToolbar(0);
                    return true;
                case R.id.navigation_notifications:
                    replaceFragment(new NotificationsFragment());
                    mToolbar.setTitle(getString(R.string.title_notifications));
                    return true;
                case R.id.navigation_calculator:
                    replaceFragment(new CalculatorFragment());
                    mToolbar.setTitle(getString(R.string.title_calculator));
                    return true;
                case R.id.navigation_offices:
                    replaceFragment(new OfficesFragment());
                    mToolbar.setTitle(getString(R.string.title_offices));
                    return true;
                case R.id.navigation_settings:
                    replaceFragment(new SettingsFragment());
                    mToolbar.setTitle(getString(R.string.title_settings));
                    return true;
            }
            return false;
        }

    };

    Toolbar mToolbar;
    String mData, mUserCode;
    static RelativeLayout mRelProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.title_parcels));
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mData = null;
            } else {
                mData = extras.getString("response");
            }
        } else {
            mData = (String) savedInstanceState.getSerializable("response");
        }

        mUserCode = parseResponse(mData);

        Paper.init(this);

        Paper.book().write("UserCode", mUserCode);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mRelProgressBar = (RelativeLayout) findViewById(R.id.rel_progress);

        replaceFragment(new ParcelsFragment());

    }

    public static void showProgressBar(boolean show){
        if (show) {
            mRelProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            mRelProgressBar.setVisibility(View.GONE);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void changeToolbar(int view){
        if (view == 1) {
            mToolbar.setTitle(R.string.title_detail_parcel);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(0);
                }
            });
        }
        else if (view == 0){
            mToolbar.setTitle(R.string.title_parcels);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    public String parseResponse(String mData){

        String temp = mData.replaceAll("\\[", "");
        String newData = temp.replaceAll("]", "");
        try {
            JSONObject jsonObject = new JSONObject(newData);
            return jsonObject.getString("USERCODE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
