package com.bauble_app.bauble;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by princ on 5/28/2017.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase writeableDB;
    private SQLiteDatabase readableDB;

    public final static String STORY_TABLE ="stories"; // name of table

    public final static String STORY_ID ="_id"; // default primary key column
    // convenient to have for certain widgets
    public final static String STORY_KEY ="uniqueId";  // story's unique id
    public final static String STORY_OBJ ="story"; //story stored as JSON string
    private Gson mGson;

    // Method for turning stories into JSON and storing them in our
    // SQLite DB
    public long createRecord(StoryObject story){
        if (story != null) {
            String storyAsString = mGson.toJson(story);
            String storyKey = story.grabUniqueId();
            ContentValues values = new ContentValues();
            values.put(STORY_KEY, storyKey);
            values.put(STORY_OBJ, storyAsString);
            return writeableDB.insert(STORY_TABLE, null, values);
        } else {
            throw new IllegalArgumentException("Cannot call createRecord" +
                    "(StoryObject story) with a null StoryObject");
        }
    }

    // Method for getting stories as JSON from the DB
    public Cursor selectRecordWithKey(String storyKey) {
        String[] cols = new String[] {STORY_ID, STORY_KEY, STORY_OBJ};
        String whereCondition = STORY_KEY + " = \"" + storyKey + "\"";
        Cursor mCursor = readableDB.query(true, STORY_TABLE, cols, whereCondition, null, null, null, null, null);
        return mCursor;
    }

    // Get a single story in its string representation by its key
    public String selectSingleRecordAsString(String storyKey) {
        Cursor cursor = selectRecordWithKey(storyKey);
        String storyAsString = null;
        try {
            while (cursor.moveToNext()) {
                storyAsString = cursor.getString(cursor.getColumnIndex(STORY_OBJ));
            }
        } finally {
            cursor.close();
            return storyAsString;
        }
    }

    // Get a single story in its JSON representation by its key
    public JsonObject selectSingleRecordAsJSON(String storyKey) {
        String storyAsString = selectSingleRecordAsString(storyKey);
        return (new JsonParser()).parse(storyAsString).getAsJsonObject();
    }

    // Method for getting stories as JSON from the DB
    public Cursor selectRecords() {
        String[] cols = new String[] {STORY_ID, STORY_KEY, STORY_OBJ};
        Cursor mCursor = readableDB.query(true, STORY_TABLE, cols, null, null,
                null, null, null, null);
        return mCursor;
    }

    // Method for updating an existing row, taking a parameter assumed to be
    // a StoryObject as a JsonObject
    public void updateRecord(JsonObject storyAsJSON) {
        if (storyAsJSON != null) {
            String storyKey = storyAsJSON.get("uniqueId").getAsString();
            String storyAsString = storyAsJSON.toString();
            String whereCondition = STORY_KEY + " = " + storyKey;
            ContentValues values = new ContentValues();
            values.put(STORY_OBJ, storyAsString);
            writeableDB.update(STORY_TABLE, values, whereCondition, null);
        } else {
            throw new IllegalArgumentException("Cannot call updateRecord" +
                    "(JsonObject storyAsJSON) with a null JsonObject");
        }
    }

    // Method for updating an existing row, taking a StoryObject parameter to
    // update its corresponding row
    public void updateRecord(StoryObject so) {
        if (so != null) {
            String storyKey = so.grabUniqueId();
            String storyAsString = mGson.toJson(so, StoryObject.class);
            String whereCondition = STORY_KEY + " = \"" + storyKey + "\"";
            ContentValues values = new ContentValues();
            values.put(STORY_OBJ, storyAsString);
            writeableDB.update(STORY_TABLE, values, whereCondition, null);
        } else {
            throw new IllegalArgumentException("Cannot call updateRecord" +
                    "(StoryObject so) with a null StoryObject");
        }
    }

    public void incrementPlays(String storyKey) {
        JsonObject storyAsJson = selectSingleRecordAsJSON(storyKey);
        Long storyPlays = storyAsJson.get("plays").getAsLong();
        storyPlays++;
        JsonElement storyPlaysAsJSON = (new JsonParser()).parse(storyPlays
                .toString());
        storyAsJson.add("plays", storyPlaysAsJSON);
        updateRecord(storyAsJson);
    }

    private static final String DATABASE_NAME = "TellTooDB";

    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_DROP = "DROP TABLE IF EXISTS stories;";
    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS stories (" +
            "_id integer PRIMARY KEY ASC," +
            "uniqueId varchar(128) NULL," +
            "story TEXT NULL" +
            ");";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mGson = new Gson();
        readableDB = this.getReadableDatabase();
        writeableDB = this.getWritableDatabase();
    }

    public void resetDB() {
        writeableDB.execSQL(DATABASE_DROP);
        writeableDB.execSQL(DATABASE_CREATE);
    }

    // Method is called during creation of the readableDB
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_DROP);
        database.execSQL(DATABASE_CREATE);
    }

    // Method is called during an upgrade of the readableDB,
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int
            newVersion){
        Log.w(MyDBHelper.class.getName(),
                "Upgrading readableDB from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS stories");
        onCreate(database);
    }

}
