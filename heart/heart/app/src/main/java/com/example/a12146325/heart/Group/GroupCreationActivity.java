
package com.example.a12146325.heart.Group;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a12146325.heart.MainActivity;
import com.example.a12146325.heart.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 김준성 on 2015-07-14.
 */
public class GroupCreationActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_back, btn_save;
    private GroupTask mAuthTask;
    private EditText et_gname; //그룹이름을 넣는 EditText


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_creation);
        btn_back=(Button)findViewById(R.id.button_back);
        btn_save=(Button)findViewById(R.id.button_save);
        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        et_gname=(EditText)findViewById(R.id.et_gname);
    }

    public void onClick(View v) {

        if(v == btn_back){
            Intent i = new Intent(GroupCreationActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }else if(v == btn_save){

            attemptCreation();
            Intent i = new Intent(GroupCreationActivity.this,MainActivity.class);
            startActivity(i);
            finish();

        }
    }
    private boolean isGnameValid() {
        //TODO: Replace this with your own logic
        return et_gname.length() > 3;
    }
    private void attemptCreation() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        et_gname.setError(null);


        // Store values at the time of the login attempt.
        String gname = et_gname.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(gname)) {
            et_gname.setError(getString(R.string.error_field_required));
            focusView = et_gname;
            cancel = true;
        } else if (!isGnameValid()) {
           et_gname.setError(getString(R.string.error_invalid_email));
            focusView = et_gname;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            mAuthTask = new GroupTask(et_gname.getText().toString());
            mAuthTask.execute((Void) null);
        }
    }
    public class GroupTask extends AsyncTask<Void, Void, Boolean> {

        private final String _id;
        private final String userID;
        private String line_ex;
        private Boolean yn = false;
        private String gid;
        private SharedPreferences preferences= getSharedPreferences("PerfLogin", MODE_PRIVATE);

        private String stateid;

        GroupTask(String id) {
            _id= id;
            userID = preferences.getString("state","fail");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                URL url = new URL("http://oioi9i.synology.me/Junsung/Android/accept.php?");
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
                    buffer.append("_id").append("=").append(_id).append("&");
                    buffer.append("userID").append("=").append(userID);

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
                    gid = pieces[0];
                    //userID = pieces[1];
                    Log.e("_id",gid);
                    Log.e("userID",pieces[1]);

                    stateid = pieces[2];

                    Log.d("total", line_ex);
                    Log.d("gid", gid);
                    Log.d("userId", stateid);

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
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
