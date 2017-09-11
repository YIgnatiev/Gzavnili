package com.team.noty.gzavnili;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.team.noty.gzavnili.fragment.BaseFragment;
import com.team.noty.gzavnili.fragment.CalculatorFragment;
import com.team.noty.gzavnili.fragment.DeliveryRequestFragment;
import com.team.noty.gzavnili.fragment.DetailParcelInfoFragment;
import com.team.noty.gzavnili.fragment.MakePaymentFragment;
import com.team.noty.gzavnili.fragment.MorePageFragment;
import com.team.noty.gzavnili.fragment.NotificationsFragment;
import com.team.noty.gzavnili.fragment.OfficesFragment;
import com.team.noty.gzavnili.fragment.ParcelScheduleFragment;
import com.team.noty.gzavnili.fragment.ParcelsFragment;
import com.team.noty.gzavnili.fragment.ProofOfDeliveryFragment;
import com.team.noty.gzavnili.utils.BottomNavigationViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

public class BottomNavActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_CALL = 2;
    static RelativeLayout mRelProgressBar;
    Toolbar mToolbar;
    String mData, mUserCode, mStatus, tracking, mParcelId;
    String mUrlShareParcel = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcelshare";
    String mUrlArchiveParcel = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parcelarchive";
    String mUrlDeleteParcel = "http://gz.ecomsolutions.net/apinew/gzavnili.cfm?method=parceldelete";
    String mApiCode = "testAPI";
    Menu mMenu;
    ImageView fab_delivery_request, fab_delete, fab_pay, fab_share, fab_edit, fab_proof, fab,
            fab_tracking, fab_archive, fab_help;
    LinearLayout layout_alpha;
    FrameLayout frameLayout;
    RelativeLayout container;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_parcels:
                    replaceFragment(new ParcelsFragment());
                    mToolbar.setTitle(getString(R.string.title_parcels));
                    changeToolbar(0);
                    return true;
                case R.id.navigation_notifications:
                    replaceFragment(new NotificationsFragment());
                    mToolbar.setTitle(getString(R.string.title_notifications));
                    changeToolbar(11);
                    return true;
                case R.id.navigation_calculator:
                    replaceFragment(new CalculatorFragment());
                    mToolbar.setTitle(getString(R.string.title_calculator));
                    changeToolbar(4);
                    return true;
                case R.id.navigation_offices:
                    replaceFragment(new OfficesFragment());
                    mToolbar.setTitle(getString(R.string.title_offices));
                    changeToolbar(4);
                    return true;
                case R.id.navigation_some_pages:
                    replaceFragment(new MorePageFragment());
                    mToolbar.setTitle("More");
                    changeToolbar(4);
                    return true;
            }
            return false;
        }

    };

    public static void showProgressBar(boolean show) {
        if (show) {
            mRelProgressBar.setVisibility(View.VISIBLE);
        } else {
            mRelProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);

        String languageToLoad = Paper.book().read("language"); // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_bottom_nav);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.title_parcels));
        setSupportActionBar(mToolbar);

        mData = Paper.book().read("responseLogin");
        mUserCode = parseResponse(mData);

        Paper.book().write("UserCode", mUserCode);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mRelProgressBar = (RelativeLayout) findViewById(R.id.rel_progress);
        container = (RelativeLayout) findViewById(R.id.container);
        layout_alpha = (LinearLayout) findViewById(R.id.layout_alpha);
        frameLayout = (FrameLayout) findViewById(R.id.layout_fab);

        fab_delivery_request = (ImageView) findViewById(R.id.fab_delivery_request);
        fab_delete = (ImageView) findViewById(R.id.fab_delete);
        fab_edit = (ImageView) findViewById(R.id.fab_edit);
        fab_pay = (ImageView) findViewById(R.id.fab_pay);
        fab_proof = (ImageView) findViewById(R.id.fab_show_proof);
        fab_share = (ImageView) findViewById(R.id.fab_share);
        fab_tracking = (ImageView) findViewById(R.id.fab_tracking);
        fab_archive = (ImageView) findViewById(R.id.fab_archive);
        fab = (ImageView) findViewById(R.id.fab);
        fab_help = (ImageView) findViewById(R.id.fab_help);

        fab.setOnClickListener(this);

        fab_delivery_request.setOnClickListener(this);
        fab_delete.setOnClickListener(this);
        fab_edit.setOnClickListener(this);
        fab_proof.setOnClickListener(this);
        fab_tracking.setOnClickListener(this);
        fab_share.setOnClickListener(this);
        fab_archive.setOnClickListener(this);
        fab_pay.setOnClickListener(this);
        fab_help.setOnClickListener(this);

        commitFirstFragment(new ParcelsFragment());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CALL_PHONE
                    },
                    REQUEST_CODE_CALL);
            // Show rationale and request permission.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ParcelsFragment fragment = null;
        switch (item.getItemId()) {

            case R.id.menu_calendar_fragment:
                replaceFragment(new ParcelScheduleFragment());
                changeToolbar(2);
                break;
            case R.id.menu_share:
                fragment = (ParcelsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.content);
                mParcelId = fragment.getParcelsId();
                createDialogShareParcel();
                break;
            case R.id.menu_selected:
                fragment = (ParcelsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.content);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(true);
                fragment.checkLog(true);
                break;
            case R.id.menu_choose:
                fragment = (ParcelsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.content);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(true);
                fragment.checkLog(false);
                break;
            case R.id.menu_delivery_request:
                fragment = (ParcelsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.content);
                mParcelId = fragment.getParcelsId();
                Paper.book().write("parcelID", mParcelId);
                replaceFragment(new DeliveryRequestFragment());
                changeToolbar(3);
                break;
            case R.id.menu_make_payment:
                fragment = (ParcelsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.content);
                mParcelId = fragment.getParcelsId();
                Paper.book().write("parcelID", mParcelId);
                replaceFragment(new MakePaymentFragment());
                changeToolbar(6);
                break;
            case R.id.menu_delete:
                fragment = (ParcelsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.content);
                mParcelId = fragment.getParcelsId();
                sendDeleteParcel(mUserCode, mParcelId);
                break;
            case R.id.menu_help:
                sendEmail();
                break;
            case R.id.menu_archive:
                fragment = (ParcelsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.content);
                mParcelId = fragment.getParcelsId();
                sendArchiveParcel(mUserCode, mParcelId);
                break;
            case R.id.menu_clear:
                NotificationsFragment notificationsFragment = (NotificationsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.content);
                notificationsFragment.callClearMethod();
                break;

            default:
                break;
        }
        return true;
    }

    public void enabledMenuGroup(boolean check) {
        ParcelsFragment fragment = (ParcelsFragment)
                getSupportFragmentManager().findFragmentById(R.id.content);
        if (check) {
            mMenu.findItem(R.id.menu_choose).setVisible(false);
            mMenu.findItem(R.id.menu_selected).setVisible(false);
            mMenu.setGroupVisible(R.id.menu_context, true);

            for (int i = 0; i < fragment.getStatusLists().length; i++) {
                if (fragment.getStatusLists()[i] != null) {
                    switch (fragment.getStatusLists()[i]) {
                        case "awaiting":
                            mMenu.findItem(R.id.menu_delete).setVisible(false);
                            mMenu.findItem(R.id.menu_share).setVisible(false);
                            break;
                        case "new":
                            mMenu.findItem(R.id.menu_delete).setVisible(false);
                            break;
                        case "delivered":
                            mMenu.findItem(R.id.menu_share).setVisible(false);
                            mMenu.findItem(R.id.menu_archive).setVisible(false);
                            break;

                    }
                }
            }

        } else {
            mMenu.findItem(R.id.menu_selected).setVisible(false);
            mMenu.findItem(R.id.menu_choose).setVisible(true);
            mMenu.setGroupVisible(R.id.menu_context, false);
        }

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        if (fab.getVisibility() == View.VISIBLE) {
            hideFloatingButtonMenu(mStatus);
        }
    }

    public void commitFirstFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();

        if (fab.getVisibility() == View.VISIBLE) {
            hideFloatingButtonMenu(mStatus);
        }
    }

    public void changeToolbar(int view) {
        if (view == 1) {
            mToolbar.setTitle(R.string.title_detail_parcel);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(0);
                }
            });
        } else if (view == 0) {
            mToolbar.setTitle(R.string.title_parcels);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(true);
                mMenu.findItem(R.id.menu_selected).setVisible(true);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }
        } else if (view == 2) {
            mToolbar.setTitle(R.string.title_schedule_parcel);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(0);
                }
            });
        } else if (view == 3) {
            mToolbar.setTitle(R.string.delivery_request);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(0);
                }
            });

        } else if (view == 4) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }
        } else if (view == 5) {
            mToolbar.setTitle(R.string.tittle_proof);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(0);
                }
            });

        } else if (view == 6) {
            mToolbar.setTitle(R.string.title_payment);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(0);
                }
            });

        } else if (view == 7) {
            mToolbar.setTitle(R.string.title_add_funds);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(17);
                }
            });

        } else if (view == 8) {
            mToolbar.setTitle(R.string.title_settings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

        } else if (view == 9) {
            mToolbar.setTitle(R.string.title_receiver_list);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(17);
                }
            });

        } else if (view == 10) {
            mToolbar.setTitle("Address");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(17);
                }
            });

        } else if (view == 11) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(true);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }
        } else if (view == 12) {
            mToolbar.setTitle(getString(R.string.text_new_parcel));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(13);
                }
            });

        } else if (view == 13) {
            mToolbar.setTitle(getString(R.string.title_more));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }
        } else if (view == 14) {
            mToolbar.setTitle(getString(R.string.make_payment));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(13);
                }
            });

        } else if (view == 15) {
            mToolbar.setTitle(getString(R.string.title_add_funds));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(13);
                }
            });

        } else if (view == 16) {
            mToolbar.setTitle(getString(R.string.title_schedule_parcel));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(13);
                }
            });

        } else if (view == 17) {
            mToolbar.setTitle(getString(R.string.title_settings));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(13);
                }
            });

        } else if (view == 18) {
            mToolbar.setTitle(getString(R.string.delivery_request));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_calendar_fragment).setVisible(false);
                mMenu.findItem(R.id.menu_selected).setVisible(false);
                mMenu.findItem(R.id.menu_choose).setVisible(false);
                mMenu.findItem(R.id.menu_clear).setVisible(false);
                mMenu.setGroupVisible(R.id.menu_context, false);
            }

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    changeToolbar(13);
                }
            });

        }
    }


    public String parseResponse(String mData) {

        String temp = mData.replaceAll("\\[", "");
        String newData = temp.replaceAll("]", "");
        try {
            JSONObject jsonObject = new JSONObject(newData);
            return jsonObject.getString("USERCODE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void sendShareParcel(final String mUserCode, final String name, final String privateNumber,
                                final String email, final String mParcelId) {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlShareParcel,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response share " + response);
                        ParcelsFragment fragment = (ParcelsFragment)
                                getSupportFragmentManager().findFragmentById(R.id.content);
                        fragment.callUpdateList();
                        mMenu.findItem(R.id.menu_choose).setVisible(false);
                        mMenu.findItem(R.id.menu_selected).setVisible(true);
                        mMenu.setGroupVisible(R.id.menu_context, false);

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
                params.put("name", name);
                params.put("privatenumber", privateNumber);
                params.put("email", email);
                params.put("parcel", mParcelId);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public void sendArchiveParcel(final String mUserCode, final String mParcelId) {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlArchiveParcel,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response archive " + response);
                        ParcelsFragment fragment = (ParcelsFragment)
                                getSupportFragmentManager().findFragmentById(R.id.content);
                        fragment.callUpdateList();
                        mMenu.findItem(R.id.menu_choose).setVisible(false);
                        mMenu.findItem(R.id.menu_selected).setVisible(true);
                        mMenu.setGroupVisible(R.id.menu_context, false);

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
                params.put("parcel", mParcelId);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public void sendDeleteParcel(final String mUserCode, final String mParcelId) {

        showProgressBar(true);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, mUrlDeleteParcel,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyLog", "response delete " + response);
                        ParcelsFragment fragment = (ParcelsFragment)
                                getSupportFragmentManager().findFragmentById(R.id.content);
                        fragment.callUpdateList();
                        mMenu.findItem(R.id.menu_choose).setVisible(false);
                        mMenu.findItem(R.id.menu_selected).setVisible(true);
                        mMenu.setGroupVisible(R.id.menu_context, false);
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
                params.put("parcels", mParcelId);
                return params;
            }
        };
        queue.add(strRequest);

    }

    public void createDialogShareParcel() {
        final Dialog dialogPick = new Dialog(this);
        dialogPick.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPick.setContentView(R.layout.layout_for_dialog_share);
        dialogPick.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btn_cancel_share = (Button) dialogPick.findViewById(R.id.btn_cancel_share);
        Button btn_share_parcel = (Button) dialogPick.findViewById(R.id.btn_share_parcel);

        final EditText editName = (EditText) dialogPick.findViewById(R.id.edit_first_last_name);
        final EditText editPrivateNumber = (EditText) dialogPick.findViewById(R.id.edit_private_number);
        final EditText editEmail = (EditText) dialogPick.findViewById(R.id.edit_email_phone);

        btn_cancel_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPick.dismiss();
            }
        });

        btn_share_parcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendShareParcel(mUserCode, editName.getText().toString(),
                        editPrivateNumber.getText().toString(), editEmail.getText().toString(), mParcelId);
                dialogPick.dismiss();
            }
        });

        dialogPick.show();
    }

    public boolean isParcelNeedPayment(String debt, String paid) {
        boolean isPaid = false;

        if (debt.length() > 0 && Double.parseDouble(debt) > 0 || Integer.parseInt(paid) == 0) {
            isPaid = true;
        }

        return isPaid;
    }

    public boolean isParcelNeedCorrection(String store, String content, String value) {
        boolean isCorrection = false;

        if (store.length() != 0 || content.length() != 0 || value.length() != 0 || Double.parseDouble(value) == 0) {
            isCorrection = true;
        }

        return isCorrection;
    }

    public boolean isDeliveryRequestAvailable(String deliveryRequest) {
        boolean isDelivery = false;

        if (Integer.parseInt(deliveryRequest) == 1) {
            isDelivery = true;
        }

        return isDelivery;
    }

    public boolean isDeliveryProofAvailable(String mStatus) {
        boolean isProof = false;

        if (mStatus.equals("delivered")) {
            isProof = true;
        }

        return isProof;
    }

    public boolean isShareAvalaible(String paid) {
        boolean isShare = false;

        if (Integer.parseInt(paid) == 1) {
            isShare = true;
        }

        return isShare;
    }

    public boolean isDeleteAvailable(String paid) {
        boolean isDelete = false;

        if (Integer.parseInt(paid) == 0) {
            isDelete = true;
        }

        return isDelete;
    }

    public boolean isTrackingAvalaible(String status) {
        boolean isTracking = false;

        if (status.equals("delay") || status.equals("office") || status.equals("outdelivery")
                || status.equals("received") || status.equals("custom") || status.equals("shipped")
                || status.equals("notonhold")) {
            isTracking = true;
        }
        return isTracking;
    }

    public void createFloatingButtonMenu(String status, String tracking, String parcelId,
                                         String store, String content, String value,
                                         String debt, String paid, String deliveryRequest) {

        frameLayout.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        layout_alpha.setVisibility(View.VISIBLE);

        mStatus = status;
        this.tracking = tracking;
        mParcelId = parcelId;


        Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_1);
        Animation show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_2);
        Animation show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_3);
        Animation show_fab_4 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_4);
        Animation show_fab_5 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_5);
        Animation show_fab_6 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab_6);

        switch (status) {
            case "awaiting":
            case "new":
                if (!isDeliveryRequestAvailable(deliveryRequest))
                    setLayoutParams(fab_delivery_request, 2.2, 0.1, show_fab_1);

                if (!isShareAvalaible(paid))
                    setLayoutParams(fab_share, 1.75, 1.25, show_fab_2);

                if (!isParcelNeedPayment(debt, paid))
                    setLayoutParams(fab_pay, 0.65, 2, show_fab_3);

                if (!isParcelNeedCorrection(store, content, value))
                    setLayoutParams(fab_edit, -0.65, 2, show_fab_4);

                if (!isDeleteAvailable(paid))
                    setLayoutParams(fab_delete, -1.75, 1.25, show_fab_5);

                setLayoutParams(fab_help, -2.2, 0.1, show_fab_6);
                break;

            case "delay":
            case "office":
            case "outdelivery":
            case "received":
            case "custom":
            case "shipped":
            case "notonhold":
                if (!isDeliveryRequestAvailable(deliveryRequest))
                    setLayoutParams(fab_delivery_request, 2.2, 0.1, show_fab_1);

                if (!isShareAvalaible(paid))
                    setLayoutParams(fab_share, 1.75, 1.25, show_fab_2);

                if (!isParcelNeedPayment(debt, paid))
                    setLayoutParams(fab_pay, 0.65, 2, show_fab_3);

                if (!isParcelNeedCorrection(store, content, value))
                    setLayoutParams(fab_edit, -0.65, 2, show_fab_4);

                if (!isTrackingAvalaible(mStatus))
                    setLayoutParams(fab_tracking, -1.75, 1.25, show_fab_5);


                setLayoutParams(fab_help, -2.2, 0.1, show_fab_6);
                break;

            case "delivered":
                if (!isDeliveryRequestAvailable(deliveryRequest))
                    setLayoutParams(fab_delivery_request, 2.2, 0.1, show_fab_1);

                if (!isParcelNeedPayment(debt, paid))
                    setLayoutParams(fab_pay, 1.75, 1.25, show_fab_2);

                if (!isDeliveryProofAvailable(mStatus))
                    setLayoutParams(fab_proof, 0.65, 2, show_fab_3);

                setLayoutParams(fab_archive, -0.65, 2, show_fab_4);
                setLayoutParams(fab_help, -1.75, 1.25, show_fab_5);
                break;

            case "onhold":
            case "region":
                if (!isDeliveryRequestAvailable(deliveryRequest))
                    setLayoutParams(fab_delivery_request, 2.2, 0.1, show_fab_1);

                if (!isShareAvalaible(paid))
                    setLayoutParams(fab_share, 1.75, 1.25, show_fab_2);

                if (!isParcelNeedPayment(debt, paid))
                    setLayoutParams(fab_pay, 0.65, 2, show_fab_3);

                if (!isParcelNeedCorrection(store, content, value))
                    setLayoutParams(fab_edit, -0.65, 2, show_fab_4);

                setLayoutParams(fab_help, -1.75, 1.25, show_fab_5);
                break;

            case "archived":
                setLayoutParams(fab_help, 0.65, 2, show_fab_3);
                break;
        }
    }

    public void hideFloatingButtonMenu(String status) {

        Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab_1);
        Animation hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab_2);
        Animation hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab_3);
        Animation hide_fab_4 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab_4);
        Animation hide_fab_5 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab_5);
        Animation hide_fab_6 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab_6);


        Log.d("MyLog", "status hide" + mStatus);

        switch (status) {
            case "awaiting":
            case "new":
                setLayoutParams(fab_delivery_request, -2.2, -0.1, hide_fab_1);
                setLayoutParams(fab_share, -1.75, -1.25, hide_fab_2);
                setLayoutParams(fab_pay, -0.65, -2, hide_fab_3);
                setLayoutParams(fab_edit, 0.65, -2, hide_fab_4);
                setLayoutParams(fab_edit, 1.75, -1.25, hide_fab_5);
                setLayoutParams(fab_delete, 2.2, -0.1, hide_fab_6);

                break;
            case "delay":
            case "office":
            case "outdelivery":
            case "received":
            case "custom":
            case "shipped":
            case "notonhold":
                setLayoutParams(fab_delivery_request, -2.2, -0.1, hide_fab_1);
                setLayoutParams(fab_share, -1.75, -1.25, hide_fab_2);
                setLayoutParams(fab_pay, -0.65, -2, hide_fab_3);
                setLayoutParams(fab_edit, 0.65, -2, hide_fab_4);
                setLayoutParams(fab_tracking, 1.75, -1.25, hide_fab_5);
                setLayoutParams(fab_help, 2.2, -0.1, hide_fab_6);
                break;

            case "delivered":
                setLayoutParams(fab_delivery_request, -2.2, -0.1, hide_fab_1);
                setLayoutParams(fab_pay, -1.75, -1.25, hide_fab_2);
                setLayoutParams(fab_proof, -0.65, -2, hide_fab_3);
                setLayoutParams(fab_archive, 0.65, -2, hide_fab_4);
                setLayoutParams(fab_help, 1.75, -1.25, hide_fab_5);
                break;

            case "onhold":
            case "region":
                setLayoutParams(fab_delivery_request, -2.2, -0.1, hide_fab_1);
                setLayoutParams(fab_share, -1.75, -1.25, hide_fab_2);
                setLayoutParams(fab_pay, -0.65, -2, hide_fab_3);
                setLayoutParams(fab_edit, 0.65, -2, hide_fab_4);
                setLayoutParams(fab_help, 1.75, -1.25, hide_fab_5);
                break;

            case "archived":
                setLayoutParams(fab_help, -0.65, -2, hide_fab_3);
                break;
        }
        frameLayout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        layout_alpha.setVisibility(View.GONE);
    }

    public void setLayoutParams(ImageView imageView, double cofWidth, double cofHeight, Animation animation) {

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.rightMargin += (int) (imageView.getLayoutParams().width * cofWidth);
        layoutParams.bottomMargin += (int) (imageView.getLayoutParams().height * cofHeight);
        imageView.setLayoutParams(layoutParams);
        imageView.startAnimation(animation);
        imageView.setClickable(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                hideFloatingButtonMenu(mStatus);
                break;

            case R.id.fab_show_proof:
                Paper.book().write("tracking", mParcelId);
                replaceFragment(new ProofOfDeliveryFragment());
                changeToolbar(5);
                break;

            case R.id.fab_delivery_request:
                Paper.book().write("parcelID", mParcelId);
                replaceFragment(new DeliveryRequestFragment());
                changeToolbar(3);
                break;

            case R.id.fab_share:
                createDialogShareParcel();
                hideFloatingButtonMenu(mStatus);
                break;

            case R.id.fab_edit:
                replaceFragment(new DetailParcelInfoFragment());
                changeToolbar(1);
                break;

            case R.id.fab_tracking:
                replaceFragment(new DetailParcelInfoFragment());
                changeToolbar(1);
                break;

            case R.id.fab_pay:
                Paper.book().write("parcelID", mParcelId);
                replaceFragment(new MakePaymentFragment());
                changeToolbar(6);
                break;
            case R.id.fab_help:
                sendEmail();
                break;

            case R.id.fab_archive:
                sendArchiveParcel(mUserCode, mParcelId);
                break;

            case R.id.fab_delete:
                sendDeleteParcel(mUserCode, mParcelId);
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
            Toast.makeText(BottomNavActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        boolean handled = false;
        for (Fragment f : fragmentList) {
            if (f instanceof BaseFragment) {
                handled = ((BaseFragment) f).onBackPressed();
                if (handled) {
                    break;
                }
            }
        }

        if (!handled) {
            super.onBackPressed();
            changeToolbar(0);
        }

    }
}
