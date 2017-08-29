package com.team.noty.gzavnili.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    View mView;
    LinearLayout layoutFunds, layoutReceivers, layoutHelp, layoutPhone, layoutCellPhone, layoutOrganization,
            layoutPostalCode, layoutCountry, layoutCity, layoutHomeAddress, layoutWorkAddress,
            layoutPrivateNumber, layoutState;
    BottomNavActivity bottomNavActivity;
    TextView txtFunds, txtFirstName, txtLastName, txtEmail, txtPhone, txtCellPhone, txtOrganization,
            txtPostalCode, txtCountry, txtCity, txtHomeAddress, txtWorkAddress, txtPrivateNumber, txtState;

    String response, responseUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_settings, container, false);

        Paper.init(getContext());

        response = Paper.book().read("responseLogin");
        responseUpdate = Paper.book().read("updateUserData");

        Log.d("MyLog", "response " + response);
        Log.d("MyLog", "response " + responseUpdate);

        layoutFunds = (LinearLayout) mView.findViewById(R.id.layout_funds);
        layoutReceivers = (LinearLayout) mView.findViewById(R.id.layout_receiver);
        layoutHelp = (LinearLayout) mView.findViewById(R.id.layout_help);
        layoutPhone = (LinearLayout) mView.findViewById(R.id.layout_phone);
        layoutCellPhone = (LinearLayout) mView.findViewById(R.id.layout_cell_phone);
        layoutOrganization = (LinearLayout) mView.findViewById(R.id.layout_organization);
        layoutPostalCode = (LinearLayout) mView.findViewById(R.id.layout_postal_code);
        layoutCountry = (LinearLayout) mView.findViewById(R.id.layout_country);
        layoutCity = (LinearLayout) mView.findViewById(R.id.layout_city);
        layoutHomeAddress = (LinearLayout) mView.findViewById(R.id.layout_home_address);
        layoutWorkAddress = (LinearLayout) mView.findViewById(R.id.layout_work_address);
        layoutPrivateNumber = (LinearLayout) mView.findViewById(R.id.layout_private_number);
        layoutState = (LinearLayout) mView.findViewById(R.id.layout_state);

        txtFunds = (TextView) mView.findViewById(R.id.txt_funds);
        txtFirstName = (TextView) mView.findViewById(R.id.txt_first_name);
        txtLastName = (TextView) mView.findViewById(R.id.txt_last_name);
        txtEmail = (TextView) mView.findViewById(R.id.txt_email);
        txtPhone = (TextView) mView.findViewById(R.id.txt_phone);
        txtCellPhone = (TextView) mView.findViewById(R.id.txt_cell_phone);
        txtOrganization = (TextView) mView.findViewById(R.id.txt_organization);
        txtPostalCode = (TextView) mView.findViewById(R.id.txt_postal_code);
        txtCountry = (TextView) mView.findViewById(R.id.txt_country);
        txtCity = (TextView) mView.findViewById(R.id.txt_city);
        txtState = (TextView) mView.findViewById(R.id.txt_state);
        txtHomeAddress = (TextView) mView.findViewById(R.id.txt_home_address);
        txtWorkAddress = (TextView) mView.findViewById(R.id.txt_work_address);
        txtPrivateNumber = (TextView) mView.findViewById(R.id.txt_private_number);

        bottomNavActivity = (BottomNavActivity) getActivity();

        layoutFunds.setOnClickListener(this);
        layoutReceivers.setOnClickListener(this);
        layoutHelp.setOnClickListener(this);
        layoutPhone.setOnClickListener(this);
        layoutCellPhone.setOnClickListener(this);
        layoutOrganization.setOnClickListener(this);
        layoutPostalCode.setOnClickListener(this);
        layoutCountry.setOnClickListener(this);
        layoutCity.setOnClickListener(this);
        layoutHomeAddress.setOnClickListener(this);
        layoutWorkAddress.setOnClickListener(this);
        layoutPrivateNumber.setOnClickListener(this);
        layoutState.setOnClickListener(this);

        if (responseUpdate != null) {
            setUpdateInformation(responseUpdate, response);
        }
        else {
            setInformation(response);
        }

        return mView;
    }

    public void setUpdateInformation(String response, String oldResponse) {
        String tempResponse = response.replace("[", "");
        String newResponse = tempResponse.replace("]", "");

        try {
            JSONObject jsonObject = new JSONObject(newResponse);

            txtPhone.setText(jsonObject.getString("PHONE"));
            txtCellPhone.setText(jsonObject.getString("CELLPHONE"));
            txtOrganization.setText(jsonObject.getString("ORGANIZATION"));
            txtPostalCode.setText(jsonObject.getString("POSTALCODE"));
            txtCountry.setText(jsonObject.getString("COUNTRY"));
            txtCity.setText(jsonObject.getString("CITY"));
            txtState.setText(jsonObject.getString("STATE"));
            txtHomeAddress.setText(jsonObject.getString("STREET1"));
            txtWorkAddress.setText(jsonObject.getString("STREET2"));
            txtPrivateNumber.setText(jsonObject.getString("PRIVEATENUMBER"));

            tempResponse = oldResponse.replace("[", "");
            newResponse = tempResponse.replace("]", "");

            jsonObject = new JSONObject(newResponse);

            txtFunds.setText("$" + jsonObject.getString("BALANCE"));
            txtFirstName.setText(jsonObject.getString("FIRSTNAME"));
            txtLastName.setText(jsonObject.getString("LASTNAME"));
            txtEmail.setText(jsonObject.getString("EMAILADDRESS"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Paper.book().delete("updateUserData");
    }

    public void setInformation(String response) {
        String tempResponse = response.replace("[", "");
        String newResponse = tempResponse.replace("]", "");

        try {
            JSONObject jsonObject = new JSONObject(newResponse);

            txtFunds.setText("$" + jsonObject.getString("BALANCE"));
            txtFirstName.setText(jsonObject.getString("FIRSTNAME"));
            txtLastName.setText(jsonObject.getString("LASTNAME"));
            txtEmail.setText(jsonObject.getString("EMAILADDRESS"));
            txtPhone.setText(jsonObject.getString("PHONE"));
            txtCellPhone.setText(jsonObject.getString("CELLPHONE"));
            txtOrganization.setText(jsonObject.getString("ORGANIZATION"));
            txtPostalCode.setText(jsonObject.getString("POSTALCODE"));
            txtCountry.setText(jsonObject.getString("COUNTRY"));
            txtCity.setText(jsonObject.getString("CITY"));
            txtState.setText(jsonObject.getString("STATE"));
            txtHomeAddress.setText(jsonObject.getString("STREET1"));
            txtWorkAddress.setText(jsonObject.getString("STREET2"));
            txtPrivateNumber.setText(jsonObject.getString("PRIVEATENUMBER"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_funds:
                bottomNavActivity.replaceFragment(new AddFoundsFragment());
                bottomNavActivity.changeToolbar(7);
                break;

            case R.id.layout_receiver:
                bottomNavActivity.replaceFragment(new ReceiverslistFragment());
                bottomNavActivity.changeToolbar(9);
                break;

            case R.id.layout_help:
                sendEmail();
                break;

            case R.id.layout_phone:
                openUpdateAddress();
                break;

            case R.id.layout_cell_phone:
                openUpdateAddress();
                break;

            case R.id.layout_organization:
                openUpdateAddress();
                break;

            case R.id.layout_postal_code:
                openUpdateAddress();
                break;

            case R.id.layout_country:
                openUpdateAddress();
                break;

            case R.id.layout_state:
                openUpdateAddress();
                break;

            case R.id.layout_city:
                openUpdateAddress();
                break;

            case R.id.layout_home_address:
                openUpdateAddress();
                break;
            case R.id.layout_work_address:
                openUpdateAddress();
                break;
            case R.id.layout_private_number:
                openUpdateAddress();
                break;
        }
    }
    public void openUpdateAddress(){
        bottomNavActivity.replaceFragment(new UpdateAddressFragment());
        bottomNavActivity.changeToolbar(10);
    }

    public void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Help");

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
