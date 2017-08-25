package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.noty.gzavnili.R;
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

public class ReceiverslistFragment extends Fragment{

    private static final String ATTRIBUTE_NAME_TEXT = "text";
    View mView;
    ListView listView;

    String mUrlGetReceiverList = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=receiverlist";
    String mApiCode = "testAPI", mUserCode;
    ArrayList<GetTerSetter> getTerSetters = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_list_receivers, container, false);

        Paper.init(getContext());

        mUserCode = Paper.book().read("UserCode");

        listView = (ListView) mView.findViewById(R.id.list_view);


        getReceiversList();

        return mView;
    }
    public void getReceiversList() {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlGetReceiverList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", response);
                        try {
                            showProgressBar(false);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("STATUS").equals("S")) {
                                JSONArray jsonArray = null;
                                jsonArray = new JSONArray(jsonObject.getString("DATA"));
                                Gson gson = new Gson();
                                getTerSetters = gson.fromJson(jsonArray.toString(),
                                        new TypeToken<List<GetTerSetter>>() {}.getType());

                                // массив имен атрибутов, из которых будут читаться данные
                                String[] from = { ATTRIBUTE_NAME_TEXT};
                                // массив ID View-компонентов, в которые будут вставлять данные
                                int[] to = { R.id.txt_text};
                                ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                                        getTerSetters.size());
                                Map<String, Object> m;

                                if (getTerSetters.size() != 0) {
                                    for (int i = 0; i < getTerSetters.size(); i++) {
                                        m = new HashMap<String, Object>();
                                        m.put(ATTRIBUTE_NAME_TEXT,  getTerSetters.get(i).getFirstName() +
                                                getTerSetters.get(i).getLastName());
                                        data.add(m);
                                    }
                                }

                                // создаем адаптер
                                SimpleAdapter sAdapter = new SimpleAdapter(getContext(), data, R.layout.item_receivers,
                                        from, to);

                                listView.setAdapter(sAdapter);
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
                return params;
            }
        };
        queue.add(strRequest);

    }
}
