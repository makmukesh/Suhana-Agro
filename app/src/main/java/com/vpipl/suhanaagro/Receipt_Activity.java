package com.vpipl.suhanaagro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.suhanaagro.Utils.AppUtils;
import com.vpipl.suhanaagro.Utils.QueryUtils;
import com.vpipl.suhanaagro.Utils.SPUtils;
import com.vpipl.suhanaagro.print.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Receipt_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        executeReceiptRequest();

        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button.setVisibility(View.GONE);
                handleclick();
            }
        });
    }

    public void handleclick() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout rootlayout = (LinearLayout) inflater.inflate(R.layout.activity_receipt, null); //RelativeLayout is root view of my UI(xml) file.
        rootlayout.setDrawingCacheEnabled(true);

        Bitmap screen = getBitmapFromView(this.getWindow().findViewById(R.id.scrollView));

//        AppController.URI = getImageUri(screen);

        try {
            String path = Environment.getExternalStorageDirectory() + "/Android/Suhana_Receipts/";

            File root = new File(path);
            if (!root.exists()) {
                root.mkdirs();
            }

            String filename = "Img_" + AppController.getSpUserInfo().getString(SPUtils.USER_P_Code, "") + ".png";

            File f = new File(path + filename);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();


            FileOutputStream stream = new FileOutputStream(f);
            screen.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

            AppController.URI = Uri.fromFile(f);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        Toast.makeText(this, "Receipt Exported Successfully", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(Receipt_Activity.this, MainActivity.class));
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmapFromView(View view) {

//        view.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
//        view.measure(LinearLayout.MeasureSpec.makeMeasureSpec(view.getLayoutParams().width, LinearLayout.MeasureSpec.EXACTLY), LinearLayout.MeasureSpec.makeMeasureSpec(view.getLayoutParams().height, LinearLayout.MeasureSpec.EXACTLY));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)

            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);

        view.draw(canvas);
        return returnedBitmap;
    }


    private void executeReceiptRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(Receipt_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {

                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("ReceiptNo", getIntent().getStringExtra("ReceiptNo")));
                    response = AppUtils.callWebServiceWithMultiParam(Receipt_Activity.this, postParameters, QueryUtils.methodGet_Print_Reciept, "Receipt_Activity");
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
                        if (jsonArrayData.length() > 0) {
                            setData(jsonArrayData.getJSONObject(0));

                        } else {
                            AppUtils.alertDialog(Receipt_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(Receipt_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setData(JSONObject Object) {

        try {
            TextView txt_name = (TextView) findViewById(R.id.txt_name);
            TextView txt_receipt = (TextView) findViewById(R.id.txt_receipt);
            TextView txt_date = (TextView) findViewById(R.id.txt_date);
            TextView txt_address = (TextView) findViewById(R.id.txt_address);
            TextView tst_amount = (TextView) findViewById(R.id.tst_amount);
            TextView tst_cheque = (TextView) findViewById(R.id.tst_cheque);

            txt_receipt.setText(Object.getString("ReceiptNo"));
            txt_name.setText(Object.getString("CustomerName"));
            txt_date.setText(Object.getString("DispPaidDate"));
            tst_amount.setText(Object.getString("PlanAmount"));
            tst_amount.setText("\u20B9 "+Object.getString("PlanAmount"));
            tst_cheque.setText(Object.getString("ChqNo"));

//        txt_address.setText(AppController.getSpUserInfo().getString(SPUtils.USER_ADDRESS,""));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
