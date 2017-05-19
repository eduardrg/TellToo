package com.bauble_app.bauble.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bauble_app.bauble.R;

/**
 * Created by princ on 5/18/2017.
 */

public class PrivacyFragment extends Fragment {
    private CreateFragment mCreateFrag;

    public PrivacyFragment() {
        // required constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_privacy, container, false);
        mCreateFrag = (CreateFragment) getParentFragment();
        mCreateFrag.setNextListener(this.getNextListener());
        return v;
    }

    View.OnClickListener getNextListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                Fragment uploadFrag = new UploadFragment();
                getFragmentManager().beginTransaction().replace(R.id
                        .create_tools, uploadFrag).commit();
            }
        };
        return listener;
    }

}
