package com.bauble_app.bauble.create;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bauble_app.bauble.R;

import java.io.File;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetMetaFragment extends Fragment {
    private Button mAddTitle;
    private EditText mTitleInput;
    private TextInputLayout mTitleInputLayout;
    private Button mTitleInputDone;

    private CreateFragment mCreateFrag;

    // Requesting permission to read external storage
    private static final int REQUEST_READ_EXTERNAL_PERMISSION = 200;
    private boolean permissionToReadExternalAccepted = false;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    public SetMetaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_set_story_meta, container, false);
        mTitleInput = (EditText) v.findViewById(R.id.create_title_input);
        mTitleInputLayout = (TextInputLayout) v.findViewById(R.id
                .create_title_input_layout);
        mTitleInputDone = (Button) v.findViewById(R.id.create_title_input_done);
        mAddTitle = (Button) v.findViewById(R.id.create_add_title);
        mCreateFrag = (CreateFragment) getParentFragment();

        Button setCover = (Button) v.findViewById(R.id.create_set_cover_image);
        setCover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                // TODO: handle setting story image
                if (!permissionToReadExternalAccepted) {
                    Log.d("tag", "in if");
                    requestPermissions(permissions,
                            REQUEST_READ_EXTERNAL_PERMISSION);
                } else {
                    Log.d("tag", "in else");
                    EasyImage.openChooserWithGallery(getFragment(), "Pick a cover " +
                            "image", 0);
                }
            }
        });


        mTitleInputDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                mCreateFrag.setTitle(mTitleInput
                        .getText()
                        .toString());

            }
        });

        mAddTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                // Hide 'add title' button
                mAddTitle.setVisibility(View
                        .GONE);

                // Show EditText for entering a title
                mTitleInputLayout.setVisibility(View.VISIBLE);
                mTitleInputDone.setVisibility
                        (View.VISIBLE);

            }
        });

        return v;
    }

    // Callback called after user accepts or denies permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("tag", "in permissions callback");
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("tag", "permission granted");
                    permissionToReadExternalAccepted = true;
                    EasyImage.openChooserWithGallery(getFragment(), "Pick a cover " +
                            "image", 0);
                    break;
                } else {
                    Log.d("tag", "in permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private Fragment getFragment() {
        return this;
    }

    private void onPhotosReturned(List<File> imagesFiles) {
        File imageFile = imagesFiles.get(0);
        mCreateFrag.setThumbnailPath(imageFile.getAbsolutePath());
        Button setCover = (Button) getView().findViewById(R.id
                .create_set_cover_image);
        BitmapDrawable myBitmap = new BitmapDrawable(getResources(),
                BitmapFactory
                .decodeFile(imageFile
                .getAbsolutePath
                ()));
        setCover.setBackground(myBitmap);
        setCover.setText(null);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new
                DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                //Handle the images
                onPhotosReturned(imagesFiles);
            }
        });
    }


}
