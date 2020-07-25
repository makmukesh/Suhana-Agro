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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
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

public class Register_agent_Activity extends AppCompatActivity {

    private static final String TAG = "Register_agent_Activity";

    private String customercode, app_no, name, codenumber, FatherName, address, pincode, city, district, mobile_number, Introducer_Code, Introducer_Name, Rank, nominee_name, nominee_age, nominee_relation, bank_ifsc, bank_name, bank_account_number, bank_branch_name, pan_number, password;

    String CityID, RankID, DistrictID, BankId;

    private Button btn_submit;
    private Button btn_cancel;

    private Calendar myCalendar;
    private String[] districtArray;

    private String[] rankArray;
    private String[] bankArray;

    private SimpleDateFormat sdf;

    private TelephonyManager telephonyManager;

    private TextInputEditText edtxt_applicationNumber, edtxt_firstname, edtxt_CodeNumber, edtxt_father_name, txt_select_DOB, edtxt_address, edtxt_pinCode, edtxt_city, txt_district, edtxt_mobile, edtxt_IntroducerCode, edtxt_IntroducerName, txt_select_rank,
            txt_nominee_dob, edtxt_nomineeRelation, edtxt_nomineeName, edtxt_bankIfsc, edtxt_bankBranch, edtxt_bankAcntNumber, edtxt_PANNumber, txt_bankname, edtxt_password, edtxt_customercode;

    private DatePickerDialog datePickerDialog;
    public ArrayList<HashMap<String, String>> cityList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> districtList = new ArrayList<>();
    public ArrayList<HashMap<String, String>> rankList = new ArrayList<>();

    public DrawerLayout drawer;
    public NavigationView navigationView;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number;
    private String PCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_agent);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppUtils.setActionbarTitle(getSupportActionBar(), Register_agent_Activity.this);

        edtxt_customercode = (TextInputEditText) findViewById(R.id.edtxt_customercode);
        edtxt_applicationNumber = (TextInputEditText) findViewById(R.id.edtxt_applicationNumber);
        edtxt_firstname = (TextInputEditText) findViewById(R.id.edtxt_firstname);
        edtxt_CodeNumber = (TextInputEditText) findViewById(R.id.edtxt_CodeNumber);
        edtxt_father_name = (TextInputEditText) findViewById(R.id.edtxt_father_name);
        txt_select_DOB = (TextInputEditText) findViewById(R.id.txt_select_DOB);
        edtxt_address = (TextInputEditText) findViewById(R.id.edtxt_address);
        edtxt_pinCode = (TextInputEditText) findViewById(R.id.edtxt_pinCode);
        edtxt_city = (TextInputEditText) findViewById(R.id.edtxt_city);
        txt_district = (TextInputEditText) findViewById(R.id.txt_district);
        edtxt_mobile = (TextInputEditText) findViewById(R.id.edtxt_mobile);
        edtxt_IntroducerCode = (TextInputEditText) findViewById(R.id.edtxt_IntroducerCode);
        edtxt_IntroducerName = (TextInputEditText) findViewById(R.id.edtxt_IntroducerName);
        txt_select_rank = (TextInputEditText) findViewById(R.id.txt_select_rank);
        txt_nominee_dob = (TextInputEditText) findViewById(R.id.txt_nominee_dob);
        edtxt_nomineeRelation = (TextInputEditText) findViewById(R.id.edtxt_nomineeRelation);
        edtxt_nomineeName = (TextInputEditText) findViewById(R.id.edtxt_nomineeName);
        edtxt_bankIfsc = (TextInputEditText) findViewById(R.id.edtxt_bankIfsc);
        edtxt_bankAcntNumber = (TextInputEditText) findViewById(R.id.edtxt_bankAcntNumber);
        edtxt_bankBranch = (TextInputEditText) findViewById(R.id.edtxt_bankBranch);
        edtxt_PANNumber = (TextInputEditText) findViewById(R.id.edtxt_PANNumber);
        txt_bankname = (TextInputEditText) findViewById(R.id.txt_bankname);
        edtxt_password = (TextInputEditText) findViewById(R.id.edtxt_password);

        myCalendar = Calendar.getInstance();

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        sdf = new SimpleDateFormat("dd MMM yyyy");
        txt_select_DOB.setText(sdf.format(myCalendar.getTime()));

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 84);
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_agent_Activity.this, view);
                ValidateData();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.hideKeyboardOnClick(Register_agent_Activity.this, v);
                finish();
            }
        });

        txt_select_DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdatePicker();
                txt_select_DOB.clearFocus();
            }
        });

        edtxt_city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (cityList.size() > 0) {
                        showCityDialog();
                    } else
                        executeCityRequest();

                    edtxt_city.clearFocus();
                    edtxt_city.setError(null);
                }
            }
        });

        txt_select_rank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (rankList.size() != 0) {
                        showRankDialog();
                    }

                    txt_select_rank.clearFocus();
                    txt_select_rank.setError(null);
                }
            }
        });

        edtxt_IntroducerCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                executetoCheckIntroducerCode(edtxt_IntroducerCode.getText().toString());
            }
        });

        edtxt_customercode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                executetoCheckCustomerCode(edtxt_customercode.getText().toString());
            }
        });

        txt_bankname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (AppController.bankList.size() != 0) {
                        showBankDialog();
                        txt_bankname.clearFocus();
                    } else {
                        executeBankRequest();
                    }
                }
            }
        });

        if (AppUtils.isNetworkAvailable(this)) {
            executeCityRequest();
            executeDistrictRequest();
        } else
            AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));


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
            String PCode = getIntent().getStringExtra("PCode");
            executeToGetAgentDetail(PCode);
            txt_heading.setText("Edit Agent Detail");
        }

//        txt_district.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) {
//                    if (AppController.stateList.size() != 0) {
//                        showDistrictDialog();
//                        txt_district.clearFocus();
//                    } else {
//                        executeDistrictRequest();
//                    }
//                }
//            }
//        });
    }

    private void executeToGetAgentDetail(final String PCode) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_agent_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("PCode", PCode));
                    response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodGetAAgentDetail, TAG);
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
                            setAgentDetails(jsonArrayData.getJSONObject(0));

                        } else {
                            AppUtils.alertDialog(Register_agent_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_agent_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void setAgentDetails(JSONObject jsonObject) {
        try {
            findViewById(R.id.LL_button).setVisibility(View.VISIBLE);

            customercode = jsonObject.getString("LedgerId");
            PCode = jsonObject.getString("PCode");

            executetoGet_CustomerCodeForAgentEdit(jsonObject.getString("LeaderCode"));

            edtxt_firstname.setText(jsonObject.getString("PartyName"));
            edtxt_address.setText(jsonObject.getString("Address1"));
            edtxt_father_name.setText(jsonObject.getString("FName"));
            edtxt_nomineeName.setText(jsonObject.getString("Nominee"));
            edtxt_nomineeRelation.setText(jsonObject.getString("NomineeRelation"));
            txt_nominee_dob.setText(jsonObject.getString("NomineeAge"));
            edtxt_mobile.setText(jsonObject.getString("MobileNo"));
            edtxt_IntroducerName.setText(jsonObject.getString("IntroName"));

            executetoGet_LoginIDForAgent(jsonObject.getString("IntroCode"));

            edtxt_applicationNumber.setText(jsonObject.getString("AppNo"));
            edtxt_pinCode.setText(jsonObject.getString("PinCode"));
            edtxt_PANNumber.setText(jsonObject.getString("PanNo"));
            edtxt_bankAcntNumber.setText(jsonObject.getString("BankAcNo"));
            txt_bankname.setText(jsonObject.getString("BankName"));
            txt_select_rank.setText(jsonObject.getString("RankName"));

            RankID = jsonObject.getString("RankId");

            txt_select_DOB.setText(AppUtils.getDateFromAPIDate(jsonObject.getString("DOB")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showBankDialog() {
        try {
            bankArray = new String[AppController.bankList.size()];
            for (int i = 0; i < AppController.bankList.size(); i++) {
                bankArray[i] = AppController.bankList.get(i).get("Bank");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setItems(bankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    txt_bankname.setText(bankArray[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
    }

    private void executeBankRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_agent_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodMaster_FillBank, TAG);
                } catch (Exception ignored) {
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    AppUtils.dismissProgressDialog();

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getBankResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(Register_agent_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_agent_Activity.this, jsonObject.getString("Message"));
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
                map.put("Bank", WordUtils.capitalizeFully(jsonObject.getString("BankName")));

                AppController.bankList.add(map);
            }

            showBankDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(Register_agent_Activity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//        calendar.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    private void executeDistrictRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_agent_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodMaster_FillDistrictDetailForCustomer, TAG);
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
                            AppUtils.alertDialog(Register_agent_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_agent_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ValidateData() {

        app_no = edtxt_applicationNumber.getText().toString();
        name = edtxt_firstname.getText().toString();
        codenumber = edtxt_CodeNumber.getText().toString();

        FatherName = edtxt_father_name.getText().toString();
        address = edtxt_address.getText().toString();
        pincode = edtxt_pinCode.getText().toString();
        city = edtxt_city.getText().toString();
        district = txt_district.getText().toString();
        mobile_number = edtxt_mobile.getText().toString();
        Introducer_Code = edtxt_IntroducerCode.getText().toString();
        Introducer_Name = edtxt_IntroducerName.getText().toString();
        Rank = txt_select_rank.getText().toString();

        nominee_name = edtxt_nomineeName.getText().toString();
        nominee_age = txt_nominee_dob.getText().toString();
        nominee_relation = edtxt_nomineeRelation.getText().toString();
        bank_ifsc = edtxt_bankIfsc.getText().toString();
        bank_name = txt_bankname.getText().toString();
        bank_account_number = edtxt_bankAcntNumber.getText().toString();
        bank_branch_name = edtxt_bankBranch.getText().toString();
        pan_number = edtxt_PANNumber.getText().toString();
        password = edtxt_password.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(customercode)) {
            edtxt_customercode.setError("Please Enter Customer Code");
            edtxt_customercode.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(app_no)) {
            edtxt_applicationNumber.setError("Please Enter Application Form No");
            edtxt_applicationNumber.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(name)) {
            edtxt_firstname.setError("Please Enter Agent Name.");
            edtxt_firstname.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(codenumber)) {
            edtxt_CodeNumber.setError("Please Enter Agent Code No.");
            edtxt_CodeNumber.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(address)) {
            edtxt_address.setError(getResources().getString(R.string.error_et_mr_address));
            edtxt_address.requestFocus();
            cancel = true;
        } else if (!pincode.trim().matches(AppUtils.mPINCodePattern)) {
            edtxt_pinCode.setError(getResources().getString(R.string.error_et_mr_PINno));
            edtxt_pinCode.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(city)) {
            edtxt_city.setError("Please Select City.");
            edtxt_city.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(district)) {
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
        } else if (TextUtils.isEmpty(Introducer_Code)) {
            edtxt_IntroducerCode.setError("Please Enter Introducer Agent Login ID");
            edtxt_IntroducerCode.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(Rank)) {
            txt_select_rank.setError("Please Select Rank");
            cancel = true;
        } else if (!TextUtils.isEmpty(pan_number) && !pan_number.matches(AppUtils.mPANPattern)) {
            edtxt_PANNumber.setError("Invalid PAN No.");
            edtxt_PANNumber.requestFocus();
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            edtxt_password.setError("Please Enter Password");
            edtxt_password.requestFocus();
            cancel = true;
        }

        if (!cancel) {
            if (AppUtils.isNetworkAvailable(Register_agent_Activity.this)) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createRegistrationRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(Register_agent_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createRegistrationRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            CityID = "0";
            for (int i = 0; i < cityList.size(); i++) {
                if (city.equals(cityList.get(i).get("CityName"))) {
                    CityID = cityList.get(i).get("CityCode");
                }
            }

            RankID = "0";
            for (int i = 0; i < rankList.size(); i++) {
                if (Rank.equals(rankList.get(i).get("RankName"))) {
                    RankID = rankList.get(i).get("RankId");
                }
            }

            DistrictID = "0";
            for (int i = 0; i < districtList.size(); i++) {
                if (district.equals(districtList.get(i).get("DistrictName"))) {
                    DistrictID = districtList.get(i).get("DistrictCode");
                }
            }

            BankId = "0";
            for (int i = 0; i < AppController.bankList.size(); i++) {
                if (bank_name.equals(AppController.bankList.get(i).get("Bank"))) {
                    BankId = AppController.bankList.get(i).get("BID");
                }
            }

            postParameters.add(new BasicNameValuePair("BranchCode", "" + AppController.getSpUserInfo().getString(SPUtils.BranchCode, "")));

            postParameters.add(new BasicNameValuePair("AgentCode", "" + codenumber));
            postParameters.add(new BasicNameValuePair("Aid", "0"));
            postParameters.add(new BasicNameValuePair("ApplicationNo", "" + app_no));
            postParameters.add(new BasicNameValuePair("CustomerCode", "" + customercode));

            postParameters.add(new BasicNameValuePair("RankID", "" + RankID));
            postParameters.add(new BasicNameValuePair("RankName", "" + Rank));

            postParameters.add(new BasicNameValuePair("AgentName", "" + name.trim()));

            postParameters.add(new BasicNameValuePair("Address", "" + address.trim()));

            postParameters.add(new BasicNameValuePair("CityID", "" + CityID));
            postParameters.add(new BasicNameValuePair("CityName", "" + city));

            postParameters.add(new BasicNameValuePair("DistrictID", "" + DistrictID));

            postParameters.add(new BasicNameValuePair("Pincode", "" + pincode));

            postParameters.add(new BasicNameValuePair("MobileNo", "" + mobile_number));
            postParameters.add(new BasicNameValuePair("IntroducerCode", "" + Introducer_Code));
            postParameters.add(new BasicNameValuePair("IntroducerName", "" + Introducer_Name));
            postParameters.add(new BasicNameValuePair("Password", "" + password));

            postParameters.add(new BasicNameValuePair("LedgerID", "" + customercode));
            postParameters.add(new BasicNameValuePair("PCode", "" + PCode));

            postParameters.add(new BasicNameValuePair("FatherName", "" + FatherName));
            postParameters.add(new BasicNameValuePair("DOB", "" + txt_select_DOB.getText().toString()));
            postParameters.add(new BasicNameValuePair("Nominee", "" + nominee_name));
            postParameters.add(new BasicNameValuePair("NomineeAge", "" + nominee_age));
            postParameters.add(new BasicNameValuePair("Nomineerelation", "" + nominee_relation));
            postParameters.add(new BasicNameValuePair("ACNo", "" + bank_account_number));

            postParameters.add(new BasicNameValuePair("BankID", "" + BankId));
            postParameters.add(new BasicNameValuePair("BankName", "" + bank_name));
            postParameters.add(new BasicNameValuePair("BankBranchID", "0"));
            postParameters.add(new BasicNameValuePair("BankBranchName", "" + bank_branch_name));
            postParameters.add(new BasicNameValuePair("IFSCCode", "" + bank_ifsc));
            postParameters.add(new BasicNameValuePair("Panno", "" + pan_number));
            postParameters.add(new BasicNameValuePair("Panno", "" + pan_number));
            postParameters.add(new BasicNameValuePair("Password", "" + password));

            executeMemberRegistrationRequest(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
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
                    startActivity(new Intent(Register_agent_Activity.this, HomeActivity_Static.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Query")) {
                    startActivity(new Intent(Register_agent_Activity.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Customer Detail")) {
                    startActivity(new Intent(Register_agent_Activity.this, Customer_List_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Print Receipt")) {
                    startActivity(new Intent(Register_agent_Activity.this, Receipt_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(Register_agent_Activity.this);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Login")) {
                    startActivity(new Intent(Register_agent_Activity.this, Login_Activity.class));
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
                    startActivity(new Intent(Register_agent_Activity.this, Register_agent_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Detail"))
                    startActivity(new Intent(Register_agent_Activity.this, Agent_List_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Registration"))
                    startActivity(new Intent(Register_agent_Activity.this, Register_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Customer Detail"))
                    startActivity(new Intent(Register_agent_Activity.this, Customer_List_activity.class));
               /* else if (ChildItemTitle.equalsIgnoreCase("Land Selling Summary Report"))
                    startActivity(new Intent(Register_agent_Activity.this, ROI_summary_report_activity.class));*/
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Down Agent Detail"))
                    startActivity(new Intent(Register_agent_Activity.this, Agent_wise_down_agent_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Customer Detail"))
                    startActivity(new Intent(Register_agent_Activity.this, Agent_wise_down_customer_activity.class));

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

         /*   List<String> LandSelling = new ArrayList<>();
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


    private void executeCityRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Register_agent_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodMaster_FillCity, TAG);
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
                            AppUtils.alertDialog(Register_agent_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Register_agent_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getCityResult(JSONArray jsonArray) {
        try {
            cityList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("CityCode", jsonObject.getString("CityCode"));
                map.put("CityName", WordUtils.capitalizeFully(jsonObject.getString("CityName")));
                map.put("DistrictCode", (jsonObject.getString("DistrictCode")));

                cityList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getRankResult(JSONArray jsonArray) {
        try {
            rankList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("RankId", jsonObject.getString("RankId"));
                map.put("RankName", WordUtils.capitalizeFully(jsonObject.getString("RankName")));

                rankList.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCityDialog() {
        try {

            final String[] cityArray = new String[cityList.size()];
            for (int i = 0; i < cityList.size(); i++) {
                cityArray[i] = cityList.get(i).get("CityName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select City");
            builder.setItems(cityArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    edtxt_city.setText(cityArray[item]);

                    String districtid = "0", Districtname = "";

                    for (int i = 0; i < cityList.size(); i++) {
                        if (cityArray[item].equals(cityList.get(i).get("CityName"))) {
                            districtid = cityList.get(i).get("DistrictCode");
                        }
                    }

                    for (int i = 0; i < districtList.size(); i++) {
                        if (districtid.equals(districtList.get(i).get("DistrictCode"))) {
                            Districtname = districtList.get(i).get("DistrictName");
                        }
                    }

                    txt_district.setText(Districtname);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
    }

    private void showRankDialog() {
        try {

            rankArray = new String[rankList.size()];
            for (int i = 0; i < rankList.size(); i++) {
                rankArray[i] = rankList.get(i).get("RankName");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Rank");
            builder.setItems(rankArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    txt_select_rank.setText(rankArray[item]);
                }
            });
            builder.create().show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
    }

    private void executeMemberRegistrationRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(Register_agent_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_agent_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodAdd_Edit_Agent, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_agent_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject object = new JSONObject(resultData);

                            if (object.getString("Status").equalsIgnoreCase("True")) {

                                AppUtils.alertDialogWithFinish(Register_agent_Activity.this, object.getString("Message"));

                            } else {
                                AppUtils.alertDialog(Register_agent_Activity.this, object.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_agent_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_agent_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
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
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void MovetoNext(Intent intent) {
        try {
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
    }

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().after(myCalendar.getTime())) {
                txt_select_DOB.setText(sdf.format(myCalendar.getTime()));
            } else {
                AppUtils.alertDialog(Register_agent_Activity.this, "Invalid Birth Date");
            }
        }
    };

    public void executetoCheckIntroducerCode(final String IntroducerCode) {
        try {
            if (AppUtils.isNetworkAvailable(Register_agent_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("IntroducerCode", "" + IntroducerCode));
                            response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodCheckIntroducerCode, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_agent_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject Jobject = new JSONObject(resultData);
                            setIntroducerName(Jobject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_agent_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
    }


    public void executetoCheckCustomerCode(final String CustomerCode) {
        try {
            if (AppUtils.isNetworkAvailable(Register_agent_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("UserPartyCode", "" + CustomerCode));
                            response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodLoad_CustomerDetailforAgent, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_agent_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject Jobject = new JSONObject(resultData);
                            setCustomerCode(Jobject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_agent_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
    }


    public void executetoGet_CustomerCodeForAgentEdit(final String LeaderCode) {
        try {
            if (AppUtils.isNetworkAvailable(Register_agent_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("LeaderCode", "" + LeaderCode));
                            response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodGet_CustomerCodeForAgentEdit, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_agent_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject Jobject = new JSONObject(resultData);

                            if (Jobject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArray = Jobject.getJSONArray("Data");
                                edtxt_customercode.setText(jsonArray.getJSONObject(0).getString("UserPartyCode"));
                            } else {
                                edtxt_customercode.setError(Jobject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_agent_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
    }

    public void executetoGet_LoginIDForAgent(final String PCode) {
        try {
            if (AppUtils.isNetworkAvailable(Register_agent_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("PCode", "" + PCode));
                            response = AppUtils.callWebServiceWithMultiParam(Register_agent_Activity.this, postParameters, QueryUtils.methodGet_LoginIDForAgent, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_agent_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject Jobject = new JSONObject(resultData);

                            if (Jobject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArray = Jobject.getJSONArray("Data");
                                edtxt_IntroducerCode.setText(jsonArray.getJSONObject(0).getString("LoginID"));
                            } else {
                                edtxt_IntroducerCode.setError(Jobject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Register_agent_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_agent_Activity.this);
        }
    }



    public void setIntroducerName(JSONObject jobject) {
        try {
            edtxt_IntroducerCode.setError(null);
            if (jobject.getString("Status").equalsIgnoreCase("True")) {
                JSONArray jsonArrayData = jobject.getJSONArray("IntroducerDetails");
                JSONArray jsonArrayRankDetails = jobject.getJSONArray("RankDetails");

                Introducer_Name = jsonArrayData.getJSONObject(0).getString("PartyName");
                edtxt_IntroducerName.setText(Introducer_Name);
                getRankResult(jsonArrayRankDetails);

            } else {
                edtxt_IntroducerCode.setError(jobject.getString("Message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCustomerCode(JSONObject jobject) {
        try {
            edtxt_customercode.setError(null);
            if (jobject.getString("Status").equalsIgnoreCase("True")) {
                findViewById(R.id.LL_button).setVisibility(View.VISIBLE);

                JSONArray jsonArrayData = jobject.getJSONArray("Data");
                JSONObject jsonObject = jsonArrayData.getJSONObject(0);

                customercode = jsonObject.getString("LedgerId");
                PCode = jsonObject.getString("PCode");

                edtxt_firstname.setText(jsonObject.getString("PartyName"));
                edtxt_address.setText(jsonObject.getString("Address1"));
                edtxt_father_name.setText(jsonObject.getString("FName"));
                edtxt_nomineeName.setText(jsonObject.getString("Nominee"));
                edtxt_nomineeRelation.setText(jsonObject.getString("NomineeRelation"));
                txt_nominee_dob.setText(jsonObject.getString("NomineeAge"));
                edtxt_mobile.setText(jsonObject.getString("MobileNo"));

                edtxt_IntroducerName.setText(jsonObject.getString("IntroName"));

                executetoGet_LoginIDForAgent(jsonObject.getString("IntroCode"));

                edtxt_applicationNumber.setText(jsonObject.getString("AppNo"));
                edtxt_pinCode.setText(jsonObject.getString("PinCode"));
                edtxt_PANNumber.setText(jsonObject.getString("PanNo"));
                edtxt_bankAcntNumber.setText(jsonObject.getString("BankAcNo"));
                txt_bankname.setText(jsonObject.getString("BankName"));
                txt_select_rank.setText(jsonObject.getString("RankName"));

                RankID = jsonObject.getString("RankId");

                txt_select_DOB.setText(AppUtils.getDateFromAPIDate(jsonObject.getString("DOB")));

            } else {

                findViewById(R.id.LL_button).setVisibility(View.GONE);
                edtxt_customercode.setError(jobject.getString("Message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}