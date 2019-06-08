package ks.tamil.gag.memes.adapter;

/**
 * Created by Mehul on 30-Nov-2015.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private LayoutInflater inflater;
    Context context;

    public GridViewAdapter(Context context, ArrayList<String> images,ArrayList<String> desc,ArrayList<String> category,ArrayList<String> timestamp,ArrayList<Integer> upvotes,ArrayList<Integer> downvotes) {
        inflater = LayoutInflater.from(context);
        this.context = context;
//
        allItemsUrl = images;
        allDesc = desc;
        all_category = category;
        all_timestamp = timestamp;
        all_upvotes = upvotes;
        all_downvotes = downvotes;


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
            holder.imageView = (ImageView) view.findViewById(R.id.ivImageInflator);
            holder.text_desc = (TextView) view.findViewById(R.id.textView_desc);
            holder.imageButton_category = view.findViewById(R.id.imageView_category);
            holder.text_category =  view.findViewById(R.id.tv_category);
            holder.text_timestamp =  view.findViewById(R.id.tv_timestamp);
            holder.btn_upvote = view.findViewById(R.id.btn_like);
            holder.btn_downvote = view.findViewById(R.id.btn_dislike);


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

            holder.btn_upvote.setText(   all_upvotes.get(position)+"");
            holder.btn_downvote.setText(   all_downvotes.get(position)+"");

            String timestamp = all_timestamp.get(position);

            timestamp =     timestamp.substring(timestamp.indexOf("seconds=")+8,timestamp.indexOf(","))+
                    timestamp.substring(timestamp.indexOf("nanoseconds=")+12,timestamp.indexOf("nanoseconds=")+15);

            holder.text_timestamp.setText( timeago(Long.parseLong(timestamp)));
            Log.i("tagg1",timeago(Long.parseLong(timestamp)));

        }



        return view;



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
    Button btn_upvote,btn_downvote;
}