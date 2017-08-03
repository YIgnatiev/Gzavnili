package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class ParcelListFragment extends Fragment{

    View mView;
    ListView mListView;

    String mUrlGetParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";
    String mApiCode = "testAPI", mUserCode, mStatus;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();
    ArrayList<ParcelData> parcelDatas = new ArrayList<>();
    ParcelsListAdapter adapter;
    BottomNavActivity bottomNavActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_list, container, false);

        Paper.init(getContext());
        Bundle arguments = getArguments();
        mStatus = arguments.getString("status");

        mUserCode = Paper.book().read("UserCode");

        mListView = (ListView) mView.findViewById(R.id.list_view);

        bottomNavActivity = (BottomNavActivity) getActivity();

        getParcelList(mUserCode, mStatus);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bottomNavActivity.replaceFragment(new DetailParcelInfoFragment());
                bottomNavActivity.changeToolbar(1);
            }
        });

        return mView;
    }

    public void getParcelList(final String mUserCode, final String mStatus) {

        showProgressBar(true);
        getTerSetters.clear();
        parcelDatas.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetParcels,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response parcels " + response);
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
                                    boolean mCorrect, mUnpaid;
                                    for (int i = 0; i < getTerSetters.size(); i++) {
                                        mCorrect = isCorrect(getTerSetters.get(i).getmValue(),
                                                getTerSetters.get(i).getmStore(),
                                                getTerSetters.get(i).getmContents());

                                        mUnpaid = isPaid(getTerSetters.get(i).getmPaid(),
                                                getTerSetters.get(i).getmDept());

                                        parcelDatas.add(new ParcelData(getTerSetters.get(i).getLocation(),
                                                getTerSetters.get(i).getTrackingNumber(), mCorrect, mUnpaid));
                                    }
                                    adapter = new ParcelsListAdapter(getContext(), parcelDatas);
                                    mListView.setAdapter(adapter);
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
                params.put("status", mStatus);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public boolean isCorrect(String mValue, String mStore, String mContents){
        boolean correct;
        if (mContents.length() != 0 && mValue.length() != 0 && Double.parseDouble(mValue) > 0 &&
                mStore.length() != 0){
            correct = false;
        }
        else {
            correct = true;
        }

        return correct;
    }

    public boolean isPaid(String mPaid, String mDept){
        boolean correct;
        if (mPaid.length() != 0 && mDept.length() != 0 && Double.parseDouble(mPaid) > 0){
            correct = false;
        }
        else {
            correct = true;
        }

        return correct;
    }

}
