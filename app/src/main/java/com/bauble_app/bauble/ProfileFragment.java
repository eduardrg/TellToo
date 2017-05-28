package com.bauble_app.bauble;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by princ on 4/19/2017.
 */

public class ProfileFragment extends Fragment {
    public ListView listView;
    public ProfileAdapter adapter;
    public ProgressBar progressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        showName(v);
        Button btn = (Button) v.findViewById(R.id.profile_logout);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                Toast.makeText(getContext(), "Sorry, this demo prohibits you " +
                        "from logging out.", Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    private void showName(final View v) {
        String name = "CapstoneUser";
        StorySingleton.getInstance().setUserName(name);
        ((TextView) v.findViewById(R.id.profile_username))
                .setText(name);

        // Remove loading spinner
        progressBar = (ProgressBar) v.findViewById(R.id.profile_loading);
        progressBar.setVisibility(View.GONE);

        // Load owned stories
        listView = (ListView) v.findViewById(R.id.profile_list);

        for (String key : StorySingleton.getInstance()
                .getStoryMap().keySet()) {
            StoryObject story = StorySingleton.getInstance()
                    .getStoryMap()
                    .get(key);
            if (!StorySingleton.getInstance().getOwnedStoriesMap().keySet().contains(key) &&
                    story.getAuthor().equals(name)) {
//                                ownedStories.put(key, story);
                StorySingleton.getInstance().addOwnedKey(key);
                // Add story to owned array for profile fragment
                StorySingleton.getInstance().addOwnedStory(key, story);
            }
        }

        adapter = new ProfileAdapter(getContext(), StorySingleton.getInstance().getOwnedKeys());

        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }

}
