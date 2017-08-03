package com.team.noty.gzavnili;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mEditFirstName, mEditLastName, mEditEmail, mEditPhone, mEditPassword,
            mEditConfirmPassword;
    Button mButtonSignUp;
    RelativeLayout mRelProgressBar;
    TextView mTxtTermPrivacy;

    String mStringFirstName, mStringLastName, mStringEmail, mStringPhone, mStringPassword,
            mStringConfirmPassword, mApiCode = "testAPI";
    String mUrlAuthorization = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=register";
    boolean mConfirmPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEditFirstName = (EditText) findViewById(R.id.edit_first_name);
        mEditLastName = (EditText) findViewById(R.id.edit_last_name);
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditPhone = (EditText) findViewById(R.id.edit_phone_number);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditConfirmPassword = (EditText) findViewById(R.id.edit_confirm_password);

        mRelProgressBar = (RelativeLayout) findViewById(R.id.rel_progress);

        mButtonSignUp = (Button) findViewById(R.id.btn_sign_up);

        mTxtTermPrivacy = (TextView) findViewById(R.id.txt_term_privacy);

        setTextTermPrivacy(mTxtTermPrivacy);

        mEditConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 6) {
                    if (mEditPassword.getText().toString().equals(s.toString())) {
                        mConfirmPass = true;
                        mEditPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.done, 0);
                        mEditConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.done, 0);
                    } else {
                        mConfirmPass = false;
                        mEditPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0);
                        mEditConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0);
                    }
                }
                else{
                    mEditPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0);
                    mEditConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 6) {
                    if (mEditConfirmPassword.getText().toString().equals(s.toString())) {
                        mConfirmPass = true;
                        mEditPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.done, 0);
                        mEditConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.done, 0);
                    } else {
                        mConfirmPass = false;
                        mEditPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0);
                        mEditConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0);
                    }
                }
                else {
                    mEditPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0);
                    mEditConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mButtonSignUp.setOnClickListener(this);
    }

    public void setTextTermPrivacy(TextView textTermPrivacy) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        String text_term_privacy_part1 = getString(R.string.text_term_privacy_part1);
        SpannableString text_term_privacy_part1Spaneble = new SpannableString(text_term_privacy_part1);
        text_term_privacy_part1Spaneble.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_color_grey)), 0, text_term_privacy_part1.length(), 0);
        builder.append(text_term_privacy_part1Spaneble);

        String text_term_privacy_part2 = getString(R.string.text_term_privacy_part2);
        SpannableString text_term_privacy_part2Spaneble = new SpannableString(text_term_privacy_part2);
        text_term_privacy_part2Spaneble.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_color_blue)), 0, text_term_privacy_part2.length(), 0);
        builder.append(text_term_privacy_part2Spaneble);

        String text_term_privacy_part3 = getString(R.string.text_term_privacy_part3);
        SpannableString text_term_privacy_part3Spaneble = new SpannableString(text_term_privacy_part3);
        text_term_privacy_part3Spaneble.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_color_grey)), 0, text_term_privacy_part3.length(), 0);
        builder.append(text_term_privacy_part3Spaneble);

        String text_term_privacy_part4 = getString(R.string.text_term_privacy_part4);
        SpannableString text_term_privacy_part4Spaneble = new SpannableString(text_term_privacy_part4);
        text_term_privacy_part4Spaneble.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_color_blue)), 0, text_term_privacy_part4.length(), 0);
        builder.append(text_term_privacy_part4Spaneble);

        textTermPrivacy.setText(builder, TextView.BufferType.SPANNABLE);
    }

    private boolean isEmailValid(String email) {
        if (email.contains("@") && email.contains("."))
            return true;
        else
            return false;
    }

    public boolean checkEmptyField() {
        boolean cancel = false;

        mStringFirstName = mEditFirstName.getText().toString().trim();
        mStringLastName = mEditLastName.getText().toString().trim();
        mStringEmail = mEditEmail.getText().toString().trim();
        mStringPhone = mEditPhone.getText().toString().trim();
        mStringPassword = mEditPassword.getText().toString().trim();
        mStringConfirmPassword = mEditConfirmPassword.getText().toString().trim();

        if (mStringFirstName.length() < 2) {
            cancel = true;
            //mEditFirstName.setError("Short Name");
            Toast.makeText(this, getString(R.string.error_first_name), Toast.LENGTH_SHORT).show();
        }
        if (mStringLastName.length() < 2) {
            cancel = true;
            //mEditLastName.setError("Short Last name");
            Toast.makeText(this, getString(R.string.error_last_name), Toast.LENGTH_SHORT).show();
        }
        if (!isEmailValid(mStringEmail)) {
            cancel = true;
            //mEditEmail.setError("Incorrect E-mail");
            Toast.makeText(this, getString(R.string.error_email), Toast.LENGTH_SHORT).show();
        }
        if (mStringPhone.length() < 9) {
            cancel = true;
            //mEditPhone.setError("Incorrect phone");
            Toast.makeText(this, getString(R.string.error_phone), Toast.LENGTH_SHORT).show();
        }
        if (mStringPassword.length() < 6) {
            cancel = true;
            //mEditPassword.setError("Short password");
            Toast.makeText(this, getString(R.string.error_pass), Toast.LENGTH_SHORT).show();
        }

        return cancel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up:
                if (!checkEmptyField() && mConfirmPass) {
                    sendRequestLogin();
                }
                break;
        }
    }
    public void showProgressBar(boolean show){
        if (show) {
            mRelProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            mRelProgressBar.setVisibility(View.GONE);
        }
    }
    public void sendRequestLogin() {

        showProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlAuthorization,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgressBar(false);
                        Log.d("MyLog", "response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                Intent intent = new Intent(SignUpActivity.this, BottomNavActivity.class);
                                intent.putExtra("response", jsonObject.getString("DATA"));
                                startActivity(intent);
                                finish();
                            } else {
                                buildErrorMessage(jsonObject.getString("ERRORMESSAGE"));
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
                        Log.d("MyLog", "error " + error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apicode", mApiCode);
                params.put("register_firstname", mStringFirstName);
                params.put("register_lastname", mStringLastName);
                params.put("phone", mStringPhone);
                params.put("register_password", mStringPassword);
                params.put("register_passwordverify", mStringConfirmPassword);
                params.put("register_emailaddress", mStringEmail);

                return params;
            }
        };
        queue.add(strRequest);

    }

    public void buildErrorMessage(String mMessage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }
}
