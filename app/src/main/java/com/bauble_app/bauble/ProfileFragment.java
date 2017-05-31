package com.bauble_app.bauble;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    public DemoProfileAdapter adapter;
    public ProgressBar progressBar;
    private FragmentManager fragManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);
        showName(v);
        Button btn = (Button) v.findViewById(R.id.profile_logout);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_logout,
                        (ViewGroup) v.findViewById(R.id.toast_layout_root));

                TextView text = (TextView) layout.findViewById(R.id.toast_save_text);
                text.setText("Sorry, this demo prohibits you " +
                        "from logging out.");

                Toast toast = new Toast(getContext().getApplicationContext());
                //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setGravity(Gravity.BOTTOM, 0, 200);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

//                Toast.makeText(getContext(), "Sorry, this demo prohibits you " +
//                        "from logging out.", Toast.LENGTH_LONG).show();
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

        adapter = new DemoProfileAdapter(getContext(),StorySingleton.getInstance()
                .getStoryMap(), StorySingleton.getInstance().getOwnedKeys());

        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = (ListView) view.findViewById(R.id
                .profile_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //ARGUMENTS: AdapterView<?> parent, View view, int position, long id

                StorySingleton.getInstance().setViewStory(arg2);

                // Placeholder for transition to view
                ProfileFragment.this.fragManager = getActivity().getSupportFragmentManager();
                // Placeholder frag transaction
                fragManager.beginTransaction()
                        .replace(R.id.content, new ViewFragment())
                        .commit();
                // Toast.makeText(getActivity().getApplicationContext(), "Text message", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
