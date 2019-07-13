package funny.tamil.gag.memes.adapter;


import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.reflect.TypeToken;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import funny.tamil.gag.memes.MainActivity;
import funny.tamil.gag.memes.R;
import funny.tamil.gag.memes.SwipeActivity;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class GridViewAdapter extends BaseAdapter {

    public ArrayList<String> allItemsUrl;
    public ArrayList<String> allDesc;
    public ArrayList<String> all_category;
    public ArrayList<String> all_timestamp;
    public ArrayList<Integer> all_upvotes;
    public ArrayList<Integer> all_downvotes;
    public ArrayList<String> all_Document_Reference;
    public ArrayList<QueryDocumentSnapshot> all_document;

    public ArrayList<String> list_like;
    public ArrayList<String> list_dislike;


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static final String FAVORITES = "funny.tamil.gag.memes.favorites";
    public static final String LIKE = "like";
    public static final String DISLIKE = "dislike";

    AlertDialog alert;
    private LayoutInflater inflater;
    Context context;
    private FirebaseAnalytics mFirebaseAnalytics;



    public GridViewAdapter(Context context, ArrayList<String> images, ArrayList<String> desc, ArrayList<String> category, ArrayList<String> timestamp, ArrayList<Integer> upvotes, ArrayList<Integer> downvotes, ArrayList<String> document_Reference, ArrayList<QueryDocumentSnapshot> documents) {
        inflater = LayoutInflater.from(context);
        this.context = context;
//
        allItemsUrl = images;
        allDesc = desc;
        all_category = category;
        all_timestamp = timestamp;
        all_upvotes = upvotes;
        all_downvotes = downvotes;
        all_Document_Reference = document_Reference;
        all_document = documents;

        list_like = getArrayList(LIKE);
        if (list_like == null) {
            list_like = new ArrayList<String>();
            list_like.add("0");
        }
        list_dislike = getArrayList(DISLIKE);
        if (list_dislike == null) {
            list_dislike = new ArrayList<String>();
            list_dislike.add("0");
        }


        Log.w("tagg4-getarraylist", list_dislike.size() + "");

        Log.w("tag2", images.size() + "");

        Log.d("RecyclerSnapAdapter", "Create Image RecyclerSnapAdapter " + allItemsUrl.size());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

    }

    GridViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return allItemsUrl.size();
    }

    @Override
    public Object getItem(int position) {
        return allItemsUrl.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;

        View view = convertView;



           Log.i("tag13","position - img -  "+position);

         view = convertView;

        Log.w("tag13", position + " NULL");


        if (view == null) {
            view = inflater.inflate(R.layout.image_inflater, parent, false);
            holder = new ViewHolder();
            assert view != null;
            holder.imageView = view.findViewById(R.id.ivImageInflator);
            holder.text_desc = view.findViewById(R.id.textView_desc);
            holder.imageButton_category = view.findViewById(R.id.imageView_category);
            holder.text_category = view.findViewById(R.id.tv_category);
            holder.text_timestamp = view.findViewById(R.id.tv_timestamp);
            holder.btn_upvote = view.findViewById(R.id.btn_like);
            holder.btn_downvote = view.findViewById(R.id.btn_dislike);
            holder.btn_share = view.findViewById(R.id.btn_share);
            holder.btn_save = view.findViewById(R.id.btn_download);
            holder.ib_popup_menu = view.findViewById(R.id.imageButton_popup_menu);
            holder.cardView = view.findViewById(R.id.cardview);
            holder.ad_native = view.findViewById(R.id.adView_native);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        if (!(allItemsUrl.size() <= position)) {
            if (allItemsUrl.get(position) !=null) {
                Log.w("tag13", position + "Not NULL");


                holder.cardView.setVisibility(View.VISIBLE);
                holder.ad_native.setVisibility(View.GONE);

                Glide.with(context)
                        .load(allItemsUrl.get(position))
                        .dontAnimate()
                        .placeholder(R.drawable.transparentbg)
                        .error(R.drawable.reloadtransparent)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .thumbnail(0.2f)
                        .into(holder.imageView);


                holder.imageButton_category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.text_category.performClick();
                    }
                });

                holder.text_category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle params = new Bundle();
                        params.putString("activity", "Gridviewadapter-click");
                        params.putString("button_name", "category_text");
                        mFirebaseAnalytics.logEvent("category_text_click", params);

                        String tv_string = holder.text_category.getText().toString();

                        String category = "";

                        if (tv_string.contains(","))
                            category = tv_string.substring(0, tv_string.indexOf(",")).trim();
                        else if (tv_string.contains("Trending"))
                            category = "Home";
                        else
                            category = tv_string.trim();
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).open_drawer(category);
                        }

                    }
                });


                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle params = new Bundle();
                        params.putString("activity", "Gridviewadapter-click");
                        params.putString("button_name", "image_click");
                        mFirebaseAnalytics.logEvent("image_click", params);


                        new open_swipeActivity(holder).execute(position);


                        Log.i("tag12-iv-imgurl", allItemsUrl.get(position));
                        Log.i("tag12-iv-video", all_document.get(position).get("video_link").toString());
                        Log.i("tag12-iv-type", all_document.get(position).get("type").toString());


                    }
                });


                holder.text_desc.setText(allDesc.get(position));


                String category = all_category.get(position).replace('[', ' ').replace(']', ' ');
                if (category.length() < 3)
                    category = "Trending";

                holder.text_category.setText(category);

                holder.imageButton_category.setImageResource(getCategoryDrawable(category));

                Log.i("tagg4 - getview ", "*******" + list_dislike.size() + "*****");

                if (list_like.contains(all_Document_Reference.get(position))) {
                    holder.btn_upvote.setTextOn(all_upvotes.get(position) + "");
                    holder.btn_upvote.setTextOff(all_upvotes.get(position) + "");
                    holder.btn_upvote.setText(all_upvotes.get(position) + "");
                    holder.btn_upvote.setChecked(true);
                    holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_blue_button));
                    holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumbs_up_blue, 0, 0, 0);
                } else {
                    holder.btn_upvote.setTextOn(all_upvotes.get(position) + "");
                    holder.btn_upvote.setTextOff(all_upvotes.get(position) + "");
                    holder.btn_upvote.setText(all_upvotes.get(position) + "");
                    holder.btn_upvote.setChecked(false);
                    holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.twotone_thumb_up_alt_black_24, 0, 0, 0);
                    holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_black));

                }

                if (list_dislike.contains(all_Document_Reference.get(position))) {

                    holder.btn_downvote.setText(all_downvotes.get(position) + "");
                    holder.btn_downvote.setTextOn(all_downvotes.get(position) + "");
                    holder.btn_downvote.setTextOff(all_downvotes.get(position) + "");
                    holder.btn_downvote.setChecked(true);
                    holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_red_button));
                    holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumbs_down_red, 0, 0, 0);
                } else {
                    holder.btn_downvote.setText(all_downvotes.get(position) + "");
                    holder.btn_downvote.setTextOn(all_downvotes.get(position) + "");
                    holder.btn_downvote.setTextOff(all_downvotes.get(position) + "");
                    holder.btn_downvote.setChecked(false);
                    holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.twotone_thumb_down_alt_black_24, 0, 0, 0);
                    holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_black));

                }

                String timestamp = all_timestamp.get(position);
                Log.i("timestamp", timestamp);
                String nanoseconds = "000";
                try {
                    nanoseconds = timestamp.substring(timestamp.indexOf("nanoseconds=") + 12, timestamp.indexOf("nanoseconds=") + 15);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                timestamp = timestamp.substring(timestamp.indexOf("seconds=") + 8, timestamp.indexOf(",")) + nanoseconds;


                holder.text_timestamp.setText(timeago(Long.parseLong(timestamp)));
                Log.i("tagg1", timeago(Long.parseLong(timestamp)));

                holder.btn_upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Bundle params = new Bundle();
                        params.putString("activity", "Gridviewadapter-click");
                        params.putString("button_name", "upvote");
                        mFirebaseAnalytics.logEvent("upvote_clicked", params);

                        Log.i("tagg5 - before", position + "-" + all_upvotes);
                        if (holder.btn_upvote.isChecked()) {
                            holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_blue_button));
                            holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumbs_up_blue, 0, 0, 0);

                            all_upvotes.set(position, all_upvotes.get(position) + 1);

                            holder.btn_upvote.setTextOn(all_upvotes.get(position) + "");
                            holder.btn_upvote.setTextOff(all_upvotes.get(position) + "");
                            holder.btn_upvote.setText(all_upvotes.get(position) + "");

                            addLike(all_Document_Reference.get(position), 1, all_upvotes.get(position));

                        } else {

                            holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_black));
                            holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.twotone_thumb_up_alt_black_24, 0, 0, 0);

                            all_upvotes.set(position, all_upvotes.get(position) - 1);

                            holder.btn_upvote.setTextOn(all_upvotes.get(position) + "");
                            holder.btn_upvote.setTextOff(all_upvotes.get(position) + "");
                            holder.btn_upvote.setText(all_upvotes.get(position) + "");

                            addLike(all_Document_Reference.get(position), -1, all_upvotes.get(position));
                        }
                        Log.i("tagg5 - after", position + "-" + all_upvotes);

                    }
                });


                holder.btn_downvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle params = new Bundle();
                        params.putString("activity", "Gridviewadapter-click");
                        params.putString("button_name", "downvote");
                        mFirebaseAnalytics.logEvent("downvote_clicked", params);

                        Log.i("tagg5 - before", position + "-" + all_downvotes);

                        if (holder.btn_downvote.isChecked()) {
                            holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_red_button));
                            holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumbs_down_red, 0, 0, 0);

                            all_downvotes.set(position, all_downvotes.get(position) + 1);

                            holder.btn_downvote.setText(all_downvotes.get(position) + "");
                            holder.btn_downvote.setTextOn(all_downvotes.get(position) + "");
                            holder.btn_downvote.setTextOff(all_downvotes.get(position) + "");

                            addDisLike(all_Document_Reference.get(position), 1, all_downvotes.get(position));

                        } else {
                            holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_black));
                            holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.twotone_thumb_down_alt_black_24, 0, 0, 0);

                            all_downvotes.set(position, all_downvotes.get(position) - 1);

                            holder.btn_downvote.setText(all_downvotes.get(position) + "");
                            holder.btn_downvote.setTextOn(all_downvotes.get(position) + "");
                            holder.btn_downvote.setTextOff(all_downvotes.get(position) + "");

                            addDisLike(all_Document_Reference.get(position), -1, all_downvotes.get(position));

                        }
                        Log.i("tagg5 - after", position + "-" + all_downvotes);

                    }
                });


                holder.btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle params = new Bundle();
                        params.putString("activity", "Gridviewadapter-click");
                        params.putString("button_name", "save");
                        mFirebaseAnalytics.logEvent("save_clicked", params);

                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                String fileName = Environment.getExternalStorageDirectory().toString() + "/Tamil GAG/";
                                new File(fileName).mkdirs();

                                if (all_document.get(position).get("type").toString().contains("image"))
                                    save(holder);
                                else if (all_document.get(position).get("type").toString().contains("video"))
                                    new Share_Video_Async(holder.btn_share, "save").execute(all_document.get(position).get("video_link").toString(), Environment.getExternalStorageDirectory() + "/Tamil GAG/" + "video_" + System.currentTimeMillis() + ".mp4");


                            }
                        });

                        t.start();


                    }
                });

                holder.btn_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle params = new Bundle();
                        params.putString("activity", "Gridviewadapter-click");
                        params.putString("button_name", "share");
                        mFirebaseAnalytics.logEvent("share_clicked", params);

                        Thread t = new Thread(new Runnable() {
                            public void run() {

                                if (all_document.get(position).get("type").toString().contains("image"))
                                    share(holder);

                                else if (all_document.get(position).get("type").toString().contains("video"))
                                    new Share_Video_Async(holder.btn_share, "share").execute(all_document.get(position).get("video_link").toString(), context.getExternalCacheDir() + "video_cache.mp4");


                            }
                        });

                        t.start();


                    }
                });


                holder.ib_popup_menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  dialog();
                        report_dialog(allItemsUrl.get(position), all_timestamp.get(position), all_Document_Reference.get(position));

                    }
                });

            }
            else
            {
                Log.w("tag13", position + " NULL");
                holder.cardView.setVisibility(View.GONE);
                AdRequest adRequest = new AdRequest.Builder().build();

                holder.ad_native.loadAd(adRequest);
                holder.ad_native.setVisibility(View.VISIBLE);


            }
        }
            return view;





    }




    class Share_Video_Async extends AsyncTask<String,Integer,String> {
        Snackbar snackbar;
        private View rootView;
        String save_or_share;
        ProgressBar item = new ProgressBar(context,null,android.R.attr.progressBarStyleSmallTitle);


        public Share_Video_Async(View rootView,String save_or_share) {

            this.rootView =rootView;
            this.save_or_share = save_or_share;
        }

        @Override
        protected String doInBackground(String... strings) {

            File file_video = new File(strings[1]);
            String snackbar_title = "Preparing to Share Video...";

            if(save_or_share.contains("save"))
                snackbar_title = "Downloading Video...";
            if(save_or_share.contains("share"))
                snackbar_title = "Preparing to Share Video...";


                snackbar = Snackbar.make(rootView, snackbar_title, Snackbar.LENGTH_INDEFINITE);
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

            if(save_or_share.contains("share"))
                shareVideo("Share Video...",s);
            else if (save_or_share.contains("save"))
                { Snackbar snackbar = Snackbar.make(rootView, "Video Saved at "+s,Snackbar.LENGTH_SHORT);
                    snackbar.setAction("OPEN", new MyOpenListener(s,"video/*"));
                 snackbar.show();}

            super.onPostExecute(s);
        }
    }
    public void shareVideo(final String title, String path) {

        MediaScannerConnection.scanFile(context, new String[] { path },

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
                        context.startActivity(Intent.createChooser(shareIntent, "Share Video..."));

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


    private void share(ViewHolder holder) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        holder.imageView.buildDrawingCache();
        Bitmap bmap = holder.imageView.getDrawingCache();
        Uri uri = null;
        try {
            File file = new File(context.getExternalCacheDir(), "cache.png");
            FileOutputStream stream = new FileOutputStream(file);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            uri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }


        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        context.startActivity(intent);

    }

    private void save(ViewHolder holder) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        holder.imageView.buildDrawingCache();
        Bitmap bmap = holder.imageView.getDrawingCache();

        try {
            String fileName = Environment.getExternalStorageDirectory().toString() + "/Tamil GAG/";
            new File(fileName).mkdirs();
            fileName += System.currentTimeMillis() + ".jpg";

            File file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            stream.close();



            Snackbar snackbar = Snackbar.make(holder.imageView, "Image Saved at "+fileName,Snackbar.LENGTH_LONG);
            snackbar.setAction("OPEN", new MyOpenListener(fileName,"image/*"));

            snackbar.show();





        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }

    }
    public class MyOpenListener implements View.OnClickListener {
        String filename,mimetype;

        MyOpenListener(String filename,String mimetype)
        {
            this.filename = filename;
            this.mimetype = mimetype;
        }


        @Override
        public void onClick(View v) {

            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            newIntent.setDataAndType(Uri.fromFile(new File(filename)),mimetype);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(newIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
            }

        }
    }
    private void addLike(String documentReference, int i,int count) {

        if (i == 1) {
            if (!list_like.contains(documentReference))
                list_like.add(documentReference);
        }
        if (i == -1) {
            if (list_like.contains(documentReference))
                list_like.remove(documentReference);
        }

        saveArrayList(list_like, LIKE);

        saveMemes("upvote",documentReference,count);

    }
    private void addDisLike(String documentReference, int i,int count) {

        if (i == 1) {
            if (!list_dislike.contains(documentReference))
                list_dislike.add(documentReference);
        }
        if (i == -1) {
            if (list_dislike.contains(documentReference))
                list_dislike.remove(documentReference);
        }
        Log.w("tagg4-savearraylist", list_dislike.size() + "");

        saveArrayList(list_dislike, DISLIKE);

        saveMemes("downvote",documentReference,count);

    }

    void report_dialog(final String content_url, final String content_timestamp, final String doc_ref) {

        final String[] grpname = new String[10];
        grpname[0] = "Violent or repulsive content";
        grpname[1] = "Hateful or abusive content";
        grpname[2] = "Harmful dangerous acts";
        grpname[3] = "Child abuse";
        grpname[4] = "Promotes terrorism";
        grpname[5] = "Sexual content";
        grpname[6] = "Spam or misleading";
        grpname[7] = "Infringes my rights";
        grpname[8] = "Others";
        grpname[9] = "Delete";


        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
        alt_bld.setIcon(R.drawable.baseline_report_black_18);
        alt_bld.setTitle("Report Content!");
        alt_bld.setSingleChoiceItems(grpname, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                sendFeedback(content_url, content_timestamp, grpname[item],doc_ref);
                alert.dismiss();

            }
        });
        alert = alt_bld.create();
        alert.show();


    }


    protected void sendFeedback(String image_url, String timestamp, String violation_type,String doc_ref) {

        if(violation_type.toLowerCase().contains("delete"))
        {
            deleteMemes(doc_ref,image_url);
        }

        else {
            try {
                int i = 3 / 0;
            } catch (Exception e) {
                ApplicationErrorReport report = new ApplicationErrorReport();
                report.packageName = report.processName = context.getPackageName();
                report.time = System.currentTimeMillis();
                report.type = ApplicationErrorReport.TYPE_CRASH;
                report.systemApp = false;

                ApplicationErrorReport.CrashInfo crash = new ApplicationErrorReport.CrashInfo();
                crash.exceptionClassName = e.getClass().getSimpleName();
                crash.exceptionMessage = e.getMessage();

                StringWriter writer = new StringWriter();
                PrintWriter printer = new PrintWriter(writer);
                e.printStackTrace(printer);

                crash.stackTrace = writer.toString();

                StackTraceElement stack = e.getStackTrace()[0];
                crash.throwClassName = image_url;
                crash.throwFileName = timestamp;
                crash.throwMethodName = violation_type;
                crash.throwLineNumber = stack.getLineNumber();


                report.crashInfo = crash;

                Intent intent = new Intent(Intent.ACTION_APP_ERROR);
                intent.putExtra(Intent.EXTRA_BUG_REPORT, report);
                context.startActivity(intent);
            }
        }



    }


    class DownloadFileFromURL extends AsyncTask<String, String, String[]> {
        private ViewHolder rootView;
        private ProgressDialog dialog;
        public DownloadFileFromURL(ViewHolder rootView) {
            dialog = new ProgressDialog(context);
            this.rootView = rootView;
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

            if (file_url[1].contains("save")) {

                Toast.makeText(context, "Saved at : " + file_url[2], Toast.LENGTH_LONG).show();
            } else if (file_url[1].contains("share")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file_url[2]));
                context.startActivity(Intent.createChooser(sharingIntent, "Share Memes"));
            }

        }

    }

    public void saveArrayList(ArrayList<String> list, String key) {
        SharedPreferences prefs = context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void saveMemes(String fieldname,String docReference,int count) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Boolean> uploaded = new ArrayList<Boolean>();

        Log.i("tagg6",docReference);
        Map<String, Object> doc_upload = new HashMap<>();

       doc_upload.put(fieldname, count);




        db.document( docReference)
                .update(doc_upload)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.w("tag7", "Update Success");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("tag7", "Error writing document", e);
                    }
                });


    }


    private void deleteMemes(final String docReference,final String img_url) {


            final EditText taskEditText = new EditText(context);
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Admin Password")

                    .setView(taskEditText)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(taskEditText.getText());
                            if(task.contains("asdfghjkl"))
                            {
try {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.document(docReference).delete();

}catch (Exception e){e.printStackTrace();}

    try {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(img_url);
        httpsReference.delete();

    }catch (Exception e){e.printStackTrace();}




                            }

                           else if(task.contains("link")) {

                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("link", img_url);
                                clipboard.setPrimaryClip(clip);


                            }


                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();





    }





    private int getCategoryDrawable(String category) {

        if (category.toLowerCase().contains("funny"))
             return  R.drawable.funny;
        else if (category.toLowerCase().contains("Trending"))
            return  R.drawable.baseline_whatshot_black_18;
        else if (category.toLowerCase().contains("relationship"))
            return  R.drawable.baseline_favorite_black_24;
        else if (category.toLowerCase().contains("sad"))
            return  R.drawable.baseline_sentiment_very_dissatisfied_black_24;
        else if (category.toLowerCase().contains("college"))
            return  R.drawable.baseline_local_library_black_24;
        else if (category.toLowerCase().contains("corporate"))
            return  R.drawable.baseline_work_black_24;

        else if (category.toLowerCase().contains("politics"))
            return  R.drawable.politics;
        else if (category.toLowerCase().contains("cinema"))
            return  R.drawable.cinema;
        else if (category.toLowerCase().contains("sports"))
            return  R.drawable.sports;
        else if (category.toLowerCase().contains("actors"))
            return  R.drawable.actor;
        else if (category.toLowerCase().contains("actress"))
            return  R.drawable.actress;
        else if (category.toLowerCase().contains("games"))
            return  R.drawable.pubg;
        else if (category.toLowerCase().contains("mokkai"))
            return  R.drawable.mokkai;
        else if (category.toLowerCase().contains("others"))
            return  R.drawable.others;
        else if (category.toLowerCase().contains("hollywood"))
            return  R.drawable.hollywood;
        else if (category.toLowerCase().contains("savage"))
            return  R.drawable.savage;
        else if (category.toLowerCase().contains("dark humor"))
            return  R.drawable.darkhumor;
        else if (category.toLowerCase().contains("wtf"))
            return  R.drawable.wtf;
        else if (category.toLowerCase().contains("gif"))
            return  R.drawable.gif;
        else if (category.toLowerCase().contains("trending"))
            return  R.drawable.baseline_whatshot_black_18;
        else if (category.toLowerCase().contains("vadivelu"))
            return  R.drawable.vadivel;
        else if (category.toLowerCase().contains("santhanam"))
            return  R.drawable.santhanam;
        else if (category.toLowerCase().contains("goundamani"))
            return  R.drawable.goundamani;
        else if (category.toLowerCase().contains("vivek"))
            return  R.drawable.vivek;
        else if (category.toLowerCase().contains("movie"))
            return  R.drawable.cinema;
        else if (category.toLowerCase().contains("youtube"))
            return  R.drawable.youtube;
        else
            return R.drawable.baseline_whatshot_black_18;





    }

    public String timeago(long timestamp) {
    final long diff = System.currentTimeMillis() - timestamp;
    if (diff < 60 * MINUTE_MILLIS) {
        return diff / MINUTE_MILLIS + " M";
    }  else if (diff < 24 * HOUR_MILLIS) {
        return diff / HOUR_MILLIS + " H";
    } else {
        return diff / DAY_MILLIS + " D";
    }


}


    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


    class open_swipeActivity extends AsyncTask<Integer,Integer,Integer>{

        ViewHolder holder;

        final ProgressDialog  progressDialog = new ProgressDialog(context);

   open_swipeActivity(ViewHolder holder){
            this.holder = holder;

        }



        @Override
        protected void onPreExecute() {


            progressDialog.setMessage("Opening...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... ints) {
/*
            if(all_document.get(ints[0]).get("type").toString().contains("image"))
try {
    holder.imageView.buildDrawingCache();
    Bitmap bitmap = holder.imageView.getDrawingCache();

    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
    byte[] byteArray = bStream.toByteArray();

    File file = new File(Environment.getExternalStorageDirectory() + "/Tamil GAG/", "swipeactivity.jpg");

    file.mkdirs();

    if(file.exists())
        file.delete();

        FileOutputStream stream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        stream.close();
        Log.i("taggg6", file.getTotalSpace() + "size");

    } catch (Exception e) {
        e.printStackTrace();
    }

*/

            return ints[0];
        }

        @Override
        protected void onPostExecute(Integer position) {
            if(progressDialog.isShowing())
                 progressDialog.dismiss();

            Log.i("tag12-grid-imgurl",allItemsUrl.get(position));
            Log.i("tag12-grid-video",all_document.get(position).get("video_link").toString());
            Log.i("tag12-grid-type", all_document.get(position).get("type").toString());

            Intent intent = new Intent(context, SwipeActivity.class);
            intent.putExtra("URLList", allItemsUrl);
            intent.putExtra("video_link", all_document.get(position).get("video_link").toString());
            intent.putExtra("video_or_image", all_document.get(position).get("type").toString());
            intent.putExtra("position", position);
            Log.i("taggg6",all_document.get(position).get("video_link").toString());
            context.startActivity(intent);


            super.onPostExecute(position);
        }
    }


}

class ViewHolder {
    ImageView imageView;
    TextView text_desc,text_category,text_timestamp;
    ImageView imageButton_category;

    ToggleButton btn_upvote,btn_downvote;
    Button btn_share;
    Button btn_save;
    ImageButton ib_popup_menu;
    DrawerLayout drawerLayout;
    AdView ad_native;
    CardView cardView;
}


