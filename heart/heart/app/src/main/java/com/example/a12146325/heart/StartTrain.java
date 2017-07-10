package com.example.a12146325.heart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a12146325.heart.Heartrate.HeartRateMonitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class StartTrain extends AppCompatActivity{
    int h1,h2,m1,m2,s1,s2 = 0;
    int h,m,s = 0;
    TimeThread thread;
    TextView tv, tv2, hr1,hr2,min1,min2,sec1,sec2;
    Button check, pause, start, finish;
    Boolean state = false;
    Boolean stopped = false;
    //String[] trList;
    //int[] trSet;
    private SharedPreferences preferences = null;
    String[] trName, trComp, trSet;
    URL murle;
    String mid;
    int comp = 0;
    ExerGetTask mEGetTask;
    Intent fromAct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferences = getSharedPreferences("PerfLogin",MODE_PRIVATE);
        mid = preferences.getString("state","fail");


        try{
            murle = new URL("http://oioi9i.synology.me/Junsung/Android/exer.php?");
            mEGetTask = new ExerGetTask(mid, murle);
            mEGetTask.execute((Void)null);
        }catch (Exception e){

        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_train);

        tv = (TextView)findViewById(R.id.prac);
        tv2 = (TextView)findViewById(R.id.setnum);

        hr1 = (TextView)findViewById(R.id.hr1);
        min1 = (TextView)findViewById(R.id.min1);
        sec1 = (TextView)findViewById(R.id.sec1);

        hr2 = (TextView)findViewById(R.id.hr2);
        min2 = (TextView)findViewById(R.id.min2);
        sec2 = (TextView)findViewById(R.id.sec2);

        check = (Button)findViewById(R.id.check);
        pause = (Button)findViewById(R.id.pause);
        start = (Button)findViewById(R.id.start);
        finish = (Button)findViewById(R.id.finish);
        hr1.setText(tString(h1));
        min1.setText(tString(m1));
        sec1.setText(tString(s1));
        hr2.setText(tString(h2));
        min2.setText(tString(m2));
        sec2.setText(tString(s2));


        thread = new TimeThread(handler);
        /*try{
            //스레드 시작 시키기
            thread.start();
            state = true;
        }catch(Exception e){ //익셉션 발생하면
            //스레드 정지
            thread.stopForever();
            //스레드 객체 다시 생성
            thread=new TimeThread(handler);
            //스레드를 시작한다.
            thread.start();
            state = true;
        }*/
    }

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){

                s++;
                if(s==60){
                    m++;
                    s=0;
                    if(m==60){
                        h++;
                        m=0;
                    }
                }
            if(stopped){
                s=0;
            }
            h1 = h/10;
            h2 = h%10;
            m1 = m/10;
            m2 = m%10;
            s1 = s/10;
            s2 = s%10;

            hr1.setText(tString(h1));
            min1.setText(tString(m1));
            sec1.setText(tString(s1));
            hr2.setText(tString(h2));
            min2.setText(tString(m2));
            sec2.setText(tString(s2));

            if(m>=1){ // 운동 당 조건으로 변경
                check.setVisibility(View.VISIBLE);
            }

        }
    };


    public String tString(int n){
        String tS = Integer.toString(n);
        return tS;
    }
    public void onClick(View v){

        new AlertDialog.Builder(v.getContext()).setTitle("Input new size of preview").setIcon(R.mipmap.ic_launcher)
                .setItems(trName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv.setText(trName[which].toString());
                        comp = which;
                        tv2.setText(trComp[which]+"/"+trSet[which]+" Sets");
                        h=m=s=0;
                        thread.stopForever();
                        if (pause.getText() == "Resume") {
                            pause.setText("Pause");
                        }
                        start.setText("Start");
                        state = false;
                        h=m=s=0;
                        check.setVisibility(View.INVISIBLE);
                        stopped = true;

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
    public void on(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.pause:
                if(start.getText()=="Stop") {
                    stopped = false;
                    if (state) {
                        thread.pauseNResume(state);
                        pause.setText("Resume");
                        state = false;
                    } else {
                        thread.pauseNResume(state);
                        pause.setText("Pause");
                        state = true;
                    }
                }
                break;
            case R.id.start:
                if(state || start.getText()=="Stop"){
                    thread.stopForever();
                    if (pause.getText() == "Resume") {
                        pause.setText("Pause");
                    }
                    start.setText("Start");
                    state = false;
                    h=m=s=0;
                    check.setVisibility(View.INVISIBLE);
                    stopped = true;
                }else {
                    thread = new TimeThread(handler);
                    h=m=s=0;
                    try {
                        thread.start();
                        if (pause.getText() == "Resume") {
                            pause.setText("Pause");
                        }
                        start.setText("Stop");
                        state = true;
                    }catch(Exception e){

                    }
                    stopped = false;
                }
                break;
            case R.id.finish:
                thread.stopForever();
                h=m=s=0;
                pause.setText("Pause");
                start.setText("Start");
                check.setVisibility(View.VISIBLE);
                state = false;
                stopped = true;
                break;
            case R.id.check:

                intent = new Intent(this, HeartRateMonitor.class);
                intent.putExtra("comp",trComp[comp]);
                switch(trName[comp]) {
                    case "Running":
                        intent.putExtra("exerID", "1");
                        break;
                    case "Cycle":
                        intent.putExtra("exerID", "2");
                        break;
                    case "Boxing":
                        intent.putExtra("exerID", "3");
                        break;
                    case "Golf":
                        intent.putExtra("exerID", "4");
                        break;
                        default: break;
                }
                startActivity(intent);
                stopped = false;
                break;
            default:
                break;

        }
    }

    public class ExerGetTask extends AsyncTask<Void, Void, Boolean> {

        ExerGetTask(String id, URL url){
            mid = id;
            murle = url;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean && trName != null){
                for(int a = 0; a<trName.length; a++){
                    switch(trName[a]){
                        case "1":
                            trName[a]="Running";
                            break;
                        case "2":
                            trName[a]="Cycle";
                            break;
                        case "3":
                            trName[a]="Boxing";
                            break;
                        case "4":
                            trName[a]="Golf";
                            break;
                    }
                }


                fromAct = getIntent();
                try {
                    if (fromAct.getExtras().getString("name") != null) {
                        int which = Integer.parseInt(fromAct.getExtras().getString("name"))-1;
                        tv.setText(trName[which].toString());
                        comp = which;
                        tv2.setText(trComp[which] + "/" + trSet[which] + " Sets");
                        h = m = s = 0;
                        thread.stopForever();
                        if (pause.getText() == "Resume") {
                            pause.setText("Pause");
                        }
                        start.setText("Start");
                        state = false;
                        h = m = s = 0;
                        check.setVisibility(View.INVISIBLE);
                        stopped = true;

                    }
                    else{

                    }
                }catch (Exception e){

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
                    String line_ex = br.readLine();
                    Log.e("AAAAAAAAAAAaa", line_ex);
                    String[] pieces = line_ex.split(":");
                    Log.e("AAAAAAAAAAAaa", pieces[2]);
                    trName = new String[pieces.length/3];
                    trComp = new String[pieces.length/3];
                    trSet = new String[pieces.length/3];
                    for(int i = 0; i<pieces.length; i++){
                        if(i%3==0){
                            trName[i/3]=pieces[i];
                        }
                        else if(i%3==1){
                            trComp[i/3]=pieces[i];
                        }
                        else trSet[i/3]=pieces[i];
                    }
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
