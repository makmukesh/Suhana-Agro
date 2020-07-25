package com.vpipl.suhanaagro;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Agent_commission_detail_activity extends AppCompatActivity {

    String TAG = "ROI_detail_activity";

    int StartedRow = 0;
    int PageIndex = 1;
    TextView text_pg_number;
    Button btn_load_more, button_load_less;

    TextView txt_total_business, txt_commission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_commission_detail);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            text_pg_number = (TextView) findViewById(R.id.text_pg_number);
            txt_total_business = (TextView) findViewById(R.id.txt_total_business);
            txt_commission = (TextView) findViewById(R.id.txt_commission);

            btn_load_more = (Button) findViewById(R.id.btn_load_more);
            button_load_less = (Button) findViewById(R.id.button_load_less);

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = PageIndex + 1;
                    StartedRow = StartedRow + 25;
                    createROIDetailRequest();
                }
            });

            button_load_less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (PageIndex > 1) {
                        PageIndex = PageIndex - 1;
                        StartedRow = StartedRow - 25;
                        createROIDetailRequest();
                    }
                }
            });

            if (AppUtils.isNetworkAvailable(this))
                createROIDetailRequest();
            else
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void createROIDetailRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();

        String TransNo;

        TransNo = getIntent().getStringExtra("IDNo");
        postParameters.add(new BasicNameValuePair("IDNo", TransNo));
        postParameters.add(new BasicNameValuePair("ShowRowData", "" + StartedRow));
        executeROIDetailsRequest(postParameters);
    }

    private void executeROIDetailsRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Agent_commission_detail_activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Agent_commission_detail_activity.this);

                        System.gc();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Agent_commission_detail_activity.this, postparameters, QueryUtils.methodLoad_AgentCommissionDetail, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Agent_commission_detail_activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("AgentDetail");
                            JSONArray jsonArrayTotalBusiness = jsonObject.getJSONArray("TotalBusiness");
                            JSONArray jsonArrayNetIncome = jsonObject.getJSONArray("AgentCode");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                WriteValues(jsonArrayData, jsonArrayTotalBusiness, jsonArrayNetIncome);

                            } else {
                                AppUtils.alertDialog(Agent_commission_detail_activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Agent_commission_detail_activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Agent_commission_detail_activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray, JSONArray jsonArrayTotalBusiness, JSONArray jsonArrayNetIncome) {
        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);
        try {

            txt_total_business.setText("Total Business: " + jsonArrayTotalBusiness.getJSONObject(0).getString("TotalBusiness"));
            txt_commission.setText("Commission Amount: " + jsonArrayNetIncome.getJSONObject(0).getString("NetIncome"));

            float sp = 10;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (12 * getResources().getDisplayMetrics().scaledDensity);

            TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
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
            TextView F1 = new TextView(this);
            TextView G1 = new TextView(this);
            TextView H1 = new TextView(this);
            TextView I1 = new TextView(this);
            TextView J1 = new TextView(this);
            TextView K1 = new TextView(this);

            A1.setText("S.No.");
            B1.setText("Commission For");
            C1.setText("Agent Code");
            D1.setText("Agent Name");
            E1.setText("Mobile No.");
            F1.setText("Rank ID");
            G1.setText("Rank Name");
            H1.setText("Plan Name");
            I1.setText("Total Business");
            J1.setText("Commission (%)");
            K1.setText("Commission Amount");

            A1.setTypeface(typeface);
            B1.setTypeface(typeface);
            C1.setTypeface(typeface);
            D1.setTypeface(typeface);
            E1.setTypeface(typeface);
            F1.setTypeface(typeface);
            G1.setTypeface(typeface);
            H1.setTypeface(typeface);
            I1.setTypeface(typeface);
            J1.setTypeface(typeface);
            K1.setTypeface(typeface);
            
            A1.setPadding(px, px, px, px);
            B1.setPadding(px, px, px, px);
            C1.setPadding(px, px, px, px);
            D1.setPadding(px, px, px, px);
            E1.setPadding(px, px, px, px);
            F1.setPadding(px, px, px, px);
            G1.setPadding(px, px, px, px);
            H1.setPadding(px, px, px, px);
            I1.setPadding(px, px, px, px);
            J1.setPadding(px, px, px, px);
            K1.setPadding(px, px, px, px);

            A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            H1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            I1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            J1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            A1.setGravity(Gravity.CENTER);
            B1.setGravity(Gravity.CENTER);
            C1.setGravity(Gravity.CENTER);
            D1.setGravity(Gravity.CENTER_VERTICAL);
            E1.setGravity(Gravity.CENTER);
            F1.setGravity(Gravity.CENTER);
            G1.setGravity(Gravity.CENTER);
            H1.setGravity(Gravity.CENTER);
            I1.setGravity(Gravity.CENTER);
            J1.setGravity(Gravity.CENTER);
            K1.setGravity(Gravity.CENTER);

            A1.setTextColor(Color.BLACK);
            B1.setTextColor(Color.BLACK);
            C1.setTextColor(Color.BLACK);
            D1.setTextColor(Color.BLACK);
            E1.setTextColor(Color.BLACK);
            F1.setTextColor(Color.BLACK);
            G1.setTextColor(Color.BLACK);
            H1.setTextColor(Color.BLACK);
            I1.setTextColor(Color.BLACK);
            J1.setTextColor(Color.BLACK);
            K1.setTextColor(Color.BLACK);

            row1.addView(A1);
            row1.addView(B1);
            row1.addView(C1);
            row1.addView(D1);
            row1.addView(E1);
            row1.addView(F1);
            row1.addView(G1);
            row1.addView(H1);
            row1.addView(I1);
            row1.addView(J1);
            row1.addView(K1);

            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(Color.parseColor("#999999"));

            ll.addView(row1);
            ll.addView(view);

            for (int i = 0; i < jarray.length(); i++) {
                try {

                    JSONObject jobject = jarray.getJSONObject(i);

                    String SNo = jobject.getString("Sno");
                    String CommissionFor = jobject.getString("CommssnFor");
                    String AgentCode = jobject.getString("AgentCode");
                    String AgentName = jobject.getString("AgentName");
                    String MobileNo = jobject.getString("MobileNo");
                    String RankID = (jobject.getString("RankID"));
                    String RankName = jobject.getString("RankName");
                    String PlanName = jobject.getString("PlanName");
                    String TotalBusiness = jobject.getString("TotalBusiness");
                    String Commission_per = jobject.getString("CommssnPer");
                    String CommissionAmount = jobject.getString("CommissionAmount");

                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    row.setBackgroundColor(Color.WHITE);

                    TextView A = new TextView(this);
                    TextView B = new TextView(this);
                    TextView C = new TextView(this);
                    TextView D = new TextView(this);
                    TextView E = new TextView(this);
                    TextView F = new TextView(this);
                    TextView G = new TextView(this);
                    TextView H = new TextView(this);
                    TextView I = new TextView(this);
                    TextView J = new TextView(this);
                    TextView K = new TextView(this);

                    A.setText(SNo);
                    B.setText(CommissionFor);
                    C.setText(AgentCode);
                    D.setText(AgentName);
                    E.setText(MobileNo);
                    F.setText(RankID);
                    G.setText(RankName);
                    H.setText(PlanName);
                    I.setText(TotalBusiness);
                    J.setText(Commission_per);
                    K.setText(CommissionAmount);

                    A.setTypeface(typeface);
                    B.setTypeface(typeface);
                    C.setTypeface(typeface);
                    D.setTypeface(typeface);
                    E.setTypeface(typeface);
                    F.setTypeface(typeface);
                    G.setTypeface(typeface);
                    H.setTypeface(typeface);
                    I.setTypeface(typeface);
                    J.setTypeface(typeface);
                    K.setTypeface(typeface);

                    A.setPadding(px, px, px, px);
                    B.setPadding(px, px, px, px);
                    C.setPadding(px, px, px, px);
                    D.setPadding(px, px, px, px);
                    E.setPadding(px, px, px, px);
                    F.setPadding(px, px, px, px);
                    G.setPadding(px, px, px, px);
                    H.setPadding(px, px, px, px);
                    I.setPadding(px, px, px, px);
                    J.setPadding(px, px, px, px);
                    K.setPadding(px, px, px, px);

                    A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    H.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    I.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    J.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    K.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                    A.setGravity(Gravity.CENTER);
                    B.setGravity(Gravity.CENTER);
                    C.setGravity(Gravity.CENTER);
                    D.setGravity(Gravity.CENTER_VERTICAL);
                    E.setGravity(Gravity.CENTER);
                    F.setGravity(Gravity.CENTER);
                    G.setGravity(Gravity.CENTER);
                    H.setGravity(Gravity.CENTER);
                    I.setGravity(Gravity.CENTER);
                    J.setGravity(Gravity.CENTER);
                    K.setGravity(Gravity.CENTER);

//                    A.setTextColor(Color.BLACK);
//                    B.setTextColor(Color.BLACK);
//                    C.setTextColor(Color.BLACK);
//                    D.setTextColor(Color.BLACK);
//                    E.setTextColor(Color.BLACK);
//                    F.setTextColor(Color.BLACK);
//                    G.setTextColor(Color.BLACK);
//                    H.setTextColor(Color.BLACK);
//                    I.setTextColor(Color.BLACK);
//                    J.setTextColor(Color.BLACK);
//                    K.setTextColor(Color.BLACK);

                    row.addView(A);
                    row.addView(B);
                    row.addView(C);
                    row.addView(D);
                    row.addView(E);
                    row.addView(F);
                    row.addView(G);
                    row.addView(H);
                    row.addView(I);
                    row.addView(J);
                    row.addView(K);

                    View view_one = new View(this);
                    view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    view_one.setBackgroundColor(Color.parseColor("#999999"));

                    ll.addView(row);

                    ll.addView(view_one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            AppUtils.showExceptionDialog(Agent_commission_detail_activity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}