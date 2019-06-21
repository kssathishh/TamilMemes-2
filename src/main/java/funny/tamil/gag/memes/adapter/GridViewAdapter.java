package funny.tamil.gag.memes.adapter;


import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import funny.tamil.gag.memes.R;
import funny.tamil.gag.memes.SwipeActivity;
import funny.tamil.gag.memes.UploadActivity;

public class GridViewAdapter extends BaseAdapter {

    public ArrayList<String> allItemsUrl;
    public ArrayList<String> allDesc;
    public ArrayList<String> all_category;
    public ArrayList<String> all_timestamp;
    public ArrayList<Integer> all_upvotes;
    public ArrayList<Integer> all_downvotes;
    public ArrayList<String> all_Document_Reference;

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

    public GridViewAdapter(Context context, ArrayList<String> images, ArrayList<String> desc, ArrayList<String> category, ArrayList<String> timestamp, ArrayList<Integer> upvotes, ArrayList<Integer> downvotes, ArrayList<String> document_Reference) {
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
        final ViewHolder holder;
        View view = convertView;


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


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!(allItemsUrl.size() <= position)) {
            Log.w("tag2", allItemsUrl.get(position));


            Glide.with(context)
                    .load(allItemsUrl.get(position))
                    .placeholder(R.drawable.transparentbg)
                    .error(R.drawable.notfound)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SwipeActivity.class);
                    intent.putExtra("URLList", allItemsUrl);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
            if (!allDesc.get(position).equals(""))
                holder.text_desc.setText(allDesc.get(position));
            else
                holder.text_desc.setVisibility(View.GONE);


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
            }
            else
            {
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

                    Log.i("tagg5 - before", position + "-" + all_upvotes);
                    if (holder.btn_upvote.isChecked()) {
                        holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_blue_button));
                        holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumbs_up_blue, 0, 0, 0);

                        all_upvotes.set(position, all_upvotes.get(position) + 1);

                        holder.btn_upvote.setTextOn(all_upvotes.get(position) + "");
                        holder.btn_upvote.setTextOff(all_upvotes.get(position) + "");
                        holder.btn_upvote.setText(all_upvotes.get(position) + "");

                        addLike(all_Document_Reference.get(position),1, all_upvotes.get(position));

                    } else {

                        holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_black));
                        holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.twotone_thumb_up_alt_black_24, 0, 0, 0);

                        all_upvotes.set(position, all_upvotes.get(position) - 1);

                        holder.btn_upvote.setTextOn(all_upvotes.get(position) + "");
                        holder.btn_upvote.setTextOff(all_upvotes.get(position) + "");
                        holder.btn_upvote.setText(all_upvotes.get(position) + "");

                        addLike(all_Document_Reference.get(position), -1,all_upvotes.get(position));
                    }
                    Log.i("tagg5 - after", position + "-" + all_upvotes);

                }
            });


            holder.btn_downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("tagg5 - before", position + "-" + all_downvotes);

                    if(holder.btn_downvote.isChecked())
                    {
                        holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_red_button));
                        holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumbs_down_red, 0, 0, 0);

                        all_downvotes.set(position, all_downvotes.get(position) + 1);

                        holder.btn_downvote.setText(all_downvotes.get(position) + "");
                        holder.btn_downvote.setTextOn(all_downvotes.get(position) + "");
                        holder.btn_downvote.setTextOff(all_downvotes.get(position) + "");

                        addDisLike(all_Document_Reference.get(position),1, all_downvotes.get(position));

                    }
                    else
                    {
                        holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_black));
                        holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.twotone_thumb_down_alt_black_24, 0, 0, 0);

                        all_downvotes.set(position, all_downvotes.get(position) - 1);

                        holder.btn_downvote.setText(all_downvotes.get(position) + "");
                        holder.btn_downvote.setTextOn(all_downvotes.get(position) + "");
                        holder.btn_downvote.setTextOff(all_downvotes.get(position) + "");

                        addDisLike(all_Document_Reference.get(position),-1, all_downvotes.get(position));

                    }
                    Log.i("tagg5 - after", position + "-" + all_downvotes);

                }
            });




            holder.btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fileName = Environment.getExternalStorageDirectory().toString() + "/Tamil GAG/";
                    new File(fileName).mkdirs();
                    fileName += System.currentTimeMillis() + ".jpg";

                    new DownloadFileFromURL(holder).execute(allItemsUrl.get(position), "save", fileName);

                }
            });

            holder.btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fileName = context.getExternalCacheDir() + "/cache.jpg";
                    new DownloadFileFromURL(holder).execute(allItemsUrl.get(position), "share", fileName);
                }
            });


            holder.ib_popup_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  dialog();
                    dialog(allItemsUrl.get(position), all_timestamp.get(position));

                }
            });


        }

        return view;

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

    void dialog(final String content_url, final String content_timestamp) {

        final String[] grpname = new String[9];
        grpname[0] = "Violent or repulsive content";
        grpname[1] = "Hateful or abusive content";
        grpname[2] = "Harmful dangerous acts";
        grpname[3] = "Child abuse";
        grpname[4] = "Promotes terrorism";
        grpname[5] = "Sexual content";
        grpname[6] = "Spam or misleading";
        grpname[7] = "Infringes my rights";
        grpname[8] = "Others";


        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
        alt_bld.setIcon(R.drawable.baseline_report_black_18);
        alt_bld.setTitle("Report Content!");
        alt_bld.setSingleChoiceItems(grpname, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                sendFeedback(content_url, content_timestamp, grpname[item]);
                alert.dismiss();

            }
        });
        alert = alt_bld.create();
        alert.show();


    }


    protected void sendFeedback(String image_url, String timestamp, String violation_type) {
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

























    private int getCategoryDrawable(String category) {

        if (category.toLowerCase().contains("funny"))
             return  R.drawable.funny;

        else if (category.toLowerCase().contains("relationship"))
            return  R.drawable.baseline_favorite_black_24;
        else if (category.toLowerCase().contains("sad"))
            return  R.drawable.baseline_sentiment_very_dissatisfied_black_24;
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
        else if (category.toLowerCase().contains("pubg"))
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
    if (diff < MINUTE_MILLIS) {
        return "just now";
    } else if (diff < 2 * MINUTE_MILLIS) {
        return "a minute ago";
    } else if (diff < 50 * MINUTE_MILLIS) {
        return diff / MINUTE_MILLIS + " m";
    } else if (diff < 90 * MINUTE_MILLIS) {
        return "an hour ago";
    } else if (diff < 24 * HOUR_MILLIS) {
        return diff / HOUR_MILLIS + " h";
    } else if (diff < 48 * HOUR_MILLIS) {
        return "yesterday";
    } else {
        return diff / DAY_MILLIS + " days";
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
}