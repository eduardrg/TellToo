package com.bauble_app.bauble.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bauble_app.bauble.R;

/**
 * Created by princ on 5/18/2017.
 */

public class PrivacyFragment extends Fragment {
    private CreateFragment mCreateFrag;
    private SeekBar mSeekBar;

    public PrivacyFragment() {
        // required constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_privacy, container, false);
        mSeekBar = (SeekBar) v.findViewById(R.id.privacy_seek_bar);
        final TextView textview = (TextView) v.findViewById(R.id.privacy_seek_bar_text);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int stepSize = 25;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int)Math.round(progress/stepSize))*stepSize;
                seekBar.setProgress(progress);
                String progressText = "Only me";
                switch (progress) {
                    case (0):
                        progressText = "Only me";
                        mCreateFrag.setmAccess("justMe");
                        break;
                    case (25):
                        progressText = "Only the author of the story I'm " +
                                "replying to";
                        mCreateFrag.setmAccess("parentAuthor");
                        break;
                    case (50):
                        progressText = "The author of the story I'm replying " +
                                "to, and others who have replied";
                        mCreateFrag.setmAccess("parentRepliers");
                        break;
                    case (75):
                        progressText = "All signed-in users";
                        mCreateFrag.setmAccess("signedIn");
                        break;
                    case (100):
                        progressText = "All users";
                        mCreateFrag.setmAccess("all");
                        break;
                    default:
                        progressText = "Only me";
                        mCreateFrag.setmAccess("justMe");
                        break;
                }
                textview.setText(progressText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mCreateFrag = (CreateFragment) getParentFragment();
        mCreateFrag.setNextListener(this.getNextListener());
        mCreateFrag.setSkipListener(this.getSkipListener());
        return v;
    }

    View.OnClickListener getNextListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                Fragment uploadFrag = new UploadFragment();
                getFragmentManager().beginTransaction().replace(R.id
                        .create_tools, uploadFrag).commit();
            }
        };
        return listener;
    }

    View.OnClickListener getSkipListener() {
        return getNextListener();
    }

}
