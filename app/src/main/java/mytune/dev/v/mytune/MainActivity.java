package mytune.dev.v.mytune;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.net.URISyntaxException;


public class MainActivity extends ActionBarActivity  {
    final static int RQS_OPEN_AUDIO_MP3 = 1;
    public String filename;
    public String fn=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //show
        MediaMetadataRetriever metaRetriver;
        byte[] art;
        final ImageView album_art = (ImageView) findViewById(R.id.album_art);

        //core
        TextView tv4 = (TextView) findViewById(R.id.Text4);
        SharedPreferences sharedPref = getSharedPreferences("mytune", Context.MODE_MULTI_PROCESS);


        fn = sharedPref.getString("filename", "k");
        final SharedPreferences.Editor editor = sharedPref.edit();
        //Enabling service on preference
        if (sharedPref.getInt("enable_mytune", 0) == 1 && sharedPref.getInt("SActive",0) != 1) {
            startService(new Intent(getBaseContext(), RingService.class));

        } else {
            stopService(new Intent(getBaseContext(), RingService.class));

        }
        //Checking status of preferencs in menu
        final Switch repeatChkBx = (Switch) findViewById(R.id.switch1);
        if (sharedPref.getInt("enable_mytune", 0) == 1) {
            repeatChkBx.setChecked(true);
        } else {
            repeatChkBx.setChecked(false);
        }
        //Mytune Enabling listener
        repeatChkBx.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView tv2 = (TextView) findViewById(R.id.Text2);
                if (fn == null) {
                    //  Toast.makeText(MainActivity.this,"Please select a MP3 file above",Toast.LENGTH_LONG);
                }
                if (isChecked) {

                    editor.putInt("enable_mytune", 1);
                    Intent mIntent = new Intent(MainActivity.this, RingService.class);
                    mIntent.putExtra("filename", getFileName());
                    startService(mIntent);
                    editor.apply();
                    tv2.setText("MyTune is Enabled");
                } else {
                    editor.putInt("enable_mytune", 0);
                    stopService();
                    editor.apply();
                    tv2.setText("MyTune is Disabled");
                }

            }
        });


        TextView tv3 = (TextView) findViewById(R.id.Text3);
        tv3.setOnTouchListener(new View.OnTouchListener()

                               {


                                   public boolean onTouch(View v, MotionEvent event) {
                                       // TODO Auto-generated method stub


                                       //do stuff here
                                       Intent intent = new Intent();
                                       intent.setType("audio/mp3");
                                       intent.setAction(Intent.ACTION_GET_CONTENT);
                                       startActivityForResult(Intent.createChooser(
                                               intent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);


                                       return false;
                                   }
                               }

        );
       if(!(fn=="k") || fn!=null){tv4.setText(fn);}
        if (fn != "k") {
            try {


                metaRetriver = new MediaMetadataRetriever();
                metaRetriver.setDataSource(fn);
                art = metaRetriver.getEmbeddedPicture();
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                album_art.setImageBitmap(songImage);
                //   Palette p = Palette.generate(songImage);
             /*   Palette.generateAsync(songImage, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // Here's your generated palette
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        if (swatch != null) {
                            RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.rl1);
                            rl1.setBackgroundColor(swatch.getRgb());
                            //  titleView.setTextColor(swatch.getTitleTextColor());
                        }
                    }
                });*/
                Blur b=new Blur();
                Bitmap blursongimage=b.fastblur(MainActivity.this,songImage,10);
                ImageView bluimg=(ImageView)findViewById(R.id.bluimg);
                bluimg.setImageBitmap(blursongimage);

            } catch (Exception e) {
                e.printStackTrace();
                album_art.setBackgroundColor(Color.GRAY);
                RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.rl1);
                rl1.setBackgroundColor(Color.GRAY);
            }
        }else{
            int id = getResources().getIdentifier("mytune.dev.v.mytune:drawable/" + "defaultcover", null, null);
            album_art.setImageResource(id);
            RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.rl1);
            rl1.setBackgroundColor(getResources().getColor(R.color.primaryDef));

        }


    }

      @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) if (requestCode == RQS_OPEN_AUDIO_MP3) {

                Uri audioFileUri = data.getData();
                try {
                    String path = getPath(this, audioFileUri);
                    TextView tv4 = (TextView) findViewById(R.id.Text4);
                    SharedPreferences sharedPref = getSharedPreferences("mytune", Context.MODE_MULTI_PROCESS);
                    final SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("filename", path);
                    editor.apply();
                    tv4.setText(sharedPref.getString("filename", null));
                    MediaMetadataRetriever metaRetriver;
                    byte[] art;
                    ImageView album_art = (ImageView) findViewById(R.id.album_art);
                    metaRetriver = new MediaMetadataRetriever();
                    metaRetriver.setDataSource(sharedPref.getString("filename", null));
                    art = metaRetriver.getEmbeddedPicture();
                    Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                    album_art.setImageBitmap(songImage);
                    //Background color for art
                /*    Palette.generateAsync(songImage, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            // Here's your generated palette
                            Palette.Swatch swatch = palette.getVibrantSwatch();
                            if (swatch != null) {
                                RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.rl1);
                                rl1.setBackgroundColor(swatch.getRgb());
                                //  titleView.setTextColor(swatch.getTitleTextColor());
                            }
                        }
                    });*/
                    Blur b=new Blur();
                    Bitmap blursongimage=b.fastblur(MainActivity.this,songImage,10);
                    ImageView bluimg=(ImageView)findViewById(R.id.bluimg);
                    bluimg.setImageBitmap(blursongimage);
                    stopService();
                    startService();
                } catch (Exception e) {
                    e.printStackTrace();
                    ImageView album_art = (ImageView) findViewById(R.id.album_art);
                    album_art.setBackgroundColor(Color.GRAY);
                }

            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), RingService.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), RingService.class));
    }


    public String getFileName(){
        SharedPreferences sharedPref = getSharedPreferences("mytune",Context.MODE_PRIVATE);
        filename=sharedPref.getString("filename",null);
        return filename;
    }


    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
