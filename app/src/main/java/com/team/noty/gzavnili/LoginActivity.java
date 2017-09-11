package com.team.noty.gzavnili;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mLogin, mPassword;
    Button mButtonLogin;
    TextView mSignUp, mSkipReg;
    RelativeLayout mRelProgressBar;
    String mUrlLogin = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=login";
    String mApiCode = "testAPI", mStringLogin, mStringPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        } else {
            // Hide status bar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        Paper.init(this);

        mLogin = (EditText) findViewById(R.id.edit_login);
        mPassword = (EditText) findViewById(R.id.edit_password);

        mButtonLogin = (Button) findViewById(R.id.btn_login);

        mSignUp = (TextView) findViewById(R.id.txt_sign_up);
        mSkipReg = (TextView) findViewById(R.id.txt_skip_reg);

        mRelProgressBar = (RelativeLayout) findViewById(R.id.rel_progress);

        mButtonLogin.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mSkipReg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_login:
                if (!checkEmptyField()){
                    sendRequestLogin(mStringLogin, mStringPassword);
                }
            break;

            case R.id.txt_sign_up:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            case R.id.txt_skip_reg:
                Intent intent = new Intent(LoginActivity.this, BottomNavActivity.class);
                intent.putExtra("response", "You are not authorized ");
                startActivity(intent);
                break;
        }
    }
    public boolean checkEmptyField(){
        mStringLogin = mLogin.getText().toString().trim();
        mStringPassword = mPassword.getText().toString().trim();

        boolean cancel = false;

        if (mStringLogin.length() < 2)
        {
            cancel = true;
            mLogin.setError("Short login");
        }

        if (mStringPassword.length() < 6)
        {
            cancel = true;
            mPassword.setError("Short password");
        }

        return cancel;
    }
    public void sendRequestLogin(final String mLogin, final String mPassword) {

        showProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                Paper.book().write("responseLogin", jsonObject.getString("DATA"));
                                Intent intent = new Intent(LoginActivity.this, BottomNavActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apicode", mApiCode);
                params.put("username", mLogin);
                params.put("password", mPassword);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public void showProgressBar(boolean show){
        if (show) {
            mRelProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            mRelProgressBar.setVisibility(View.GONE);
        }
    }

    public void buildErrorMessage(String mMessage){
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
