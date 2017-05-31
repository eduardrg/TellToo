package com.bauble_app.bauble.create;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bauble_app.bauble.MainNavActivity;
import com.bauble_app.bauble.MyDBHelper;
import com.bauble_app.bauble.R;
import com.bauble_app.bauble.StoryObject;
import com.bauble_app.bauble.StorySingleton;
import com.bauble_app.bauble.ViewFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {
    private String mThumbnailStoragePath;
    private String mRecordingStoragePath;
    private MediaPlayer mPlayer;
    private CreateFragment mCreateFrag;
    private HoloCircleSeekBar mPicker;

    // Firebase stuff
    private StorageReference mStorage;
    private FirebaseDatabase mDatabase;
    private DateFormat mDateFormat;
    private Calendar mCalendar;
    private MyDBHelper mDB;
    private Gson mGson;

    private StorySingleton mStorySingleton;
    private StoryObject mParent;
    private View mView;

    // Create a StoryObject for upload to Firebase Database
    // The cover and audio params are the paths of the cover image and audio
    // file in Firebase Storage, respectively. These are only available after
    // we upload, so they are not stored in the CreateFragment parent.
    private StoryObject makeStoryObject() {
        /*
        Gson gsonDebug = new GsonBuilder().setPrettyPrinting().create();
         */

        StoryObject so = new StoryObject(mRecordingStoragePath, mCreateFrag
                        .getAuthor()
                , mThumbnailStoragePath,
                mCreateFrag.getTitle());
        so.setUniqueId("" + so.hashCode());
        if (mParent != null) {
            so.setParent(mParent);
            so.setParentString(mParent.grabUniqueId());
        }
        so.setTags(mCreateFrag.getmTags());
        so.setAccess(mCreateFrag.getmAccess());
        so.setExpiration(mCreateFrag.getmExpiration());
        String created = so.getCreated();
        String expiration = so.getExpiration();
        try {
            mCalendar.setTime(mDateFormat.parse(created));
            mCalendar.add(Calendar.DATE, mPicker.getValue());  // set to expire
            // mPicker.getValue (# of days) after created date
            expiration = mDateFormat.format(mCalendar.getTime());
        } catch (ParseException e) {
            // do nothing
        }

        so.setExpiration(expiration);

        if (mParent != null) {
            mParent.addChildStory(so.grabUniqueId());
            /*
            String replyDebug = gsonDebug.toJson(mParent, StoryObject.class);
            System.out.println(replyDebug);
            */
        }

        // String soDebug = gsonDebug.toJson(so, StoryObject.class);
        // System.out.println(soDebug);

        return so;
    }


    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.US);
        mCalendar = Calendar.getInstance();
        mDB = new MyDBHelper(getContext());
        mGson = new Gson();
        mStorySingleton = StorySingleton.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit, container, false);
        mView = v;
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        mCreateFrag = (CreateFragment) getParentFragment();
        mPicker = (HoloCircleSeekBar) v.findViewById(R.id.picker);
        mParent = mCreateFrag.getReplyParent();
        Button nextBtn = (Button) mCreateFrag.getView().findViewById(R.id
                .create_next_btn);

        nextBtn.setText("Submit");
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                // args: params, progress, result>
                // TODO: show progress bar while task is executing
                AsyncTask<Void, Void, Boolean> uploadTask = new AsyncTask<Void,
                        Void, Boolean>() {
                    private String mKey;

                    //Before the background task
                    @Override
                    protected void onPreExecute() {
                    }

                    @SuppressWarnings("ResultOfMethodCallIgnored")
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        // Make a StoryObject from the data stored in CreateFrag
                        StoryObject story;
                        try {
                            story = makeStoryObject();
                            mKey = story.grabUniqueId();
                        } catch (Exception e) {
                            Log.e("UploadFragment", "Failed to " +
                                    "makeStoryObject():\n"
                                    + e.getMessage());
                            return false;
                        }

                        // Get story cover image
                        try {
                            Bitmap thumbBitmap;
                            // Image is not set
                            if (mCreateFrag.getThumbnailPath().isEmpty()) {
                                return false;
                            // Get selected image
                            } else {
                                Uri imageFile = Uri.fromFile(new File(mCreateFrag
                                        .getThumbnailPath()));
                                thumbBitmap = BitmapFactory.decodeFile(imageFile
                                        .getPath());
                            }

                            File myDir = new File(MainNavActivity.THUMB_ROOT_DIR);
                            myDir.mkdirs();
                            String fname = mKey + ".png";
                            File file = new File(myDir, fname);
                            if (file.exists()) {
                                file.delete();
                            }
                            FileOutputStream out = new FileOutputStream(file);
                            thumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            Log.e("UploadFragment", "Error getting story's " +
                                    "cover image:\n"
                                    + e.getMessage());
                            return false;
                        }

                        // Get story audio
                        String durationString = "0";
                        MediaMetadataRetriever mmr = null;
                        try {
                            File from = new File(mCreateFrag
                                    .getRecordingPath());
                            File temp = new File(MainNavActivity.STORY_ROOT_DIR);
                            temp.mkdirs();
                            String audioFileName = mKey + ".m4a";

                            // Get audio duration
                            mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(from.getPath());
                            durationString = mmr.extractMetadata
                                    (MediaMetadataRetriever.METADATA_KEY_DURATION);
                            File to = new File(temp, audioFileName);
                            if (to.exists()) {
                                to.delete();
                            }
                            from.renameTo(to);
                        } catch (Exception e) {
                            Log.e("UploadFragment", "Error getting story's " +
                                    "audio:\n"
                                    + e.getMessage());
                            return false;
                        } finally {
                            if (mmr != null) {
                                mmr.release();
                                mmr = null;
                            }
                        }

                        // If this is a reply, update the parent to mark the reply as
                        // a child
                        // This will only update the last found "parent" row in the
                        // DB if there are duplicates in the cursor
                        if (mParent != null) {
                            mDB.updateRecord(mParent);
                            mStorySingleton.putStory(mParent);
                        }

                        // Save the duration
                        long duration_ms = Long.parseLong(durationString);
                        long duration = duration_ms / 1000;
                        story.setDuration(duration);

                        // Add this story to the list of stories
                        mDB.createRecord(story);
                        mStorySingleton.addStory(story);
                        return true;
                    }

                    // The UI thread; update the UI after task is done
                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_logout,
                                    (ViewGroup) mView.findViewById(R.id
                                            .toast_layout_root));

                            TextView text = (TextView) layout.findViewById(R.id.toast_save_text);
                            text.setText("Story uploaded!");

                            Toast toast = new Toast(getContext().getApplicationContext());
                            //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setGravity(Gravity.BOTTOM, 0, 200);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            mStorySingleton.setViewKey(mKey);
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().replace
                                    (R.id.content, new ViewFragment())
                                    .commit();
                        } else if (mCreateFrag.getThumbnailPath().isEmpty()) {
                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_logout,
                                    (ViewGroup) mView.findViewById(R.id
                                            .toast_layout_root));

                            TextView text = (TextView) layout.findViewById(R.id.toast_save_text);
                            text.setText("Please select an " +
                                    "image!");

                            Toast toast = new Toast(getContext().getApplicationContext());
                            //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setGravity(Gravity.BOTTOM, 0, 200);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        } else {
                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_logout,
                                    (ViewGroup) mView.findViewById(R.id
                                            .toast_layout_root));

                            TextView text = (TextView) layout.findViewById(R.id.toast_save_text);
                            text.setText("Upload failed");

                            Toast toast = new Toast(getContext().getApplicationContext());
                            //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setGravity(Gravity.BOTTOM, 0, 200);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        }
                    }
                };
                uploadTask.execute((Void[]) null);
            }
        });

        mCreateFrag.removeSkip();
        return v;
    }

}
