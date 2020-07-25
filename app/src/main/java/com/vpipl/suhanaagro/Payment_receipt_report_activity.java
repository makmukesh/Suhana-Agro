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

public class Payment_receipt_report_activity extends AppCompatActivity {

    String TAG = "Payment_receipt_report_activity";

    Button btn_proceed, btn_load_more, button_load_less;

    int StartedRow = 0;
    int PageIndex = 1;
    TextView text_pg_number;

    public DrawerLayout drawer;
    public NavigationView navigationView;

    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number;

    public ArrayList<HashMap<String, String>> AgentList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> productList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> planList = new ArrayList<>();

    TextInputEditText txt_select_viewfor, txt_select_agent, txt_selectPlan, txt_selectProduct;
    private String Usertype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymenr_receipt_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            txt_selectPlan = (TextInputEditText) findViewById(R.id.txt_selectPlan);
            txt_selectProduct = (TextInputEditText) findViewById(R.id.txt_selectProduct);
            txt_select_agent = (TextInputEditText) findViewById(R.id.txt_select_agent);
            txt_select_viewfor = (TextInputEditText) findViewById(R.id.txt_select_viewfor);

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

            txt_select_viewfor.setText("Self");
            txt_select_agent.setEnabled(false);

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


            txt_selectPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (planList.size() != 0) {
                        showPlanDialog();
                        txt_selectPlan.clearFocus();
                        txt_selectPlan.setError(null);
                    }
                }
            });


            txt_selectProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productList.size() != 0) {
                        showProductDialog();
                        txt_selectProduct.clearFocus();
                        txt_selectProduct.setError(null);
                    }
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

            Usertype = AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, "");

            if (AppUtils.isNetworkAvailable(this)) {

                executePlanRequest();

                if (!Usertype.equalsIgnoreCase("3")) {
                    executeAgentRequest();
                    createROISummaryRequest();
                } else
                    createROISummaryRequest();
            } else
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));


            txt_select_agent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAgentDialog();
                    txt_select_agent.setError(null);
                }
            });

            txt_select_viewfor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Usertype.equalsIgnoreCase("3")) showViewforDialog();
                    txt_select_viewfor.setError(null);
                }
            });

            if (Usertype.equalsIgnoreCase("3"))
            {
                findViewById(R.id.ll_select_agent).setVisibility(View.GONE);
                txt_select_viewfor.setEnabled(false);
                txt_select_viewfor.setClickable(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void showPlanDialog() {
        try {

            final String[] planArray = new String[planList.size()];
            for (int i = 0; i < planList.size(); i++) {
                planArray[i] = planList.get(i).get("Planname");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Plan");
            builder.setItems(planArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_selectPlan.setText(planArray[item]);

                    String PlanId = "0";
                    for (int i = 0; i < planList.size(); i++) {
                        if (planArray[item].equals(planList.get(i).get("Planname"))) {
                            PlanId = planList.get(i).get("PlanId");
                        }
                    }

                    executeProductRequest(PlanId);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Payment_receipt_report_activity.this);
        }
    }

    private void showProductDialog() {
        try {
            final String[] productArray = new String[productList.size()];
            for (int i = 0; i < productList.size(); i++) {
                productArray[i] = productList.get(i).get("Productname");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Product");
            builder.setItems(productArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    txt_selectProduct.setText(productArray[item]);

                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Payment_receipt_report_activity.this);
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
                    startActivity(new Intent(Payment_receipt_report_activity.this, HomeActivity_Static.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Query")) {
                    startActivity(new Intent(Payment_receipt_report_activity.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Customer Detail")) {
                    startActivity(new Intent(Payment_receipt_report_activity.this, Customer_List_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Print Receipt")) {
                    startActivity(new Intent(Payment_receipt_report_activity.this, Receipt_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Payment_receipt_report_activity.this);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Login")) {
                    startActivity(new Intent(Payment_receipt_report_activity.this, Login_Activity.class));
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
                    startActivity(new Intent(Payment_receipt_report_activity.this, Register_agent_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Detail"))
                    startActivity(new Intent(Payment_receipt_report_activity.this, Agent_List_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Registration"))
                    startActivity(new Intent(Payment_receipt_report_activity.this, Register_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Detail"))
                    startActivity(new Intent(Payment_receipt_report_activity.this, Customer_List_activity.class));
               /* else if (ChildItemTitle.equalsIgnoreCase("Land Selling Summary Report"))
                    startActivity(new Intent(Payment_receipt_report_activity.this, Payment_receipt_report_activity.class));*/
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Down Agent Detail"))
                    startActivity(new Intent(Payment_receipt_report_activity.this, Agent_wise_down_agent_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Customer Detail"))
                    startActivity(new Intent(Payment_receipt_report_activity.this, Agent_wise_down_customer_activity.class));

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

          /*  List<String> LandSelling = new ArrayList<>();
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
              /*  listDataHeader.add("Land Selling");
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

    private void createROISummaryRequest() {
        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();

        String GroupId = AppController.getSpUserInfo().getString(SPUtils.USER_Group_Id, "");
        String UserPartyCode = AppController.getSpUserInfo().getString(SPUtils.USER_PartyCode, "");

        String SelectedAgentCode = "0";

        String ProductID = "0";
        for (int i = 0; i < productList.size(); i++) {
            if (txt_selectProduct.getText().toString().equals(productList.get(i).get("Productname"))) {
                ProductID = productList.get(i).get("ProductCode");
            }
        }

        String PlanId = "0";
        for (int i = 0; i < planList.size(); i++) {
            if (txt_selectPlan.getText().toString().equals(planList.get(i).get("Planname"))) {
                PlanId = planList.get(i).get("PlanId");
            }
        }

        if (txt_select_viewfor.getText().toString().equalsIgnoreCase("Downline")) {
            for (int i = 0; i < AgentList.size(); i++) {
                if (txt_select_agent.getText().toString().equals(AgentList.get(i).get("PartyName"))) {
                    SelectedAgentCode = AgentList.get(i).get("UserPartyCode");
                }
            }
        }

        postParameters.add(new BasicNameValuePair("GroupId", GroupId));
        postParameters.add(new BasicNameValuePair("PlanID", PlanId));
        postParameters.add(new BasicNameValuePair("ProductCode", ProductID));
        postParameters.add(new BasicNameValuePair("UserPartyCode", UserPartyCode));
        postParameters.add(new BasicNameValuePair("ShowRowData", "" + StartedRow));


        postParameters.add(new BasicNameValuePair("ViewFor", "" + txt_select_viewfor.getText().toString().substring(0, 1)));
        postParameters.add(new BasicNameValuePair("SelectedAgentCode", "" + SelectedAgentCode));

        postParameters.add(new BasicNameValuePair("BranchCode", "" + AppController.getSpUserInfo().getString(SPUtils.BranchCode, "")));

        executeROISummaryReportRequest(postParameters);
    }

    private void executeROISummaryReportRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Payment_receipt_report_activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Payment_receipt_report_activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {

                            response = AppUtils.callWebServiceWithMultiParam(Payment_receipt_report_activity.this,
                                    postparameters, QueryUtils.methodLoad_Payment_Receipt, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Payment_receipt_report_activity.this);
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
                                        AppUtils.alertDialog(Payment_receipt_report_activity.this, "No Data Found");
                                } else {
                                    AppUtils.alertDialog(Payment_receipt_report_activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Payment_receipt_report_activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Payment_receipt_report_activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Payment_receipt_report_activity.this);
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
        TextView Z1 = new TextView(this);
        TextView Z2 = new TextView(this);
        TextView Z3 = new TextView(this);
        TextView G1 = new TextView(this);
        TextView H1 = new TextView(this);
        TextView I1 = new TextView(this);
        TextView J1 = new TextView(this);
        TextView K1 = new TextView(this);
        TextView K2 = new TextView(this);
        TextView L1 = new TextView(this);
        TextView M1 = new TextView(this);
        TextView N1 = new TextView(this);
        TextView O1 = new TextView(this);

        TextView OO1 = new TextView(this);
        TextView OO2 = new TextView(this);
        TextView OO3 = new TextView(this);
        TextView OO4 = new TextView(this);

        A1.setText("S.No.");
        B1.setText("Customer Code");
        C1.setText("Customer Name");
        D1.setText("Mobile No");
        N1.setText("Trans No.");
        E1.setText("Plan Name");
        F1.setText("Product Name");
        Z1.setText("Plan Amount");
        Z2.setText("Bank Name");
        Z3.setText("Ac. No.");
        G1.setText("Inv. Amount");
        H1.setText("Due Date");
        I1.setText("Next Due Date");
        J1.setText("Inst. No.");
        K1.setText("Net Income");
        K2.setText("Status");
        L1.setText("Paid Date");
        M1.setText("Pay Mode");
        OO1.setText("Pay Bank");
        OO2.setText("Chq. No.");
        OO3.setText("Chq Date");
        OO4.setText("Narration");
        O1.setText(" ");


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
        Z1.setPadding(px, px, px, px);
        Z2.setPadding(px, px, px, px);
        Z3.setPadding(px, px, px, px);
        K2.setPadding(px, px, px, px);
        OO1.setPadding(px, px, px, px);
        OO2.setPadding(px, px, px, px);
        OO3.setPadding(px, px, px, px);
        OO4.setPadding(px, px, px, px);

      
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
        Z1.setTypeface(typeface);
        Z2.setTypeface(typeface);
        Z3.setTypeface(typeface);
        K2.setTypeface(typeface);
        OO1.setTypeface(typeface);
        OO2.setTypeface(typeface);
        OO3.setTypeface(typeface);
        OO4.setTypeface(typeface);

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
        Z1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Z2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        Z3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        K2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        OO1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        OO2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        OO3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        OO4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

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
        Z1.setGravity(Gravity.CENTER);
        Z2.setGravity(Gravity.CENTER);
        Z3.setGravity(Gravity.CENTER);
        K2.setGravity(Gravity.CENTER);
        OO1.setGravity(Gravity.CENTER);
        OO2.setGravity(Gravity.CENTER);
        OO3.setGravity(Gravity.CENTER);
        OO4.setGravity(Gravity.CENTER);

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
        Z1.setTextColor(Color.BLACK);
        Z2.setTextColor(Color.BLACK);
        Z3.setTextColor(Color.BLACK);
        K2.setTextColor(Color.BLACK);
        OO1.setTextColor(Color.BLACK);
        OO2.setTextColor(Color.BLACK);
        OO3.setTextColor(Color.BLACK);
        OO4.setTextColor(Color.BLACK);

        row1.addView(A1);
        row1.addView(B1);
        row1.addView(C1);
        row1.addView(D1);
        row1.addView(N1);
        row1.addView(E1);
        row1.addView(F1);
        row1.addView(Z1);
        row1.addView(Z2);
        row1.addView(Z3);
        row1.addView(G1);
        row1.addView(H1);
        row1.addView(I1);
        row1.addView(J1);
        row1.addView(K1);
        row1.addView(K2);
        row1.addView(L1);
        row1.addView(M1);
        row1.addView(OO1);
        row1.addView(OO2);
        row1.addView(OO3);
        row1.addView(OO4);
        row1.addView(O1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#999999"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject jobject = jarray.getJSONObject(i);

                String Sno = jobject.getString("Sno");

                String UserCustCode = jobject.getString("UserCustCode");
                String CustomerName = jobject.getString("CustomerName");
                String MobileNo = jobject.getString("MobileNo");
                String PlanName = jobject.getString("PlanName");
                String ProductName = jobject.getString("ProductName");

                String PlanAmount = jobject.getString("PlanAmount");
                String BankName = jobject.getString("BankName");
                String BankAcNo = jobject.getString("BankAcNo");


                String InvAmount = (jobject.getString("InvAmount"));

                String DueDate = jobject.getString("DispDueDate");
                String NextDueDate = jobject.getString("DispNextDueDate");

                String NetIncome = jobject.getString("NetIncome");
                String PaidDate = jobject.getString("DispPaidDate");
                String PayMode = jobject.getString("PayMode");
                String TransNo = jobject.getString("TransNo");
                String InstNo = jobject.getString("InstNo");

                String Status = jobject.getString("PaidStatus");
                String PayByBank = jobject.getString("PayByBank");
                String ChqNo = jobject.getString("ChqNo");
                String ChqDate = jobject.getString("ChqDate");
                String Narration = jobject.getString("Narration");

                final String ReceiptNo = jobject.getString("ReceiptNo");

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
                TextView X1 = new TextView(this);
                TextView X2 = new TextView(this);
                TextView X3 = new TextView(this);
                TextView G = new TextView(this);
                TextView H = new TextView(this);
                TextView I = new TextView(this);
                TextView J = new TextView(this);
                TextView K = new TextView(this);
                TextView K21 = new TextView(this);
                TextView L = new TextView(this);
                TextView M = new TextView(this);
                TextView N = new TextView(this);
                TextView OOO1 = new TextView(this);
                TextView OOO2 = new TextView(this);
                TextView OOO3 = new TextView(this);
                TextView OOO4 = new TextView(this);
                final TextView O = new TextView(this);

                O.setTag(ReceiptNo);

                StringBuilder sb = new StringBuilder(PlanName);
                StringBuilder sbname = new StringBuilder(CustomerName);
                StringBuilder sbProductName = new StringBuilder(ProductName);

                int ii = 0;
                while ((ii = sb.indexOf(" ", ii + 8)) != -1) {
                    sb.replace(ii, ii + 1, "\n");
                }

                ii = 0;
                while ((ii = sbname.indexOf(" ", ii + 8)) != -1) {
                    sbname.replace(ii, ii + 1, "\n");
                }

                ii = 0;
                while ((ii = sbProductName.indexOf(" ", ii + 8)) != -1) {
                    sbProductName.replace(ii, ii + 1, "\n");
                }

                A.setText(Sno);
                B.setText(UserCustCode);
                C.setText(WordUtils.capitalizeFully(sbname.toString()));
                D.setText(MobileNo);
                N.setText(TransNo);
                E.setText(sb.toString());
                F.setText(sbProductName.toString());
                X1.setText(PlanAmount);
                X2.setText(BankName);
                X3.setText(BankAcNo);
                G.setText(InvAmount);
                H.setText(DueDate);
                I.setText(NextDueDate);
                J.setText(InstNo);
                K.setText(NetIncome);
                K21.setText(Status);
                L.setText(PaidDate);
                M.setText(PayMode);
                OOO1.setText(PayByBank);
                OOO2.setText(ChqNo);
                OOO3.setText(ChqDate);
                OOO4.setText(Narration);
                O.setText("Print");


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
                X1.setPadding(px, px, px, px);
                X2.setPadding(px, px, px, px);
                X3.setPadding(px, px, px, px);
                K21.setPadding(px, px, px, px);
                OOO1.setPadding(px, px, px, px);
                OOO2.setPadding(px, px, px, px);
                OOO3.setPadding(px, px, px, px);
                OOO4.setPadding(px, px, px, px);

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
                X1.setTypeface(typeface);
                X2.setTypeface(typeface);
                X3.setTypeface(typeface);
                K21.setTypeface(typeface);
                OOO1.setTypeface(typeface);
                OOO2.setTypeface(typeface);
                OOO3.setTypeface(typeface);
                OOO4.setTypeface(typeface);

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
                X1.setGravity(Gravity.CENTER);
                X2.setGravity(Gravity.CENTER);
                X3.setGravity(Gravity.CENTER);
                K21.setGravity(Gravity.CENTER);
                OOO1.setGravity(Gravity.CENTER);
                OOO2.setGravity(Gravity.CENTER);
                OOO3.setGravity(Gravity.CENTER);
                OOO4.setGravity(Gravity.CENTER);

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
                O.setTextColor(Color.BLUE);


                row.addView(A);
                row.addView(B);
                row.addView(C);
                row.addView(D);
                row.addView(N);
                row.addView(E);
                row.addView(F);
                row.addView(X1);
                row.addView(X2);
                row.addView(X3);
                row.addView(G);
                row.addView(H);
                row.addView(I);
                row.addView(J);
                row.addView(K);
                row.addView(K21);
                row.addView(L);
                row.addView(M);
                row.addView(OOO1);
                row.addView(OOO2);
                row.addView(OOO3);
                row.addView(OOO4);
                row.addView(O);

                O.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ReceiptNo = O.getTag().toString();
                        startActivity(new Intent(Payment_receipt_report_activity.this, Receipt_Activity.class).putExtra("ReceiptNo", ReceiptNo));
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
            AppUtils.showExceptionDialog(Payment_receipt_report_activity.this);
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

    private void executeAgentRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Payment_receipt_report_activity.this);
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
                    postParameters.add(new BasicNameValuePair("GroupID", Groupid));
                    postParameters.add(new BasicNameValuePair("BranchCode", BranchCode));

                    response = AppUtils.callWebServiceWithMultiParam(Payment_receipt_report_activity.this, postParameters, QueryUtils.methodMaster_FillAgent, TAG);
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
                            getAgentResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Payment_receipt_report_activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Payment_receipt_report_activity.this, jsonObject.getString("Message"));
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
            AppUtils.showExceptionDialog(Payment_receipt_report_activity.this);
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
            AppUtils.showExceptionDialog(Payment_receipt_report_activity.this);
        }
    }

    private void executePlanRequest() {
        if (AppUtils.isNetworkAvailable(Payment_receipt_report_activity.this))
            new AsyncTask<Void, Void, String>() {
                protected void onPreExecute() {
                    AppUtils.showProgressDialog(Payment_receipt_report_activity.this);
                }

                @Override
                protected String doInBackground(Void... params) {
                    String response = "";
                    try {

                        List<NameValuePair> postParameters = new ArrayList<>();
                        response = AppUtils.callWebServiceWithMultiParam(Payment_receipt_report_activity.this, postParameters, QueryUtils.methodMaster_FillPlan, TAG);
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
                                getPlanResult(jsonArrayData);
                            } else {
                                AppUtils.alertDialog(Payment_receipt_report_activity.this, jsonObject.getString("Message"));
                            }
                        } else {
                            AppUtils.alertDialog(Payment_receipt_report_activity.this, jsonObject.getString("Message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            AppUtils.alertDialogWithFinish(Payment_receipt_report_activity.this, "" + getResources().getString(R.string.txt_networkAlert));
    }

    private void executeProductRequest(final String PlanId) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Payment_receipt_report_activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("PlanID", "" + PlanId));
                    response = AppUtils.callWebServiceWithMultiParam(Payment_receipt_report_activity.this, postParameters, QueryUtils.methodMaster_FillPlanTramWithProduct, TAG);
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    AppUtils.dismissProgressDialog();

                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayIntroducerDetails = jsonObject.getJSONArray("IntroducerDetails");
                    JSONArray jsonArrayRankDetails = jsonObject.getJSONArray("RankDetails");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayRankDetails.length() != 0) {
                            getProductResult(jsonArrayIntroducerDetails, jsonArrayRankDetails);
                        } else {
                            AppUtils.alertDialog(Payment_receipt_report_activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Payment_receipt_report_activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPlanResult(JSONArray jsonArray) {
        try {
            planList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("PlanId", jsonObject.getString("PlanId"));
                map.put("Planname", (jsonObject.getString("Planname")));

                planList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProductResult(JSONArray jsonArrayIntroducerDetails, JSONArray jsonArrayProductDetails) {
        try {
            productList.clear();

            for (int i = 0; i < jsonArrayProductDetails.length(); i++) {
                JSONObject jsonObject = jsonArrayProductDetails.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("ProductCode", jsonObject.getString("ProductCode"));
                map.put("Productname", WordUtils.capitalizeFully(jsonObject.getString("Productname")));
                productList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}