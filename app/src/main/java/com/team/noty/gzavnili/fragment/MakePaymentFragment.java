package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.adapters.ParcelData;
import com.team.noty.gzavnili.adapters.ParcelPaymentData;
import com.team.noty.gzavnili.adapters.ParcelsListAdapter;
import com.team.noty.gzavnili.adapters.RVAdapter;
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

public class MakePaymentFragment extends Fragment{

    View mView;
    private RecyclerView rv;

    String mUrlPaymentInfo = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=paymentinfo";
    String mApiCode = "testAPI", mUserCode, mParcelId;
    private List<ParcelPaymentData> parcelPaymentDatas = new ArrayList<>();
    String mUrlGetParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_make_payment, container, false);

        Paper.init(getContext());

        mParcelId = Paper.book().read("parcelID");
        mUserCode = Paper.book().read("UserCode");

        rv=(RecyclerView) mView.findViewById(R.id.recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        getPaymentInfo();
        getParcelList();

        return mView;
    }

    public void getPaymentInfo() {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlPaymentInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", response);
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {

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
                params.put("parcelid", mParcelId);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public void getParcelList() {

        showProgressBar(true);
        getTerSetters.clear();
        parcelPaymentDatas.clear();

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
                                        new TypeToken<List<GetTerSetter>>() {}.getType());
                                String[] splitId = mParcelId.split(",");
                                int step = 0;
                                if (getTerSetters.size() != 0) {
                                    for (int i = 0; i < getTerSetters.size(); i++) {
                                        Log.d("MyLog", "size " + splitId.length + " " + splitId[step] + " " + getTerSetters.get(i).getId());
                                        if (step == splitId.length - 1){
                                            break;
                                        }
                                        else if (!splitId[step].trim().equals("")) {
                                            if (splitId[step].trim().equals(getTerSetters.get(i).getId())) {
                                                parcelPaymentDatas.add(new ParcelPaymentData(
                                                        getTerSetters.get(i).getTrackingNumber(), getTerSetters.get(i).getStore(),
                                                        getTerSetters.get(i).getWeight(), getTerSetters.get(i).getPrice()));
                                                step++;
                                            }
                                        }
                                    }
                                    initializeAdapter();

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
                params.put("status", "");
                return params;
            }
        };
        queue.add(strRequest);

    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(parcelPaymentDatas);
        rv.setAdapter(adapter);
    }
}
