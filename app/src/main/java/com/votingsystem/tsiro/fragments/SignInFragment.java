package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;
import com.votingsystem.tsiro.deserializer.FirmsDeserializer;
import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.POJO.User;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.mainClasses.AdminBaseActivity;
import com.votingsystem.tsiro.mainClasses.LoginActivity;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by user on 10/10/2015.
 */
public class SignInFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String debugTag = SignInFragment.class.getSimpleName();
    private static String error_no_connection, empty_fields;
    private TextView forgotPasswordTtV, registerTtv, errorresponseTtv, popupErrorresponseTtv, showHidePasswordTtv;
    private EditText usernameEdt, passwordEdt, firmcodeEdt;
    private Button signInBtn, popUpLoginBtn;
    private Spinner firmSpnr;
    private PopupWindow popupWindow;
    private LayoutInflater inflater;
    private SharedPreferences sessionPrefs;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private ConnectivityObserver connectivityObserver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_signin, container, false);
        usernameEdt             =   (EditText) view.findViewById(R.id. usernameEdt);
        passwordEdt             =   (EditText) view.findViewById(R.id.passwordEdt);
        signInBtn               =   (Button) view.findViewById(R.id.signInBtn);
        forgotPasswordTtV       =   (TextView) view.findViewById(R.id.forgotPasswordTtv);
        registerTtv             =   (TextView) view.findViewById(R.id.registerTtv);
        errorresponseTtv        =   (TextView) view.findViewById(R.id.errorresponseTtv);
        showHidePasswordTtv     =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        connectivityObserver    =   getArguments().getParcelable("connectivityObserver");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionPrefs = LoginActivity.getSessionPrefs(getActivity());
        boolean key = sessionPrefs.contains("17");
        int value = sessionPrefs.getInt("17", 0);
        Toast.makeText(getContext(), "key, value" + key + " " + value, Toast.LENGTH_SHORT).show();
        if ( sessionPrefs.contains("user_id") ) startBaseActivity();

        signInBtn.setTransformationMethod(null);
        setRegisterSpan();
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(debugTag, "CONNECTION: " + LoginActivity.connectionStatusUpdated);
                Log.d(debugTag, "CONNECTIVITY STATUS: " + connectivityObserver.getConnectivityStatus(getActivity()));
                if (usernameEdt.getText().toString().isEmpty() || passwordEdt.getText().toString().isEmpty()) {
                    empty_fields = commonElements.encodeUtf8(getResources().getString(R.string.empty_fields));
                    errorresponseTtv.setText(commonElements.decodeUtf8(empty_fields));
                } else {
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    checkLogin(usernameEdt.getText().toString(), passwordEdt.getText().toString());
                }
            }
        });

        usernameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorresponseTtv.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(errorresponseTtv.getText())) errorresponseTtv.setText("");
                if ( start >= 0 ) {
                        if ( passwordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod ) {
                            showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                        } else if ( passwordEdt.getTransformationMethod() instanceof PasswordTransformationMethod ) {
                            showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                        }
                }
                if ( start == 0 && before == 1 ) showHidePasswordTtv.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        showHidePasswordTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( passwordEdt.getTransformationMethod() instanceof PasswordTransformationMethod ) {
                    //use HideReturnsTransformationMethod to make password visible
                    passwordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showHidePasswordTtv.setText(getResources().getString(R.string.hidePassword));
                } else if ( passwordEdt.getTransformationMethod() instanceof HideReturnsTransformationMethod ){
                    passwordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showHidePasswordTtv.setText(getResources().getString(R.string.showPassword));
                }
                passwordEdt.setSelection(passwordEdt.getText().length());
            }
        });
        forgotPasswordTtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( v instanceof TextView ) commonElements.forgotPasswordOnClick();
            }
        });
        registerTtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( v instanceof TextView ) commonElements.registerOnClick();
            }
        });
    }

    private void setRegisterSpan() {
        commonElements.setLoginActivitySpan(registerTtv, getResources().getString(R.string.register), 16, 23, 0);
    }

    private void checkLogin(String username, String password){

        ApiService apiService = RetrofitSingleton.getInstance().getApiService();
        Log.d(debugTag, "ApiService Singleton: " + apiService.toString());
        Call<User> call = apiService.validateUser(username, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(retrofit.Response<User> response, Retrofit retrofit) {
                if (response.body().getError()) {
                    errorresponseTtv.setText(response.body().getError_msg());
                } else {
                    SharedPreferences.Editor sessionPrefsEditor = sessionPrefs.edit();
                    sessionPrefsEditor.putInt("user_id", response.body().getId());
                    sessionPrefsEditor.putString("user_email", response.body().getEmail());
                    sessionPrefsEditor.putString("username", response.body().getUsername());
                    sessionPrefsEditor.apply();

                    Log.d(debugTag, "user id: " + response.body().getId() + "email: " + response.body().getEmail() + " username: " + response.body().getUsername());
                    inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View popup = inflater.inflate(R.layout.popup_login, null);
                    createPopUpWindow(popup);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private void createPopUpWindow(View popup) {
                popupWindow = new PopupWindow(popup,
                                              ViewGroup.LayoutParams.WRAP_CONTENT,
                                              ViewGroup.LayoutParams.WRAP_CONTENT,
                                              true);
                firmSpnr = (Spinner) popup.findViewById(R.id.firmSpnr);
                firmcodeEdt = (EditText) popup.findViewById(R.id.firmcodeEdt);
                popupErrorresponseTtv = (TextView) popup.findViewById(R.id.popupErrorresponseTtv);
                popUpLoginBtn = (Button) popup.findViewById(R.id.popUpLoingSpnrBtn);
                //populate spinner from server request results
                getFirmNamesToPopulateSpinner(new FirmNamesRequestCallback() {
                    @Override
                    public void getFirmNames(List<Firm.FirmElement> firmNames) {
                        //ArrayList<Firm> firmNamesList = new ArrayList<Firm>(Arrays.asList(firmNames));
                        ArrayAdapter<Firm.FirmElement> loginInSpinnerAdapter = new ArrayAdapter<Firm.FirmElement>(getActivity(), android.R.layout.simple_spinner_item, firmNames);
                        loginInSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        loginInSpinnerAdapter.notifyDataSetChanged();
                        firmSpnr.setAdapter(loginInSpinnerAdapter);
                    }
                });

                firmSpnr.setOnItemSelectedListener(this);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ShapeDrawable());
                popupWindow.showAtLocation(popup, Gravity.CENTER, 0, 0);
               // popupWindow.update();

                popUpLoginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (firmcodeEdt.getText().toString().isEmpty()) {
                            String encoded = commonElements.encodeUtf8(getResources().getString(R.string.empty_firm_code));
                            popupErrorresponseTtv.setText(commonElements.decodeUtf8(encoded));
                        } else {
                            ApiService apiService = RetrofitSingleton.getInstance().getApiService();
                            Log.d(debugTag, "ApiService Singleton: " + apiService.toString());
                            Call<Firm> calls = apiService.getFirmByNameAndCode("getFirmByNameAndCode", firmSpnr.getSelectedItem().toString(), firmcodeEdt.getText().toString());
                            calls.enqueue(new Callback<Firm>() {
                                @Override
                                public void onResponse(Response<Firm> response, Retrofit retrofit) {
                                    if (response.body().getError()) {
                                        popupErrorresponseTtv.setText(response.body().getError_msg());
                                        Log.d(debugTag, response.body().getError_msg());
                                    } else {
                                        startBaseActivity();
                                        Log.d(debugTag, String.valueOf(response.isSuccess()));
                                        Log.d(debugTag, response.body().getFirm_name() + " " + response.body().getCity() + " " + response.body().getAddress());
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                }
                            });
                        }
                    }
                });
    }

    private void getFirmNamesToPopulateSpinner(final FirmNamesRequestCallback firmNamesRequestCallback) {
        /*
         * create an instance of Gson object through GsonBuilder
         * Using the registerTypeAdapter() method, we are registering our deserializer and instructing Gson to use our deserializer
         * when deserializing objects of type Firms
         */
        Gson firmsGson = new GsonBuilder()
                        .registerTypeAdapter(Firm[].class, new FirmsDeserializer())
                        .create();

        ApiService apiService = RetrofitSingleton.getInstanceWithCustoGson(firmsGson).getApiService();
        Log.d(debugTag, "Custom Gson ApiService Singleton: " + apiService.toString());
        Call<Firm> call = apiService.getFirm_name("getFirmNames");
        call.enqueue(new Callback<Firm>() {
            @Override
            public void onResponse(retrofit.Response<Firm> response, Retrofit retrofit) {
                Firm firmNames = response.body();
                //for (int i = 0; i<firmNames.length; i++ ) {

                Log.d(debugTag, String.valueOf(response.body().getError()));
                Log.d(debugTag, String.valueOf(response.body().getError_msg()));
                List<Firm.FirmElement> firmElements = response.body().getFirm_element();

                for (int i = 0; i < firmElements.size(); i++) {
                    Log.d(debugTag, firmElements.get(i).getFirm_name());
                }

                firmNamesRequestCallback.getFirmNames(firmElements);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private static Gson getGsonBuilder(){
        Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Firm.class, new FirmsDeserializer())
                        .create();
        return gson;
    }

    private void startBaseActivity() {
        Intent intent = new Intent(getActivity(), AdminBaseActivity.class);
        String user_id, user_email, username;
        if ( sessionPrefs.getInt("user_id", -1) != -1 ) {
            Bundle sessionBundle = new Bundle();
            sessionBundle.putInt("user_id", sessionPrefs.getInt("user_id", -1));
            intent.putExtras(sessionBundle);
            startActivity(intent);
        }
    }

    private interface FirmNamesRequestCallback{
        void getFirmNames(List<Firm.FirmElement> firmNames);
    }

}
