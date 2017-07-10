package com.example.a12146325.heart.Heartrate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.a12146325.heart.MainActivity;
import com.example.a12146325.heart.R;
import com.example.a12146325.heart.StartTrain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartRateMonitor extends AppCompatActivity {


    private SharedPreferences preferences = null;


    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    static Button act;
    String[] trComp, trName;
    int x = 0;
    Intent intent;
    CompUpTask mCompUp = null;
    DefHeartTask mDefHr = null;
    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView text = null;
    String mid;
    URL murl, murlh;
    private static PowerManager.WakeLock wakeLock = null;
    static String hr;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;

    /**
     * {@inheritDoc}
     */

    public void onClick(View v) {
        try {
            if (v.getId() == R.id.insure) {

                mCompUp = new CompUpTask(mid, murl);
                mCompUp.execute((Void) null);

                Intent intentM = new Intent(this, StartTrain.class);
                intentM.putExtra("name", intent.getExtras().getString("exerID"));
                startActivity(intentM);

            } else if (v.getId() == R.id.send) {
                mDefHr = new DefHeartTask(mid, murlh);
                mDefHr.execute((Void) null);

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }catch (Exception e){

            mDefHr = new DefHeartTask(mid, murlh);
            mDefHr.execute((Void) null);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("PerfLogin", MODE_PRIVATE);
        mid = preferences.getString("state", "fail");
        intent = getIntent();
        try {
            murl = new URL("http://oioi9i.synology.me/Junsung/Android/compup.php?");
            murlh = new URL("http://oioi9i.synology.me/Junsung/Android/sethr.php?");
        }catch (Exception e){

        }
            preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        image = findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        act = (Button) findViewById(R.id.insure);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();

        camera = Camera.open();

        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();

        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private static Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            // Log.i(TAG, "imgAvg="+imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    // Log.d(TAG, "BEAT!! beats="+beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                image.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                // Log.d(TAG,
                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                //int default_heartrate = db에서 값 받아오기
                text.setText(String.valueOf(beatsAvg));
                hr = String.valueOf(beatsAvg);
                try {
                    if (beatsAvg > 0/*디폴트값*/) {
                        act.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){

                }
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {

            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

    public class CompUpTask extends AsyncTask<Void, Void, Boolean> {

        CompUpTask(String id, URL url){
            mid = id;
            murl = url;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                HttpURLConnection conn = (HttpURLConnection) murl.openConnection();
                // Simulate network access.
                if (conn != null) { //초기연결 셋팅
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("charset", "UTF-8");
                    //보내주는 부분

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("_id").append("=").append(mid).append("&").append("exerID").append("=").append(intent.getExtras().getString("exerID")).append("&")
                            .append("comp").append("=").append(Integer.parseInt(intent.getExtras().getString("comp"))+1);

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    PrintWriter writer = new PrintWriter(out);
                    writer.write(buffer.toString());
                    writer.flush();
                    writer.close();
                    out.close();
                    //읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    br.close();
                    conn.disconnect();

                }
            }catch (Exception ex) {
                ex.printStackTrace();

            }
            return true;
        }


    }
    public class DefHeartTask extends AsyncTask<Void, Void, Boolean> {

        DefHeartTask(String id, URL url){
            mid = id;
            murl = url;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                HttpURLConnection conn = (HttpURLConnection) murl.openConnection();
                // Simulate network access.
                if (conn != null) { //초기연결 셋팅
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("charset", "UTF-8");
                    //보내주는 부분

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("_id").append("=").append(mid).append("&").append("hr").append("=").append(hr);

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    PrintWriter writer = new PrintWriter(out);
                    writer.write(buffer.toString());
                    writer.flush();
                    writer.close();
                    out.close();
                    //읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    br.close();
                    conn.disconnect();

                }
            }catch (Exception ex) {
                ex.printStackTrace();

            }
            return true;
        }


    }
}
