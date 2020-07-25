package com.vpipl.suhanaagro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.CurrencyAmount;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

public class Customer_List_activity extends AppCompatActivity {

    String TAG = "Customer_List_activity";
    TextView txt_from_date, txt_to_date, txt_count;
    Button btn_proceed, btn_load_more, button_load_less;
    Calendar myCalendar;
    SimpleDateFormat sdf;
    String whichdate = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer_list);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            txt_from_date = (TextView) findViewById(R.id.txt_from_date);
            txt_to_date = (TextView) findViewById(R.id.txt_to_date);

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
                    createCustomerDetailsRequest();
                }
            });

            button_load_less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (PageIndex > 1) {
                        PageIndex = PageIndex - 1;
                        StartedRow = StartedRow - 25;
                        createCustomerDetailsRequest();
                    }
                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = 1;
                    StartedRow =0;
                    createCustomerDetailsRequest();
                }
            });

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
                    new DatePickerDialog(Customer_List_activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            txt_to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_to_date";
                    new DatePickerDialog(Customer_List_activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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

            if (AppUtils.isNetworkAvailable(this))
                createCustomerDetailsRequest();
            else
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
                    startActivity(new Intent(Customer_List_activity.this, HomeActivity_Static.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Query")) {
                    startActivity(new Intent(Customer_List_activity.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Customer Detail")) {
                    startActivity(new Intent(Customer_List_activity.this, Customer_List_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Print Receipt")) {
                    startActivity(new Intent(Customer_List_activity.this, Receipt_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Customer_List_activity.this);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Login")) {
                    startActivity(new Intent(Customer_List_activity.this, Login_Activity.class));
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
                    startActivity(new Intent(Customer_List_activity.this, Register_agent_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Detail"))
                    startActivity(new Intent(Customer_List_activity.this, Agent_List_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Registration"))
                    startActivity(new Intent(Customer_List_activity.this, Register_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Detail"))
                    startActivity(new Intent(Customer_List_activity.this, Customer_List_activity.class));
             /*   else if (ChildItemTitle.equalsIgnoreCase("Land Selling Summary Report"))
                    startActivity(new Intent(Customer_List_activity.this, ROI_summary_report_activity.class));*/
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Down Agent Detail"))
                    startActivity(new Intent(Customer_List_activity.this, Agent_wise_down_agent_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Customer Detail"))
                    startActivity(new Intent(Customer_List_activity.this, Agent_wise_down_customer_activity.class));

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
               /* listDataHeader.add("Land Selling");
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

    private void createCustomerDetailsRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("BranchCode", AppController.getSpUserInfo().getString(SPUtils.BranchCode, "")));
        postParameters.add(new BasicNameValuePair("PCode", AppController.getSpUserInfo().getString(SPUtils.USER_P_Code, "")));
        postParameters.add(new BasicNameValuePair("GroupId", AppController.getSpUserInfo().getString(SPUtils.USER_Group_Id, "")));
        postParameters.add(new BasicNameValuePair("Todate", "" + txt_to_date.getText().toString()));
        postParameters.add(new BasicNameValuePair("Fromdate", "" + txt_from_date.getText().toString()));

        postParameters.add(new BasicNameValuePair("ShowRowData", "" + StartedRow));


        executeCustomerDetailsRequest(postParameters);
    }

    private void executeCustomerDetailsRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Customer_List_activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Customer_List_activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Customer_List_activity.this,
                                    postparameters, QueryUtils.methodtoGetCustomerList, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Customer_List_activity.this);
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
                                        AppUtils.alertDialog(Customer_List_activity.this, "No Data Found");
                                } else {
                                    AppUtils.alertDialog(Customer_List_activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(Customer_List_activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Customer_List_activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Customer_List_activity.this);
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
        TextView A2 = new TextView(this);
        TextView A3 = new TextView(this);
        TextView A4 = new TextView(this);
        TextView A5 = new TextView(this);
        TextView A6 = new TextView(this);
        TextView A7 = new TextView(this);
        TextView A8 = new TextView(this);
        TextView A9 = new TextView(this);
        TextView A10 = new TextView(this);
        TextView A11 = new TextView(this);
        TextView A12 = new TextView(this);
        TextView A13 = new TextView(this);
        TextView A14 = new TextView(this);
        TextView A15 = new TextView(this);
        TextView A16 = new TextView(this);
        TextView A17 = new TextView(this);
        TextView A18 = new TextView(this);
        TextView A19 = new TextView(this);
        TextView A20 = new TextView(this);
        TextView A21 = new TextView(this);
        TextView A22 = new TextView(this);
        TextView A23 = new TextView(this);
        TextView A24 = new TextView(this);
        TextView A25 = new TextView(this);
        TextView A26 = new TextView(this);
        TextView A27 = new TextView(this);
        TextView A28 = new TextView(this);
        TextView A29 = new TextView(this);
        TextView A30 = new TextView(this);
        TextView A31 = new TextView(this);
        TextView A32 = new TextView(this);
        TextView AA1 = new TextView(this);

        A1.setText("S.No.");
        A2.setText("App. Form No.");
        A3.setText("Joining Date");
        A4.setText("Plan Name");
        A5.setText("Plan Term");
        A6.setText("Product Name");
        A7.setText("Plan Mode");
        A8.setText("Amount");
        A9.setText("Agent Code");
        A10.setText("Agent Name");
        A11.setText("Coustomer Code");
        A12.setText("Coustomer Name");
        A13.setText("Father / Husband Name");
        A14.setText("Address");
        A15.setText("Pin Code");
        A16.setText("Tehsil");
        A17.setText("City");
        A18.setText("District");
        A19.setText("Mobile No.");
        A20.setText("Phone No.");
        A21.setText("Date of Birth");
        A22.setText("Nominee");
        A23.setText("Nominee Relation");
        A24.setText("Nominee Age");
        A25.setText("Guardian");
        A26.setText("Bank");
        A27.setText("Branch");
        A28.setText("Account No.");
        A29.setText("PAN No.");
        A30.setText("Status");
        AA1.setText(" ");

        A1.setPadding(px, px, px, px);
        A2.setPadding(px, px, px, px);
        A3.setPadding(px, px, px, px);
        A4.setPadding(px, px, px, px);
        A5.setPadding(px, px, px, px);
        A6.setPadding(px, px, px, px);
        A7.setPadding(px, px, px, px);
        A8.setPadding(px, px, px, px);
        A9.setPadding(px, px, px, px);
        A10.setPadding(px, px, px, px);
        A11.setPadding(px, px, px, px);
        A12.setPadding(px, px, px, px);
        A13.setPadding(px, px, px, px);
        A14.setPadding(px, px, px, px);
        A15.setPadding(px, px, px, px);
        A16.setPadding(px, px, px, px);
        A17.setPadding(px, px, px, px);
        A18.setPadding(px, px, px, px);
        A19.setPadding(px, px, px, px);
        A20.setPadding(px, px, px, px);
        A21.setPadding(px, px, px, px);
        A22.setPadding(px, px, px, px);
        A23.setPadding(px, px, px, px);
        A24.setPadding(px, px, px, px);
        A25.setPadding(px, px, px, px);
        A26.setPadding(px, px, px, px);
        A27.setPadding(px, px, px, px);
        A28.setPadding(px, px, px, px);
        A29.setPadding(px, px, px, px);
        A30.setPadding(px, px, px, px);
        AA1.setPadding(px, px, px, px);

        A1.setTypeface(typeface);
        A2.setTypeface(typeface);
        A3.setTypeface(typeface);
        A4.setTypeface(typeface);
        A5.setTypeface(typeface);
        A6.setTypeface(typeface);
        A7.setTypeface(typeface);
        A8.setTypeface(typeface);
        A9.setTypeface(typeface);
        A10.setTypeface(typeface);
        A11.setTypeface(typeface);
        A12.setTypeface(typeface);
        A13.setTypeface(typeface);
        A14.setTypeface(typeface);
        A15.setTypeface(typeface);
        A16.setTypeface(typeface);
        A17.setTypeface(typeface);
        A18.setTypeface(typeface);
        A19.setTypeface(typeface);
        A20.setTypeface(typeface);
        A21.setTypeface(typeface);
        A22.setTypeface(typeface);
        A23.setTypeface(typeface);
        A24.setTypeface(typeface);
        A25.setTypeface(typeface);
        A26.setTypeface(typeface);
        A27.setTypeface(typeface);
        A28.setTypeface(typeface);
        A29.setTypeface(typeface);
        A30.setTypeface(typeface);
        AA1.setTypeface(typeface);

        A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A6.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A7.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A8.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A9.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A10.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A11.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A12.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A13.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A14.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A15.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A16.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A17.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A18.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A19.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A20.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A21.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A22.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A23.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A24.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A25.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A26.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A27.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A28.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A29.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        A30.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        AA1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        A1.setGravity(Gravity.CENTER);
        A2.setGravity(Gravity.CENTER);
        A3.setGravity(Gravity.CENTER);
        A4.setGravity(Gravity.CENTER);
        A5.setGravity(Gravity.CENTER);
        A6.setGravity(Gravity.CENTER);
        A7.setGravity(Gravity.CENTER);
        A8.setGravity(Gravity.CENTER);
        A9.setGravity(Gravity.CENTER);
        A10.setGravity(Gravity.CENTER);
        A11.setGravity(Gravity.CENTER);
        A12.setGravity(Gravity.CENTER);
        A13.setGravity(Gravity.CENTER);
        A14.setGravity(Gravity.CENTER);
        A15.setGravity(Gravity.CENTER);
        A16.setGravity(Gravity.CENTER);
        A17.setGravity(Gravity.CENTER);
        A18.setGravity(Gravity.CENTER);
        A19.setGravity(Gravity.CENTER);
        A20.setGravity(Gravity.CENTER);
        A21.setGravity(Gravity.CENTER);
        A22.setGravity(Gravity.CENTER);
        A23.setGravity(Gravity.CENTER);
        A24.setGravity(Gravity.CENTER);
        A25.setGravity(Gravity.CENTER);
        A26.setGravity(Gravity.CENTER);
        A27.setGravity(Gravity.CENTER);
        A28.setGravity(Gravity.CENTER);
        A29.setGravity(Gravity.CENTER);
        A30.setGravity(Gravity.CENTER);
        AA1.setGravity(Gravity.CENTER);

        A1.setTextColor(Color.BLACK);
        A2.setTextColor(Color.BLACK);
        A3.setTextColor(Color.BLACK);
        A4.setTextColor(Color.BLACK);
        A5.setTextColor(Color.BLACK);
        A6.setTextColor(Color.BLACK);
        A7.setTextColor(Color.BLACK);
        A8.setTextColor(Color.BLACK);
        A9.setTextColor(Color.BLACK);
        A10.setTextColor(Color.BLACK);
        A11.setTextColor(Color.BLACK);
        A12.setTextColor(Color.BLACK);
        A13.setTextColor(Color.BLACK);
        A14.setTextColor(Color.BLACK);
        A15.setTextColor(Color.BLACK);
        A16.setTextColor(Color.BLACK);
        A17.setTextColor(Color.BLACK);
        A18.setTextColor(Color.BLACK);
        A19.setTextColor(Color.BLACK);
        A20.setTextColor(Color.BLACK);
        A21.setTextColor(Color.BLACK);
        A22.setTextColor(Color.BLACK);
        A23.setTextColor(Color.BLACK);
        A24.setTextColor(Color.BLACK);
        A25.setTextColor(Color.BLACK);
        A26.setTextColor(Color.BLACK);
        A27.setTextColor(Color.BLACK);
        A28.setTextColor(Color.BLACK);
        A29.setTextColor(Color.BLACK);
        A30.setTextColor(Color.BLACK);
        AA1.setTextColor(Color.BLACK);

        row1.addView(A1);
        row1.addView(A2);
        row1.addView(A3);
        row1.addView(A4);
        row1.addView(A5);
        row1.addView(A6);
        row1.addView(A7);
        row1.addView(A8);
        row1.addView(A9);
        row1.addView(A10);
        row1.addView(A11);
        row1.addView(A12);
        row1.addView(A13);
        row1.addView(A14);
        row1.addView(A15);
        row1.addView(A16);
        row1.addView(A17);
        row1.addView(A18);
        row1.addView(A19);
        row1.addView(A20);
        row1.addView(A21);
        row1.addView(A22);
        row1.addView(A23);
        row1.addView(A24);
        row1.addView(A25);
        row1.addView(A26);
        row1.addView(A27);
        row1.addView(A28);
        row1.addView(A29);
        row1.addView(A30);

        row1.addView(AA1);

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.parseColor("#999999"));

        ll.addView(row1);
        ll.addView(view);

        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject jobject = jarray.getJSONObject(i);

                int AcNo = jobject.getInt("AcNo");

                String AppNo = jobject.getString("AppNo");
                String Sno = jobject.getString("Sno");

                String JoiningDate = jobject.getString("JoinDate");
                String IntroCode = (jobject.getString("IntroCode"));

                String IntroName = (jobject.getString("IntroName"));
                String PlanTerm = jobject.getString("PlanTerm");

                String PlanMode = jobject.getString("PlanMode");
                String PlanAmount = jobject.getString("PlanAmount");

                String ProductName = jobject.getString("ProductName");

                String CustomerName = jobject.getString("PartyName");
                String MobileNo = jobject.getString("MobileNo");

                String District = jobject.getString("DistrictName");
                String NomineeName = jobject.getString("Nominee");

                String BankName = jobject.getString("BankName");
                String BranchName = jobject.getString("CustBranchName");
                String AccountNo = jobject.getString("AcNo");
                String Status = jobject.getString("Status");

                String FatherHusbandName = jobject.getString("FName");
                String Address = jobject.getString("Address1");
                String City = jobject.getString("CityName");
                String PinCode = jobject.getString("PinCode");

                String Tehsil = jobject.getString("Tehsil");
                String DOB = jobject.getString("DOB");
                String NomineeRelation = jobject.getString("NomineeRelation");

                String GuardianName = jobject.getString("Guardian");
                String PhoneNo = jobject.getString("PhoneNo");

                String PanNo = jobject.getString("PanNo");

                String UserID = jobject.getString("LoginId");
                String Password = jobject.getString("Password");
                String PlanName = jobject.getString("PlanName");
                String UserPartyCode = jobject.getString("UserPartyCode");
                String PartyName = jobject.getString("PartyName");
                String NomineeAge = jobject.getString("NomineeAge");

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                row.setBackgroundColor(Color.WHITE);

                TextView B1 = new TextView(this);
                TextView B2 = new TextView(this);
                TextView B3 = new TextView(this);
                TextView B4 = new TextView(this);
                TextView B5 = new TextView(this);
                TextView B6 = new TextView(this);
                TextView B7 = new TextView(this);
                TextView B8 = new TextView(this);
                TextView B9 = new TextView(this);
                TextView B10 = new TextView(this);
                TextView B11 = new TextView(this);
                TextView B12 = new TextView(this);
                TextView B13 = new TextView(this);
                TextView B14 = new TextView(this);
                TextView B15 = new TextView(this);
                TextView B16 = new TextView(this);
                TextView B17 = new TextView(this);
                TextView B18 = new TextView(this);
                TextView B19 = new TextView(this);
                TextView B20 = new TextView(this);
                TextView B21 = new TextView(this);
                TextView B22 = new TextView(this);
                TextView B23 = new TextView(this);
                TextView B24 = new TextView(this);
                TextView B25 = new TextView(this);
                TextView B26 = new TextView(this);
                TextView B27 = new TextView(this);
                TextView B28 = new TextView(this);
                TextView B29 = new TextView(this);
                TextView B30 = new TextView(this);


                final TextView AA = new TextView(this);

                AA.setId(AcNo);

                StringBuilder sb = new StringBuilder(FatherHusbandName);
                StringBuilder sbname = new StringBuilder(CustomerName);
                StringBuilder sbIntroName = new StringBuilder(IntroName);

                int ii = 0;
                while ((ii = sb.indexOf(" ", ii + 8)) != -1) {
                    sb.replace(ii, ii + 1, "\n");
                }

                ii = 0;
                while ((ii = sbname.indexOf(" ", ii + 8)) != -1) {
                    sbname.replace(ii, ii + 1, "\n");
                }

                ii = 0;
                while ((ii = sbIntroName.indexOf(" ", ii + 8)) != -1) {
                    sbIntroName.replace(ii, ii + 1, "\n");
                }

/*                B25.setText(WordUtils.capitalizeFully(sbIntroName.toString()));
                B26.setText(WordUtils.capitalizeFully(sbname.toString()));
                B27.setText(WordUtils.capitalizeFully(sb.toString()));*/

                B1.setText(Sno);
                B2.setText(AppNo);
                B3.setText(JoiningDate);
                B4.setText(PlanName);
                B5.setText(PlanTerm);
                B6.setText(ProductName);
                B7.setText(PlanMode);
                B8.setText(PlanAmount);
                B9.setText(IntroCode);
                B10.setText(IntroName);
                B11.setText(UserPartyCode);
                B12.setText(PartyName);
                B13.setText(FatherHusbandName);
                B14.setText(Address);
                B15.setText(PinCode);
                B16.setText(Tehsil);
                B17.setText(City);
                B18.setText(District);
                B19.setText(MobileNo);
                B20.setText(PhoneNo);
                B21.setText(DOB);
                B22.setText(NomineeName);
                B23.setText(NomineeRelation);
                B24.setText(NomineeAge);
                B25.setText(GuardianName);
                B26.setText(BankName);
                B27.setText(BranchName);
                B28.setText(AccountNo);
                B29.setText(PanNo);
                B30.setText(Status);
                AA.setText("View More/ Edit");

                B1.setPadding(px, px, px, px);
                B2.setPadding(px, px, px, px);
                B3.setPadding(px, px, px, px);
                B4.setPadding(px, px, px, px);
                B5.setPadding(px, px, px, px);
                B6.setPadding(px, px, px, px);
                B7.setPadding(px, px, px, px);
                B8.setPadding(px, px, px, px);
                B9.setPadding(px, px, px, px);
                B10.setPadding(px, px, px, px);
                B11.setPadding(px, px, px, px);
                B12.setPadding(px, px, px, px);
                B13.setPadding(px, px, px, px);
                B14.setPadding(px, px, px, px);
                B15.setPadding(px, px, px, px);
                B16.setPadding(px, px, px, px);
                B17.setPadding(px, px, px, px);
                B18.setPadding(px, px, px, px);
                B19.setPadding(px, px, px, px);
                B20.setPadding(px, px, px, px);
                B21.setPadding(px, px, px, px);
                B22.setPadding(px, px, px, px);
                B23.setPadding(px, px, px, px);
                B24.setPadding(px, px, px, px);
                B25.setPadding(px, px, px, px);
                B26.setPadding(px, px, px, px);
                B27.setPadding(px, px, px, px);
                B28.setPadding(px, px, px, px);
                B29.setPadding(px, px, px, px);
                B30.setPadding(px, px, px, px);
                AA.setPadding(px, px, px, px);

                B1.setTypeface(typeface);
                B2.setTypeface(typeface);
                B3.setTypeface(typeface);
                B4.setTypeface(typeface);
                B5.setTypeface(typeface);
                B6.setTypeface(typeface);
                B7.setTypeface(typeface);
                B8.setTypeface(typeface);
                B9.setTypeface(typeface);
                B10.setTypeface(typeface);
                B11.setTypeface(typeface);
                B12.setTypeface(typeface);
                B13.setTypeface(typeface);
                B14.setTypeface(typeface);
                B15.setTypeface(typeface);
                B16.setTypeface(typeface);
                B17.setTypeface(typeface);
                B18.setTypeface(typeface);
                B19.setTypeface(typeface);
                B20.setTypeface(typeface);
                B21.setTypeface(typeface);
                B22.setTypeface(typeface);
                B23.setTypeface(typeface);
                B24.setTypeface(typeface);
                B25.setTypeface(typeface);
                B26.setTypeface(typeface);
                B27.setTypeface(typeface);
                B28.setTypeface(typeface);
                B29.setTypeface(typeface);
                B30.setTypeface(typeface);

                AA.setTypeface(typeface);

                AA.setTextColor(Color.BLUE);

                B1.setGravity(Gravity.CENTER);
                B2.setGravity(Gravity.CENTER);
                B3.setGravity(Gravity.CENTER);
                B4.setGravity(Gravity.CENTER);
                B5.setGravity(Gravity.CENTER);
                B6.setGravity(Gravity.CENTER);
                B7.setGravity(Gravity.CENTER);
                B8.setGravity(Gravity.CENTER);
                B9.setGravity(Gravity.CENTER);
                B10.setGravity(Gravity.CENTER);
                B11.setGravity(Gravity.CENTER);
                B12.setGravity(Gravity.CENTER);
                B13.setGravity(Gravity.CENTER);
                B14.setGravity(Gravity.CENTER);
                B15.setGravity(Gravity.CENTER);
                B16.setGravity(Gravity.CENTER);
                B17.setGravity(Gravity.CENTER);
                B18.setGravity(Gravity.CENTER);
                B19.setGravity(Gravity.CENTER);
                B20.setGravity(Gravity.CENTER);
                B21.setGravity(Gravity.CENTER);
                B22.setGravity(Gravity.CENTER);
                B23.setGravity(Gravity.CENTER);
                B24.setGravity(Gravity.CENTER);
                B25.setGravity(Gravity.CENTER);
                B26.setGravity(Gravity.CENTER);
                B27.setGravity(Gravity.CENTER);
                B28.setGravity(Gravity.CENTER);
                B29.setGravity(Gravity.CENTER);
                B30.setGravity(Gravity.CENTER);

                AA.setGravity(Gravity.CENTER);

                row.addView(B1);
                row.addView(B2);
                row.addView(B3);
                row.addView(B4);
                row.addView(B5);
                row.addView(B6);
                row.addView(B7);
                row.addView(B8);
                row.addView(B9);
                row.addView(B10);
                row.addView(B11);
                row.addView(B12);
                row.addView(B13);
                row.addView(B14);
                row.addView(B15);
                row.addView(B16);
                row.addView(B17);
                row.addView(B18);
                row.addView(B19);
                row.addView(B20);
                row.addView(B21);
                row.addView(B22);
                row.addView(B23);
                row.addView(B24);
                row.addView(B25);
                row.addView(B26);
                row.addView(B27);
                row.addView(B28);
                row.addView(B29);
                row.addView(B30);
                row.addView(AA);

                AA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Customer_List_activity.this, Register_Activity.class).putExtra("AcNo", "" + AA.getId()));
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
            AppUtils.showExceptionDialog(Customer_List_activity.this);
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
                AppUtils.alertDialog(Customer_List_activity.this, "Selected Date Can't be After today");
            }
        }
    };
}
