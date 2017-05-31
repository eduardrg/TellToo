package com.bauble_app.bauble.create;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bauble_app.bauble.FeedAdapter.calculateExpire;

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
            StoryObject story = StorySingleton.getInstance().getStory
                    (mReplyStoryKey);
            TextView title = (TextView) v.findViewById(R.id.feed_listitem_title);
            String titleString = story.getTitle();
            title.setText(titleString);
            TextView author = (TextView) v.findViewById(R.id.feed_listitem_author);
            String authorString = story.getAuthor();
            author.setText(authorString);
            TextView time = (TextView) v.findViewById(R.id.feed_listitem_length);

            Long durationInSeconds = story.getDuration();
            Long durationMinutes = durationInSeconds / 60;
            Long durationSeconds = durationInSeconds % 60;

            String durationString = "";
            if (durationMinutes < 10) {
                durationString += "0";
            }
            durationString += durationMinutes + ":";
            if (durationSeconds < 10) {
                durationString += "0";
            }
            durationString += durationSeconds;
            time.setText(durationString);

            TextView chains = (TextView) v.findViewById(R.id.feed_listitem_chains);
            chains.setText(story.getChains().toString());
            final TextView expire = (TextView) v.findViewById(R.id.feed_listitem_expire);
            expire.setText(story.getExpireDate());

            final String expireDate = story.getExpireDate();
            final String createDate = story.getCreated();
            // Experimental countdown timer
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date cDate = null;
            Date date = null;
            Date currentDate = new Date();
            try {
                cDate = format.parse(createDate);
                date = format.parse(expireDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long timeTill = calculateExpire(date, currentDate);
            // seconds
            // till expire
            Long totalTime = calculateExpire(date, cDate);

            // To set Progress Bar
            ProgressBar countDown = (ProgressBar) v.findViewById(R.id.feed_progressBarToday);
            if (timeTill > 0) {

                expire.setText("" + timeTill / 60 + "m");
                countDown.setProgress((int) (timeTill.doubleValue() / totalTime.intValue() * 48));
//            final int countDownInterval = 1000;

                // Countdown timer
//            countDownTimer = new CountDownTimer(timeTill * 1000, countDownInterval) { // 5 second timer
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    expire.setText("" + millisUntilFinished / countDownInterval); // to get seconds
//                }
//
//                @Override
//                public void onFinish() {
//
//                    expire.setText("expired");
//                }
//            };
//            countDownTimer.start();
            } else {
                expire.setText("expired");
                countDown.setProgress(0);
            }
            TextView plays = (TextView) v.findViewById(R.id.feed_listitem_plays);
            plays.setText(story.getPlays().toString());

            if (expire.getText().equals("expired")) {
                v.findViewById(R.id.feed_listitem).setBackgroundResource(R.drawable.listview_expired);
                expire.setTextColor(Color.RED);
            } else {
                v.findViewById(R.id.feed_listitem).setBackgroundResource(R.drawable.listview_gradient);
                expire.setTextColor(Color.DKGRAY);
            }

            // ImageView in your Activity
            CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id
                    .feed_listitem_picture);

            // get and set images & audio
            if (story.grabUniqueId().equals("CapstoneRootStory")) {
                Glide.with(getContext() /* context */)
                        .load(R.drawable.drumfountain)
                        .into(circleImageView);
            } else {
                String imageFileName = story.grabUniqueId() + ".png";
                File imageFile = new File(MainNavActivity.THUMB_ROOT_DIR,
                        imageFileName);

                // Load the image using Glide
                Glide.with(getContext() /* context */)
                        .load(imageFile)
                        .into(circleImageView);
            }
            
            // Set font
            title.setTypeface(FontHelper.getTypeface("Lato-Italic", getContext()));
        }

        return v;
    }
}
