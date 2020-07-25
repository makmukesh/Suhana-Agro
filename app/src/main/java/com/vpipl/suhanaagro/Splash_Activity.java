package com.vpipl.suhanaagro;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.vpipl.suhanaagro.Utils.AppUtils;
import com.vpipl.suhanaagro.Utils.QueryUtils;
import com.vpipl.suhanaagro.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Splash_Activity extends AppCompatActivity {

    private static final String TAG = "Splash_Activity";

    String[] PermissionGroup = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.GET_ACCOUNTS};
    String version;
    private int versionCode;

    public static JSONArray HeadingJarray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        String DeviceModel = manufacturer + " " + model;

        AppController.getSpIsInstall()
                .edit().putString(SPUtils.IS_INSTALL_DeviceModel, "" + DeviceModel)
                .putString(SPUtils.IS_INSTALL_DeviceName, "" + DeviceModel)
                .commit();

        try {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {

                    executeApplicationStatus();

                } else {
                    AppUtils.alertDialogWithFinish(Splash_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            } else
                ActivityCompat.requestPermissions(this, PermissionGroup, 84);

            PackageManager manager = getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = info.versionName;
            versionCode = info.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 84) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                executeApplicationStatus();
            } else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)
                        ) {
                    showDialogOK(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            ActivityCompat.requestPermissions(Splash_Activity.this, PermissionGroup, 84);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            });
                } else {
                    AppUtils.alertDialogWithFinish(this, "Go to settings and Manually Enable these permissions");
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("These Permissions are required for use this Application")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public void executesGetVersionRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Versioninfo", "" + versionCode));
                            response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToGetVersion, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {

                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonArrayData.getJSONObject(0).getString("Status").equalsIgnoreCase("False")) {
                                showUpdateDialog(jsonArrayData.getJSONObject(0).getString("Msg"));
                            } else {
                                moveNextScreen();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Splash_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    public void showUpdateDialog(String Msg) {
        try {
            final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(Msg));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Update Now");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        dialog.dismiss();
                        finish();

                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setVisibility(View.GONE);

            txt_cancel.setText("Update Later");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        moveNextScreen();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    private void startSplash(final Intent intent) {
        try {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            };
            new Handler().postDelayed(runnable, 3000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeApplicationStatus() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToApplicationStatus, "Splash");
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                System.gc();
                Runtime.getRuntime().gc();
                try {
                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                        executesGetVersionRequest();

                    } else {
                        final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, true);
                        TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
                        dialog4all_txt.setText(jsonObject.getString("Message"));

                        TextView txtsubmit = (TextView) dialog.findViewById(R.id.txt_submit);
                        txtsubmit.setText("Exit");
                        txtsubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                                System.exit(0);
                            }
                        });
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void moveNextScreen() {
        try {

            startSplash(new Intent(Splash_Activity.this, HomeActivity_Static.class));

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            {
                startSplash(new Intent(Splash_Activity.this, HomeActivity_Static.class));
            } else {
                startSplash(new Intent(Splash_Activity.this, Login_Activity.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}