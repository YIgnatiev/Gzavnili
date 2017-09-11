package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class NewParcelFragment extends Fragment {

    View mView;
    Button btnSubmit;
    Spinner mSpinnerContent, spinner;
    EditText mTxtFirstName, mTxtLastName, mTxtCellPhone, mTxtAddress, mTxtCity, mTxtPrivate,
            mEditTrackingNumber, mEditValue, mEditStore;

    String mUrlGetContentList = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=contentlist";
    String mUrlGetReceiverList = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=receiverlist";
    String mUrlAddnewParcel = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=addparcel";
    String mApiCode = "testAPI",  mUserCode, mStrContent = "", strReceiveredId = "", strFirstName = "",
            strLastName = "", strPhone = "", strAddress = "", strCity = "", strPrivate = "", language = "",
            strTracking = "", strValue = "", strStore = "";
    ArrayList<GetTerSetter> contentList = new ArrayList<>();
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();
    BottomNavActivity bottomNavActivity;
    String[] titleReceiver, receiveredId, shortName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_new_parcel, container, false);

        Paper.init(getContext());

        mUserCode = Paper.book().read("UserCode");
        language = Paper.book().read("language");


        bottomNavActivity = (BottomNavActivity) getActivity();

        mSpinnerContent = (Spinner) mView.findViewById(R.id.txt_content);
        spinner = (Spinner) mView.findViewById(R.id.spinner);

        btnSubmit = (Button) mView.findViewById(R.id.btn_submit);

        mTxtFirstName = (EditText) mView.findViewById(R.id.txt_first_name);
        mTxtLastName = (EditText) mView.findViewById(R.id.txt_last_name);
        mTxtCellPhone = (EditText) mView.findViewById(R.id.txt_cell_phone);
        mTxtAddress = (EditText) mView.findViewById(R.id.txt_address);
        mTxtCity = (EditText) mView.findViewById(R.id.txt_city);
        mTxtPrivate = (EditText) mView.findViewById(R.id.txt_private);
        mEditStore = (EditText) mView.findViewById(R.id.edit_store);
        mEditTrackingNumber = (EditText) mView.findViewById(R.id.edit_tracking_number);
        mEditValue = (EditText) mView.findViewById(R.id.edit_value);

        getContentList(mUserCode);
        getChooseReceiver();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAllFields()){
                    addNewParcel(mUserCode);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    setContent(position - 1);
                    strReceiveredId = receiveredId[position - 1];
                }else {
                    strReceiveredId = "";
                    strFirstName = "";
                    strLastName = "";
                    strPhone =  "";
                    strAddress = "";
                    strCity = "";
                    strPrivate = "";

                    mTxtFirstName.setText(strFirstName);
                    mTxtLastName.setText(strLastName);
                    mTxtCellPhone.setText(strPhone);
                    mTxtAddress.setText(strAddress);
                    mTxtCity.setText(strCity);
                    mTxtPrivate.setText(strPrivate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return mView;
    }


    public void getContentList(final String mUserCode) {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetContentList,
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
                                contentList = gson.fromJson(jsonArray.toString(),
                                        new TypeToken<List<GetTerSetter>>() {}.getType());
                                if (contentList.size() != 0) {
                                    shortName = new String[contentList.size()];
                                    for (int i = 0; i < contentList.size(); i++){
                                        shortName[i] = contentList.get(i).getShortName();
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext().getApplicationContext(), R.layout.item_spinner, shortName);
                                    mSpinnerContent.setAdapter(adapter);
                                    choosePositionSelected();
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
    public void choosePositionSelected(){
        if (mStrContent != null) {
            if (!mStrContent.trim().equals("")) {
                for (int i = 0; i < shortName.length; i++) {
                    if (shortName[i].equals(mStrContent)) {
                        mSpinnerContent.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    public boolean checkAllFields(){
        strFirstName = mTxtFirstName.getText().toString().trim();
        strLastName =  mTxtLastName.getText().toString().trim();
        strPhone =  mTxtCellPhone.getText().toString().trim();
        strAddress =  mTxtAddress.getText().toString().trim();
        strCity =  mTxtCity.getText().toString().trim();
        strPrivate = mTxtPrivate.getText().toString().trim();

        strTracking = mEditTrackingNumber.getText().toString().trim();
        strValue = mEditValue.getText().toString().trim();
        strStore = mEditStore.getText().toString().trim();

        if (strFirstName.length() == 0 && strLastName.length() == 0 && strPhone.length() == 0
                && strAddress.length() == 0 && strCity.length() == 0 && strPrivate.length() == 0
                && strTracking.length() == 0 && strValue.length() == 0 && strStore.length() == 0){
            return false;
        }
        else {
            return true;
        }
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

                                    titleReceiver = new String[getTerSetters.size() + 1];
                                    titleReceiver[0] = "";
                                    receiveredId = new String[getTerSetters.size()];
                                    for (int i = 0; i < getTerSetters.size(); i++){
                                        receiveredId[i] = getTerSetters.get(i).getReceiveredId();
                                        titleReceiver[i + 1] = getTerSetters.get(i).getFirstName() +
                                                getTerSetters.get(i).getLastName();
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext().getApplicationContext(),
                                                R.layout.item_spinner, titleReceiver);
                                        spinner.setAdapter(adapter);
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

    public void addNewParcel(final String mUserCode) {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlAddnewParcel,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response receiver " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                FragmentManager fragmentManager = getFragmentManager();
                                if (fragmentManager.getBackStackEntryCount() != 0) {
                                    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                                        fragmentManager.popBackStack();
                                    }
                                }
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
                params.put("Tracking", strTracking);
                params.put("store", strStore);
                params.put("content", mStrContent);
                params.put("value", strValue);
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
