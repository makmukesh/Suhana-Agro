package com.vpipl.suhanaagro;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent_wise_down_agent_activity extends AppCompatActivity {

    String TAG = "Agent_wise_down_agent_activity";
    TextView txt_from_date, txt_to_date, txt_count;
    Button btn_proceed, btn_load_more, button_load_less;

    Calendar myCalendar;
    SimpleDateFormat sdf;
    String whichdate = "", USER_AcNo = "0";
    TextView text_pg_number;
    int StartedRow = 0;
    int PageIndex = 1;

    public DrawerLayout drawer;
    public NavigationView navigationView;

    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number;

    TextInputEditText txt_select_agent;
    public ArrayList<HashMap<String, String>> AgentList = new ArrayList<>();
    private String Usertype;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_wise_down_agent);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            txt_from_date = (TextView) findViewById(R.id.txt_from_date);
            txt_to_date = (TextView) findViewById(R.id.txt_to_date);

            txt_select_agent = (TextInputEditText) findViewById(R.id.txt_select_agent);
            txt_count = (TextView) findViewById(R.id.txt_count);

            text_pg_number = (TextView) findViewById(R.id.text_pg_number);

            btn_proceed = (Button) findViewById(R.id.btn_proceed);
            btn_load_more = (Button) findViewById(R.id.btn_load_more);
            button_load_less = (Button) findViewById(R.id.button_load_less);

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = PageIndex + 1;
                    StartedRow = StartedRow + 25;
                    createDownAgentDetailRequest();
                }
            });


            button_load_less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (PageIndex > 1) {
                        PageIndex = PageIndex - 1;
                        StartedRow = StartedRow - 25;
                        createDownAgentDetailRequest();
                    }
                }
            });


            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = 1;
                    StartedRow = 0;
                    createDownAgentDetailRequest();
                }
            });


            txt_select_agent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAgentDialog();
                    txt_select_agent.setError(null);
                }
            });

            Usertype = AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, "");
            if (Usertype.equalsIgnoreCase("1"))
                txt_select_agent.setEnabled(true);
            else {
                USER_AcNo = AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "");
                txt_select_agent.setEnabled(false);
            }

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMMM yyyy");
            txt_to_date.setText(sdf.format(myCalendar.getTime()));

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -30);
            txt_from_date.setText(sdf.format(c.getTime()));

            txt_from_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_date";
                    new DatePickerDialog(Agent_wise_down_agent_activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });


            txt_to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_to_date";
                    new DatePickerDialog(Agent_wise_down_agent_activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });


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

            if (AppUtils.isNetworkAvailable(this)) {
                if (Usertype.equalsIgnoreCase("1"))
                    executeAgentRequest();
                else
                    createDownAgentDetailRequest();
            } else
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
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
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, HomeActivity_Static.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Query")) {
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Customer Detail")) {
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Customer_List_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Print Receipt")) {
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Receipt_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Agent_wise_down_agent_activity.this);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Login")) {
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Login_Activity.class));
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
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Register_agent_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Detail"))
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Agent_List_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Registration"))
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Register_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Detail"))
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Customer_List_activity.class));
               /* else if (ChildItemTitle.equalsIgnoreCase("Land Selling Summary Report"))
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, ROI_summary_report_activity.class));*/
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Down Agent Detail"))
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Agent_wise_down_agent_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Customer Detail"))
                    startActivity(new Intent(Agent_wise_down_agent_activity.this, Agent_wise_down_customer_activity.class));

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

            /*List<String> LandSelling = new ArrayList<>();
            LandSelling.add("Land Selling Summary Report");*/

            List<String> Report = new ArrayList<>();
            Report.add("Agent Wise Down Agent Detail");
            Report.add("Agent Wise Customer Detail");


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
             /*   listDataHeader.add("Land Selling");
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

    private void createDownAgentDetailRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);
        List<NameValuePair> postParameters = new ArrayList<>();
        String AcNo = "0";

        if (Usertype.equalsIgnoreCase("1")) {
            for (int i = 0; i < AgentList.size(); i++) {
                if (txt_select_agent.getText().toString().equals(AgentList.get(i).get("PartyName"))) {
                    AcNo = AgentList.get(i).get("AcNo");
                }
            }
        } else {
            USER_AcNo = AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "");
            AcNo = USER_AcNo;
        }

        postParameters.add(new BasicNameValuePair("GroupId", AppController.getSpUserInfo().getString(SPUtils.USER_Group_Id, "")));

        postParameters.add(new BasicNameValuePair("AcNo", AcNo));
        postParameters.add(new BasicNameValuePair("Todate", "" + txt_to_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("Fromdate", "" + txt_from_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("ShowRowData", "" + StartedRow));

        executeDownAgentRequest(postParameters);
    }

    private void executeDownAgentRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Agent_wise_down_agent_activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Agent_wise_down_agent_activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Agent_wise_down_agent_activity.this,
                                    postparameters, QueryUtils.methodLoad_AgentWiseDownAgentDetils, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Agent_wise_down_agent_activity.this);
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
                                        AppUtils.alertDialog(Agent_wise_down_agent_activity.this, "No Data Found");
                                } else {
                                    AppUtils.alertDialog(Agent_wise_down_agent_activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Agent_wise_down_agent_activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Agent_wise_down_agent_activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Agent_wise_down_agent_activity.this);
        }
    }

    public void WriteValues(final JSONArray jarray) {

        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        float sp = 10;
        int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
        int px_right = (int) (12 * getResources().getDisplayMetrics().scaledDensity);

        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
        ll.removeAllViews();

        txt_count.setText("( Displaying " + jarray.length() + " Results )");

        text_pg_number.setText("" + PageIndex);

        if (PageIndex <= 1)
            button_load_less.setVisibility(View.GONE);
        else
            button_load_less.setVisibility(View.VISIBLE);

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
//        TextView K1 = new TextView(this);
//        TextView L1 = new TextView(this);
//        TextView M1 = new TextView(this);
//        TextView N1 = new TextView(this);
//        TextView O1 = new TextView(this);
//        TextView P1 = new TextView(this);
//        TextView Q1 = new TextView(this);
//        TextView R1 = new TextView(this);
//        TextView S1 = new TextView(this);
//        TextView T1 = new TextView(this);
//        TextView U1 = new TextView(this);
//        TextView V1 = new TextView(this);
//        TextView W1 = new TextView(this);
//        TextView X1 = new TextView(this);
//        TextView Y1 = new TextView(this);

        A1.setText("S.No.");
        B1.setText("Agent\nCode");
        C1.setText("Agent\nName");
        D1.setText("Join Date");
        E1.setText("Rank");
        F1.setText("Mobile No");
        G1.setText("Downline\nAgent Code");
        H1.setText("Downline Agent\nName");
        I1.setText("D.A.Rank");
        J1.setText("D.A.MobileNo");
//        K1.setText("Phone No.");
//        L1.setText("Birth Date");
//        M1.setText("Intro. Code");
//        N1.setText("Intro. Name");
//        O1.setText("Rank");
//        P1.setText("Nominee");
//        Q1.setText("Nominee Relation");
//        R1.setText("Nominee Age");
//        S1.setText("Bank");
//        T1.setText("Branch");
//        U1.setText("Account No");
//        V1.setText("PAN No");
//        W1.setText("LoginID");
//        X1.setText("Password");
//        Y1.setText("Status");

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
//        K1.setPadding(px, px, px, px);
//        L1.setPadding(px, px, px, px);
//        M1.setPadding(px, px, px, px);
//        N1.setPadding(px, px, px, px);
//        O1.setPadding(px, px, px, px);
//        P1.setPadding(px, px, px, px);
//        Q1.setPadding(px, px, px, px);
//        R1.setPadding(px, px, px, px);
//        S1.setPadding(px, px, px, px);
//        T1.setPadding(px, px, px, px);
//        U1.setPadding(px, px, px, px);
//        V1.setPadding(px, px, px, px);
//        W1.setPadding(px, px, px, px);
//        X1.setPadding(px, px, px, px);
//        Y1.setPadding(px, px, px, px);
        
        
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
//        K1.setTypeface(typeface);
//        L1.setTypeface(typeface);
//        M1.setTypeface(typeface);
//        N1.setTypeface(typeface);
//        O1.setTypeface(typeface);
//        P1.setTypeface(typeface);
//        Q1.setTypeface(typeface);
//        R1.setTypeface(typeface);
//        S1.setTypeface(typeface);
//        T1.setTypeface(typeface);
//        U1.setTypeface(typeface);
//        V1.setTypeface(typeface);
//        W1.setTypeface(typeface);
//        X1.setTypeface(typeface);
//        Y1.setTypeface(typeface);


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
//        K1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        L1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        M1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        N1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        O1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        P1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        Q1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        R1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        S1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        T1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        U1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        V1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        W1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        X1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        Y1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


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
//        K1.setGravity(Gravity.CENTER);
//        L1.setGravity(Gravity.CENTER);
//        M1.setGravity(Gravity.CENTER);
//        N1.setGravity(Gravity.CENTER);
//        O1.setGravity(Gravity.CENTER);
//        P1.setGravity(Gravity.CENTER);
//        Q1.setGravity(Gravity.CENTER);
//        R1.setGravity(Gravity.CENTER);
//        S1.setGravity(Gravity.CENTER);
//        T1.setGravity(Gravity.CENTER);
//        U1.setGravity(Gravity.CENTER);
//        V1.setGravity(Gravity.CENTER);
//        W1.setGravity(Gravity.CENTER);
//        X1.setGravity(Gravity.CENTER);
//        Y1.setGravity(Gravity.CENTER);

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
//        K1.setTextColor(Color.BLACK);
//        L1.setTextColor(Color.BLACK);
//        M1.setTextColor(Color.BLACK);
//        N1.setTextColor(Color.BLACK);
//        O1.setTextColor(Color.BLACK);
//        P1.setTextColor(Color.BLACK);
//        Q1.setTextColor(Color.BLACK);
//        R1.setTextColor(Color.BLACK);
//        S1.setTextColor(Color.BLACK);
//        T1.setTextColor(Color.BLACK);
//        U1.setTextColor(Color.BLACK);
//        V1.setTextColor(Color.BLACK);
//        W1.setTextColor(Color.BLACK);
//        X1.setTextColor(Color.BLACK);
//        Y1.setTextColor(Color.BLACK);

        row1.addView(A1);
        if (Usertype.equalsIgnoreCase("1")) {
            row1.addView(B1);
//        row1.addView(C1);
            row1.addView(D1);
            row1.addView(E1);
            row1.addView(F1);
        }
        row1.addView(G1);
        row1.addView(H1);
        row1.addView(I1);
        row1.addView(J1);
//        row1.addView(K1);
//        row1.addView(L1);
//        row1.addView(M1);
//        row1.addView(N1);
//        row1.addView(O1);
//        row1.addView(P1);
//        row1.addView(Q1);
//        row1.addView(R1);
//        row1.addView(S1);
//        row1.addView(T1);
//        row1.addView(U1);
//        row1.addView(V1);
//        row1.addView(W1);
//        row1.addView(X1);
//        row1.addView(Y1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#999999"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject jobject = jarray.getJSONObject(i);

                String SNo = jobject.getString("Sno");

                String AgentName = "", JoinDate = "", Rank = "", MobileNo = "", AgentCode = "";

                if (Usertype.equalsIgnoreCase("1")) {

                    AgentName = jobject.getString("Agent Name");
                    JoinDate = (jobject.getString("Join Date"));
                    Rank = (jobject.getString("Rank"));
                    MobileNo = jobject.getString("Mobile No");
                    AgentCode = jobject.getString("Agent Code");
                }

                String DownlineAgentCode = jobject.getString("Downline Agent Code");
                String DownlineAgentName = jobject.getString("Downline Agent Name");
                String DARank = jobject.getString("D.A.Rank");
                String DAMobileNo = jobject.getString("D.A.MobileNo");

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
//                TextView K = new TextView(this);
//                TextView L = new TextView(this);
//                TextView M = new TextView(this);
//                TextView N = new TextView(this);
//                TextView O = new TextView(this);
//                TextView P = new TextView(this);
//                TextView Q = new TextView(this);
//                TextView RR = new TextView(this);
//                TextView S = new TextView(this);
//                TextView T = new TextView(this);
//                TextView U = new TextView(this);
//                TextView V = new TextView(this);
//                TextView W = new TextView(this);
//                TextView X = new TextView(this);
//                TextView Y = new TextView(this);


                StringBuilder sb = new StringBuilder(AgentName);

                int ii = 0;
                while ((ii = sb.indexOf(" ", ii + 10)) != -1) {
                    sb.replace(ii, ii + 1, "\n");
                }


                A.setText(SNo);

                B.setText(AgentCode);
                C.setText(sb);
                D.setText(JoinDate);
                E.setText(Rank);
                F.setText(MobileNo);

                G.setText(DownlineAgentCode);
                H.setText(DownlineAgentName);
                I.setText(DARank);
                J.setText(DAMobileNo);


//                K.setText(PhoneNo);
//                L.setText(AppUtils.getDateFromAPIDate(DOB));
//                M.setText(IntroCode);
//                N.setText(IntroName);
//                O.setText(Rank);
//                P.setText(NomineeName);
//                Q.setText(NomineeRelation);
//                RR.setText(NomineeAge);
//                S.setText(BankName);
//                T.setText(BranchName);
//                U.setText(AccountNo);
//                V.setText(PanNo);
//                W.setText(UserID);
//                X.setText(Password);
//                Y.setText(Status);

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
//                K.setPadding(px, px, px, px);
//                L.setPadding(px, px, px, px);
//                M.setPadding(px, px, px, px);
//                N.setPadding(px, px, px, px);
//                O.setPadding(px, px, px, px);
//                P.setPadding(px, px, px, px);
//                Q.setPadding(px, px, px, px);
//                RR.setPadding(px, px, px, px);
//                S.setPadding(px, px, px, px);
//                T.setPadding(px, px, px, px);
//                U.setPadding(px, px, px, px);
//                V.setPadding(px, px, px, px);
//                W.setPadding(px, px, px, px);
//                X.setPadding(px, px, px, px);
//                Y.setPadding(px, px, px, px);


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
//                K.setTypeface(typeface);
//                L.setTypeface(typeface);
//                M.setTypeface(typeface);
//                N.setTypeface(typeface);
//                O.setTypeface(typeface);
//                P.setTypeface(typeface);
//                Q.setTypeface(typeface);
//                RR.setTypeface(typeface);
//                S.setTypeface(typeface);
//                T.setTypeface(typeface);
//                U.setTypeface(typeface);
//                V.setTypeface(typeface);
//                W.setTypeface(typeface);
//                X.setTypeface(typeface);
//                Y.setTypeface(typeface);

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
//                K.setGravity(Gravity.CENTER);
//                L.setGravity(Gravity.CENTER);
//                M.setGravity(Gravity.CENTER);
//                N.setGravity(Gravity.CENTER);
//                O.setGravity(Gravity.CENTER);
//                P.setGravity(Gravity.CENTER);
//                Q.setGravity(Gravity.CENTER);
//                RR.setGravity(Gravity.CENTER);
//                S.setGravity(Gravity.CENTER);
//                T.setGravity(Gravity.CENTER);
//                U.setGravity(Gravity.CENTER);
//                V.setGravity(Gravity.CENTER);
//                W.setGravity(Gravity.CENTER);
//                X.setGravity(Gravity.CENTER);
//                Y.setGravity(Gravity.CENTER);

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
//                P.setTextColor(Color.BLACK);
//                Q.setTextColor(Color.BLACK);
//                RR.setTextColor(Color.BLACK);
//                S.setTextColor(Color.BLACK);
//                T.setTextColor(Color.BLACK);
//                U.setTextColor(Color.BLACK);
//                V.setTextColor(Color.BLACK);
//                W.setTextColor(Color.BLACK);
//                X.setTextColor(Color.BLACK);
//                Y.setTextColor(Color.BLACK);

                row.addView(A);

                if (Usertype.equalsIgnoreCase("1")) {
                    row.addView(B);
//                row.addView(C);
                    row.addView(D);
                    row.addView(E);
                    row.addView(F);
                }

                row.addView(G);
                row.addView(H);
                row.addView(I);
                row.addView(J);
//                row.addView(K);
//                row.addView(L);
//                row.addView(M);
//                row.addView(N);
//                row.addView(O);
//                row.addView(P);
//                row.addView(Q);
//                row.addView(RR);
//                row.addView(S);
//                row.addView(T);
//                row.addView(U);
//                row.addView(V);
//                row.addView(W);
//                row.addView(X);
//                row.addView(Y);

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
            AppUtils.showExceptionDialog(Agent_wise_down_agent_activity.this);
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

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {
                if (whichdate.equalsIgnoreCase("txt_from_date"))
                    txt_from_date.setText(sdf.format(myCalendar.getTime()));
                else if (whichdate.equalsIgnoreCase("txt_to_date"))
                    txt_to_date.setText(sdf.format(myCalendar.getTime()));
            } else {
                AppUtils.alertDialog(Agent_wise_down_agent_activity.this, "Selected Date Can't be After today");
            }
        }
    };


    private void executeAgentRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Agent_wise_down_agent_activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("BranchCode", AppController.getSpUserInfo().getString(SPUtils.BranchCode, "")));
                    response = AppUtils.callWebServiceWithMultiParam(Agent_wise_down_agent_activity.this, postParameters, QueryUtils.methodLoad_AgentForDownAgent, TAG);
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
                            AppUtils.alertDialog(Agent_wise_down_agent_activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Agent_wise_down_agent_activity.this, jsonObject.getString("Message"));
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
                map.put("AcNo", jsonObject.getString("AcNo"));
                map.put("PartyName", WordUtils.capitalizeFully(jsonObject.getString("PartyName")));
                AgentList.add(map);
            }

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
            AppUtils.showExceptionDialog(Agent_wise_down_agent_activity.this);
        }
    }
}
