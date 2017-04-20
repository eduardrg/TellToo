package com.bauble_app.bauble;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    public ListView listView;
    public FeedAdapter adapter;


    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
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
