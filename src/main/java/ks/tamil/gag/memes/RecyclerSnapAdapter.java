package ks.tamil.gag.memes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;

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
        UploadActivity  ua  = new UploadActivity();
        ua.download_url_list.clear();

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

        if(imgFile.exists()){
            Bitmap myBitmap =null;
            if(imgFile.getAbsolutePath().endsWith(".jpg")||imgFile.getAbsolutePath().endsWith(".png")||imgFile.getAbsolutePath().endsWith(".jpeg"))
            {
                holder.imageView.setForeground(null);
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            else
            {
                holder.imageView.setForeground(holder.play_icon);
                myBitmap = ThumbnailUtils.createVideoThumbnail(imgFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);

            }

            holder.imageView.setImageBitmap(myBitmap);


//---

            /*
            String filename = app;
            File file1 = new File(filename);


            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+ new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())+"/"+ Build.MODEL+Build.ID+"/"+file1.getName());
            Uri file = Uri.fromFile(new File(filename));

            UploadTask uploadTask = ref.putFile(file);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    holder.progressBar.setProgress((int)(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()));
                    Log.d("tag5", "onProgress: "+(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()));



                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("tag5", "onSuccess: uri= "+ uri.toString());
                            download_url_list.add(uri.toString());

                            UploadActivity  ua  = new UploadActivity();
                            ua.download_url_list.add(uri.toString());

                        }
                    });
                }
            });



*/

   //---







        }


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
            imageView = (ImageView) itemView.findViewById(R.id.imageView);



        }

        @Override
        public void onClick(View v) {

            Log.d("App", mApps.get(getAdapterPosition()));

        }
    }

}
