package com.team.noty.gzavnili.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.LoginActivity;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.WebViewPrivacyPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import io.paperdb.Paper;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    View mView;
    LinearLayout layoutFunds, layoutReceivers, layoutHelp, layoutPhone, layoutCellPhone, layoutOrganization,
            layoutPostalCode, layoutCountry, layoutCity, layoutHomeAddress, layoutWorkAddress,
            layoutPrivateNumber, layoutState, layoutLanguage, layoutLogOut, layoutPrivacyPolicy;
    BottomNavActivity bottomNavActivity;
    TextView txtFunds, txtFirstName, txtLastName, txtEmail, txtPhone, txtCellPhone, txtOrganization,
            txtPostalCode, txtCountry, txtCity, txtHomeAddress, txtWorkAddress, txtPrivateNumber,
            txtState, txtLanguage;

    String response, responseUpdate, balance, language;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_settings, container, false);

        Paper.init(getContext());

        response = Paper.book().read("responseLogin");
        responseUpdate = Paper.book().read("updateUserData");
        language = Paper.book().read("language");

        Log.d("MyLog", "login " + response);

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
        layoutLanguage = (LinearLayout) mView.findViewById(R.id.layout_language);
        layoutLogOut = (LinearLayout) mView.findViewById(R.id.layout_log_out);
        layoutPrivacyPolicy = (LinearLayout) mView.findViewById(R.id.layout_privacy_policy);

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
        txtLanguage = (TextView) mView.findViewById(R.id.txt_language);

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
        layoutLanguage.setOnClickListener(this);
        layoutLogOut.setOnClickListener(this);
        layoutPrivacyPolicy.setOnClickListener(this);

        if (responseUpdate != null) {
            setUpdateInformation(responseUpdate, response);
        }
        else {
            setInformation(response);
        }

        if (language != null){
            switch (language){
                case "en":
                    txtLanguage.setText("English");
                    break;
                case "ge":
                    txtLanguage.setText("Georgian");
                    break;
            }
        }
        else {
            txtLanguage.setText("Georgian");
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

            balance = jsonObject.getString("BALANCE");

            txtFunds.setText("$" + balance);
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

            balance = jsonObject.getString("BALANCE");

            txtFunds.setText("$" + balance);
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
                Paper.book().write("funds", balance);
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
            case R.id.layout_language:
                createDialogChooseLanguage();
                break;

            case R.id.layout_log_out:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
                break;

            case R.id.layout_privacy_policy:
                Intent intentPrivacy = new Intent(getContext(), WebViewPrivacyPolicy.class);
                intentPrivacy.putExtra("link", "http://gzavnili.com/privacy-policy.html");
                getActivity().startActivity(intentPrivacy);
                break;
        }
    }

    public void createDialogChooseLanguage() {
        final Dialog dialogPick = new Dialog(getContext());
        dialogPick.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPick.setContentView(R.layout.layout_dialog_choose_language);
        dialogPick.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView btn_english = (TextView) dialogPick.findViewById(R.id.btn_english);
        TextView btn_georgian = (TextView) dialogPick.findViewById(R.id.btn_georgian);
        TextView btn_cancel = (TextView) dialogPick.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPick.dismiss();
            }
        });

        btn_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
                dialogPick.dismiss();
                Paper.book().write("language", "en");
            }
        });

        btn_georgian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("ge");
                Paper.book().write("language", "ge");
                dialogPick.dismiss();
            }
        });

        dialogPick.show();
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(getContext(), BottomNavActivity.class);
        startActivity(refresh);
        getActivity().finish();
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
