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
 * Created by princ on 4/6/2017.
 */

public class AuthChoiceFragment extends Fragment {
    private FragmentManager fragManager;

    public AuthChoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_auth_choice, container,
                false);
        fragManager = getFragmentManager();

        Button signIn = (Button) v.findViewById(R.id.auth_choice_signin);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new SignInFragment())
                        .commit();
            }
        });

        Button signUp = (Button) v.findViewById(R.id.auth_choice_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new SignUpFragment())
                        .commit();
            }
        });

        return v;
    }

}
