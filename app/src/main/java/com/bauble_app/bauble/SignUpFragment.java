package com.bauble_app.bauble;


import android.graphics.Typeface;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private FragmentManager fragManager;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the fragment manager
        fragManager = ((MainNavActivity) getActivity()).getMyFragManager();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container,
                false);

        // Attach OnClick listener to refresh button, so that sign up name is
        // toggled on each click
        attachRefreshListener(v);

        // Attach OnClick listener to sign in button to switch screens
        Button signIn = (Button) v.findViewById(R.id.sign_up_goto_signin);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new
                        SignInFragment())
                        .commit();
            }
        });

        // Set TextViews to use custom typefaces defined in FontHelper
        TextView title = (TextView) v.findViewById(R.id.sign_up_page_title);
        title.setTypeface(FontHelper.getInstance(getActivity()).LatoBold);
        return v;
    }

    private void attachRefreshListener(View v) {
        ImageButton refresh = (ImageButton) v.findViewById(R.id
                .sign_up_refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View signUpRefresh) {
                TextView signUpName = (TextView) (signUpRefresh
                        .getRootView())
                        .findViewById(R.id
                                .sign_up_name);
                String currentName = (String) signUpName.getText();
                if (currentName.equals("CowardlyPurpleElephant")) {
                    signUpName.setText("HeckinRarePupper");
                } else {
                    signUpName.setText("CowardlyPurpleElephant");
                }
            }
        });
    }

}
