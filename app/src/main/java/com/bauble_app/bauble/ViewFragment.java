package com.bauble_app.bauble;


import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bauble_app.bauble.create.CreateFragment;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.facebook.FacebookSdk.getCacheDir;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    private DatabaseReference mDatabase; // Do I have to have this field any time
    private CountDownTimer countDownTimer; // So I can count down

    private ImageView waveforms;
    private ProgressBar loading;

    private ImageButton mReplyButton;
    private FragmentManager mFragManager;
    private MediaPlayer mPlayer;
    private StorySingleton mStorySingleton;

    private de.hdodenhof.circleimageview.CircleImageView storyImage;
    private de.hdodenhof.circleimageview.CircleImageView leftStoryImage;
    private de.hdodenhof.circleimageview.CircleImageView rightStoryImage;

    private TextView leftStoryLabel;
    private TextView rightStoryLabel;

    private OnSuccessListener audioLoading;
    private StorageReference audioPathReference;

    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view, container,
                false);

        // Initialize media player
        mPlayer = new MediaPlayer();
        mPlayer.pause();

        // Emoji stuff
        // emoji_1f600 - emoji_1f637
        List<String> emojiSource = new ArrayList<String>();
        for (int i = 600; i <= 637; i++) {
            emojiSource.add("emoji_1f" + i);
        }
        LinearLayout emojiChain = (LinearLayout) v.findViewById(R.id.view_emoji_chain);
        for (String code : emojiSource) {
            ImageView emoji = new ImageView(getContext());
            emoji.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            emoji.setPadding(4, 4, 4, 4);
            emoji.setImageResource(getResources().getIdentifier(code, "drawable", "com.bauble_app.bauble"));
            emojiChain.addView(emoji);
        }

        // Hide waveforms initially until loading complete
        waveforms = (ImageView) v.findViewById(R.id.view_waveforms);
        waveforms.setVisibility(View.GONE);
        loading = (ProgressBar) v.findViewById(R.id.view_loading);

        // set frag manager and final story object
        mFragManager = getActivity().getSupportFragmentManager();
        mStorySingleton = StorySingleton.getInstance();
        final StoryObject story = mStorySingleton.getViewStory();

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

        // Hide waveforms initially until loading complete
        waveforms = (ImageView) v.findViewById(R.id.view_waveforms);
        waveforms.setVisibility(View.GONE);
        loading = (ProgressBar) v.findViewById(R.id.view_loading);

        // Circle Image Views
        storyImage = (CircleImageView) v.findViewById(R.id.view_thumbnail);
        leftStoryImage = (CircleImageView) v.findViewById(R.id.view_thumbnail_left);
        rightStoryImage = (CircleImageView) v.findViewById(R.id.view_thumbnail_right);

        // Circle Image Labels
        leftStoryLabel = (TextView) v.findViewById(R.id.view_thumbnail_left_label);
        rightStoryLabel = (TextView) v.findViewById(R.id.view_thumbnail_right_label);

        // Get Neighbor Lists
        List<String> leftSibs = getLeftNeighbors(story.getParentString(), story.grabUniqueId());
        List<String> rightSibs = getRightNeighbors(story.getParentString(), story.grabUniqueId());

        // update database
        int storyNumber = StorySingleton.getInstance().getViewStoryIndex();
        Long storyPlays = story.getPlays() + 1;
        story.setPlays(storyPlays);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("stories").child(story.grabUniqueId()).child("plays").setValue(storyPlays);

        // get and set images & audio
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storage.getReferenceFromUrl("gs://bauble-90a48.appspot.com");
        String imagePath = story.grabUniqueId();
        StorageReference pathReference = storageReference.child("thumbnails/" + imagePath + ".png");
        // TODO: also uniform file type For Tech Demo
        audioPathReference = storageReference.child
                ("teststories/" + imagePath + ".m4a");

        // Load the image using Glide
        Glide.with(getContext() /* context */)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(storyImage);

        // set sib pictures if not null and set sibling counters
        if (leftSibs != null && leftSibs.size() > 0) {
            leftStoryLabel.setText("" + leftSibs.size());
            Log.i("ViewFragment", "Left: " + leftSibs.toString());
            StorageReference leftPathReference = storageReference.child("thumbnails/" +
                    leftSibs.get(leftSibs.size() - 1) + ".png");
            Glide.with(getContext() /* context */)
                    .using(new FirebaseImageLoader())
                    .load(leftPathReference)
                    .into(leftStoryImage);
        } else {
            leftStoryLabel.setText("0");
            leftStoryLabel.setTextColor(Color.LTGRAY);
        }

        if (rightSibs != null && rightSibs.size() > 0) {
            Log.i("ViewFragment", "Right: " + rightSibs.toString());
            StorageReference leftPathReference = storageReference.child("thumbnails/" +
                    rightSibs.get(0) + ".png");
            Glide.with(getContext() /* context */)
                    .using(new FirebaseImageLoader())
                    .load(leftPathReference)
                    .into(rightStoryImage);
            rightStoryLabel.setText("" + rightSibs.size());
        } else {
            rightStoryLabel.setText("0");
            rightStoryLabel.setTextColor(Color.LTGRAY);
        }

        TextView title = (TextView) v.findViewById(R.id.view_title);
        title.setText(story.getTitle());
        TextView author = (TextView) v.findViewById(R.id.view_author);
        author.setText("by " + story.getAuthor());
        final TextView time = (TextView) v.findViewById(R.id.view_length);
        time.setText("00:" + story.getDuration());
        TextView chains = (TextView) v.findViewById(R.id.view_chains);
        chains.setText(story.getChains().toString());
        final TextView expire = (TextView) v.findViewById(R.id.view_expire);
        expire.setText(story.getExpireDate());
        final String expireDate = story.getExpireDate();

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
        storyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (story.getParentString() != null) {
                    String uniqueIdentifier = story.getParentString(); // could be null
                    StorySingleton.getInstance().setViewKey(uniqueIdentifier);
                    FragmentTransaction ft = mFragManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
                            .replace(R.id.content, new ViewFragment())
                            // TODO: even though add to back stack, need to find way to load correct story when back pressed
                            .addToBackStack("tag")
                            .commit();
                }
            }
        });

        TextView plays = (TextView) v.findViewById(R.id.view_plays);
        plays.setText(story.getPlays().toString());

        LinearLayout childrenContainer = (LinearLayout) v.findViewById(R.id.view_container_childern);
        if (story.getChildren().size() > 0) {
            for (String childName: story.getChildren()) {
                final String uniqueIdentifyer = childName;

                ImageView child = new ImageView(getContext());
                child.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));
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
                            // TODO: even though add to back stack, need to find way to load correct story when back pressed
                            .addToBackStack("tag")
                            .commit();
//                        fragManager.beginTransaction()
//                                .replace(R.id.content, new ViewFragment())
//                                .commit();
                    }
                });
                childrenContainer.addView(child);
                StorageReference childPath = storageReference.child("thumbnails/" + uniqueIdentifyer + ".png");
                Log.e("ViewFragment", "thumbnails/" + uniqueIdentifyer + ".png");
                // Load the image using Glide
                Glide.with(getContext() /* context */)
                        .using(new FirebaseImageLoader())
                        .load(childPath)
                        .into(child);
            }
        }

        // Initializes MediaPlayer
//        mPlayer = MediaPlayer.create(getContext(), R.raw.law_of_the_jungle);
//        if (mPlayer.isPlaying()) {
//            mPlayer.stop();
//            mPlayer.reset();
//        }

        // Load and play audio
        // TODO: Insure the phone has space to store TEN_MEGABYTE otherwise crash
        final long TEN_MEGABYTE = 1024 * 1024 * 10;
        audioLoading = new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    // Data for ".mp3" is returned, use this as needed
                    // create temp file that will hold byte array
                    // File tempMp3 = File.createTempFile("tempStory", "mp3", getCacheDir());
                    // TODO: make the files all mp4 or all mp3, For Tech Demo
                    File tempM4a = File.createTempFile("tempStory", "m4a",
                            getCacheDir());

                    tempM4a.deleteOnExit();
                    FileOutputStream fos = new FileOutputStream(tempM4a);
                    fos.write(bytes);
                    fos.close();


                    // resetting mediaplayer instance to evade problems
                    // mPlayer.reset();

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
                        String parsedDuration = "";
                        if (duration / 60 / 60 / 1000 > 0) {
                            parsedDuration = parsedDuration + (duration / 1000 / 60 / 60)  + ":";
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
                        time.setText(parsedDuration);
                    }

                    waveforms.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);

                } catch (IOException ex) {
                    System.out.println(ex.toString());
                    System.out.println("Could not find file ");
                }
            }
        };
        audioPathReference.getBytes(TEN_MEGABYTE).addOnSuccessListener(audioLoading).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // Save Button Click Functionality
        ImageButton save = (ImageButton) v.findViewById(R.id.view_btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = StorySingleton.getInstance().getViewKey();
                // StorySingleton.getInstance().getOwnedStoriesMap().put(key, story);
                StorySingleton.getInstance().getOwnedKeys().add(0, key);
                Toast.makeText(getActivity().getApplicationContext(), story.getTitle() +
                        " added to your collection", Toast.LENGTH_SHORT).show();
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
                vanishSibs();
                if (story.getParentString() != null) {
                    String parentIdentifier = story.getParentString();
                    String uniqueIdentifier = story.grabUniqueId();
                    List<String> childList = StorySingleton.getInstance().getStory(parentIdentifier).getChildren();
                    int newStoryIndex = -1;
                    for (int i = 0; i < childList.size(); i++) {
                        if (uniqueIdentifier.equals(childList.get(i))) {
                            newStoryIndex = i;
                        }
                    }
                    if (newStoryIndex > 0 && newStoryIndex < childList.size()) {
                        StorySingleton.getInstance().setViewKey(childList.get(newStoryIndex - 1));
                        FragmentTransaction ft = mFragManager.beginTransaction();
                        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.content, new ViewFragment())
                                // TODO: even though add to back stack, need to find way to load correct story when back pressed
                                .addToBackStack("tag")
                                .commit();
                    }
                    // StorySingleton.getInstance().setViewKey(uniqueIdentifier);

                }
            }
            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
                vanishSibs();
                if (story.getParentString() != null) {
                    String parentIdentifier = story.getParentString();
                    String uniqueIdentifier = story.grabUniqueId();
                    List<String> childList = StorySingleton.getInstance().getStory(parentIdentifier).getChildren();
                    int newStoryIndex = -1;
                    for (int i = 0; i < childList.size(); i++) {
                        if (uniqueIdentifier.equals(childList.get(i))) {
                            newStoryIndex = i;
                        }
                    }
                    if (newStoryIndex >= 0 && newStoryIndex < childList.size() - 1) {
                        StorySingleton.getInstance().setViewKey(childList.get(newStoryIndex + 1));
                        FragmentTransaction ft = mFragManager.beginTransaction();
                        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.content, new ViewFragment())
                                // TODO: even though add to back stack, need to find way to load correct story when back pressed
                                .addToBackStack("tag")
                                .commit();
                    }
                    // StorySingleton.getInstance().setViewKey(uniqueIdentifier);

                }
            }
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
                if (story.getParentString() != null) {
                    String uniqueIdentifier = story.getParentString(); // could be null
                    StorySingleton.getInstance().setViewKey(uniqueIdentifier);
                    FragmentTransaction ft = mFragManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
                            .replace(R.id.content, new ViewFragment())
                            // TODO: even though add to back stack, need to find way to load correct story when back pressed
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

    // takes in a parent key and the key of the current story and returns a list of it's right
    // siblings
    // returns null if the parent key is null or there are not siblings to the right
    private List<String> getRightNeighbors(String parentKey, String currentKey) {
        if (parentKey != null) {
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
        }
        return null;
    }

    // takes in a parent key and the key of the current story and returns a list of it's right
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
        List<FileDownloadTask> tasks = audioPathReference.getActiveDownloadTasks();
        for (FileDownloadTask task : tasks) {
            task.removeOnSuccessListener(audioLoading);
        }
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

}
