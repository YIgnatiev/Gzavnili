package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class ParcelListFragment extends Fragment {

    public ParcelsListAdapter adapter;
    View mView;
    ListView mListView;
    String mUrlGetParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";
    String mApiCode = "testAPI", mUserCode, mStatus, language;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();
    ArrayList<ParcelData> parcelDatas = new ArrayList<>();
    BottomNavActivity bottomNavActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_list, container, false);

        Paper.init(getContext());
        Bundle arguments = getArguments();
        mStatus = arguments.getString("status");

        mUserCode = Paper.book().read("UserCode");
        language = Paper.book().read("language");

        mListView = (ListView) mView.findViewById(R.id.list_view);

        bottomNavActivity = (BottomNavActivity) getActivity();

        getParcelList(mUserCode, mStatus);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Paper.book().write("tracking", getTerSetters.get(position).getId());
                bottomNavActivity.replaceFragment(new DetailParcelInfoFragment());
                bottomNavActivity.changeToolbar(1);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                bottomNavActivity.createFloatingButtonMenu(getTerSetters.get(position).getStatus(),
                        getTerSetters.get(position).getTrackingNumber(),
                        getTerSetters.get(position).getId(), getTerSetters.get(position).getStore(),
                        getTerSetters.get(position).getContents(), getTerSetters.get(position).getValue(),
                        getTerSetters.get(position).getDept(), getTerSetters.get(position).getPaid(),
                        getTerSetters.get(position).getDeliveryRequest());
                return true;
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
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                JSONArray jsonArray = null;
                                jsonArray = new JSONArray(jsonObject.getString("DATA"));
                                Gson gson = new Gson();
                                getTerSetters = gson.fromJson(jsonArray.toString(),
                                        new TypeToken<List<GetTerSetter>>() {
                                        }.getType());
                                if (getTerSetters.size() != 0) {
                                    boolean mCorrect, mUnpaid;
                                    for (int i = 0; i < getTerSetters.size(); i++) {
                                        mCorrect = isCorrect(getTerSetters.get(i).getValue(),
                                                getTerSetters.get(i).getStore(),
                                                getTerSetters.get(i).getContents());

                                        mUnpaid = isPaid(getTerSetters.get(i).getPaid(),
                                                getTerSetters.get(i).getDept());

                                        parcelDatas.add(new ParcelData(getTerSetters.get(i).getLocation(),
                                                getTerSetters.get(i).getTrackingNumber(), mCorrect, mUnpaid,
                                                getTerSetters.get(i).getId(), false, getTerSetters.get(i).getStatus(),
                                                getTerSetters.get(i).getDateCreated()));
                                    }
                                    Collections.sort(parcelDatas, byDate);
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
                params.put("language", language);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public Comparator<ParcelData> byDate = new Comparator<ParcelData>() {
        SimpleDateFormat format = new java.text.SimpleDateFormat("MMMMM, dd yyyy HH:mm:ss",
                Locale.US);

        public int compare(ParcelData ord1, ParcelData ord2) {
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(ord1.getDateCreate());
                d2 = format.parse(ord2.getDateCreate());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return (d1.getTime() > d2.getTime() ? -1 : 1);     //descending
            //  return (d1.getTime() > d2.getTime() ? 1 : -1);     //ascending
        }
    };
    public String getParcelsId() {
        String parcelId = "";
        for (ParcelData p : adapter.getBox()) {
            if (p.box) {
                parcelId += p.getParcelId() + ", ";
            }
        }
        return parcelId;
    }

    public String[] getStatusList() {
        String[] parcelId = new String[adapter.getBox().size()];
        int i = 0;
        for (ParcelData p : adapter.getBox()) {
            if (p.box) {
                parcelId[i] = p.getStatus();
            }
        }
        return parcelId;
    }

    public void updateList() {
        getParcelList(mUserCode, mStatus);
    }

    public void callSelected(boolean visibility) {
        adapter.isSelected(visibility);
        adapter.notifyDataSetChanged();
    }

    public boolean isCorrect(String mValue, String mStore, String mContents) {
        boolean correct;
        if (mContents.length() != 0 && mValue.length() != 0 && Double.parseDouble(mValue) > 0 &&
                mStore.length() != 0) {
            correct = false;
        } else {
            correct = true;
        }

        return correct;
    }


    public boolean isPaid(String mPaid, String mDept) {
        boolean correct;
        if (mPaid.length() != 0 && mDept.length() != 0 && Double.parseDouble(mPaid) > 0) {
            correct = false;
        } else {
            correct = true;
        }

        return correct;
    }

}
