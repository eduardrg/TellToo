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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by princ on 4/6/2017.
 */

public class SignInFragment extends Fragment {
    private FragmentManager fragManager;
    private FirebaseAuth mAuth;

    private EditText mNameInput;
    private EditText mPassInput;
    private Button mSignUp;
    private Button mForgot;
    private DatabaseReference mFirebaseRef;
    private Button mSignIn;
    /*
    private ProgressBar mProgress;
    private int mProgressStatus;
    */

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
        mFirebaseRef = FirebaseDatabase.getInstance().getReference();

        // mProgressStatus = 0;

        mNameInput = (EditText) v.findViewById(R.id.sign_in_name_input);
        mPassInput = (EditText) v.findViewById(R.id.sign_in_pass_input);
        mSignUp = (Button) v.findViewById(R.id.sign_in_signup_btn);
        mForgot = (Button) v.findViewById(R.id.sign_in_forgot_butt);
        mSignIn = (Button) v.findViewById(R.id.sign_in_signin_btn);
        // mProgress = (ProgressBar) v.findViewById(R.id.sign_in_progress);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new SignUpFragment())
                        .commit();
            }
        });

        mForgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                fragManager.beginTransaction().replace(R.id.content, new ForgotFragment())
                        .commit();
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                String emailOrName = getInput(mNameInput);
                String pass = getInput(mPassInput);
                signIn(emailOrName, pass);
            }
        });

        return v;
    }

    private void signIn(final String nameOrEmail, final String password) {
        if (AuthUtils.isValidEmail(nameOrEmail) && AuthUtils.isValidPassword(password)) {
            signInWithEmail(nameOrEmail, password);
        } else if (AuthUtils.isValidName(nameOrEmail) && AuthUtils.isValidPassword(password)){
            mFirebaseRef.child("emails/" + nameOrEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                signInWithEmail(dataSnapshot.getValue()
                                        .toString(), password);
                            } else {
                                Log.i("SignIn", "does not exist: " + nameOrEmail);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //TODO: handle error
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Invalid credentials.", Toast
                    .LENGTH_SHORT).show();
        }
    }

    private String getInput(EditText input) {
        return input.getText().toString().trim();
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
