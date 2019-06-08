package ks.tamil.gag.memes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nileshp.multiphotopicker.photopicker.activity.PickImageActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import ks.tamil.gag.memes.adapter.RecyclerSnapAdapter;


public class UploadActivity extends AppCompatActivity {

    private ArrayList<String> pathList;
    public static final String ORIENTATION = "orientation";

    private RecyclerView mRecyclerView;
    private boolean mHorizontal;
    ArrayList<String> selected_category= new ArrayList<>();
     Button btn_category;
    public static ArrayList<String> download_url_list = new ArrayList<>();

    Bundle savedInstanceState1;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState1 = savedInstanceState;
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Upload Memes");

        Window window = UploadActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(UploadActivity.this,R.color.colorPrimaryDark));


       TextView tv_select_category =  findViewById(R.id.tv_select_category);
       tv_select_category.setText(Html.fromHtml(getString(R.string.select_category)));
        TextView tv_select_images =  findViewById(R.id.tv_select_images);
        tv_select_images.setText(Html.fromHtml(getString(R.string.select_images)));



        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,},
                    103);
        }

        Intent mIntent = new Intent(UploadActivity.this, PickImageActivity.class);
        mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, 60);
        mIntent.putExtra(PickImageActivity.KEY_LIMIT_MIN_IMAGE, 1);
        startActivityForResult(mIntent, PickImageActivity.PICKER_REQUEST_CODE);

        Button btn_choose_image = findViewById(R.id.btn_choose_image);


        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(UploadActivity.this, PickImageActivity.class);
                mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, 60);
                mIntent.putExtra(PickImageActivity.KEY_LIMIT_MIN_IMAGE, 1);
                startActivityForResult(mIntent, PickImageActivity.PICKER_REQUEST_CODE);

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
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        final ArrayList<Boolean> uploaded = new ArrayList<Boolean>();

        EditText et_desc = findViewById(R.id.editText_desc);


        Log.i("tag7",pathList.size()+" - "+ download_url_list.size());

        if(pathList.size() == download_url_list.size() ) {


            for ( int i = 0; i < download_url_list.size(); i++) {


                final int random_upvote = new Random().nextInt((999 - 100) + 1) + 100;
                final int random_downvote = new Random().nextInt((100 - 10) + 1) + 10;


                Map<String, Object> doc_upload = new HashMap<>();
                doc_upload.put("category", selected_category);

                doc_upload.put("description", et_desc.getText() + "");
                doc_upload.put("downvote", random_downvote);
                doc_upload.put("upvote", random_upvote);
                doc_upload.put("link", download_url_list.get(i));
                doc_upload.put("sensitive", false);
                doc_upload.put("tags", Arrays.asList("tag1"));
                doc_upload.put("time", new Timestamp(new Date()));
                doc_upload.put("type", "image");
                doc_upload.put("username", Build.MODEL + "-" + Build.ID);
                doc_upload.put("comments", Arrays.asList(""));
                doc_upload.put("flag_string", "");
                doc_upload.put("flag_integer", 0);
                doc_upload.put("flag_boolean", false);


                db.collection("collection1").document(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())+"-"+Build.MODEL + Build.ID + System.currentTimeMillis() +"-"+i)
                        .set(doc_upload)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("tag7", "DocumentSnapshot successfully written!");
                                uploaded.add(true);

                                //  if(uploaded.size() == download_url_list.size())
                                Snackbar snackbar = Snackbar
                                        .make(findViewById(R.id.coordinatorLayout), uploaded.size() + "/" + download_url_list.size() + " Memes Uploaded Successfully!!!", Snackbar.LENGTH_LONG);
                                snackbar.show();

                                if(uploaded.size() == download_url_list.size())
                                {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
                                    builder.setMessage("Memes uploaded Successfully.")
                                            .setTitle("Upload Successful")
                                            .setIcon(R.drawable.success);

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();

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
        builder.setMessage("Cancel")
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

        boolean[] checkedItems = new boolean[]{false, false, false, false ,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false,false, false, false, false, false}; //this will checked the items when user open the dialog
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
        ArrayList<String> apps = img_pathlist;

        SnapAdapter snapAdapter = new SnapAdapter();
        if (mHorizontal) {
            //snapAdapter.addSnap(new Snap(Gravity.CENTER_HORIZONTAL, "Snap center", apps));
            snapAdapter.addSnap(new Snap(Gravity.START, "", apps,UploadActivity.this));
            //snapAdapter.addSnap(new Snap(Gravity.END, "Snap end", apps));
            //snapAdapter.addSnap(new Snap(Gravity.CENTER, "GravityPager snap", apps));
            mRecyclerView.setAdapter(snapAdapter);
        } else {

          RecyclerSnapAdapter adapter = new RecyclerSnapAdapter(false, false, apps,UploadActivity.this);

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
        if (resultCode != RESULT_OK) {
            return;
        }
        if (resultCode == -1 && requestCode == PickImageActivity.PICKER_REQUEST_CODE) {
            this.pathList = intent.getExtras().getStringArrayList(PickImageActivity.KEY_DATA_RESULT);
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(UploadActivity.this));
            mRecyclerView.setHasFixedSize(true);

            if (savedInstanceState1 == null) {
                mHorizontal = true;
            } else {
                mHorizontal = savedInstanceState1.getBoolean(ORIENTATION);
            }
            uploadImages(this.pathList);

            setupRecyclerAdapter(this.pathList);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void uploadImages(ArrayList<String> upload_pathList)
    {
        final ProgressBar pb_upload = findViewById(R.id.progressBar_upload);
        final TextView tv_upload = findViewById(R.id.tv_upload);
        download_url_list.clear();
        tv_upload.setText("Uploading...");
        tv_upload.setTextColor(getColor(R.color.red));
        pb_upload.setVisibility(View.VISIBLE);


        pb_upload.setMax(upload_pathList.size());
        pb_upload.setProgress(0);
          for(int ii =0;ii< upload_pathList.size();ii++)
                {
                    String filename = upload_pathList.get(ii);
                    File file1 = new File(filename);


                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+android.os.Build.MODEL+file1.getName());
                Uri file = Uri.fromFile(new File(filename));

               UploadTask uploadTask = ref.putFile(file);
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                           // progressBar.setProgress((int)(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()));


                            Log.d("tag5", "onProgress: "+(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()));
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
                                    pb_upload.setProgress(pb_upload.getProgress()+1);
                                    tv_upload.setText("Uploading("+pb_upload.getProgress()+"/"+pb_upload.getMax()+")...");

                                    if(pb_upload.getProgress() == pb_upload.getMax())
                                    {
                                        tv_upload.setText("Uploaded Successfully");
                                        tv_upload.setTextColor(getColor(R.color.green));
                                        pb_upload.setVisibility(View.GONE);
                                    }

                                }
                            });
                        }
                    });


            }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        close();
    }
}
