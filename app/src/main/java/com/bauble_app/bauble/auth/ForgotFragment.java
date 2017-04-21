package com.bauble_app.bauble.auth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.bauble_app.bauble.R;

/**
 * Created by princ on 4/19/2017.
 */

public class ForgotFragment extends Fragment {
    private FragmentManager fragManager;

    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forgot, container, false);

        fragManager = getFragmentManager();
        Button cancel = (Button) v.findViewById(R.id.forgot_cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new AuthChoiceFragment())
                        .commit();
            }
        });

        return v;
    }

}
