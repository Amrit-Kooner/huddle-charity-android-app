package com.example.huddlecharityapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth mAuth;
    ProgressBar progressBar;

    private View toolbarView;
    private View headerbarView;
    private BottomNavigationView navView;
    private ImageButton burger_icon;

    private ImageButton back_icon;
    private TextView pageTitle;
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button noAccount = view.findViewById(R.id.dont_have_account);
        Button login_button = view.findViewById(R.id.confirm_login_button);

        EditText editTextEmail = view.findViewById(R.id.email);
        EditText editTextPassword = view.findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        toolbarView = getActivity().findViewById(R.id.main_toolbar);
        headerbarView = getActivity().findViewById(R.id.header_bar);
        navView = getActivity().findViewById(R.id.bottomNavigationView);
        burger_icon = getActivity().findViewById(R.id.burger_icon);
        pageTitle = getActivity().findViewById(R.id.pageTitle);
        back_icon = getActivity().findViewById(R.id.back_icon);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setText("WELCOME");
                back_icon.setVisibility(View.GONE);
                replaceFragment(new WelcomeFragment(), R.id.container);
            }
        });

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setText("SIGNUP");
                replaceFragment(new SignupFragment(), R.id.container);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (checkIfEmailVerification()) {
                                        toolbarView.setVisibility(View.VISIBLE);
                                        navView.setVisibility(View.VISIBLE);
                                        headerbarView.setVisibility(View.VISIBLE);
                                        burger_icon.setVisibility(View.VISIBLE);
                                        back_icon.setVisibility(View.GONE);
                                        pageTitle.setText("NEWS");
                                        Toast.makeText(getActivity(), "Login successful.", Toast.LENGTH_SHORT).show();
                                        replaceFragment(new HomeFragment(), R.id.container);
                                    } else {
                                        Toast.makeText(getActivity(), "Email not verified.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return view;
    }
    private boolean checkIfEmailVerification() {
        if(!mAuth.getCurrentUser().isEmailVerified()){
            return false;
        }
        return true;
    }
    private void replaceFragment(Fragment fragment, int containerId) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}