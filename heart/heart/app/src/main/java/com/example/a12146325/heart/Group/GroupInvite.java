package com.example.a12146325.heart.Group;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a12146325.heart.MainActivity;
import com.example.a12146325.heart.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * Created by 빛나 on 2015-07-15.
 */
public class GroupInvite extends Activity implements View.OnClickListener {
    static int i = 0;
    Button btnback;
    Button btnsave;
    EditText email;
    Button btnadd;
    InviteTask mAuthTask=null;

    LinearLayout dynamicarea;
    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

    public void onCreate(Bundle savedInstanced) {
        super.onCreate(savedInstanced);
        setContentView(R.layout.group_invite);

        btnback = (Button) findViewById(R.id.button_back);
        btnback.setOnClickListener(this);

        btnsave = (Button) findViewById(R.id.button_save);
        btnsave.setOnClickListener(this);

        email = (EditText) findViewById(R.id.email);
        email.setOnClickListener(this);

        btnadd = (Button) findViewById(R.id.button_add);
        btnadd.setOnClickListener(this);

        dynamicarea = (LinearLayout) findViewById(R.id.dynamicarea);
        param.setMargins(130, 10, 10, 10);
    }
    private void attemptGinvite(String sender, String receiver, String gname) {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        // Store values at the time of the login attempt.

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            mAuthTask = new InviteTask(sender,receiver,gname);
            mAuthTask.execute((Void) null);
        }
    }

    public void onClick(View v) {
        if (v == btnback) {
            Intent i = new Intent(GroupInvite.this, MainActivity.class);
            startActivity(i);
            finish();
        } else if (v == btnsave) {
            Toast toast = Toast.makeText(GroupInvite.this, "그룹 초대 완료! ", Toast.LENGTH_SHORT);
            toast.show();
            Intent i = new Intent(GroupInvite.this, MainActivity.class);
            startActivity(i);
            finish();
            //그냥 다음 페이지로 넘어감,
        } else if (v == btnadd) {//이메일 입력하고 누르면 초대

            SharedPreferences pref = getSharedPreferences("PerfLogin", MODE_PRIVATE);
            String sender = pref.getString("state", "fail");//현재 유저 아이디

            String receiver = email.getText().toString();//메일 보낼 주소 get
            String groupname = pref.getString("groupname", "fail");//현재 초대하려는 그룹이름

            attemptGinvite(sender,receiver,groupname);
            TextView success = new TextView(this);//결과를 보여주는 뷰

            Boolean join = true;//join 초대장 발송할지말지

            String suc = "초대장을 발송했습니다";
            String warning = " ";
            /*
            if (receiver.isEmpty()) {//이메일칸이 비었을때
                Toast toast = Toast.makeText(GroupInvite.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT);
                toast.show();

            } else {

                //가입여부를 알아보는 커서

                    if (join) {//아직 멤버가 아닐때 실행
                        //이메일 발송부분
                        Intent ie = new Intent(Intent.ACTION_SEND);
                        ie.setType("plaine/text");
                        ie.putExtra(Intent.EXTRA_CC, new String[]{sender});
                        ie.putExtra(Intent.EXTRA_EMAIL, new String[]{receiver});//받는사람
                        ie.putExtra(Intent.EXTRA_SUBJECT, "당장만나 앱으로 초대합니다");
                        ie.putExtra(Intent.EXTRA_TEXT, "당장만나앱을 깔으세요+ 앱스토어 주소");
                        startActivity(Intent.createChooser(ie, "Send mail..."));

                        success.append(receiver + "  에게  " + suc);
                        success.setTextColor(Color.BLUE);
                        success.setTextSize(15);
                        email.setText(null);

                        dynamicarea.setLayoutParams(param);
                        dynamicarea.addView(success);

                    } else {//이미 멤버일떄 출력

                        success.append(receiver + warning);
                        success.setTextColor(Color.RED);
                        success.setTextSize(15);
                        email.setText(null);

                        dynamicarea.setLayoutParams(param);

                        dynamicarea.addView(success);
                    }

            }
            */
        }
    }
    public class InviteTask extends AsyncTask<Void, Void, Boolean> {

        private final String mSend;
        private final String mReceive;
        private final String mGname;
        private String line_ex;
        private Boolean yn = true;
        private String sid ;

        private String stateid;

        InviteTask(String send, String recei, String gname) {
            mSend = send;
            mReceive = recei;
            mGname=gname;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                //String temp= URLEncoder.encode("http://oioi9i.synology.me/Junsung/Android/ginvite.php?", "UTF-8");
                //URL url = new URL(temp);
                URL url = new URL("http://oioi9i.synology.me/Junsung/Android/ginvite.php?");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) { //초기연결 셋팅

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("charset", "UTF-8");
                    //보내주는 부분

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("sender").append("=").append(mSend).append("&");
                    buffer.append("receiver").append("=").append(mReceive).append("&");
                    buffer.append("gname").append("=").append(mGname);
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

                    String[] pieces=line_ex.split(":");
                    sid = pieces[0];
                    stateid = pieces[1];

                    if (stateid.equals("success")){
                        yn = true;
                        Log.v("ddd","true");
                    }
                    else if(stateid.equals("false")){
                        yn =false;
                        Log.v("ddd","false");
                    }
                    br.close();
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // TODO: register the new account here.
            return yn;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                /*SharedPreferences preferences = getSharedPreferences("PerfLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor  = preferences.edit();
                editor.putString("state", line_ex);
                editor.commit();
                */
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

                finish();

            } else {
                // mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                //Toast로 에러 메세지 표시
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }
}