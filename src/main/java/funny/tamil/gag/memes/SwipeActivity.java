package funny.tamil.gag.memes;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ortiz.touchview.TouchImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import funny.tamil.gag.memes.adapter.ExtendedViewPager;


import static androidx.constraintlayout.widget.Constraints.TAG;


public class SwipeActivity extends AppCompatActivity {
ArrayList<String> url_list;
int position;
Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

       url_list = getIntent().getStringArrayListExtra("URLList");
       position = getIntent().getIntExtra("position",0);

        //byte[] byteArray = getIntent().getByteArrayExtra("img_bitmap");
       // bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);




        final ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);

        mViewPager.setAdapter(new TouchImageAdapter(url_list,position));


        ImageButton ib_close = findViewById(R.id.imageButton_close);
        ib_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton ib_share = findViewById(R.id.imageButton_share);
        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        share(mViewPager);
                    }
                });
                t.start();
            }
        });


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    104);
        }

    }

    public void share(ExtendedViewPager mViewPager)
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Uri uri = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory()+"/Tamil GAG/", "swipeactivity.jpg");

            if(!file.exists()) {
                file.mkdirs();
                mViewPager.buildDrawingCache();
                Bitmap bmap = mViewPager.getDrawingCache();

                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    uri = Uri.fromFile(file);
                } catch (IOException e) {
                    Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
                }
            }
            uri = Uri.fromFile(file);


        } catch (Exception e) {
        Log.d(TAG, "Exception  " + e.getMessage());
    }

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivity(intent);


}


    class TouchImageAdapter extends PagerAdapter {

        ArrayList<String> url_list;
         int position;

         public TouchImageAdapter(ArrayList<String> url_list, int position) {
             this.url_list = url_list;
             this.position = position;
         }

         @Override
        public int getCount() {
             return 1;
        }

        @Override
        public View instantiateItem(ViewGroup container, int swipePosition) {
            TouchImageView img = new TouchImageView(container.getContext());



    File file = new File(Environment.getExternalStorageDirectory() + "/Tamil GAG/", "swipeactivity.jpg");

    if (file.exists()) {
    try{
        Log.i("taggg6", file.getPath() + "Exists");
        img.setImageURI(Uri.fromFile(file));
        container.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }catch (Exception e){e.printStackTrace();}
    }

    else
    {
        Log.i("taggg6", url_list.get(position)+ "-Not Exists");

        Glide.with(SwipeActivity.this)
                .load(url_list.get(position))
                .placeholder(R.drawable.transparentbg)
                .error(R.drawable.notfound)
                .crossFade()
                .into(img);
        container.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }




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
