package com.team.noty.gzavnili.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.R;

import io.paperdb.Paper;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    View mView;
    LinearLayout layoutFunds, layoutReceivers, layoutHelp;
    BottomNavActivity bottomNavActivity;

    String response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_settings, container, false);

        Paper.init(getContext());

        response = Paper.book().read("responseLogin");

        Log.d("MyLog", "response " + response);

        layoutFunds = (LinearLayout) mView.findViewById(R.id.layout_funds);
        layoutReceivers = (LinearLayout) mView.findViewById(R.id.layout_receiver);
        layoutHelp = (LinearLayout) mView.findViewById(R.id.layout_help);

        bottomNavActivity = (BottomNavActivity) getActivity();

        layoutFunds.setOnClickListener(this);
        layoutReceivers.setOnClickListener(this);
        layoutHelp.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.layout_funds:
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
        }
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
