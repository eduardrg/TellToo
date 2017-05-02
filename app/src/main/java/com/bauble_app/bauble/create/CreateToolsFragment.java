package com.bauble_app.bauble.create;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bauble_app.bauble.R;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.media.MediaRecorder.AudioSource.MIC;

/**
 * Created by princ on 5/1/2017.
 */

public class CreateToolsFragment extends Fragment {
    private String mFilePath;
    private String mFileName;
    private String mFileExtension;
    private RecordButton mRecordButton;
    private MediaRecorder recorder;
    private int recordCount;

    public CreateToolsFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_tools, container,
                false);
        // Record to the external cache directory for visibility
        mFilePath = getActivity().getExternalCacheDir().getAbsolutePath();
        mFileName = "/audiorecordtest";
        mFileExtension = ".mp4";
        recordCount = 0;

        // Append the record buttons to the layout
        appendButtons(v);

        // Add a listener for the 'next' button to show the EditFragment for
        // reviewing/editing the audio
        Button btn = (Button) v.findViewById(R.id.create_next_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                processFiles();
                Bundle data = new Bundle();
                data.putString("fileLocation", mFilePath + mFileName + mFileExtension);
                data.putString("fileName", mFileName +
                        mFileExtension);
                Fragment editFrag = new EditFragment();
                editFrag.setArguments(data);
                getFragmentManager().beginTransaction().replace(R.id
                        .create_tools, editFrag).commit();
            }
        });
        return v;
    }
    private void appendButtons(View v) {
        ViewGroup layout = (ViewGroup) v;

        // Initialize buttons
        mRecordButton = new RecordButton(getActivity());
        mRecordButton.setId(R.id.create_record_btn);

        // We want the buttons to be 60dp x 60dp but LayoutParams takes pixel
        // arguments
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                60,
                getResources().getDisplayMetrics());

        RelativeLayout.LayoutParams mRecordButtonParams = new RelativeLayout
                .LayoutParams(dp, dp);

        mRecordButtonParams.addRule(RelativeLayout.BELOW, R.id.create_wave_forms);
        mRecordButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        mRecordButton.setLayoutParams(mRecordButtonParams);

        layout.addView(mRecordButton);
    }

    // Button that starts or stops the audio recorder in CreateFragment
    public class RecordButton extends android.support.v7.widget
            .AppCompatImageButton {
        boolean mStartRecording = true;

        // Listener for the record button that starts or stops the recorder when
        // the button is tapped
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setImageResource(R.drawable.ic_action_btn_stop);
                } else {
                    setImageResource(R.drawable.ic_action_btn_record);
                }
                mStartRecording = !mStartRecording;
            }
        };

        // Constructor
        public RecordButton(Context context) {
            super(context);
            // use setBackground or setBackgroundDrawable depending on the
            // API level
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(getResources().getDrawable(R.drawable.round_button));
            } else {
                setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
            }
            setImageResource(R.drawable.ic_action_btn_record);
            setAdjustViewBounds(true);
            setScaleType(ScaleType.FIT_CENTER);
            setOnClickListener(clicker);
        }
    }

    private MediaRecorder initializeRecorder() {
        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(mFilePath + "/" + mFileName + "(" +
                recordCount + ")" + mFileExtension);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("MR", e.getMessage() + ": " + mFileName);
        }
        return recorder;
    }

    private void startRecording() {
        recorder = initializeRecorder();
        recorder.start();
        recordCount++;
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    // Uses the isoparser-1.1.22 library to concatenate MP4 files
    private void processFiles() {
        List<Movie> inMovies = new ArrayList<Movie>();

        for (int i = 0; i < recordCount; i++) {
            String videoUri = mFilePath + mFileName + "(" + i + ")" +
                    mFileExtension;
            try {
                inMovies.add(MovieCreator.build(videoUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<Track> audioTracks = new LinkedList<Track>();

        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                audioTracks.add(t);
            }
        }

        Movie result = new Movie();

        if (!audioTracks.isEmpty()) {
            try {
                result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Container out = new DefaultMp4Builder().build(result);

        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(String.format(mFilePath + mFileName + mFileExtension),
                    "rw")
                    .getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.writeContainer(fc);
            fc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
}
