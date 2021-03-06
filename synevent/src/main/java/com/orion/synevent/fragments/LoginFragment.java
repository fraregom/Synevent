package com.orion.synevent.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orion.synevent.MenuActivity;
import com.orion.synevent.R;
import com.orion.synevent.models.Response;
import com.orion.synevent.apiservice.NetworkUtil;
import com.orion.synevent.models.User;
import com.orion.synevent.utils.Constants;

import java.io.IOException;
import java.util.Map;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.orion.synevent.utils.Validation.validateEmail;
import static com.orion.synevent.utils.Validation.validateFields;

public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtLogin;
    private TextView mTvRegister;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        mSubscriptions = new CompositeSubscription();
        initViews(view);
        if(initSharedPreferences())
        {
            return view;
        }else{
            return view;
        }
    }

    private void initViews(View v) {

        mEtEmail = (EditText) v.findViewById(R.id.et_email);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mBtLogin = (Button) v.findViewById(R.id.btn_login);
        mTiEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mTvRegister = (TextView) v.findViewById(R.id.tv_register);

        mBtLogin.setOnClickListener(view -> login());
        mTvRegister.setOnClickListener(view -> goToRegister());
    }

    private Boolean initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(mSharedPreferences.getAll().isEmpty())
        {
            return false;
        }else{
            Map<String, ?> shareds = mSharedPreferences.getAll();
            User user = new User(shareds.get("email").toString(),shareds.get("password").toString() );
            loginProcess(user.getEmail(),shareds.get("password").toString());
            return true;
        }
    }

    private void login() {

        setError();

        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        int err = 0;

        if (!validateEmail(email)) {

            err++;
            mTiEmail.setError("Couldn't find your Synevent Account");
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Check your password!");
        }

        if (err == 0) {

            loginProcess(email,password);
            mProgressBar.setVisibility(View.VISIBLE);

        } else {

            showSnackBarMessage("Check your info!");
        }
    }

    private void setError() {
        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }

    private void loginProcess(String email, String password) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.EMAIL,email);
        editor.putString(Constants.PASSWORD,password);
        editor.apply();

        User user = new User(email, password);

        mSubscriptions.add(NetworkUtil.getRetrofit().login(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN,response.getToken());
        editor.apply();

        //mEtEmail.setText(null);
        //mEtPassword.setText(null);

        Intent intent = new Intent(getActivity(), MenuActivity.class);
        startActivity(intent);

    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMsg());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showSnackBarMessage("An unknown error has occurred!");
        }
    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message,Snackbar.LENGTH_SHORT).show();
        }
    }

    private void goToRegister(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragments fragment = new RegisterFragments();
        ft.replace(R.id.fragmentFrame,fragment,RegisterFragments.TAG);
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
