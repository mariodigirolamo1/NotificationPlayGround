package com.mdg.notificationplayground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static String CHANNEL_ID = "";

    /**
     * Keep trace of your notifications ID if you want to
     * remove or modify a specific notification later.
     * I used an HashMap so that I could give a significant name to each notification
     * I'll show.
     */

    private static HashMap<String,Integer> notificationIds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            String action = extras.getString("action");
            switch (action){

                case "notificationShortTap":

                    Log.d(TAG, "onCreate: notificationShortTap");

                    break;

                default:

                    break;

            }
        }else {

            makeCollapsedNotification();

        }
    }

    /**
     * Basic notification with:
     * Icon, Title, small amount of context text.
     */

    private void makeCollapsedNotification(){

        createChannel();

        createNotification();

    }

    /**
     * This is only required for Android 8.0 (API 26+)
     * In older versinos it is ignored.
     */

    private void setChannelId(){
        CHANNEL_ID = "CHANNEL_ID_"+System.currentTimeMillis();
    }

    /**
     * !!!!!!!!!!!!! THIS IS THE FIRST THING TO DO BEFORE ANY NOTIFICATION CREATION
     */

    private void createChannel(){

        setChannelId();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    /**
     * Chose Extras and Class to class on tap.
     * @return
     */

    private PendingIntent createIntentForTap(){

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("action","notificationShortTap");

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent =
                taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return pendingIntent;

    }

    /**
     * Finally build and show the notification
     */

    private void createNotification(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)

                .setSmallIcon(R.drawable.ic_launcher_background)

                .setContentTitle("textTitle")
                .setContentText("textContent")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))

                /**
                 * Set an intent to do something on tap
                 */

                .setContentIntent(createIntentForTap())
                //setAutoCancel(true) to remove the Notification after tap. Set false to ignore this behavior.
                .setAutoCancel(true)

                /**
                 * Setting this priority helps us with compatibility with Android 7.1 and lower
                 */

                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        /**
         * text content is truncated to fit one line by default.
         * To display a longer text add
         * .setStyle(new NotificationCompat.BigTextStyle()
         * .bigText("Much longer text that cannot fit one line..."))
         */

        NotificationManagerCompat notificationManagerCompat
                = NotificationManagerCompat.from(this);

        //Remember to save the notification ID that you pass to NotificationManagerCompat.notify()
        // because you'll need it later if you want to update or remove the notification.

        int notificationId = (int)System.currentTimeMillis();

        notificationIds.put("SimpleNotification",notificationId);

        // notificationId is a unique int for each notification that you must define
        notificationManagerCompat.notify(notificationId, builder.build());

    }
}
