package es.pue.eventos.presentationlayer.broadcastreceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import es.pue.eventos.R;

/**
 * Created by android-ed1 on 23/05/2016.
 */
public class RestEventBroadcastReciever extends BroadcastReceiver {

    public static String REST_RESULT_EXTRA="REST_RESULT_EXTRA";



    @Override
    public void onReceive(Context context, Intent intent) {

        Boolean result=intent.getBooleanExtra(REST_RESULT_EXTRA,false);
        Log.i("In Rest Receiver", "Result is "+result);

        if(result){
            Toast.makeText(context,"Saved data to server",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context,"Could not save data to server",Toast.LENGTH_LONG).show();
        }

        NotificationManager notificationManager=(NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_media_play)
                        .setContentTitle("Datos sincronizados")
                        .setContentText("Los datos se han bajado correctamente")
                        .setAutoCancel(true);
        notificationManager.notify(001,mBuilder.build());

    }
}
