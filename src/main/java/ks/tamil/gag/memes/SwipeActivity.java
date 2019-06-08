package ks.tamil.gag.memes;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

import java.util.ArrayList;

import ks.tamil.gag.memes.adapter.ImageAdapter;

public class SwipeActivity extends AppCompatActivity {
ArrayList<String> url_list;
int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

       url_list = getIntent().getStringArrayListExtra("URLList");
       position = getIntent().getIntExtra("position",0);



        Log.i("tagg3",url_list.get(0));

       /* ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImageAdapter adapter = new ImageAdapter(this,url_list,position);
        viewPager.setAdapter(adapter);*/



        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
//        setContentView(mViewPager);
        mViewPager.setAdapter(new TouchImageAdapter(url_list,position));


    }
     class TouchImageAdapter extends PagerAdapter {

        private  int[] images = { R.drawable.savage, R.drawable.vivek, R.drawable.santhanam };
         ArrayList<String> url_list;
         int position;

         public TouchImageAdapter(ArrayList<String> url_list, int position) {
             this.url_list = url_list;
             this.position = position;
         }

         @Override
        public int getCount() {
            return url_list.size()-position;
        }

        @Override
        public View instantiateItem(ViewGroup container, int swipePosition) {
            TouchImageView img = new TouchImageView(container.getContext());

if(position+swipePosition < url_list.size())
            Glide.with(SwipeActivity.this)
                    .load(url_list.get(position + swipePosition))
                    .placeholder(R.drawable.hourglass)
                    .error(R.drawable.notfound)
                    .crossFade()
                    .into(img);

            //img.setImageResource(images[position]);

            container.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
    }
