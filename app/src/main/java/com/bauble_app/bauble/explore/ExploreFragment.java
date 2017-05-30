package com.bauble_app.bauble.explore;


import android.content.res.Resources;
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
import com.bauble_app.bauble.ViewFragment;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {
    private StorySingleton mStorySingleton;
    private StoryObject mRoot;
    private Resources mResources;
    private View mView;
    // TODO: this can be used to add a back button
    private Stack<StoryObject> mClickStack;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorySingleton = StorySingleton.getInstance();
        mResources = getResources();
        mClickStack = new Stack<StoryObject>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
        mView = v;
        mRoot = mStorySingleton.getViewStory();
        if (mRoot == null) {
            if (!mStorySingleton.isEmpty()) {
                mRoot = mStorySingleton.getStory(0);
                mClickStack.push(mRoot);
                showChildrenLoop(mRoot, "explore_root");
            }
        } else {
            mClickStack.push(mRoot);
            showChildrenLoop(mRoot, "explore_root");
        }
        /*
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
        */
        return v;
    }

    private void loadCoverImage(String uniqueId, CircleImageView cover) {
        String imageFileName = uniqueId + ".png";
        File imageFile = new File(MainNavActivity.THUMB_ROOT_DIR,
                imageFileName);
        Glide.with(getContext())
                .load(imageFile)
                .into(cover);
        // cannot call setTag(Object) when using Glide
        cover.setTag(cover.getId(), uniqueId);
        attachListeners(uniqueId, cover);
    }

    // for when we are redrawing the tree
    // we need to load "blank" images into the cover CircleImageViews
    // and remove any lingering tags/listeners so that clicking them does
    // nothing
    private void loadBlankCover(CircleImageView cover) {
        Glide.with(getContext()).load("").placeholder(R.drawable
                .light_grey_color_drawable)
                .into(cover);
        cover.setTag(cover.getId(), null);
        cover.setOnClickListener(null);
        cover.setOnLongClickListener(null);
    }

    // Iterate over all views and loadBlankCovers if they are circleImageViews
    private void resetTreeCovers(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof CircleImageView) {
                loadBlankCover((CircleImageView) v);
            } else if (v instanceof ViewGroup) {
                resetTreeCovers((ViewGroup) v);
            }
        }

    }

    /*
     TODO: maybe use swipe gestures to navigate instead, bring up a preview of
      the
      story on double tap and, open the story on long press
    */
    private void attachListeners(String uniqueId, CircleImageView cover) {
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag(v.getId());
                if (tag != null && tag instanceof String) {
                    StoryObject clickedStory = mStorySingleton.getStory(
                            (String) tag);
                    if (!clickedStory.equals(mRoot)) {
                        mClickStack.push(clickedStory);
                        mRoot = mStorySingleton.getStory((String) tag);
                        resetTreeCovers((ViewGroup) mView);
                        showChildrenLoop(mRoot, "explore_root");
                    }
                }
            }
        });
        cover.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Object tag = v.getTag(v.getId());
                if (tag != null && tag instanceof String) {
                    mStorySingleton.setViewKey((String) tag);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction().replace
                            (R.id.content, new ViewFragment())
                            .commit();
                }
                return true;
            }
        });

    }
    private void showChildrenLoop(StoryObject so, String idAsString) {
        if (idAsString.equals("explore_root")) {
            CircleImageView cover = (CircleImageView) mView.findViewById(R
                    .id.explore_root);
            String uniqueId = so.grabUniqueId();
            loadCoverImage(uniqueId, cover);

            //left
            if (so.getChildren().size() >= 1) {
                StoryObject leftChild = mStorySingleton.getStory(so
                        .getChildren().get(0));
                showChildrenLoop(leftChild, "l");
            }
            //right
            if (so.getChildren().size() >= 2) {
                StoryObject rightChild = mStorySingleton.getStory(so
                        .getChildren().get(1));
                showChildrenLoop(rightChild, "r");
            }
        } else {
            int id = mResources.getIdentifier(idAsString, "id", getContext()
                    .getPackageName());
            if (id != 0) {
                // 0 is not a valid id; it is returned when the id is not found
                // if it's not 0, we're still not at the bottom of the tree
                CircleImageView cover = (CircleImageView) mView.findViewById
                        (id);
                String uniqueId = so.grabUniqueId();
                loadCoverImage(uniqueId, cover);
                //left
                if (so.getChildren().size() >= 1) {
                    StoryObject leftChild = mStorySingleton.getStory(so
                            .getChildren().get(0));
                    showChildrenLoop(leftChild, idAsString + "l");
                }
                //right
                if (so.getChildren().size() >= 2) {
                    StoryObject rightChild = mStorySingleton.getStory(so
                            .getChildren().get(1));
                    showChildrenLoop(rightChild, idAsString + "r");
                }
            }
        }

    }
/*
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
    */

}
