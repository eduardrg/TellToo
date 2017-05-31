package com.bauble_app.bauble;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    private void copyAssets(InputStream in) {
        OutputStream out = null;
        try {

            String outDir = MainNavActivity.THUMB_ROOT_DIR ;

            File outFile = new File(outDir, "CapstoneRootStory.png");

            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + e);
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public void loadData() {
        StoryObject capstoneRoot = new StoryObject("", "TellToo", "", "Capstone Night");
        capstoneRoot.setUniqueId("CapstoneRootStory");
        mDB.createRecord(capstoneRoot);
        StorySingleton.getInstance().addStory(capstoneRoot);
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
