package com.bauble_app.bauble.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bauble_app.bauble.FontHelper;
import com.bauble_app.bauble.MainNavActivity;
import com.bauble_app.bauble.R;
import com.bauble_app.bauble.StoryObject;
import com.bauble_app.bauble.StorySingleton;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by princ on 5/17/2017.
 */

public class ReplyFragment extends Fragment {
    private String mReplyStoryKey;
    private StorageReference mStorageReference;

    public ReplyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.listitem_feed, container, false);

        mStorageReference = FirebaseStorage.getInstance().getReference();

        if (getArguments() != null) {
            mReplyStoryKey = getArguments().getString("replyStoryKey", null);
        }
        if (mReplyStoryKey != null) {
            // Load the parent story's data into the ReplyFragment layout
            StoryObject so = StorySingleton.getInstance().getStory
                    (mReplyStoryKey);
            TextView title = (TextView) v.findViewById(R.id.feed_listitem_title);
            String titleString = so.getTitle();
            title.setText(titleString);
            TextView author = (TextView) v.findViewById(R.id.feed_listitem_author);
            String authorString = so.getAuthor();
            author.setText(authorString);
            TextView time = (TextView) v.findViewById(R.id.feed_listitem_length);
            time.setText("00:" + so.getDuration().toString());
            TextView chains = (TextView) v.findViewById(R.id.feed_listitem_chains);
            chains.setText(so.getChains().toString());
            TextView expire = (TextView) v.findViewById(R.id.feed_listitem_expire);
            expire.setText(so.getExpireDate());
            TextView plays = (TextView) v.findViewById(R.id.feed_listitem_plays);
            plays.setText(so.getPlays().toString());

            String imageFileName = so.grabUniqueId() + ".png";
            File imageFile = new File(MainNavActivity.THUMB_ROOT_DIR,
                    imageFileName);

            // ImageView in your Activity
            CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id
                    .feed_listitem_picture);

            // Load the image using Glide
            Glide.with(getContext() /* context */)
                    .load(imageFile)
                    .into(circleImageView);

            // Set font
            title.setTypeface(FontHelper.getTypeface("Lato-Italic", getContext()));
        }

        return v;
    }
}
