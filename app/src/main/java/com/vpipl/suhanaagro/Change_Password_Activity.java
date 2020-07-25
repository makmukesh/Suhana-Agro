package com.vpipl.suhanaagro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.vpipl.suhanaagro.Utils.AppUtils;
import com.vpipl.suhanaagro.Utils.QueryUtils;
import com.vpipl.suhanaagro.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Change_Password_Activity extends AppCompatActivity {

    private static final String TAG = "Change_Password_Activity";
    TextInputEditText edtxt_new_password, edtxt_confirm_password, edtxt_old_password;

    Button button_change_password;
    String mobile, old_pass, new_pass, confirm_pass, password_type = "Login";
    TelephonyManager telephonyManager;

    String[] passtypearray = {"Login", "Transaction"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppUtils.setActionbarTitle(getSupportActionBar(), this);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mobile = AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, "");

        edtxt_new_password = (TextInputEditText) findViewById(R.id.edtxt_new_password);
        edtxt_confirm_password = (TextInputEditText) findViewById(R.id.edtxt_confirm_password);
        edtxt_old_password = (TextInputEditText) findViewById(R.id.edtxt_old_password);


        button_change_password = (Button) findViewById(R.id.btn_submit);
        button_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Change_Password_Activity.this, v);
                ValidateData();
            }
        });
    }

    public void ValidateData() {
        old_pass = edtxt_old_password.getText().toString();
        new_pass = edtxt_new_password.getText().toString();
        confirm_pass = edtxt_confirm_password.getText().toString();
        password_type = "Login";

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(old_pass)) {
            AppUtils.alertDialog(Change_Password_Activity.this, getResources().getString(R.string.error_required_old_password));
            focusView = edtxt_old_password;
            cancel = true;
        } else if (TextUtils.isEmpty(new_pass)) {
            AppUtils.alertDialog(Change_Password_Activity.this, getResources().getString(R.string.error_required_new_password));
            focusView = edtxt_new_password;
            cancel = true;
        } else if (TextUtils.isEmpty(confirm_pass)) {
            AppUtils.alertDialog(Change_Password_Activity.this, getResources().getString(R.string.error_required_confirm_password));
            focusView = edtxt_confirm_password;
            cancel = true;
        } else if (!new_pass.equalsIgnoreCase(confirm_pass)) {
            AppUtils.alertDialog(Change_Password_Activity.this, getResources().getString(R.string.password_mismatch));
            focusView = edtxt_confirm_password;
            cancel = true;
        } else if (TextUtils.isEmpty(password_type)) {
            AppUtils.alertDialog(Change_Password_Activity.this, getResources().getString(R.string.error_required_pass_type));
            focusView = edtxt_old_password;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Change_Password_Activity.this)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createChangePasswordRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Change_Password_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createChangePasswordRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            postParameters.add(new BasicNameValuePair("LoginID", AppController.getSpUserInfo().getString(SPUtils.USER_LoginID, "")));
            postParameters.add(new BasicNameValuePair("OldPassword", old_pass));
            postParameters.add(new BasicNameValuePair("NewPassword", new_pass));

            executeChangePasswordRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeChangePasswordRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Change_Password_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Change_Password_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Change_Password_Activity.this, postParameters, QueryUtils.methodToChangePassword, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                final Dialog dialog = AppUtils.createDialog(Change_Password_Activity.this, true);
                                TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
                                dialog4all_txt.setText(jsonObject.getString("Message"));

                                dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();

                                        AppController.getSpUserInfo().edit().clear().commit();
                                        AppController.getSpIsLogin().edit().clear().commit();

                                        Intent intent = new Intent(Change_Password_Activity.this, Login_Activity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                dialog.show();
                            } else {
                                AppUtils.alertDialog(Change_Password_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Change_Password_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Change_Password_Activity.this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
        return super.onOptionsItemSelected(item);
    }
}