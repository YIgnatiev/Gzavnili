package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.games.GameEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.api.GetTerSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class DetailParcelInfoFragment extends Fragment implements View.OnClickListener {

    View mView;
    TextView mTxtTrackingNumber, mTxtWeight, mTxtServiceType, mTxtEdit, mTxtDone,
        mTxtPrice, mTxtFirstName, mTxtLastName, mTxtCellPhone, mTxtAddress, mTxtCity, mTxtPrivate;

    EditText mEditValue, mEditStore;
    Spinner mSpinnerContent;
    LinearLayout mLayoutReceiver, mLayoutTracking;

    String mUrlGetParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";
    String mUrlGetContentList = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=contentlist";
    String mUrlCorrectParcel = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcelcorrect";
    String mApiCode = "testAPI", mTracking, mUserCode, mParcelId, mStrContent, language;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();
    ArrayList<GetTerSetter> getTimeLine = new ArrayList<>();
    ArrayList<GetTerSetter> contentList = new ArrayList<>();
    String[] shortName, mDate, mLocation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_parcel_detail, container, false);

        Paper.init(getContext());

        mTracking = Paper.book().read("tracking");
        mUserCode = Paper.book().read("UserCode");
        language = Paper.book().read("language");

        mTxtTrackingNumber = (TextView) mView.findViewById(R.id.txt_tracking_number);
        mTxtWeight = (TextView) mView.findViewById(R.id.txt_weight);
        mTxtServiceType = (TextView) mView.findViewById(R.id.txt_service_type);
        mTxtPrice = (TextView) mView.findViewById(R.id.txt_price);
        mTxtFirstName = (TextView) mView.findViewById(R.id.txt_first_name);
        mTxtLastName = (TextView) mView.findViewById(R.id.txt_last_name);
        mTxtCellPhone = (TextView) mView.findViewById(R.id.txt_cell_phone);
        mTxtAddress = (TextView) mView.findViewById(R.id.txt_address);
        mTxtCity = (TextView) mView.findViewById(R.id.txt_city);
        mTxtPrivate = (TextView) mView.findViewById(R.id.txt_private);
        mTxtEdit = (TextView) mView.findViewById(R.id.txt_edit);
        mTxtDone = (TextView) mView.findViewById(R.id.txt_done);

        mEditStore = (EditText) mView.findViewById(R.id.edit_store);
        mEditValue = (EditText) mView.findViewById(R.id.edit_value);

        mSpinnerContent = (Spinner) mView.findViewById(R.id.txt_content);

        mLayoutReceiver = (LinearLayout) mView.findViewById(R.id.layout_receiver);
        mLayoutTracking = (LinearLayout) mView.findViewById(R.id.layout_tracking);

        mTxtEdit.setOnClickListener(this);
        mTxtDone.setOnClickListener(this);

        mSpinnerContent.setEnabled(false);

        getDetailInformation(mUserCode, mTracking);

        return mView;
    }

    public void getDetailInformation(final String mUserCode, final String mTracking) {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetParcels,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getContentList(mUserCode);
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
                                    mTxtTrackingNumber.setText(mTracking);
                                    mParcelId = getTerSetters.get(0).getId();
                                    mEditValue.setText(getTerSetters.get(0).getValue());
                                    mTxtWeight.setText(getTerSetters.get(0).getWeight());
                                    mEditStore.setText(getTerSetters.get(0).getStore());
                                    mTxtServiceType.setText(getTerSetters.get(0).getService());
                                    mTxtPrice.setText(getTerSetters.get(0).getPrice());
                                    mTxtFirstName.setText(getTerSetters.get(0).getFirstName());
                                    mTxtLastName.setText(getTerSetters.get(0).getLastName());
                                    mTxtCellPhone.setText(getTerSetters.get(0).getPhone1());
                                    mTxtAddress.setText(getTerSetters.get(0).getStreet());
                                    mTxtCity.setText(getTerSetters.get(0).getCity());
                                    mTxtPrivate.setText(getTerSetters.get(0).getPrivate());
                                    mStrContent = getTerSetters.get(0).getContents();
                                }

                                JSONArray jsonArrayTimeLine = null;
                                jsonArrayTimeLine = new JSONArray(jsonObject.getString("TIMELINE"));
                                Gson gsonTimeLine = new Gson();
                                getTimeLine = gsonTimeLine.fromJson(jsonArrayTimeLine.toString(),
                                        new TypeToken<List<GetTerSetter>>() {}.getType());
                                if (getTimeLine.size() != 0) {
                                    mLayoutTracking.setVisibility(View.VISIBLE);
                                    createLayout(mLayoutTracking, getTimeLine);
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
                params.put("tracking", mTracking);
                params.put("language", language);
                return params;
            }
        };
        queue.add(strRequest);

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

    public void sendCorrectData(final String mUserCode) {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlCorrectParcel,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response correct " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getString("STATUS").equals("S"))
                                getDetailInformation(mUserCode, mTracking);
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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apicode", mApiCode);
                params.put("usercode", mUserCode);
                params.put("parcelid", mParcelId);
                params.put("store", mEditStore.getText().toString());
                params.put("othercontent", shortName[mSpinnerContent.getSelectedItemPosition()]);
                params.put("value", mEditValue.getText().toString());
                return params;
            }
        };
        queue.add(strRequest);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.txt_edit:
                setEnabledView(true);
            break;
            case R.id.txt_done:
                setEnabledView(false);
                sendCorrectData(mUserCode);
                break;
        }

    }

    public void setEnabledView(boolean enabled){
        if (enabled){
            mEditValue.setEnabled(true);
            mEditStore.setEnabled(true);
            mSpinnerContent.setEnabled(true);
            mTxtEdit.setVisibility(View.GONE);
            mTxtDone.setVisibility(View.VISIBLE);
            mLayoutReceiver.setBackgroundColor(getResources().getColor(R.color.white));
            mLayoutReceiver.setAlpha((float) 0.2);
            mLayoutTracking.setBackgroundColor(getResources().getColor(R.color.white));
            mLayoutTracking.setAlpha((float) 0.2);
        }
        else {
            mEditValue.setEnabled(false);
            mEditStore.setEnabled(false);
            mSpinnerContent.setEnabled(false);
            mTxtEdit.setVisibility(View.VISIBLE);
            mTxtDone.setVisibility(View.GONE);
            mLayoutReceiver.setBackgroundResource(R.drawable.back_item_parcel);
            mLayoutReceiver.setAlpha(1);
            mLayoutTracking.setBackgroundResource(R.drawable.back_item_parcel);
            mLayoutTracking.setAlpha(1);
        }
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

    public void createLayout(LinearLayout linearLayout, ArrayList<GetTerSetter> getTerSetter){
        for (int i =  getTerSetter.size() - 1; i >= 0; i--){
            View child1 = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_for_detail_tracking, null);
            linearLayout.addView(child1);

            TextView txt_date = (TextView) child1.findViewById(R.id.txt_date);
            TextView txt_location = (TextView) child1.findViewById(R.id.txt_location);

            ImageView top_line = (ImageView) child1.findViewById(R.id.line_top);
            ImageView bottom_line = (ImageView) child1.findViewById(R.id.line_bottom);

            if (i == getTerSetter.size() - 1){
                top_line.setVisibility(View.INVISIBLE);
            }
            if (i == 0){
                bottom_line.setVisibility(View.INVISIBLE);
            }
            String time = "";
            SimpleDateFormat format = new SimpleDateFormat("MMMMM, dd yyyy HH:mm:ss",
                    Locale.US);

            SimpleDateFormat formatView = new SimpleDateFormat("yyyy/MM/dd",
                    Locale.US);

            try {
                Date newDate = format.parse(getTerSetter.get(i).getTimLine());
                time = formatView.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            txt_date.setText(time);
            txt_location.setText(getTerSetter.get(i).getLocation());
         }
    }

    /*public void setDataTracking(String status){
        switch (status){
            case "new":
                break;
            case "awaiting":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[1];
                mLocation = new String[1];
                mDate[0] = getTerSetters.get(0).getTrackingAway();

                mLocation[0] = "";
                createLayout(mLayoutTracking);
                break;
            case "received":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[2];
                mLocation = new String[2];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                createLayout(mLayoutTracking);
                break;
            case "onhold":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[2];
                mLocation = new String[2];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                createLayout(mLayoutTracking);
                break;
            case "notonhold":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[2];
                mLocation = new String[2];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                createLayout(mLayoutTracking);
                break;
            case "shipped":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[3];
                mLocation = new String[3];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();
                mDate[2] = getTerSetters.get(0).getTrackingSnipped();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                mLocation[2] = getString(R.string.transit);
                createLayout(mLayoutTracking);
                break;
            case "delay":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[4];
                mLocation = new String[4];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();
                mDate[2] = getTerSetters.get(0).getTrackingSnipped();
                mDate[3] = getTerSetters.get(0).getTrackingDelay();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                mLocation[2] = getString(R.string.transit);
                mLocation[3] = getString(R.string.transit);
                createLayout(mLayoutTracking);
                break;
            case "custom":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[5];
                mLocation = new String[5];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();
                mDate[2] = getTerSetters.get(0).getTrackingSnipped();
                mDate[3] = getTerSetters.get(0).getTrackingDelay();
                mDate[4] = getTerSetters.get(0).getTrackingCustom();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                mLocation[2] = getString(R.string.transit);
                mLocation[3] = getString(R.string.transit);
                mLocation[4] = getString(R.string.transit);
                createLayout(mLayoutTracking);
                break;
            case "office":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[6];
                mLocation = new String[6];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();
                mDate[2] = getTerSetters.get(0).getTrackingSnipped();
                mDate[3] = getTerSetters.get(0).getTrackingDelay();
                mDate[4] = getTerSetters.get(0).getTrackingCustom();
                mDate[5] = getTerSetters.get(0).getTrackinOffice();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                mLocation[2] = getString(R.string.transit);
                mLocation[3] = getString(R.string.transit);
                mLocation[4] = getString(R.string.transit);
                mLocation[5] = getString(R.string.tbilisi);
                createLayout(mLayoutTracking);
                break;
            case "region":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[7];
                mLocation = new String[7];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();
                mDate[2] = getTerSetters.get(0).getTrackingSnipped();
                mDate[3] = getTerSetters.get(0).getTrackingDelay();
                mDate[4] = getTerSetters.get(0).getTrackingCustom();
                mDate[5] = getTerSetters.get(0).getTrackinOffice();
                mDate[6] = getTerSetters.get(0).getTrackingSendRegion();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                mLocation[2] = getString(R.string.transit);
                mLocation[3] = getString(R.string.transit);
                mLocation[4] = getString(R.string.transit);
                mLocation[5] = getString(R.string.tbilisi);
                mLocation[6] = getString(R.string.transit);
                createLayout(mLayoutTracking);
                break;
            case "outdelivery":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[8];
                mLocation = new String[8];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();
                mDate[2] = getTerSetters.get(0).getTrackingSnipped();
                mDate[3] = getTerSetters.get(0).getTrackingDelay();
                mDate[4] = getTerSetters.get(0).getTrackingCustom();
                mDate[5] = getTerSetters.get(0).getTrackinOffice();
                mDate[6] = getTerSetters.get(0).getTrackingSendRegion();
                mDate[7] = getTerSetters.get(0).getTrackingOutDelivery();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                mLocation[2] = getString(R.string.transit);
                mLocation[3] = getString(R.string.transit);
                mLocation[4] = getString(R.string.transit);
                mLocation[5] = getString(R.string.tbilisi);
                mLocation[6] = getString(R.string.transit);
                mLocation[7] = getString(R.string.out_of_delivery);
                createLayout(mLayoutTracking);
                break;
            case "delivered":
                mLayoutTracking.setVisibility(View.VISIBLE);
                mDate = new String[9];
                mLocation = new String[9];
                mDate[0] = getTerSetters.get(0).getTrackingAway();
                mDate[1] = getTerSetters.get(0).getTrackingReceived();
                mDate[2] = getTerSetters.get(0).getTrackingSnipped();
                mDate[3] = getTerSetters.get(0).getTrackingDelay();
                mDate[4] = getTerSetters.get(0).getTrackingCustom();
                mDate[5] = getTerSetters.get(0).getTrackinOffice();
                mDate[6] = getTerSetters.get(0).getTrackingSendRegion();
                mDate[7] = getTerSetters.get(0).getTrackingOutDelivery();
                mDate[8] = getTerSetters.get(0).getTrackingDeliveredSigned();

                mLocation[0] = "";
                mLocation[1] = getString(R.string.brooklyn);
                mLocation[2] = getString(R.string.transit);
                mLocation[3] = getString(R.string.transit);
                mLocation[4] = getString(R.string.transit);
                mLocation[5] = getString(R.string.tbilisi);
                mLocation[6] = getString(R.string.transit);
                mLocation[7] = getString(R.string.out_of_delivery);
                mLocation[8] = getString(R.string.receiver_address);
                createLayout(mLayoutTracking);
                break;
        }


    }*/
}
