package com.votingsystem.tsiro.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.votingsystem.tsiro.POJO.Survey;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.mainClasses.AdminBaseActivity;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by user on 16/11/2015.
 */
public class AdminBaseFragment extends Fragment{

    private static final String debugTag = "AdminBaseFragment";
    private Button addSurveyBtn, logoutBtn;
    private TextView userIdTtv;
    private EditText surveyTitleEdt, surveyDescEdt;
    private Toolbar toolbar;
    private NavigationDrawerFragment navigationDrawerFragment;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( getView() == null ) {
            view = inflater.inflate(R.layout.fragment_adminbase, container, false);
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        addSurveyBtn    = (Button)   view.findViewById(R.id.addSurveyBtn);
        logoutBtn       = (Button)   view.findViewById(R.id.logoutBtn);
        surveyTitleEdt  = (EditText) view.findViewById(R.id.surveyTitleEdt);
        surveyDescEdt   = (EditText) view.findViewById(R.id.surveyDescEdt);
        userIdTtv       = (TextView) view.findViewById(R.id.userIdTtv);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        navigationDrawerFragment = (NavigationDrawerFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.navigationDrawerFgmt);
        navigationDrawerFragment.setUp(R.id.navigationDrawerFgmt, (DrawerLayout) getActivity().findViewById(R.id.adminBaseDrlt), toolbar);
        Log.d(debugTag, "AdminBaseFragment onActivityCreated");

        String[] array = getActivity().getResources().getStringArray(R.array.agree_array);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("list1");
        arrayList.add("list2");
        arrayList.add("list3");
        arrayList.add("list4");
        ArrayList<String> arrayList1 = new ArrayList<String>();
        arrayList1.add("kist1");
        arrayList1.add("kist2");
        arrayList1.add("kist3");
        arrayList1.add("kist4");
        HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();
        hashMap.put(0, arrayList);
        hashMap.put(1, arrayList1);
        for (Map.Entry<Integer, ArrayList<String>> entry : hashMap.entrySet()) {
            Log.d(debugTag, entry.getKey().toString() + " " + entry.getValue().toString());
            for (int i=0; i<entry.getValue().size(); i++) {
                Log.d(debugTag, entry.getValue().get(i));
            }
        }

        addSurveyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String surveyTitle = surveyTitleEdt.getText().toString();
                String surveyDesc  = surveyDescEdt.getText().toString();
                Survey survey = new Survey(surveyTitle, surveyDesc);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] stringSessionKeys = {"user_id", "username", "user_email"};
                clearSessionPrefs(getActivity(), stringSessionKeys);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private static void clearSessionPrefs(Context context, String[] stringSessionKeys){
        SharedPreferences sessionPrefs = LoginActivity.getSessionPrefs(context);
        SharedPreferences.Editor editor = sessionPrefs.edit();
        for ( String keys : stringSessionKeys ) {
            editor.remove(keys);
        }
        editor.apply();
    }

}
