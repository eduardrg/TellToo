package com.bauble_app.bauble.create;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bauble_app.bauble.MyDBHelper;
import com.bauble_app.bauble.R;
import com.bauble_app.bauble.StoryObject;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.io.File;
import java.io.FileOutputStream;
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

    // Create a StoryObject for upload to Firebase Database
    // The cover and audio params are the paths of the cover image and audio
    // file in Firebase Storage, respectively. These are only available after
    // we upload, so they are not stored in the CreateFragment parent.
    private StoryObject makeStoryObject() {
        StoryObject so = new StoryObject(mRecordingStoragePath, mCreateFrag
                        .getAuthor()
                , mThumbnailStoragePath,
                mCreateFrag.getTitle());
        so.setUniqueId(String.valueOf(so.hashCode()));
        so.setParent(mCreateFrag.getReplyParent());
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit, container, false);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        mCreateFrag = (CreateFragment) getParentFragment();
        mPicker = (HoloCircleSeekBar) v.findViewById(R.id.picker);
        Button nextBtn = (Button) mCreateFrag.getView().findViewById(R.id
                .create_next_btn);
        nextBtn.setText("Submit");
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                // Upload the audio file and thumbnail files to FirebaseStorage
                    Uri audioFile = Uri.fromFile(new File(mCreateFrag
                            .getRecordingPath()));
                    Uri imageFile = Uri.fromFile(new File(mCreateFrag
                        .getThumbnailPath()));

                    Bitmap thumbBitmap = BitmapFactory.decodeFile(imageFile
                            .getPath());

                // Make a StoryObject from the data stored in CreateFrag
                StoryObject story = makeStoryObject();
                String key = story.grabUniqueId();

                // If this is a reply, update the parent to mark the reply as
                // a child
                // This will only update the last found "parent" row in the
                // DB if there are duplicates in the cursor!
                StoryObject parent = story.getParent();
                if (parent != null) {
                    Cursor cursor = mDB.selectRecordWithKey(parent.grabUniqueId());
                    parent.addChildStory(key);
                    mDB.updateRecord(parent);
                }
                // Add this story to the list of stories
                mDB.createRecord(story);
/*
                StoryObject story = makeStoryObject();
                DatabaseReference dbStoriesRef = mDatabase.getReference().child
                        ("stories");
                String key = dbStoriesRef.push().getKey();
                dbStoriesRef.child(key).setValue(story);
                StoryObject parent = story.getParent();
                // Add this story as a child of its parent in Firebase Database
                if (parent != null) {
                    dbStoriesRef.child(parent.grabUniqueId()).child("children")
                            .child(key).setValue(true);
                }

                // Add any new tags that this story possesses to the master
                // list of tags
                DatabaseReference dbTagsRef = mDatabase.getReference().child
                        ("tags");
                Map<String, Boolean> tags = story.getTags();
                if (tags != null) {
                    for (String tag : tags.keySet()) {
                        final DatabaseReference dbTagRef = dbTagsRef.child
                                (tag);
                        final String storyKey = key;
                        // Increment the count of stories tagged with this tag
                        dbTagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long prevCount = 1L;
                                if (dataSnapshot.hasChild("taggedCount")) {
                                    prevCount = dataSnapshot.child
                                            ("taggedCount").getValue(Long.class);
                                    prevCount++;
                                }
                                dbTagRef.child("taggedCount").setValue
                                        (prevCount);
                                dbTagRef.child(storyKey).setValue(true);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
*/
                StorageReference testStoriesRef = mStorage.child
                        ("teststories/" + key +
                                ".m4a");
                StorageReference thumbnailsRef = mStorage.child
                        ("thumbnails/" + key + ".jpg");

                String thumbRoot = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_PICTURES).toString();
                File myDir = new File(thumbRoot + "/saved_images");
                myDir.mkdirs();
                String fname = key + ".png";
                File file = new File(myDir, fname);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    thumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                    testStoriesRef.putFile(audioFile)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String path = taskSnapshot.getStorage().toString();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            });

                    thumbnailsRef.putFile(imageFile).addOnSuccessListener(new
                            OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String path = taskSnapshot.getStorage()
                                            .toString();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                */
            }
        });

        return v;
    }

}
