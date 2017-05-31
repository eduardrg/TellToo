package com.bauble_app.bauble;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

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

    public FrontFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_front, container,
                false);

        saveCollection = (com.bauble_app.bauble.CustomButton) v.findViewById(R.id.community_collect);
        saveCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog(getContext(), v);
            }
        });

        donateBar = (ProgressBar) v.findViewById(R.id.community_donate_progress);
        donateAmount = (TextView) v.findViewById(R.id.community_amount);

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
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.45);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.y = 0;
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();
    }

}
