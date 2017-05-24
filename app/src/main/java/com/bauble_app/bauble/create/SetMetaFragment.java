package com.bauble_app.bauble.create;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bauble_app.bauble.R;
import com.bauble_app.bauble.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.Constants;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetMetaFragment extends Fragment {
    private Button mAddTitle;
    private EditText mTitleInput;
    private TextInputLayout mTitleInputLayout;
    private Button mTitleInputDone;

    private CreateFragment mCreateFrag;
    private FragmentManager mFragManager;

    // Requesting permission to read external storage
    private static final int MULTIPLE_PERMISSIONS_REQUEST = 123;
    private boolean permissionToReadExternalAccepted = false;
    private boolean permissionToWriteExternalAccepted = false;
    private String[] permissions = {Manifest.permission
            .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public SetMetaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragManager = getFragmentManager();
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
        attachCoverListener(setCover);

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

        if (savedInstanceState != null) {

        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    // Sets an onclick listener for changing the cover image & returns the id
    // of the view affected

    private int attachCoverListener(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                if (!(permissionToReadExternalAccepted &&
                        permissionToWriteExternalAccepted)) {
                    Log.d("tag", "in if");
                    requestPermissions(permissions,
                            MULTIPLE_PERMISSIONS_REQUEST);
                } else {
                    Log.d("tag", "in else");
                    EasyImage.openChooserWithGallery(getFragment(), "Pick a cover " +
                            "image", 0);
                }
            }
        });
        return v.getId();
    }

    // Callback called after user accepts or denies permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("tag", "in permissions callback");
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean writePermission = grantResults[1] == PackageManager
                            .PERMISSION_GRANTED;
                    boolean readPermission = grantResults[0] == PackageManager
                            .PERMISSION_GRANTED;
                    if (writePermission && readPermission) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Log.d("tag", "permission granted");
                        permissionToReadExternalAccepted = true;
                        permissionToWriteExternalAccepted = true;
                        EasyImage.openChooserWithGallery(getFragment(), "Pick a cover " +
                                "image", 0);
                        break;
                    }
                } else {
                    Log.d("tag", "in permission denied");
                    // permission denied
                }
                return;
            }
        }
    }

    private Fragment getFragment() {
        return this;
    }

    // Callback when EasyImage picker returns a file
    private void onPhotosReturned(List<File> imagesFiles) {
        File imageFile = imagesFiles.get(0);
        // Crop the image to required dimensions (a circle between 100x100
        // and 500x500 pixels in size)
        int dp100 = Utils.getDp(100, getResources());
        int dp500 = Utils.getDp(500, getResources());

        CropImage.activity(Uri.fromFile(imageFile)).setCropShape(CropImageView
                .CropShape
                .OVAL).setMinCropResultSize(dp100, dp100).setMaxCropResultSize
                (dp500, dp500).setFixAspectRatio
                (true)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // This is a result from the image cropper
            CropImage.ActivityResult result = CropImage.getActivityResult
                    (data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mCreateFrag.setThumbnailPath(resultUri.getPath());
                // Find the existing but hidden CircleImageView that will
                // display the cover image
                CircleImageView cover = (CircleImageView) getView()
                        .findViewById(R.id
                                .create_cover_image);
                // Set the CircleImageView's image and attach a listener so
                // the image can still be replaced
                cover.setImageBitmap(BitmapFactory.decodeFile(resultUri
                        .getPath()));
                attachCoverListener(cover);

                // Prepare the RelativeLayout rules for the View directly
                // below the old "set cover" button
                TextInputLayout setTitle = (TextInputLayout) getView().findViewById(R.id
                        .create_title_input_layout);
                Button addTitle = (Button) getView().findViewById(R.id
                        .create_add_title);
                RelativeLayout.LayoutParams addTitleParams = (RelativeLayout
                        .LayoutParams) addTitle.getLayoutParams();
                RelativeLayout.LayoutParams setTitleParams = (RelativeLayout.LayoutParams) setTitle
                        .getLayoutParams();

                // hide the old "set cover" button
                getView().findViewById(R.id.create_set_cover_image)
                        .setVisibility(View.GONE);

                // Show the new CircularImageView
                cover.setVisibility(View.VISIBLE);

                // Update the RelativeLayout rules for the View directly
                // below the old "set cover" button
                setTitleParams.addRule(RelativeLayout.BELOW, R.id.create_cover_image);
                addTitleParams.addRule(RelativeLayout.BELOW, R.id.create_cover_image);
                setTitle.setLayoutParams(setTitleParams);
                addTitle.setLayoutParams(addTitleParams);

                System.out.println("result ok");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Error", "cropper error", error);
            }

        } else if ((requestCode & Constants.RequestCodes.EASYIMAGE_IDENTIFICATOR) > 0) {
            // This is a result from EasyImage image picker
            if (resultCode == RESULT_OK) {
                EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new
                        DefaultCallback() {
                            @Override
                            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                                //TODO: error wrangling
                            }

                            @Override
                            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                                //Handle the images picked by user
                                onPhotosReturned(imagesFiles);
                            }
                        });
            }

        }
    }


}

