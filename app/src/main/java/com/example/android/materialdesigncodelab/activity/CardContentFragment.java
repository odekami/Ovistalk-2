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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
 * Provides UI for the view with Cards.
 */
public class CardContentFragment extends Fragment {

    ListView listview;
    View rootView;
    String value;

    ArrayList<User_class> contact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list_chat, container, false);

        listview = (ListView) rootView.findViewById(R.id.list);
        // Write a message to the database
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("contact").child("contact_" + currentFirebaseUser.getUid()).child("channels");

        // myRef.setValue("Hello, World!");
        //items save_data = new items("test","test");
        //Adding values
        //DatabaseReference myRef1 = database.getReference("test").child("test_1").push();
        //myRef1.setValue(save_data);


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

                    // Setup the data source
                    contact.add(new User_class(contact_list.Get_email(), contact_list.Get_pin(), contact_list.Get_token(), contact_list.Get_type(), contact_list.Get_user_id(), "", 0, contact_list.Get_images()));

                    //Adding it to a string
                    String string = "Name: " + contact_list.Get_email() + "\nAddress: " + contact_list.Get_token() + "\n\n";
                    System.out.println(string);
                }

                ListView listview = (ListView) rootView.findViewById(R.id.list);

                // instantiate the custom list adapter
                ListViewAdapterContact adapter = new ListViewAdapterContact(getContext(), contact, "channel");

                // get the ListView and attach the adapter
                ListView itemsListView = (ListView) rootView.findViewById(R.id.list);
                itemsListView.setAdapter(adapter);

                // click event
                itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println(position);
                        Intent myIntent = new Intent(view.getContext(), Chats.class);
                        myIntent.putExtra("EXTRA_NAME", contact.get(position).Email);
                        myIntent.putExtra("EXTRA_ID", contact.get(position).Uid);
                        myIntent.putExtra("EXTRA_TOKEN", contact.get(position).Token);
                        myIntent.putExtra("EXTRA_PIN", contact.get(position).Pin);
                        myIntent.putExtra("EXTRA_TYPE", "channel");
                        MainActivity.chat_with = contact.get(position).Email;
                        startActivityForResult(myIntent, 0);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

                Toast.makeText(getActivity().getApplicationContext(), "Failed to read value." + error.toException(), Toast.LENGTH_SHORT).show();
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return rootView;
    }
}
