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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.materialdesigncodelab.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * Provides UI for the main screen.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static String chat_with;
    FirebaseUser currentFirebaseUser;
    public static ImageView img_profil;

    StorageReference storageReference;

    // profil image
    String profil_image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check user login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child("user_" + uid);

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Getting the data from snapshot
                    User_data message = dataSnapshot.getValue(User_data.class);
                    profil_image = message.Get_images();

                    // Create a storage reference from our app
                    storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference islandRef = storageReference.child("images/" + profil_image);

                    final long ONE_MEGABYTE = 1024 * 1024;
                    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            img_profil.setImageBitmap(bitmap);
                            // Data for "images/island.jpg" is returns, use this as needed
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                    System.out.println(error);
                    //Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            // Adding Toolbar to Main screen
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // Setting ViewPager for each Tabs
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);
            // Set Tabs inside Toolbar
            TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
            // Create Navigation drawer and inlfate layout
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            // Adding menu icon to Toolbar
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                VectorDrawableCompat indicator
                        = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
                indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
                supportActionBar.setHomeAsUpIndicator(indicator);
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
            setTitle("Studio Kami");
            // Set behavior of Navigation drawer
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        // This method will trigger on item Click of navigation menu
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            // Set item in checked state
                            //menuItem.setChecked(true);

                            // TODO: handle navigation
                            switch (item.getItemId()) {

                                case R.id.menu_home: {
                                    //do somthing
                                    to_search();
                                    break;
                                }
                                case R.id.menu_two: {
                                    //do somthing
                                    add_channel();
                                    break;
                                }
                                case R.id.menu_three: {
                                    //do somthing
                                    add_group();
                                    break;
                                }
                                case R.id.menu_exit: {
                                    //do somthing
                                    FirebaseAuth.getInstance().signOut();
                                    to_login();
                                    break;
                                }
                            }

                            // Closing drawer on item click
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
            // Adding Floating Action Button to bottom right of main view
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Hello Snackbar!",
                            Snackbar.LENGTH_LONG).show();
                }
            });

            currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            View headerLayout = navigationView.getHeaderView(0); // 0-index header
            // Image View Profil
            img_profil = (ImageView) headerLayout.findViewById(R.id.img_add_profil);
            TextView user_login = (TextView) headerLayout.findViewById(R.id.txt_profil_email);
            if (user != null) {
                user_login.setText(currentFirebaseUser.getEmail());
            } else {
            }

            img_profil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profil();
                }
            });

        } else {
            // login page
            to_login();
        }
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ListContentFragment(), "Personal");
        adapter.addFragment(new TileContentFragment(), "Group");
        adapter.addFragment(new CardContentFragment(), "Channel");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds com.example.android.materialdesigncodelab.activity.items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public void to_login() {
        Intent myIntent = new Intent(this, Login.class);
        //myIntent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(myIntent, 0);
    }

    public void to_search() {
        Intent myIntent = new Intent(this, Search.class);
        myIntent.putExtra("EXTRA_TYPE", "personal");
        startActivityForResult(myIntent, 0);
    }

    public void add_channel() {
        Intent myIntent = new Intent(this, AddChannel.class);
        myIntent.putExtra("EXTRA_TYPE", "channel");
        startActivityForResult(myIntent, 0);
    }

    public void add_group() {
        Intent myIntent = new Intent(this, AddChannel.class);
        myIntent.putExtra("EXTRA_TYPE", "group");
        startActivityForResult(myIntent, 0);
    }

    public void profil() {
        String name = currentFirebaseUser.getEmail();
        String id = currentFirebaseUser.getUid();
        String pin_channel_group = "";
        String type = "personal";
        Intent myIntent;
        if (type.equals("personal")) {
            myIntent = new Intent(this, Profil.class);
        } else {
            myIntent = new Intent(this, DetailActivity.class);
        }
        myIntent.putExtra("EXTRA_TYPE", type);
        myIntent.putExtra("EXTRA_NAME", name);
        myIntent.putExtra("EXTRA_ID", id);
        myIntent.putExtra("EXTRA_PIN", pin_channel_group);
        startActivityForResult(myIntent, 0);
    }
}
