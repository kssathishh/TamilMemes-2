package funny.tamil.gag.memes;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.session.MediaSession;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.ortiz.touchview.TouchImageView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
String video_link,video_or_image;
SpinKitView spin_kit;

Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

       url_list = getIntent().getStringArrayListExtra("URLList");
       position = getIntent().getIntExtra("position",0);
        video_link = getIntent().getStringExtra("video_link")+"";
        video_or_image = getIntent().getStringExtra("video_or_image")+"";



        Log.i("tag12-swipe-imgurl",url_list.get(position));
        Log.i("tag12-swipe-video",video_link);
        Log.i("tag12-swipe-type",video_or_image);

        //byte[] byteArray = getIntent().getByteArrayExtra("img_bitmap");
       // bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        final ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);

        spin_kit = findViewById(R.id.spin_kit_swipe_activity);
        VideoView videoPlayer;
        videoPlayer = (VideoView) findViewById(R.id.videoView);

        MediaController mediaController = new MediaController(this);



if(video_or_image.contains("image")) {
     mViewPager.setAdapter(new TouchImageAdapter(url_list, position));


}
else if (video_or_image.contains("video"))
        {



            videoPlayer.setVisibility(View.VISIBLE);
            videoPlayer.setVideoPath(video_link);
            videoPlayer.setMediaController(mediaController);

            mediaController.setAnchorView(videoPlayer);

            videoPlayer.start();
            videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    spin_kit.setVisibility(View.GONE);
                }
            });
        }


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
                        File file1 = new File(Environment.getExternalStorageDirectory()+"/Tamil GAG/");
                        file1.mkdirs();
                        if(video_or_image.contains("image"))
                             share(mViewPager);
                        else if (video_or_image.contains("video"))
                             new Share_Video_Async(findViewById(R.id.swipe_ll)).execute(video_link,getExternalCacheDir() + "video_cache.mp4");

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

            File file = new File(this.getExternalCacheDir(), "cache.png");
               mViewPager.buildDrawingCache();
                Bitmap bmap = mViewPager.getDrawingCache();

                   FileOutputStream stream = new FileOutputStream(file);
                    bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    uri = Uri.fromFile(file);
            Log.d("tag13", "share bmp height"+  bmap.getHeight());




        } catch (Exception e) {
        Log.d(TAG, "Exception  " + e.getMessage());


    }
        Log.d("tag13", "share URI"+uri);

               Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivity(intent);


}

    class Share_Video_Async extends AsyncTask<String,Integer,String> {
        Snackbar snackbar;
        private View rootView;
        ProgressBar item = new ProgressBar(SwipeActivity.this,null,android.R.attr.progressBarStyleSmallTitle);


        public Share_Video_Async(View rootView) {

 this.rootView =rootView;
    }

        @Override
        protected String doInBackground(String... strings) {

            File file_video = new File(strings[1]);

            snackbar = Snackbar.make(rootView, "Preparing to Share Video...", Snackbar.LENGTH_INDEFINITE);
            ViewGroup contentLay = (ViewGroup) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
            item.setIndeterminate(true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            item.setLayoutParams(params);

            contentLay.addView(item);

            snackbar.show();
            return downloadFile(strings[0],file_video);
        }



        @Override
        protected void onPostExecute(String s) {
            Log.d("tag14", "share video"+s);
            snackbar.dismiss();

            shareVideo("Share Video...",s);
            super.onPostExecute(s);
        }
    }
    public void shareVideo(final String title, String path) {

        MediaScannerConnection.scanFile(SwipeActivity.this, new String[] { path },

                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_TITLE, title);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        SwipeActivity.this.startActivity(Intent.createChooser(shareIntent, "Share Video..."));

                    }
                });
    }

    private static String downloadFile(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();

            return outputFile.getPath();
        } catch(FileNotFoundException e) {
            return null; // swallow a 404
        } catch (IOException e) {
            return null; // swallow a 404
        }
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


/*
    File file = new File(Environment.getExternalStorageDirectory() + "/Tamil GAG/", "swipeactivity.jpg");
            file.mkdirs();
    if (file.exists()) {
    try{
        Log.i("taggg6", file.getPath() + "Exists");
        img.setImageURI(Uri.fromFile(file));
        container.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }catch (Exception e){e.printStackTrace();}
    }

    else
    {*/
        Log.i("taggg6", url_list.get(position)+ "-Not Exists");

        Glide.with(SwipeActivity.this)
                .load(url_list.get(position))
                .listener(new RequestListener<String, GlideDrawable>(){

                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        spin_kit.setVisibility(View.GONE);
                        return false;
                    }
                })
                .placeholder(R.drawable.transparentbg)
                .error(R.drawable.notfound)
                .crossFade()
                .into(img);
        container.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    //}




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
