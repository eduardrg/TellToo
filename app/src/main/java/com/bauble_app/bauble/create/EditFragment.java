package com.bauble_app.bauble.create;


import android.content.Context;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {
    // Firebase stuff
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private PlayButton mPlayButton;
    private MediaPlayer mPlayer;

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit, container, false);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        appendButtons(v);
        Button upload = (Button) v.findViewById(R.id.edit_upload_btn);
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                // Upload the file that was just recorded to FirebaseStorage in a path
                // containing the authenticated user's id
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    Uri file = Uri.fromFile(new File(getArguments().getString
                            ("fileLocation")));
                    StorageReference riversRef = mStorage.child("audio/" + userId +
                            "/" + getArguments().getString("fileName"));

                    riversRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    Toast.makeText(getContext(), downloadUrl.toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getContext(), "upload failed", Toast
                                            .LENGTH_LONG).show();
                                }
                            });
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
            mPlayer.setDataSource(getArguments().getString("fileLocation"));
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

    // Button that plays or stops the audio being played in CreateFragment
    public class PlayButton extends android.support.v7.widget
            .AppCompatImageButton {
        boolean mStartPlaying = true;

        // Listener for the play button that plays or stops the audio when
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
