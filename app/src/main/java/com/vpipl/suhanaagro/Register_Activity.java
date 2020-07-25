package com.vpipl.suhanaagro;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class Register_Activity extends AppCompatActivity {

    private static final String TAG = "Register_Activity";

    private String app_no, customerAcNo = "0", join_date, plan, plan_term, product, mode, amount, InvestAmount, PlotSize, introducer_code, introducer_name, firstname, Mem_relation, FatherName, GuardianName, dob, address, pincode, tehsil, district, city, mobile_number, nominee_name, nominee_relation, nominee_dob, bank_ifsc, bank_name, bank_account_number, bank_branch_name, pan_number, password;

    private Button btn_submit;
    private Button btn_cancel;
    private RadioGroup rg_gender;

    private Calendar myCalendar;

    private String[] selectRelationArray;

    private String onWhichDateClick = "";

    private SimpleDateFormat sdf;

    private TelephonyManager telephonyManager;

    private TextInputEditText edtxt_app_number, edtxt_planTerm, edtxt_amount, edtxt_PlotSize, edtxt_InvestAmount, edtxt_IntroducerCode, edtxt_IntroducerName, edtxt_name, edtxt_father_name, edtxt_address, edtxt_city, edtxt_tehsil, edtxt_pinCode, edtxt_mobile, edtxt_nomineeName, edtxt_nomineeRelation, edtxt_guardian_name, txt_joining_Date, txt_selectPlan, txt_selectProduct, txt_selectMode, txt_mem_relation, txt_select_DOB, txt_District, txt_nominee_dob, edtxt_bankIfsc, edtxt_bankBranch, edtxt_bankAcntNumber, edtxt_PANNumber, txt_bankname, edtxt_Password;

    String PCode = "0", UserPartyCode = "0";

    public ArrayList<HashMap<String, String>> planList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> districtList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> cityList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> productList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> modeList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> branchList = new ArrayList<>();

    public DrawerLayout drawer;
    public NavigationView navigationView;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number;

    String PlanId = "0";
    String ProductID = "0";
    String BankID = "0";
    String BankBranchID = "0";
    String districtid = "0";
    String CityCode = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppUtils.setActionbarTitle(getSupportActionBar(), Register_Activity.this);

        selectRelationArray = getResources().getStringArray(R.array.selectPrefix);

        edtxt_app_number = (TextInputEditText) findViewById(R.id.edtxt_app_number);
        edtxt_planTerm = (TextInputEditText) findViewById(R.id.edtxt_planTerm);
        edtxt_amount = (TextInputEditText) findViewById(R.id.edtxt_amount);
        edtxt_InvestAmount = (TextInputEditText) findViewById(R.id.edtxt_InvestAmount);
        edtxt_PlotSize = (TextInputEditText) findViewById(R.id.edtxt_PlotSize);
        edtxt_IntroducerCode = (TextInputEditText) findViewById(R.id.edtxt_IntroducerCode);
        edtxt_IntroducerName = (TextInputEditText) findViewById(R.id.edtxt_IntroducerName);

        edtxt_name = (TextInputEditText) findViewById(R.id.edtxt_name);
        edtxt_address = (TextInputEditText) findViewById(R.id.edtxt_address);
        edtxt_father_name = (TextInputEditText) findViewById(R.id.edtxt_father_name);
        edtxt_guardian_name = (TextInputEditText) findViewById(R.id.edtxt_guardian_name);
        edtxt_nomineeName = (TextInputEditText) findViewById(R.id.edtxt_nomineeName);
        edtxt_nomineeRelation = (TextInputEditText) findViewById(R.id.edtxt_nomineeRelation);

        edtxt_city = (TextInputEditText) findViewById(R.id.edtxt_city);
        edtxt_pinCode = (TextInputEditText) findViewById(R.id.edtxt_pinCode);
        edtxt_tehsil = (TextInputEditText) findViewById(R.id.edtxt_tehsil);

        edtxt_mobile = (TextInputEditText) findViewById(R.id.edtxt_mobile);

        edtxt_bankIfsc = (TextInputEditText) findViewById(R.id.edtxt_bankIfsc);
        edtxt_bankAcntNumber = (TextInputEditText) findViewById(R.id.edtxt_bankAcntNumber);
        edtxt_bankBranch = (TextInputEditText) findViewById(R.id.edtxt_bankBranch);
        edtxt_PANNumber = (TextInputEditText) findViewById(R.id.edtxt_PANNumber);
        edtxt_Password = (TextInputEditText) findViewById(R.id.edtxt_Password);

        edtxt_PANNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edtxt_bankIfsc.setFilters(new InputFilter[]{new InputFilter.AllCaps()});


        txt_joining_Date = (TextInputEditText) findViewById(R.id.txt_joining_Date);
        txt_select_DOB = (TextInputEditText) findViewById(R.id.txt_select_DOB);
        txt_nominee_dob = (TextInputEditText) findViewById(R.id.txt_nominee_dob);
        txt_mem_relation = (TextInputEditText) findViewById(R.id.txt_mem_relation);

        txt_District = (TextInputEditText) findViewById(R.id.txt_District);
        txt_selectPlan = (TextInputEditText) findViewById(R.id.txt_selectPlan);
        txt_selectProduct = (TextInputEditText) findViewById(R.id.txt_selectProduct);
        txt_selectMode = (TextInputEditText) findViewById(R.id.txt_selectMode);
        txt_bankname = (TextInputEditText) findViewById(R.id.txt_bankname);

        myCalendar = Calendar.getInstance();

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        sdf = new SimpleDateFormat("dd MMM yyyy");
        txt_joining_Date.setText(sdf.format(myCalendar.getTime()));
        txt_select_DOB.setText(sdf.format(myCalendar.getTime()));

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        rg_gender = (RadioGroup) findViewById(R.id.rg_gender);


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 84);
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, view);
                ValidateData();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Register_Activity.this, v);
                finish();
            }
        });

        txt_joining_Date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    AppUtils.hideKeyboardOnClick(Register_Activity.this, view);
                    onWhichDateClick = "et_joningdate";
                    new DatePickerDialog(Register_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    txt_joining_Date.clearFocus();
                }

            }
        });

        txt_select_DOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    AppUtils.hideKeyboardOnClick(Register_Activity.this, view);
                    onWhichDateClick = "et_dob";
                    new DatePickerDialog(Register_Activity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }

            }
        });

        txt_District.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (districtList.size() != 0) {
                        showDistrictDialog();

                    } else {
                        executeDistrictRequest();

                    }
                    txt_District.clearFocus();
                    txt_District.setError(null);
                }
            }
        });


        edtxt_city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (cityList.size() != 0) {
                        showCityDialog();
                    } else
                        executeCityRequest();

                    edtxt_city.clearFocus();
                    edtxt_city.setError(null);
                }


            }
        });


        txt_selectPlan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (planList.size() != 0) {
                        showPlanDialog();
                        txt_selectPlan.clearFocus();
                        txt_selectPlan.setError(null);
                    }
                }
            }
        });

        txt_selectProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (productList.size() != 0) {
                        showProductDialog();
                        txt_selectProduct.clearFocus();
                        txt_selectProduct.setError(null);
                    }
                }
            }
        });


        txt_bankname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (AppController.bankList.size() != 0) {
                        showBankDialog();
                    } else {
                        executeBankRequest();
                    }
                    txt_bankname.clearFocus();
                    txt_bankname.setError(null);
                }
            }
        });


        edtxt_bankBranch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (branchList.size() != 0) {
                        showBankBranchDialog();
                        edtxt_bankBranch.clearFocus();
                        edtxt_bankBranch.setError(null);
                    }
                }
            }
        });


        txt_selectMode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (modeList.size() != 0) {
                        showModeDialog();
                        txt_selectMode.clearFocus();
                    } else {
                        executeModeRequest();
                    }
                    txt_selectMode.setError(null);
                }
            }
        });

        txt_mem_relation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    AppUtils.hideKeyboardOnClick(Register_Activity.this, view);
                    if (selectRelationArray.length != 0) {
                        showMemRelationDialog();
                        txt_mem_relation.clearFocus();
                    } else {
                        AppUtils.showExceptionDialog(Register_Activity.this);
                    }

                    txt_mem_relation.setError(null);
                }

            }
        });

        edtxt_IntroducerCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                edtxt_IntroducerName.setText("");
                executeToGetIntroducerforCoustomer(s.toString());

            }
        });


        if (AppUtils.isNetworkAvailable(this)) {
            executePlanRequest();
        } else
            AppUtils.alertDialogWithFinish(this, "" + getResources().getString(R.string.txt_networkAlert));

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

        TextView txt_heading = (TextView) findViewById(R.id.txt_heading);

        if (getIntent().getExtras() != null) {
            customerAcNo = getIntent().getStringExtra("AcNo");
            executeToGetCustomerDetail(customerAcNo);
            txt_heading.setText("Edit Customer Detail");
        }

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
                    startActivity(new Intent(Register_Activity.this, HomeActivity_Static.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Query")) {
                    startActivity(new Intent(Register_Activity.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Customer Detail")) {
                    startActivity(new Intent(Register_Activity.this, Customer_List_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Print Receipt")) {
                    startActivity(new Intent(Register_Activity.this, Receipt_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Register_Activity.this);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Login")) {
                    startActivity(new Intent(Register_Activity.this, Login_Activity.class));
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
                    startActivity(new Intent(Register_Activity.this, Register_agent_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Detail"))
                    startActivity(new Intent(Register_Activity.this, Agent_List_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Registration"))
                    startActivity(new Intent(Register_Activity.this, Register_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Detail"))
                    startActivity(new Intent(Register_Activity.this, Customer_List_activity.class));
               /* else if (ChildItemTitle.equalsIgnoreCase("Land Selling Summary Report"))
                    startActivity(new Intent(Register_Activity.this, ROI_summary_report_activity.class));*/
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Down Agent Detail"))
                    startActivity(new Intent(Register_Activity.this, Agent_wise_down_agent_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Customer Detail"))
                    startActivity(new Intent(Register_Activity.this, Agent_wise_down_customer_activity.class));

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

    private void showBankDialog() {
        try {

            final String[] bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    // Do something with the selection
                    txt_bankname.setText(bankArray[item]);
                    String BankID = "0";
                    for (int i = 0; i < AppController.bankList.size(); i++) {
                        if (bankArray[item].equals(AppController.bankList.get(i).get("Bank"))) {
                            BankID = AppController.bankList.get(i).get("BID");
                        }
                    }

                    executeBankBranchRequest(BankID);

                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void showBankBranchDialog() {
        try {

            final String[] bankbranchArray = new String[branchList.size()];
            for (int i = 0; i < branchList.size(); i++) {
                bankbranchArray[i] = branchList.get(i).get("BranchName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank Branch");
            builder.setItems(bankbranchArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    edtxt_bankBranch.setText(bankbranchArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void showMemRelationDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Member Relation");
            builder.setItems(selectRelationArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_mem_relation.setText(selectRelationArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void showDistrictDialog() {
        try {

            final String[] districtArray = new String[districtList.size()];
            for (int i = 0; i < districtList.size(); i++) {
                districtArray[i] = districtList.get(i).get("DistrictName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select District");
            builder.setItems(districtArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    txt_District.setText(districtArray[item]);

                    districtid = "0";
                    for (int i = 0; i < districtList.size(); i++) {
                        if (districtArray[item].equals(districtList.get(i).get("DistrictName")) && districtid.equalsIgnoreCase("0")) {
                            districtid = districtList.get(i).get("DistrictCode");
                        }
                    }

                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void showCityDialog() {
        try {

            List<String> citynmelist = new ArrayList<>();
            for (int i = 0; i < cityList.size(); i++) {
                if (districtid.equalsIgnoreCase(cityList.get(i).get("DistrictCode")))
                    citynmelist.add(cityList.get(i).get("CityName"));
            }


            final String[] outStrArray = citynmelist.toArray(new String[0]);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select City");

            builder.setItems(outStrArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    edtxt_city.setText(outStrArray[item]);
                }
            });

            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
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

                    edtxt_planTerm.setText("");
                    executeProductRequest(PlanId);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
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


                    String ProductID = "0";
                    for (int i = 0; i < productList.size(); i++) {
                        if (productArray[item].equals(productList.get(i).get("Productname"))) {
                            ProductID = productList.get(i).get("ProductCode");
                        }
                    }

                    edtxt_amount.setText("");
                    edtxt_InvestAmount.setText("");
                    edtxt_PlotSize.setText("");
                    executeProductAmountRequest(ProductID);

                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void showModeDialog() {
        try {
            final String[] modeArray = new String[modeList.size()];
            for (int i = 0; i < modeList.size(); i++) {
                modeArray[i] = modeList.get(i).get("PMode");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Mode");
            builder.setItems(modeArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_selectMode.setText(modeArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
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

                    AppUtils.dismissProgressDialog();

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeBankBranchRequest(final String BankID) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("BankID", BankID));
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillBankBranch, TAG);
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
                            getBankBranchResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankResult(JSONArray jsonArray) {
        try {
            AppController.bankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("BID", jsonObject.getString("BankId"));
                map.put("Bank", (jsonObject.getString("BankName")));

                AppController.bankList.add(map);
            }
            showBankDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBankBranchResult(JSONArray jsonArray) {
        try {

            branchList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("Branchid", jsonObject.getString("Branchid"));
                map.put("BranchName", (jsonObject.getString("BranchName")));

                branchList.add(map);
            }

            showBankBranchDialog();

        } catch (Exception e) {
            Log.e("asdfgas", "" + e);
            e.printStackTrace();
        }
    }

    private void executeToGetCustomerDetail(final String Acno) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("AccountNo", Acno));
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodGetACustomerDetail, TAG);
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

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                        if (jsonArrayData.length() != 0) {
                            setCustomerDetails(jsonArrayData.getJSONObject(0));
                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setCustomerDetails(JSONObject jsonObject) {
        try {

            PlanId = jsonObject.getString("PlanId");
            ProductID = jsonObject.getString("ProductCode");

            districtid = jsonObject.getString("DistrictCode");
            CityCode = jsonObject.getString("CityCode");

            edtxt_app_number.setText(jsonObject.getString("AppNo"));
            txt_joining_Date.setText(jsonObject.getString("JoinDate"));
            txt_selectPlan.setText(jsonObject.getString("PlanName"));
            edtxt_planTerm.setText(jsonObject.getString("PlanTerm"));

            txt_selectProduct.setText(jsonObject.getString("ProductName"));
            txt_selectMode.setText(jsonObject.getString("PlanMode"));

            edtxt_amount.setText(jsonObject.getString("PlanAmount"));
            edtxt_PlotSize.setText(jsonObject.getString("PlotSize"));
            edtxt_InvestAmount.setText(jsonObject.getString("InvestAmount"));

            executeToGet_LoginIDForCustomer(jsonObject.getString("IntroCode"));

            edtxt_IntroducerName.setText(jsonObject.getString("IntroName"));

            edtxt_name.setText(jsonObject.getString("PartyName"));

            edtxt_father_name.setText(jsonObject.getString("FName"));
            edtxt_guardian_name.setText(jsonObject.getString("Guardian"));
            txt_select_DOB.setText(jsonObject.getString("DOB"));

            edtxt_address.setText(jsonObject.getString("Address1"));
            edtxt_pinCode.setText(jsonObject.getString("PinCode"));

            edtxt_tehsil.setText(jsonObject.getString("Tehsil"));
            txt_District.setText(jsonObject.getString("DistrictName"));

            districtid = jsonObject.getString("DistrictCode");

            edtxt_city.setText(jsonObject.getString("CityName"));
            edtxt_mobile.setText(jsonObject.getString("MobileNo"));

            edtxt_nomineeName.setText(jsonObject.getString("Nominee"));
            edtxt_nomineeRelation.setText(jsonObject.getString("NomineeRelation"));
            txt_nominee_dob.setText(jsonObject.getString("NomineeAge"));

            edtxt_bankIfsc.setText(jsonObject.getString("IFSCCode"));
            txt_bankname.setText(jsonObject.getString("BankName"));

            edtxt_bankAcntNumber.setText(jsonObject.getString("BankAcNo"));

            edtxt_PANNumber.setText(jsonObject.getString("PanNo"));
            edtxt_Password.setText(jsonObject.getString("Password"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetIntroducerforCoustomer(final String Introducercode) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("Introducercode", "" + Introducercode));

                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodCheckIntroducer, TAG);
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            PCode = jsonArrayData.getJSONObject(0).getString("PCode");
                            UserPartyCode = jsonArrayData.getJSONObject(0).getString("UserPartyCode");
                            edtxt_IntroducerName.setText(jsonArrayData.getJSONObject(0).getString("PartyName"));
                        }
                    } else
                        edtxt_IntroducerCode.setError(jsonObject.getString("Message"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeToGet_LoginIDForCustomer(final String UserPartyCode) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("UserPartyCode", "" + UserPartyCode));

                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodGet_LoginIDForCustomer, TAG);
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            edtxt_IntroducerCode.setText(jsonArrayData.getJSONObject(0).getString("LoginID"));
                        }
                    } else
                        edtxt_IntroducerCode.setError(jsonObject.getString("Message"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeDistrictRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillDistrictDetailForCustomer, TAG);
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
                            getDistrictResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeCityRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillCity, TAG);
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
                            getCityResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeProductAmountRequest(final String ProductID) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("ProductID", "" + ProductID));
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_ProductDetails, TAG);
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
                            edtxt_amount.setText("" + jsonArrayData.getJSONObject(0).getString("ProductValue"));
                            edtxt_PlotSize.setText("" + jsonArrayData.getJSONObject(0).getString("PlotSize"));
                            edtxt_InvestAmount.setText("" + jsonArrayData.getJSONObject(0).getString("InvestAmount"));

                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executeModeRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillMode, TAG);
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
                            getModeResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void executePlanRequest() {
        if (AppUtils.isNetworkAvailable(Register_Activity.this))
            new AsyncTask<Void, Void, String>() {
                protected void onPreExecute() {
                    AppUtils.showProgressDialog(Register_Activity.this);
                }

                @Override
                protected String doInBackground(Void... params) {
                    String response = "";
                    try {

                        List<NameValuePair> postParameters = new ArrayList<>();
                        response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillPlan, TAG);
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
                                AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                            }
                        } else {
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            AppUtils.alertDialogWithFinish(Register_Activity.this, "" + getResources().getString(R.string.txt_networkAlert));
    }

    private void executeProductRequest(final String PlanId) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("PlanID", "" + PlanId));
                    response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodMaster_FillPlanTramWithProduct, TAG);
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
                            AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_Activity.this, jsonObject.getString("Message"));
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

            if (planList.size() > 0)
                txt_selectPlan.setText(planList.get(0).get("Planname"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProductResult(JSONArray jsonArrayIntroducerDetails, JSONArray jsonArrayRankDetails) {
        try {
            productList.clear();
            edtxt_planTerm.setText("" + jsonArrayIntroducerDetails.getJSONObject(0).getString("PlanTerm"));
            for (int i = 0; i < jsonArrayRankDetails.length(); i++) {
                JSONObject jsonObject = jsonArrayRankDetails.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("ProductCode", jsonObject.getString("ProductCode"));
                map.put("Productname", WordUtils.capitalizeFully(jsonObject.getString("Productname")));
                productList.add(map);
            }
            if (productList.size() > 0)
                txt_selectProduct.setText(productList.get(0).get("Productname"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDistrictResult(JSONArray jsonArray) {
        try {
            districtList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("DistrictCode", jsonObject.getString("DistrictCode"));
                map.put("DistrictName", WordUtils.capitalizeFully(jsonObject.getString("DistrictName")));

                districtList.add(map);
            }

            showDistrictDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCityResult(JSONArray jsonArray) {
        try {
            cityList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                //todo District code
                map.put("CityCode", jsonObject.getString("CityCode"));
                map.put("CityName", WordUtils.capitalizeFully(jsonObject.getString("CityName")));
                map.put("DistrictCode", (jsonObject.getString("DistrictCode")));

                cityList.add(map);
            }

            showCityDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getModeResult(JSONArray jsonArray) {
        try {

            modeList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("PModeId", jsonObject.getString("PModeId"));
                map.put("PMode", WordUtils.capitalizeFully(jsonObject.getString("PMode")));

                modeList.add(map);
            }

            showModeDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ValidateData() {

        app_no = edtxt_app_number.getText().toString();
        join_date = txt_joining_Date.getText().toString();
        plan = txt_selectPlan.getText().toString();
        plan_term = edtxt_planTerm.getText().toString();
        product = txt_selectProduct.getText().toString();
        mode = txt_selectMode.getText().toString();
        amount = edtxt_amount.getText().toString();
        InvestAmount = edtxt_PlotSize.getText().toString();
        PlotSize = edtxt_InvestAmount.getText().toString();
        introducer_code = edtxt_IntroducerCode.getText().toString();
        introducer_name = edtxt_IntroducerName.getText().toString();
        firstname = edtxt_name.getText().toString();
        Mem_relation = txt_mem_relation.getText().toString();
        FatherName = edtxt_father_name.getText().toString();
        GuardianName = edtxt_guardian_name.getText().toString();
        dob = txt_select_DOB.getText().toString();
        address = edtxt_address.getText().toString();
        pincode = edtxt_pinCode.getText().toString();
        tehsil = edtxt_tehsil.getText().toString();
        district = txt_District.getText().toString();
        city = edtxt_city.getText().toString();
        mobile_number = edtxt_mobile.getText().toString();
        nominee_name = edtxt_nomineeName.getText().toString();
        nominee_relation = edtxt_nomineeRelation.getText().toString();
        nominee_dob = txt_nominee_dob.getText().toString();

        bank_ifsc = edtxt_bankIfsc.getText().toString();
        bank_name = txt_bankname.getText().toString();
        bank_account_number = edtxt_bankAcntNumber.getText().toString();
        bank_branch_name = edtxt_bankBranch.getText().toString();
        pan_number = edtxt_PANNumber.getText().toString();
        password = edtxt_Password.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(app_no)) {
            edtxt_app_number.setError("Please Enter Application Form No");
            edtxt_app_number.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(join_date)) {
            txt_joining_Date.setError("Please Select Join Date");
            txt_joining_Date.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(plan)) {
            txt_selectPlan.setError("Please Select Plan");
            txt_selectPlan.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(plan_term)) {
            txt_selectPlan.setError("Please Select Plan");
            txt_selectPlan.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(product)) {
            txt_selectProduct.setError("Please Select Product");
            txt_selectProduct.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(mode)) {
            txt_selectMode.setError("Please Select Mode");
            txt_selectMode.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(amount)) {
            txt_selectProduct.setError("Please Select Product");
            txt_selectProduct.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(introducer_code)) {
            edtxt_IntroducerCode.setError("Please Enter Agent Login ID");
            edtxt_IntroducerCode.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(introducer_name)) {
            edtxt_IntroducerCode.setError("Please Enter Agent Name");
            edtxt_IntroducerCode.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(firstname)) {
            edtxt_name.setError("Please Enter Customer Name");
            edtxt_name.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(Mem_relation)) {
            txt_mem_relation.setError("Please Select Prefix");
            txt_mem_relation.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(FatherName)) {
            edtxt_father_name.setError("Please Enter Father/Husband Name");
            edtxt_father_name.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(dob)) {
            txt_select_DOB.setError("Please Select Date of Birth");
            txt_select_DOB.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(address)) {
            edtxt_address.setError(getResources().getString(R.string.error_et_mr_address));
            edtxt_address.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(pincode)) {
            edtxt_pinCode.setError("Please Enter Pin Code");
            edtxt_pinCode.requestFocus();
            cancel = true;
        } else if (!pincode.trim().matches(AppUtils.mPINCodePattern)) {
            edtxt_pinCode.setError(getResources().getString(R.string.error_et_mr_PINno));
            edtxt_pinCode.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(tehsil)) {
            edtxt_tehsil.setError("Please Enter Tehsil");
            edtxt_tehsil.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(district)) {
            txt_District.setError("Please Select District");
            txt_District.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(city)) {
            edtxt_city.setError("Please Select City.");
            edtxt_city.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(mobile_number)) {
            edtxt_mobile.setError(getResources().getString(R.string.error_required_mobile_number));
            edtxt_mobile.requestFocus();
            cancel = true;
        } else if ((mobile_number).length() != 10) {
            edtxt_mobile.setError(getResources().getString(R.string.error_invalid_mobile_number));
            edtxt_mobile.requestFocus();
            cancel = true;
        } else if (!TextUtils.isEmpty(bank_ifsc) && bank_ifsc.length() != 11) {
            edtxt_bankIfsc.setError("Invalid IFSC");
            edtxt_bankIfsc.requestFocus();
            cancel = true;
        } else if (!TextUtils.isEmpty(pan_number) && !pan_number.matches(AppUtils.mPANPattern)) {
            edtxt_PANNumber.setError("Invalid PAN Number");
            edtxt_PANNumber.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            edtxt_Password.setError("Please Enter Password");
            edtxt_Password.requestFocus();
            cancel = true;
        }

        if (!cancel) {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createRegistrationRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createRegistrationRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            for (int i = 0; i < planList.size(); i++) {
                if (plan.equals(planList.get(i).get("Planname"))) {
                    PlanId = planList.get(i).get("PlanId");
                }
            }


            for (int i = 0; i < productList.size(); i++) {
                if (product.equals(productList.get(i).get("Productname"))) {
                    ProductID = productList.get(i).get("ProductCode");
                }
            }


            for (int i = 0; i < AppController.bankList.size(); i++) {
                if (bank_name.equals(AppController.bankList.get(i).get("Bank"))) {
                    BankID = AppController.bankList.get(i).get("BID");
                }
            }


            for (int i = 0; i < branchList.size(); i++) {
                if (bank_branch_name.equals(branchList.get(i).get("BranchName"))) {
                    BankBranchID = branchList.get(i).get("Branchid");
                }
            }


            for (int i = 0; i < districtList.size(); i++) {
                if (district.equals(districtList.get(i).get("DistrictName"))) {
                    districtid = districtList.get(i).get("DistrictCode");
                }
            }


            for (int i = 0; i < cityList.size(); i++) {
                if (city.equals(cityList.get(i).get("CityName"))) {
                    CityCode = cityList.get(i).get("CityCode");
                }
            }

            int selectedId = rg_gender.getCheckedRadioButtonId();
            RadioButton rb_gender = (RadioButton) findViewById(selectedId);
            String ActiveStatus = rb_gender.getText().toString();

            if (ActiveStatus.equalsIgnoreCase("Active"))
                ActiveStatus = "Y";
            else
                ActiveStatus = "N";

            postParameters.add(new BasicNameValuePair("BranchCode", "" + AppController.getSpUserInfo().getString(SPUtils.BranchCode, "")));
            postParameters.add(new BasicNameValuePair("CustomerAccountNo", "" + customerAcNo));
            postParameters.add(new BasicNameValuePair("Appno", "" + app_no));
            postParameters.add(new BasicNameValuePair("joindate", "" + join_date));
            postParameters.add(new BasicNameValuePair("prefix", "" + Mem_relation));
            postParameters.add(new BasicNameValuePair("CustomerName", "" + firstname.trim()));
            postParameters.add(new BasicNameValuePair("DOB", "" + dob));
            postParameters.add(new BasicNameValuePair("Address", "" + address.trim()));
            postParameters.add(new BasicNameValuePair("DistrictCode", "" + districtid.trim()));
            postParameters.add(new BasicNameValuePair("CityCode", "" + CityCode.trim()));
            postParameters.add(new BasicNameValuePair("Tehsil", "" + tehsil.trim()));
            postParameters.add(new BasicNameValuePair("Pincode", "" + pincode.trim()));
            postParameters.add(new BasicNameValuePair("Phoneno", "" + mobile_number.trim()));
            postParameters.add(new BasicNameValuePair("mobileno", "" + mobile_number));
            postParameters.add(new BasicNameValuePair("Fathername", "" + FatherName));
            postParameters.add(new BasicNameValuePair("Nominee", "" + nominee_name.trim()));
            postParameters.add(new BasicNameValuePair("Nomineerelation", "" + nominee_relation.trim()));
            postParameters.add(new BasicNameValuePair("NomineeAge", "" + nominee_dob.trim()));
            postParameters.add(new BasicNameValuePair("Agentcode", "" + UserPartyCode));
            postParameters.add(new BasicNameValuePair("AgentName", "" + introducer_name));
            postParameters.add(new BasicNameValuePair("Guardianname", "" + GuardianName));
            postParameters.add(new BasicNameValuePair("PlanID", "" + PlanId));
            postParameters.add(new BasicNameValuePair("PlanName", "" + plan));
            postParameters.add(new BasicNameValuePair("Planterm", "" + plan_term));
            postParameters.add(new BasicNameValuePair("PlanMode", "" + mode));
            postParameters.add(new BasicNameValuePair("InvestAmount", "" + InvestAmount));
            postParameters.add(new BasicNameValuePair("PlotSize", "" + PlotSize));
            postParameters.add(new BasicNameValuePair("Amount", "" + amount));
            postParameters.add(new BasicNameValuePair("ProductID", "" + ProductID));
            postParameters.add(new BasicNameValuePair("ProductName", "" + product));
            postParameters.add(new BasicNameValuePair("PCode", "" + PCode));
            postParameters.add(new BasicNameValuePair("ACNo", "" + bank_account_number));
            postParameters.add(new BasicNameValuePair("BankID", "" + BankID));
            postParameters.add(new BasicNameValuePair("BankName", "" + bank_name));
            postParameters.add(new BasicNameValuePair("BankBranchID", "" + BankBranchID));
            postParameters.add(new BasicNameValuePair("BankBranchName", "" + bank_branch_name));
            postParameters.add(new BasicNameValuePair("Panno", "" + pan_number));
            postParameters.add(new BasicNameValuePair("IFSCCode", "" + bank_ifsc));
            postParameters.add(new BasicNameValuePair("Password", "" + password));
            postParameters.add(new BasicNameValuePair("ActiveStatus", "" + ActiveStatus));
            postParameters.add(new BasicNameValuePair("UserPartyCode", "" + UserPartyCode));

            executeMemberRegistrationRequest(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeMemberRegistrationRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Register_Activity.this, postParameters, QueryUtils.methodAdd_Edit_Customer, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject object = new JSONObject(resultData);

                            if (object.length() > 0) {
                                if (object.getString("Status").equalsIgnoreCase("True")) {
                                    JSONArray Data = object.getJSONArray("Data");
                                    if (Data.length() > 0)
                                        AppUtils.alertDialogWithFinish(Register_Activity.this, Data.getJSONObject(0).getString("Message"));
                                    else
                                        AppUtils.alertDialog(Register_Activity.this, object.getString("Message"));
                                } else {
                                    AppUtils.alertDialog(Register_Activity.this, object.getString("Message"));
                                }
                            } else {
                                AppUtils.showExceptionDialog(Register_Activity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
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
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void MovetoNext(Intent intent) {
        try {
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_Activity.this);
        }
    }

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {
                switch (onWhichDateClick) {

                    case "et_joningdate":
                        txt_joining_Date.setText(sdf.format(myCalendar.getTime()));
                        break;

                    case "et_dob":
                        txt_select_DOB.setText(sdf.format(myCalendar.getTime()));
                        break;

                    default:
                        txt_nominee_dob.setText(sdf.format(myCalendar.getTime()));
                        break;
                }
            } else {
                AppUtils.alertDialog(Register_Activity.this, getResources().getString(R.string.error_invalid_dob));
            }
        }
    };
}