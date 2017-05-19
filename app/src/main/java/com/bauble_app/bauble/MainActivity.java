package com.bauble_app.bauble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static final boolean DEBUG = false; // set to true if want to debug

    private DatabaseReference mDatabase; // for accessing JSON
    private FragmentManager fragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragManager = getSupportFragmentManager();
        fragManager.beginTransaction()
                .replace(R.id.loading_content, new LoadingFragment())
                .commit();

        loadData();
    }

    public void loadData() {
        // TODO: need loading bar / splash screen for wait time for getting data
        // Load data from firebase to singleton
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = mDatabase.child("stories");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for (DataSnapshot snap : data) {
                    if (DEBUG) {
                        Log.i("MainActivity", snap.child("title").toString() + snap.child("chains").toString());
                    }
                    String title = "";
                    if (snap.hasChild("title")) {
                        title = snap.child("title").getValue(String.class);
                    }
                    Long chains = 0L;
                    if (snap.hasChild("chains")) {
                        chains = snap.child("chains").getValue(Long.class);
                    }
                    String author = "";
                    if (snap.hasChild("author")) {
                        author = snap.child("author").getValue(String.class);
                    }
                    long plays = 0L; // previously not set to data
                    if (snap.hasChild("plays")) {
                        snap.child("plays")
                                .getValue(Long.class);
                    }
                    long time = 0L;
                    if (snap.hasChild("duration")) {
                        time = snap.child("duration").getValue(Long.class);
                    }
                    String expire = "";
                    if (snap.hasChild("expiration")) {
                        expire = snap.child("expiration").getValue(String.class);
                    }

                    // String title, int durration, int chains, String expireDate, int plays
                    StoryObject story = new StoryObject(title, author, time, chains, expire, plays);
                    story.setUniqueId(snap.getKey());
                    if (snap.child("children").getChildren() != null) {
                        for (DataSnapshot child : snap.child("children").getChildren()) {
                            if (child.getValue(Boolean.class)) {
                                story.addChildStory(child.getKey());
                            }
                        }
                        Log.i("MainNavActivity", story.getChildren().toString());
                    }
                    if (!StorySingleton.getInstance().containsStory(story)) {
                        if (DEBUG) {
                            Log.e("MainNavActivity", "" + StorySingleton.getInstance().containsStory(story));
                            Log.e("MainNavActivity", story.toString());
                            Log.e("MainNavActivity", snap.getKey().toString());
                        }
                        story.setUniqueId(snap.getKey().toString());
                        StorySingleton.getInstance().addStory(story);
                    }
                }
                Intent intent = new Intent(getApplicationContext(), MainNavActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainNavActivity", "Database Error");
            }
        });
    }
}
