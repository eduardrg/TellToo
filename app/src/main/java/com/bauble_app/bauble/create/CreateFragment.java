package com.bauble_app.bauble.create;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bauble_app.bauble.BuildConfig;
import com.bauble_app.bauble.CustomText;
import com.bauble_app.bauble.R;
import com.bauble_app.bauble.StoryObject;
import com.bauble_app.bauble.StorySingleton;
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
    private Button mNextButton;

    private FragmentManager mChildFragManager;
    private StorySingleton mStorySingleton;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String FDBTag = "FDB";
    private String mReplyStoryKey;

    private String[] mTags;
    private String mAccess;
    private String mExpiration;
    private RecordFragment mRecordFrag;

    public CreateFragment() {
        // Required empty public constructor
    }

    public RecordFragment getRecordFrag() {
        return mRecordFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorySingleton = StorySingleton.getInstance();
        mChildFragManager = getChildFragmentManager();
        mAuthor = "Anonymous";
        mTitle = "Untitled";
        mThumbnailPath = "android.resource://"+ BuildConfig
                .APPLICATION_ID+"‌​/" + R.drawable.place_holder_img;
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference users = mDatabase.getReference("users");
        users.child(userId).child("name").addValueEventListener(new ValueEventListener() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create, container, false);

        mNextButton = (Button) v.findViewById(R.id.create_next_btn);
        // Insert the fragment that handles showing the story being replied
        // to if this is going to be a reply
        if (getArguments() != null) {
            mReplyStoryKey = getArguments().getString("replyStoryKey", null);
        }
        if (mReplyStoryKey != null) {
            CustomText replyTitle = (CustomText) v.findViewById(R.id
                    .create_reply_title);
            replyTitle.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            bundle.putString("replyStoryKey", mReplyStoryKey);
            Fragment replyFrag = new ReplyFragment();
            replyFrag.setArguments(bundle);
            mChildFragManager.beginTransaction().replace(R.id
                    .create_reply, replyFrag).commit();
        }

        // Insert the fragment that handles setting a story's metadata (cover
        // image, title)
        Fragment setMetaFrag = new SetMetaFragment();
        mChildFragManager.beginTransaction().replace(R.id.create_set_meta, setMetaFrag)
                .commit();

        // Insert the fragment that handles recording
        mRecordFrag =  new RecordFragment();
        mChildFragManager.beginTransaction().replace(R.id
                .create_tools, mRecordFrag).commit();

        mNextButton.setOnClickListener(mRecordFrag.getNextListener());
        return v;
    }

    // Toggle whether the button to proceed to the next step is clickable
    // This is used to prevent the user from advancing if they have not
    // completed a necessary step (i.e., recording a story)
    /*
    void toggleNextVisible(boolean enable) {
        if (enable) {
            mNextButton.setVisibility(View.VISIBLE);
        } else {
            mNextButton.setVisibility(View.INVISIBLE);
        }
    }
    */

    public StoryObject getReplyParent() {
        return mStorySingleton.getStory(mReplyStoryKey);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setNextListener(View.OnClickListener listener) {
        this.mNextButton.setOnClickListener(listener);
    }

    public String[] getmTags() {
        return mTags;
    }

    public void setmTags(String[] mTags) {
        this.mTags = mTags;
    }

    public String getmAccess() {
        return mAccess;
    }

    public void setmAccess(String mAccess) {
        this.mAccess = mAccess;
    }

    public String getmExpiration() {
        return mExpiration;
    }

    public void setmExpiration(String mExpiration) {
        this.mExpiration = mExpiration;
    }
}
