package com.vpipl.suhanaagro;

import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Stock_Report_Activity extends AppCompatActivity {

    String TAG = "Stock_Report_Activity";

    Button btn_proceed;

    TextInputEditText txt_franchisee, txt_product;

    public ArrayList<HashMap<String, String>> FranchiseeList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> ProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            txt_franchisee = findViewById(R.id.txt_franchisee);
            txt_product = findViewById(R.id.txt_product);

            btn_proceed = findViewById(R.id.btn_proceed);

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(Stock_Report_Activity.this, view);

                    createStockReportRequest();
                }
            });


            txt_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProductDialog();
                }
            });

            txt_franchisee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFranchiseeDialog();
                }
            });

            if (AppUtils.isNetworkAvailable(this)) {
                executeFranchiseeRequest();
                executeProductRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void executeFranchiseeRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Stock_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Stock_Report_Activity.this, postParameters, QueryUtils.methodToLoad_CategoryForStockReport, TAG);
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
                            getFranchiseeResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Stock_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Stock_Report_Activity.this, jsonObject.getString("Message"));
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
                AppUtils.showProgressDialog(Stock_Report_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {

                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Stock_Report_Activity.this, postParameters, QueryUtils.methodToLoad_ProductForStockReport, TAG);
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
                            AppUtils.alertDialog(Stock_Report_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Stock_Report_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getFranchiseeResult(JSONArray jsonArray) {
        try {

            FranchiseeList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("CatId", jsonObject.getString("CatId"));
                map.put("CatName", (jsonObject.getString("CatName")));

                FranchiseeList.add(map);
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

    private void showFranchiseeDialog() {
        try {
            final String[] stateArray = new String[FranchiseeList.size()];
            for (int i = 0; i < FranchiseeList.size(); i++) {
                stateArray[i] = FranchiseeList.get(i).get("CatName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Franchisee");
            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_franchisee.setText(stateArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Stock_Report_Activity.this);
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
            AppUtils.showExceptionDialog(Stock_Report_Activity.this);
        }
    }

    private void createStockReportRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        String product = txt_product.getText().toString();
        String franchisee = txt_franchisee.getText().toString();

        String productcode = "0";
        for (int i = 0; i < ProductList.size(); i++) {
            if (product.equals(ProductList.get(i).get("ProductName"))) {
                productcode = ProductList.get(i).get("ProdId");
            }
        }

        String franchiseeCode = "0";
        for (int i = 0; i < FranchiseeList.size(); i++) {
            if (franchisee.equals(FranchiseeList.get(i).get("CatName"))) {
                franchiseeCode = FranchiseeList.get(i).get("CatId");
            }
        }

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("PartyCode", AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "")));
        postParameters.add(new BasicNameValuePair("ProductID", "" + productcode));
        postParameters.add(new BasicNameValuePair("Category", "" + franchiseeCode));

        executeStockReportRequest(postParameters);
    }

    private void executeStockReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Stock_Report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Stock_Report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Stock_Report_Activity.this, postparameters, QueryUtils.methodToReport_StockReport, TAG);
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
                                AppUtils.alertDialog(Stock_Report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Stock_Report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Stock_Report_Activity.this);
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
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);

                A1.setText("Sr No");
                B1.setText("Product Id");
                D1.setText("Product Name");
                E1.setText("Rate");

                A1.setPadding(px, px, px, px);
                B1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);

                A1.setTypeface(typeface);
                B1.setTypeface(typeface);
                D1.setTypeface(typeface);
                E1.setTypeface(typeface);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER_VERTICAL);
                B1.setGravity(Gravity.CENTER_VERTICAL);
                D1.setGravity(Gravity.CENTER_VERTICAL);
                E1.setGravity(Gravity.CENTER_VERTICAL);

                A1.setTextColor(Color.BLACK);
                B1.setTextColor(Color.BLACK);
                D1.setTextColor(Color.BLACK);
                E1.setTextColor(Color.BLACK);

                row1.addView(A1);
                row1.addView(B1);
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

                        String Category = jobject.getString("CatName");
                        String Code = (jobject.getString("ProdId"));
                        String ProductName = jobject.getString("ProductName");
                        String BatchNo = jobject.getString("BatchCode");
                        String TotalQty = jobject.getString("TotalQty");

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        if (i % 2 == 0)
                            row.setBackgroundColor(Color.parseColor("#F0F0F0"));
                        else
                            row.setBackgroundColor(Color.WHITE);

                        TextView A = new TextView(this);
                        TextView B = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);

                        A.setText(""+(i+1));
                        B.setText(Category);
                        D.setText(ProductName);
                        E.setText(BatchNo);


                        A.setGravity(Gravity.CENTER_VERTICAL);
                        B.setGravity(Gravity.CENTER_VERTICAL);
                        D.setGravity(Gravity.CENTER_VERTICAL);
                        E.setGravity(Gravity.CENTER_VERTICAL);

                        A.setPadding(px, px, px, px);
                        B.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);

                         A.setTypeface(typeface);
                        B.setTypeface(typeface);
                        D.setTypeface(typeface);
                        E.setTypeface(typeface);

                       A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
                        row.addView(B);
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