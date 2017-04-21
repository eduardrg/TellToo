package com.bauble_app.bauble;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    public ListView listView;
    public FeedAdapter adapter;
    private FragmentManager fragManager;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_feed, container,
                false);

        ListView listView = (ListView) v.findViewById(R.id.feed_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // Placeholder for transition to view
                FeedFragment.this.fragManager = getActivity().getSupportFragmentManager();
                // Placeholder frag transaction
                fragManager.beginTransaction()
                        .replace(R.id.content, new ViewFragment())
                        .commit();
                // Toast.makeText(getActivity().getApplicationContext(), "Text message", Toast.LENGTH_SHORT).show();

            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.feed_list);


        List<StoryObject> list = new ArrayList<StoryObject>();

        for (int i = 1; i <= 10; i++) {
            list.add(new StoryObject(i + " Title " + i));
        }

        adapter = new FeedAdapter(this.getContext(), list);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }

}
