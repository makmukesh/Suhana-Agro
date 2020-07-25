package com.vpipl.suhanaagro.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.vpipl.suhanaagro.R;
import com.vpipl.suhanaagro.Utils.AppUtils;

import java.util.ArrayList;

public class ImageSliderViewPagerHomeStaticAdapter extends PagerAdapter
{
    Context context;
    LayoutInflater inflater;
    ArrayList<Drawable> imageSlider;

    public ImageSliderViewPagerHomeStaticAdapter(Context con, ArrayList<Drawable> datalist)
    {
        this.context = con;
        this.imageSlider = datalist;
    }

    @Override
    public int getCount() {
        return imageSlider.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageView swipeImageView;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.home_swipeimage_layout, container, false);
        swipeImageView = (ImageView) itemView.findViewById(R.id.swipeImageView);

        try {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    swipeImageView.setImageDrawable(imageSlider.get(position));
                }
            };
            new Handler().postDelayed(runnable, 100);

            container.addView(itemView);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}