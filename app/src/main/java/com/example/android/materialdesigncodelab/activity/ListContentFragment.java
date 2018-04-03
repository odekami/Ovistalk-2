/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.materialdesigncodelab.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.materialdesigncodelab.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Provides UI for the view with List.
 */
public class ListContentFragment extends Fragment {
    ListView listview;
    View rootView;
    String value;

    ArrayList<User_class> contact;
    FirebaseUser currentFirebaseUser;
    String last_message = "";
    User_class contact_list;

    int postition_select = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.list_chat, container, false);

        listview =(ListView) rootView.findViewById(R.id.list);

        // firebase database

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("contact").child("contact_"+ currentFirebaseUser.getUid()).child("contact");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                contact = new ArrayList<User_class>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    contact_list = postSnapshot.getValue(User_class.class);
                    contact.add(new User_class(contact_list.Get_email(), contact_list.Get_pin(), contact_list.Get_token(), contact_list.Get_type(), contact_list.Get_user_id(),contact_list.Get_last(), contact_list.Get_new(), contact_list.Get_images()));
                    // Setup the data source

                    //Adding it to a string
                    String string = "Name: " + contact_list.Get_email() + "\nAddress: " + contact_list.Get_token() + "\n\n";
                    System.out.println("Last message : " + contact_list.Get_last());
                }

                // instantiate the custom list adapter
                ListViewAdapterContact adapter = new ListViewAdapterContact(getContext(), contact, "personal");

                // get the ListView and attach the adapter
                ListView itemsListView = (ListView) rootView.findViewById(R.id.list);
                itemsListView.setAdapter(adapter);

                // click event
                itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        TextView status = (TextView) rootView.findViewById(R.id.txt_count_message);
                        if(contact.get(position).Token.equals("requestby")){

                        }
                        else if(contact.get(position).Token.equals("request")){
                            postition_select = position;
                            String id_contact = "";
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Ovistalk");
                            alertDialog.setMessage("Approve this contact?");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                            DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference("contact").child("contact_"+ currentFirebaseUser.getUid()).child("contact").child(contact.get(postition_select).Uid);
                                            myRef1.setValue(new User_class(contact.get(postition_select).Email,contact.get(postition_select).Pin,"approved","contact",contact.get(postition_select).Uid,"",0,""));

                                            // Request contact
                                            DatabaseReference Request = database.getReference("contact").child("contact_" + contact.get(postition_select).Uid).child("contact").child(currentFirebaseUser.getUid());
                                            Request.setValue(new User_class(currentFirebaseUser.getEmail(), "pin", "approved", "contact", currentFirebaseUser.getUid(), "", 0, ""));

                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.setCancelable(true);
                            alertDialog.show();
                        }
                        else{
                            Intent myIntent = new Intent(view.getContext(), Chats.class);
                            myIntent.putExtra("EXTRA_NAME", contact.get(position).Email);
                            myIntent.putExtra("EXTRA_ID", contact.get(position).Uid);
                            myIntent.putExtra("EXTRA_TOKEN", contact.get(position).Token);
                            myIntent.putExtra("EXTRA_PIN", contact.get(position).Pin);
                            myIntent.putExtra("EXTRA_TYPE", "personal");
                            MainActivity.chat_with = contact.get(position).Email;
                            startActivityForResult(myIntent, 0);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        return rootView;
    }

}
