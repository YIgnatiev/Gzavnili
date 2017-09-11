package com.team.noty.gzavnili.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.WebViewLink;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class AddFoundsFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    TextView mTxtBalance;
    Button btn_pay_pal, btn_credit_card;
    EditText editAmount;

    BottomNavActivity bottomNavActivity;
    String mBalance;
    String mUrlAddPayment = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=addpayment";
    String mApiCode = "testAPI", mUserCode, iDTransaction, addFundsLink, amount = "&amount=", response;


    /**
     * - Set to PaymentActivity.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PaymentActivity.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    // private static final String CONFIG_ENVIRONMENT =
    // PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox
    // environments.
    private static final String CONFIG_CLIENT_ID = "AXyuCXaAomVEvKYzY-Y59TyKIkLN7Esyw3lRrL3_rO0jwlhacBvQwUQNwutLROGDMzwXNXpU61ZfDfTE";

    private static final int REQUEST_CODE_PAYMENT = 1;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .acceptCreditCards(false)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Gzavnili")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.example.com/legal"));

    PayPalPayment thingToBuy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_add_funds, container, false);

        Paper.init(getContext());

        mBalance = Paper.book().read("funds");
        mUserCode = Paper.book().read("UserCode");
        response = Paper.book().read("responseLogin");

        getAddFunkLink(response);

        mTxtBalance = (TextView) mView.findViewById(R.id.txt_balance);

        btn_pay_pal = (Button) mView.findViewById(R.id.btn_pay_pal);
        btn_credit_card = (Button) mView.findViewById(R.id.btn_credit_card);

        editAmount = (EditText) mView.findViewById(R.id.edit_amount);

        bottomNavActivity = (BottomNavActivity) getActivity();

        mTxtBalance.setText("$" + mBalance);

        btn_pay_pal.setOnClickListener(this);
        btn_credit_card.setOnClickListener(this);


        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        return mView;
    }

    public void onDetach() {
        super.onDetach();
        getActivity().stopService(new Intent(getContext(), PayPalService.class));
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
            bottomNavActivity.changeToolbar(17);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_pay_pal:
                thingToBuy = new PayPalPayment(new BigDecimal(editAmount.getText().toString()), "USD",
                        "Account balance", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intentPay = new Intent(getContext(),
                        PaymentActivity.class);

                intentPay.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                startActivityForResult(intentPay, REQUEST_CODE_PAYMENT);
            break;
            case R.id.btn_credit_card:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse( addFundsLink + mUserCode + amount + editAmount.getText().toString()));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {

                        JSONObject jsonObject = new JSONObject(confirm.toJSONObject().toString(4));

                        String response = jsonObject.getString("response");

                        jsonObject = new JSONObject(response);

                        iDTransaction = jsonObject.getString("id");

                        addPaymentMethod(iDTransaction);

                        Toast.makeText(getContext(), "Balance placed",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out
                        .println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    public void getAddFunkLink(String response) {
        String tempResponse = response.replace("[", "");
        String newResponse = tempResponse.replace("]", "");

        try {
            JSONObject jsonObject = new JSONObject(newResponse);
            addFundsLink = jsonObject.getString("ADDFUNDLINK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addPaymentMethod(final String id){
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlAddPayment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "pay method " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Paper.book().write("responseLogin", jsonObject.getString("DATA"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        FragmentManager fragmentManager = getFragmentManager();
                        if (fragmentManager.getBackStackEntryCount() != 0) {
                            fragmentManager.popBackStack();
                            bottomNavActivity.changeToolbar(8);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apicode", mApiCode);
                params.put("usercode", mUserCode);
                params.put("amount", editAmount.getText().toString());
                params.put("transactionid", id);
                params.put("paymentmethod", "PayPal");
                return params;
            }
        };
        queue.add(strRequest);
    }

}
