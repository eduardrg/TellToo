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

import com.bauble_app.bauble.MainNavActivity;
import com.bauble_app.bauble.ProfileFragment;
import com.bauble_app.bauble.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private FragmentManager fragManager;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the fragment manager
        fragManager = ((MainNavActivity) getActivity()).getMyFragManager();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container,
                false);

        // Listener that directs the user to the authorization choice screen
        // when 'Cancel' is tapped
        Button cancel = (Button) v.findViewById(R.id.sign_up_cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new AuthChoiceFragment())
                        .commit();
            }
        });

        attachSubmitListener(v);

        return v;
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // If the user is already signed in, redirect them to the front page
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            fragManager.beginTransaction().replace(R.id
                    .content, new
                    ProfileFragment()).commit();
        }
    }

    private void attachSubmitListener(final View v) {
        Button btn = (Button) v.findViewById(R.id.sign_up_submit_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                String name = ((EditText) v.findViewById(R.id
                        .sign_up_name_input)).getText().toString();
                String email = ((EditText) v.findViewById(R.id
                        .sign_up_email_input)).getText().toString();
                String pass = ((EditText) v.findViewById(R.id
                        .sign_up_pass_input)).getText().toString();
                registerAccount(name, email, pass);
            }
        });
    }

    // TODO: validate sign up form
    private boolean areValid(String name, String email, String password) {
        return true;
    }

    // Register the new account with FirebaseAuth and store username in
    // FirebaseDB
    private void registerAccount(final String name, final String email, String password) {
        if (areValid(name, email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>
                            () {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Auth", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = user.getUid();
                                DatabaseReference userIdRef = database
                                        .getReference().child("users").child
                                                (userId);
                                userIdRef.child("name").setValue(name);
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Auth", "createUserWithEmail:failure", task
                                        .getException());
                                Toast.makeText(getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Invalid name/email/pass",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
