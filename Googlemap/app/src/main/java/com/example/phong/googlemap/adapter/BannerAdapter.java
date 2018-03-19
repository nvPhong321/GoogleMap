package com.example.phong.googlemap.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.phong.googlemap.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phong on 2/2/2018.
 */

public class BannerAdapter extends PagerAdapter {

    private List<String> imageList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;

    public BannerAdapter(Context context, ArrayList<String> list) {
        mContext = context;
        imageList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        View myImageLayout = inflater.inflate(R.layout.item_viewpaper_banner, view, false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.imv_viewpaper_banner);
        //myImage.setImageResource(imageList.get(position).getPhoto());
        Glide.with(mContext).load(imageList.get(position)).into(myImage);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
