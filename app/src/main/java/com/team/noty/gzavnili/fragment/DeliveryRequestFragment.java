package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.team.noty.gzavnili.adapters.ParcelPaymentData;
import com.team.noty.gzavnili.adapters.RVAdapter;
import com.team.noty.gzavnili.api.GetTerSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.card.payment.B;
import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class DeliveryRequestFragment extends Fragment {

    View mView;
    TextView  mTxtFirstName, mTxtLastName, mTxtCellPhone, mTxtAddress, mTxtCity, mTxtPrivate;
    Spinner spinner;

    String mUrlGetReceiverList = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=receiverlist";
    String mUrlSendReceiverData = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=delrequest";
    String mApiCode = "testAPI", mUserCode, strReceiveredId, strFirstName, strLastName, strPhone,
            strAddress, strCity, strPrivate, mParcelId, language;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();
    String[] titleReceiver, receiveredId, splitId;
    Button btnSubmit;
    BottomNavActivity bottomNavActivity;

    private RecyclerView rv;

    private List<ParcelPaymentData> parcelPaymentDatas = new ArrayList<>();
    String mUrlGetParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_delivery_request, container, false);

        Paper.init(getContext());

        mParcelId = Paper.book().read("parcelID");
        mUserCode = Paper.book().read("UserCode");
        language = Paper.book().read("language");

        bottomNavActivity = (BottomNavActivity) getActivity();

        mTxtFirstName = (TextView) mView.findViewById(R.id.txt_first_name);
        mTxtLastName = (TextView) mView.findViewById(R.id.txt_last_name);
        mTxtCellPhone = (TextView) mView.findViewById(R.id.txt_cell_phone);
        mTxtAddress = (TextView) mView.findViewById(R.id.txt_address);
        mTxtCity = (TextView) mView.findViewById(R.id.txt_city);
        mTxtPrivate = (TextView) mView.findViewById(R.id.txt_private);

        rv=(RecyclerView) mView.findViewById(R.id.recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        spinner = (Spinner) mView.findViewById(R.id.spinner);

        btnSubmit = (Button) mView.findViewById(R.id.btn_submit);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setContent(position);
                strReceiveredId = receiveredId[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDeliveryData(mUserCode);
            }
        });

        splitId = mParcelId.split(",");

        getChooseReceiver();
        getParcelList();


        return mView;
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
                        Log.d("MyLog", "response" + response);
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                JSONArray jsonArray = null;
                                jsonArray = new JSONArray(jsonObject.getString("DATA"));
                                Gson gson = new Gson();
                                getTerSetters = gson.fromJson(jsonArray.toString(),
                                        new TypeToken<List<GetTerSetter>>() {}.getType());

                                int step = 0;
                                if (getTerSetters.size() != 0) {
                                    for (int i = 0; i < getTerSetters.size(); i++) {
                                        if (splitId.length - 1 != 0) {
                                            if (step == splitId.length - 1) {
                                                break;
                                            } else if (!splitId[step].trim().equals("")) {
                                                if (splitId[step].trim().equals(getTerSetters.get(i).getId())) {
                                                    parcelPaymentDatas.add(new ParcelPaymentData(
                                                            getTerSetters.get(i).getTrackingNumber(), getTerSetters.get(i).getStore(),
                                                            getTerSetters.get(i).getWeight()));
                                                    step++;
                                                }
                                            }
                                        }
                                        else{
                                            if (splitId[step].trim().equals(getTerSetters.get(i).getId())) {
                                                parcelPaymentDatas.add(new ParcelPaymentData(
                                                        getTerSetters.get(i).getTrackingNumber(), getTerSetters.get(i).getStore(),
                                                        getTerSetters.get(i).getWeight()));
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
                params.put("language", language);
                return params;
            }
        };
        queue.add(strRequest);

    }
    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(parcelPaymentDatas);
        rv.setAdapter(adapter);
    }
    public void setContent(int position){

        strFirstName = getTerSetters.get(position).getFirstName();
        strLastName = getTerSetters.get(position).getLastName();
        strPhone =  getTerSetters.get(position).getCellPhone();
        strAddress = getTerSetters.get(position).getStreet();
        strCity = getTerSetters.get(position).getCity();
        strPrivate = getTerSetters.get(position).getPrivate();

        mTxtFirstName.setText(strFirstName);
        mTxtLastName.setText(strLastName);
        mTxtCellPhone.setText(strPhone);
        mTxtAddress.setText(strAddress);
        mTxtCity.setText(strCity);
        mTxtPrivate.setText(strPrivate);
    }
    public void getChooseReceiver() {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetReceiverList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", response);
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

                                    setContent(0);

                                    titleReceiver = new String[getTerSetters.size()];
                                    receiveredId = new String[getTerSetters.size()];
                                    for (int i = 0; i < getTerSetters.size(); i++){
                                        receiveredId[i] = getTerSetters.get(i).getReceiveredId();
                                        titleReceiver[i] = getTerSetters.get(i).getFirstName() +
                                                getTerSetters.get(i).getLastName();
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext().getApplicationContext(),
                                                R.layout.item_spinner, titleReceiver);
                                        spinner.setAdapter(adapter);
                                        spinner.setSelection(0);
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
                params.put("language", language);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public void sendDeliveryData(final String mUserCode) {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlSendReceiverData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response receiver " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                Toast.makeText(getContext(), jsonObject.getString("MESSAGE"), Toast.LENGTH_SHORT).show();
                                FragmentManager fragmentManager = getFragmentManager();
                                if (fragmentManager.getBackStackEntryCount() != 0) {
                                    fragmentManager.popBackStack();
                                }
                                bottomNavActivity.replaceFragment(new MakePaymentFragment());
                                bottomNavActivity.changeToolbar(6);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        showProgressBar(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgressBar(false);
                        Log.d("MyLog", "response error " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apicode", mApiCode);
                params.put("usercode", mUserCode);
                params.put("parcelid", mParcelId);
                params.put("ReceiverID", strReceiveredId);
                params.put("firstname", strFirstName);
                params.put("lastname", strLastName);
                params.put("address", strAddress);
                params.put("city", strCity);
                params.put("cellphone", strPhone);
                params.put("private", strPrivate);
                return params;
            }
        };
        queue.add(strRequest);

    }

}
