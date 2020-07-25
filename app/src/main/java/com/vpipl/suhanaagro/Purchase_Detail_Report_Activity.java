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

public class Purchase_Detail_Report_Activity extends AppCompatActivity {

    String TAG = "Purchase_Detail_Report_Activity";

    Button btn_proceed;

    TextInputEditText edtxt_from_date, edtxt_to_date, txt_type, txt_state, txt_category, txt_product, txt_supplier;

    public ArrayList<HashMap<String, String>> typeList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> stateList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> CategoryList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> ProductList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> supplierList = new ArrayList<>();

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
        setContentView(R.layout.activity_purchase_detail_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            edtxt_from_date = findViewById(R.id.edtxt_from_date);
            edtxt_to_date = findViewById(R.id.edtxt_to_date);
            txt_type = findViewById(R.id.txt_type);
            txt_state = findViewById(R.id.txt_state);
            txt_category = findViewById(R.id.txt_category);
            txt_product = findViewById(R.id.txt_product);
            txt_supplier = findViewById(R.id.txt_supplier);

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
                    AppUtils.hideKeyboardOnClick(Purchase_Detail_Report_Activity.this, view);
                    createPurchaseReportRequest();
                }
            });

            txt_supplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSupplierDialog();
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

            txt_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProductDialog();
                }
            });

            txt_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCategoryDialog();
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
                executeSupplierRequest();
                executestateRequest();
                executeCategoryRequest();
                executeProductRequest();
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
                AppUtils.showProgressDialog(Purchase_Detail_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Purchase_Detail_Report_Activity.this, postParameters, QueryUtils.methodToLoad_InvoiceTypeForProductWisePurchaseDetailReport, TAG);
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
                            AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeSupplierRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Purchase_Detail_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {

                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Purchase_Detail_Report_Activity.this, postParameters, QueryUtils.methodToLoad_SupplierForProductWisePurchaseDetailReport, TAG);
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
                            getSupplierResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
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
                AppUtils.showProgressDialog(Purchase_Detail_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {

                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Purchase_Detail_Report_Activity.this, postParameters, QueryUtils.methodToLoad_Purchase_Load_State, TAG);
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
                            AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeCategoryRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Purchase_Detail_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Purchase_Detail_Report_Activity.this, postParameters, QueryUtils.methodToLoad_CategoryTypeForProductWisePurchaseDetailReport, TAG);
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
                            getCategoryResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeProductRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Purchase_Detail_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {

                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Purchase_Detail_Report_Activity.this, postParameters, QueryUtils.methodToLoad_ProductDetailForProductWisePurchaseDetailReport, TAG);
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
                            getProductResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
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

    private void getSupplierResult(JSONArray jsonArray) {
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

    private void getCategoryResult(JSONArray jsonArray) {
        try {

            CategoryList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("CatId", jsonObject.getString("CatId"));
                map.put("CatName", (jsonObject.getString("CatName")));

                CategoryList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProductResult(JSONArray jsonArray) {
        try {
            ProductList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("ProdId", jsonObject.getString("ProdId"));
                map.put("ProductName", (jsonObject.getString("ProductName")));

                ProductList.add(map);
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
            builder.setTitle("Select Type");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_type.setText(stateArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Purchase_Detail_Report_Activity.this);
        }
    }

    private void showSupplierDialog() {
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
            AppUtils.showExceptionDialog(Purchase_Detail_Report_Activity.this);
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
            AppUtils.showExceptionDialog(Purchase_Detail_Report_Activity.this);
        }
    }

    private void showCategoryDialog() {
        try {
            final String[] stateArray = new String[CategoryList.size()];
            for (int i = 0; i < CategoryList.size(); i++) {
                stateArray[i] = CategoryList.get(i).get("CatName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Category");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_category.setText(stateArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Purchase_Detail_Report_Activity.this);
        }
    }

    private void showProductDialog() {
        try {

            final String[] weekArray = new String[ProductList.size()];
            for (int i = 0; i < ProductList.size(); i++) {
                weekArray[i] = ProductList.get(i).get("ProductName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Product");
            builder.setItems(weekArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_product.setText(weekArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Purchase_Detail_Report_Activity.this);
        }
    }

    private void createPurchaseReportRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        String PartyName = txt_supplier.getText().toString();
        String type = txt_type.getText().toString();
        String state = txt_state.getText().toString();

        String product = txt_product.getText().toString();
        String category = txt_category.getText().toString();


        String PartyNamecode = "";
        for (int i = 0; i < supplierList.size(); i++) {
            if (PartyName.equals(supplierList.get(i).get("PartyName"))) {
                PartyNamecode = supplierList.get(i).get("PartyCode");
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

        String productcode = "";
        for (int i = 0; i < ProductList.size(); i++) {
            if (product.equals(ProductList.get(i).get("ProductName"))) {
                productcode = ProductList.get(i).get("ProdId");
            }
        }

        String categoryCode = "";
        for (int i = 0; i < CategoryList.size(); i++) {
            if (category.equals(CategoryList.get(i).get("CatName"))) {
                categoryCode = CategoryList.get(i).get("CatId");
            }
        }


        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("AcNo", AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "")));
        postParameters.add(new BasicNameValuePair("FromDate", "" + edtxt_from_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("ToDate", "" + edtxt_to_date.getText().toString()));

        postParameters.add(new BasicNameValuePair("CategoryID", "" + categoryCode));
        postParameters.add(new BasicNameValuePair("Productid", "" + productcode));
        postParameters.add(new BasicNameValuePair("StateID", "" + stateCode));
        postParameters.add(new BasicNameValuePair("SelectType", "" + typeCode));

        postParameters.add(new BasicNameValuePair("SupplierID", "" + PartyNamecode));


        executePurchaseReportRequest(postParameters);
    }

    private void executePurchaseReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Purchase_Detail_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Purchase_Detail_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Purchase_Detail_Report_Activity.this, postparameters, QueryUtils.methodToReport_ProductWisePurchaseDetailReport, TAG);
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
                                AppUtils.alertDialog(Purchase_Detail_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Purchase_Detail_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Purchase_Detail_Report_Activity.this);
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
                TextView N1 = new TextView(this);
                TextView O1 = new TextView(this);
                TextView P1 = new TextView(this);
                TextView Q1 = new TextView(this);
                TextView R1 = new TextView(this);
                TextView S1 = new TextView(this);
                TextView T1 = new TextView(this);
                TextView U1 = new TextView(this);
                TextView V1 = new TextView(this);
                TextView W1 = new TextView(this);
                TextView X1 = new TextView(this);
                TextView Y1 = new TextView(this);

                A1.setText("Sr No");
                B1.setText("Seller Code");
                C1.setText("Seller Name");
                D1.setText("GSTIN");
                E1.setText("Bill No.");
                G1.setText("Bill Date");
                H1.setText("Product Id");
                I1.setText("Product Name");
                J1.setText("HSN Code");
                K1.setText("UOM");
                L1.setText("Packing");
                M1.setText("Batch No.");
                N1.setText("Bar Code");
                O1.setText("MRP");
                P1.setText("DP");
                Q1.setText("Rate");
                R1.setText("Qty");
                S1.setText("Amount");
                T1.setText("Wallet (%)");
                U1.setText("Wallet Amount");
                V1.setText("CGST Amt");
                W1.setText("SGST Amt");
                X1.setText("IGST Amt");
                Y1.setText("Total Amt");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                G1.setPadding(px, px, px, px);
                H1.setPadding(px, px, px, px);
                I1.setPadding(px, px, px, px);
                J1.setPadding(px, px, px, px);
                K1.setPadding(px, px, px, px);
                L1.setPadding(px, px, px, px);
                M1.setPadding(px, px, px, px);
                N1.setPadding(px, px, px, px);
                O1.setPadding(px, px, px, px);
                P1.setPadding(px, px, px, px);
                Q1.setPadding(px, px, px, px);
                R1.setPadding(px, px, px, px);
                S1.setPadding(px, px, px, px);
                T1.setPadding(px, px, px, px);
                U1.setPadding(px, px, px, px);
                V1.setPadding(px, px, px, px);
                W1.setPadding(px, px, px, px);
                X1.setPadding(px, px, px, px);
                Y1.setPadding(px, px, px, px);

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
                N1.setTypeface(typeface);
                O1.setTypeface(typeface);
                P1.setTypeface(typeface);
                Q1.setTypeface(typeface);
                R1.setTypeface(typeface);
                S1.setTypeface(typeface);
                T1.setTypeface(typeface);
                U1.setTypeface(typeface);
                V1.setTypeface(typeface);
                W1.setTypeface(typeface);
                X1.setTypeface(typeface);
                Y1.setTypeface(typeface);

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
                N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                O1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                P1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                Q1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                R1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                S1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                T1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                U1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                V1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                W1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                X1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                Y1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

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
                N1.setGravity(Gravity.CENTER_VERTICAL);
                O1.setGravity(Gravity.CENTER_VERTICAL);
                P1.setGravity(Gravity.CENTER_VERTICAL);
                Q1.setGravity(Gravity.CENTER_VERTICAL);
                R1.setGravity(Gravity.CENTER_VERTICAL);
                S1.setGravity(Gravity.CENTER_VERTICAL);
                T1.setGravity(Gravity.CENTER_VERTICAL);
                U1.setGravity(Gravity.CENTER_VERTICAL);
                V1.setGravity(Gravity.CENTER_VERTICAL);
                W1.setGravity(Gravity.CENTER_VERTICAL);
                X1.setGravity(Gravity.CENTER_VERTICAL);
                Y1.setGravity(Gravity.CENTER_VERTICAL);

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
                N1.setTextColor(Color.BLACK);
                O1.setTextColor(Color.BLACK);
                P1.setTextColor(Color.BLACK);
                Q1.setTextColor(Color.BLACK);
                R1.setTextColor(Color.BLACK);
                S1.setTextColor(Color.BLACK);
                T1.setTextColor(Color.BLACK);
                U1.setTextColor(Color.BLACK);
                V1.setTextColor(Color.BLACK);
                W1.setTextColor(Color.BLACK);
                X1.setTextColor(Color.BLACK);
                Y1.setTextColor(Color.BLACK);

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
                row1.addView(N1);
                row1.addView(O1);
                row1.addView(P1);
                row1.addView(Q1);
                row1.addView(R1);
                row1.addView(S1);
                row1.addView(T1);
                row1.addView(U1);
                row1.addView(V1);
                row1.addView(W1);
                row1.addView(X1);
                row1.addView(Y1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#666666"));

                ll.addView(row1);
                ll.addView(view);

                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        String SellerCode = jobject.getString("SellerCode");
                        String SellerName = jobject.getString("SellerName");
                        String GSTIN = jobject.getString("SellerGSTIN");
                        String BillNo = (jobject.getString("BillNo"));
                        String Qty = jobject.getString("Qty");
                        String Rate = jobject.getString("Rate");
                        String Amount = jobject.getString("Amount");
                        String BillDate = jobject.getString("BillDate");
                        String ProductId = jobject.getString("ProductId");
                        String ProductName = jobject.getString("ProductName");
                        String HSNCode = jobject.getString("HSNCode");
                        String UOM = jobject.getString("UOM");
                        String Packing = jobject.getString("Packing");
                        String BatchNo = jobject.getString("BatchNo");
                        String BarCode = jobject.getString("Barcode");
                        String MRP = jobject.getString("MRP");
                        String Wallet = jobject.getString("WalletPer");
                        String WalletAmount = jobject.getString("WalletAmt");
                        String CGSTAmt = jobject.getString("CGSTAmount");
                        String SGSTAmt = jobject.getString("TaxAmount");
                        String IGSTAmt = jobject.getString("IGSTAmount");
                        String TotalAmt = jobject.getString("TotalAmount");
                        String DP = jobject.getString("DP");

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
                        TextView N = new TextView(this);
                        TextView O = new TextView(this);
                        TextView P = new TextView(this);
                        TextView Q = new TextView(this);
                        TextView R = new TextView(this);
                        TextView S = new TextView(this);
                        TextView T = new TextView(this);
                        TextView U = new TextView(this);
                        TextView V = new TextView(this);
                        TextView W = new TextView(this);
                        TextView X = new TextView(this);
                        TextView Y = new TextView(this);

A.setText(""+(i+1));
B.setText(SellerCode);
C.setText(SellerName);
D.setText(GSTIN);
E.setText(BillNo);
G.setText(BillDate);
H.setText(ProductId);
I.setText(ProductName);
J.setText(HSNCode);
K.setText(UOM);
L.setText(Packing);
M.setText(BatchNo);
N.setText(BarCode);
O.setText(MRP);
P.setText(DP);
Q.setText(Rate);
R.setText(Qty);
S.setText(Amount);
T.setText(Wallet);
U.setText(WalletAmount);
V.setText(CGSTAmt);
W.setText(SGSTAmt);
X.setText(IGSTAmt);
Y.setText(TotalAmt);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        G.setPadding(px, px, px, px);
                        H.setPadding(px, px, px, px);
                        I.setPadding(px, px, px, px);
                        J.setPadding(px, px, px, px);
                        K.setPadding(px, px, px, px);
                        L.setPadding(px, px, px, px);
                        M.setPadding(px, px, px, px);
                        N.setPadding(px, px, px, px);
                        O.setPadding(px, px, px, px);
                        P.setPadding(px, px, px, px);
                        Q.setPadding(px, px, px, px);
                        R.setPadding(px, px, px, px);
                        S.setPadding(px, px, px, px);
                        T.setPadding(px, px, px, px);
                        U.setPadding(px, px, px, px);
                        V.setPadding(px, px, px, px);
                        W.setPadding(px, px, px, px);
                        X.setPadding(px, px, px, px);
                        Y.setPadding(px, px, px, px);

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
                        N.setTypeface(typeface);
                        O.setTypeface(typeface);
                        P.setTypeface(typeface);
                        Q.setTypeface(typeface);
                        R.setTypeface(typeface);
                        S.setTypeface(typeface);
                        T.setTypeface(typeface);
                        U.setTypeface(typeface);
                        V.setTypeface(typeface);
                        W.setTypeface(typeface);
                        X.setTypeface(typeface);
                        Y.setTypeface(typeface);

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
                        N.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        O.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        P.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        Q.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        R.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        S.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        T.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        U.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        V.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        W.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        X.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        Y.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

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
                        N.setGravity(Gravity.CENTER_VERTICAL);
                        O.setGravity(Gravity.CENTER_VERTICAL);
                        P.setGravity(Gravity.CENTER_VERTICAL);
                        Q.setGravity(Gravity.CENTER_VERTICAL);
                        R.setGravity(Gravity.CENTER_VERTICAL);
                        S.setGravity(Gravity.CENTER_VERTICAL);
                        T.setGravity(Gravity.CENTER_VERTICAL);
                        U.setGravity(Gravity.CENTER_VERTICAL);
                        V.setGravity(Gravity.CENTER_VERTICAL);
                        W.setGravity(Gravity.CENTER_VERTICAL);
                        X.setGravity(Gravity.CENTER_VERTICAL);
                        Y.setGravity(Gravity.CENTER_VERTICAL);

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
                        row.addView(N);
                        row.addView(O);
                        row.addView(P);
                        row.addView(Q);
                        row.addView(R);
                        row.addView(S);
                        row.addView(T);
                        row.addView(U);
                        row.addView(V);
                        row.addView(W);
                        row.addView(X);
                        row.addView(Y);

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
            AppUtils.showExceptionDialog(Purchase_Detail_Report_Activity.this);
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