package com.bauble_app.bauble.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bauble_app.bauble.ProfileFragment;
import com.bauble_app.bauble.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by princ on 4/6/2017.
 */

public class SignInFragment extends Fragment {
    private FragmentManager fragManager;
    private FirebaseAuth mAuth;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        fragManager = getFragmentManager();
        mAuth = FirebaseAuth.getInstance();

        Button signUp = (Button) v.findViewById(R.id.sign_in_signup_btn);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new SignUpFragment())
                        .commit();
            }
        });


        Button forgot = (Button) v.findViewById(R.id.sign_in_forgot_butt);
        forgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new ForgotFragment())
                        .commit();
            }
        });

        attachSignInListener(v);

        return v;
    }

    // Attach a listener to the sign in button
    private void attachSignInListener(final View v) {
        Button signIn = (Button) v.findViewById(R.id.sign_in_signin_btn);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                String email = getInput(v, R.id.sign_in_name_input);
                String pass = getInput(v, R.id.sign_in_pass_input);
                signInWithEmail(email, pass);
            }
        });
    }

    //TODO: complete method
    private String getEmailFromUserId(String userId) {
        return "eduardrg@uw.edu";
    }

    private String getInput(View v, int resId) {
        return ((EditText) v.findViewById(resId)).getText().toString();
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>
                        () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FASignIn", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FASignIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            fragManager.beginTransaction().replace(R.id.content, new ProfileFragment())
                    .commit();
        }
    }


}
