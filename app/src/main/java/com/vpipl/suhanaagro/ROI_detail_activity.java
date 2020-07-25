package com.vpipl.suhanaagro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.suhanaagro.Adapters.ExpandableListAdapter;
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
import java.util.Map;

public class ROI_detail_activity extends AppCompatActivity {

    String TAG = "ROI_detail_activity";

    public DrawerLayout drawer;
    public NavigationView navigationView;

    int StartedRow = 0;
    int PageIndex = 1;
    TextView text_pg_number;
    Button btn_proceed, btn_load_more, button_load_less;
    TextInputEditText txt_select_agent;

    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roi_summary_detail);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);

            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = (TextView) navHeaderView.findViewById(R.id.txt_welcome_name);

            txt_id_number = (TextView) navHeaderView.findViewById(R.id.txt_id_number);

            expListView = (ExpandableListView) findViewById(R.id.left_drawer);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            enableExpandableList();
            LoadNavigationHeaderItems();

            text_pg_number = (TextView) findViewById(R.id.text_pg_number);
            txt_select_agent = (TextInputEditText) findViewById(R.id.txt_select_agent);

            btn_proceed = (Button) findViewById(R.id.btn_proceed);
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

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = 1;
                    StartedRow = 0;
                    createROIDetailRequest();
                }
            });

            txt_select_agent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAgentDialog();
                    txt_select_agent.setError(null);
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

    private void showAgentDialog() {
        try {

            final String[] AgentArray = {"Paid", "UnPaid"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Type");
            builder.setItems(AgentArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_select_agent.setText(AgentArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ROI_detail_activity.this);
        }
    }

    private void enableExpandableList() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        prepareListDataDistributor(listDataHeader, listDataChild);

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase("Home")) {
                    startActivity(new Intent(ROI_detail_activity.this, HomeActivity_Static.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Query")) {
                    startActivity(new Intent(ROI_detail_activity.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Customer Detail")) {
                    startActivity(new Intent(ROI_detail_activity.this, Customer_List_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Print Receipt")) {
                    startActivity(new Intent(ROI_detail_activity.this, Receipt_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(ROI_detail_activity.this);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Login")) {
                    startActivity(new Intent(ROI_detail_activity.this, Login_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }

                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if (ChildItemTitle.equalsIgnoreCase("Agent Registration"))
                    startActivity(new Intent(ROI_detail_activity.this, Register_agent_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Detail"))
                    startActivity(new Intent(ROI_detail_activity.this, Agent_List_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Registration"))
                    startActivity(new Intent(ROI_detail_activity.this, Register_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Detail"))
                    startActivity(new Intent(ROI_detail_activity.this, Customer_List_activity.class));
               /* else if (ChildItemTitle.equalsIgnoreCase("Land Selling Summary Report"))
                    startActivity(new Intent(ROI_detail_activity.this, ROI_summary_report_activity.class));*/
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Down Agent Detail"))
                    startActivity(new Intent(ROI_detail_activity.this, Agent_wise_down_agent_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Customer Detail"))
                    startActivity(new Intent(ROI_detail_activity.this, Agent_wise_down_customer_activity.class));

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    private void prepareListDataDistributor(List<String> listDataHeader, Map<String, List<String>> listDataChild) {

        try {

            List<String> Empty = new ArrayList<>();
            List<String> Agent = new ArrayList<>();
            Agent.add("Agent Registration");
            Agent.add("Agent Detail");

            List<String> Customer = new ArrayList<>();
            Customer.add("Customer Registration");
            Customer.add("Customer Detail");

           /* List<String> LandSelling = new ArrayList<>();
            LandSelling.add("Land Selling Summary Report");*/

            List<String> Report = new ArrayList<>();
            Report.add("Agent Wise Down Agent Detail");
            Report.add("Agent Wise Customer Detail");

            String Usertype = AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, "");


            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {

                if (Usertype.equalsIgnoreCase("3")) {

                    listDataHeader.add("Home");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                    listDataHeader.add("Customer Detail");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                } else {


                    listDataHeader.add("Home");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                    listDataHeader.add("Agent");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Agent);

                    listDataHeader.add("Customer");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Customer);


//                listDataHeader.add("Report");
//                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Report);

//                listDataHeader.add("Print Receipt");
//                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
                }
                /*listDataHeader.add("Land Selling");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), LandSelling);*/

                listDataHeader.add("Query");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                listDataHeader.add("Logout");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
            } else {
                listDataHeader.add("Login");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createROIDetailRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();

        String TransNo;
        String paid = txt_select_agent.getText().toString().trim();

        TransNo = getIntent().getStringExtra("TransNo");

        if (TextUtils.isEmpty(paid))
            paid = "0";
        else if (paid.equalsIgnoreCase("Paid"))
            paid = "Y";
        else
            paid = "N";

        postParameters.add(new BasicNameValuePair("Paid", paid));
        postParameters.add(new BasicNameValuePair("TransNo", TransNo));
        postParameters.add(new BasicNameValuePair("ShowRowData", "" + StartedRow));
        executeROIDetailsRequest(postParameters);
    }

    private void executeROIDetailsRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(ROI_detail_activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ROI_detail_activity.this);

                        System.gc();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(ROI_detail_activity.this, postparameters, QueryUtils.methodLoad_RIODetail, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ROI_detail_activity.this);
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
                                        AppUtils.alertDialog(ROI_detail_activity.this, "No Data Found");
                                } else {
                                    AppUtils.alertDialog(ROI_detail_activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(ROI_detail_activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ROI_detail_activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ROI_detail_activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray) {

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

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
        TextView L1 = new TextView(this);
        TextView M1 = new TextView(this);
        TextView N1 = new TextView(this);
        TextView O1 = new TextView(this);

        A1.setText("S.No.");
        B1.setText("Bank Name");
        C1.setText("Ac. No");
        D1.setText("Inv. Amount");
        E1.setText("Due Date");
        F1.setText("Next Due Date");
        G1.setText("Inst. No.");
        H1.setText("Net Income");
        I1.setText("Status");
        J1.setText("Paid Date");
        K1.setText("Pay Mode");
        L1.setText("Pay Bank");
        M1.setText("Chq. No.");
        N1.setText("Chq Date");
        O1.setText("Remarks");

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
        B1.setGravity(Gravity.CENTER);
        C1.setGravity(Gravity.CENTER);
        D1.setGravity(Gravity.CENTER);
        E1.setGravity(Gravity.CENTER);
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
        row1.addView(O1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#999999"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {

                JSONObject jobject = jarray.getJSONObject(i);

                String BankName = jobject.getString("BankName");
                String AcNo = jobject.getString("BankAcNo");
                String DueDate = jobject.getString("DueDate");
                String NextDueDate = jobject.getString("NextDueDate");
                String NetIncome = jobject.getString("NetIncome");
                String InvAmount = (jobject.getString("InvAmount"));
                String InstNo = jobject.getString("InstNo");
                String PaidDate = jobject.getString("PaidDate");
                String PayMode = jobject.getString("PayMode");
                String PayBank = jobject.getString("PayByBank");
                String ChqNo = jobject.getString("ChqNo");


                String ChqDate = jobject.getString("ChqDate");


                String Narration = jobject.getString("Narration");
                String Status = jobject.getString("PaidStatus");

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

//                if (i % 2 == 0)
//                    row.setBackgroundColor(Color.parseColor("#F8FFBC"));
//                else
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
                TextView O = new TextView(this);

                A.setText("" + (i + 1));
                B.setText(BankName);
                C.setText(AcNo);
                D.setText(InvAmount);
                E.setText(AppUtils.getDateFromAPIDate(DueDate));
                F.setText(AppUtils.getDateFromAPIDate(NextDueDate));
                G.setText(InstNo);
                H.setText(NetIncome);
                I.setText(Status);

                if (!PaidDate.equalsIgnoreCase("null"))
                    J.setText(AppUtils.getDateFromAPIDate(PaidDate));
                else
                    J.setText("");

                K.setText(PayMode);
                L.setText(PayBank);
                M.setText(ChqNo);

                if (ChqDate.equalsIgnoreCase("null"))
                    N.setText("");
                else
                    N.setText(AppUtils.getDateFromAPIDate(ChqDate));

                O.setText(Narration);

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
                L.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                M.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                N.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                O.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A.setGravity(Gravity.CENTER);
                B.setGravity(Gravity.CENTER);
                C.setGravity(Gravity.CENTER);
                D.setGravity(Gravity.CENTER);
                E.setGravity(Gravity.CENTER);
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

//                A.setTextColor(Color.BLACK);
//                B.setTextColor(Color.BLACK);
//                C.setTextColor(Color.BLACK);
//                D.setTextColor(Color.BLACK);
//                E.setTextColor(Color.BLACK);
//                F.setTextColor(Color.BLACK);
//                G.setTextColor(Color.BLACK);
//                H.setTextColor(Color.BLACK);
//                I.setTextColor(Color.BLACK);
//                J.setTextColor(Color.BLACK);
//                K.setTextColor(Color.BLACK);
//                L.setTextColor(Color.BLACK);
//                M.setTextColor(Color.BLACK);
//                N.setTextColor(Color.BLACK);
//                O.setTextColor(Color.BLACK);

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
            AppUtils.showExceptionDialog(ROI_detail_activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadNavigationHeaderItems() {
        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_welcome_name.setText("");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);

            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_P_Code, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);
        }
    }
}