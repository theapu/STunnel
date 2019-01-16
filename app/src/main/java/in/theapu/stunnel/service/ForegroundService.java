package in.theapu.stunnel.service;

/**
 * Created by APU V on 16-01-2019.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;

import in.theapu.stunnel.R;
import in.theapu.stunnel.ui.MainActivity;


public class ForegroundService extends Service{

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Only a stub to protect the process
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.running))
                .setContentText(getString(R.string.desc))
                .setContentIntent(resultPendingIntent)
                .build();
        startForeground(R.string.app_name, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent p1) {
        // Not bindable
        return null;
    }
}
