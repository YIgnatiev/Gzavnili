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
import com.team.noty.gzavnili.adapters.OfficeData;
import com.team.noty.gzavnili.adapters.OfficesListAdapter;
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

/**
 * Created by copch on 08.08.2017.
 */

public class GeorgiaOfficesListFragment extends Fragment {

    public OfficesListAdapter adapter;
    String mUrlGetOffices = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=offices";
    String mApiCode = "testAPI", language;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();
    ArrayList<OfficeData> officeDatas = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_view);

        adapter = new OfficesListAdapter(getContext(), officeDatas);

        Paper.init(getContext());

        language = Paper.book().read("language");

        listView.setAdapter(adapter);

        getOfficesList();

        return view;
    }

    public void getOfficesList() {

        showProgressBar(true);
        getTerSetters.clear();
        officeDatas.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetOffices,
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
                                    if (getTerSetters.get(i).getCountry().equals("Georgia")) {
                                        officeDatas.add(new OfficeData(getTerSetters.get(i).getCity(),
                                                getTerSetters.get(i).getAddress(), getTerSetters.get(i).getPhone(),
                                                getTerSetters.get(i).getFax(), getTerSetters.get(i).getEmail(),
                                                getTerSetters.get(i).getWorkTime(), getTerSetters.get(i).getLat(),
                                                getTerSetters.get(i).getLon()));
                                    }
                                }

                                adapter.notifyDataSetChanged();

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
}