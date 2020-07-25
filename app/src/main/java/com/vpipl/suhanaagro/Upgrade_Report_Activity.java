package com.vpipl.suhanaagro;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.suhanaagro.Utils.AppUtils;
import com.vpipl.suhanaagro.Utils.QueryUtils;
import com.vpipl.suhanaagro.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Upgrade_Report_Activity extends AppCompatActivity {

    String TAG = "Upgrade_Report_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            if (AppUtils.isNetworkAvailable(this)) {
                createUpgradeReportRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void createUpgradeReportRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("AcNo", AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "")));

        executeUpgradeReportRequest(postParameters);
    }

    private void executeUpgradeReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Upgrade_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Upgrade_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Upgrade_Report_Activity.this, postparameters, QueryUtils.methodToPurchase_Load_UpgradeDetail, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

//                                WriteValues(jsonArrayData);

                            } else {
                                AppUtils.alertDialog(Upgrade_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Upgrade_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Upgrade_Report_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArrayDownLineDetail) {
        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        try {
            float sp = 12;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

            if (jsonArrayDownLineDetail.length() > 0) {

                TableLayout ll = findViewById(R.id.displayLinear);
                ll.removeAllViews();

                TableRow row1 = new TableRow(this);

                Typeface typeface = ResourcesCompat.getFont(this, R.font.opensans_regular);

                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(Color.WHITE);

                TextView A1 = new TextView(this);
                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);
                TextView G1 = new TextView(this);
                TextView H1 = new TextView(this);
                TextView I1 = new TextView(this);

                A1.setText("Sr No");
                B1.setText("Invoice No.");
                C1.setText("Invoice Date");
                D1.setText("Supplier Code");
                E1.setText("Supplier Name");
                G1.setText("Amount");
                H1.setText("Tax Amt");
                I1.setText("Total Amt");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                G1.setPadding(px, px, px, px);
                G1.setPadding(px, px, px, px);
                H1.setPadding(px, px, px, px);
                I1.setPadding(px, px, px, px);
                
                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                G1.setTypeface(typeface);
                G1.setTypeface(typeface);
                H1.setTypeface(typeface);
                I1.setTypeface(typeface);
                
                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER_VERTICAL);
                B1.setGravity(Gravity.CENTER_VERTICAL);
                C1.setGravity(Gravity.CENTER_VERTICAL);
                D1.setGravity(Gravity.CENTER_VERTICAL);
                E1.setGravity(Gravity.CENTER_VERTICAL);
                G1.setGravity(Gravity.CENTER_VERTICAL);
                H1.setGravity(Gravity.CENTER_VERTICAL);
                I1.setGravity(Gravity.CENTER_VERTICAL);


                A1.setTextColor(Color.BLACK);
                B1.setTextColor(Color.BLACK);
                C1.setTextColor(Color.BLACK);
                G1.setTextColor(Color.BLACK);
                D1.setTextColor(Color.BLACK);
                E1.setTextColor(Color.BLACK);
                G1.setTextColor(Color.BLACK);
                H1.setTextColor(Color.BLACK);
                I1.setTextColor(Color.BLACK);


                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(G1);
                row1.addView(H1);
                row1.addView(I1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#666666"));

                ll.addView(row1);
                ll.addView(view);

                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        String InvoiceNo = jobject.getString("PINo");
                        String InvoiceDate = jobject.getString("PIDate");
                        String SupplierCode = (jobject.getString("SupplierCode"));
                        String SupplierName = jobject.getString("SupplierName");
                        String Amount = jobject.getString("Amount");
                        String TaxAmt = jobject.getString("TaxAmount");
                        String TotalAmt = jobject.getString("TotalAmount");

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        if (i % 2 == 0)
                            row.setBackgroundColor(Color.parseColor("#F0F0F0"));
                        else
                            row.setBackgroundColor(Color.WHITE);

                        TextView A = new TextView(this);
                        TextView B = new TextView(this);
                        TextView C = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);
                        TextView G = new TextView(this);
                        TextView H = new TextView(this);
                        TextView I = new TextView(this);

                        A.setText(""+(i+1));
                        B.setText(InvoiceNo);
                        C.setText(InvoiceDate);
                        D.setText(SupplierCode);
                        E.setText(SupplierName);
                        G.setText(Amount);
                        H.setText(TaxAmt);
                        I.setText(TotalAmt);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        G.setPadding(px, px, px, px);
                        G.setPadding(px, px, px, px);
                        H.setPadding(px, px, px, px);
                        I.setPadding(px, px, px, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        G.setTypeface(typeface);
                        G.setTypeface(typeface);
                        H.setTypeface(typeface);
                        I.setTypeface(typeface);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        A.setGravity(Gravity.CENTER_VERTICAL);
                        B.setGravity(Gravity.CENTER_VERTICAL);
                        C.setGravity(Gravity.CENTER_VERTICAL);
                        D.setGravity(Gravity.CENTER_VERTICAL);
                        E.setGravity(Gravity.CENTER_VERTICAL);
                        G.setGravity(Gravity.CENTER_VERTICAL);
                        H.setGravity(Gravity.CENTER_VERTICAL);
                        I.setGravity(Gravity.CENTER_VERTICAL);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(G);
                        row.addView(H);
                        row.addView(I);

                        View view_one = new View(this);
                        view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                        view_one.setBackgroundColor(Color.parseColor("#666666"));

                        ll.addView(row);
                        ll.addView(view_one);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Upgrade_Report_Activity.this);
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
        }
        return super.onOptionsItemSelected(item);
    }
}