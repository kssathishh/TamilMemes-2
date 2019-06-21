package funny.tamil.gag.memes.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import funny.tamil.gag.memes.R;
import funny.tamil.gag.memes.UploadActivity;

public class RecyclerSnapAdapter extends RecyclerView.Adapter<RecyclerSnapAdapter.ViewHolder> {

    private ArrayList<String> mApps;
    private boolean mHorizontal;
    private boolean mPager;
    private Context mcontext;
    ArrayList<String> download_url_list = new ArrayList<>();

    public RecyclerSnapAdapter(boolean horizontal, boolean pager, ArrayList<String> apps, Context context) {
        mHorizontal = horizontal;
        mApps = apps;
        mPager = pager;
        mcontext = context;
        UploadActivity ua  = new UploadActivity();
        ua.download_url_list.clear();
        Log.i("Logg4-contructor",apps.get(0));


    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_snap_adapter, parent, false)) ;

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        String app = mApps.get(position);

        File imgFile = new File(app);


              Bitmap myBitmap =null;
              holder.imageView.setForeground(null);

                try {

                      if(imgFile.exists())
                       {

                           myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                       }
                       else {

                           InputStream is = mcontext.getContentResolver().openInputStream(Uri.parse(app));
                           myBitmap = BitmapFactory.decodeStream(is);
                       }
                    Log.i("Logg5","Recycler_uri");

                }catch (Exception e){

                        Log.i("Logg5","Recycler_path");

                    }






            holder.imageView.setImageBitmap(myBitmap);





    }
    public ArrayList<String> setDownloadURLList()
    {
        Log.i("tag6 - mapps", mApps.size()+"");
        Log.i("tag6 - download_list", download_url_list.size()+"");
        return  download_url_list;
    }
    public ArrayList<String> getDownloadURLList()
    {
        Log.i("tag6 - mapps", mApps.size()+"");
        Log.i("tag6 - download_list", download_url_list.size()+"");
        return  download_url_list;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;

        public Drawable play_icon;



        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_snap);



        }

        @Override
        public void onClick(View v) {

            Log.d("App", mApps.get(getAdapterPosition()));

        }
    }

}
