package com.bauble_app.bauble.create;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.widget.RelativeLayout.BELOW;

/**
 * Created by princ on 5/1/2017.
 */

public class RecordFragment extends Fragment {
    private String mFilePath;
    private String mFileName;
    private String mFileExtension;
    private RecordButton mRecordButton;
    private MediaRecorder recorder;
    private int recordCount;

    private boolean mSupportsPause;

    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private CreateFragment mCreateFrag;
    private MediaPlayer mPlayer;
    private PlayButton mPlayButton;


    // Handles the event of the user allowing/denying permission to record.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("tag", "permission granted");
                    break;
                } else {
                    Log.d("tag", "in permission denied");
                }
                return;
            }
        }

    }

    public RecordFragment() {
        // required empty constructor
    }


    // Perform initialization that does not require Views to have been rendered
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSupportsPause = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
        // Record to the external cache directory for visibility
        mFilePath = getActivity().getExternalFilesDir(null).getAbsolutePath();
        mFileName = "/audiorecordtest";
        mFileExtension = ".m4a";
        recordCount = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_tools, container,
                false);
        mCreateFrag = (CreateFragment) getParentFragment();
        // Append the record buttons to the layout
        appendButtons(v);
        return v;
    }

    View.OnClickListener getNextListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                if (!mSupportsPause) {
                    processFiles();
                } else {
                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                    }
                }
                mCreateFrag.setRecordingPath(mFilePath + mFileName +
                        mFileExtension);
                Fragment tagFrag = new TagFragment();
                getFragmentManager().beginTransaction().replace(R.id
                        .create_tools, tagFrag).commit();
                /*
                Fragment editFrag = new UploadFragment();
                getFragmentManager().beginTransaction().replace(R.id
                        .create_tools, editFrag).commit();
                        */
            }
        };
        return listener;
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

        mRecordButtonParams.addRule(BELOW, R.id.create_wave_forms);
        mRecordButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        mRecordButton.setLayoutParams(mRecordButtonParams);

        layout.addView(mRecordButton);

    }

    private boolean haveRecordPermission()
    {
        String permission = permissions[0];
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    // Button that starts or stops the audio recorder in CreateFragment
    public class RecordButton extends android.support.v7.widget
            .AppCompatImageButton {
        boolean mStartRecording = true;

        // Listener for the record button that starts or stops the recorder when
        // the button is tapped
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                if (haveRecordPermission()) {
                    Log.d("clicker", "permission accepted");
                    onRecord(mStartRecording);
                    if (mStartRecording) {
                        setImageResource(R.drawable.ic_action_btn_stop);
                    } else {
                        setImageResource(R.drawable.ic_action_btn_record);
                    }
                    mStartRecording = !mStartRecording;
                } else {
                    Log.d("clicker", "permission not accepted");
                    requestPermissions(permissions,
                            REQUEST_RECORD_AUDIO_PERMISSION);
                }
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

    private void initializeRecorder() {
        try {
            if (recorder == null) {
                recorder = new MediaRecorder();
            }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            if (mSupportsPause) {
                recorder.setOutputFile(mFilePath + "/" + mFileName +
                        mFileExtension);
            } else {
                recorder.setOutputFile(mFilePath + "/" + mFileName + "(" +
                        recordCount + ")" + mFileExtension);
            }
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {
        initializeRecorder();
        if (recordCount == 0 || !mSupportsPause) {
            recorder.start();
        } else {
            recorder.resume();
        }
    }

    private void stopRecording() {
        if (mSupportsPause) {
            recorder.pause();
        } else {
            recorder.stop();
            recordCount++;
            recorder.reset();
        }
    }



    // Uses the isoparser-1.1.22 library to concatenate MP4 files
    //
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

    private Fragment getFragment() {
        return this;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void appendPlayButtons(View v) {
        ViewGroup layout = (ViewGroup) v.findViewById(R.id
                .edit_root_viewgroup);

        // Initialize buttons
        mPlayButton = new PlayButton(getActivity());

        // We want the buttons to be 60dp x 60dp but LayoutParams takes pixel
        // arguments
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                60,
                getResources().getDisplayMetrics());

        RelativeLayout.LayoutParams mPlayButtonParams = new RelativeLayout
                .LayoutParams(dp, dp);

        // mPlayButtonParams.addRule(RelativeLayout.BELOW, R.id
        // .edit_upload_btn);
        mPlayButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        mPlayButton.setLayoutParams(mPlayButtonParams);

        layout.addView(mPlayButton);
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(((CreateFragment) getParentFragment())
                    .getRecordingPath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("MP", e.getMessage());
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    // Button that plays or stops the mAudio being played in CreateFragment
    public class PlayButton extends android.support.v7.widget
            .AppCompatImageButton {
        boolean mStartPlaying = true;

        // Listener for the play button that plays or stops the mAudio when
        // the button is tapped
        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setImageResource(R.drawable.ic_action_btn_stop);
                } else {
                    setImageResource(R.drawable.ic_action_btn_play);
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        // Constructor
        public PlayButton(Context context) {
            super(context);
            setImageResource(R.drawable.ic_action_btn_play);
            setOnClickListener(clicker);
        }
    }
}
