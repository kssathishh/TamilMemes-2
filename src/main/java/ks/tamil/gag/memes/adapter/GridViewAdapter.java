package ks.tamil.gag.memes.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ks.tamil.gag.memes.R;
import ks.tamil.gag.memes.SwipeActivity;

public class GridViewAdapter extends BaseAdapter {

    public ArrayList<String> allItemsUrl;
    public ArrayList<String> allDesc;
    public ArrayList<String> all_category ;
    public ArrayList<String> all_timestamp ;
    public ArrayList<Integer> all_upvotes ;
    public ArrayList<Integer> all_downvotes ;
    public ArrayList<String> all_Document_Reference ;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    AlertDialog alert;
    private LayoutInflater inflater;
    Context context;

    public GridViewAdapter(Context context, ArrayList<String> images,ArrayList<String> desc,ArrayList<String> category,ArrayList<String> timestamp,ArrayList<Integer> upvotes,ArrayList<Integer> downvotes,ArrayList<String> document_Reference) {
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



        Log.w("tag2", images.size()+"");

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
            holder.imageView =  view.findViewById(R.id.ivImageInflator);
            holder.text_desc =  view.findViewById(R.id.textView_desc);
            holder.imageButton_category = view.findViewById(R.id.imageView_category);
            holder.text_category =  view.findViewById(R.id.tv_category);
            holder.text_timestamp =  view.findViewById(R.id.tv_timestamp);
            holder.btn_upvote = view.findViewById(R.id.btn_like);
            holder.btn_downvote = view.findViewById(R.id.btn_dislike);
            holder.btn_share = view.findViewById(R.id.btn_share);
            holder.btn_save = view.findViewById(R.id.btn_download);
            holder.ib_popup_menu = view.findViewById(R.id.imageButton_popup_menu);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!(allItemsUrl.size()<=position)) {
            Log.w("tag2", allItemsUrl.get(position));



           Glide.with(context)
                .load(allItemsUrl.get(position))
                .placeholder(R.drawable.hourglass)
                .error(R.drawable.notfound)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

                   .thumbnail(0.1f)
                .into(holder.imageView);

holder.imageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, SwipeActivity.class);
        intent.putExtra("URLList",allItemsUrl);
        intent.putExtra("position",position);
        context.startActivity(intent);
    }
});
            if(!allDesc.get(position).equals(""))
              holder.text_desc.setText(allDesc.get(position));
            else
              holder.text_desc.setVisibility(View.GONE);


            String category = all_category.get(position).replace('[',' ').replace(']',' ');
            if(category.length()<3)
                category ="Trending";

            holder.text_category.setText(category);

            holder.imageButton_category.setImageResource(getCategoryDrawable(category));

            holder.btn_upvote.setTextOn(all_upvotes.get(position)+"");
            holder.btn_upvote.setTextOff(all_upvotes.get(position)+"");
            holder.btn_upvote.setText(all_upvotes.get(position)+"");

            holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds( R.drawable.twotone_thumb_up_alt_black_24, 0, 0, 0);
            holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_black));


            holder.btn_downvote.setText(   all_downvotes.get(position)+"");
            holder.btn_downvote.setTextOn(   all_downvotes.get(position)+"");
            holder.btn_downvote.setTextOff(   all_downvotes.get(position)+"");
            holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds( R.drawable.twotone_thumb_down_alt_black_24, 0, 0, 0);
            holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_black));



            String timestamp = all_timestamp.get(position);

            timestamp=timestamp.substring(timestamp.indexOf("seconds=")+8,timestamp.indexOf(","))+
                    timestamp.substring(timestamp.indexOf("nanoseconds=")+12,timestamp.indexOf("nanoseconds=")+15);

            holder.text_timestamp.setText( timeago(Long.parseLong(timestamp)));
            Log.i("tagg1",timeago(Long.parseLong(timestamp)));


            holder.btn_upvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked)
                    {
                        holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_blue_button));
                        holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds( R.drawable.thumbs_up_blue, 0, 0, 0);

                    }
                    else
                    {
                        holder.btn_upvote.setTextColor(context.getResources().getColor(R.color.color_black));
                        holder.btn_upvote.setCompoundDrawablesWithIntrinsicBounds( R.drawable.twotone_thumb_up_alt_black_24, 0, 0, 0);

                    }



                }
            });

            holder.btn_downvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_red_button));
                        holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds( R.drawable.thumbs_down_red, 0, 0, 0);

                    }
                    else
                    {
                        holder.btn_downvote.setTextColor(context.getResources().getColor(R.color.color_black));
                        holder.btn_downvote.setCompoundDrawablesWithIntrinsicBounds( R.drawable.twotone_thumb_down_alt_black_24, 0, 0, 0);


                    }
                }
            });

            holder.btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fileName = Environment.getExternalStorageDirectory().toString()+"/Tamil GAG/";
                    new File(fileName).mkdirs();
                    fileName+= System.currentTimeMillis()+".jpg";

                    new DownloadFileFromURL(holder).execute(allItemsUrl.get(position),"save",fileName);

                }
            });

            holder.btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   String fileName =  context.getExternalCacheDir()+"/cache.jpg";
                    new DownloadFileFromURL(holder).execute(allItemsUrl.get(position),"share",fileName);
                }
            });


            holder.ib_popup_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  dialog();
                    dialog(allItemsUrl.get(position),all_timestamp.get(position));

                }
            });










        }

        return view;

    }

    void dialog(final String content_url, final String content_timestamp){

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

                sendFeedback(content_url,content_timestamp,grpname[item]);
                alert.dismiss();

            }
        });
        alert = alt_bld.create();
        alert.show();




    }


    protected void sendFeedback(String image_url,String timestamp,String violation_type) {
        try {
            int i = 3 / 0;
        } catch (Exception e) {
            ApplicationErrorReport report = new ApplicationErrorReport();
            report.packageName = report.processName =  context.getPackageName();
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
        }   }





    class DownloadFileFromURL extends AsyncTask<String, String, String[]> {
       private ViewHolder rootView;

       public DownloadFileFromURL(ViewHolder rootView) {

           this.rootView = rootView;
       }
        @Override
        protected void onPreExecute() {
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
            // dismiss the dialog after the file was downloaded

            if(file_url[1].contains("save")) {

                Toast.makeText(context, "Saved at : " + file_url[2], Toast.LENGTH_LONG).show();
            }

            else if (file_url[1].contains("share")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file_url[2]));
                context.startActivity(Intent.createChooser(sharingIntent, "Share Memes"));
            }

        }

    }






















    private int getCategoryDrawable(String category) {

        if (category.toLowerCase().contains("funny"))
             return  R.drawable.funny;
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