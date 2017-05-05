package com.bauble_app.bauble;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    MediaPlayer mPlayer;

    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view, container,
                false);

        StoryObject story = StorySingleton.getInstance().getViewStory();
        ImageView thumbnail = (ImageView) v.findViewById(R.id.view_thumbnail);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storage.getReferenceFromUrl("gs://bauble-90a48.appspot.com");
        String imagePath = story.getAuthor() + story.getTitle().replace(" ", "");
        StorageReference pathReference = storageReference.child("thumbnails/" + imagePath + ".png");

        TextView title = (TextView) v.findViewById(R.id.view_title);
        title.setText(story.getTitle());
        TextView author = (TextView) v.findViewById(R.id.view_author);
        author.setText("by " + story.getAuthor());
        TextView time = (TextView) v.findViewById(R.id.view_length);
        time.setText("00:" + story.getDurration());
        TextView chains = (TextView) v.findViewById(R.id.view_chains);
        chains.setText(story.getChains().toString());
        TextView expire = (TextView) v.findViewById(R.id.view_expire);
        expire.setText(story.getExpireDate());
        TextView plays = (TextView) v.findViewById(R.id.view_plays);
        plays.setText(story.getPlays().toString());

        mPlayer = MediaPlayer.create(getContext(), R.raw.law_of_the_jungle);
        mPlayer.start();

        // Load the image using Glide
        Glide.with(getContext() /* context */)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(thumbnail);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }
}
