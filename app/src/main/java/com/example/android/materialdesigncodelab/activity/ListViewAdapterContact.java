package com.example.android.materialdesigncodelab.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.materialdesigncodelab.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by studio kami on 06/03/2018.
 */

public class ListViewAdapterContact extends BaseAdapter {
    private Context context; //context
    private ArrayList<User_class> items; //data source of the list adapter

    private Drawable[] mPlaceAvators;
    FirebaseDatabase database;
    ArrayList<items> chats_data;
    String type;
    String last_message;
    ImageView avator;
    String profil_image;
    File localFile;
    int count_message;

    // get current item to be displayed
    User_class currentItem;
    FirebaseUser currentFirebaseUser;
    TextView textViewItemDescription;

    //public constructor
    public ListViewAdapterContact(Context context, ArrayList<User_class> items, String type) {
        this.context = context;
        this.items = items;
        this.type = type;

    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        // get current item to be displayed
        currentItem = (User_class) getItem(position);
        final long ONE_MEGABYTE = 1024 * 1024 *5;
        // inflate the layout for each list row

        if(currentItem.Get_token().equals("request")){
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_list_request, parent, false);
        }
        else{
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_list, parent, false);
        }

        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.list_title);
        textViewItemDescription = (TextView)
                convertView.findViewById(R.id.list_desc);
        // count message
        TextView message_new_count = (TextView) convertView.findViewById(R.id.txt_count_message);
        avator = (ImageView) convertView.findViewById(R.id.list_avatar);

        // cocunt message
        String tex_message = currentItem.Get_last();
        int count_message = currentItem.Get_new();
        //getLast_message(position, currentItem.Get_user_id());

        if(currentItem.Get_token().equals("request")){
            message_new_count.setText("Approve");
        }
        else if(currentItem.Get_token().equals("requestby")){
            message_new_count.setText("Requesting...");
        }
        else if(count_message > 0){
            String text = String.valueOf(count_message);

            message_new_count.setText(text);
        }
        else{
            message_new_count.setVisibility(View.INVISIBLE);
            //new_message.setVisibility(View.INVISIBLE);
        }

        // check if view is group or channel
        if(type.equals("group") || type.equals("channel")){
            avator.setImageResource(R.drawable.a_avator);
        }
        // contact list
        else{
            try{
                StorageReference storageReference;
                storageReference = FirebaseStorage.getInstance().getReference();
                // Load the image using Glide

                StorageReference islandRef = storageReference.child("images/profil_image_" + currentItem.Get_user_id());
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(islandRef)
                        .error(R.drawable.a_avator)
                        .into(avator);
            }
            catch(Exception e){
                avator.setImageResource(R.drawable.a_avator);
            }
        }

        textViewItemName.setText(currentItem.Get_email());
        textViewItemDescription.setText(tex_message);
        // returns the view for the current row
        return convertView;
    }
    private String getLast_message(int position, String chat_with){
        System.out.println(chat_with);

        database = FirebaseDatabase.getInstance();
        Query lastQuery = database.getReference("chat").child("chat_" + currentFirebaseUser.getUid()).child("to_" + chat_with).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    items message = postSnapshot.getValue(items.class);
                    textViewItemDescription.setText(message.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
        return last_message;
    }
}
