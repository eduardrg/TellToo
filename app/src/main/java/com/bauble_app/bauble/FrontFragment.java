package com.bauble_app.bauble;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public FrontFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_front, container,
                false);

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

}
