package in.theapu.stunnel.receiver;

/**
 * Created by APU V on 16-01-2019.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.theapu.stunnel.service.ForegroundService;
import in.theapu.stunnel.util.Utility;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utility.isRunning()) {
            // Keep the running status
            Utility.stop();
            Utility.start();

            // Wait for some time
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }

            if (Utility.isRunning())
                context.startService(new Intent(context, ForegroundService.class));
        }
    }
}
