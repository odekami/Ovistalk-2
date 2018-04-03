package com.example.android.materialdesigncodelab.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.materialdesigncodelab.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddChannel extends AppCompatActivity {
    TextView name;
    public static String Channel_ID;
    public static String Group_ID;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_channel);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getActionBar().setIcon(R.drawable.my_icon);
        //toolbar.setTitle("Search Contact");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Button btn_save = (Button) findViewById(R.id.btnCreate);
        TextView title = (TextView) findViewById(R.id.statusText);

        name = (TextView) findViewById(R.id.channel_name);

        type = getIntent().getStringExtra("EXTRA_TYPE");
        if (type.equals("channel")) {
            title.setText("Create Channel");
            name.setHint("Channel name");
            setTitle("Add Channel");
        } else {
            title.setText("Create Group");
            name.setHint("Group name");
            setTitle("Add Group");
        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String message = name.getText().toString();
                if (message.equals("")) {

                } else {

                    if (type.equals("channel")) {
                        save_channel();
                    } else {
                        save_group();
                    }
                }
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

    private void postMessage_channel(String message) {
        // post message//
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = currentFirebaseUser.getUid();
        String Name = name.getText().toString();
        String Email = currentFirebaseUser.getEmail();

        // token device
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // save channel
        DatabaseReference myRef1 = database.getReference("channels").child(AddChannel.Channel_ID);
        myRef1.setValue(new ChannelsSave(Name, "open", Uid));
        // save memeber
        DatabaseReference Memeber = database.getReference("channel_member/channel_member_" + AddChannel.Channel_ID).child(Uid);
        Memeber.setValue(new ChannelMember(Email, Uid, refreshedToken, "admin"));

        DatabaseReference contact = database.getReference("contact").child("contact_" + currentFirebaseUser.getUid()).child("channels").child(AddChannel.Channel_ID);
        contact.setValue(new User_class(Name, AddChannel.Channel_ID, refreshedToken, "channel", Uid, "", 0, ""));

        // back to main act
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private void postMessage_group(String message) {
        // post message//
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = currentFirebaseUser.getUid();
        String Name = name.getText().toString();
        String Email = currentFirebaseUser.getEmail();

        // token device
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // save channel
        DatabaseReference myRef1 = database.getReference("group").child(AddChannel.Group_ID);
        myRef1.setValue(new GroupSave(Name, "open", Uid));
        // save memeber
        DatabaseReference Memeber = database.getReference("group_member/group_member_" + AddChannel.Group_ID).child(Uid);
        Memeber.setValue(new ChannelMember(Email, Uid, refreshedToken, "admin"));

        DatabaseReference contact = database.getReference("contact").child("contact_" + currentFirebaseUser.getUid()).child("group").child(AddChannel.Group_ID);
        contact.setValue(new User_class(Name, AddChannel.Group_ID, refreshedToken, "group", Uid, "", 0, ""));

        // back to main act
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private void save_group() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query generate_id = database.getReference("group").orderByKey().limitToLast(1);

        generate_id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cehck = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    cehck++;
                    GroupSave Get_id = postSnapshot.getValue(GroupSave.class);
                    int pin = Integer.parseInt(Get_id.GroupId);
                    pin += 1;
                    AddChannel.Group_ID = String.valueOf(pin);
                    postMessage_group("");
                }
                if (cehck == 0) {
                    AddChannel.Group_ID = "200000001";
                    postMessage_group("");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void save_channel() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query generate_id = database.getReference("channels").orderByKey().limitToLast(1);

        generate_id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int cehck = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ChannelsSave Get_id = postSnapshot.getValue(ChannelsSave.class);
                    int pin = Integer.parseInt(Get_id.ChannelId);
                    pin += 1;
                    AddChannel.Channel_ID = String.valueOf(pin);
                    postMessage_channel("");
                }
                if (cehck == 0) {
                    AddChannel.Channel_ID = "100000001";
                    postMessage_channel("");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private class ChannelsSave {
        public String Name;
        public String Type;
        public String ChannelId;
        public String CreateOn;
        public String CreateBy;

        public ChannelsSave() {
        }

        public ChannelsSave(String Name, String Type, String CreateBy) {
            this.Name = Name;
            this.Type = Type;
            this.CreateBy = CreateBy;
            ChannelId = AddChannel.Channel_ID;
            CreateOn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        }
    }

    private class GroupSave {
        public String Name;
        public String Type;
        public String GroupId;
        public String CreateOn;
        public String CreateBy;

        public GroupSave() {
        }

        public GroupSave(String Name, String Type, String CreateBy) {
            this.Name = Name;
            this.Type = Type;
            this.CreateBy = CreateBy;
            GroupId = AddChannel.Group_ID;
            CreateOn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        }
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
