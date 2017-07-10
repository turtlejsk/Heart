package com.example.a12146325.heart.Group;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a12146325.heart.MainActivity;
import com.example.a12146325.heart.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class GroupListActivity extends Activity implements View.OnClickListener {
    Button btn_back;

    private ListView groupList;
    private GroupListTask mAuthTask=null;
    private ReadyTask mAuthTask2=null;
    static private ArrayList<String> ex;
    static private ArrayList<String> ex2;
    String _name, userID;
    private ListView readygroupList;
    static String accGname=null;

    SharedPreferences pref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list);
        //String managerID = pref.getString("useremail", "fail");//여기 usermail은 핸드폰에서 로그인할때마다 바껴서 cursor로 수정해야함..
        pref = getSharedPreferences("PerfLogin", MODE_PRIVATE);
        userID = pref.getString("state", "fail");
        ex=new ArrayList<>();
        ex2=new ArrayList<>();
        attemptReady(userID);
        attemptGlist();

        groupList = (ListView) findViewById(R.id.group_list);
        readygroupList = (ListView) findViewById(R.id.listview);

        btn_back = (Button) findViewById(R.id.button_back);
        btn_back.setOnClickListener(this);
    }


    public void onClick(View v) {
        if (v == btn_back) {
            Intent t = new Intent(GroupListActivity.this, MainActivity.class);
            startActivity(t);
            finish();
              /*
            뒤로가기 버튼, MainMenuActivity로
             */
        }

    }
    private void attemptGlist() {
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

            mAuthTask = new GroupListTask();
            mAuthTask.execute((Void) null);
        }
    }
    private void attemptReady(String receiver) {
        if (mAuthTask != null) {
            return;
        }
        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            focusView.requestFocus();
        } else {

            mAuthTask2 = new ReadyTask(receiver);
            mAuthTask2.execute((Void) null);
        }
    }

    private class GroupAdapter extends ArrayAdapter<GroupListItem> {//그룹리스트 아이템을 다루는 커스텀 어댑터
        private ArrayList<GroupListItem> items;

        public GroupAdapter(Context context, int textViewResourceId, ArrayList<GroupListItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.listitem_group, null);
            }
            GroupListItem gi = items.get(position);
            if (gi != null) {
                TextView groupName = (TextView) v.findViewById(R.id.group_name);
                groupName.setText(gi.getName());
            }
            return v;
        }
    }

    private class ReadyGroupAdapter extends ArrayAdapter<ReadyGroupList> {//승낙대기 리스트 어댑터
        private ArrayList<ReadyGroupList> items;
        public ReadyGroupAdapter(Context context, int textViewResourceId, ArrayList<ReadyGroupList> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.readygroup, null);
            }
            ReadyGroupList gi = items.get(position);
            if (gi != null) {
                TextView groupName = (TextView) v.findViewById(R.id.beoncall_list);
                groupName.setText(gi.getName());

            }
            return v;
        }
    }
//ReadyGroupAdapter 끝


    private void CreateDialog(String name) {//그룹가입을 묻는 팝업 메소드
        AlertDialog.Builder joingroup = new AlertDialog.Builder(this);
        _name = name;
        userID = pref.getString("useremail", "fail");

        joingroup.setMessage("이 그룹에 가입하겠습니까?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {//Yes 를 클릭했을때
                Intent t = new Intent(GroupListActivity.this, GroupListActivity.class);//페이지 새로 불러옴
                startActivity(t);
                finish();
            }
        });

        AlertDialog alertDialog = joingroup.create();
        alertDialog.setTitle("그룹가입여부");
        alertDialog.show();

    }
    public class GroupListTask extends AsyncTask<Void, Void, Boolean> {

        private final String userID;
        private String line_ex;
        private Boolean yn = false;
        private String gid;
        private SharedPreferences preferences= getSharedPreferences("PerfLogin", MODE_PRIVATE);

        private String total_state ="false";

        GroupListTask() {
            userID = preferences.getString("state","fail");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                URL url = new URL("http://oioi9i.synology.me/Junsung/Android/glist.php?");
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
                    if(line_ex !=null){
                        JSONObject jsonObject = new JSONObject(line_ex);
                        JSONArray contacts = jsonObject.getJSONArray("results");
                        for(int i =0;i<contacts.length();i++){
                            JSONObject c = contacts.getJSONObject(i);
                            String _id = c.getString("_id");

                            ex.add(_id);
                            total_state = "success";
                        }
                    }
                    if (total_state.equals("success")){
                        yn = true;
                        Log.v("list result","true");
                    }
                    else if(total_state.equals("false")){
                        yn =false;
                        Log.v("list result","false");
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
                ListAdapter(ex);
            } else {
                Log.d("onPostExecute","fail");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
    public class ReadyTask extends AsyncTask<Void, Void, Boolean> {

        private final String mReceive;
        private String total_state ="false";
        private String line_ex;
        private Boolean yn = false;


        ReadyTask(String recei) {
            mReceive = recei;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                URL url = new URL("http://oioi9i.synology.me/Junsung/Android/invitation.php?");
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

                    buffer.append("receiver").append("=").append(mReceive).append("&");

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
                    Log.d("onPostExecute",line_ex);
                    if(line_ex !=null){
                        JSONObject jsonObject = new JSONObject(line_ex);
                        JSONArray contacts = jsonObject.getJSONArray("results");
                        for(int i =0;i<contacts.length();i++){
                            JSONObject c = contacts.getJSONObject(i);
                            String gname = c.getString("gname");
                            ex2.add(gname);
                            total_state = "success";
                        }
                    }else{

                    }

                    if (total_state.equals("success")){
                        yn = true;
                        Log.v("InviteService","true");
                    }
                    else if(total_state.equals("false")){
                        yn =false;
                        Log.v("InviteService","false");
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
                RListAdapter(ex2);
            } else {
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void RListAdapter(ArrayList<String> ex2){
        ArrayList<ReadyGroupList> data = new ArrayList<>();
        for(int i=0;i<ex2.size();i++){
            data.add(new ReadyGroupList(ex2.get(i)));
        }
        final ReadyGroupAdapter RarrayAdapter = new ReadyGroupAdapter(this, android.R.layout.simple_list_item_1,data);
        ListView listView2 = (ListView)findViewById(R.id.listview);
        listView2.setAdapter(RarrayAdapter);
        readygroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tmp = RarrayAdapter.items.get(position).getName(); // 누른 아이템의 position을 가지고 ArrayList<GroupListItem>에서 해당 그룹을 찾아 그룹이름을 반환
                Log.d("getName",tmp);
                accGname=tmp;
                SharedPreferences pref = getSharedPreferences("PerfLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("readygroupname", tmp);
                editor.commit(); //SharedPreference에 저장
                AlertDialog.Builder d = new AlertDialog.Builder(GroupListActivity.this);//팝업 창 생성
                d.setMessage("그룹에 가입하시겠습니까?");
                d.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                d.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AcceptTask a = new AcceptTask(accGname);
                        a.execute((Void) null);
                    }
                });
                d.show();
            }
        });
    }
    private void ListAdapter(ArrayList<String> ex) {
        ArrayList<GroupListItem> agi = new ArrayList<>();
        for(int i =0;i<ex.size();i++){
            agi.add(new GroupListItem(ex.get(i)));
            Log.d("agi add",ex.get(i));
        }
       final GroupAdapter arrayAdapter = new GroupAdapter(this,
                android.R.layout.simple_list_item_1, agi);
        ListView listView = (ListView)findViewById(R.id.group_list);
        listView.setAdapter(arrayAdapter);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tmp = arrayAdapter.items.get(position).getName(); // 누른 아이템의 position을 가지고 ArrayList<GroupListItem>에서 해당 그룹을 찾아 그룹이름을 반환
                Log.d("getName",tmp);
                SharedPreferences pref = getSharedPreferences("PerfLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("groupname", tmp);
                editor.commit(); //SharedPreference에 저장
                Intent i = new Intent(GroupListActivity.this, GroupInvite.class);
                startActivity(i);
            }
        });
    }
    public class AcceptTask extends AsyncTask<Void, Void, Boolean> {

        private final String gnameString;
        private Boolean yn = false;
        private String line_ex;

        AcceptTask(String gname) {
            gnameString = gname;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

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
                    buffer.append("receiver").append("=").append(userID).append("&");
                    buffer.append("gname").append("=").append(gnameString);

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
                    Log.d("Accept",line_ex);
                    if(line_ex !=null){
                        Log.d("echo",line_ex);
                    }else{
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

            } else {
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}