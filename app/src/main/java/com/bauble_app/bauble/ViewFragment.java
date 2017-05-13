package com.bauble_app.bauble;


import android.media.MediaPlayer;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.facebook.FacebookSdk.getCacheDir;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    private FragmentManager fragManager;
    MediaPlayer mPlayer;

    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view, container,
                false);

        final StoryObject story = StorySingleton.getInstance().getViewStory();
        ImageView thumbnail = (ImageView) v.findViewById(R.id.view_thumbnail);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storage.getReferenceFromUrl("gs://bauble-90a48.appspot.com");
        String imagePath = story.getAuthor() + story.getTitle().replace(" ", "");
        StorageReference pathReference = storageReference.child("thumbnails/" + imagePath + ".png");
        StorageReference audioPathReference = storageReference.child
                ("teststories/" + imagePath + ".mp4");

        TextView title = (TextView) v.findViewById(R.id.view_title);
        title.setText(story.getTitle());
        TextView author = (TextView) v.findViewById(R.id.view_author);
        author.setText("by " + story.getAuthor());
        TextView time = (TextView) v.findViewById(R.id.view_length);
        time.setText("00:" + story.getDuration());
        TextView chains = (TextView) v.findViewById(R.id.view_chains);
        chains.setText(story.getChains().toString());
        TextView expire = (TextView) v.findViewById(R.id.view_expire);
        expire.setText(story.getExpireDate());
        TextView plays = (TextView) v.findViewById(R.id.view_plays);
        plays.setText(story.getPlays().toString());

        LinearLayout childrenContainer = (LinearLayout) v.findViewById(R.id.view_container_childern);
        if (story.getChildren().size() > 0) {
            for (String childName: story.getChildren()) {
                ImageView child = new ImageView(getContext());
                child.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StorySingleton.getInstance().setViewStory(2);
                        // Placeholder for transition to view
                        // ViewFragment.this.fragManager = getActivity().getSupportFragmentManager();

                        // Stop sound before transaction
                        mPlayer.stop();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
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
                StorageReference childPath = storageReference.child("thumbnails/" + childName + ".png");
                Log.e("ViewFragment", "thumbnails/" + childName + ".png");
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
                    File tempMp3 = File.createTempFile("tempStory", "mp4",
                            getCacheDir());
                    tempMp3.deleteOnExit();
                    FileOutputStream fos = new FileOutputStream(tempMp3);
                    fos.write(bytes);
                    fos.close();

                    // resetting mediaplayer instance to evade problems
                    mPlayer.reset();

                    // In case you run into issues with threading consider new instance like:
                    // MediaPlayer mediaPlayer = new MediaPlayer();

                    // Tried passing path directly, but kept getting
                    // "Prepare failed.: status=0x1"
                    // so using file descriptor instead
                    FileInputStream fis = new FileInputStream(tempMp3);
                    mPlayer.setDataSource(fis.getFD());

                    mPlayer.prepare();
                    mPlayer.start();
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
}
