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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private FragmentManager mFragManager;
    private DatabaseReference mFirebaseRef;
    private FirebaseAuth mAuth;

    private EditText mNameInput;
    private EditText mEmailInput;
    private EditText mPassInput;
    private Button mCancel;
    private Button mSubmit;
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the fragment manager
        mFragManager = ((MainNavActivity) getActivity()).getMyFragManager();
        mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container,
                false);

        mNameInput = ((EditText) v.findViewById(R.id
                .sign_up_name_input));
        mEmailInput = ((EditText) v.findViewById(R.id
                .sign_up_email_input));
        mPassInput = ((EditText) v.findViewById(R.id
                .sign_up_pass_input));
        mCancel = (Button) v.findViewById(R.id.sign_up_cancel_btn);
        mSubmit = (Button) v.findViewById(R.id.sign_up_submit_btn);

        // Listener that directs the user to the authorization choice screen
        // when 'Cancel' is tapped
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                mFragManager.beginTransaction().replace(R.id.content, new AuthChoiceFragment())
                        .commit();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                String name = mNameInput.getText().toString();
                String email = mEmailInput.getText().toString();
                String pass = mPassInput.getText().toString();
                registerAccount(name, email, pass);
            }
        });

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
            mFragManager.beginTransaction().replace(R.id
                    .content, new
                    ProfileFragment()).commit();
        }
    }

    // Register the new account with FirebaseAuth and store username in
    // FirebaseDB, making sure name, email, and password are ok
    private void registerAccount(final String name, final String email, final String password) {
        // Check that the name, email, and password are valid
        if (!AuthUtils.isValidName(name)) {
            Toast.makeText(getContext(), "Invalid username.",
                    Toast.LENGTH_SHORT).show();
        } else if (!AuthUtils.isValidEmail(email)) {
            Toast.makeText(getContext(), "Invalid email.",
                    Toast.LENGTH_SHORT).show();
        } else if (!AuthUtils.isValidPassword(password)) {
            Toast.makeText(getContext(), "Invalid password.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Check whether the name already exists
            mFirebaseRef.child("emails/" + name)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Register the new account if the username is
                            // not taken
                            if (!dataSnapshot.exists()) {
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
                                                    DatabaseReference userIdRef = mFirebaseRef.child("users/" + userId);
                                                    DatabaseReference
                                                            emailRef =
                                                            mFirebaseRef
                                                                    .child
                                                                            ("emails");
                                                    userIdRef.child("name").setValue(name);
                                                    emailRef.child(name)
                                                            .setValue(email);
                                                    updateUI(user);
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(getContext(), task.getException().getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                    updateUI(null);
                                                }
                                            }
                                        });
                            } else {
                                // Notify the user if username is already taken
                                Toast.makeText(getContext(), "Username " +
                                                "already exists.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //TODO: handle error
                        }
                    });
        }

    }

}
