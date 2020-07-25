/*
 * Hewlett-Packard Company
 * All rights reserved.
 *
 * This file, its contents, concepts, methods, behavior, and operation
 * (collectively the "Software") are protected by trade secret, patent,
 * and copyright laws. The use of the Software is governed by a license
 * agreement. Disclosure of the Software to third parties, in any form,
 * in whole or in part, is expressly prohibited except as authorized by
 * the license agreement.
 */

package com.vpipl.suhanaagro.print;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;


import com.hp.mss.hpprint.model.ImagePrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.model.asset.ImageAsset;
import com.hp.mss.hpprint.util.PrintUtil;
import com.vpipl.suhanaagro.AppController;
import com.vpipl.suhanaagro.R;

import java.io.IOException;


public class TabFragmentPrintLayout extends Fragment implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, PrintUtil.PrintMetricsListener {

    public static String CONTENT_TYPE_IMAGE = "Image";
    public static String MIME_TYPE_IMAGE = "image/*";
    public static String MIME_TYPE_IMAGE_PREFIX = "image/";
    public static String TAG = "TabFragmentPrintLayout";

    String contentType;
    PrintItem.ScaleType scaleType;
    PrintAttributes.Margins margins;
    boolean showMetricsDialog;
    boolean showCustomData;
    PrintJobData printJobData;
    EditText tagText;
    EditText valueText;

    static final int PICKFILE_RESULT_CODE = 1;
    private Uri userPickedUri;

    //Example for creating a custom media size in android.
    PrintAttributes.MediaSize mediaSize5x7;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.tab_fragment_print_layout, container, false);


        RadioGroup layoutRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.layoutRadioGroup);
        layoutRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(layoutRadioGroup, layoutRadioGroup.getCheckedRadioButtonId());

        RadioGroup layoutMarginRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.layoutMarginRadioGroup);
        layoutMarginRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(layoutMarginRadioGroup, layoutMarginRadioGroup.getCheckedRadioButtonId());

        SwitchCompat metricsSwitch = (SwitchCompat) inflatedView.findViewById(R.id.metricsRadioGroup);
        metricsSwitch.setOnCheckedChangeListener(this);
        onCheckedChanged(metricsSwitch, metricsSwitch.isChecked());


        RadioGroup deviceIdRadioGroup = (RadioGroup) inflatedView.findViewById(R.id.deviceIdRadioGroup);
        deviceIdRadioGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(deviceIdRadioGroup, deviceIdRadioGroup.getCheckedRadioButtonId());

        tagText = (EditText) inflatedView.findViewById(R.id.tagEditText);
        valueText = (EditText) inflatedView.findViewById(R.id.valueEditText);
        LinearLayout customData = (LinearLayout) inflatedView.findViewById(R.id.customData);
        showCustomData = customData.getVisibility() == View.VISIBLE;


        FloatingActionButton printButton = (FloatingActionButton) inflatedView.findViewById(R.id.printBtn);
        printButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                continueButtonClicked(v);
            }
        });

        mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "5 x 7", 5000, 7000);

        contentType = CONTENT_TYPE_IMAGE;

        //Todo get URI from bitmap

        userPickedUri = AppController.URI;

        return inflatedView;
    }

    public void continueButtonClicked(View v) {
        createPrintJobData();
        PrintUtil.setPrintJobData(printJobData);
        createCustomData();
//        PrintUtil.sendPrintMetrics = showMetricsDialog;
        PrintUtil.print(getActivity());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.metricsRadioGroup:
                showMetricsDialog = isChecked;
            default:
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

        switch(checkedId) {
            case R.id.layoutCenterTop:
                scaleType = PrintItem.ScaleType.CENTER_TOP;
                break;
            case R.id.layoutCenter:
                scaleType = PrintItem.ScaleType.CENTER;
                break;
            case R.id.layoutCrop:
                scaleType = PrintItem.ScaleType.CROP;
                break;
            case R.id.layoutFit:
                scaleType = PrintItem.ScaleType.FIT;
                break;
            case R.id.layoutCenterTopLeft:
                scaleType = PrintItem.ScaleType.CENTER_TOP_LEFT;
                break;
            case R.id.layoutWithMargin:
                margins = new PrintAttributes.Margins(500, 500, 500, 500);
                break;
            case R.id.layoutWithTopMargin:
                margins = new PrintAttributes.Margins(0, 500, 0, 0);
                break;
            case R.id.layoutWithoutMargin:
                margins = new PrintAttributes.Margins(0, 0, 0, 0);
                break;

            case R.id.notEncrypted:
                PrintUtil.doNotEncryptDeviceId = true;
                break;
            case R.id.uniquePerApp:
                PrintUtil.doNotEncryptDeviceId = false;
                PrintUtil.uniqueDeviceIdPerApp = true;
                break;
            case R.id.uniquePerVendor:
                PrintUtil.doNotEncryptDeviceId = false;
                PrintUtil.uniqueDeviceIdPerApp = false;
                break;
            default:
                showMetricsDialog = true;
                scaleType = PrintItem.ScaleType.CENTER;
                contentType = "Image";
                margins = new PrintAttributes.Margins(0, 0, 0, 0);
        }
    }

    private void createPrintJobData() {

        createUserSelectedImageJobData();

        //Giving the print job a name.
        printJobData.setJobName("Example");

        //Optionally include print attributes.
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .build();
        printJobData.setPrintDialogOptions(printAttributes);

    }

    private void createUserSelectedImageJobData() {

        Bitmap userPickedBitmap;

        try {
            userPickedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), userPickedUri);
            int width = userPickedBitmap.getWidth();
            int height = userPickedBitmap.getHeight();

            // if user picked bitmap is too big, just reduce the size, so it will not chock the print plugin
            if (width * height > 5000) {
                width = width / 2;
                height = height / 2;
                userPickedBitmap = Bitmap.createScaledBitmap(userPickedBitmap, width, height, true);
            }

            DisplayMetrics mDisplayMetric = getActivity().getResources().getDisplayMetrics();
            float widthInches =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, width, mDisplayMetric);
            float heightInches =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, height, mDisplayMetric);

            ImageAsset imageAsset = new ImageAsset(getActivity(),
                    userPickedBitmap,
                    ImageAsset.MeasurementUnits.INCHES,
                    widthInches, heightInches);

            PrintItem printItem4x6 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6,margins, scaleType, imageAsset);
            PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER,margins, scaleType, imageAsset);
            PrintItem printItem5x7 = new ImagePrintItem(mediaSize5x7,margins, scaleType, imageAsset);

            printJobData = new PrintJobData(getActivity(), printItem4x6);
            printJobData.addPrintItem(printItem85x11);
            printJobData.addPrintItem(printItem5x7);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createCustomData() {
        PrintUtil.customData.clear();
        if (showCustomData)
            PrintUtil.customData.put(tagText.getText(), valueText.getText());
    }

    @Override
    public void onPrintMetricsDataPosted(PrintMetricsData printMetricsData) {
        if (showMetricsDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(printMetricsData.toMap().toString());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private String getMimeType(Uri uri) {
        Uri returnUri = uri;
        return getActivity().getContentResolver().getType(returnUri);
    }
}
