package com.bauble_app.bauble;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    public ListView listView;
    public FeedAdapter adapter;
    private FragmentManager fragManager;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    public FeedFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_feed, container,
                false);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.

        // TODO: need loading bar / splash screen for wait time for getting data
        // Load data from firebase to singleton
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = mDatabase.child("stories");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot snap : data) {
                    String title = snap.child("title").getValue(String.class);
                    long chains = snap.child("chains").getValue(Long.class);
                    String author = snap.child("author").getValue(String.class);
                    long plays = snap.child("plays").getValue(Long.class);
                    long time = snap.child("duration").getValue(Long.class);
                    String expire = snap.child("expiration").getValue(String.class);
                    // String title, int durration, int chains, String expireDate, int plays
                    StoryObject story = new StoryObject(title, author, time, chains, expire, plays);
                    if (snap.child("children").getChildren() != null) {
                        for(DataSnapshot child : snap.child("children").getChildren()) {
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
                // Get ListView object from xml
                listView = (ListView) v.findViewById(R.id.feed_list);


                // Old code for hard coding stories in
                /*
                List<StoryObject> list = new ArrayList<StoryObject>();

                for (int i = 1; i <= 10; i++) {
                    list.add(new StoryObject(i + " Title " + i));
                }
        */

                adapter = new FeedAdapter(getContext(), StorySingleton.getInstance().storyList);

                // Assign adapter to ListView
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainNavActivity", "Database Error");
            }
        });

        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = (ListView) view.findViewById(R.id
                .feed_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //ARGUMENTS: AdapterView<?> parent, View view, int position, long id

                StorySingleton.getInstance().setViewStory(arg2);

                // Placeholder for transition to view
                FeedFragment.this.fragManager = getActivity().getSupportFragmentManager();
                // Placeholder frag transaction
                fragManager.beginTransaction()
                        .replace(R.id.content, new ViewFragment())
                        .commit();
                // Toast.makeText(getActivity().getApplicationContext(), "Text message", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
