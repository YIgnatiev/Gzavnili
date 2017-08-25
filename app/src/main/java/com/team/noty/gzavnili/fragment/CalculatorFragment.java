package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.team.noty.gzavnili.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class CalculatorFragment extends Fragment implements View.OnClickListener {

    View mView;
    EditText mEditWeight, mEditLength, mEditHeight, mEditWidth;
    TextView mTxtLength, mTxtWeight, mTxtHeight, mTxtWidth,
        btn_kg, btn_lb, btn_cm, btn_in, mTxtRegularService, mTxtExpressService, mTxtRegularParcel,
        mTxtOnlineParcel, mTxtMon, mTxtTue, mTxtWen, mTxtThu, mTxtFri, mTxtSat, mTxtSun, mTxtPrice,
        mTxtWeekDay;
    Button btn_calculate;

    String mStrTypeWeight, mStrTypeLength, mStrTypeService, mStrTypeParcel, mStrShipDay,
            mStrWeight, mStrLength, mStrHeight, mStrWidth, mApiCode = "testAPI";
    String mUrlCalculate = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=calculator";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_calculator, container, false);

        mEditWeight = (EditText) mView.findViewById(R.id.edit_weight);
        mEditLength = (EditText) mView.findViewById(R.id.edit_length);
        mEditHeight = (EditText) mView.findViewById(R.id.edit_height);
        mEditWidth = (EditText) mView.findViewById(R.id.edit_width);

        mTxtHeight = (TextView) mView.findViewById(R.id.txt_height);
        mTxtLength = (TextView) mView.findViewById(R.id.txt_length);
        mTxtWeight = (TextView) mView.findViewById(R.id.txt_weight);
        mTxtWidth = (TextView) mView.findViewById(R.id.txt_width);
        btn_kg = (TextView) mView.findViewById(R.id.btn_kg);
        btn_lb = (TextView) mView.findViewById(R.id.btn_lb);
        btn_in = (TextView) mView.findViewById(R.id.btn_in);
        btn_cm = (TextView) mView.findViewById(R.id.btn_cm);
        mTxtRegularService = (TextView) mView.findViewById(R.id.txt_regular_service);
        mTxtExpressService = (TextView) mView.findViewById(R.id.txt_express_service);
        mTxtRegularParcel = (TextView) mView.findViewById(R.id.txt_regular_parcel);
        mTxtOnlineParcel = (TextView) mView.findViewById(R.id.txt_online_parcel);
        mTxtMon = (TextView) mView.findViewById(R.id.txt_mon);
        mTxtTue = (TextView) mView.findViewById(R.id.txt_tue);
        mTxtWen = (TextView) mView.findViewById(R.id.txt_wen);
        mTxtThu = (TextView) mView.findViewById(R.id.txt_thu);
        mTxtFri = (TextView) mView.findViewById(R.id.txt_fri);
        mTxtSat = (TextView) mView.findViewById(R.id.txt_sat);
        mTxtSun = (TextView) mView.findViewById(R.id.txt_sun);
        mTxtPrice = (TextView) mView.findViewById(R.id.txt_price);
        mTxtWeekDay = (TextView) mView.findViewById(R.id.txt_weekday);

        btn_calculate = (Button) mView.findViewById(R.id.btn_calculate);

        mStrTypeLength = getString(R.string.text_sm);
        mStrTypeWeight = getString(R.string.text_kg);
        mStrTypeService = "regular";
        mStrTypeParcel = "regular";
        mStrShipDay = "1";
        mStrWeight = "0";
        mStrLength = "0";
        mStrHeight = "0";
        mStrWidth = "0";

        mEditWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mTxtWeight.setText(0 + mStrTypeWeight);
                    mStrWeight = "0";
                }
                else {
                    mTxtWeight.setText(s + mStrTypeWeight);
                    mStrWeight = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mTxtLength.setText(0 + mStrTypeLength);
                    mStrLength = "0";
                }
                else {
                    mTxtLength.setText(s + mStrTypeLength);
                    mStrLength = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mTxtHeight.setText(0 + mStrTypeLength);
                    mStrHeight = "0";
                }
                else {
                    mTxtHeight.setText(s + mStrTypeLength);
                    mStrHeight = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mTxtWidth.setText(0 + mStrTypeLength);
                    mStrWidth = "0";
                }
                else {
                    mTxtWidth.setText(s + mStrTypeLength);
                    mStrWidth = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_kg.setOnClickListener(this);
        btn_cm.setOnClickListener(this);
        btn_in.setOnClickListener(this);
        btn_lb.setOnClickListener(this);
        mTxtRegularService.setOnClickListener(this);
        mTxtExpressService.setOnClickListener(this);
        mTxtRegularParcel.setOnClickListener(this);
        mTxtOnlineParcel.setOnClickListener(this);
        mTxtMon.setOnClickListener(this);
        mTxtTue.setOnClickListener(this);
        mTxtWen.setOnClickListener(this);
        mTxtThu.setOnClickListener(this);
        mTxtFri.setOnClickListener(this);
        mTxtSat.setOnClickListener(this);
        mTxtSun.setOnClickListener(this);
        btn_calculate.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_calculate:
                sendDataCalculate();
                break;
            case R.id.btn_kg:
                changeView(btn_kg, btn_lb);
                mStrTypeWeight = getString(R.string.text_kg);
                mTxtWeight.setText(mStrWeight + mStrTypeWeight);
            break;

            case R.id.btn_lb:
                changeView(btn_lb, btn_kg);
                mStrTypeWeight = getString(R.string.text_lb);
                mTxtWeight.setText(mStrWeight + mStrTypeWeight);
                break;

            case R.id.btn_cm:
                changeView(btn_cm, btn_in);
                mStrTypeLength = getString(R.string.text_sm);
                mTxtLength.setText(mStrLength + mStrTypeLength);
                mTxtHeight.setText(mStrHeight + mStrTypeLength);
                mTxtWidth.setText(mStrWidth + mStrTypeLength);
                break;

            case R.id.btn_in:
                changeView(btn_in, btn_cm);
                mStrTypeLength = getString(R.string.text_in);
                mTxtLength.setText(mStrLength + mStrTypeLength);
                mTxtHeight.setText(mStrHeight + mStrTypeLength);
                mTxtWidth.setText(mStrWidth + mStrTypeLength);
                break;

            case R.id.txt_regular_service:
                changeViewService(mTxtRegularService, mTxtExpressService);
                mStrTypeService = "regular";
                break;
            case R.id.txt_express_service:
                changeViewService(mTxtExpressService, mTxtRegularService);
                mStrTypeService = "express";
                break;

            case R.id.txt_regular_parcel:
                changeViewService(mTxtRegularParcel, mTxtOnlineParcel);
                mStrTypeParcel = "regular";
                break;
            case R.id.txt_online_parcel:
                changeViewService(mTxtOnlineParcel, mTxtRegularParcel);
                mStrTypeParcel = "online";
                break;

            case R.id.txt_mon:
                changeDayView(mTxtMon, mTxtTue, mTxtWen, mTxtThu, mTxtFri, mTxtSat, mTxtSun);
                mStrShipDay = "1";
                break;
            case R.id.txt_tue:
                changeDayView(mTxtTue, mTxtWen, mTxtThu, mTxtFri, mTxtSat, mTxtSun, mTxtMon);
                mStrShipDay = "2";
                break;
            case R.id.txt_wen:
                changeDayView(mTxtWen, mTxtThu, mTxtFri, mTxtSat, mTxtSun, mTxtMon, mTxtTue);
                mStrShipDay = "3";
                break;
            case R.id.txt_thu:
                changeDayView(mTxtThu, mTxtFri, mTxtSat, mTxtSun, mTxtMon, mTxtTue, mTxtWen);
                mStrShipDay = "4";
                break;
            case R.id.txt_fri:
                changeDayView(mTxtFri, mTxtSat, mTxtSun, mTxtMon, mTxtTue, mTxtWen, mTxtThu);
                mStrShipDay = "5";
                break;
            case R.id.txt_sat:
                changeDayView(mTxtSat, mTxtSun, mTxtMon, mTxtTue, mTxtWen, mTxtThu, mTxtFri);
                mStrShipDay = "6";
                break;
            case R.id.txt_sun:
                changeDayView(mTxtSun, mTxtMon, mTxtTue, mTxtWen, mTxtThu, mTxtFri, mTxtSat);
                mStrShipDay = "7";
                break;
        }
    }

    public void sendDataCalculate(){
        showProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlCalculate,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgressBar(false);
                        Log.d("MyLog", "response calculate" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String price = String.format("%.2f", Double.parseDouble(jsonObject.getString("PRICE")));
                            mTxtPrice.setText("$ " + price);
                            mTxtWeekDay.setText(jsonObject.getString("DAYOFWEEK"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgressBar(false);
                        Log.d("MyLog", "error " + error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apicode", mApiCode);
                params.put("language", "en");
                params.put("weight", mStrWeight);
                params.put("weighttype", mStrTypeWeight);
                params.put("length", mStrLength);
                params.put("width", mStrWidth);
                params.put("height", mStrHeight);
                params.put("dimtype", mStrTypeLength);
                params.put("service", mStrTypeService);
                params.put("parceltype", mStrTypeParcel);
                params.put("day", mStrShipDay);

                return params;
            }
        };
        queue.add(strRequest);
    }

    public void changeView(TextView txt1, TextView txt2){
        txt1.setBackgroundResource(R.drawable.back_for_buttons_calculator);
        txt1.setTextColor(getResources().getColor(R.color.white));

        txt2.setBackgroundResource(0);
        txt2.setTextColor(getResources().getColor(R.color.color_text_label_item));
    }

    public void changeViewService(TextView txt1, TextView txt2){
        txt1.setBackgroundResource(R.drawable.back_for_blue_button);
        txt1.setTextColor(getResources().getColor(R.color.white));

        txt2.setBackgroundResource(0);
        txt2.setTextColor(getResources().getColor(R.color.color_text_label_item));
    }

    public void changeDayView(TextView txt1, TextView txt2, TextView txt3, TextView txt4,
                              TextView txt5, TextView txt6, TextView txt7){
        txt1.setBackgroundResource(R.drawable.back_for_buttons_calculator);
        txt1.setTextColor(getResources().getColor(R.color.white));

        txt2.setBackgroundResource(0);
        txt2.setTextColor(getResources().getColor(R.color.color_text_label_item));
        txt3.setBackgroundResource(0);
        txt3.setTextColor(getResources().getColor(R.color.color_text_label_item));
        txt4.setBackgroundResource(0);
        txt4.setTextColor(getResources().getColor(R.color.color_text_label_item));
        txt5.setBackgroundResource(0);
        txt5.setTextColor(getResources().getColor(R.color.color_text_label_item));
        txt6.setBackgroundResource(0);
        txt6.setTextColor(getResources().getColor(R.color.color_text_label_item));
        txt7.setBackgroundResource(0);
        txt7.setTextColor(getResources().getColor(R.color.color_text_label_item));
    }
}
