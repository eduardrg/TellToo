package com.bauble_app.bauble;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainNavActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FragmentManager fragManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_front:
                    mTextMessage.setText(R.string.title_front);
                    fragManager.beginTransaction()
                            .replace(R.id.content, new FrontFragment())
                            .commit();
                    return true;
                case R.id.navigation_explore:
                    mTextMessage.setText(R.string.title_explore);
                    return true;
                case R.id.navigation_create:
                    mTextMessage.setText(R.string.title_create);
                    fragManager.beginTransaction()
                            .replace(R.id.content, new CreateFragment())
                            .commit();
                    return true;
                case R.id.navigation_me:
                    mTextMessage.setText(R.string.title_me);
                    return true;
                case R.id.navigation_messages:
                    mTextMessage.setText(R.string.title_messages);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        fragManager = getSupportFragmentManager();
        fragManager.beginTransaction()
                .replace(R.id.content, new FrontFragment())
                .commit();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
