package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.adapters.CalendarView;
import com.team.noty.gzavnili.adapters.ParcelData;
import com.team.noty.gzavnili.adapters.ParcelsListAdapter;
import com.team.noty.gzavnili.api.GetTerSetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

import static com.team.noty.gzavnili.BottomNavActivity.showProgressBar;

public class ParcelScheduleFragment extends Fragment{

    View mView;
    CalendarView cv;
    ListView listView;

    String mUrlGetDates = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=deliverydates";
    String mUrlGetParcels = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcels";
    String mApiCode = "testAPI", mUserCode;
    ArrayList<GetTerSetter> getTerSetters = new  ArrayList<>();
    ArrayList<GetTerSetter> getListParcels = new  ArrayList<>();
    ArrayList<ParcelData> parcelDatas = new ArrayList<>();
    public ParcelsListAdapter adapter;
    SimpleDateFormat format;
    //public BottomNavActivity bottomNavActivity = new BottomNavActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_parcel_schedule, container, false);

        Paper.init(getContext());

        mUserCode = Paper.book().read("UserCode");

        cv = (CalendarView) mView.findViewById(R.id.calendar_view);
        listView = (ListView) mView.findViewById(R.id.list_view);

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Paper.book().write("tracking", getListParcels.get(position).getTrackingNumber());
                bottomNavActivity.replaceFragment(new DetailParcelInfoFragment());
                bottomNavActivity.changeToolbar(1);
            }
        });*/

        getDatesParcels();

        format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date = new Date();
        final String deliveryDate = format.format(date);

        getParcelList(mUserCode, deliveryDate);

        cv.setEventHandler(new CalendarView.EventHandler() {
            @Override
            public void onDayClick(Date date) {
                DateFormat df = SimpleDateFormat.getDateInstance();

                String date_format = format.format(date);
                getParcelList(mUserCode, date_format);

            }
        });

        return mView;
    }

    public void getDatesParcels() {
        showProgressBar(true);
        //getTerSetters.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetDates,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                JSONArray jsonArray = null;
                                jsonArray = new JSONArray(jsonObject.getString("DATA"));
                                Gson gson = new Gson();
                                getTerSetters = gson.fromJson(jsonArray.toString(), new TypeToken<List<GetTerSetter>>() {
                                }.getType());
                                if (getTerSetters.size() != 0) {
                                    HashSet<Date> events = new HashSet<>();
                                    for (int i = 0; i < getTerSetters.size(); i++) {
                                        if (!getTerSetters.get(i).getDate().equals("")) {
                                            SimpleDateFormat format = new SimpleDateFormat("MMMMM, dd yyyy hh:mm:ss",
                                                    Locale.US);
                                            Date newDate = null;

                                            newDate = format.parse(getTerSetters.get(i).getDate());

                                            format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                            String time = format.format(newDate);
                                            newDate = format.parse(time);
                                            events.add(newDate);
                                        }
                                        cv.updateCalendar(events, 1);
                                    }
                                }
                            }
                            showProgressBar(false);


                        } catch (JSONException | ParseException e) {
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
                return params;
            }
        };
        queue.add(strRequest);
    }

    public void getParcelList(final String mUserCode, final String mDeliveryDate) {

        showProgressBar(true);
        getListParcels.clear();
        parcelDatas.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetParcels,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response " + response);
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                JSONArray jsonArray = null;
                                jsonArray = new JSONArray(jsonObject.getString("DATA"));
                                Gson gson = new Gson();
                                getListParcels = gson.fromJson(jsonArray.toString(),
                                        new TypeToken<List<GetTerSetter>>() {}.getType());
                                if (getListParcels.size() != 0) {
                                    boolean mCorrect, mUnpaid;
                                    for (int i = 0; i < getListParcels.size(); i++) {
                                        mCorrect = isCorrect(getListParcels.get(i).getValue(),
                                                getListParcels.get(i).getStore(),
                                                getListParcels.get(i).getContents());

                                        mUnpaid = isPaid(getListParcels.get(i).getPaid(),
                                                getListParcels.get(i).getDept());

                                        parcelDatas.add(new ParcelData(getListParcels.get(i).getLocation(),
                                                getListParcels.get(i).getTrackingNumber(), mCorrect, mUnpaid,
                                                getTerSetters.get(i).getId(), false));
                                    }

                                    adapter = new ParcelsListAdapter(getContext(), parcelDatas);
                                    listView.setAdapter(adapter);

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
                params.put("deliverydate", mDeliveryDate);
                return params;
            }
        };
        queue.add(strRequest);

    }
    public boolean isCorrect(String mValue, String mStore, String mContents){
        boolean correct;
        if (mContents.length() != 0 && mValue.length() != 0 && Double.parseDouble(mValue) > 0 &&
                mStore.length() != 0){
            correct = false;
        }
        else {
            correct = true;
        }

        return correct;
    }

    public boolean isPaid(String mPaid, String mDept){
        boolean correct;
        if (mPaid.length() != 0 && mDept.length() != 0 && Double.parseDouble(mPaid) > 0){
            correct = false;
        }
        else {
            correct = true;
        }

        return correct;
    }


}
