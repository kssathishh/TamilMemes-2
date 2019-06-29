package funny.tamil.gag.memes;



import android.annotation.TargetApi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "taggg2";
    Bitmap bitmap;





    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.i("taggg2","onmessage received");
        String url =  remoteMessage.getData().get("url");

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

Log.i("taggg2","image_url"+url);
        new generatePictureStyleNotification(this, title,body,
                url).execute();


    }




    public static class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext = null;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d("taggg2", "do in background" );

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            Log.d("taggg2", "completed" );
            NotificationManager mNotificationManager;
            NotificationCompat.Builder mBuilder;
            final String NOTIFICATION_CHANNEL_ID = "10001";

            Intent resultIntent = new Intent(mContext , MainActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setSmallIcon(R.drawable.ic_tha_icon);
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(result))
                    .setLargeIcon(result);




            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NEW MEMES", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(R.color.red);
                assert mNotificationManager != null;
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(0 /* Request Code */, mBuilder.build());



            Log.d("taggg2", "show notif" );
            super.onPostExecute(result);


        }
    }


}