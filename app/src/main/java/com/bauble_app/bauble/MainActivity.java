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
                    String title = snap.child("title").getValue(String.class);
                    Long chains = snap.child("chain").getValue(Long.class);
                    String author = snap.child("author").getValue(String.class);
                    Long plays = snap.child("play").getValue(Long.class);
                    Long time = snap.child("duration").getValue(Long.class);
                    String expire = snap.child("expiration").getValue(String.class);
                    // String title, int durration, int chains, String expireDate, int plays
                    StoryObject story = new StoryObject(title, author, time, chains, expire, plays);
                    if (snap.child("children").getChildren() != null) {
                        for (DataSnapshot child : snap.child("children").getChildren()) {
                            story.addChildStory(child.getValue(String.class));

                        }
                        Log.i("MainNavActivity", story.getChildren().toString());
                    }
                    if (!StorySingleton.getInstance().containsStory(story)) {
                        Log.e("MainNavActivity", "" + StorySingleton.getInstance().containsStory(story));
                        Log.e("MainNavActivity", story.toString());
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
