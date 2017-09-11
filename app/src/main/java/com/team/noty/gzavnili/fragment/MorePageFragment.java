package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.adapters.OfficesPagerAdapter;
import com.team.noty.gzavnili.adapters.ParcelData;
import com.team.noty.gzavnili.adapters.ParcelsListAdapter;
import com.team.noty.gzavnili.api.GetTerSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class MorePageFragment extends Fragment implements View.OnClickListener {

    View mView;
    LinearLayout layoutNewParcel, layoutDeliveryRequest, layoutMakePayment, layoutAddFunds,
            layoutSchedule, layoutSettings;

    BottomNavActivity bottomNavActivity;
    String mUrlGetUnpaidParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";
    String mApiCode = "testAPI", mUserCode;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_more_pages, container, false);

        Paper.init(getContext());

        bottomNavActivity = (BottomNavActivity) getActivity();

        mUserCode = Paper.book().read("UserCode");

        layoutNewParcel = (LinearLayout) mView.findViewById(R.id.layout_new_parcel);
        layoutDeliveryRequest = (LinearLayout) mView.findViewById(R.id.layout_delivery_request);
        layoutMakePayment = (LinearLayout) mView.findViewById(R.id.layout_make_payment);
        layoutAddFunds = (LinearLayout) mView.findViewById(R.id.layout_add_funds);
        layoutSchedule = (LinearLayout) mView.findViewById(R.id.layout_schedule);
        layoutSettings = (LinearLayout) mView.findViewById(R.id.layout_settings);

        layoutNewParcel.setOnClickListener(this);
        layoutDeliveryRequest.setOnClickListener(this);
        layoutMakePayment.setOnClickListener(this);
        layoutAddFunds.setOnClickListener(this);
        layoutSchedule.setOnClickListener(this);
        layoutSettings.setOnClickListener(this);


        return mView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.layout_new_parcel:
                bottomNavActivity.replaceFragment(new NewParcelFragment());
                bottomNavActivity.changeToolbar(12);
            break;
            case R.id.layout_delivery_request:
                getParcelList(mUserCode, "", "delivery");
                break;
            case R.id.layout_make_payment:
                getParcelList(mUserCode, "", "payment");

                break;
            case R.id.layout_add_funds:
                bottomNavActivity.replaceFragment(new AddFoundsFragment());
                bottomNavActivity.changeToolbar(15);
                break;
            case R.id.layout_schedule:
                bottomNavActivity.replaceFragment(new ParcelScheduleFragment());
                bottomNavActivity.changeToolbar(16);
                break;
            case R.id.layout_settings:
                bottomNavActivity.replaceFragment(new SettingsFragment());
                bottomNavActivity.changeToolbar(17);
                break;
        }
    }

    public void getParcelList(final String mUserCode, final String mStatus, final String type) {

        showProgressBar(true);
        getTerSetters.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetUnpaidParcels,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response unpaid list " + response);
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                JSONArray jsonArray = null;
                                jsonArray = new JSONArray(jsonObject.getString("DATA"));
                                Gson gson = new Gson();
                                getTerSetters = gson.fromJson(jsonArray.toString(),
                                        new TypeToken<List<GetTerSetter>>() {}.getType());
                                if (getTerSetters.size() != 0) {
                                    String parcelId = "";
                                    for (int i = 0; i < getTerSetters.size(); i++){
                                        parcelId += getTerSetters.get(i).getId() + ", ";
                                    }
                                    Paper.book().write("parcelID", parcelId);
                                    if (type.equals("payment")) {
                                        bottomNavActivity.replaceFragment(new MakePaymentFragment());
                                        bottomNavActivity.changeToolbar(14);
                                    }else {
                                        bottomNavActivity.replaceFragment(new DeliveryRequestFragment());
                                        bottomNavActivity.changeToolbar(18);
                                    }
                                }
                            }

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
                if (type.equals("payment")) {
                    params.put("isPaid", "N");
                }else if (type.equals("delivery")) {
                    params.put("isDelivery", "N");
                }
                params.put("status", mStatus);
                return params;
            }
        };
        queue.add(strRequest);

    }
}
