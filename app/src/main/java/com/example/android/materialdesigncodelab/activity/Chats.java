package com.example.android.materialdesigncodelab.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.android.materialdesigncodelab.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Chats extends AppCompatActivity {

    ArrayList<items> chats_data;
    FirebaseDatabase database;
    ListView itemsListView;
    String chat_with = "";
    String type;
    public static String pin_channel_group;
    public static boolean on_chat = false;
    public static boolean on_update = false;

    private static String url = "http://ovistalk.studiokami.co.id/api/sendnotification.php";

    private ProgressDialog pDialog;
    FirebaseUser currentFirebaseUser;

    String message_to_sent;
    int unread;
    int check;
    int last_load = 0;
    int new_message_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        // set on chat to true
        on_chat = true;

        // required variable
        String name = getIntent().getStringExtra("EXTRA_NAME");
        String id = getIntent().getStringExtra("EXTRA_ID");
        String token = getIntent().getStringExtra("EXTRA_TOKEN");
        pin_channel_group = getIntent().getStringExtra("EXTRA_PIN");
        type = getIntent().getStringExtra("EXTRA_TYPE");

        // check if this chat is channel
        if(type.equals("channel")){
            LinearLayout input_area = (LinearLayout) findViewById(R.id.bottomlayout);
            input_area.setVisibility(View.GONE);
        }

        // set chat with id
        chat_with = id;

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(name);
        //getActionBar().setIcon(R.drawable.my_icon);
        //toolbar.setTitle("Search Contact");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        // profil button
        toolbar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Do something
                profil();
            }
        });

        // send message
        ImageButton button_send = (ImageButton) findViewById(R.id.btn_send);

        button_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                EditText message_content = (EditText) findViewById(R.id.message_text);
                String message = message_content.getText().toString();
                message_content.setText("");
                if(message.equals("")){

                }else{
                    postMessage(message);
                }
            }
        });

        // firebase user

        database = FirebaseDatabase.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        itemsListView = (ListView)findViewById(R.id.chat_list_view);

        itemsListView.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0){
                    display_message_next();
                }
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        // Update chat message to read
        update_status();

        display_message();


    }

    protected void onDestroy(){
        super.onDestroy();
        Chats.on_chat = false;
    }
    protected void onPause(){
        super.onPause();
        Chats.on_chat = false;
    }

    // Display message

    private void display_message() {

        Query myRef;
        // channel chat
        if(type.equals("channel")){
            // personal chat
            myRef = database.getReference("channel_message").child("channel_message_" + pin_channel_group).orderByKey().limitToLast(50);
        }

        // group chat
        else if(type.equals("group")){
            // personal chat
            myRef = database.getReference("group_message").child("group_message_" + pin_channel_group).orderByKey().limitToLast(50);
        }

        // personal chat
        else{
            myRef = database.getReference("chat").child("chat_" + currentFirebaseUser.getUid()).child("to_" + chat_with).orderByKey().limitToLast(50);
        }

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // update status is running
                if(on_update){

                }
                else{

                    chats_data = new ArrayList<items>();
                    int first = 0;
                    currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Getting the data from snapshot
                        items message = postSnapshot.getValue(items.class);
                        String key = postSnapshot.getKey();
                        if(first == 0){
                            last_load = Integer.parseInt(key);
                        }
                        first++;
                        // Setup the data source
                        chats_data.add(new items(message.getEmail(), message.getMessage(), message.getTime(), message.getStatus()));
                        if(on_chat){

                            if(message.getStatus().equals("sent")){
                                unread++;
                                // channel chat
                                if(type.equals("channel")){
                                    DatabaseReference myRef11 = database.getReference("channel_message").child("channel_message_" + pin_channel_group);
                                    myRef11.child(key).child("status").setValue("read");
                                }

                                // group chat
                                else if(type.equals("group")){
                                    DatabaseReference myRef11 = database.getReference("group_message").child("group_message_" + pin_channel_group);
                                    myRef11.child(key).child("status").setValue("read");
                                }

                                // personal chat
                                else{
                                    DatabaseReference myRef11 = database.getReference("chat/chat_" + chat_with + "/to_" + currentFirebaseUser.getUid());
                                    myRef11.child(key).child("status").setValue("read");
                                }

                            }
                        }
                    }
                }

                displaMessage(chats_data);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }

    private void display_message_next() {
        int end = last_load - 1;
        if(end > 2){

            int start = end - 20;

            database = FirebaseDatabase.getInstance();
            currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

            Query myRef;
            // channel chat
            if(type.equals("channel")){
                // personal chat
                myRef = database.getReference("channel_message").child("channel_message_" + pin_channel_group).orderByKey().startAt(Integer.toString(start)).endAt(Integer.toString(end));
            }

            // group chat
            else if(type.equals("group")){
                // personal chat
                myRef = database.getReference("group_message").child("group_message_" + pin_channel_group).orderByKey().startAt(Integer.toString(start)).endAt(Integer.toString(end));
            }

            // personal chat
            else{
                myRef = database.getReference("chat").child("chat_" + currentFirebaseUser.getUid()).child("to_" + chat_with).orderByKey().startAt(Integer.toString(start)).endAt(Integer.toString(end));
            }

            // Read from the database
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    int first = 0;
                    currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Getting the data from snapshot
                        items message = postSnapshot.getValue(items.class);
                        String key = postSnapshot.getKey();

                        if(first == 0){
                            last_load = Integer.parseInt(key);
                        }
                        first++;

                        // Setup the data source
                        chats_data.add(new items(message.getEmail(), message.getMessage(), message.getTime(), message.getStatus()));
                        if(on_chat){

                            if(message.getStatus().equals("sent")){
                                unread++;
                                // channel chat
                                if(type.equals("channel")){
                                    DatabaseReference myRef11 = database.getReference("channel_message").child("channel_message_" + pin_channel_group);
                                    myRef11.child(key).child("status").setValue("read");
                                }

                                // group chat
                                else if(type.equals("group")){
                                    DatabaseReference myRef11 = database.getReference("group_message").child("group_message_" + pin_channel_group);
                                    myRef11.child(key).child("status").setValue("read");
                                }

                                // personal chat
                                else{
                                    DatabaseReference myRef11 = database.getReference("chat/chat_" + chat_with + "/to_" + currentFirebaseUser.getUid());
                                    myRef11.child(key).child("status").setValue("read");
                                }

                            }
                        }
                    }

                    displaMessageNext(chats_data);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }

    }


    // update status
    private void update_status(){

        // set activity to update message
        on_update = true;

        DatabaseReference myRef;
        myRef = database.getReference("chat").child("chat_" + chat_with).child("to_" + currentFirebaseUser.getUid());
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int check_data = 0;
                unread = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    items message = postSnapshot.getValue(items.class);
                    String key = postSnapshot.getKey();

                    if(message.getStatus().equals("sent")){
                        unread++;
                        // channel chat
                        if(type.equals("channel")){
                            DatabaseReference myRef11 = database.getReference("channel_message").child("channel_message_" + pin_channel_group);
                            myRef11.child(key).child("status").setValue("read");
                        }

                        // group chat
                        else if(type.equals("group")){
                            DatabaseReference myRef11 = database.getReference("group_message").child("group_message_" + pin_channel_group);
                            myRef11.child(key).child("status").setValue("read");
                        }

                        // personal chat
                        else{
                            DatabaseReference myRef11 = database.getReference("chat/chat_" + chat_with + "/to_" + currentFirebaseUser.getUid());
                            myRef11.child(key).child("status").setValue("read");
                        }

                    }
                    check_data ++;
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        // Unread message
        // channel chat
        if(type.equals("channel")){
            DatabaseReference count_message = database.getReference("contact").child("contact_"+ currentFirebaseUser.getUid()).child("channels").child(pin_channel_group).child("New_message");
            //DatabaseReference count_message_2 = database.getReference("contact").child("contact_"+ getIntent().getStringExtra("EXTRA_ID")).child("contact").child(currentFirebaseUser.getUid()).child("New_message");
            count_message.setValue(0);
        }

        // group chat
        else if(type.equals("group")){
            DatabaseReference count_message = database.getReference("contact").child("contact_"+ currentFirebaseUser.getUid()).child("group").child(pin_channel_group).child("New_message");
            //DatabaseReference count_message_2 = database.getReference("contact").child("contact_"+ getIntent().getStringExtra("EXTRA_ID")).child("contact").child(currentFirebaseUser.getUid()).child("New_message");
            count_message.setValue(0);
        }

        // personal chat
        else{
            DatabaseReference count_message = database.getReference("contact").child("contact_"+ currentFirebaseUser.getUid()).child("contact").child(getIntent().getStringExtra("EXTRA_ID")).child("New_message");
            //DatabaseReference count_message_2 = database.getReference("contact").child("contact_"+ getIntent().getStringExtra("EXTRA_ID")).child("contact").child(currentFirebaseUser.getUid()).child("New_message");
            count_message.setValue(0);
        }

        // update status complete
        on_update = false;

    }

    private void profil(){

        String name = getIntent().getStringExtra("EXTRA_NAME");
        String id = getIntent().getStringExtra("EXTRA_ID");
        String token = getIntent().getStringExtra("EXTRA_TOKEN");
        pin_channel_group = getIntent().getStringExtra("EXTRA_PIN");
        type = getIntent().getStringExtra("EXTRA_TYPE");
        Intent myIntent;
        if(type.equals("personal")){
            myIntent = new Intent(this, Profil.class);
        }
        else{
            myIntent = new Intent(this, DetailActivity.class);
        }
        myIntent.putExtra("EXTRA_TYPE", type);
        myIntent.putExtra("EXTRA_EMAIL", name);
        myIntent.putExtra("EXTRA_ID", id);
        myIntent.putExtra("EXTRA_PIN", pin_channel_group);
        startActivityForResult(myIntent, 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds com.example.android.materialdesigncodelab.activity.items to the action bar if it is present.

        // channel chat
        if(type.equals("channel") || type.equals("group")){
            // personal chat
            getMenuInflater().inflate(R.menu.chat_menu, menu);
        }
        // personal chat
        else{
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_add:
                to_search();
                return true;

            case R.id.action_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void displaMessage(ArrayList<items> data){
        // channel chat
        if(type.equals("channel") || type.equals("group")){
            // group channel
            ListViewAdapterGroupChannel adapter = new ListViewAdapterGroupChannel(this, data);
            itemsListView.setAdapter(adapter);
            itemsListView.setSelection(itemsListView.getAdapter().getCount()-1);
        }
        // personal chat
        else{
            ListViewAdapter adapter = new ListViewAdapter(this, data);
            itemsListView.setAdapter(adapter);
            itemsListView.setSelection(itemsListView.getAdapter().getCount()-1);
        }
    }

    private void displaMessageNext(ArrayList<items> data){
        // channel chat
        if(type.equals("channel") || type.equals("group")){
            // group channel
            ListViewAdapterGroupChannel adapter = new ListViewAdapterGroupChannel(this, data);
            itemsListView.setAdapter(adapter);
            itemsListView.setSelection(last_load + 20);
        }
        // personal chat
        else{
            ListViewAdapter adapter = new ListViewAdapter(this, data);
            itemsListView.setAdapter(adapter);
            itemsListView.setSelection(last_load + 20);
        }
    }

    private void postMessage(String message){
        // post message//
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        message_to_sent = message;

        chats_data.add(new items(currentFirebaseUser.getEmail(), message, date, "sent"));

        displaMessage(chats_data);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        // channel chat
        if(type.equals("channel")){
            // push notification
            String response = request_notification(chat_with, message);
            System.out.println(response);
        }

        // group chat
        else if(type.equals("group")){

            String response = request_notification(chat_with, message);
        }

        // personal chat
        else{
            check=0;
            Query lastQuery = database.getReference("chat").child("chat_" + currentFirebaseUser.getUid()).child("to_" + chat_with).orderByKey().limitToLast(1);
            lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        check++;
                        String get_key = postSnapshot.getKey();

                        System.out.println(get_key + "=====================================");
                        if(get_key != null){
                            int key = Integer.parseInt(get_key);
                            key++;
                            post_message_personal(key);
                        }else{
                            post_message_personal(1);
                        }
                    }
                    if(check < 1){
                        post_message_personal(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Handle possible errors.
                    post_message_personal(1);
                    check++;
                }
            });
            Query token = database.getReference("users").child("user_" + chat_with);
            token.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User_class contact_list = dataSnapshot.getValue(User_class.class);
                    String response = request_notification(contact_list.Get_token(), message_to_sent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }

    }

    public void post_message_personal(int key){
        System.out.println(key + " ================================================== " + message_to_sent);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        Integer intInstance = new Integer(key);
        String numberAsString = intInstance.toString();

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        DatabaseReference myRef1 = database.getReference("chat").child("chat_" + currentFirebaseUser.getUid()).child("to_" + chat_with).child(numberAsString);
        myRef1.setValue(new items(currentFirebaseUser.getEmail(),message_to_sent,date, "sent"));
        DatabaseReference myRef11 = database.getReference("chat").child("chat_" + chat_with).child("to_" + currentFirebaseUser.getUid()).child(numberAsString);
        myRef11.setValue(new items(currentFirebaseUser.getEmail(),message_to_sent,date, "sent"));

        // Last message
        DatabaseReference last_message = database.getReference("contact").child("contact_"+ currentFirebaseUser.getUid()).child("contact").child(getIntent().getStringExtra("EXTRA_ID")).child("Last_message");
        DatabaseReference last_message_2 = database.getReference("contact").child("contact_"+ getIntent().getStringExtra("EXTRA_ID")).child("contact").child(currentFirebaseUser.getUid()).child("Last_message");
        last_message.setValue(message_to_sent);
        last_message_2.setValue(message_to_sent);

        //Unread

        // personal chat
        DatabaseReference count_new = database.getReference("chat").child("chat_" + currentFirebaseUser.getUid() ).child("to_" + chat_with);

        // Read from the database
        count_new.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new_message_count = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    items message = postSnapshot.getValue(items.class);
                    String key = postSnapshot.getKey();
                    if(message.getStatus() != null){
                        if(message.getStatus().equals("sent")){
                            new_message_count++;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        DatabaseReference count_message_2 = database.getReference("contact").child("contact_"+ getIntent().getStringExtra("EXTRA_ID")).child("contact").child(currentFirebaseUser.getUid()).child("New_message");
        count_message_2.setValue(new_message_count);

    }

    public void to_search(){
        Intent myIntent = new Intent(this, Search.class);
        myIntent.putExtra("EXTRA_TYPE", type);
        myIntent.putExtra("EXTRA_ID", pin_channel_group);
        myIntent.putExtra("EXTRA_NAME", getIntent().getStringExtra("EXTRA_NAME"));
        startActivityForResult(myIntent, 0);
    }

    private String request_notification(String personal_token, String message){
        try {

            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
            String token = URLEncoder.encode(personal_token, "UTF-8");
            String message_url = URLEncoder.encode(message, "UTF-8");
            String from = URLEncoder.encode(Chats.pin_channel_group, "UTF-8");

            String strURL;

            // channel chat
            if(type.equals("channel")){

                strURL = "http://ovistalk.studiokami.co.id/api/post_message.php?channel=" + pin_channel_group + "&message=" + message_url + "&from=" + currentFirebaseUser.getEmail();
            }

            // group chat
            else if(type.equals("group")){

                strURL = "http://ovistalk.studiokami.co.id/api/post_message_group.php?group_id=" + pin_channel_group + "&message=" + message_url + "&from=" + currentFirebaseUser.getEmail();
            }

            // personal chat
            else{

                strURL = "http://ovistalk.studiokami.co.id/api/sendnotification.php?id=" + token + "&message=" + message_url + "&from=" + currentFirebaseUser.getEmail();
            }
            url = strURL;
            new GetContacts().execute();
            return "0";
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return "0";
        }
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.e("reee", "started");
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Chats.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e("reee", "Response from url: " + jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.e("reee", "sdfsdf sdf ds" + result);
            /**
             * Updating parsed JSON data into ListView
             * */
        }

    }
}
