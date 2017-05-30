package com.bauble_app.bauble;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bauble_app.bauble.create.CreateFragment;
import com.bauble_app.bauble.explore.ExploreFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainNavActivity extends AppCompatActivity {
    public static final String THUMB_ROOT_DIR = Environment
            .getExternalStoragePublicDirectory
            (Environment.DIRECTORY_PICTURES) + "/saved_images";
    public static final String STORY_ROOT_DIR = Environment
            .getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_MUSIC) + "/saved_stories";

    private TextView mTextMessage;
    private FragmentManager mFragManager;
    private FirebaseAuth mAuth;
    private BottomNavigationView mNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_front:
                    mFragManager.beginTransaction()
                            .replace(R.id.content, new FeedFragment())
                            .commit();
                    return true;
                case R.id.navigation_explore:
                    mFragManager.beginTransaction().replace(R.id.content, new
                            ExploreFragment()).commit();
                    return true;
                case R.id.navigation_create:
                    Fragment frag;
                    /*
                    if (mAuth.getCurrentUser() == null) {
                        // User is unauthenticated; show signup screen
                        frag = new AuthChoiceFragment();
                    } else {
                        frag = new CreateFragment();
                    }
                    */
                    frag = new CreateFragment();
                    mFragManager.beginTransaction()
                            .replace(R.id.content, frag).commit();
                    return true;
                case R.id.navigation_me:
                    Fragment meFrag;
                    /*
                    // Decide whether to show signup or profile screen
                    if (mAuth.getCurrentUser() == null) {
                        meFrag = new AuthChoiceFragment();
                    } else {
                        meFrag = new ProfileFragment();
                    }
                    */
                    meFrag = new ProfileFragment();
                    mFragManager.beginTransaction()
                            .replace(R.id.content, meFrag)
                            .commit();
                    return true;
                case R.id.navigation_messages:
                    mFragManager.beginTransaction()
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

        mFragManager = getSupportFragmentManager();
        mFragManager.beginTransaction()
                .replace(R.id.content, new FeedFragment())
                .commit();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mFragManager.beginTransaction()
                .replace(R.id.content, new FeedFragment())
                .commit();

    }

    public BottomNavigationView getNav() {
        return this.mNavigation;
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

/*
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.
    }
*/
    public FragmentManager getMyFragManager() {
        return this.mFragManager;
    }

}
