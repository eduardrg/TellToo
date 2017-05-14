package com.bauble_app.bauble.create;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bauble_app.bauble.BuildConfig;
import com.bauble_app.bauble.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFragment extends Fragment {
    private String mTitle;
    private String mAuthor;
    private String mRecordingPath;
    private String mThumbnailPath;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String FDBTag = "FDB";

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mAuthor = "Anonymous";
        mTitle = "Untitled";
        mThumbnailPath = "android.resource://"+ BuildConfig
                .APPLICATION_ID+"‌​/" + R.drawable.place_holder_img;

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference users = mDatabase.getReference("users");
        users.child(userId).child("name").addValueEventListener(new
                                                                   ValueEventListener
                () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAuthor = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FDBTag, "Failed to read user name: " + databaseError
                        .getCode());
            }
        });

        // Insert the fragment that handles setting a story's metadata (cover
        // image, title)
        Fragment setMetaFrag = new SetMetaFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.create_set_meta, setMetaFrag).commit();

        // Insert the fragment that handles recording
        Fragment createToolsFrag = new RecordFragment();
        getChildFragmentManager().beginTransaction().replace(R.id
                .create_tools, createToolsFrag).commit();

        return v;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getRecordingPath() {
        return mRecordingPath;
    }

    public void setRecordingPath(String recordingPath) {
        this.mRecordingPath = recordingPath;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.mThumbnailPath = thumbnailPath;
    }
}
