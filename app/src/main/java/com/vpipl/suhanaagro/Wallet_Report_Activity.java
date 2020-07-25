package com.vpipl.suhanaagro;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import java.util.HashMap;
import java.util.List;

public class Wallet_Report_Activity extends AppCompatActivity {

    String TAG = "Wallet_Report_Activity";

    Button btn_proceed;

    TextInputEditText edtxt_from_date, edtxt_to_date;

    private String Date_for;
    private SimpleDateFormat sdf;
    private Calendar myCalendar;
    DatePickerDialog datePickerDialog;

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar = Calendar.getInstance();
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (Date_for.equals("edtxt_from_date"))
                edtxt_from_date.setText(sdf.format(myCalendar.getTime()));
            else if (Date_for.equals("edtxt_to_date"))
                edtxt_to_date.setText(sdf.format(myCalendar.getTime()));
        }
    };

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            edtxt_from_date = findViewById(R.id.edtxt_from_date);
            edtxt_to_date = findViewById(R.id.edtxt_to_date);

            btn_proceed = findViewById(R.id.btn_proceed);

            sdf = new SimpleDateFormat("dd-MMM-yyyy");
            myCalendar = Calendar.getInstance();

            Calendar c = Calendar.getInstance();
            edtxt_to_date.setText(sdf.format(c.getTime()));
            c.set(Calendar.DATE, 1);
            edtxt_from_date.setText(sdf.format(c.getTime()));

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Wallet_Report_Activity.this, view);

                    createPurchaseReportRequest();
                }
            });


            edtxt_from_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showdatePicker();
                    Date_for = "edtxt_from_date";
                }
            });

            edtxt_to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showdatePicker();
                    Date_for = "edtxt_to_date";
                }
            });


            if (AppUtils.isNetworkAvailable(this)) {
                createPurchaseReportRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void createPurchaseReportRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("AcNo", AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "")));
        postParameters.add(new BasicNameValuePair("FromDate", "" + edtxt_from_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToDate", "" + edtxt_to_date.getText().toString()));

        executeSaleReportRequest(postParameters);
    }

    private void executeSaleReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Wallet_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wallet_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Wallet_Report_Activity.this, postparameters, QueryUtils.methodToPurchase_Load_WalletDetail, TAG);
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

                                WriteValues(jsonArrayData);

                            } else {
                                AppUtils.alertDialog(Wallet_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wallet_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wallet_Report_Activity.this);
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
              
                A1.setText("Sr No");
                B1.setText("Trans. No.");
                C1.setText("Trans. Date/Time");
                D1.setText("Trans. Amount");
                E1.setText("Remarks");
             

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);
               
                
                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                E1.setTypeface(typeface);
                
                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                
                A1.setGravity(Gravity.CENTER_VERTICAL);
                B1.setGravity(Gravity.CENTER_VERTICAL);
                C1.setGravity(Gravity.CENTER_VERTICAL);
                D1.setGravity(Gravity.CENTER_VERTICAL);
                E1.setGravity(Gravity.CENTER_VERTICAL);
               
                A1.setTextColor(Color.BLACK);
                B1.setTextColor(Color.BLACK);
                C1.setTextColor(Color.BLACK);
                D1.setTextColor(Color.BLACK);
                E1.setTextColor(Color.BLACK);
              
                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
            
                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#666666"));

                ll.addView(row1);
                ll.addView(view);

                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        String TransNo = jobject.getString("TransNo");
                        String TransDate = jobject.getString("DispTransDate");
                        String TransAmount = (jobject.getString("Amount"));
                        String Remarks = jobject.getString("Remarks");

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

                        A.setText(""+(i+1));
                        B.setText(TransNo);
                        C.setText(TransDate);
                        D.setText(TransAmount);
                        E.setText(Remarks);
                        
                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);


                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        E.setTypeface(typeface);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        A.setGravity(Gravity.CENTER_VERTICAL);
                        B.setGravity(Gravity.CENTER_VERTICAL);
                        C.setGravity(Gravity.CENTER_VERTICAL);
                        D.setGravity(Gravity.CENTER_VERTICAL);
                        E.setGravity(Gravity.CENTER_VERTICAL);
                        
                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);

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
            AppUtils.showExceptionDialog(Wallet_Report_Activity.this);
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