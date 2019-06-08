package ks.tamil.gag.memes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ks.tamil.gag.memes.R;

public class ImageAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> url_list;
    int position;

    public ImageAdapter(Context context, ArrayList<String> url_list,int position){
        this.context=context;
        this.url_list = url_list;
        this.position = position;
    }
    @Override
    public int getCount() {
        return url_list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup container, int swipePosition) {
        ImageView imageView = new ImageView(context);
        int padding = 25;
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        //imageView.setImageResource(GalImages[position]);

        Glide.with(context)
                .load(url_list.get(position + swipePosition))
                .placeholder(R.drawable.hourglass)
                .error(R.drawable.notfound)
                .into(imageView);



        ((ViewPager) container).addView(imageView, 0);


        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position_destroy, Object object) {
        Log.i("tagg4",position_destroy+"-");
        ((ViewPager) container).removeView((ImageView) object);
    }


}