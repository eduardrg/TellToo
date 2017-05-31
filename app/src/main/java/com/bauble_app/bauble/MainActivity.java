package com.bauble_app.bauble;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    public static final boolean DEBUG = false; // set to true if want to debug

    private FragmentManager fragManager;
    private MyDBHelper mDB;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragManager = getSupportFragmentManager();
        fragManager.beginTransaction()
                .replace(R.id.loading_content, new LoadingFragment())
                .commit();

        mDB = new MyDBHelper(getApplicationContext());
        mGson = new Gson();
        loadData();
    }

    public void loadData() {
        Cursor cursor = mDB.selectRecords();
        try {
            while (cursor.moveToNext()) {
                String storyAsString = cursor.getString(cursor.getColumnIndex
                        (MyDBHelper.STORY_OBJ));
                StoryObject so = mGson.fromJson(storyAsString, StoryObject.class);

                if (!StorySingleton.getInstance().containsStory(so)) {
                    if (DEBUG) {
                        Log.e("MainNavActivity", "" + StorySingleton.getInstance().containsStory(so));
                        Log.e("MainNavActivity", so.toString());
                        Log.e("MainNavActivity", so.grabUniqueId());
                    }
                    StorySingleton.getInstance().addStory(so);
                }
            }
            StorySingleton.getInstance().setDonationProgress(1);
        } finally {
            cursor.close();
            Intent intent = new Intent(getApplicationContext(), MainNavActivity.class);
            startActivity(intent);
        }
    }

}
