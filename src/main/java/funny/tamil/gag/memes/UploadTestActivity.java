package funny.tamil.gag.memes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;


import java.util.List;


public class UploadTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_test);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,},
                    103);
        }

        /*
        new ImagePicker.Builder(UploadTestActivity.this)
                .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                .directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.PNG)
                .scale(600, 600)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();
                */

       /* new VideoPicker.Builder(UploadTestActivity.this)
                .mode(VideoPicker.Mode.GALLERY)
                .directory(VideoPicker.Directory.DEFAULT)
                .extension(VideoPicker.Extension.MP4)
                .enableDebuggingMode(true)
                .build();
*/



        Matisse.from(UploadTestActivity.this)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(50)
                .thumbnailScale(0.5f)
                .imageEngine(new GlideEngine())

                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

                .forResult(1);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      /*  if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);

            Log.i("tagg9", mPaths.toString());

        }

        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths =  data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);
            Log.i("tagg9", mPaths.toString());

        }*/

        List<Uri> mSelected;
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);

            Log.d("Matisse", "mSelected: " + mSelected);

            Uri  uri_video = mSelected.get(0);


            BitmapFactory.Options options=new BitmapFactory.Options();

            options.inSampleSize = 1;
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(getPath(uri_video), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            ImageView iv_test = (ImageView) findViewById(R.id.imageView_test);
            Log.i("taggg1",thumb.getHeight()+"-"+thumb.getWidth());

           Bitmap play =  BitmapFactory.decodeResource(getResources(),
                    R.drawable.thumbs_down_red);










            iv_test.setImageBitmap(overlay(thumb,play));



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
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
   /* public  void conversion(String[] cmd) {
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {
                    Log.i("tagg9","Load Success");
                }

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {

        }
        try {


            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                }

                @Override
                public void onFailure(String message) {
                }

                @Override
                public void onSuccess(String message) {
                    Log.i("tagg9",message);
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
        }
    }*/
}
