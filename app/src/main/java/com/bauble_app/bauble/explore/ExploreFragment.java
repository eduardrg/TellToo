package com.bauble_app.bauble.explore;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bauble_app.bauble.BaseTouchListener;
import com.bauble_app.bauble.MainNavActivity;
import com.bauble_app.bauble.R;
import com.bauble_app.bauble.StoryObject;
import com.bauble_app.bauble.StorySingleton;
import com.bauble_app.bauble.ViewFragment;
import com.bauble_app.bauble.create.ReplyFragment;
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
    private Button mBack;
    private BaseTouchListener mSwipeListener;
    private BaseTouchListener mChildTouchListener;

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
        mBack = (Button) v.findViewById(R.id
                .explore_back);
        mBack.setVisibility(View.GONE);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BackDebug", mClickStack.toString());
                if (!mClickStack.isEmpty() && mClickStack.size() > 1) {
                    mClickStack.pop();
                    updateUI(mClickStack.peek());
                }
                if (mClickStack.isEmpty() || mClickStack.size() == 1) {
                    mBack.setVisibility(View.GONE);
                }
            }
        });

        mSwipeListener = new BaseTouchListener(getActivity()) {
            public void onSwipeTop() {
                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() { // get child before, the one that is on the left
                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
                if (mRoot.getChildren() != null && !mRoot.getChildren().isEmpty
                        ()) {
                    processNav(mStorySingleton.getStory(mRoot.getChildren().get
                            (0)));
                }
            }
            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();

                if (mRoot.getChildren() != null && mRoot.getChildren().size
                        () >= 2) {
                    processNav(mStorySingleton.getStory(mRoot.getChildren().get
                            (1)));
                }
            }
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
                processNav(mStorySingleton.getStory(mRoot.getParentString()));
            }

        };

        // Set Swipe Action Recognizer for general layout
        LinearLayout wholeView = (LinearLayout) v.findViewById(R.id
                .explore_whole_view);
        wholeView.setOnTouchListener(mSwipeListener);

        mRoot = mStorySingleton.getViewStory();
        if (mRoot == null) {
            if (!mStorySingleton.isEmpty()) {
                mRoot = mStorySingleton.getStory(0);
                mClickStack.push(mRoot);
                showChildrenLoop(mRoot, "explore_root");
                showReply(mRoot.grabUniqueId(), v);
            }
        } else {
            mClickStack.push(mRoot);
            showChildrenLoop(mRoot, "explore_root");
            showReply(mRoot.grabUniqueId(), v);
        }
        return v;
    }

    private void loadCoverImage(String uniqueId, CircleImageView cover) {
        // get and set images & audio
        if (uniqueId.equals("CapstoneRootStory")) {
            Glide.with(getContext() /* context */)
                    .load(R.drawable.drumfountain)
                    .into(cover);
        } else {
            String imageFileName = uniqueId + ".png";
            File imageFile = new File(MainNavActivity.THUMB_ROOT_DIR,
                    imageFileName);
            Glide.with(getContext())
                    .load(imageFile)
                    .into(cover);
        }

        cover.setTag(cover.getId(), uniqueId);
        attachListeners(cover);
    }

    private void processNav(StoryObject clickedStory) {
        if (clickedStory != null) {
            // Add the clicked story to the back stack
            mClickStack.push(clickedStory);
            updateUI(clickedStory);
            // Show the back button because the stack is guaranteed non-empty
            mBack.setVisibility(View.VISIBLE);
        }
    }

    private void updateUI(StoryObject clickedStory) {
        if (clickedStory != null) {
            // Set the new root to the clicked story
            mRoot = clickedStory;
            resetTreeCovers((ViewGroup) mView);
            // Load cover images into children that exist
            showChildrenLoop(mRoot, "explore_root");
            // Show a preview of the clicked story at top
            showReply(clickedStory.grabUniqueId(), getView());
        }
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
        cover.setOnTouchListener(null);
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

    private void showReply(final String uniqueId, View v) {
        if (uniqueId != null) {
            Bundle bundle = new Bundle();
            bundle.putString("replyStoryKey", uniqueId);
            Fragment replyFrag = new ReplyFragment();
            replyFrag.setArguments(bundle);
            getChildFragmentManager().beginTransaction().replace(R.id
                    .explore_preview, replyFrag).commit();
            v.findViewById(R.id.explore_preview)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mStorySingleton.setViewKey(uniqueId);
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().replace
                                    (R.id.content, new ViewFragment())
                                    .commit();
                        }
                    });
        }
    }

    private void attachListeners(final CircleImageView cover) {
        cover.setOnTouchListener(new GraphTouchListener(getActivity()) {
             public void onSwipeTop() {
                 mSwipeListener.onSwipeTop();
             }

             public void onSwipeRight() { // get child before, the one that is on the left
                 mSwipeListener.onSwipeRight();
             }

             public void onSwipeLeft() {
                 mSwipeListener.onSwipeLeft();
             }

             public void onSwipeBottom() {
                 mSwipeListener.onSwipeBottom();
             }

             @Override
             public void onClick() {
                 final Object tag = cover.getTag(cover.getId());
                 if (tag != null && tag instanceof String) {
                     StoryObject clickedStory = mStorySingleton.getStory(
                             (String) tag);
                     if (!clickedStory.equals(mRoot)) {
                         processNav(clickedStory);
                     }
                 }
             }

             @Override
             public void onDoubleTap() {
                 Object tag = cover.getTag(cover.getId());
                 if (tag != null && tag instanceof String) {
                     mStorySingleton.setViewKey((String) tag);
                     getActivity().getSupportFragmentManager()
                             .beginTransaction().replace
                             (R.id.content, new ViewFragment())
                             .commit();
                 }
             }
                                 }
        );
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

}
