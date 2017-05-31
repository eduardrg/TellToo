package com.bauble_app.bauble;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bauble_app.bauble.create.CreateFragment;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class FrontFragment extends Fragment {

    private DatabaseReference mDatabase;
    public TextView chainView;
    public TextView authorView;
    public TextView timeView;
    public TextView expireView;
    public TextView playView;

    public com.bauble_app.bauble.CustomButton saveCollection;
    public ProgressBar donateBar;
    public TextView donateAmount;

    private FragmentManager mFragManager;
    private MediaPlayer mPlayer;
    private StoryObject mStory;

    public FrontFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_front, container,
                false);

        mStory = StorySingleton.getInstance().getViewStory();

        // get and set images & audio
        String imageFileNamePre = "";
        File imageFilePre = null;

        if (mStory != null) {
            imageFileNamePre = mStory.grabUniqueId() + ".png";
            imageFilePre = new File(MainNavActivity.THUMB_ROOT_DIR,
                    imageFileNamePre);
        }

        final String imageFileName = imageFileNamePre;
        final File imageFile = imageFilePre;

        saveCollection = (com.bauble_app.bauble.CustomButton) v.findViewById(R.id.community_collect);
        saveCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog(getContext(), v);
            }
        });

        donateBar = (ProgressBar) v.findViewById(R.id.community_donate_progress);
        donateBar.setProgress(StorySingleton.getInstance().getDonationProgress());
        donateAmount = (TextView) v.findViewById(R.id.community_amount);
        donateAmount.setText("" + (StorySingleton.getInstance().getDonationProgress() * 10));

        // Set Swipe Action Recognizer
        LinearLayout wholeView = (LinearLayout) v.findViewById(R.id.community_whole_view);
        wholeView.setOnTouchListener(new BaseTouchListener(getActivity()) {
            public void onSwipeTop() {
//                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
                showChildDialog(getContext(), v, imageFileName, imageFile);
            }
            public void onSwipeRight() {

            }
            public void onSwipeLeft() {

            }
            public void onSwipeBottom() {

            }

        });

        com.bauble_app.bauble.CustomButton showStoryBtn = (com.bauble_app.bauble.CustomButton) v.findViewById(R.id.community_show_child);
        showStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChildDialog(getContext(), v, imageFileName, imageFile);
            }
        });

        LinearLayout causeElements = (LinearLayout) v.findViewById(R.id.cause_elements);
        causeElements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog(getContext(), v);
            }
        });

        ImageButton replyButton = (ImageButton) v.findViewById(R.id
                .front_reply_btn);
        // set up reply button
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StorySingleton.getInstance().isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("replyStoryKey", StorySingleton
                            .getInstance().getKey());
                    // set Fragmentclass Arguments
                    CreateFragment createFrag = new CreateFragment();
                    createFrag.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.content,
                            createFrag, "REPLY_FRAG").commit();
                }
            }
        });

//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        // Reference to an image file in Firebase Storage
//        StorageReference storageReference = storage.getReferenceFromUrl("gs://bauble-90a48.appspot.com");
//        StorageReference pathReference = storageReference.child("audio/RE02p5GBRgWMwZbG95Rb1144gL13/thumbnail_01.png");
//
//        chainView = (TextView) v.findViewById(R.id.community_listitem_chains);
//        authorView = (TextView)  v.findViewById(R.id.community_listitem_author);
//        timeView = (TextView)  v.findViewById(R.id.community_listitem_length);
//        expireView = (TextView)  v.findViewById(R.id.community_listitem_expire);
//        playView  = (TextView)  v.findViewById(R.id.community_listitem_plays);
//        Log.e("FrontFragment", "Data Called for");
//
//        // Set Story's stats
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference reference = mDatabase.child("stories").child("1");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e("FrontFragment", "Data Called for");
//                String title = dataSnapshot.child("Title").getValue(String.class);
//                Long chains = dataSnapshot.child("chain").getValue(Long.class);
//                String author = dataSnapshot.child("author").getValue(String.class);
//                Long plays = dataSnapshot.child("play").getValue(Long.class);
//                Long time = dataSnapshot.child("duration").getValue(Long.class);
//                String expire = dataSnapshot.child("expiration").getValue(String.class);
//                chainView.setText(chains.toString());
//                authorView.setText(author.toString());
//                playView.setText(plays.toString());
//                timeView.setText("00:" + time.toString());
//                expireView.setText(expire.toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//
//
//        // ImageView in your Activity
//        ImageView imageView = (ImageView) v.findViewById(R.id.community_listitem_picture);
//
//        // Load the image using Glide
//        Glide.with(this /* context */)
//                .using(new FirebaseImageLoader())
//                .load(pathReference)
//                .into(imageView);


        // Inflate the layout for this fragment
        return v;
    }

    // Call to show custom dialog
    private void showSaveDialog(Context context, View v) {
        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.capstone_save_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        Button btnBtmLeft = (Button) dialog.findViewById(R.id.btnBtmLeft);
        Button btnBtmRight = (Button) dialog.findViewById(R.id.btnBtmRight);

        // Donate
        btnBtmLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = donateBar.getProgress();
                donateBar.setProgress(progress + 1);
                StorySingleton.getInstance().setDonationProgress(progress + 1);
                int amount = Integer.parseInt(donateAmount.getText().toString());
                donateAmount.setText("" + (amount + 10));
                dialog.dismiss();
            }
        });

        btnBtmRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // do whatever you want here
            }
        });

        /**
         * if you want the dialog to be specific size, do the following
         * this will cover 85% of the screen (95% width and 33% height)
         */
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.95);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.52);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.y = 0;
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
    }

    // Call to show custom dialog
    private void showChildDialog(Context context, View v, String imageFileName, File imageFile) {
        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.story_reply_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.story_reply_dialog,
                (ViewGroup) v.findViewById(R.id.reply_layout_root));

        LinearLayout childrenContainer = (LinearLayout) layout.findViewById(R.id.view_container_childern);
        if (mStory != null && mStory.getChildren().size() > 0) {
            Log.e("ViewFragment", "GOT HERE");
            TextView emptyLabel = (TextView) layout.findViewById(R.id.view_container_empty);
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

        dialog.setContentView(layout);

        /**
         * if you want the dialog to be specific size, do the following
         * this will cover 85% of the screen (95% width and 33% height)
         */
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.95);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.20);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.y = 400;
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
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

}
