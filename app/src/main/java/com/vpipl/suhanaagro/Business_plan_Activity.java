package com.vpipl.suhanaagro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.suhanaagro.Utils.AppUtils;
import com.vpipl.suhanaagro.Utils.SPUtils;


/**
 * Created by PC14 on 3/21/2016.
 */
public class Business_plan_Activity extends AppCompatActivity {
    String TAG = "Business_plan_Activity";
    WebView webView_viewGenealogy;

    String URL = "";

    ImageView img_nav_back, img_login_logout;

    public void SetupToolbar() {

      //  img_nav_back = findViewById(R.id.img_nav_back);
        img_login_logout = findViewById(R.id.img_login_logout);

        final Drawable upArrow;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            upArrow = getDrawable(R.drawable.abc_ic_ab_back_img);
        } else
            upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_img);
        assert upArrow != null;

        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        img_nav_back.setImageDrawable(upArrow);

        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        img_login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(Business_plan_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(Business_plan_Activity.this);
            }
        });

        /*if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_logout_white));
        else
            img_login_logout.setImageDrawable(getResources().getDrawable(R.drawable.icon_distributor_login_white));
*/
        TextView textheading = findViewById(R.id.textheading);
        textheading.setText("Sales and Marketing Plan");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);


            FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

            webView_viewGenealogy = findViewById(R.id.webView_productinfo);

            webView_viewGenealogy.getSettings().setJavaScriptEnabled(true);
            webView_viewGenealogy.getSettings().setBuiltInZoomControls(true);

            webView_viewGenealogy.getSettings().setDisplayZoomControls(false);
            webView_viewGenealogy.getSettings().setSupportZoom(true);

            webView_viewGenealogy.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
            webView_viewGenealogy.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            webView_viewGenealogy.getSettings().setAllowFileAccess(true);
            webView_viewGenealogy.getSettings().setAppCacheEnabled(true);
            webView_viewGenealogy.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

            webView_viewGenealogy.getSettings().setLoadWithOverviewMode(true);
            webView_viewGenealogy.getSettings().setUseWideViewPort(true);

            webView_viewGenealogy.setWebViewClient(new WebViewClient()
            {
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    AppUtils.showProgressDialog(Business_plan_Activity.this);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    AppUtils.dismissProgressDialog();
                }
            });

            if (AppUtils.isNetworkAvailable(Business_plan_Activity.this)) {
                URL = getIntent().getStringExtra("URL");
                webView_viewGenealogy.loadUrl(URL);
            } else {
                AppUtils.alertDialogWithFinish(Business_plan_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Suhana Fmcg : Invoice\n\n" + URL;
//                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ascend Product : " + Name);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share using..."));

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Business_plan_Activity.this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView_viewGenealogy.canGoBack()) {
                        webView_viewGenealogy.goBack();
                    } else {
                        finish();
                        ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
            AppUtils.showExceptionDialog(Business_plan_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Business_plan_Activity.this);
        }

        System.gc();
    }
}