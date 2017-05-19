package com.bauble_app.bauble;


import android.content.res.Resources;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getCacheDir;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {
    private FragmentManager fragManager;
    private DatabaseReference mDatabase; // Do I have to have this field any time
    private CountDownTimer countDownTimer; // So I can count down
    private ImageButton mReplyButton;
    private FragmentManager mFragManager;
    private MediaPlayer mPlayer;
    private StorySingleton mStorySingleton;
    private ImageView waveforms;
    private ProgressBar loading;
    private de.hdodenhof.circleimageview.CircleImageView storyImage;


    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view, container,
                false);
        mFragManager = getActivity().getSupportFragmentManager();
        mStorySingleton = StorySingleton.getInstance();
        final StoryObject story = mStorySingleton.getViewStory();

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

        CircleImageView thumbnail = (CircleImageView) v.findViewById(R.id.view_thumbnail);

        // update database
        int storyNumber = StorySingleton.getInstance().getViewStoryIndex();
        Long storyPlays = story.getPlays() + 1;
        story.setPlays(storyPlays);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("stories").child(story.grabUniqueId()).child("plays").setValue(storyPlays);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storage.getReferenceFromUrl("gs://bauble-90a48.appspot.com");
        String imagePath = story.grabUniqueId();
        StorageReference pathReference = storageReference.child("thumbnails/" + imagePath + ".png");
        // StorageReference audioPathReference = storageReference.child("teststories/" + imagePath + ".mp3");
        // TODO: also uniform file type For Tech Demo
        StorageReference audioPathReference = storageReference.child
                ("teststories/" + imagePath + ".m4a");

        TextView title = (TextView) v.findViewById(R.id.view_title);
        title.setText(story.getTitle());
        TextView author = (TextView) v.findViewById(R.id.view_author);
        author.setText("by " + story.getAuthor());
        TextView time = (TextView) v.findViewById(R.id.view_length);
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
        storyImage = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.view_thumbnail);
        storyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (story.getParentString() != null) {
                    String uniqueIdentifier = story.getParentString(); // could be null
                    StorySingleton.getInstance().setViewKey(uniqueIdentifier);
                    mPlayer.stop();
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
                        mPlayer.stop();
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
        mPlayer = MediaPlayer.create(getContext(), R.raw.law_of_the_jungle);
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.reset();
        }
//        mPlayer.start();

        // Load the image using Glide
        Glide.with(getContext() /* context */)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(thumbnail);

        // Load and play audio
        // TODO: Insure the phone has space to store TEN_MEGABYTE otherwise crash
        final long TEN_MEGABYTE = 1024 * 1024 * 10;
        audioPathReference.getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                    mPlayer.reset();

                    // In case you run into issues with threading consider new instance like:
                    // MediaPlayer mediaPlayer = new MediaPlayer();

                    // Tried passing path directly, but kept getting
                    // "Prepare failed.: status=0x1"
                    // so using file descriptor instead
                    FileInputStream fis = new FileInputStream(tempM4a);
                    mPlayer.setDataSource(fis.getFD());

                    mPlayer.prepare();
                    mPlayer.start();

                    waveforms.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);

                } catch(IOException ex) {
                    System.out.println (ex.toString());
                    System.out.println("Could not find file ");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // Add Button Click funtionality
        ImageButton save = (ImageButton) v.findViewById(R.id.view_btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), story.getTitle() +
                        " added to your collection", Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
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
