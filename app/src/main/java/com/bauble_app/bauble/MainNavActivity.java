package com.bauble_app.bauble;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bauble_app.bauble.auth.AuthChoiceFragment;
import com.bauble_app.bauble.create.CreateFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainNavActivity extends MainActivity {

    private DatabaseReference mDatabase; // for accessing JSON
    private TextView mTextMessage;
    private FragmentManager fragManager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_front:
                    fragManager.beginTransaction()
                            .replace(R.id.content, new FeedFragment())
                            .commit();
                    return true;
                case R.id.navigation_explore:
                    fragManager.beginTransaction().replace(R.id.content, new
                            ExploreFragment()).commit();
                    return true;
                case R.id.navigation_create:
                    Fragment createFrag = new CreateFragment();
                    /*
                    // Decide whether to show signup or create screen
                    if (mAuth.getCurrentUser() == null) {
                        createFrag = new AuthChoiceFragment();
                    } else {
                        createFrag = new CreateFragment();
                    }
                    */
                    fragManager.beginTransaction()
                            .replace(R.id.content, createFrag).commit();
                    return true;
                case R.id.navigation_me:
                    Fragment meFrag;
                    // Decide whether to show signup or profile screen
                    if (mAuth.getCurrentUser() == null) {
                        meFrag = new AuthChoiceFragment();
                    } else {
                        meFrag = new ProfileFragment();
                    }
                    fragManager.beginTransaction()
                            .replace(R.id.content, meFrag)
                            .commit();
                    return true;
                case R.id.navigation_messages:
                    fragManager.beginTransaction()
                            .replace(R.id.content, new FrontFragment())
                            .commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        fragManager = getSupportFragmentManager();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
                    Long chains = snap.child("chain").getValue(Long.class);
                    String author = snap.child("author").getValue(String.class);
                    Long plays = snap.child("play").getValue(Long.class);
                    Long time = snap.child("duration").getValue(Long.class);
                    String expire = snap.child("expiration").getValue(String.class);
                    // String title, int durration, int chains, String expireDate, int plays
                    StoryObject story = new StoryObject(title, author, time, chains, expire, plays);
                    Log.e("MainNavActivity", story.toString());
                    StorySingleton.getInstance().addStory(story);
                }
                fragManager.beginTransaction()
                        .replace(R.id.content, new FeedFragment())
                        .commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainNavActivity", "Database Error");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Prevent the search bar from collapsing
//        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                return false; //never collapse
//            }
//        });

        SearchManager searchManager = (SearchManager) MainNavActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Make searchview spans action bar
        searchView.setIconifiedByDefault(false);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        searchView.setLayoutParams(params);
        // searchItem.expandActionView(); // Start with search open


        return true;
    }

    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    public FragmentManager getMyFragManager() {
        return this.fragManager;
    }

}
