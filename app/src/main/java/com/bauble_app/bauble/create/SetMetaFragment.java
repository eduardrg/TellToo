package com.bauble_app.bauble.create;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bauble_app.bauble.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetMetaFragment extends Fragment {
    public SetMetaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_set_story_meta, container, false);

        Button setCover = (Button) v.findViewById(R.id.create_set_cover_image);
        setCover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                //TODO: handle setting story image
            }
        });

        Button setTitle = (Button) v.findViewById(R.id.create_set_title);
        setTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                //TODO: handle setting story title
            }
        });

        return v;
    }
}
