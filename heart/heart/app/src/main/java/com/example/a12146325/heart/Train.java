package com.example.a12146325.heart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Train extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    CheckBox checkBox_running, checkBox_cycle, checkBox_boxing, checkBox_golf;
    EditText running_set, cycle_set, boxing_set, golf_set;
    TextView r,c,bb,g;
    String[] UserID, trName, trSet, trComp;
    private SharedPreferences preferences = null;
    String mid;
    int count = 0;
    URL murle;
    int upcount = 0;
    ExerUpTask mEUpTask;
    int checked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train);
        try {
            murle = new URL("http://oioi9i.synology.me/Junsung/Android/exerup.php?");
        }catch (Exception e){
            Log.e("AAAAA","AAAAA");
        }
        preferences = getSharedPreferences("PerfLogin",MODE_PRIVATE);
        mid = preferences.getString("state","fail");

        checkBox_running = (CheckBox)findViewById(R.id.running);
        checkBox_running.setOnCheckedChangeListener(this);
        checkBox_cycle = (CheckBox)findViewById(R.id.cycle);
        checkBox_cycle.setOnCheckedChangeListener(this);
        checkBox_boxing = (CheckBox)findViewById(R.id.boxing);
        checkBox_boxing.setOnCheckedChangeListener(this);
        checkBox_golf = (CheckBox)findViewById(R.id.golf);
        checkBox_golf.setOnCheckedChangeListener(this);
        running_set = (EditText)findViewById(R.id.runningset);
        cycle_set = (EditText)findViewById(R.id.cycleset);
        boxing_set = (EditText)findViewById(R.id.boxingset);
        golf_set = (EditText)findViewById(R.id.golfset);
        r=(TextView)findViewById(R.id.textView2);
        c=(TextView)findViewById(R.id.textView3);
        bb=(TextView)findViewById(R.id.textView4);
        g=(TextView)findViewById(R.id.textView5);


    }

    public void set(View v){
        try{
            checked = 0;
            UserID = new String[count];
            trName = new String[count];
            trComp = new String[count];
            trSet = new String[count];
            if(checkBox_running.isChecked()){
                UserID[checked] = mid;
                trName[checked] = "1";
                trComp[checked] = "0";
                trSet[checked] = running_set.getText().toString();

                mEUpTask = new ExerUpTask(mid, murle, checked);
                mEUpTask.execute((Void) null);

                checked++;
            }
            if(checkBox_cycle.isChecked()){
                UserID[checked] = mid;
                trName[checked] = "2";
                trComp[checked] = "0";
                trSet[checked] = cycle_set.getText().toString();
                mEUpTask = new ExerUpTask(mid, murle, checked);
                mEUpTask.execute((Void)null);
                checked++;
            }
            if(checkBox_boxing.isChecked()){
                UserID[checked] = mid;
                trName[checked] = "3";
                trComp[checked] = "0";
                trSet[checked] = boxing_set.getText().toString();
                mEUpTask = new ExerUpTask(mid, murle, checked);
                mEUpTask.execute((Void)null);
                checked++;
            }
            if(checkBox_golf.isChecked()){
                UserID[checked] = mid;
                trName[checked] = "4";
                trComp[checked] = "0";
                trSet[checked] = golf_set.getText().toString();
                mEUpTask = new ExerUpTask(mid, murle, checked);
                mEUpTask.execute((Void)null);
                checked++;
            }
            Log.e("0",trName[0]+trComp[0]+trSet[0]);


        }catch (Exception e){

        }


        Intent intent = new Intent(this, MainActivity.class);
        /*int count = 0;
        try {
            if (checkBox_running.isChecked()) {
                count++;
                trName.add("1");
                trSet.add(Integer.parseInt(running_set.getText().toString()));
            }
            if (checkBox_cycle.isChecked()) {
                count++;
                trName.add("Cycle");
                trSet.add(Integer.parseInt(cycle_set.getText().toString()));
            }
            if (checkBox_boxing.isChecked()) {
                count++;
                trName.add("Boxing");
                trSet.add(Integer.parseInt(boxing_set.getText().toString()));
            }
            if (checkBox_golf.isChecked()) {
                count++;
                trName.add("Golf");
                trSet.add(Integer.parseInt(golf_set.getText().toString()));
            }

            intent.putExtra("count", count);
            intent.putExtra("Name",trName);
            intent.putExtra("Set",trSet);*/
            startActivity(intent);
/*
        }catch (Exception e){
            count = 0;
            trName.clear();
            trSet.clear();

        }*/
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b) {
            switch (compoundButton.getId()) {//체크박스 누를때 운동량 설정창으로
                case R.id.running:
                    count++;
                    running_set.setVisibility(View.VISIBLE);
                    r.setVisibility(View.VISIBLE);
                    break;
                case R.id.cycle:
                    count++;
                    cycle_set.setVisibility(View.VISIBLE);
                    c.setVisibility(View.VISIBLE);
                    break;
                case R.id.boxing:
                    count++;
                    boxing_set.setVisibility(View.VISIBLE);
                    bb.setVisibility(View.VISIBLE);
                    break;
                case R.id.golf:
                    count++;
                    golf_set.setVisibility(View.VISIBLE);
                    g.setVisibility(View.VISIBLE);
                    break;
            }
        }
        else{
            switch(compoundButton.getId()){
                case R.id.running:
                    count--;
                    running_set.setVisibility(View.INVISIBLE);
                    r.setVisibility(View.INVISIBLE);
                    break;
                case R.id.cycle:
                    count--;
                    cycle_set.setVisibility(View.INVISIBLE);
                    c.setVisibility(View.INVISIBLE);
                    break;
                case R.id.boxing:
                    count--;
                    boxing_set.setVisibility(View.INVISIBLE);
                    bb.setVisibility(View.INVISIBLE);
                    break;
                case R.id.golf:
                    count--;
                    golf_set.setVisibility(View.INVISIBLE);
                    g.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    public class ExerUpTask extends AsyncTask<Void, Void, Boolean> {

        private int ul = 0;
        ExerUpTask(String id, URL url, int x){
            mid = id;
            murle = url;
            upcount = x;
        }


        @Override
        protected void onPreExecute() {
            ul = upcount;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
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
                        buffer.append("_id").append("=").append(mid).append("&").append("exerID").append("=").append(trName[ul]).append("&")
                                .append("comp").append("=").append(trComp[ul]).append("&").append("setN").append("=").append(trSet[ul]);

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
