package com.bauble_app.bauble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ChrisLi on 4/20/17.
 */

public class FeedAdapter extends BaseAdapter {
    private Context context;
    private static LayoutInflater inflater = null;
    private Typeface tf;
    private CountDownTimer countDownTimer;

    private Map<String, StoryObject> mData;
    private List<String> mKeys;

    public FeedAdapter(Context context, Map<String, StoryObject> data,
                       List<String> keys) {
        // TODO Auto-generated constructor stub
        // Initialize font
        this.tf = FontHelper.getTypeface("Lato-Italic", context);
        this.context = context;
        this.mData = data;
        this.mKeys = keys;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mData.get(mKeys.get(position));
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.listitem_feed, null);
        }

        String key = mKeys.get(position);
        StoryObject story = (StoryObject) getItem(position);

        TextView title = (TextView) vi.findViewById(R.id.feed_listitem_title);
        String titleString = story.getTitle();
        title.setText(titleString);
        TextView author = (TextView) vi.findViewById(R.id.feed_listitem_author);
        String authorString = story.getAuthor();
        author.setText(authorString);
        TextView time = (TextView) vi.findViewById(R.id.feed_listitem_length);
        time.setText("00:" + story.getDuration().toString());
        TextView chains = (TextView) vi.findViewById(R.id.feed_listitem_chains);
        chains.setText(story.getChains().toString());
        final TextView expire = (TextView) vi.findViewById(R.id.feed_listitem_expire);
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
        Long timeTill = calculateExpire(date, currentDate); // seconds till expire
        Long totalTime = calculateExpire(date, cDate);

        // To set Progress Bar
        ProgressBar countDown = (ProgressBar) vi.findViewById(R.id.feed_progressBarToday);
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
        TextView plays = (TextView) vi.findViewById(R.id.feed_listitem_plays);
        plays.setText(story.getPlays().toString());

        if (expire.getText().equals("expired")) {
            vi.findViewById(R.id.feed_listitem).setBackgroundResource(R.drawable.listview_expired);
            expire.setTextColor(Color.RED);
        } else {
            vi.findViewById(R.id.feed_listitem).setBackgroundResource(R.drawable.listview_gradient);
            expire.setTextColor(Color.DKGRAY);
        }

        // Expire Progress Bar Animation Turns 90 degrees clockwise
//        ProgressBar pb = (ProgressBar) vi.findViewById(R.id.progressBarToday);
//
//        Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
//        an.setFillAfter(true);
//        pb.startAnimation(an);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storage.getReferenceFromUrl("gs://bauble-90a48.appspot.com");
        String imagePath = story.grabUniqueId();
        StorageReference pathReference = storageReference.child("thumbnails/" + imagePath + ".png");

        // ImageView in your Activity
        CircleImageView circleImageView = (CircleImageView) vi.findViewById(R.id
                .feed_listitem_picture);

        // Load the image using Glide
        Glide.with(context /* context */)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(circleImageView);

        // Set font
        title.setTypeface(tf);

        return vi;
    }

    // Returns seconds remaining
    private Long calculateExpire(Date expire, Date current) {
        // Get msec from each, and subtract.
        Long diff = expire.getTime() - current.getTime();
        Long diffSeconds = diff / 1000;
        Long diffMinutes = diff / (60 * 1000);
        Long diffHours = diff / (60 * 60 * 1000);
        return diffSeconds;
    }
}
