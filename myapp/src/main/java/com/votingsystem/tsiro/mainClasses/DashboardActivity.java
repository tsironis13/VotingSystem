package com.votingsystem.tsiro.mainClasses;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
//import com.squareup.leakcanary.RefWatcher;
import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.*;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by user on 16/11/2015.
 */
public class DashboardActivity extends AppCompatActivity{

    private static final String debugTag = "DashboardActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Bundle userBundle;
    private int navigation_item_clicked;
    private Intent intent;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        userBundle = getIntent().getExtras();
//        Log.e(debugTag, LoginActivity.getSessionPrefs(getApplicationContext()).getInt(getResources().getString(R.string.user_id), 0)+"");
//        Log.e(debugTag, "USER BUNDLE: "+userBundle.getInt(getResources().getString(R.string.user_id)));
        toolbar                 =   (Toolbar) findViewById(R.id.appBar);
        drawerLayout            =   (DrawerLayout) findViewById(R.id.dashboardDrlt);
        navigationView          =   (NavigationView) findViewById(R.id.navigationView);

        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            toolbar.setElevation((float)MyApplication.convertPixelToDpAndViceVersa(this, 0, 3));
        }
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            DashboardFragment dashboardFragmentFragment = new DashboardFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.baseFrlt, dashboardFragmentFragment, getResources().getString(R.string.dashboard_fgmt)).commit();
        }
        initializeNavigationDrawer();
        initializeNavigationView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        //refWatcher.watch(this);
    }

    private void initializeNavigationDrawer() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) { super.onDrawerOpened(drawerView); }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (menuItem != null) menuItem.setChecked(false);
                switch (navigation_item_clicked) {
                    case 1:
                        intent.putExtra(getResources().getString(R.string.action), getResources().getString(R.string.firm_surveys));
                        startActivity(intent);
                        break;
                    case 2:
                        intent.putExtra(getResources().getString(R.string.action), getResources().getString(R.string.user_surveys));
                        startActivity(intent);
                        break;
                    case 3:
                        logUserOut(new String[] {getResources().getString(R.string.user_id), getResources().getString(R.string.username_tag), getResources().getString(R.string.email_tag), getResources().getString(R.string.firm_id)});
                        break;
                }
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void initializeNavigationView() {
        View header = navigationView.getHeaderView(0);
        TextView usernameTxt    =   (TextView) header.findViewById(R.id.username);
        TextView emailTxt       =   (TextView) header.findViewById(R.id.email);
        if (usernameTxt != null && emailTxt != null) {
            String username, email = "";
            if (userBundle.getString(getResources().getString(R.string.username_tag)) != null) {
                username    =   userBundle.getString(getResources().getString(R.string.username_tag));
                email       =   userBundle.getString(getResources().getString(R.string.email_tag));
            } else {
                username    =   LoginActivity.getSessionPrefs(getApplicationContext()).getString(getResources().getString(R.string.username_tag), "");
                email       =   LoginActivity.getSessionPrefs(getApplicationContext()).getString(getResources().getString(R.string.email_tag), "");
            }
            usernameTxt.setText(username);
            emailTxt.setText(email);
        }
        //navigationView.setItemTextAppearance(R.style.test);
        //navigationView.setItemTextColor(getResources().getColorStateList(R.color.custom_radio_button));
        //navigationView.setItemBackgroundResource(R.drawable.test);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                menuItem = item;
                if(item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                drawerLayout.closeDrawers();
                intent = new Intent(DashboardActivity.this, SurveysActivity.class);
                switch (item.getItemId()) {
                    case R.id.surveys:
                        navigation_item_clicked = 1;
                        break;
                    case R.id.user_surveys:
                        navigation_item_clicked = 2;
                        break;
                    case R.id.logout:
                        navigation_item_clicked = 3;
                        break;
                }
                return true;
            }
        });
    }

    private void logUserOut(String[] keys) {
        for (String sessionKeys : keys) {
            LoginActivity.getSessionPrefs(getApplicationContext()).edit().remove(sessionKeys).apply();
        }
        this.finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
