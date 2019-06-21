package funny.tamil.gag.memes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.ortiz.touchview.TouchImageView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import funny.tamil.gag.memes.adapter.ExtendedViewPager;



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


        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);

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
                String fileName =  getExternalCacheDir()+"/cache.jpg";
                new DownloadFileFromURL().execute(url_list.get(position),"share",fileName);
            }
        });



    }

    class DownloadFileFromURL extends AsyncTask<String, String, String[]> {

        private ProgressDialog dialog;
        public DownloadFileFromURL() {
            dialog = new ProgressDialog(SwipeActivity.this);

        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Preparing...");
            dialog.show();
            super.onPreExecute();

        }


        @Override
        protected String[] doInBackground(String... f_url) {
            int count;
            String fileName = f_url[2];

            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();



                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);



                // Output stream
                OutputStream output = new FileOutputStream(fileName);

                byte data[] = new byte[1024];


                while ((count = input.read(data)) != -1) {

                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }


            return f_url;
        }




        @Override
        protected void onPostExecute(String[] file_url) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(file_url[1].contains("save")) {

                Toast.makeText(SwipeActivity.this, "Saved at : " + file_url[2], Toast.LENGTH_LONG).show();
            }

            else if (file_url[1].contains("share")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file_url[2]));
                startActivity(Intent.createChooser(sharingIntent, "Share Memes"));
            }

        }

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

if(position+swipePosition < url_list.size()) {






    Glide.with(SwipeActivity.this)
            .load(url_list.get(position + swipePosition))
            .placeholder(R.drawable.transparentbg)
            .error(R.drawable.notfound)
            .crossFade()
            .into(img);
}
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
