package com.example.DiaShield;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class CalculateScreen extends AppCompatActivity {
    Button btnNext, btnRespirat, btnHrt;
    String usr_nme ="";
    int thresh = 12;
    int frameInd = 0;
    int totframes = 0;
    int hrtRateCnt =0;
    VideoView vidView;
    TextView viewRespi;
    ArrayList<Float> colorRed = new ArrayList<Float>();
    private static int VID_REQUEST =101;
    private static int REQ_PERM =1;
    private static int READ_PERM = 102;
    private static int WRITE_PERM = 103;
    private Uri vidUri;
    Intent vidIntent;
    DB_Structure instanceDB = new DB_Structure();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == VID_REQUEST && resultCode == RESULT_OK){
            Uri VidUri = data.getData();
            String samplUri = VidUri.toString();
            vidUri = Uri.parse(samplUri);
            videoPlayback();
            HRCalc instanceHRCalc = new HRCalc();
            instanceHRCalc.execute(vidUri);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_measure_screen);
        super.onCreate(savedInstanceState);

        viewRespi = (TextView) findViewById(R.id.measureResp);
        vidView = findViewById(R.id.vidView);

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            usr_nme ="";
        }else{
            usr_nme = extras.getString("name");
        }
        btnNext = findViewById(R.id.nextBtn);
        btnRespirat = findViewById(R.id.RespBtn);
        btnHrt = findViewById(R.id.heartBtn);
        buttonClick();
        measureResp();
        measureHR();
    }
    private BroadcastReceiver bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String str = (String) intent.getExtras().get("success").toString();
            instanceDB.setRESPIRATORY_RATE(Integer.parseInt(str));
            viewRespi.setText("Respiratory Rate: " +str);
        }
    };

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("message"));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }
    public void buttonClick(){
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(instanceDB);
                Intent symptomsIntent = new Intent(getApplicationContext(), SymptomScreen.class);
                symptomsIntent.putExtra("myDBObject", instanceDB);
                System.out.println(instanceDB + "sent");
                startActivity(symptomsIntent);
            }
        });
    }
    public void measureResp(){
        btnRespirat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respiratoryIntentService = new Intent(getApplicationContext(), CalculateResp.class);
                startService(respiratoryIntentService);
                viewRespi.setText("Respiratory Rate: Calculating..");
            }
        });
    }
    public void measureHR(){
        btnHrt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                vidIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                vidIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
                vidIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                vidIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if (vidIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(vidIntent, VID_REQUEST);
                            }
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_PERM);
                            if (vidIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(vidIntent, VID_REQUEST);
                            }
                        }
                    }else{
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERM);
                    }
                }else{
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERM);
                }

            }
        });
    }
    public void videoPlayback(){
        vidView.setVideoURI(vidUri);
        vidView.setMediaController(new MediaController(this));
        vidView.requestFocus();
        vidView.start();
        Toast.makeText(getApplicationContext(), "Calculating Heart Rate...", Toast.LENGTH_LONG).show();
        TextView viewHeart = findViewById(R.id.measureHeart);
        viewHeart.setText("Heart Rate: Calculating...");
    }
    class HRCalc extends AsyncTask<Uri, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected Void doInBackground(Uri... uris) {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            try {
                metaRetriever.setDataSource(getApplicationContext(), vidUri);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            MediaPlayer forTime = MediaPlayer.create(getBaseContext(), vidUri);
            int videoDuration = forTime.getDuration();
            int processFramesPerSec = 12;
            int processtime = 100000;
            totframes = (int) Math.floor(videoDuration/1000) * processFramesPerSec;
            hrtRateCnt = 1;
            frameInd = 1;
            while(frameInd < totframes){
                float currentColor = 0f;
                Bitmap currentFrameBitmap = metaRetriever.getFrameAtTime(processtime,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                processtime = processtime + 100000;
                Log.d("HeartRateCalculatorTask", "Processing frame number"+processtime);

                int i = 450;
                while(i <= 550){
                    int j = 900;
                    while(j < 1200){
                        currentColor = currentColor + Color.red(currentFrameBitmap.getPixel(i,j));
                        j++;
                    }
                    i++;
                }

                float previousColor = 1f;
                boolean isArrayListEmpty = (colorRed.size()!=0);
                if(isArrayListEmpty!=false){
                    int currentSize = colorRed.size();
                    previousColor = colorRed.get(currentSize - 1);
                }

                boolean isCountable = Math.abs(previousColor - currentColor) > thresh;
                if(isCountable == true){
                    hrtRateCnt++;
                }

                colorRed.add(currentColor);

                Log.d("HeartRateCalculatorTask", "Processing frame number"+processtime);
                frameInd++;

            }

            metaRetriever.release();
            Log.d("HeartRateCalculatorTask", "Sending heart rate"+ hrtRateCnt);

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TextView heartRateValue = findViewById(R.id.measureHeart);
            heartRateValue.setText("Heart Rate: " + String.valueOf(hrtRateCnt));
            instanceDB.setHEART_RATE(hrtRateCnt);
            //uploadData(outputHeartRate);
        }
    }

}