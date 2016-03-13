package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.rey.material.widget.EditText;

import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.Register.RegisterPresenterImpl;
import com.votingsystem.tsiro.Register.RegisterPresenterParamsObj;
import com.votingsystem.tsiro.Register.RegisterView;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.votingsystem.R;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 11/12/2015.
 */
public class RegisterFragment extends Fragment implements RegisterView{
    private static final String debugTag = RegisterFragment.class.getSimpleName();
    private static String error_no_connection, empty_fields;
    private RelativeLayout acceptUsernameRlt, acceptPasswordRlt, acceptEmailRlt;
    private TextView signInHereTtv, errorresponseTtv, showHidePasswordTtv, passwordErrorTtv;
    private EditText registerUsernameEdt, registerPasswordEdt, confirmPasswordEdt, registerEmailEdt, firmCodeEdt;
    private Button submitBtn;
    private ProgressView usernameProgressView, emailProgressView;
    private Spinner pickFirmSpnr;
    private View view;
    private LoginActivityCommonElementsAndMuchMore commonElements;
    private SparseIntArray inputValidationCodes;
    private FirmNameWithID spinnerState;
    private BroadcastReceiver broadcastReceiver;
    private HashMap<String, Boolean> inputValidityMap;
    private static Handler mainThreadHandler;
    private static long showhideAcceptPasswordAnimationTargetTimeinMillis;
    private Runnable showhideAcceptPasswordAnimationRunnable;
    private TextWatcher registerPasswordEdtTextWatcher;
    private int connectionStatus, initialConnectionStatus;
    private RegisterPresenterImpl registerPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity ) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_register, container, false);
        acceptUsernameRlt           =   (RelativeLayout) view.findViewById(R.id.acceptUsernameRlt);
        acceptPasswordRlt           =   (RelativeLayout) view.findViewById(R.id.acceptPasswordRlt);
        acceptEmailRlt              =   (RelativeLayout) view.findViewById(R.id.acceptEmailRlt);
        registerUsernameEdt         =   (EditText) view.findViewById(R.id.registerUsernameEdt);
        registerPasswordEdt         =   (EditText) view.findViewById(R.id.registerPasswordEdt);
        confirmPasswordEdt          =   (EditText) view.findViewById(R.id.confirmPasswordEdt);
        registerEmailEdt            =   (EditText) view.findViewById(R.id.registerEmailEdt);
        firmCodeEdt                 =   (EditText) view.findViewById(R.id.firmCodeEdt);
        signInHereTtv               =   (TextView) view.findViewById(R.id.signInHereTtv);
        errorresponseTtv            =   (TextView) view.findViewById(R.id.errorresponseTtv);
        showHidePasswordTtv         =   (TextView) view.findViewById(R.id.showHidePasswordTtv);
        passwordErrorTtv            =   (TextView) view.findViewById(R.id.passwordErrorTtv);
        submitBtn                   =   (Button) view.findViewById(R.id.submitBtn);
        usernameProgressView        =   (ProgressView) view.findViewById(R.id.usernameProgressView);
        emailProgressView           =   (ProgressView) view.findViewById(R.id.emailProgressView);
        pickFirmSpnr                =   (Spinner) view.findViewById(R.id.pickFirmSpnr);
        initialConnectionStatus     =   getArguments().getInt("connectivityStatus");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState == null ) {
            registerPresenter = new RegisterPresenterImpl(this);
            registerPasswordEdt.addTextChangedListener(handleRegisterPasswordTextChanges());
            fillValidityInputMap(getResources().getStringArray(R.array.input_fields_array));
            inputValidationCodes = AppConfig.getCodes();

            registerPresenter.getFirmNamesToPopulateSpnr(initialConnectionStatus);
            connectionStatus = initialConnectionStatus;
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    connectionStatus = intent.getExtras().getInt("connectivityStatus");
                    registerPresenter.getFirmNamesToPopulateSpnr(connectionStatus);
                }
            };
            submitBtn.setTransformationMethod(null);
            setSignInHereSpan();
            registerUsernameEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputValidityMap.put(getResources().getString(R.string.username_tag), false);
                    registerPresenter.handleInputFieldTextChanges(start, before, registerUsernameEdt, acceptUsernameRlt, null, "username");
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            registerUsernameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (!inputValidityMap.get(getResources().getString(R.string.username_tag)))
                            registerPresenter.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), registerUsernameEdt, usernameProgressView, getResources().getString(R.string.usernameValidation), acceptUsernameRlt, getResources().getString(R.string.username_tag), registerUsernameEdt));
                }
            });
            registerPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (!inputValidityMap.get(getResources().getString(R.string.password_tag)))
                            registerPresenter.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), registerPasswordEdt, null, getResources().getString(R.string.passwordValidation), acceptPasswordRlt, getResources().getString(R.string.password_tag), passwordErrorTtv));
                }

            });
            showHidePasswordTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerPasswordEdt.removeTextChangedListener(registerPasswordEdtTextWatcher);
                    registerPresenter.handleShowHidePasswordTtv(registerPasswordEdt);
                    registerPasswordEdt.addTextChangedListener(registerPasswordEdtTextWatcher);
                }
            });
            confirmPasswordEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    registerPresenter.handleInputFieldTextChanges(start, before, confirmPasswordEdt, null, registerPasswordEdt, "confirmpassword");
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            confirmPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) if (confirmPasswordEdt.getText().toString().isEmpty())
                        setText("error", confirmPasswordEdt, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.empty_requried_field))), "#DD2C00");
                }
            });
            registerEmailEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputValidityMap.put(getResources().getString(R.string.email_tag), false);
                    registerPresenter.handleInputFieldTextChanges(start, before, registerEmailEdt, acceptEmailRlt, null, "email");
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            registerEmailEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        if (!inputValidityMap.get(getResources().getString(R.string.email_tag)))
                            registerPresenter.validateInputFieldOnFocusChange(setPresenterObjParams(connectionStatus, isAdded(), registerEmailEdt, emailProgressView, getResources().getString(R.string.emailValidation), acceptEmailRlt, getResources().getString(R.string.email_tag), registerEmailEdt));
                }
            });
            pickFirmSpnr.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner parent, View view, int position, long id) {
                    if ( view instanceof TextView ) ((TextView) view).setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    Log.e(debugTag, "Spinner item selected: " + parent.getSelectedItem().toString());
                    if ( !firmCodeEdt.getText().toString().isEmpty() ) registerPresenter.validateFirmCode(parent.getSelectedItem().toString(), firmCodeEdt.getText().toString());
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*// ) {
                        spinnerState = (FirmNameWithID) pickFirmSpnr.getSelectedItem();
                        Log.d(debugTag, spinnerState.getId() + "");
                        empty_fields = commonElements.encodeUtf8(getResources().getString(R.string.empty_fields));
                        if (!commonElements.validateEditText(new EditText[]{registerUsernameEdt, registerPasswordEdt, confirmPasswordEdt, registerEmailEdt, firmCodeEdt})) {
                            errorresponseTtv.setText(commonElements.decodeUtf8(empty_fields));
                        } else {
                            checkRegister(registerUsernameEdt.getText().toString(), registerPasswordEdt.getText().toString(), confirmPasswordEdt.getText().toString(), registerEmailEdt.getText().toString(), firmCodeEdt.getText().toString());
                        }
                  //  } else {
                        error_no_connection = commonElements.encodeUtf8(getResources().getString(R.string.no_connection));
                        Toast.makeText(getActivity(), commonElements.decodeUtf8(error_no_connection), Toast.LENGTH_SHORT).show();
                  //  }*/
                }
            });
            signInHereTtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof TextView) commonElements.signInHereOnClick();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(debugTag, "onResume");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("networkStateUpdated"));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(debugTag, "onPause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerPresenter.onDestroy();
        this.commonElements = null;
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
    /*
     *  REGISTER VIEW CALLBACKS
     *
     */
    @Override
    public void clearEditextHelpersAndSuccessIcon(String action, RelativeLayout acceptRlt, EditText inputEdt) {
        if ( action.equals("clearSuccessIcon") ) {
            animationStaff(acceptRlt, 1.0f, 0.0f, "gone");
        } else {
            if ( !inputEdt.getHelper().toString().isEmpty() ) inputEdt.setHelper(null);
        }
    }

    @Override
    public void changeTransformationMethod(TransformationMethod transformationMethod, String text) {
        registerPasswordEdt.setTransformationMethod(transformationMethod);
        animationStaff(showHidePasswordTtv, 0.0f, 1.0f, "visible");
        showHidePasswordTtv.setText(text);
        registerPasswordEdt.setSelection(registerPasswordEdt.getText().length());
    }

    @Override
    public void handlePasswordTextChanges(String text, TextView showHidePasswordTtv) {
        showHidePasswordTtv.setVisibility(View.VISIBLE);
        showHidePasswordTtv.setText(text);
    }


    @Override
    public void onSuccessfulFirmNamesSpnrLoad(ArrayList<FirmNameWithID> firmNameWithIDArrayList) {

        ArrayAdapter<FirmNameWithID> spinnerAdapter = new ArrayAdapter<FirmNameWithID>(getActivity(), R.layout.spinner_selection_item, firmNameWithIDArrayList){

            @Override
            public void setDropDownViewResource(int resource) {
                super.setDropDownViewResource(resource);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {


                return super.getDropDownView(position, convertView, parent);
            }

            @Override
            public int getCount() {
                Log.e(debugTag,super.getCount()+"");
                return super.getCount();
            }
        };
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        pickFirmSpnr.setAdapter(spinnerAdapter);
        //pickFirmSpnr.setSelection(spinnerAdapter.getCount()+ 1);
    }

    @Override
    public void onFailure() {
        Log.e(debugTag, "onFailure"); }

    @Override
    public void setToastMsg(int code) { Toast.makeText(getActivity(), commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(inputValidationCodes.get(code)))), Toast.LENGTH_SHORT).show(); }

    @Override
    public void showFieldValidationProgress(ProgressView inputFieldPrgv) { inputFieldPrgv.start(); }

    @Override
    public void hideFieldValidationProgress(ProgressView inputFieldPrgv) { inputFieldPrgv.stop(); }

    @Override
    public void setInputFieldError(int code, View view) {
        if ( view instanceof TextView ) {
            inputValidityMap.put(getResources().getString(R.string.password_tag), false);
            if ( !inputValidityMap.get(getResources().getString(R.string.password_tag)) ) registerPasswordEdt.getChildAt(1).setVisibility(View.GONE);
        }
        setText("error", view, commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(inputValidationCodes.get(code)))), "#DD2C00");
    }

    @Override
    public void onSuccess(RelativeLayout inputValidRlt, String tag) {
        animationStaff(inputValidRlt, 0.0f, 1.0f, "visible");
        if ( tag.equals(getResources().getString(R.string.password_tag)) ) {
            animationStaff(showHidePasswordTtv, 1.0f, 0.0f, "gone");
            showhideAcceptPasswordAnimationRunnable = new Runnable() {
                @Override
                public void run() {
                    animationStaff(showHidePasswordTtv, 0.0f, 1.0f, "visible");
                    animationStaff(acceptPasswordRlt, 1.0f, 0.0f, "gone");
                }
            };
            handlerStaff().postDelayed(showhideAcceptPasswordAnimationRunnable, AppConfig.showhideAcceptDelay);
            showhideAcceptPasswordAnimationTargetTimeinMillis = System.currentTimeMillis() + AppConfig.showhideAcceptDelay;
        }
        inputValidityMap.put(tag, true);
    }

    private RegisterPresenterParamsObj setPresenterObjParams(int connectionStatus, boolean isAdded, EditText inputEditext, ProgressView inputFieldProgressView, String retrofitAction, RelativeLayout validInputRlt, String tag, View errorView) {
        RegisterPresenterParamsObj registerPresenterParamsObj  = new RegisterPresenterParamsObj();
        registerPresenterParamsObj.setConnectionStatus(connectionStatus);
        registerPresenterParamsObj.setIsAdded(isAdded);
        registerPresenterParamsObj.setInputEditText(inputEditext);
        registerPresenterParamsObj.setInputFieldProgressView(inputFieldProgressView);
        registerPresenterParamsObj.setRetrofitAction(retrofitAction);
        registerPresenterParamsObj.setValidInputRlt(validInputRlt);
        registerPresenterParamsObj.setTag(tag);
        registerPresenterParamsObj.setErrorView(errorView);
        return registerPresenterParamsObj;
    }

    private void setText(String action, View view, String decodedMessage, String color) {
        if ( view instanceof EditText ) {
            if (action.equals("error")) ((EditText) view).setHelper(Html.fromHtml("<font color=" + color + ">" + decodedMessage + "</font>"));
        } else if ( view instanceof TextView ) {
            ((TextView) view).setText(decodedMessage);
        }
    }

    private static Handler handlerStaff() {
        if ( mainThreadHandler == null ) mainThreadHandler = new Handler(Looper.getMainLooper());
        return mainThreadHandler;
    }

    private void fillValidityInputMap(String[] fieldsArray) {
        inputValidityMap = new HashMap<>();
        for ( String fieldsArrayItem : fieldsArray ) {
            inputValidityMap.put(fieldsArrayItem, false);
        }
    }

    private TextWatcher handleRegisterPasswordTextChanges() {
        registerPasswordEdtTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputValidityMap.put(getResources().getString(R.string.password_tag), false);
                if ( System.currentTimeMillis() < showhideAcceptPasswordAnimationTargetTimeinMillis ) handlerStaff().removeCallbacks(showhideAcceptPasswordAnimationRunnable);
                registerPresenter.handleRegisterPasswordEdtTextChanges(start, before, registerPasswordEdt, acceptPasswordRlt, showHidePasswordTtv);
                if ( !passwordErrorTtv.getText().toString().isEmpty() ) {
                    passwordErrorTtv.setText(null);
                    registerPasswordEdt.getChildAt(1).setVisibility(View.VISIBLE);
                }
            }
        };
        return registerPasswordEdtTextWatcher;
    }

    private void setSignInHereSpan(){ commonElements.setLoginActivitySpan(signInHereTtv, getResources().getString(R.string.signInHere), 22, 34, 1); }

    private static void animationStaff(View view, float fromAlpha, float toAlpha, String visibility) {
        if ( visibility.equals("visible")) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        Animation fadeAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        fadeAnimation.setDuration(400);
        view.startAnimation(fadeAnimation);
    }

    private static void clearEditextHelper(EditText editText) {
        if ( !editText.getHelper().toString().isEmpty() ) editText.setHelper(null);
    }
}