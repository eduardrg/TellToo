package com.bauble_app.bauble;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {
    private StorySingleton mStorySingleton;

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
        for (String key : mStorySingleton.getStoryMap().keySet()) {
            StoryObject so = mStorySingleton.getStoryMap().get(key);
            if (so.getParent() == null) {
                TextView textView = new TextView(getContext());
                textView.setText(key);
                ((ViewGroup) v).addView(textView);
            }
        }
        return v;
    }

}
