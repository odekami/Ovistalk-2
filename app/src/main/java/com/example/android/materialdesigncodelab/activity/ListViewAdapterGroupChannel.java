package com.example.android.materialdesigncodelab.activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.materialdesigncodelab.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by studio kami on 06/03/2018.
 */

public class ListViewAdapterGroupChannel extends BaseAdapter {
    ArrayList<items> chats; //data source of the list adapter
    Chats test;
    //public constructor
    public ListViewAdapterGroupChannel(Chats test, ArrayList<items> items) {
        this.test = test;
        this.chats = items;
    }

    @Override
    public int getCount() {
        return chats.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return chats.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String email = currentFirebaseUser.getEmail();

        // get current item to be displayed
        items currentItem = (items) getItem(position);

        // inflate the layout for each list row
        if (email.equals(currentItem.getEmail())) {
            convertView = LayoutInflater.from(test).
                    inflate(R.layout.chat_user2_item, parent, false);
            System.out.println("outgoing");
        } else {
            convertView = LayoutInflater.from(test).
                    inflate(R.layout.chat_user3_item, parent, false);
            System.out.println("incoming");
        }

        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.textview_message);
        TextView textViewItemDescription = (TextView)
                convertView.findViewById(R.id.textview_time);
        TextView textViewEmail = (TextView)
                convertView.findViewById(R.id.textview_from);
        ImageView user_reply_status = (ImageView)
                convertView.findViewById(R.id.user_reply_status);
        ImageView user_image = (ImageView)
                convertView.findViewById(R.id.list_avatar);

        //sets the text for item name and item description from the current item object
        textViewItemName.setText(currentItem.getMessage());

        // format time
        String new_time = currentItem.getTime();
        String time_format = new_time.substring(11,16);
        textViewItemDescription.setText(time_format);


        // inflate the layout for each list row
        if (email.equals(currentItem.getEmail())) {
            // out going
            String status = currentItem.getStatus();
            if (TextUtils.isEmpty(status)) {
                status = "read";
                System.out.println("null --------------------------------------------------------------------------------------- ");
            }

            if(status.equals("sent")){
                user_reply_status.setImageResource(R.drawable.message_got_receipt_from_target);
            }
            else if(status.equals("read")){
                user_reply_status.setImageResource(R.drawable.message_got_read_receipt_from_target);
            }
            else{
                user_reply_status.setImageResource(R.drawable.message_got_receipt_from_server);
            }
        } else {
            // incomeing
            user_image.setImageResource(R.drawable.a_avator);
            textViewEmail.setText(currentItem.getEmail());
        }
        // returns the view for the current row
        return convertView;
    }
}
