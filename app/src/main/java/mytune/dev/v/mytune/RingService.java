package mytune.dev.v.mytune;


/**
 * Created by Vamsi on 5/11/2015.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import 	android.media.AudioManager;
import android.util.Log;
import java.io.IOException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RingService extends Service implements SensorEventListener{
    private TelephonyManager tm;
    public  static MediaPlayer ob;
    private final int mId = 1;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    public static  int i=0,k=0,r=0;
   public static String fn=null;
    public Uri myUri1;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Log.d(Constants.LOG, "onCreated called");
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(mpl, PhoneStateListener.LISTEN_CALL_STATE);
        AudioManager mManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mManager.setMode(AudioManager.MODE_IN_CALL);
        SharedPreferences sharedPref = getSharedPreferences("mytune",Context.MODE_MULTI_PROCESS);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SActive",1);
        editor.apply();
      //  ob=new MediaPlayer();

    }

    private PhoneStateListener mpl = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
         //   ob = MediaPlayer.create(RingService.this, R.raw.trouble);
            SharedPreferences sharedPref = getSharedPreferences("mytune",Context.MODE_MULTI_PROCESS);
            fn=sharedPref.getString("filename",null);
            AudioManager mManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


            try{    if(ob.isPlaying()==true){ob.stop();}}catch(Exception e){e.printStackTrace();}

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    r=1;
                break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                 if(r!=1 && mManager.isBluetoothA2dpOn()==false){ //  ob =new MediaPlayer();

                     mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                     mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                     mManager.setMode(AudioManager.MODE_IN_CALL);
                     try{if(fn==null){
                         myUri1 = Uri.parse("/storage/ext_sd/music/keepup.mp3");}
                     else {
                         myUri1 = Uri.parse(fn);
                     }
                         ob=new MediaPlayer();
                         ob.setDataSource(RingService.this,myUri1);
                         ob.prepare();}catch (Exception e){e.printStackTrace();}
                    ob.setLooping(true);
                     mManager.setMode(AudioManager.MODE_IN_CALL);
                    ob.start();
                     mManager.setMode(AudioManager.MODE_IN_CALL);
                    mSensorManager.registerListener(RingService.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    i=0;k=1;
                          Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(RingService.this)
                                    .setSmallIcon(R.drawable.icon)
                                    .setContentTitle("My Tune")
                                    .setContentText("Toggle the proximity sensor to stop tune!")
                                    .setColor(1).setLargeIcon(largeIcon)
                                    .setAutoCancel(true);
                    //  .addAction(R.drawable.stop, "STOP", pIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify(mId, mBuilder.build());}
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    // Toast.makeText(RingService.this, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
                    NotificationManager mNotificationManager1 =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager1.cancel(mId);
                   if(k==1){ob.stop();k=0;}
                    if(r==1){r=0;}
                    mSensorManager.unregisterListener(RingService.this);

                    try{ob.stop(); ob.reset();
                        ob.release();}catch(Exception e){e.printStackTrace();}
                    break;
                default:


            }

        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = getSharedPreferences("mytune",Context.MODE_MULTI_PROCESS);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SActive",0);
        editor.apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


//try{           fn = intent.getStringExtra("filename");}catch (Exception e){e.printStackTrace();}
     //   Toast.makeText(RingService.this, "Filepath"+intent.getStringExtra("filename"), Toast.LENGTH_SHORT).show();
        return 0;
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public interface Constants {
        String LOG = "mytune.dev.v.mytune";
    }

    public void startPlay(){
      if (!ob.isPlaying()) {
          ob.start();
      }
    }
    public void stopPlay() {
        if (ob.isPlaying()) {
            ob.stop();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.values[0] < event.sensor.getMaximumRange()) {
            i=1;
            }else{
                if (ob.isPlaying() && i==1) {
                    ob.stop();
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(mId);
                }
            }

        }
    }

