package com.bauble_app.bauble;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    public ListView listView;
    public FeedAdapter adapter;
    private FragmentManager fragManager;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    public FeedFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_feed, container,
                false);

        // Old code for hard coding stories in
        /*
        List<StoryObject> list = new ArrayList<StoryObject>();

        for (int i = 1; i <= 10; i++) {
            list.add(new StoryObject(i + " Title " + i));
        }
        */

        // Get ListView object from xml
        listView = (ListView) v.findViewById(R.id.feed_list);

        adapter = new FeedAdapter(getContext(), StorySingleton.getInstance().storyList);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = (ListView) view.findViewById(R.id
                .feed_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //ARGUMENTS: AdapterView<?> parent, View view, int position, long id

                StorySingleton.getInstance().setViewStory(arg2);

                // Placeholder for transition to view
                FeedFragment.this.fragManager = getActivity().getSupportFragmentManager();
                // Placeholder frag transaction
                fragManager.beginTransaction()
                        .replace(R.id.content, new ViewFragment())
                        .commit();
                // Toast.makeText(getActivity().getApplicationContext(), "Text message", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
