package com.vpipl.suhanaagro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vpipl.suhanaagro.Utils.AppUtils;
import com.vpipl.suhanaagro.Utils.QueryUtils;
import com.vpipl.suhanaagro.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login_Activity extends AppCompatActivity {

    private static final String TAG = "Login_Activity";

    Button button_login;
    CheckBox cb_login_rememberMe;
    TextView txt_password_type;

    private TextInputEditText edtxt_userid_member, edtxt_password_member;

    String[] logintypearray = {"Franchisee", "Agent", "Customer"};
    String login_type = "Franchisee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(" " + getResources().getString(R.string.app_name));
        getSupportActionBar().setSubtitle("");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edtxt_userid_member = (TextInputEditText) findViewById(R.id.edtxt_userid_member);
        edtxt_password_member = (TextInputEditText) findViewById(R.id.edtxt_password_member);
        button_login = (Button) findViewById(R.id.button_login);
        cb_login_rememberMe = (CheckBox) findViewById(R.id.cb_login_rememberMe);


        if (AppController.getSpRememberUserInfo().getBoolean(SPUtils.IS_REMEMBER_User, false)) {
            cb_login_rememberMe.setChecked(true);
            String useridmember = AppController.getSpRememberUserInfo().getString(SPUtils.IS_REMEMBER_ID_Member, "");
            edtxt_userid_member.setText(useridmember);
        }

        txt_password_type = (TextView) findViewById(R.id.txt_password_type);
        txt_password_type.setText("Franchisee");
        txt_password_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginTypedialog();
            }
        });

        edtxt_password_member.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 12345 || id == EditorInfo.IME_NULL) {
                    ValidateData();
                    return true;
                }
                return false;
            }
        });

        button_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Login_Activity.this, view);
                ValidateData();
            }
        });
    }


    private void showLoginTypedialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select User Type");
            builder.setItems(logintypearray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_password_type.setText(logintypearray[item]);
                    login_type = logintypearray[item];
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Login_Activity.this);
        }
    }

    private void ValidateData() {
        edtxt_userid_member.setError(null);
        edtxt_password_member.setError(null);

        String userid = edtxt_userid_member.getText().toString();
        String password = edtxt_password_member.getText().toString();
        String password_type = txt_password_type.getText().toString();

        if (TextUtils.isEmpty(userid)) {
            AppUtils.alertDialog(Login_Activity.this, getResources().getString(R.string.error_required_user_id));
            edtxt_userid_member.requestFocus();
        } else {

            if (TextUtils.isEmpty(password)) {
                AppUtils.alertDialog(Login_Activity.this, getResources().getString(R.string.error_required_password));
                edtxt_password_member.requestFocus();
            } else if (TextUtils.isEmpty(password_type)) {
                AppUtils.alertDialog(Login_Activity.this, "Please Select User Type");
                txt_password_type.requestFocus();
            } else {
                if (AppUtils.isNetworkAvailable(Login_Activity.this)) {
                    executeLoginRequest(userid, password, password_type);
                } else {
                    AppUtils.alertDialog(Login_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeLoginRequest(final String userId, final String passwd, final String password_type) {
        try {
            if (AppUtils.isNetworkAvailable(Login_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Login_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Username", userId));
                            postParameters.add(new BasicNameValuePair("Password", passwd));
                            response = AppUtils.callWebServiceWithMultiParam(Login_Activity.this, postParameters, QueryUtils.methodMemberLogin, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() != 0) {
                                    saveLoginUserInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(Login_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Login_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Login_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Login_Activity.this);
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
            AppUtils.showExceptionDialog(Login_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            AppUtils.dismissProgressDialog();

            AppController.getSpUserInfo().edit().clear().commit();

            if (cb_login_rememberMe.isChecked()) {
                String memberuserid = edtxt_userid_member.getText().toString();
                AppController.getSpRememberUserInfo().edit().putBoolean(SPUtils.IS_REMEMBER_User, true)
                        .putString(SPUtils.IS_REMEMBER_ID_Member, memberuserid).commit();
            } else {
                AppController.getSpRememberUserInfo().edit().putBoolean(SPUtils.IS_REMEMBER_User, false)
                        .putString(SPUtils.IS_REMEMBER_ID_Member, "").commit();
            }

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_TYPE, jsonArray.getJSONObject(0).getString("GroupId"))
                    .putString(SPUtils.USER_P_Code, jsonArray.getJSONObject(0).getString("PCode"))
                    .putString(SPUtils.USER_PartyCode, jsonArray.getJSONObject(0).getString("UserPartyCode"))
                    .putString(SPUtils.USER_FIRST_NAME, jsonArray.getJSONObject(0).getString("PartyName"))
                    .putString(SPUtils.USER_MOBILE_NO, jsonArray.getJSONObject(0).getString("MobileNo"))
                    .putString(SPUtils.USER_ADDRESS, jsonArray.getJSONObject(0).getString("Address1"))
                    .putString(SPUtils.USER_DOJ, jsonArray.getJSONObject(0).getString("JoinDate"))
                    .putString(SPUtils.USER_Group_Id, jsonArray.getJSONObject(0).getString("GroupId"))
                    .putString(SPUtils.BranchCode, jsonArray.getJSONObject(0).getString("PartyCode"))
                    .putString(SPUtils.BranchCodeOne, jsonArray.getJSONObject(0).getString("BranchCode"))
                    .putString(SPUtils.User_IntroCode, jsonArray.getJSONObject(0).getString("IntroCode"))
                    .putString(SPUtils.USER_AcNo, jsonArray.getJSONObject(0).getString("AcNo"))
                    .putString(SPUtils.USER_LoginID, jsonArray.getJSONObject(0).getString("LoginID"))
                    .commit();

            startSplash(new Intent(Login_Activity.this, HomeActivity_Static.class));
            AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Login_Activity.this);
        }
    }

    private void startSplash(final Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}