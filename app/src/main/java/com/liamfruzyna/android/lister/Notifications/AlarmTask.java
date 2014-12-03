package com.liamfruzyna.android.lister.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmTask implements Runnable
{
    // The date selected for the alarm
    private final Calendar date;
    // The android system alarm manager
    private final AlarmManager am;
    // Your context to retrieve the alarm manager from
    private final Context context;

    private final String item;

    private final long alarmID;

    public AlarmTask(Context context, Calendar date, long id, String item)
    {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
        this.alarmID = id;
        this.item = item;
        run();
    }

    @Override
    public void run()
    {
        // Request to start our service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity

        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra("item", item);
        intent.putExtra("alarmID", alarmID);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        System.out.println("-----------------------");
        System.out.println("Setting Notification");
        System.out.println("At " + date.getTime());
        System.out.println("In " + (date.getTimeInMillis() - System.currentTimeMillis())/1000 + "s");
        System.out.println("-----------------------");
        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }
}