package com.bauble_app.bauble.create;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bauble_app.bauble.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFragment extends Fragment {
    private MediaRecorder recorder;
    private View mRecordButton;
    private String mFilePath;
    private String mFileName;
    private int recordCount;

    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private String mFileExtension;

    // Handles the event of the user allowing/denying permission to record.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) {
         //TODO: disable UI and prompt for permission again if the user
            // attempts to record
        }

    }

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create, container, false);

        // Insert the fragment that handles setting a story's metadata (cover
        // image, title)
        Fragment setMetaFrag = new SetMetaFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.create_set_meta, setMetaFrag).commit();

        // Insert the fragment that handles recording
        Fragment createToolsFrag = new CreateToolsFragment();
        getChildFragmentManager().beginTransaction().replace(R.id
                .create_tools, createToolsFrag).commit();

        // Request permission to use the microphone
        ActivityCompat.requestPermissions(getActivity(), permissions,
                REQUEST_RECORD_AUDIO_PERMISSION);

        return v;
    }

}
