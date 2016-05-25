package es.pue.eventos.presentationlayer.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import es.pue.eventos.R;
import es.pue.eventos.presentationlayer.activities.MapsActivity;
import es.pue.eventos.presentationlayer.androidextends.application.PueAndroidApplication;
import es.pue.eventos.presentationlayer.listeners.GeoPositionListener;

/**
 * Created by android-ed1 on 11/05/2016.
 */

@SuppressWarnings("MissingPermission")
public class GeoPositionService extends Service {

    public static final int NOTIFICATION_ID=1000;

    private LocationManager locationManager;
    private GeoPositionListener geoPositionListener;

    @Override
    public void onCreate() {
        super.onCreate();


        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        geoPositionListener= new GeoPositionListener(
                ((PueAndroidApplication)getApplication()).getAsistenciaActual());

        iniciarPosicionamiento();
    }



    private void iniciarPosicionamiento(){

        NotificationManager notificationManager=
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setContentTitle("Asistencia en curso")
                .setContentText("Clica para ver el mapa")
                .setSmallIcon(android.R.drawable.ic_menu_compass);

        Intent mapsIntent=new Intent(this,MapsActivity.class);
        PendingIntent pendingMapsIntent=PendingIntent.getActivity(this,0,mapsIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingMapsIntent);

        notificationManager.notify(NOTIFICATION_ID,builder.build());

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0,
                geoPositionListener);
    }

    @Override
    public void onDestroy() {

        NotificationManager notificationManager=
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        locationManager.removeUpdates(geoPositionListener);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
