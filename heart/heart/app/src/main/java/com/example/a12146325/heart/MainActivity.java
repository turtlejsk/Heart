package com.example.a12146325.heart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import com.example.a12146325.heart.Group.GroupCreationActivity;
import com.example.a12146325.heart.Group.GroupInvite;
import com.example.a12146325.heart.Group.GroupListActivity;
import com.example.a12146325.heart.Heartrate.HeartRateMonitor;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.acl.Group;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{
    private SharedPreferences preferences = null;
    String mid, line_ex;
    String hr;
    TextView tv;
    URL murlu, murle, murlx;
    String[] trName, trComp, trSet = null;
    private UserGetTask mGetTask = null;
    private ExerGetTask mEGetTask = null;
    private ExerDelTask mEDelTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = getSharedPreferences("PerfLogin", MODE_PRIVATE);
        mid = preferences.getString("state", "fail");

        try {
            murlu = new URL("http://oioi9i.synology.me/Junsung/Android/user.php?");
            mGetTask = new UserGetTask(mid, murlu);
            mGetTask.execute((Void) null);
        } catch (Exception e) {

        }


        try {
            murle = new URL("http://oioi9i.synology.me/Junsung/Android/exer.php?");
            mEGetTask = new ExerGetTask(mid, murle);
            mEGetTask.execute((Void) null);
        } catch (Exception e) {

        }
        try {
            murlx = new URL("http://oioi9i.synology.me/Junsung/Android/exerdel.php?");
        }catch (Exception e){

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()){
            case R.id.setting:
                try {
                    mEDelTask = new ExerDelTask(mid, murlx);
                    mEDelTask.execute((Void) null);
                } catch (Exception e) {
                }
                intent = new Intent(this, Train.class);
                break;
            case R.id.checking:
                intent = new Intent(this, HeartRateMonitor.class);
                intent.putExtra("check","check");
                break;
            case R.id.start:
                intent = new Intent(this, StartTrain.class);
                //intent.putExtra("list", trList);
                //intent.putExtra("set", trSet);
                break;

            case R.id.groupC:
                intent = new Intent(this, GroupCreationActivity.class);
                break;
            case R.id.groupL:
                intent = new Intent(this, GroupListActivity.class);
                break;
            default:
                intent = null;
        }
        try {
            startActivity(intent);
        }catch (NullPointerException e){

        }
    }


    public class UserGetTask extends AsyncTask<Void, Void, Boolean> {

        UserGetTask(String id, URL url) {
            mid = id;
            murlu = url;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                TextView rate = (TextView) findViewById(R.id.heartrate);
                rate.setText("Your default heartRate : " + hr);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                HttpURLConnection conn = (HttpURLConnection) murlu.openConnection();
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
                    buffer.append("_id").append("=").append(mid);

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    PrintWriter writer = new PrintWriter(out);
                    writer.write(buffer.toString());
                    writer.flush();
                    writer.close();
                    out.close();
                    //읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader
                            (conn.getInputStream()));
                    line_ex = br.readLine();

                    String[] pieces = line_ex.split(":");

                    hr = pieces[2].toString();
                    br.close();
                    conn.disconnect();

                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
            return true;
        }


    }

    public class ExerGetTask extends AsyncTask<Void, Void, Boolean> {

        ExerGetTask(String id, URL url) {
            mid = id;
            murle = url;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean && trName!=null) {
                for (int a = 0; a < trName.length; a++) {
                    switch (trName[a]) {
                        case "1":
                            trName[a] = "Running";
                            break;
                        case "2":
                            trName[a] = "Cycle";
                            break;
                        case "3":
                            trName[a] = "Boxing";
                            break;
                        case "4":
                            trName[a] = "Golf";
                            break;
                    }
                }
                TextView tv = (TextView) findViewById(R.id.list);
                for (int i = 0; i < trSet.length; i++) {
                    tv.setText(tv.getText() + "\n" + trName[i] + ":" + trComp[i] + "/" + trSet[i]);
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                HttpURLConnection conn = (HttpURLConnection) murle.openConnection();
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
                    buffer.append("_id").append("=").append(mid);

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    PrintWriter writer = new PrintWriter(out);
                    writer.write(buffer.toString());
                    writer.flush();
                    writer.close();
                    out.close();
                    //읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader
                            (conn.getInputStream()));
                    line_ex = br.readLine();
                    Log.e("AAAAAAAAAAAaa", line_ex);
                    String[] pieces = line_ex.split(":");
                    trName = new String[pieces.length / 3];
                    trComp = new String[pieces.length / 3];
                    trSet = new String[pieces.length / 3];
                    for (int i = 0; i < pieces.length; i++) {
                        if (i % 3 == 0) {
                            trName[i / 3] = pieces[i];
                        } else if (i % 3 == 1) {
                            trComp[i / 3] = pieces[i];
                        } else trSet[i / 3] = pieces[i];
                    }
                    br.close();
                    conn.disconnect();

                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
            return true;
        }


    }

    public class ExerDelTask extends AsyncTask<Void, Void, Boolean> {

        ExerDelTask(String id, URL url) {
            mid = id;
            murlx = url;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
            }

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                HttpURLConnection conn = (HttpURLConnection) murlx.openConnection();
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
                    buffer.append("_id").append("=").append(mid);

                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    PrintWriter writer = new PrintWriter(out);
                    writer.write(buffer.toString());
                    writer.flush();
                    writer.close();
                    out.close();
                    //읽어오는 부분
                    BufferedReader br = new BufferedReader(new InputStreamReader
                            (conn.getInputStream()));
                    line_ex = br.readLine();
                    Log.e("AAAAAAAAAAAaa", line_ex);
                    br.close();
                    conn.disconnect();

                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
            return true;
        }


    }
}
