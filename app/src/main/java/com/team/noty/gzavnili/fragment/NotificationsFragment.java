package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.noty.gzavnili.R;
import com.team.noty.gzavnili.adapters.NotificationData;
import com.team.noty.gzavnili.adapters.NotificationListAdapter;
import com.team.noty.gzavnili.adapters.OfficeData;
import com.team.noty.gzavnili.adapters.ParcelData;
import com.team.noty.gzavnili.adapters.ParcelsListAdapter;
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

public class NotificationsFragment extends Fragment{

    View mView;

    String mUrlGetNotifications = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=notification";
    String mApiCode = "testAPI", mUserCode;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();
    ArrayList<NotificationData> notificationDatas = new ArrayList<>();
    public NotificationListAdapter adapter;

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_notifications, container, false);

        Paper.init(getContext());

        mUserCode = Paper.book().read("UserCode");

        listView = (ListView) mView.findViewById(R.id.list_view);

        getNotifications();

        return mView;
    }

    public void getNotifications() {

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
                                            getTerSetters.get(i).getDtCreate()));
                                }
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
                params.put("language", "en");
                return params;
            }
        };
        queue.add(strRequest);

    }
}
