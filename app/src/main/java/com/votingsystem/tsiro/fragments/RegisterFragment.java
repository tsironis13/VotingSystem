package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;
import com.votingsystem.tsiro.POJO.RegisterUser;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by user on 11/12/2015.
 */
public class RegisterFragment extends Fragment {

    private static final String debugTag = RegisterFragment.class.getSimpleName();
    private static String error_no_connection, empty_fields;
    private LinearLayout registerBaseLlt;
    private TextView signInHereTtv, errorresponseTtv;
    private EditText usernameEdt, passwordEdt, confirmPasswordEdt, emailEdt, firmCodeEdt;
    private Button submitBtn;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private ConnectivityObserver connectivityObserver;
    private ApiService apiService;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        registerBaseLlt         =   (LinearLayout) view.findViewById(R.id.registerBaseLlt);
        usernameEdt             =   (EditText) view.findViewById(R.id.usernameEdt);
        passwordEdt             =   (EditText) view.findViewById(R.id.passwordEdt);
        confirmPasswordEdt      =   (EditText) view.findViewById(R.id.confirmPasswordEdt);
        emailEdt                =   (EditText) view.findViewById(R.id.emailEdt);
        firmCodeEdt             =   (EditText) view.findViewById(R.id.firmCodeEdt);
        signInHereTtv           =   (TextView) view.findViewById(R.id.signInHereTtv);
        errorresponseTtv        =   (TextView) view.findViewById(R.id.errorresponseTtv);
        submitBtn               =   (Button) view.findViewById(R.id.submitBtn);
        connectivityObserver    =   getArguments().getParcelable("connectivityObserver");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        submitBtn.setTransformationMethod(null);
        setSignInHereSpan();
        emailEdt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        signInHereTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView) commonElements.signInHereOnClick();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( connectivityObserver.getConnectivityStatus(getActivity()) != AppConfig.NO_CONNECTION_TYPE ) {
                    empty_fields = commonElements.encodeUtf8(getResources().getString(R.string.empty_fields));
                    if ( !commonElements.validateEditText(new EditText[]{usernameEdt, passwordEdt, confirmPasswordEdt, emailEdt, firmCodeEdt}) ) {
                        errorresponseTtv.setText(commonElements.decodeUtf8(empty_fields));
                    } else {
                        checkRegister(usernameEdt.getText().toString(), passwordEdt.getText().toString(), confirmPasswordEdt.getText().toString(), emailEdt.getText().toString(), firmCodeEdt.getText().toString());
                    }
                } else {
                    error_no_connection = commonElements.encodeUtf8(getResources().getString(R.string.no_connection));
                    Toast.makeText(getActivity(), commonElements.decodeUtf8(error_no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkRegister(String username, String password, String confirm_password, String email, String firm_code) {
        apiService = RetrofitSingleton.getInstance().getApiService();
        Call<RegisterUser> call = apiService.registerUser(username, password, confirm_password, email, "Arx.net", firm_code);
        call.enqueue(new Callback<RegisterUser>() {
            @Override
            public void onResponse(Response<RegisterUser> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signInHere), 22, 34, 1); }
}
