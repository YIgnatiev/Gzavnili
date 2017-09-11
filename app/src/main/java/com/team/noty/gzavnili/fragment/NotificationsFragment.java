package com.team.noty.gzavnili.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.adapters.NotificationData;
import com.team.noty.gzavnili.adapters.NotificationListAdapter;
import com.team.noty.gzavnili.adapters.OfficeData;
import com.team.noty.gzavnili.adapters.OfficesListAdapter;
import com.team.noty.gzavnili.adapters.ParcelData;
import com.team.noty.gzavnili.api.GetTerSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class NotificationsFragment extends Fragment implements LocationListener{

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    View mView;

    String mUrlGetNotifications = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=notification";
    String mUrlDeleteNotifications = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=deletenotification";
    String mUrlGetOffices = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=offices";

    String mApiCode = "testAPI", mUserCode, language;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();
    ArrayList<NotificationData> notificationDatas = new ArrayList<>();
    public NotificationListAdapter adapter;
    LocationManager manager;
    GoogleApiClient mGoogleApiLocationClient;
    LocationRequest mLocationRequest;
    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    int countGetLocation = 0;

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_notifications, container, false);

        Paper.init(getContext());

        mUserCode = Paper.book().read("UserCode");
        language = Paper.book().read("language");

        listView = (ListView) mView.findViewById(R.id.list_view);

        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
                if (Paper.book().read("lat") != null) {
                    fusedLatitude = Paper.book().read("lat");
                    fusedLongitude = Paper.book().read("lon");
                    Log.d("MyLog", "lat " + fusedLongitude);
                    showProgressBar(true);
                    new GetCountryFromLatLon().execute();
                }
                else {
                    getNotifications(null, null);
                }
            } else {
                showProgressBar(true);
                startFusedLocation();
                registerRequestUpdate(this);
            }
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE);
            // Show rationale and request permission.
        }

        return mView;
    }

    public Comparator<NotificationData> byDate = new Comparator<NotificationData>() {
        SimpleDateFormat format = new java.text.SimpleDateFormat("MMMMM, dd yyyy HH:mm:ss",
                Locale.US);

        public int compare(NotificationData ord1, NotificationData ord2) {
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(ord1.getDate());
                d2 = format.parse(ord2.getDate());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return (d1.getTime() > d2.getTime() ? -1 : 1);     //descending
            //  return (d1.getTime() > d2.getTime() ? 1 : -1);     //ascending
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_LOCATION_REQUEST_CODE) {

            if (permissions.length == 2 &&
                    permissions[1].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                    ) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else {

                }
            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_LOCATION_REQUEST_CODE);

                // Permission was denied. Display an error message.
            }
        }
    }

    public void callClearMethod()
    {
        adapter.clearAllMessage();
        deleteNotifications();
    }

    public void getOfficesList(final String country) {

        showProgressBar(true);
        getTerSetters.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetOffices,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = null;
                            jsonArray = new JSONArray(jsonObject.getString("DATA"));
                            Gson gson = new Gson();
                            getTerSetters = gson.fromJson(jsonArray.toString(),
                                    new TypeToken<List<GetTerSetter>>() {
                                    }.getType());
                            if (getTerSetters.size() != 0) {
                                for (int i = 0; i < getTerSetters.size(); i++) {
                                    if (getTerSetters.get(i).getCountry().equals(country)) {

                                        getNotifications(getTerSetters.get(i).getPhone(),
                                                getTerSetters.get(i).getEmail());
                                        break;
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
                params.put("language", language);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public void getNotifications(final String phone, final String email) {

        showProgressBar(true);
        getTerSetters.clear();
        notificationDatas.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetNotifications,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response " + response);
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = null;
                            jsonArray = new JSONArray(jsonObject.getString("DATA"));
                            Gson gson = new Gson();
                            getTerSetters = gson.fromJson(jsonArray.toString(),
                                    new TypeToken<List<GetTerSetter>>() {
                                    }.getType());
                            if (getTerSetters.size() != 0) {
                                for (int i = 0; i < getTerSetters.size(); i++) {
                                    notificationDatas.add(new NotificationData(getTerSetters.get(i).getMessage(),
                                            getTerSetters.get(i).getDtCreate(), phone, email));
                                }
                                Collections.sort(notificationDatas, byDate);
                                adapter = new NotificationListAdapter(getContext(), notificationDatas);
                                listView.setAdapter(adapter);
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

    public void deleteNotifications() {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlDeleteNotifications,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response " + response);
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
                params.put("language", language);
                return params;
            }
        };
        queue.add(strRequest);

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Location is disabled, please turn on location to call and send message!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // every 10 second

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiLocationClient, mLocationRequest, listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiLocationClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 1000);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        startFusedLocation();
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiLocationClient != null && mGoogleApiLocationClient.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        setFusedLatitude(location.getLatitude());
        setFusedLongitude(location.getLongitude());

        if (countGetLocation == 0) {
            new GetCountryFromLatLon().execute();
            Paper.book().write("lat", fusedLatitude);
            Paper.book().write("lon", fusedLongitude);
            countGetLocation ++;
        }

    }
    private class GetCountryFromLatLon extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?latlng=" + fusedLatitude +
                        "," + fusedLongitude + "&sensor=false");
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                String country = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONArray("address_components").getJSONObject(6).getString("short_name");

                getCorrectData(country);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("MyLog", "error " + e.getMessage());
            }
            showProgressBar(false);
        }
    }

    public void getCorrectData(String country){
        if (country.equals("US") || country.equals("CA")) {
            getOfficesList("US");
        }
        else {
            getOfficesList("Georgia");
        }
    }

    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public void setFusedLatitude(double lat) {
        fusedLatitude = lat;
    }

    public void setFusedLongitude(double lon) {
        fusedLongitude = lon;
    }

    public double getFusedLatitude() {
        return fusedLatitude;
    }

    public double getFusedLongitude() {
        return fusedLongitude;
    }
    public void startFusedLocation() {
        if (mGoogleApiLocationClient == null) {
            mGoogleApiLocationClient = new GoogleApiClient.Builder(getContext()).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }

                        @Override
                        public void onConnected(Bundle connectionHint) {

                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {

                        }
                    }).build();
            mGoogleApiLocationClient.connect();
        } else {
            mGoogleApiLocationClient.connect();
        }
    }

    public void stopFusedLocation() {
        if (mGoogleApiLocationClient != null) {
            mGoogleApiLocationClient.disconnect();
        }
    }

}
