package com.example.huddlecharityapp;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private ImageButton back_icon;
    private TextView pageTitle;

    private FirebaseDatabase db;

    private Boolean exists;

    private DatabaseReference reference = db.getInstance().getReference();
    ;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        Button haveAccount = view.findViewById(R.id.already_have_account);
        Button signin_button = view.findViewById(R.id.confirm_login_button);

        EditText editTextEmail = view.findViewById(R.id.email);
        EditText editTextUsername = view.findViewById(R.id.username);
        EditText editTextPassword = view.findViewById(R.id.password);
        EditText editTextConfirmPassword = view.findViewById(R.id.confirm_password);

        back_icon = getActivity().findViewById(R.id.back_icon);
        pageTitle = getActivity().findViewById(R.id.pageTitle);

        Context context = getActivity().getApplicationContext();
        Uri defaultImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(R.drawable.default_pp_image) + "/" +
                context.getResources().getResourceTypeName(R.drawable.default_pp_image) + "/" +
                context.getResources().getResourceEntryName(R.drawable.default_pp_image));

        String defaultImageUriString = defaultImageUri.toString();

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setText("WELCOME");
                back_icon.setVisibility(View.GONE);
                replaceFragment(new WelcomeFragment(), R.id.container);
            }
        });
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setText("LOGIN");
                replaceFragment(new LoginFragment(), R.id.container);
            }
        });

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirm_email, confirm_username, confirm_password, reconfirm_password, confirm_firstname, confirm_lastname;
                confirm_username = String.valueOf(editTextUsername.getText()).trim();
                confirm_email = String.valueOf(editTextEmail.getText()).trim();
                confirm_password = String.valueOf(editTextPassword.getText()).trim();
                reconfirm_password = String.valueOf(editTextConfirmPassword.getText()).trim();

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                Query usernameQuery = usersRef.orderByChild("username").equalTo(confirm_username);

                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getActivity(), "Username already exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });

                if (confirm_username.length() < 3 || confirm_username.length() > 20) {
                    Toast.makeText(getActivity(), "Username should be between 3 and 20 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirm_username)) {
                    Toast.makeText(getActivity(), "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirm_email)) {
                    Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirm_password)) {
                    Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(reconfirm_password)) {
                    Toast.makeText(getActivity(), "Confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!confirm_password.equals(reconfirm_password)) {
                    Toast.makeText(getActivity(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(confirm_email, confirm_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Account created.", Toast.LENGTH_SHORT).show();
                                    String username = editTextUsername.getText().toString();
                                    writeNewUser(username, "", "", defaultImageUriString, 0, "FALSE");

                                    mAuth.getCurrentUser().sendEmailVerification();

                                    replaceFragment(new LoginFragment(), R.id.container);
                                } else {
                                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return view;
    }

    public void writeNewUser(String username, String first_name, String last_name, String profile_picture, int total_donations, String admin) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            DatabaseReference userRef = reference.child("users").child(currentUser.getUid());
            UserClass user = new UserClass(username, first_name, last_name, profile_picture, total_donations, admin, "null");
            userRef.setValue(user);
        }
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}
