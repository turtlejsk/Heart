package com.example.a12146325.heart;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by turtl on 2016-12-09.
 */

public class JoinActivity extends AppCompatActivity implements View.OnClickListener{
    Button btn_back, btn_save, btn_e_confirm;
    static EditText et_email;
    EditText et_passwd, et_pwconfirm;
    TextView tv_possiblemail;
    private UserJoinTask mAuthTask = null;
    int a;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        et_email = (EditText) findViewById(R.id.et_email);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        et_pwconfirm = (EditText) findViewById(R.id.et_pwconfirm);


        tv_possiblemail = (TextView) findViewById(R.id.tv_possiblemail);

        btn_back = (Button) findViewById(R.id.button_back);
        btn_back.setOnClickListener(this);

        btn_save = (Button) findViewById(R.id.button_save);
        btn_save.setOnClickListener(this);

        btn_e_confirm = (Button) findViewById(R.id.button_e_confirm);
        btn_e_confirm.setOnClickListener(this);

    }
    private void attemptJoin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        // Store values at the time of the login attempt.
        String email =  et_email.getText().toString();
        String password = et_passwd.getText().toString();

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

            mAuthTask = new UserJoinTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    public void onClick(View v) {
        String email = et_email.getText().toString();
        String password = et_passwd.getText().toString();
        String pwconfirm = et_pwconfirm.getText().toString();

        String possible = tv_possiblemail.getText().toString();

            if (v == btn_back) {//뒤로가기
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();

            } else if (v == btn_save) {//저장
                a = 0;//모든 조건을 충족시킬 때에 a는 0으로 유지, 저장버튼 누를 때마다 갱신
                if (password.length() < 4 || password.length() > 12) {//패스워드 길이 설정
                    Toast toast = Toast.makeText(JoinActivity.this, "패스워드는 4자 이상, 12자 이하로 입력해주세요", Toast.LENGTH_SHORT);
                    toast.show();
                    et_passwd.setText("");
                    et_pwconfirm.setText("");
                    a++;
                }
                if (!password.contentEquals(pwconfirm)) {//pw확인이 되지 않았을 때
                    Toast toast = Toast.makeText(JoinActivity.this, "패스워드를 확인해주세요", Toast.LENGTH_SHORT);
                    toast.show();
                    et_passwd.setText("");
                    et_pwconfirm.setText("");
                    a++;
                }
                /*if (!possible.equals("가능")) {//이메일 중복 확인이 완료되지 않았을 시
                    Toast toast = Toast.makeText(JoinActivity.this, "이메일 중복확인을 해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                    a++;
                }*/
                if (a == 0) {
                  //DB에 저장

                    Toast toast = Toast.makeText(JoinActivity.this, email + " 씨, 환영합니다 ! ", Toast.LENGTH_SHORT);
                    toast.show();
                    attemptJoin();
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            } /*else if (v == btn_e_confirm) {//이메일 중복확인버튼
                //아이디 존재하나 검색
                //Cursor cursor = dbhandler.eselect(email+domain);
                //startManagingCursor(cursor);//커서 탐색

                if (email.isEmpty()) {
                    Toast toast = Toast.makeText(JoinActivity.this, "먼저 이메일을 입력하세요.", Toast.LENGTH_SHORT);
                    toast.show();
                }  else if (cursor.getCount() == 0) {//중복된 것이 없을 때
                    Toast toast = Toast.makeText(JoinActivity.this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    tv_possiblemail.setText("가능");
                    tv_possiblemail.setTextColor(Color.BLUE);
                } else {//중복할 때
                    Toast toast = Toast.makeText(JoinActivity.this, "이미 존재하는 E-mail입니다\n다른 이메일을 사용해주세요.", Toast.LENGTH_SHORT);
                    toast.show();
                    JoinActivity.et_email.setText("");
                    tv_possiblemail.setText("불가능");
                    tv_possiblemail.setTextColor(Color.RED);
                }
            }*/

        }


    public class UserJoinTask extends AsyncTask<Void, Void, Boolean> {

        private final String mid;
        private final String mPassword;
        private String line_ex;
        private Boolean yn = false;
        private String sid ;

        private String stateid;

        UserJoinTask(String id, String password) {
            mid = id;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                URL url = new URL("http://oioi9i.synology.me/Junsung/Android/Join.php?");
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
                    buffer.append("_id").append("=").append(mid).append("&");
                    buffer.append("pw").append("=").append(mPassword);

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
                    Log.v("total", line_ex);
                    Log.v("Join ID", sid);
                    Log.v("Join state", stateid);

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
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
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

