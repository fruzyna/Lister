package com.liamfruzyna.android.lister.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.liamfruzyna.android.lister.Activities.WLActivity;
import com.liamfruzyna.android.lister.R;

public class NotifyService extends Service
{

    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder
    {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    // Unique id to identify the notification.
    private static final int NOTIFICATION = 143;
    // Name of an intent extra we can use to identify if this service was started to create a notification
    // The system notification manager
    private NotificationManager mNM;

    @Override
    public void onCreate() {
        System.out.println("NotifyService onCreate()");
        Log.i("NotifyService", "onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("NotifyService onStartCommand()");
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // If this service was started by out AlarmTask intent then we want to show our notification
        showNotification(intent.getLongExtra("alarmID", 0), intent.getStringExtra("item"));
        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Creates a notification and shows it
     */
    @SuppressWarnings("deprecation")
    private void showNotification(long alarmID, String item) {

        Intent backToEventDetail = new Intent(this, WLActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, Integer.parseInt(String.valueOf(alarmID)), backToEventDetail, PendingIntent.FLAG_UPDATE_CURRENT);

        System.out.println("Creating Notification");
        Notification notify = new Notification.Builder(this)
                .setContentTitle(item)
                .setContentText("DO IT! touch to open Lister")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent).getNotification();

        notify.defaults = Notification.DEFAULT_SOUND;


        notify.flags = Notification.FLAG_AUTO_CANCEL;

        // Send the notification to the system.
        mNM.notify(Integer.parseInt(String.valueOf(alarmID)), notify);

        // Stop the service when we are finished
        stopSelf();
    }
}