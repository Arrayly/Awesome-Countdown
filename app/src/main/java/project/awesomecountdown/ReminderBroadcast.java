package project.awesomecountdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver implements MyConstants {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String eventTitle = intent.getStringExtra(BROADCAST_TITLE);
        long uniqueId = intent.getLongExtra(BROADCAST_ID_IDENTITY,DEFAULT_VALUE);

        Log.i("BROADCAST", "onReceive: ID = "+ uniqueId + " Event title: " + eventTitle);

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        NotificationCompat.Builder mBuilder = new Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background).setContentTitle(eventTitle)
                .setContentText("First notification").setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify((int)uniqueId, mBuilder.build());

    }
}
