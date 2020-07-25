package com.vpipl.suhanaagro;

import android.app.AlertDialog;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.suhanaagro.Utils.AppUtils;
import com.vpipl.suhanaagro.Utils.QueryUtils;
import com.vpipl.suhanaagro.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Agent_commission_report_activity extends AppCompatActivity {

    String TAG = "Agent_commission_report_activity";

    Button btn_proceed, btn_load_more, button_load_less;

    int StartedRow = 0;
    int PageIndex = 1;
    TextView text_pg_number;

    public ArrayList<HashMap<String, String>> AgentList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> SessionList = new ArrayList<>();

    TextInputEditText txt_select_agent, txt_select_viewfor, txt_select_session;
    private String Usertype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_commission_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            txt_select_agent = (TextInputEditText) findViewById(R.id.txt_select_agent);
            txt_select_viewfor = (TextInputEditText) findViewById(R.id.txt_select_viewfor);
            txt_select_session = (TextInputEditText) findViewById(R.id.txt_select_session);

            txt_select_viewfor.setText("Self");
            txt_select_agent.setEnabled(false);

            text_pg_number = (TextView) findViewById(R.id.text_pg_number);

            btn_proceed = (Button) findViewById(R.id.btn_proceed);
            btn_load_more = (Button) findViewById(R.id.btn_load_more);
            button_load_less = (Button) findViewById(R.id.button_load_less);

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = PageIndex + 1;
                    StartedRow = StartedRow + 25;
                    createROISummaryRequest();
                }
            });

            button_load_less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (PageIndex > 1) {
                        PageIndex = PageIndex - 1;
                        StartedRow = StartedRow - 25;
                        createROISummaryRequest();
                    }
                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = 1;
                    StartedRow = 0;
                    createROISummaryRequest();
                }
            });


            Usertype = AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, "");

            if (AppUtils.isNetworkAvailable(this)) {
                executeAgentRequest();
                executeSessionRequest();
            } else
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));

            txt_select_agent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAgentDialog();
                    txt_select_agent.setError(null);
                }
            });

            txt_select_session.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSessionDialog();
                    txt_select_session.setError(null);
                }
            });

            txt_select_viewfor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Usertype.equalsIgnoreCase("3")) showViewforDialog();
                    txt_select_viewfor.setError(null);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void createROISummaryRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        String UserPartyCode = AppController.getSpUserInfo().getString(SPUtils.USER_PartyCode, "");
        String GroupID = AppController.getSpUserInfo().getString(SPUtils.USER_Group_Id, "");

        String SelectedAgentCode = "0";
        if (txt_select_viewfor.getText().toString().equalsIgnoreCase("Downline")) {
            for (int i = 0; i < AgentList.size(); i++) {
                if (txt_select_agent.getText().toString().equals(AgentList.get(i).get("PartyName"))) {
                    SelectedAgentCode = AgentList.get(i).get("UserPartyCode");
                }
            }
        }

        String SessionID = "0";
        for (int i = 0; i < SessionList.size(); i++) {
            if (txt_select_session.getText().toString().equals(SessionList.get(i).get("SessnName"))) {
                SessionID = SessionList.get(i).get("SessID");
            }
        }

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("GroupId", GroupID));
        postParameters.add(new BasicNameValuePair("UserPartyCode", UserPartyCode));
        postParameters.add(new BasicNameValuePair("ShowRowData", "" + StartedRow));

        postParameters.add(new BasicNameValuePair("ViewFor", "" + txt_select_viewfor.getText().toString().substring(0, 1)));
        postParameters.add(new BasicNameValuePair("SelectedAgentCode", "" + SelectedAgentCode));
        postParameters.add(new BasicNameValuePair("BranchCode", "" + AppController.getSpUserInfo().getString(SPUtils.BranchCode, "")));

        postParameters.add(new BasicNameValuePair("SessionID", "" + SessionID));

        executeROISummaryReportRequest(postParameters);
    }

    private void executeROISummaryReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Agent_commission_report_activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Agent_commission_report_activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            response = AppUtils.callWebServiceWithMultiParam(Agent_commission_report_activity.this,
                                    postparameters, QueryUtils.methodLoad_AgentCommissionSummary, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Agent_commission_report_activity.this);
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
                                if (jsonObject.getString("Message").equalsIgnoreCase("Successfully.!")) {
                                    if (jsonArrayData.length() > 0)

                                        WriteValues(jsonArrayData);

                                    else
                                        AppUtils.alertDialog(Agent_commission_report_activity.this, jsonObject.getString("Message"));
                                } else {
                                    AppUtils.alertDialog(Agent_commission_report_activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Agent_commission_report_activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Agent_commission_report_activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Agent_commission_report_activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray) {

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
        int px_right = (int) (12 * getResources().getDisplayMetrics().scaledDensity);

        text_pg_number.setText("" + PageIndex);

        if (PageIndex <= 1)
            button_load_less.setVisibility(View.GONE);
        else
            button_load_less.setVisibility(View.VISIBLE);

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
        TextView L1 = new TextView(this);
        TextView M1 = new TextView(this);
        TextView N1 = new TextView(this);
        TextView O1 = new TextView(this);

        A1.setText("S.No.");
        B1.setText("Session");
        C1.setText("Branch Name");
        D1.setText("Agent Code");
        E1.setText("Agent Name");
        F1.setText("Join Date");
        G1.setText("Rank ID");
        H1.setText("Rank Name");
        I1.setText("Net Income");
        J1.setText("TDS (%)");
        K1.setText("TDS Amount");
        L1.setText("Admin (%)");
        M1.setText("Admin Charge");
        N1.setText("Cheque Amt");
        O1.setText("View");

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
        L1.setPadding(px, px, px, px);
        M1.setPadding(px, px, px, px);
        N1.setPadding(px, px, px, px);
        O1.setPadding(px, px, px, px);

      
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
        L1.setTypeface(typeface);
        M1.setTypeface(typeface);
        N1.setTypeface(typeface);
        O1.setTypeface(typeface);

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
        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        O1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        B1.setGravity(Gravity.CENTER_VERTICAL);
        C1.setGravity(Gravity.CENTER_VERTICAL);
        D1.setGravity(Gravity.CENTER_VERTICAL);
        E1.setGravity(Gravity.CENTER_VERTICAL);
        F1.setGravity(Gravity.CENTER);
        G1.setGravity(Gravity.CENTER);
        H1.setGravity(Gravity.CENTER);
        I1.setGravity(Gravity.CENTER);
        J1.setGravity(Gravity.CENTER);
        K1.setGravity(Gravity.CENTER);
        L1.setGravity(Gravity.CENTER);
        M1.setGravity(Gravity.CENTER);
        N1.setGravity(Gravity.CENTER);
        O1.setGravity(Gravity.CENTER);

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
        L1.setTextColor(Color.BLACK);
        M1.setTextColor(Color.BLACK);
        N1.setTextColor(Color.BLACK);
        O1.setTextColor(Color.BLACK);

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
        row1.addView(L1);
        row1.addView(M1);
        row1.addView(N1);
//        row1.addView(O1);


        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#999999"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject jobject = jarray.getJSONObject(i);

                String Session = jobject.getString("SessnName");
                String BranchName = jobject.getString("BranchName");
                String AgentCode = jobject.getString("AgentCode");
                String AgentName = jobject.getString("AgentName");

                String RankID = (jobject.getString("RankID"));
                String RankName = jobject.getString("RankName");
                String NetIncome = jobject.getString("NetIncome");
                String TDS_per = jobject.getString("TDSPer");
                String TDS_amt = jobject.getString("TDSAmount");
                String adm_per = jobject.getString("AdminPer");
                String adm_amt = jobject.getString("AdminCharge");
                String chq_amt = jobject.getString("ChqAmt");
                String JoinDate = jobject.getString("JoinDate");

                String Sno = jobject.getString("Sno");
                final int AcNo = jobject.getInt("AcNo");

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
                TextView L = new TextView(this);
                TextView M = new TextView(this);
                TextView N = new TextView(this);
                final TextView O = new TextView(this);

                A.setText(Sno);
                B.setText(Session);
                C.setText(BranchName);
                D.setText(AgentCode);
                E.setText(AgentName);
                F.setText(JoinDate);
                G.setText(RankID);
                H.setText(RankName);
                I.setText(NetIncome);
                J.setText(TDS_per);
                K.setText(TDS_amt);
                L.setText(adm_per);
                M.setText(adm_amt);
                N.setText(chq_amt);
                O.setText("View");

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
                L.setPadding(px, px, px, px);
                M.setPadding(px, px, px, px);
                N.setPadding(px, px, px, px);
                O.setPadding(px, px, px, px);

               
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
                L.setTypeface(typeface);
                M.setTypeface(typeface);
                N.setTypeface(typeface);
                O.setTypeface(typeface);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER_VERTICAL);
                C.setGravity(Gravity.CENTER_VERTICAL);
                D.setGravity(Gravity.CENTER_VERTICAL);
                E.setGravity(Gravity.CENTER_VERTICAL);
                F.setGravity(Gravity.CENTER);
                G.setGravity(Gravity.CENTER);
                H.setGravity(Gravity.CENTER);
                I.setGravity(Gravity.CENTER);
                J.setGravity(Gravity.CENTER);
                K.setGravity(Gravity.CENTER);
                L.setGravity(Gravity.CENTER);
                M.setGravity(Gravity.CENTER);
                N.setGravity(Gravity.CENTER);
                O.setGravity(Gravity.CENTER);

                O.setTextColor(Color.BLUE);

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
                row.addView(L);
                row.addView(M);
                row.addView(N);
                row.addView(O);

                O.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Agent_commission_report_activity.this, Agent_commission_detail_activity.class).putExtra("IDNo",""+ AcNo));
                    }
                });

                View view_one = new View(this);
                view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view_one.setBackgroundColor(Color.parseColor("#999999"));

                ll.addView(row);
                ll.addView(view_one);

            } catch (Exception e) {
                e.printStackTrace();
            }
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
            AppUtils.showExceptionDialog(Agent_commission_report_activity.this);
        }
        return super.onOptionsItemSelected(item);
    }


    private void executeAgentRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Agent_commission_report_activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    String Groupid = AppController.getSpUserInfo().getString(SPUtils.USER_Group_Id, "");
                    String BranchCode;

                    if (Groupid.equalsIgnoreCase("1")) {
                        BranchCode = AppController.getSpUserInfo().getString(SPUtils.BranchCodeOne, "");
                    } else if (Groupid.equalsIgnoreCase("2")) {
                        BranchCode = AppController.getSpUserInfo().getString(SPUtils.USER_PartyCode, "");
                    } else {
                        BranchCode = AppController.getSpUserInfo().getString(SPUtils.User_IntroCode, "");
                    }

                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("GroupId", Groupid));
                    postParameters.add(new BasicNameValuePair("BranchCode", BranchCode));

                    response = AppUtils.callWebServiceWithMultiParam(Agent_commission_report_activity.this, postParameters, QueryUtils.methodMaster_FillAgent, TAG);
                } catch (Exception ignored) {
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
                            getAgentResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Agent_commission_report_activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Agent_commission_report_activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getAgentResult(JSONArray jsonArray) {
        try {

            AgentList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("UserPartyCode", jsonObject.getString("UserPartyCode"));
                map.put("PartyName", WordUtils.capitalizeFully(jsonObject.getString("PartyName")));
                AgentList.add(map);
            }

            createROISummaryRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAgentDialog() {
        try {

            final String[] AgentArray = new String[AgentList.size()];
            for (int i = 0; i < AgentList.size(); i++) {
                AgentArray[i] = AgentList.get(i).get("PartyName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Agent");
            builder.setItems(AgentArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_select_agent.setText(AgentArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Agent_commission_report_activity.this);
        }
    }

    private void executeSessionRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {

                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Agent_commission_report_activity.this, postParameters, QueryUtils.methodLoad_Session, TAG);
                } catch (Exception ignored) {
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
                            getSessionResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Agent_commission_report_activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Agent_commission_report_activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getSessionResult(JSONArray jsonArray) {
        try {

            SessionList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("SessID", jsonObject.getString("SessID"));
                map.put("SessnName", (jsonObject.getString("SessnName")));
                SessionList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSessionDialog() {
        try {
            final String[] AgentArray = new String[SessionList.size()];
            for (int i = 0; i < SessionList.size(); i++) {
                AgentArray[i] = SessionList.get(i).get("SessnName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Session");
            builder.setItems(AgentArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_select_session.setText(AgentArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Agent_commission_report_activity.this);
        }
    }

    private void showViewforDialog() {
        try {

            final String[] AgentArray = {"Self", "Downline"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select View For");
            builder.setItems(AgentArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_select_viewfor.setText(AgentArray[item]);

                    if (AgentArray[item].equalsIgnoreCase("Self")) {
                        txt_select_agent.setText("");
                        txt_select_agent.setEnabled(false);
                    } else {
                        txt_select_agent.setEnabled(true);
                        txt_select_agent.setText("");
                    }

                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }
}