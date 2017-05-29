package com.bauble_app.bauble.explore;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bauble_app.bauble.MainNavActivity;
import com.bauble_app.bauble.R;
import com.bauble_app.bauble.StoryObject;
import com.bauble_app.bauble.StorySingleton;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {
    private StorySingleton mStorySingleton;
    private StoryObject mRoot;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorySingleton = StorySingleton.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
        mRoot = mStorySingleton.getViewStory();
        if (mRoot == null) {
            if (!mStorySingleton.isEmpty()) {
                mRoot = mStorySingleton.getStory(0);
            }
        }
        Gson gsonDebug = new GsonBuilder().setPrettyPrinting().create();
        String rootDebug = gsonDebug.toJson(mRoot, StoryObject.class);
        System.out.println(rootDebug);

        String rootImageFileName = mRoot.grabUniqueId() + "" +
                ".png";
        File rootImageFile = new File(MainNavActivity
                .THUMB_ROOT_DIR,
                rootImageFileName);
        CircleImageView root = (CircleImageView)
                v
                        .findViewById(R.id
                                .explore_root);
        Glide.with(getContext()).load(rootImageFile)
                .into(root);
        showChildren(v);
        return v;
    }

    private void showChildren(View v) {
        if (mRoot.getChildren() != null) {
            System.out.println("root has children:" + mRoot.getChildren()
                    .size());
            List<String> children = mRoot.getChildren();
            int childCount = children.size();
            if (childCount >= 1) {
                String leftChildImageFileName = children.get(0) + ".png";
                File leftChildImageFile = new File(MainNavActivity
                        .THUMB_ROOT_DIR,
                        leftChildImageFileName);
                CircleImageView root_left = (CircleImageView) v
                        .findViewById(R.id.explore_root_left);
                Glide.with(getContext()).load(leftChildImageFile).into(root_left);

                List<String> grandChildren = mStorySingleton.getStory
                        (children.get(0)).getChildren();
                int grandChildCount = grandChildren.size();
                if (grandChildCount >= 1) {
                    String leftGrandChildImageFileName = grandChildren.get(0) +
                            ".png";
                    File leftGrandChildImageFile = new File(MainNavActivity
                            .THUMB_ROOT_DIR,
                            leftGrandChildImageFileName);
                    CircleImageView root_left_left = (CircleImageView) v
                            .findViewById(R.id.explore_root_left_left);
                    Glide.with(getContext()).load(leftGrandChildImageFile)
                            .into(root_left_left);

                }
                if (grandChildCount >= 2) {
                    String rightGrandChildImageFileName = grandChildren.get(1) + "" +
                            ".png";
                    File rightGrandChildImageFile = new File(MainNavActivity
                            .THUMB_ROOT_DIR,
                            rightGrandChildImageFileName);
                    CircleImageView root_left_right = (CircleImageView)
                            v
                            .findViewById(R.id.explore_root_left_right);
                    Glide.with(getContext()).load(rightGrandChildImageFile)
                            .into(root_left_right);
                }
            }
            if (childCount >= 2) {
                String imageFileName = children.get(1) + ".png";
                File imageFile = new File(MainNavActivity.THUMB_ROOT_DIR,
                        imageFileName);
                CircleImageView root_right = (CircleImageView) v
                        .findViewById(R.id.explore_root_right);
                Glide.with(getContext()).load(imageFile).into(root_right);

                List<String> grandChildren = mStorySingleton.getStory
                        (children.get(1)).getChildren();
                int grandChildCount = grandChildren.size();
                if (grandChildCount >= 1) {
                    String leftGrandChildImageFileName = grandChildren.get(0) +
                            ".png";
                    File leftGrandChildImageFile = new File(MainNavActivity
                            .THUMB_ROOT_DIR,
                            leftGrandChildImageFileName);
                    CircleImageView root_right_left = (CircleImageView)
                            v
                            .findViewById(R.id.explore_root_right_left);
                    Glide.with(getContext()).load(leftGrandChildImageFile)
                            .into(root_right_left);

                }
                if (grandChildCount >= 2) {
                    String rightGrandChildImageFileName = grandChildren.get(1) + "" +
                            ".png";
                    File rightGrandChildImageFile = new File(MainNavActivity
                            .THUMB_ROOT_DIR,
                            rightGrandChildImageFileName);
                    CircleImageView root_right_right = (CircleImageView)
                            v
                                    .findViewById(R.id
                                            .explore_root_right_right);
                    Glide.with(getContext()).load(rightGrandChildImageFile)
                            .into(root_right_right);
                }
            }

        }
    }

}
