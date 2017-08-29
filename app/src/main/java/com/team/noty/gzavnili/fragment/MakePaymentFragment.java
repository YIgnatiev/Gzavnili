package com.team.noty.gzavnili.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.WebViewLink;
import com.team.noty.gzavnili.adapters.ParcelPaymentData;
import com.team.noty.gzavnili.adapters.RVAdapter;
import com.team.noty.gzavnili.api.GetTerSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class MakePaymentFragment extends Fragment implements View.OnClickListener {

    View mView;
    private RecyclerView rv;
    TextView txtAccountBalance, txtPriceDelivery, txtPrice;
    Switch cbChecked;
    Button btnCreditCard, btnPayPal;

    String mUrlPaymentInfo = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=paymentinfo";
    String mApiCode = "testAPI", mUserCode, mParcelId, mDeliveryPrice, mPayLink, mPayDeliveryLink,
            iDTransaction, totalAmount, parcelDelivery;
    String[] splitId;
    private List<ParcelPaymentData> parcelPaymentDatas = new ArrayList<>();
    String mUrlGetParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();


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
        mView =  inflater.inflate(R.layout.fragment_make_payment, container, false);

        Paper.init(getContext());

        mParcelId = Paper.book().read("parcelID");
        mUserCode = Paper.book().read("UserCode");

        Log.d("MyLog", "mparcel id " + mParcelId);
        Paper.book().delete("parcelID");

        rv=(RecyclerView) mView.findViewById(R.id.recycler_view);

        txtAccountBalance = (TextView) mView.findViewById(R.id.txt_account_balance);
        txtPriceDelivery = (TextView) mView.findViewById(R.id.txt_parcel_delivery);
        txtPrice = (TextView) mView.findViewById(R.id.txt_price);

        cbChecked = (Switch) mView.findViewById(R.id.cb_checked);

        cbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    totalAmount = String.valueOf(Double.parseDouble(totalAmount)
                            + Double.parseDouble(parcelDelivery));
                }
                else{
                    totalAmount = String.valueOf(Double.parseDouble(totalAmount)
                            - Double.parseDouble(parcelDelivery));
                }
                txtPrice.setText("$" + totalAmount);
            }
        });
        
        btnCreditCard = (Button) mView.findViewById(R.id.btn_credit_card); 
        btnPayPal = (Button) mView.findViewById(R.id.btn_pay_pal);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        splitId = mParcelId.split(",");

        getPaymentInfo();
        
        btnCreditCard.setOnClickListener(this);
        btnPayPal.setOnClickListener(this);

        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);
        
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
                                mDeliveryPrice = jsonObject.getString("PARCELAMOUNT");
                                parcelDelivery = jsonObject.getString("DELIVERYAMOUNT");
                                mPayLink = jsonObject.getString("PAYLINK");
                                mPayDeliveryLink = jsonObject.getString("PAYDELIVERYLINK");
                                txtPriceDelivery.setText(parcelDelivery + " )");
                                //txtPrice.setText("$ " + jsonObject);
                                txtAccountBalance.setText(getString(R.string.text_account_balance_payment) +
                                        jsonObject.getString("BALANCE"));

                                if (jsonObject.getString("DELIVERYAMOUNT").equals("0")){
                                    cbChecked.setEnabled(false);
                                }
                                else {
                                    cbChecked.setEnabled(false);
                                }
                                getParcelList();
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

                                int step = 0;
                                if (getTerSetters.size() != 0) {
                                    for (int i = 0; i < getTerSetters.size(); i++) {
                                        if (step == splitId.length - 1){
                                            break;
                                        }
                                        else if (!splitId[step].trim().equals("")) {
                                            if (splitId[step].trim().equals(getTerSetters.get(i).getId())) {
                                                parcelPaymentDatas.add(new ParcelPaymentData(
                                                        getTerSetters.get(i).getTrackingNumber(), getTerSetters.get(i).getStore(),
                                                        getTerSetters.get(i).getWeight(), mDeliveryPrice));
                                                step++;
                                            }
                                        }
                                    }
                                    totalAmount = String.valueOf(Double.parseDouble(mDeliveryPrice) * step);
                                    txtPrice.setText("$" + totalAmount);
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

    public void onDetach() {
        super.onDetach();
        getActivity().stopService(new Intent(getContext(), PayPalService.class));
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

                        Log.d("MyLog", "id " + iDTransaction);

                        addPaymentMethod(iDTransaction);

                        Toast.makeText(getContext(), "Parcel placed",
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

    public void addPaymentMethod(final String id){
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlPaymentInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "pay method " + response);
                        FragmentManager fragmentManager = getFragmentManager();
                        if (fragmentManager.getBackStackEntryCount() != 0) {
                            fragmentManager.popBackStack();
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
                params.put("amount", totalAmount);
                params.put("transactionid", id);
                params.put("paymentmethod", "PayPal");
                params.put("ParcelId", mParcelId);
                return params;
            }
        };
        queue.add(strRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_credit_card:
                Intent intent = new Intent(getContext(), WebViewLink.class);
                if (cbChecked.isChecked()){
                    intent.putExtra("link", mPayDeliveryLink);
                }
                else {
                    intent.putExtra("link", mPayLink);
                }
                startActivity(intent);
                break;
            case R.id.btn_pay_pal:
                thingToBuy = new PayPalPayment(new BigDecimal(totalAmount), "USD",
                        "Parcel", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intentPay = new Intent(getContext(),
                        PaymentActivity.class);

                intentPay.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                startActivityForResult(intentPay, REQUEST_CODE_PAYMENT);
                break;
        }
    }
}
