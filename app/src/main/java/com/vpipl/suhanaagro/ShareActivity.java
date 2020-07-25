package com.vpipl.suhanaagro;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.suhanaagro.Utils.AppUtils;

public class ShareActivity extends AppCompatActivity {

    String URL = "https://goo.gl/KsDBtf";

    String sAux = "Hi!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        sAux = "Hi! I am enjoying my services with "+getResources().getString(R.string.app_name)+"\n\n";
        sAux = sAux +"Download the app \n"+URL;

        AppUtils.setActionbarTitle(getSupportActionBar(), this);

        final LinearLayout rootview = (LinearLayout) findViewById(R.id.rootview) ;

        ImageView img_copy = (ImageView) findViewById(R.id.img_copy);

        ImageView img_whatsapp = (ImageView) findViewById(R.id.img_whatsapp);
        ImageView img_facebook = (ImageView) findViewById(R.id.img_facebook);
        ImageView img_mail = (ImageView) findViewById(R.id.img_mail);
        ImageView img_more = (ImageView) findViewById(R.id.img_more);

        TextView txt_whatsapp = (TextView) findViewById(R.id.txt_whatsapp);
        TextView txt_fb = (TextView) findViewById(R.id.txt_fb);
        TextView txt_mail = (TextView) findViewById(R.id.txt_mail);
        TextView txt_more = (TextView) findViewById(R.id.txt_more);

        img_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Refer URL","https://goo.gl/KsDBtf");
                clipboard.setPrimaryClip(clip);

                Snackbar.make(rootview,"Refer Link Copied to Clipboard", Snackbar.LENGTH_SHORT).show();
            }
        });

        img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareall();
            }
        });

        txt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareall();
            }
        });

        img_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharewhatsapp();
            }
        });

        txt_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharewhatsapp();
            }
        });

    img_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharefacebook();
            }
        });

        txt_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharefacebook();
            }
        });

   img_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        txt_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }


    public void sharewhatsapp()
    {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, sAux);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }

    }

    public void sharefacebook() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.facebook.katana");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, sAux);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Facebook is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareall()
    {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, ""+getResources().getString(R.string.app_name));
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Share Via"));
        } catch(Exception e) {

        }
    }

    protected void sendEmail() {

        String[] TO = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, ""+getResources().getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, sAux);
        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no Email Client Installed.", Toast.LENGTH_SHORT).show();
        }
    }
}


