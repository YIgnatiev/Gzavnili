package com.team.noty.gzavnili.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.SignUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class UpdateAddressFragment extends BaseFragment{

    View mView;

    EditText editPhone, editCellPhone, editOrganization, editPostalCode, editCountry,
            editCity, editHomeAddress, editWorkAddress, editPrivateNumber, editState;
    String response, mApiCode = "testAPI", mUserCode;
    String mUrlUpdateAddress = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=updateaddress";

    Button btn_save;
    BottomNavActivity bottomNavActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_update_address, container, false);
        Paper.init(getContext());

        response = Paper.book().read("responseLogin");
        mUserCode = Paper.book().read("UserCode");

        bottomNavActivity = (BottomNavActivity) getActivity();

        editPhone = (EditText) mView.findViewById(R.id.edit_phone);
        editCellPhone = (EditText) mView.findViewById(R.id.edit_cell_phone);
        editOrganization = (EditText) mView.findViewById(R.id.edit_organization);
        editPostalCode = (EditText) mView.findViewById(R.id.edit_postal_code);
        editCountry = (EditText) mView.findViewById(R.id.edit_country);
        editCity = (EditText) mView.findViewById(R.id.edit_city);
        editState = (EditText) mView.findViewById(R.id.edit_state);
        editHomeAddress = (EditText) mView.findViewById(R.id.edit_home_address);
        editWorkAddress = (EditText) mView.findViewById(R.id.edit_work_address);
        editPrivateNumber = (EditText) mView.findViewById(R.id.edit_private_number);

        btn_save = (Button) mView.findViewById(R.id.btn_save);

        setInformation(response);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestUpdateAddress();
            }
        });


        return mView;
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
            bottomNavActivity.changeToolbar(8);
            return true;
        }
        else {
            return false;
        }
    }

    public void setInformation(String response) {
        String tempResponse = response.replace("[", "");
        String newResponse = tempResponse.replace("]", "");

        try {
            JSONObject jsonObject = new JSONObject(newResponse);

            editPhone.setText(jsonObject.getString("PHONE"));
            editCellPhone.setText(jsonObject.getString("CELLPHONE"));
            editOrganization.setText(jsonObject.getString("ORGANIZATION"));
            editPostalCode.setText(jsonObject.getString("POSTALCODE"));
            editCountry.setText(jsonObject.getString("COUNTRY"));
            editCity.setText(jsonObject.getString("CITY"));
            editState.setText(jsonObject.getString("STATE"));
            editHomeAddress.setText(jsonObject.getString("STREET1"));
            editWorkAddress.setText(jsonObject.getString("STREET2"));
            editPrivateNumber.setText(jsonObject.getString("PRIVEATENUMBER"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendRequestUpdateAddress() {

        showProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlUpdateAddress,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgressBar(false);
                        Log.d("MyLog", "response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Paper.book().write("updateUserData", jsonObject.getString("DATA"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bottomNavActivity.onBackPressed();
                        bottomNavActivity.changeToolbar(8);
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
                params.put("usercode", mUserCode);
                params.put("ORGANIZATION", editOrganization.getText().toString());
                params.put("POSTALCODE", editPostalCode.getText().toString());
                params.put("CELLPHONE", editCellPhone.getText().toString());
                params.put("CITY", editCity.getText().toString());
                params.put("COUNTRY", editCountry.getText().toString());
                params.put("PHONE", editPhone.getText().toString());
                params.put("PRIVEATENUMBER", editPrivateNumber.getText().toString());
                params.put("STREET1", editHomeAddress.getText().toString());
                params.put("STREET2", editWorkAddress.getText().toString());
                params.put("STATE", editState.getText().toString());

                return params;
            }
        };
        queue.add(strRequest);

    }
}
