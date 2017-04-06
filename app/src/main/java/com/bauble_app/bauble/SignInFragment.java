package com.bauble_app.bauble;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by princ on 4/6/2017.
 */

public class SignInFragment extends Fragment {
    private FragmentManager fragManager;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        fragManager = ((MainNavActivity) getActivity()).getMyFragManager();

        Button signInLater = (Button) v.findViewById(R.id.sign_in_later);
        signInLater.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new SignUpFragment())
                        .commit();
            }
        });
        return v;
    }
}
