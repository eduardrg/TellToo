package com.bauble_app.bauble.create;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bauble_app.bauble.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

/**
 * Created by princ on 5/18/2017.
 */

public class TagFragment extends Fragment {
    private CreateFragment mCreateFrag;
    private DatabaseReference mReference;
    private List<String> mAddedTags;
    private List<String> mSuggestedTags;
    private TagGroup mSuggestedTagGroup;
    private TagGroup mAddedTagGroup;

    public TagFragment() {
        // required constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReference = FirebaseDatabase.getInstance().getReference();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tags, container, false);

        // Initalize suggested tags TagGroup
        /*
        mSuggestedTagGroup = (TagGroup) v.findViewById(R.id.tag_group2);
        mSuggestedTagGroup.setAllowRepeats(false);
*/
        // Initalize suggested tags list
        //mSuggestedTags = new ArrayList<String>();

        // Load tags from Firebase Database into suggested tags TagGroup
        /*
        mReference.child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
            final TagGroup tagGroup = mSuggestedTagGroup;
            final List<String> tagList = mSuggestedTags;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                for (DataSnapshot snap : it) {
                    tagList.add(snap.getKey());
                }
                tagGroup.setTags(tagList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        */

        // Initalize TagGroup for tagging this story
        mAddedTagGroup = (TagGroup) v.findViewById(R.id.tag_group1);
        mAddedTagGroup.setAllowRepeats(false);

        // We need to track added tags independently of the
        // AddedTagGroup's internal array, because we want to be able to add
        // tags to one TagGroup by clicking them in another TagGroup.
        mAddedTags = new ArrayList<String>();

        mAddedTagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public boolean onAppend(TagGroup tagGroup, String tag) {
                mAddedTags.add(tag);
                return true;
            }

            @Override
            public void onDelete(TagGroup tagGroup, String tag) {
                // mAddedTagGroup doesn't know about mAddedTags, so each time
                // a tag is deleted from mAddedTagGroup we have to manually
                // remove it from the mAddedTags ArrayList
                mAddedTags.remove(tag);
            }
        });

        // Initialize AutoComplete using suggested tags
        // Suggested tags are currently all tags that exist in the database

        // this is very buggy, don't use unless we can fix the library
        // mAddedTagGroup.setAutoCompleteTags(mSuggestedTags);

        // Clicking on a "suggested" tag should add it to the "added" tags
        /*
        mSuggestedTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                // mAddedTagGroup doesn't know about mAddedTags, so each time
                // a tag is added by clicking in mSuggestedTagGroup, we have to
                // manually add it to our ArrayList
                if  (!mAddedTags.contains(tag)) {
                    mAddedTags.add(tag);
                    mAddedTagGroup.setTags(mAddedTags);
                }
            }
        });
        */

        mCreateFrag = (CreateFragment) getParentFragment();
        mCreateFrag.setNextListener(this.getNextListener());
        mCreateFrag.setSkipListener(this.getSkipListener());

        return v;
    }

    View.OnClickListener getNextListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                mCreateFrag.setmTags(mAddedTagGroup.getTags());
                Fragment privacyFrag = new PrivacyFragment();
                getFragmentManager().beginTransaction().replace(R.id
                        .create_tools, privacyFrag).commit();
            }
        };
        return listener;
    }

    View.OnClickListener getSkipListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                mCreateFrag.setmTags(mAddedTagGroup.getTags());
                Fragment uploadFrag = new UploadFragment();
                getFragmentManager().beginTransaction().replace(R.id
                        .create_tools, uploadFrag).commit();
            }
        };
        return listener;
    }
}
