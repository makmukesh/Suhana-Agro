package com.vpipl.suhanaagro;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class Purchase_Summary_Report_Activity extends AppCompatActivity {

    String TAG = "Purchase_Summary_Report_Activity";

    Button btn_proceed;

    TextInputEditText txt_type, txt_supplier, txt_state, edtxt_from_date, edtxt_to_date;

    public ArrayList<HashMap<String, String>> typeList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> supplierList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> stateList = new ArrayList<>();

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
        setContentView(R.layout.activity_purchase_summary_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            txt_type = findViewById(R.id.txt_type);
            txt_supplier = findViewById(R.id.txt_supplier);
            txt_state = findViewById(R.id.txt_state);
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
                    AppUtils.hideKeyboardOnClick(Purchase_Summary_Report_Activity.this, view);

                    createPurchaseReportRequest();
                }
            });


            txt_supplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showsupplierDialog();
                }
            });

            txt_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showtypeDialog();
                }
            });

            txt_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showstateDialog();
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
                executetypeRequest();
                executesupplierRequest();
                executestateRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void executetypeRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Purchase_Summary_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Purchase_Summary_Report_Activity.this, postParameters, QueryUtils.methodToLoad_InvoiceTypeForPurchaseSummaryReport, TAG);
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
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            gettypeResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Purchase_Summary_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Purchase_Summary_Report_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executesupplierRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Purchase_Summary_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {

                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Purchase_Summary_Report_Activity.this, postParameters, QueryUtils.methodToReport_SupplierNameForPurchaseSummaryReport, TAG);
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
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getsupplierResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Purchase_Summary_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Purchase_Summary_Report_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executestateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Purchase_Summary_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {

                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Purchase_Summary_Report_Activity.this, postParameters, QueryUtils.methodToLoad_Purchase_Load_State, TAG);
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
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getstateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Purchase_Summary_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Purchase_Summary_Report_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void gettypeResult(JSONArray jsonArray) {
        try {

            typeList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("SaleTypePrefix", jsonObject.getString("SaleTypePrefix"));
                map.put("SaleType", (jsonObject.getString("SaleType")));

                typeList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getsupplierResult(JSONArray jsonArray) {
        try {
            supplierList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("PartyCode", jsonObject.getString("PartyCode"));
                map.put("PartyName", (jsonObject.getString("PartyName")));

                supplierList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getstateResult(JSONArray jsonArray) {
        try {
            stateList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("StateCode"));
                map.put("State", (jsonObject.getString("StateName")));

                stateList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showtypeDialog() {
        try {
            final String[] stateArray = new String[typeList.size()];
            for (int i = 0; i < typeList.size(); i++) {
                stateArray[i] = typeList.get(i).get("SaleType");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select type");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_type.setText(stateArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Purchase_Summary_Report_Activity.this);
        }
    }

    private void showsupplierDialog() {
        try {

            final String[] weekArray = new String[supplierList.size()];
            for (int i = 0; i < supplierList.size(); i++) {
                weekArray[i] = supplierList.get(i).get("PartyName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Supplier");
            builder.setItems(weekArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_supplier.setText(weekArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Purchase_Summary_Report_Activity.this);
        }
    }

    private void showstateDialog() {
        try {

            final String[] weekArray = new String[stateList.size()];
            for (int i = 0; i < stateList.size(); i++) {
                weekArray[i] = stateList.get(i).get("State");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select State");
            builder.setItems(weekArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_state.setText(weekArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Purchase_Summary_Report_Activity.this);
        }
    }

    private void createPurchaseReportRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        String supplier = txt_supplier.getText().toString();
        String type = txt_type.getText().toString();
        String state = txt_state.getText().toString();

        String suppliercode = "";
        for (int i = 0; i < supplierList.size(); i++) {
            if (supplier.equals(supplierList.get(i).get("PartyName"))) {
                suppliercode = supplierList.get(i).get("PartyCode");
            }
        }

        String typeCode = "";
        for (int i = 0; i < typeList.size(); i++) {
            if (type.equals(typeList.get(i).get("SaleType"))) {
                typeCode = typeList.get(i).get("SaleTypePrefix");
            }
        }

        String stateCode = "";
        for (int i = 0; i < stateList.size(); i++) {
            if (state.equals(stateList.get(i).get("State"))) {
                stateCode = stateList.get(i).get("STATECODE");
            }
        }

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("AcNo", AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "")));
        postParameters.add(new BasicNameValuePair("FromDate", "" + edtxt_from_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToDate", "" + edtxt_to_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("SupplierID", "" + suppliercode));
        postParameters.add(new BasicNameValuePair("State", "" + stateCode));
        postParameters.add(new BasicNameValuePair("SelectType", "" + typeCode));

        executeSaleReportRequest(postParameters);
    }

    private void executeSaleReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Purchase_Summary_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Purchase_Summary_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Purchase_Summary_Report_Activity.this, postparameters, QueryUtils.methodToReport_PurchaseSummaryReport, TAG);
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


                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                WriteValues(jsonArrayData);

                            } else {
                                AppUtils.alertDialog(Purchase_Summary_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Purchase_Summary_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Purchase_Summary_Report_Activity.this);
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
                TextView J1 = new TextView(this);
                TextView K1 = new TextView(this);
                TextView L1 = new TextView(this);
                TextView M1 = new TextView(this);

                A1.setText("S.No.");
                B1.setText("Bill No.");
                C1.setText("Bill Date");
                D1.setText("Supplier Code");
                E1.setText("Supplier Name");
                G1.setText("GSTIN");
                H1.setText("Wallet Amount");
                I1.setText("Amount");
                J1.setText("CGST Amt");
                K1.setText("SGST Amt");
                L1.setText("IGST Amt");
                M1.setText("Net Amt");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                G1.setPadding(px, px, px, px);
                G1.setPadding(px, px, px, px);
                H1.setPadding(px, px, px, px);
                I1.setPadding(px, px, px, px);
                J1.setPadding(px, px, px, px);
                K1.setPadding(px, px, px, px);
                L1.setPadding(px, px, px, px);
                M1.setPadding(px, px, px, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                C1.setTypeface(typeface);
                D1.setTypeface(typeface);
                G1.setTypeface(typeface);
                G1.setTypeface(typeface);
                H1.setTypeface(typeface);
                I1.setTypeface(typeface);
                J1.setTypeface(typeface);
                K1.setTypeface(typeface);
                L1.setTypeface(typeface);
                M1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER_VERTICAL);
                B1.setGravity(Gravity.CENTER_VERTICAL);
                C1.setGravity(Gravity.CENTER_VERTICAL);
                D1.setGravity(Gravity.CENTER_VERTICAL);
                E1.setGravity(Gravity.CENTER_VERTICAL);
                G1.setGravity(Gravity.CENTER_VERTICAL);
                H1.setGravity(Gravity.CENTER_VERTICAL);
                I1.setGravity(Gravity.CENTER_VERTICAL);
                J1.setGravity(Gravity.CENTER_VERTICAL);
                K1.setGravity(Gravity.CENTER_VERTICAL);
                L1.setGravity(Gravity.CENTER_VERTICAL);
                M1.setGravity(Gravity.CENTER_VERTICAL);


                A1.setTextColor(Color.BLACK);
                B1.setTextColor(Color.BLACK);
                C1.setTextColor(Color.BLACK);
                G1.setTextColor(Color.BLACK);
                D1.setTextColor(Color.BLACK);
                E1.setTextColor(Color.BLACK);
                G1.setTextColor(Color.BLACK);
                H1.setTextColor(Color.BLACK);
                I1.setTextColor(Color.BLACK);
                J1.setTextColor(Color.BLACK);
                K1.setTextColor(Color.BLACK);
                L1.setTextColor(Color.BLACK);
                M1.setTextColor(Color.BLACK);


                row1.addView(A1);
                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(G1);
                row1.addView(H1);
                row1.addView(I1);
                row1.addView(J1);
                row1.addView(K1);
                row1.addView(L1);
                row1.addView(M1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#666666"));

                ll.addView(row1);
                ll.addView(view);

                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        final String InvoiceNo = jobject.getString("PINo");
                        String InvoiceDate = jobject.getString("PIDate");
                        String SupplierCode = (jobject.getString("SupplierCode"));
                        String SupplierName = jobject.getString("SupplierName");
                        String GSTIN = jobject.getString("SupplierGSTIN");
                        String WalletAmount = jobject.getString("WalletAmount");
                        String Amount = jobject.getString("Amount");
                        String CGSTAmt = jobject.getString("CGSTAmount");
                        String SGSTAmt = jobject.getString("TaxAmount");
                        String IGSTAmt = jobject.getString("IGSTAmount");
                        String NetAmt = jobject.getString("TotalAmount");

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
                        TextView J = new TextView(this);
                        TextView K = new TextView(this);
                        TextView L = new TextView(this);
                        TextView M = new TextView(this);

                        A.setText("" + (i + 1));
                        B.setText(InvoiceNo);
                        C.setText(InvoiceDate);
                        D.setText(SupplierCode);
                        E.setText(SupplierName);
                        G.setText(GSTIN);
                        H.setText(WalletAmount);
                        I.setText(Amount);
                        J.setText(CGSTAmt);
                        K.setText(SGSTAmt);
                        L.setText(IGSTAmt);
                        M.setText(NetAmt);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        G.setPadding(px, px, px, px);
                        G.setPadding(px, px, px, px);
                        H.setPadding(px, px, px, px);
                        I.setPadding(px, px, px, px);
                        J.setPadding(px, px, px, px);
                        K.setPadding(px, px, px, px);
                        L.setPadding(px, px, px, px);
                        M.setPadding(px, px, px, px);

                        A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        C.setTypeface(typeface);
                        D.setTypeface(typeface);
                        G.setTypeface(typeface);
                        G.setTypeface(typeface);
                        H.setTypeface(typeface);
                        I.setTypeface(typeface);
                        J.setTypeface(typeface);
                        K.setTypeface(typeface);
                        L.setTypeface(typeface);
                        M.setTypeface(typeface);

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        A.setGravity(Gravity.CENTER_VERTICAL);
                        B.setGravity(Gravity.CENTER_VERTICAL);
                        C.setGravity(Gravity.CENTER_VERTICAL);
                        D.setGravity(Gravity.CENTER_VERTICAL);
                        E.setGravity(Gravity.CENTER_VERTICAL);
                        G.setGravity(Gravity.CENTER_VERTICAL);
                        H.setGravity(Gravity.CENTER_VERTICAL);
                        I.setGravity(Gravity.CENTER_VERTICAL);
                        J.setGravity(Gravity.CENTER_VERTICAL);
                        K.setGravity(Gravity.CENTER_VERTICAL);
                        L.setGravity(Gravity.CENTER_VERTICAL);
                        M.setGravity(Gravity.CENTER_VERTICAL);

                        row.addView(A);
                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(G);
                        row.addView(H);
                        row.addView(I);
                        row.addView(J);
                        row.addView(K);
                        row.addView(L);
                        row.addView(M);

                        View view_one = new View(this);
                        view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                        view_one.setBackgroundColor(Color.parseColor("#666666"));

                        ll.addView(row);
                        ll.addView(view_one);

                        B.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url  = getResources().getString(R.string.print_bofinvoice);
                                url = url + "?OrderNo="+InvoiceNo;
                                Intent intent = new  Intent(Purchase_Summary_Report_Activity.this , Business_plan_Activity.class) ;
                                intent.putExtra("URL" , url);
                                startActivity(intent);
                            }
                        });

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
            AppUtils.showExceptionDialog(Purchase_Summary_Report_Activity.this);
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