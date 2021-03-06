package funny.tamil.gag.memes;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nileshp.multiphotopicker.photopicker.activity.PickImageActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import funny.tamil.gag.memes.adapter.RecyclerSnapAdapter;
import funny.tamil.gag.memes.utils.fileUtils;


public class UploadActivity extends AppCompatActivity {

    private ArrayList<String> pathList = new ArrayList<String>();
    private ArrayList<String> video_pathList = new ArrayList<String>();
    public static final String ORIENTATION = "orientation";

    private RecyclerView mRecyclerView = null;
    private boolean mHorizontal = true;
    ArrayList<String> selected_category= new ArrayList<>();
     Button btn_category;
    public static ArrayList<String> download_url_list = new ArrayList<>();
    public static ArrayList<String> download_video_url_list = new ArrayList<>();
    fileUtils futils = new fileUtils();
    Bundle savedInstanceState1;
    String meme_type = "image";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState1 = savedInstanceState;
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Upload Memes");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(UploadActivity.this));
        mRecyclerView.setHasFixedSize(true);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
     Log.i("tag11 -actionsend- type",type+"");
            if (type.startsWith("image/")) {

               Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                meme_type ="image";
                pathList.add(uri.toString());
                video_pathList.add("");
                download_video_url_list.add("");


        }
            else if (type.startsWith("video/")) {

                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

                try {




                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(getPath(uri), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                    Bitmap play = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_play_circle_outline_white_48dp);
                    Bitmap thumbnail = null;

                    meme_type = "video";
                    thumbnail = overlay(thumb, play);
                    File f = new File(getCacheDir(), "thumb" + 0 + ".jpg");
                    bmpTofile(thumbnail, f);
                    pathList.add(f.getPath());
                    video_pathList.add(uri.toString());
Log.i("tag14",uri.toString() +"--"+f.getPath());

                }catch (Exception e){e.printStackTrace();}
            }


            if(meme_type.contains("image"))
                uploadImages(this.pathList,"image");
            else if(meme_type.contains("video")) {
                uploadImages(this.pathList, "image");
                uploadImages(this.video_pathList, "video");
            }

            setupRecyclerAdapter(pathList);

            }






        else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {

            Log.i("tag11 -multisend- type",type+"");


            if (type.startsWith("image/")) {

                ArrayList<Uri> uriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

                for(int i = 0;i<uriList.size();i++)
                {
                    pathList.add(uriList.get(i).toString());

                }



                uploadImages(pathList,"image");

                setupRecyclerAdapter(pathList);


            }
        } else {

            pathList.clear();
            video_pathList.clear();
            download_url_list.clear();
            download_video_url_list.clear();



            Matisse.from(UploadActivity.this)
                    .choose(MimeType.ofAll())
                    .countable(true)
                    .maxSelectable(50)
                    .thumbnailScale(0.5f)
                    .imageEngine(new GlideEngine())

                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

                    .forResult(1);

        }




        Window window = UploadActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(UploadActivity.this,R.color.colorPrimaryDark));


       TextView tv_select_category =  findViewById(R.id.tv_select_category);
       tv_select_category.setText(Html.fromHtml(getString(R.string.select_category)));
        TextView tv_select_images =  findViewById(R.id.tv_select_images);
        tv_select_images.setText(Html.fromHtml(getString(R.string.select_images)));



        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,},
                    103);
        }



        Button btn_choose_image = findViewById(R.id.btn_choose_image);


        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pathList.clear();
                video_pathList.clear();
                download_url_list.clear();
                download_video_url_list.clear();

              /*  Intent mIntent = new Intent(UploadActivity.this, PickImageActivity.class);
                mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, 60);
                mIntent.putExtra(PickImageActivity.KEY_LIMIT_MIN_IMAGE, 1);
                startActivityForResult(mIntent, PickImageActivity.PICKER_REQUEST_CODE);*/

                Matisse.from(UploadActivity.this)
                        .choose(MimeType.ofAll())
                        .countable(true)
                        .maxSelectable(50)
                        .thumbnailScale(0.5f)
                        .imageEngine(new GlideEngine())

                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

                        .forResult(1);

            }
        });



        Button btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMemes();
            }
        });
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });






    btn_category = findViewById(R.id.btn_category);

    btn_category.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           selectCategory();

        }
    });


    }



    private void saveMemes() {

        final ProgressDialog  progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Saving...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<Boolean> uploaded = new ArrayList<Boolean>();

        EditText et_desc = findViewById(R.id.editText_desc);


        Log.i("tag7",pathList.size()+" - "+ download_url_list.size());

        if(pathList.size() == download_url_list.size()) {

            progressDialog.setMax(download_url_list.size());
            progressDialog.show();

            for ( int i = 0; i < download_url_list.size(); i++) {


               // final int random_upvote = new Random().nextInt((100 - 10) + 1) + 10;
               // final int random_downvote = new Random().nextInt((10 - 1) + 1) + 1;

                final int random_upvote = 0;
                final int random_downvote = 0;
                String video_link = "";
                if(selected_category.size()==0)
                {selected_category.add("Trending");}

                video_link = download_video_url_list.get(i)+"";
                Map<String, Object> doc_upload = new HashMap<>();

                doc_upload.put("category", selected_category);

                doc_upload.put("description", et_desc.getText() + "");
                doc_upload.put("downvote", random_downvote);
                doc_upload.put("upvote", random_upvote);
                doc_upload.put("link", download_url_list.get(i));
                doc_upload.put("sensitive", false);
                doc_upload.put("tags", Arrays.asList("tag1"));
                doc_upload.put("time", new Timestamp(new Date()));
                doc_upload.put("type", meme_type);
                doc_upload.put("username", Build.MODEL + "-" + Build.ID);
                doc_upload.put("comments", Arrays.asList(""));
                doc_upload.put("flag_string", "");
                doc_upload.put("flag_integer", 0);
                doc_upload.put("flag_boolean", false);
                doc_upload.put("timestamp",System.currentTimeMillis());

                doc_upload.put("video_link",video_link );



                db.collection("collection1").document(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())+"-"+Build.MODEL + Build.ID + System.currentTimeMillis() +"-"+i)
                        .set(doc_upload)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("tag7", "DocumentSnapshot successfully written!");
                                uploaded.add(true);

                                //  if(uploaded.size() == download_url_list.size())
                                progressDialog.setProgress(uploaded.size());

                                if(uploaded.size() == download_url_list.size())
                                {
                                    progressDialog.dismiss();
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                                    builder.setMessage("Memes uploaded Successfully.")
                                            .setTitle("Upload Successful")
                                            .setIcon(R.drawable.success);

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });

                                    final AlertDialog dialog = builder.create();
                                    dialog.show();

                                    new CountDownTimer(3000, 1000) {

                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            }

                                        @Override
                                        public void onFinish() {
                                              dialog.dismiss();
                                              finish();
                                        }       }.start();

                                }



                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("tag7", "Error writing document", e);
                            }
                        });


            }
        }
        else
        {
            Log.i("tag7", "not uploaded");
            AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
            builder.setMessage("Images are Uploading!!! Please wait untill images are uploaded before saving Memes.")
                    .setTitle("Upload in Progress!!!")
                    .setIcon(R.drawable.upload_progress);
            AlertDialog dialog = builder.create();
            dialog.show();


        }

    }

    private void close() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setMessage("Do you want to cancel the Upload?")
                .setTitle("Cancel Upload?")
                .setIcon(R.drawable.cancel);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectCategory() {

        final ArrayList<String> selected_items = new ArrayList<>();
        final String[] listItems =   getResources().getStringArray(R.array.categories) ;

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setTitle("Select Category");

        boolean[] checkedItems = new boolean[]{false, false,false,false, false,false, false,false, false,false, false,false, false,false, false, false ,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false}; //this will checked the items when user open the dialog
        builder.setIcon(R.drawable.twotone_category_black_48);
        builder.setMultiChoiceItems(listItems,checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Toast.makeText(UploadActivity.this, "Position: " + which + " Value: " + listItems[which] + " State: " + (isChecked ? "checked" : "unchecked"), Toast.LENGTH_LONG).show();
                if(isChecked)
                    selected_items.add(listItems[which]);
                else
                    selected_items.remove(listItems[which]);


            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //  Toast.makeText(UploadActivity.this,  selected_items.toString() , Toast.LENGTH_LONG).show();

                selected_category = selected_items;
                btn_category.setText(selected_items.toString().replace('[',' ').replace(']',' '));

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void setupRecyclerAdapter(ArrayList<String> img_pathlist) {
        download_url_list.clear();
        download_video_url_list.clear();

//        Log.i("Logg4 - img_path",img_pathlist.get(0));
        ArrayList<String> apps = img_pathlist;
//        Log.i("Logg4 - apps",apps.get(0));
        RecyclerSnapAdapter adapter = null;
        SnapAdapter snapAdapter = new SnapAdapter();
        if (mHorizontal) {
            Log.i("Logg4 - Hori",mHorizontal+"");

            snapAdapter.addSnap(new Snap(Gravity.START, "", apps,UploadActivity.this));

            mRecyclerView.setAdapter(snapAdapter);
        } else {
            Log.i("Logg4 - apps",apps+"");

           adapter = new RecyclerSnapAdapter(false, false, apps,UploadActivity.this);
            Log.i("Logg4 - adapter",adapter.getItemCount()+"");


            mRecyclerView.setAdapter(adapter);

            new GravitySnapHelper(Gravity.TOP, false, new GravitySnapHelper.SnapListener() {
                @Override
                public void onSnap(int position) {
                    Log.d("Snapped", position + "");
                }
            }).attachToRecyclerView(mRecyclerView);
        }
        snapAdapter.notifyDataSetChanged();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {


        super.onActivityResult(requestCode, resultCode, intent);


      /*


        if (resultCode != RESULT_OK) {
            return;
        }
        if (resultCode == -1 && requestCode == PickImageActivity.PICKER_REQUEST_CODE) {
            this.pathList = intent.getExtras().getStringArrayList(PickImageActivity.KEY_DATA_RESULT);


            uploadImages(this.pathList);

            setupRecyclerAdapter(this.pathList);

        }
*/


        List<Uri> mSelected;
        if (requestCode == 1 && resultCode == RESULT_OK) {

            mSelected = Matisse.obtainResult(intent);

            Log.d("Matisse", "mSelected: " + mSelected);


for(int i = 0;i<mSelected.size();i++) {
    Log.i("tag11 -matisse- size",mSelected.size()+"");

    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inSampleSize = 1;
    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(getPath(mSelected.get(i)), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
    Bitmap play = BitmapFactory.decodeResource(getResources(),
            R.drawable.ic_play_circle_outline_white_48dp);
    Bitmap thumbnail = null;

    try {



    if(thumb == null)
    {
        meme_type ="image";
        this.pathList.add(getPath(mSelected.get(i)));
        this.video_pathList.add("");

        download_video_url_list.add("");
        Log.i("tag11 - image",mSelected.get(i)+"-"+i);

    }
    else
    {
        meme_type = "video";
        thumbnail =overlay(thumb,play);
        File f = new File(getCacheDir(), "thumb"+i+".jpg");
        bmpTofile(thumbnail,f);
        this.pathList.add(f.getPath());
        this.video_pathList.add(getPath(mSelected.get(i)));


        Log.i("tag11 - video",mSelected.get(i)+"-"+i);


    }

    } catch (IOException e) {
        e.printStackTrace();
    }



}
if(meme_type.contains("image"))
    uploadImages(this.pathList,"image");
else if(meme_type.contains("video")) {
    uploadImages(this.pathList, "image");
    uploadImages(this.video_pathList, "video");
}



            setupRecyclerAdapter(this.pathList);



        }


    }





void bmpTofile(Bitmap bmp,File f) throws IOException {

    f.createNewFile();

    //Convert bitmap to byte array
    Bitmap bitmap = bmp;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
    byte[] bitmapdata = bos.toByteArray();

    //write the bytes in file
    FileOutputStream fos = new FileOutputStream(f);
    fos.write(bitmapdata);
    fos.flush();
    fos.close();

}
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void uploadImages(final ArrayList<String> upload_pathList,String upload_type) {
int pb_max = upload_pathList.size();

        download_url_list.clear();
        download_video_url_list.clear();

if(upload_type.contains("video")) {
           pb_max = pb_max*2;
      }
       Log.i("upload_type1",upload_type);

        Uri uri = null;

        UploadTask uploadTask = null;
        for (int ii = 0; ii < upload_pathList.size(); ii++) {
            Log.i("Logg5", upload_pathList +"---"+ Environment.getExternalStorageDirectory());





             String filename = upload_pathList.get(ii);
            File file1 = new File(filename);




            try {

                if(file1.exists())
                {
                    uri = Uri.fromFile(new File(filename));

                    Log.i("Loggg5 - uri","file.exists");


                }
                else
                {
                    uri = Uri.parse(filename);
                    Log.i("Loggg5 - uri","file not exists");

                }


                String compressed_file_name = uri.toString();



                new compress_async(pb_max,upload_type).execute(compressed_file_name,ii+".jpg",upload_type);


                Log.i("Loggg5 - uri",compressed_file_name);




              //  uploadTask = ref.putFile(Uri.parse(compressed_file_name));

            } catch (Exception e) {
                e.printStackTrace();

            }



/*
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                     // pb_upload.setProgress((int)(pb_upload.getProgress()+ (taskSnapshot.getBytesTransferred() * 100 / taskSnapshot.getTotalByteCount())) );



                    // progressBar.setProgress((int)(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()));


                    Log.d("tag5", "onProgress: " + (taskSnapshot.getBytesTransferred() * 100 / taskSnapshot.getTotalByteCount()));


                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onSuccess(Uri uri) {


                            download_url_list.add(uri.toString());


                            pb_upload.setProgress(pb_upload.getProgress() + 1);
                            tv_upload.setText("Uploading(" + pb_upload.getProgress() + "/" + pb_upload.getMax() + ")...");

                            if (pb_upload.getProgress() == pb_upload.getMax()) {
                                tv_upload.setText("Uploaded Successfully");
                                tv_upload.setTextColor(getColor(R.color.green));
                                pb_upload.setVisibility(View.GONE);
                                File file = new File(Environment.getDataDirectory() , "compressed/images");
                                if (file.exists()) {
                                 //   file.delete();
                                }
                            }

                        }
                    });
                }
            });
*/

        }
    }

       @Override
    public void onBackPressed() {
        //super.onBackPressed();

        close();
    }



    public String compressImage(String imageUri,String filename1) {

            String filePath = null;

            try {
                filePath = getRealPathFromURI(imageUri);
            } catch (Exception e) {
            }

            if (filePath == null) {


                filePath = Environment.getExternalStorageDirectory() + "/Tamil GAG/compress.jpg";
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imageUri));

                    File f = new File(filePath);
                    f.createNewFile();

                    Bitmap bitmap = bmp;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            Bitmap scaledBitmap = null;
            Log.i("taggg8", filePath + "-");
            Bitmap bmp = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            int actualHeight = 0;
            int actualWidth = 0;

            bmp = BitmapFactory.decodeFile(filePath, options);
            actualHeight = options.outHeight;
            actualWidth = options.outWidth;
            Log.i("taggg8", actualHeight + "-");


            float maxHeight = 720.0f;
            float maxWidth = 1280.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);


//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path


            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            File file = new File(Environment.getExternalStorageDirectory(), "/Tamil GAG/Upload/");
            if (!file.exists()) {
                file.mkdirs();
            }

            String filename = (file.getAbsolutePath() + filename1);
            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;


    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    class compress_async extends AsyncTask <String,String,String> {

        final ProgressBar pb_upload = findViewById(R.id.progressBar_upload);
        final TextView tv_upload = findViewById(R.id.tv_upload);
        UploadTask uploadTask = null;
        String upload_type_cons = "image";



        @RequiresApi(api = Build.VERSION_CODES.M)
        compress_async(int max_size,String upload_type)
        {
            upload_type_cons = upload_type;
            Log.i("upload_type2",upload_type_cons);

            tv_upload.setText("Uploading...");
            tv_upload.setTextColor(getColor(R.color.red));
            pb_upload.setVisibility(View.VISIBLE);
            pb_upload.setMax(max_size);
            pb_upload.setProgress(0);
        }
        @Override
        protected String doInBackground(String... strings) {

            //Log.i("upload_type2-1",strings[0]+"---"+strings[2]);
           // Log.i("upload_type2-2",strings[0]+"---"+getPath(Uri.parse(strings[0])));


            if(strings[2].contains("video"))
                return strings[0];

            else

                return compressImage(strings[0],strings[1]);

        }




        @Override
        protected void onPostExecute(final String compressed_file_name) {
            Log.i("upload_type3",compressed_file_name+"-");

            Log.i("tag11 -image- list",download_url_list.size()+" - size");
            Log.i("tag11 -video- list",download_video_url_list.size()+" - size");



            String firebase_path = "image";
            if(upload_type_cons.contains("image"))
            {
                firebase_path = "images";
            }
            else if (upload_type_cons.contains("video"))
            {
                firebase_path = "videos";
            }

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            String filename  = System.currentTimeMillis()+"-"+ android.os.Build.MODEL+"-";

            final File compress_file = new File(compressed_file_name);
            if(compress_file.exists())
                filename +=compress_file.getName();


            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(firebase_path+"/" +formattedDate+"/"+  filename);

            try {
            Uri uri = null;
            if(new File(compressed_file_name).exists())
            {
                uri = Uri.fromFile(new File(compressed_file_name));

                Log.i("Loggg6 - uri","file.exists");


            }
            else
            {
                uri = Uri.parse(compressed_file_name);
                Log.i("Loggg6 - uri","file not exists");

            }


                uploadTask = ref.putFile(uri);

            }catch (Exception fe){


                fe.printStackTrace();


            }


            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Log.i("uri_exception",e.getMessage());


                                                }
                                            });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onSuccess(Uri uri) {


                                    if (upload_type_cons.contains("image")) {
                                        download_url_list.add(uri.toString());
                                        if (meme_type.contains("image")) {
                                            download_video_url_list.add("");

                                        }
                                        Log.i("tag_upload_url", uri.toString() + "-image");
                                    } else if (upload_type_cons.contains("video")) {

                                        download_video_url_list.add(uri.toString());

                                        Log.i("tag_upload_url", uri.toString() + "-video");


                                    }
                                    Log.i("tag_upload_url_image", download_url_list.toString());
                                    Log.i("tag_upload_url_video", download_video_url_list.toString());

                                    pb_upload.setProgress(pb_upload.getProgress() + 1);
                                    tv_upload.setText("Uploading(" + pb_upload.getProgress() + "/" + pb_upload.getMax() + ")...");

                                    if (pb_upload.getProgress() == pb_upload.getMax()) {

                                        tv_upload.setText("Uploaded Successfully");
                                        tv_upload.setTextColor(getColor(R.color.green));
                                        pb_upload.setVisibility(View.GONE);

                                    }
                                    if (compress_file.exists()) {
                                        compress_file.delete();
                                    }
                                }
                            });


                            Log.i("tag11 -image- list1", download_url_list.size() + " - size");
                            Log.i("tag11 -video- list1", download_video_url_list.size() + " - size");

                        }

                    });



        }
    }



    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, bmp1.getWidth()/2 - bmp2.getWidth()/2, bmp1.getHeight()/2-bmp2.getHeight()/2, null);
        return bmOverlay;
    }
    public String getPath(Uri uri) {

        try {

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            if (cursor != null) {

                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else {


                return uri.toString();

            }
        } catch (Exception e) {
            Log.i("tag14", "exception");
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                return copyInputStreamToFile(inputStream, new File(getCacheDir(), "uritofile.mp4"));

            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }

        }
    }


    private String copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if ( out != null ) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        return file.getPath();


    }
}
