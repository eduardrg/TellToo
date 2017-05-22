package com.bauble_app.bauble.create;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bauble_app.bauble.R;
import com.bauble_app.bauble.StoryObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


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

    // Create a StoryObject for upload to Firebase Database
    // The cover and audio params are the paths of the cover image and audio
    // file in Firebase Storage, respectively. These are only available after
    // we upload, so they are not stored in the CreateFragment parent.
    private StoryObject makeStoryObject() {
        StoryObject so = new StoryObject(mRecordingStoragePath, mCreateFrag
                        .getAuthor()
                , mThumbnailStoragePath,
                mCreateFrag.getTitle());
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

                // Save the other story data to FirebaseDatabase
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

                StorageReference testStoriesRef = mStorage.child
                        ("teststories/" + key +
                                ".m4a");
                StorageReference thumbnailsRef = mStorage.child
                        ("thumbnails/" + key + ".jpg");
                
                FileOutputStream out = null;
                try {
                    File file = new File(getActivity()
                            .getExternalCacheDir().getAbsolutePath() +
                            "/thumbPng.png");
                    out = new FileOutputStream(file);
                    thumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    imageFile = Uri.fromFile(file);
                    thumbnailsRef = mStorage.child
                            ("thumbnails/" + key +
                                    ".png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                    testStoriesRef.putFile(audioFile)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String path = taskSnapshot.getStorage().toString();
                                    Toast.makeText(getContext(), path,
                                            Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getContext(), "audio " +
                                            "upload failed", Toast
                                            .LENGTH_LONG).show();
                                }
                            });

                    thumbnailsRef.putFile(imageFile).addOnSuccessListener(new
                            OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String path = taskSnapshot.getStorage()
                                            .toString();
                                    Toast.makeText(getContext(), path, Toast
                                            .LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getContext(), "image upload failed",
                                    Toast
                                    .LENGTH_LONG).show();
                        }
                    });



            }
        });

        return v;
    }

}
