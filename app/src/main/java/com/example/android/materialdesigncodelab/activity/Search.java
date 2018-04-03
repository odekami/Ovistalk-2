package com.example.android.materialdesigncodelab.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.materialdesigncodelab.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Search extends AppCompatActivity {

    ArrayList<User_class> contact;
    FirebaseDatabase database;

    String emailnya;
    String tokennya;
    String pinnya;
    String idnya;

    String type;
    String channel_group_id;
    String name_group_channel;

    FirebaseUser currentFirebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        type = getIntent().getStringExtra("EXTRA_TYPE");
        channel_group_id = getIntent().getStringExtra("EXTRA_ID");
        name_group_channel = getIntent().getStringExtra("EXTRA_NAME");
        if (type.equals("channel")) {
            setTitle("Add channel member");
        } else if (type.equals("group")) {
            setTitle("Add group member");
        } else {
            setTitle("Search Contact");
        }

        //getActionBar().setIcon(R.drawable.my_icon);
        //toolbar.setTitle("Search Contact");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                contact = new ArrayList<User_class>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    User_class contact_list = postSnapshot.getValue(User_class.class);

                    if(contact_list.Get_email().equals(currentFirebaseUser.getEmail())){

                    }else{
                        // Setup the data source
                        contact.add(new User_class(contact_list.Get_email(), contact_list.Get_pin(), contact_list.Get_token(), contact_list.Get_type(), contact_list.Get_user_id(), "", 0, contact_list.Get_images()));
                    }
                }

                // instantiate the custom list adapter
                ListViewAdapterContact adapter = new ListViewAdapterContact(getBaseContext(), contact, "personal");

                // get the ListView and attach the adapter
                ListView itemsListView = (ListView) findViewById(R.id.list_view);
                itemsListView.setAdapter(adapter);

                // click event
                itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println(position);

                        emailnya = contact.get(position).Email;
                        tokennya = contact.get(position).Token;
                        pinnya = contact.get(position).Pin;
                        idnya = contact.get(position).Uid;
                        //set alert for executing the task
                        show_dialog();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void go_to_menu() {
        Intent myIntent = new Intent(this, MainActivity.class);
        //myIntent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(myIntent, 0);
    }

    private void show_dialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure want to add this contact?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (type.equals("channel")) {
                            postMessage_channel();
                        } else if (type.equals("group")) {

                            postMessage_group();
                        } else {

                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference myRef1 = database.getReference("contact").child("contact_" + currentFirebaseUser.getUid()).child("contact").child(idnya);
                            myRef1.setValue(new User_class(emailnya, pinnya, "requestby", "contact", idnya, "", 0, ""));

                            // Request contact
                            DatabaseReference Request = database.getReference("contact").child("contact_" + idnya).child("contact").child(currentFirebaseUser.getUid());
                            Request.setValue(new User_class(currentFirebaseUser.getEmail(), "pin", "request", "contact", currentFirebaseUser.getUid(), "", 0, ""));

                        }
                        dialog.dismiss();
                        go_to_menu();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    private void postMessage_channel() {
        // post message//
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = currentFirebaseUser.getUid();
        String Name = name_group_channel;
        String Email = currentFirebaseUser.getEmail();

        // token device
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // save memeber
        DatabaseReference Memeber = database.getReference("channel_member/channel_member_" + channel_group_id).push();
        Memeber.setValue(new ChannelMember(emailnya, idnya, tokennya, "member"));

        DatabaseReference contact = database.getReference("contact").child("contact_" + idnya).child("channels").push();
        contact.setValue(new User_class(Name, channel_group_id, refreshedToken, "channel", Uid, "", 0, ""));

        // back to main act
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private void postMessage_group() {
        // post message//
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = currentFirebaseUser.getUid();
        String Name = name_group_channel;
        String Email = currentFirebaseUser.getEmail();

        // token device
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // save memeber
        DatabaseReference Memeber = database.getReference("group_member/group_member_" + channel_group_id).push();
        Memeber.setValue(new ChannelMember(emailnya, idnya, tokennya, "member"));

        DatabaseReference contact = database.getReference("contact").child("contact_" + idnya).child("group").push();
        contact.setValue(new User_class(Name, channel_group_id, refreshedToken, "group", Uid, "", 0, ""));

        // back to main act
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private class ChannelMember {
        public String Email;
        public String Id;
        public String Token;
        public String Status;

        public ChannelMember() {
        }

        public ChannelMember(String Email, String Id, String Token, String Status) {
            this.Email = Email;
            this.Id = Id;
            this.Token = Token;
            this.Status = Status;
        }
    }
}
