package com.bauble_app.bauble;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bauble_app.bauble.create.CreateFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    private CountDownTimer countDownTimer; // So I can count down
    private CountDownTimer finalCountDownTimer;

    private RelativeLayout waveforms;
    private ProgressBar loading;

    private ImageButton mReplyButton;
    private ImageButton mReactButton;
    private FragmentManager mFragManager;
    private MediaPlayer mPlayer;
    private StorySingleton mStorySingleton;
    private StoryObject mStory;

    private de.hdodenhof.circleimageview.CircleImageView storyImage;
    private de.hdodenhof.circleimageview.CircleImageView leftStoryImage;
    private de.hdodenhof.circleimageview.CircleImageView rightStoryImage;

    private MyDBHelper mDB;
    private TextView leftStoryLabel;
    private TextView rightStoryLabel;

    private OnSuccessListener audioLoading;
    private StorageReference audioPathReference;
    private LinearLayout emojiChain;

    private LinearLayout wavebars;

    public ViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = new MyDBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_view, container,
                false);

        wavebars = (LinearLayout) v.findViewById(R.id.view_waveforms);

        countDownTimer = new CountDownTimer(10 * 1000, 100) { // 10 second timer

            private int[] data = null;

            @Override
            public void onTick(long millsUntilFinished) { //milliseconds?
                Random r = new Random();
                if (data == null) {
                    data = new int[wavebars.getChildCount()];
                    for (int i = 0; i < data.length; i++) {
                        data[i] = r.nextInt(101);
                    }
                }
                // Waveform setting Note: higher values mean lower bar
                for (int i = 0; i < wavebars.getChildCount() - 1; i++) {
                    ProgressBar bar = (ProgressBar) wavebars.getChildAt(i);
                    int newVal = data[i + 1];
                    if (data[i + 1] > 95) {
                        newVal = newVal - r.nextInt(5);
                    } else if (data[i + 1] < 5)  {
                        newVal = newVal + r.nextInt(5);
                    }else {
                        newVal = newVal + (r.nextInt(10) - 5);
                    }
                    bar.setProgress(newVal);
                    data[i] = newVal;
                }
                ProgressBar lastBar = (ProgressBar) wavebars.getChildAt(wavebars.getChildCount() - 1);
                int lastVal = r.nextInt(101);
                lastBar.setProgress(lastVal);
                data[wavebars.getChildCount() - 1] = lastVal;
            }

            @Override
            public void onFinish() {
//                Random r = new Random();
//                // Waveform setting
//                for (int i = 0; i < wavebars.getChildCount(); i++) {
//                    ProgressBar bar = (ProgressBar) wavebars.getChildAt(i);
//                    bar.setProgress(r.nextInt(16) + 85);
//                }

                Log.e("ViewFragment", "First Countdown Done");

                finalCountDownTimer = new CountDownTimer(2 * 1000, 100) { // 10 second timer

                    @Override
                    public void onTick(long millsUntilFinished) { //milliseconds?
                        Random r = new Random();
                        // Waveform setting Note: higher values mean lower bar
                        for (int i = 0; i < wavebars.getChildCount() - 1; i++) {
                            ProgressBar bar = (ProgressBar) wavebars.getChildAt(i);
                            int newVal = data[i] + 1;
                            if (newVal < 95) {
                                newVal = newVal + r.nextInt(10) + 10;
                            }
                            bar.setProgress(newVal);
                            data[i] = newVal;
                        }
                        ProgressBar lastBar = (ProgressBar) wavebars.getChildAt(wavebars.getChildCount() - 1);
                        int lastVal = r.nextInt(6) + 95;
                        lastBar.setProgress(lastVal);
                        data[wavebars.getChildCount() - 1] = lastVal;
                    }

                    @Override
                    public void onFinish() {
                        v.findViewById(R.id.view_color_bar).setVisibility(View.VISIBLE);
                        Log.e("ViewFragment", "Finished Countdown");
                    }
                };
                finalCountDownTimer.start();
            }
        };
        countDownTimer.start();

        // Initialize media player
        mPlayer = new MediaPlayer();
        mPlayer.pause();

        // Emoji stuff
        // emoji_1f600 - emoji_1f637
        List<String> emojiSource = new ArrayList<String>();
        for (int i = 600; i <= 637; i++) {
            emojiSource.add("emoji_1f" + i);
        }
        emojiChain = (LinearLayout) v.findViewById(R.id.view_emoji_chain);
//        for (String code : emojiSource) {
//            ImageView emoji = new ImageView(getContext());
//            emoji.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
//            emoji.setPadding(4, 4, 4, 4);
//            emoji.setImageResource(getResources().getIdentifier(code, "drawable", "com.bauble_app.bauble"));
//            emojiChain.addView(emoji);
//        }

        // Hide waveforms initially until loading complete
        waveforms = (RelativeLayout) v.findViewById(R.id.view_waveforms_layout);
        waveforms.setVisibility(View.GONE);
        loading = (ProgressBar) v.findViewById(R.id.view_loading);

        // set frag manager and final mStory object
        mFragManager = getActivity().getSupportFragmentManager();
        mStorySingleton = StorySingleton.getInstance();
        mStory = mStorySingleton.getViewStory();

        // set up reply button
        mReplyButton = (ImageButton) v.findViewById(R.id.view_reply_btn);
        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("replybtn1");
                Bundle bundle = new Bundle();
                bundle.putString("replyStoryKey", mStorySingleton.getViewKey());
                // set Fragmentclass Arguments
                CreateFragment createFrag = new CreateFragment();
                createFrag.setArguments(bundle);

                mFragManager.beginTransaction().replace(R.id.content,
                        createFrag, "REPLY_FRAG").commit();

                System.out.println("replybtn2");
            }
        });

        // set up reach button
        mReactButton = (ImageButton) v.findViewById(R.id.view_btn_react);
        mReactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog(getContext(), v);
            }
        });

        // Hide waveforms initially until loading complete
        waveforms = (RelativeLayout) v.findViewById(R.id.view_waveforms_layout);
        waveforms.setVisibility(View.GONE);
        loading = (ProgressBar) v.findViewById(R.id.view_loading);

        // Story thumbnail CircleImageViews
        storyImage = (CircleImageView) v.findViewById(R.id.view_thumbnail);
        leftStoryImage = (CircleImageView) v.findViewById(R.id.view_thumbnail_left);
        rightStoryImage = (CircleImageView) v.findViewById(R.id.view_thumbnail_right);

        // Circle Image Labels
        leftStoryLabel = (TextView) v.findViewById(R.id.view_thumbnail_left_label);
        rightStoryLabel = (TextView) v.findViewById(R.id.view_thumbnail_right_label);

        // Get Neighbor Lists
        final List<String> leftSibs;
        final List<String> rightSibs;
        if (mStory != null) {
            leftSibs = getLeftNeighbors(mStory.getParentString(), mStory.grabUniqueId());
            rightSibs = getRightNeighbors(mStory.getParentString(), mStory.grabUniqueId());
        } else {
            leftSibs = null;
            rightSibs = null;
        }

        // update database
        int storyNumber = StorySingleton.getInstance().getViewStoryIndex();
        Long storyPlays = mStory.getPlays() + 1;
        mStory.setPlays(storyPlays);

        mDB.incrementPlays(mStory.grabUniqueId());

        // get and set images & audio
        String imageFileName = mStory.grabUniqueId() + ".png";
        File imageFile = new File(MainNavActivity.THUMB_ROOT_DIR,
                imageFileName);

        // Load the image using Glide
        Glide.with(getContext() /* context */)
                .load(imageFile)
                .into(storyImage);

        // set sib pictures if not null and set sibling counters
        if (leftSibs != null && leftSibs.size() > 0) {
            leftStoryLabel.setText("" + leftSibs.size());
            Log.i("ViewFragment", "Left: " + leftSibs.toString());
            // Loop to animate sibs
            final Handler handler = new Handler();
            for (int i = 0; i < leftSibs.size(); i++) {
                final int index = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        String leftImageFileName = leftSibs.get(index) + ".png";
                        File leftImageFile = new File(MainNavActivity
                                .THUMB_ROOT_DIR,
                                leftImageFileName);

                        // Load the image using Glide
                            Glide.with(getContext())
                                .load(leftImageFile)
                                .into(leftStoryImage);
                    }
                }, (i + 1) * 333); //i * 1/3 second
            }

//            StorageReference leftPathReference = storageReference.child("thumbnails/" +
//                    leftSibs.get(leftSibs.size() - 1) + ".png");
//            Glide.with(getContext() /* context */)
//                    .using(new FirebaseImageLoader())
//                    .load(leftPathReference)
//                    .into(leftStoryImage);

        } else {
            leftStoryLabel.setText("0");
            leftStoryLabel.setTextColor(Color.LTGRAY);
        }

        if (rightSibs != null && rightSibs.size() > 0) {
            rightStoryLabel.setText("" + rightSibs.size());
            Log.i("ViewFragment", "Right: " + rightSibs.toString());

            // Loop to animate sibs
            final Handler handler = new Handler();
            for (int i = 0; i < rightSibs.size(); i++) {
                final int index = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        String rightImageFileName = rightSibs.get(rightSibs
                                .size() - 1 - index) + ".png";
                        File rightImageFile = new File(MainNavActivity
                                .THUMB_ROOT_DIR, rightImageFileName);
                        Glide.with(getContext() /* context */)
                                .load(rightImageFile)
                                .into(rightStoryImage);
                    }
                }, (i + 1) * 333); //i * 1/3 second
            }

//            StorageReference leftPathReference = storageReference.child("thumbnails/" +
//                    rightSibs.get(0) + ".png");
//            Glide.with(getContext() /* context */)
//                    .using(new FirebaseImageLoader())
//                    .load(leftPathReference)
//                    .into(rightStoryImage);
        } else {
            rightStoryLabel.setText("0");
            rightStoryLabel.setTextColor(Color.LTGRAY);
        }

        TextView title = (TextView) v.findViewById(R.id.view_title);
        title.setText(mStory.getTitle());
        TextView author = (TextView) v.findViewById(R.id.view_author);
        author.setText("by " + mStory.getAuthor());
        final TextView time = (TextView) v.findViewById(R.id.view_length);
        time.setText("00:" + mStory.getDuration());
        TextView chains = (TextView) v.findViewById(R.id.view_chains);
        chains.setText(mStory.getChains().toString());
        final TextView expire = (TextView) v.findViewById(R.id.view_expire);
        expire.setText(mStory.getExpireDate());
        final String expireDate = mStory.getExpireDate();

        // Experimental countdown timer
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        Date currentDate = new Date();
        try {
            date = format.parse(expireDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long timeTill = calculateExpire(date, currentDate); // seconds till expire
        if (timeTill > 60) { // timeTill = seconds
            final int countDownInterval = 1000 * 60; // one minite countdown interval
            final int minutesLeft = timeTill.intValue() / 60;

            expire.setText("" + (timeTill / 60) + "m");
            countDownTimer = new CountDownTimer(timeTill * 1000, countDownInterval) { // 5 second timer
                @Override
                public void onTick(long millsUntilFinished) { //milliseconds?
                    expire.setText("" + (millsUntilFinished / countDownInterval) + "m"); // to get seconds
                }

                @Override
                public void onFinish() {

                    expire.setText("expired");
                }
            };
            countDownTimer.start();
        } else if (timeTill > 0) {
            final int countDownInterval = 1000; // one second countdown interval
            final int minutesLeft = timeTill.intValue() / 60;

            expire.setText("" + (timeTill) + "s");
            countDownTimer = new CountDownTimer(timeTill * 1000, countDownInterval) { // 5 second timer
                @Override
                public void onTick(long millsUntilFinished) { //milliseconds?
                    expire.setText("" + (millsUntilFinished / countDownInterval) + "s"); // to get seconds
                }

                @Override
                public void onFinish() {

                    expire.setText("expired");
                }
            };
            countDownTimer.start();
        } else {
            expire.setText("expired");
        }

        // Set on click for parent
        // storyImage = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.view_thumbnail);
//        storyImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (story.getParentString() != null) {
//                    String uniqueIdentifier = story.getParentString(); // could be null
//                    StorySingleton.getInstance().setViewKey(uniqueIdentifier);
//                    FragmentTransaction ft = mFragManager.beginTransaction();
//                    ft.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
//                            .replace(R.id.content, new ViewFragment())
//                            // TODO: even though add to back stack, need to find way to load correct story when back pressed
//                            .addToBackStack("tag")
//                            .commit();
//                }
//            }
//        });

        TextView plays = (TextView) v.findViewById(R.id.view_plays);
        plays.setText(mStory.getPlays().toString());

        LinearLayout childrenContainer = (LinearLayout) v.findViewById(R.id.view_container_childern);
        if (mStory.getChildren().size() > 0) {
            TextView emptyLabel = (TextView) v.findViewById(R.id.view_container_empty);
            emptyLabel.setVisibility(View.GONE);
            for (String childName: mStory.getChildren()) {
                Log.e("ViewFragment", "Child Name:" + childName);
                final String uniqueIdentifyer = childName;

                de.hdodenhof.circleimageview.CircleImageView child = new de.hdodenhof.circleimageview.CircleImageView(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
                lp.setMargins(0, 0, 60, 0);
                child.setLayoutParams(lp);
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StorySingleton.getInstance().setViewKey(uniqueIdentifyer);
                        // Placeholder for transition to view
                        // ViewFragment.this.fragManager = getActivity().getSupportFragmentManager();

                        // Stop sound before transaction
                        stopMediaPlayer();
                        FragmentTransaction ft = mFragManager.beginTransaction();
                        ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                            .replace(R.id.content, new ViewFragment())
                            // TODO: even though add to back stack, need to find way to load correct mStory when back pressed
                            .addToBackStack("tag")
                            .commit();
//                        fragManager.beginTransaction()
//                                .replace(R.id.content, new ViewFragment())
//                                .commit();
                    }
                });
                childrenContainer.addView(child);

                imageFileName = childName + ".png";

                imageFile = new File(MainNavActivity.THUMB_ROOT_DIR,
                        imageFileName);

                // Load the image using Glide
                Glide.with(getContext()).load(imageFile).into(child);

            }
        }

        // Load and play audio
        // TODO: Insure the phone has space to store TEN_MEGABYTE otherwise crash
        // Begin loading the audio, and remove the loading bar when it has
        // been loaded
        AsyncTask<Void, Void, String> playAudio = new AsyncTask<Void,Void,String>
                () {
            //Before the background task
            @Override
            protected void onPreExecute() {
                mPlayer.reset();
            }

            // The background task
            @Override
            protected String doInBackground(Void... arg0) {
                //Do something...
                //Thread.sleep(5000);
                final long TEN_MEGABYTE = 1024 * 1024 * 20; // changed to 20 mb
                String parsedDuration = null;
                File tempM4a = null;
                try {
                    // Log.e("ViewFragement", "" + bytes.length + " " + bytes.length /
                    // 8);
                    //Log.e("ViewFragement", Arrays.toString(bytes));


                    // Data for ".mp3" is returned, use this as needed
                    // create temp file that will hold byte array
                    // File tempMp3 = File.createTempFile("tempStory", "mp3", getCacheDir());
                    // TODO: make the files all mp4 or all mp3, For Tech Demo

                    tempM4a = new File(MainNavActivity.STORY_ROOT_DIR,
                            mStory.grabUniqueId() + ".m4a");
                    // resetting mediaplayer instance to evade problems
                    //

                    // In case you run into issues with threading consider new instance like:
                    // MediaPlayer mediaPlayer = new MediaPlayer();

                    // Tried passing path directly, but kept getting
                    // "Prepare failed.: status=0x1"
                    // so using file descriptor instead
                    FileInputStream fis = new FileInputStream(tempM4a);
                    if (mPlayer != null) {
                        mPlayer.setDataSource(fis.getFD());

                        mPlayer.prepare();
                        mPlayer.start();
                        int duration = mPlayer.getDuration();
                        parsedDuration = "";
                        if (duration / 60 / 60 / 1000 > 0) {
                            parsedDuration = parsedDuration + (duration / 1000 / 60 / 60) + ":";
                            duration = duration % (60 * 60 * 1000);
                            if (duration / (60 * 1000) < 10) {
                                parsedDuration = parsedDuration + "0";
                            }
                        }
                        if (duration / 60 / 1000 > 0) {
                            parsedDuration = parsedDuration + (duration / 1000 / 60) + ":";
                            duration = duration % (60 * 1000);
                            if (duration / 1000 < 10) {
                                parsedDuration = parsedDuration + "0";
                            }
                        }
                        if (duration / 1000 >= 0) {
                            parsedDuration = parsedDuration + (duration / 1000);
                        }
                        //+ (duration / 1000 / 60 % 60) + ":" + (duration / 1000 % 60) + ":" + (duration % 1000);
                        //
                    }

                } catch (IOException ex) {
                    Log.e("AudioFileError", "Could not open file " +
                            tempM4a.getPath() + " for playback.", ex);
                }

                return parsedDuration;
            }

            // The UI thread; update the UI after task is done
            @Override
            protected void onPostExecute(String result) {
                // Set parsed duration
                time.setText(result);
                //dissmiss progress dialog
                waveforms.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            }

        };
        playAudio.execute((Void[])null);

        // Save Button Click Functionality
        ImageButton save = (ImageButton) v.findViewById(R.id.view_btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = StorySingleton.getInstance().getViewKey();
                // StorySingleton.getInstance().getOwnedStoriesMap().put(key, mStory);
                StorySingleton.getInstance().getOwnedKeys().add(0, key);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_collect,
                        (ViewGroup) v.findViewById(R.id.toast_layout_root));

                ImageView image = (ImageView) layout.findViewById(R.id.toast_save_image);
                Drawable drawable = storyImage.getDrawable();
                image.setImageDrawable(drawable);
                TextView text = (TextView) layout.findViewById(R.id.toast_save_text);
                text.setText(mStory.getTitle() + " added to your collection");

                Toast toast = new Toast(getContext().getApplicationContext());
                //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setGravity(Gravity.BOTTOM, 0, 200);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();


//                Toast.makeText(getActivity().getApplicationContext(), story.getTitle() +
//                        " added to your collection", Toast.LENGTH_SHORT).show();
            }
        });

        // Set Swipe Action Recognizer
        LinearLayout wholeView = (LinearLayout) v.findViewById(R.id.view_whole_view);
        wholeView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() { // get child before, the one that is on the left
                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
                if (mStory.getParentString() != null) {
                    String parentIdentifier = mStory.getParentString();
                    String uniqueIdentifier = mStory.grabUniqueId();
                    List<String> childList = StorySingleton.getInstance().getStory(parentIdentifier).getChildren();
                    int newStoryIndex = -1;
                    for (int i = 0; i < childList.size(); i++) {
                        if (uniqueIdentifier.equals(childList.get(i))) {
                            newStoryIndex = i;
                        }
                    }
                    if (newStoryIndex > 0 && newStoryIndex < childList.size()) {
                        vanishSibs();
                        StorySingleton.getInstance().setViewKey(childList.get(newStoryIndex - 1));
                        FragmentTransaction ft = mFragManager.beginTransaction();
                        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.content, new ViewFragment())
                                // TODO: even though add to back stack, need to find way to load correct mStory when back pressed
                                .addToBackStack("tag")
                                .commit();
                    }
                    // StorySingleton.getInstance().setViewKey(uniqueIdentifier);

                }
            }
            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
                if (mStory.getParentString() != null) {
                    String parentIdentifier = mStory.getParentString();
                    String uniqueIdentifier = mStory.grabUniqueId();
                    List<String> childList = StorySingleton.getInstance().getStory(parentIdentifier).getChildren();
                    int newStoryIndex = -1;
                    for (int i = 0; i < childList.size(); i++) {
                        if (uniqueIdentifier.equals(childList.get(i))) {
                            newStoryIndex = i;
                        }
                    }
                    if (newStoryIndex >= 0 && newStoryIndex < childList.size() - 1) {
                        vanishSibs();
                        StorySingleton.getInstance().setViewKey(childList.get(newStoryIndex + 1));
                        FragmentTransaction ft = mFragManager.beginTransaction();
                        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.content, new ViewFragment())
                                // TODO: even though add to back stack, need to find way to load correct mStory when back pressed
                                .addToBackStack("tag")
                                .commit();
                    }
                    // StorySingleton.getInstance().setViewKey(uniqueIdentifier);

                }
            }
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
                if (mStory.getParentString() != null) {
                    String uniqueIdentifier = mStory.getParentString(); // could be null
                    StorySingleton.getInstance().setViewKey(uniqueIdentifier);
                    FragmentTransaction ft = mFragManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
                            .replace(R.id.content, new ViewFragment())
                            // TODO: even though add to back stack, need to find way to load correct mStory when back pressed
                            .addToBackStack("tag")
                            .commit();
                }
            }

        });

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMediaPlayer();
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

    // takes in a parent key and the key of the current mStory and returns a list of it's right
    // siblings
    // returns null if the parent key is null or there are not siblings to the right
    private List<String> getRightNeighbors(String parentKey, String currentKey) {
        if (parentKey != null) {
            System.out.println("parentkey is not null");
            List<String> childList = StorySingleton.getInstance().getStory(parentKey).getChildren();
            int newStoryIndex = -1;
            for (int i = 0; i < childList.size(); i++) {
                if (currentKey.equals(childList.get(i))) {
                    newStoryIndex = i;
                }
            }
            if (newStoryIndex >= 0 && newStoryIndex < childList.size()) {
                return childList.subList(newStoryIndex + 1, childList.size());
            }
        } else {
            System.out.println("parentkey is null");
        }
        return null;
    }

    // takes in a parent key and the key of the current mStory and returns a list of it's right
    // siblings
    // returns null if the parent key is null or there are not siblings to the right
    private List<String> getLeftNeighbors(String parentKey, String currentKey) {
        if (parentKey != null) {
            List<String> childList = StorySingleton.getInstance().getStory(parentKey).getChildren();
            int newStoryIndex = -1;
            for (int i = 0; i < childList.size(); i++) {
                if (currentKey.equals(childList.get(i))) {
                    newStoryIndex = i;
                }
            }
            if (newStoryIndex > 0 && newStoryIndex < childList.size()) {
                return childList.subList(0, newStoryIndex);
            }
        }
        return null;
    }

    // Hides all of the UI for viewing siblings
    private void vanishSibs() {
        leftStoryImage.setVisibility(View.GONE);
        rightStoryImage.setVisibility(View.GONE);
        leftStoryLabel.setVisibility(View.GONE);
        rightStoryLabel.setVisibility(View.GONE);
    }

    // Stops Media Player
    private void stopMediaPlayer() {
        // TODO: Find out if this block of code does anything
        /*
        List<FileDownloadTask> tasks = audioPathReference.getActiveDownloadTasks();
        for (FileDownloadTask task : tasks) {
            task.removeOnSuccessListener(audioLoading);
        }
        */
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    // Call to show custom dialog
    private void showMyDialog(Context context, View v) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.emoji_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


        List<ImageView> emojis = new ArrayList<ImageView>();
        emojis.add((ImageView) dialog.findViewById(R.id.emoji_btn1));
        emojis.add((ImageView) dialog.findViewById(R.id.emoji_btn2));
        emojis.add((ImageView) dialog.findViewById(R.id.emoji_btn3));
        emojis.add((ImageView) dialog.findViewById(R.id.emoji_btn4));
        emojis.add((ImageView) dialog.findViewById(R.id.emoji_btn5));
        emojis.add((ImageView) dialog.findViewById(R.id.emoji_btn6));

        // TODO: add emoji data to singleton so it persists
        for (ImageView imageView : emojis) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView img = (ImageView) v;
                    Drawable emojiDrawable = img.getDrawable();
                    ImageView emoji = new ImageView(getContext());
                    emoji.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
                    emoji.setPadding(4, 4, 4, 4);
                    emoji.setImageDrawable(emojiDrawable);
                    emojiChain.addView(emoji);
                    dialog.dismiss();
                }
            });
        }

//        Button btnBtmLeft = (Button) dialog.findViewById(R.id.btnBtmLeft);
//        Button btnBtmRight = (Button) dialog.findViewById(R.id.btnBtmRight);
//
//        btnBtmLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        btnBtmRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // do whatever you want here
//            }
//        });

        /**
         * if you want the dialog to be specific size, do the following
         * this will cover 85% of the screen (95% width and 33% height)
         */
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.95);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.10);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.y = 400;
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
    }

}
