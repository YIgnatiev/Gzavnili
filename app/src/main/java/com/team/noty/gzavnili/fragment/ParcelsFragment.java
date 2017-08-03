package com.team.noty.gzavnili.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.adapters.SimpleFragmentPagerAdapter;
import com.team.noty.gzavnili.api.GetTerSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class ParcelsFragment extends Fragment{

    View mView;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    String[] mTitle, mStatus;
    String mUrlGetStatus = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcelList";
    String mApiCode = "testAPI", mUserCode;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_parcels, container, false);

        Paper.init(getContext());

        mUserCode = Paper.book().read("UserCode");

        mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);


        mTabLayout = (TabLayout) mView.findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTitleAndStatus();
    }

    private void setupViewPager(ViewPager viewPager) {
        SimpleFragmentPagerAdapter simpleFragmentPagerAdapter =
                new SimpleFragmentPagerAdapter(getChildFragmentManager(), getContext(), mTitle, mStatus);
        viewPager.setAdapter(simpleFragmentPagerAdapter);
    }

    public void getTitleAndStatus() {
        showProgressBar(true);
        getTerSetters.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetStatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                JSONArray jsonArray = null;
                                jsonArray = new JSONArray(jsonObject.getString("DATA"));
                                Gson gson = new Gson();
                                getTerSetters = gson.fromJson(jsonArray.toString(), new TypeToken<List<GetTerSetter>>() {
                                }.getType());
                                if (getTerSetters.size() != 0) {
                                    mTitle = new String[getTerSetters.size()];
                                    mStatus = new String[getTerSetters.size()];
                                    for (int i = 0; i < getTerSetters.size(); i++) {
                                        mTitle[i] = getTerSetters.get(i).getLongName();
                                        mStatus[i] = getTerSetters.get(i).getShortName();
                                    }
                                    setupViewPager(mViewPager);
                                }
                            }
                            showProgressBar(false);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgressBar(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apicode", mApiCode);
                params.put("usercode", mUserCode);
                return params;
            }
        };
        queue.add(strRequest);
    }

}
