package com.vpipl.suhanaagro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.suhanaagro.Adapters.ExpandableListAdapter;
import com.vpipl.suhanaagro.Adapters.ImageSliderViewPagerHomeStaticAdapter;
import com.vpipl.suhanaagro.Utils.AppUtils;
import com.vpipl.suhanaagro.Utils.BadgeDrawable;
import com.vpipl.suhanaagro.Utils.CirclePageIndicator;
import com.vpipl.suhanaagro.Utils.CircularImageView;
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
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity_Static extends AppCompatActivity {
    private static final String TAG = "HomeActivity_Static";

    private static DrawerLayout drawer;
    private static NavigationView navigationView;
    public ActionBarDrawerToggle drawerToggle;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    private TextView txt_welcome_name, txt_id_number , txt_dash_wallet_balance ;
    private CircularImageView profileImage;

    private ViewPager pager_slider;
    CirclePageIndicator imagePageIndicator;
    int currentPage = 0;
    Timer timer;

    static ArrayList<Drawable> imageSlider = new ArrayList<>();

    ImageView img_cart, img_user;

    public void SetupToolbar() {

        img_cart = findViewById(R.id.img_cart);
        img_user = findViewById(R.id.img_login_logout);

        img_user.setVisibility(View.VISIBLE);

        String Usertype = AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, "");

        if (Usertype.equalsIgnoreCase("3"))
            img_cart.setVisibility(View.GONE);

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(HomeActivity_Static.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(HomeActivity_Static.this);
            }
        });

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_log_out));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_user));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_static);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        SetupToolbar();
        AppUtils.setActionbarTitle(getSupportActionBar(), HomeActivity_Static.this);

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcomename = "Welcome " + WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            getSupportActionBar().setSubtitle(Html.fromHtml("<small>" + welcomename + "</small>"));
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeaderView = navigationView.getHeaderView(0);
        txt_welcome_name = (TextView) navHeaderView.findViewById(R.id.txt_welcome_name);

        txt_id_number = (TextView) navHeaderView.findViewById(R.id.txt_id_number);
        profileImage = (CircularImageView) navHeaderView.findViewById(R.id.iv_Profile_Pic);
        LinearLayout LL_Nav = (LinearLayout) navHeaderView.findViewById(R.id.LL_Nav);
        expListView = (ExpandableListView) findViewById(R.id.left_drawer);
        txt_dash_wallet_balance =  findViewById(R.id.txt_dash_wallet_balance);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        pager_slider = (ViewPager) findViewById(R.id.pager_slider);
        imagePageIndicator = (CirclePageIndicator) findViewById(R.id.imagePageIndicator);

        imageSlider.add(getResources().getDrawable(R.drawable.slide_one));
        imageSlider.add(getResources().getDrawable(R.drawable.slide_two));
        imageSlider.add(getResources().getDrawable(R.drawable.slide_three));
        imageSlider.add(getResources().getDrawable(R.drawable.slide_four));
    //    imageSlider.add(getResources().getDrawable(R.drawable.slide_five));

        setImageSlider();

     //   createWalletBalanceRequest();
    }

    public void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(HomeActivity_Static.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure !!! Do you want to Exit?"));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        finish();
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText(getResources().getString(R.string.txt_signout_no));
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageSlider() {
        try {
            pager_slider.setAdapter(new ImageSliderViewPagerHomeStaticAdapter(HomeActivity_Static.this, imageSlider));

            final float density = getResources().getDisplayMetrics().density;
            imagePageIndicator.setStrokeColor(getResources().getColor(R.color.colorAccent));
            imagePageIndicator.setFillColor(getResources().getColor(R.color.colorPrimary));
            imagePageIndicator.setStrokeWidth();
            imagePageIndicator.setRadius(4 * density);
            imagePageIndicator.setViewPager(pager_slider);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == imageSlider.size()) {
                        currentPage = 0;
                    }
                    pager_slider.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 500, 3000);

            pager_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    currentPage = position;
                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(navigationView)) {
                drawer.closeDrawer(navigationView);
            } else {
                showExitDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LoadNavigationHeaderItems();
        enableExpandableList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadNavigationHeaderItems();
        enableExpandableList();
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
                    startActivity(new Intent(HomeActivity_Static.this, HomeActivity_Static.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Wallet Report")) {
                    startActivity(new Intent(HomeActivity_Static.this, Wallet_Report_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Upgrade Report")) {
                    startActivity(new Intent(HomeActivity_Static.this, Upgrade_Report_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Query")) {
                    startActivity(new Intent(HomeActivity_Static.this, Send_Query_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Change Password")) {
                    startActivity(new Intent(HomeActivity_Static.this, Change_Password_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Customer Detail")) {
                    startActivity(new Intent(HomeActivity_Static.this, Customer_List_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Refer")) {
                    startActivity(new Intent(HomeActivity_Static.this, ShareActivity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Payment Receipt")) {
                    startActivity(new Intent(HomeActivity_Static.this, Payment_receipt_report_activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Stock Report")) {
                    startActivity(new Intent(HomeActivity_Static.this, Stock_Report_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(HomeActivity_Static.this);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Login")) {
                    startActivity(new Intent(HomeActivity_Static.this, Login_Activity.class));
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
                    startActivity(new Intent(HomeActivity_Static.this, Register_agent_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Detail"))
                    startActivity(new Intent(HomeActivity_Static.this, Agent_List_activity.class));
              /*  else if (ChildItemTitle.equalsIgnoreCase("Customer Registration"))
                    startActivity(new Intent(HomeActivity_Static.this, Register_Activity.class));*/
                else if (ChildItemTitle.equalsIgnoreCase("Customer Detail"))
                    startActivity(new Intent(HomeActivity_Static.this, Customer_List_activity.class));
              /*  else if (ChildItemTitle.equalsIgnoreCase("Land Selling Summary Report"))
                    startActivity(new Intent(HomeActivity_Static.this, ROI_summary_report_activity.class));*/
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Down Agent Detail"))
                    startActivity(new Intent(HomeActivity_Static.this, Agent_wise_down_agent_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Wise Customer Detail"))
                    startActivity(new Intent(HomeActivity_Static.this, Agent_wise_down_customer_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Agent Commission Report"))
                    startActivity(new Intent(HomeActivity_Static.this, Agent_commission_report_activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Purchase Bill Summary"))
                    startActivity(new Intent(HomeActivity_Static.this, Purchase_Summary_Report_Activity.class));
                else if (ChildItemTitle.equalsIgnoreCase("Product Wise Purchase Detail Report"))
                    startActivity(new Intent(HomeActivity_Static.this, Purchase_Detail_Report_Activity.class));

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
        //    Customer.add("Customer Registration");
            Customer.add("Customer Detail");

           /* List<String> LandSelling = new ArrayList<>();
            LandSelling.add("Land Selling Summary Report");*/

            List<String> Commission = new ArrayList<>();
            Commission.add("Agent Commission Report");

            List<String> Report = new ArrayList<>();
            Report.add("Agent Wise Down Agent Detail");
            Report.add("Agent Wise Customer Detail");

            List<String> PurchaseReport = new ArrayList<>();
            PurchaseReport.add("Purchase Bill Summary");
            PurchaseReport.add("Product Wise Purchase Detail Report");

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {

                String Usertype = AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, "");

                if (Usertype.equalsIgnoreCase("3")) {

                    listDataHeader.add("Home");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                    listDataHeader.add("Customer Detail");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                    listDataHeader.add("Purchase Report");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), PurchaseReport);

                    listDataHeader.add("Wallet Report");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                    listDataHeader.add("Upgrade Report");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                    listDataHeader.add("Stock Report");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                } else {

                    listDataHeader.add("Home");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                    listDataHeader.add("Agent");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Agent);

                    listDataHeader.add("Customer");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Customer);

                    listDataHeader.add("Report");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Report);

                    listDataHeader.add("Commission");
                    listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Commission);
                }

                /*listDataHeader.add("Land Selling");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), LandSelling);*/

                listDataHeader.add("Payment Receipt");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                listDataHeader.add("Query");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                listDataHeader.add("Change Password");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

                listDataHeader.add("Logout");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
            } else {
                listDataHeader.add("Login");
                listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);
            }

            listDataHeader.add("Refer");
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadNavigationHeaderItems() {

        txt_id_number.setText("");
        txt_id_number.setVisibility(View.GONE);

        txt_welcome_name.setText("Guest");

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
            txt_welcome_name.setText(welcome_text);
            String userid = AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "");
            txt_id_number.setText(userid);
            txt_id_number.setVisibility(View.VISIBLE);
        }

        setBadgeCount(this, (AppController.selectedProductsList.size()));

    }

    private void setBadgeCount(Context context, int count) {
        try {
            ImageView imageView = findViewById(R.id.img_cart);

            if (imageView != null) {
                LayerDrawable icon = (LayerDrawable) imageView.getDrawable();

                BadgeDrawable badge;// Reuse drawable if possible
                Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge); //getting the layer 2
                if (reuse != null && reuse instanceof BadgeDrawable) {
                    badge = (BadgeDrawable) reuse;
                } else {
                    badge = new BadgeDrawable(context);
                }
                badge.setCount("" + count);
                icon.mutate();
                icon.setDrawableByLayerId(R.id.ic_badge, badge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*  Wallet Balance  */

    private void createWalletBalanceRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            postParameters.add(new BasicNameValuePair("Acno", AppController.getSpUserInfo().getString(SPUtils.USER_AcNo, "")));
            executeWalletBalanceRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeWalletBalanceRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(HomeActivity_Static.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                 //       AppUtils.showProgressDialog(HomeActivity_Static.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(HomeActivity_Static.this, postParameters, QueryUtils.methodToGetWalletBalance, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                Double wallet_balance = 0.00 ;
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < jsonArrayData.length(); i++) {
                                    try {
                                        JSONObject jobject = jsonArrayData.getJSONObject(i);

                                        wallet_balance = wallet_balance +  Double.parseDouble(jobject.getString("Amount"));

                                   //     wallet_balance = jobject.getString("walletbalance");
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                txt_dash_wallet_balance.setText("Wallet Balance : "+wallet_balance);

                            } else {
                            //    AppUtils.alertDialog(HomeActivity_Static.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(HomeActivity_Static.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(HomeActivity_Static.this);
        }
    }
}
