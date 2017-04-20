package com.bauble_app.bauble;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainNavActivity extends MainActivity {

    private TextView mTextMessage;
    private FragmentManager fragManager;

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
                    fragManager.beginTransaction()
                            .replace(R.id.content, new CreateFragment())
                            .commit();
                    return true;
                case R.id.navigation_me:
                    fragManager.beginTransaction()
                            .replace(R.id.content, new SignUpFragment())
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

        fragManager = getSupportFragmentManager();
        fragManager.beginTransaction()
                .replace(R.id.content, new FeedFragment())
                .commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
        searchItem.expandActionView();


        return true;
    }

    public FragmentManager getMyFragManager() {
        return this.fragManager;
    }

}
