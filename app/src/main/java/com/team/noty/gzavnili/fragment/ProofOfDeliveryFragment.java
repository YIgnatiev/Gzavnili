package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
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

public class ProofOfDeliveryFragment extends Fragment{

    View mView;

    TextView mTxtTrackingNumber, mTxtWeight, mTxtServiceType, mTxtEdit, mTxtDone,
            mTxtPrice, mTxtFirstName, mTxtLastName, mTxtCellPhone, mTxtAddress, mTxtCity, mTxtPrivate,
            mEditValue, mEditStore, mTxtContent, mTxtDriverName;

    ImageView imgAvtograph;

    String mUrlGetParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";
    String mApiCode = "testAPI", mTracking, mUserCode, mStrContent;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_proof_of_delivery, container, false);

        Paper.init(getContext());

        mTracking = Paper.book().read("tracking");
        mUserCode = Paper.book().read("UserCode");

        Paper.book().delete("tracking");

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
        mEditStore = (TextView) mView.findViewById(R.id.edit_store);
        mEditValue = (TextView) mView.findViewById(R.id.edit_value);
        mTxtContent = (TextView) mView.findViewById(R.id.txt_content);
        mTxtDriverName = (TextView) mView.findViewById(R.id.driver_name);

        imgAvtograph = (ImageView) mView.findViewById(R.id.img_avtograph);

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
                        Log.d("MyLog", "response proof" + response);
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
                                    mTxtContent.setText(mStrContent);
                                    mTxtDriverName.setText(getTerSetters.get(0).getDriverName());

                                    if (!getTerSetters.get(0).getPhotoUrl().equals("")) {
                                        Picasso.with(getContext())
                                                .load(getTerSetters.get(0).getPhotoUrl())
                                                .into(imgAvtograph);
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
                params.put("tracking", mTracking);
                return params;
            }
        };
        queue.add(strRequest);

    }
}
