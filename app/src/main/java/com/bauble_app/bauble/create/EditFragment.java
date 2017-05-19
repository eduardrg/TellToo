package com.bauble_app.bauble.create;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {
    private String mThumbnailStoragePath;
    private String mRecordingStoragePath;
    private PlayButton mPlayButton;
    private MediaPlayer mPlayer;
    private CreateFragment mCreateFrag;

    // Firebase stuff
    private StorageReference mStorage;
    private FirebaseDatabase mDatabase;

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
        return so;
    }


    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit, container, false);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        mCreateFrag = (CreateFragment) getParentFragment();
        appendButtons(v);
        Button upload = (Button) v.findViewById(R.id.edit_upload_btn);
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                // Upload the audio file and thumbnail files to FirebaseStorage
                    Uri audioFile = Uri.fromFile(new File(mCreateFrag
                            .getRecordingPath()));
                    Uri imageFile = Uri.fromFile(new File(mCreateFrag
                        .getThumbnailPath()));

                    Bitmap thumbBitmap = BitmapFactory.decodeFile(imageFile
                            .getPath());


                StorageReference testStoriesRef = mStorage.child
                        ("teststories/" + mCreateFrag.getAuthor() +
                                mCreateFrag.getTitle().replace(" ", "") +
                                ".m4a");
                StorageReference thumbnailsRef = mStorage.child
                        ("thumbnails/" + mCreateFrag.getAuthor() +
                                mCreateFrag.getTitle().replace(" ", "") + ".jpg");
                
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
                            ("thumbnails/" + mCreateFrag.getAuthor() +
                                    mCreateFrag.getTitle().replace(" ", "") + "" +
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

                // Save the other story data to FirebaseDatabase
                StoryObject story = makeStoryObject();
                DatabaseReference dbStoriesRef = mDatabase.getReference().child
                        ("stories");
                String key = dbStoriesRef.push().getKey();
                dbStoriesRef.child(key).setValue(story);

            }
        });

        return v;
    }

    private void appendButtons(View v) {
        ViewGroup layout = (ViewGroup) v.findViewById(R.id
                .edit_root_viewgroup);

        // Initialize buttons
        mPlayButton = new PlayButton(getActivity());

        // We want the buttons to be 60dp x 60dp but LayoutParams takes pixel
        // arguments
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                60,
                getResources().getDisplayMetrics());

        RelativeLayout.LayoutParams mPlayButtonParams = new RelativeLayout
                .LayoutParams(dp, dp);

        mPlayButtonParams.addRule(RelativeLayout.BELOW, R.id.edit_upload_btn);
        mPlayButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        mPlayButton.setLayoutParams(mPlayButtonParams);

        layout.addView(mPlayButton);
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(((CreateFragment) getParentFragment())
                    .getRecordingPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("MP", e.getMessage());
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    // Button that plays or stops the mAudio being played in CreateFragment
    public class PlayButton extends android.support.v7.widget
            .AppCompatImageButton {
        boolean mStartPlaying = true;

        // Listener for the play button that plays or stops the mAudio when
        // the button is tapped
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setImageResource(R.drawable.ic_action_btn_stop);
                } else {
                    setImageResource(R.drawable.ic_action_btn_play);
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        // Constructor
        public PlayButton(Context context) {
            super(context);
            setImageResource(R.drawable.ic_action_btn_play);
            setOnClickListener(clicker);
        }
    }

}
