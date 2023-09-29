package com.example.huddlecharityapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.badge.BadgeUtils;
import com.squareup.picasso.Picasso;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppCompatActivity activity;

    private ConstraintLayout constraintLayout;

    String title, description, bio, endDate, target, imageUrl;

    int current;

    String fundID;
    UserClass user;

    TextView  fundTitle, fundDesc, fundBio, fundEndDate, fundTarget, fundCurrent;
    ImageView fundImage;

    String uid;


    Button deleteButton, updateButton;

    ProgressBar progressBar;
    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        CircleImageView userProfileView = view.findViewById(R.id.profile_picture);

        progressBar = view.findViewById(R.id.progressBar);
        deleteButton = view.findViewById(R.id.deleteButton);
        updateButton = view.findViewById(R.id.updateButton);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getInstance().getReference("users").child(uid);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();

        Picasso picasso = Picasso.get();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(UserClass.class);
                assert user != null;
                Uri profilePictureUri = Uri.parse(user.getProfile_picture());
                fundID = user.getFundraiserID();

                // Populate user information
                TextView emailTextView = view.findViewById(R.id.email_textview);
                TextView usernameTextView = view.findViewById(R.id.username_textview);
                TextView firstNameTextView = view.findViewById(R.id.first_name_textview);
                TextView lastNameTextView = view.findViewById(R.id.last_name_textview);

                fundTitle = view.findViewById(R.id.fund_title);
                fundDesc = view.findViewById(R.id.fund_description);
                fundBio = view.findViewById(R.id.fund_bio);
                fundEndDate = view.findViewById(R.id.fund_end);
                fundTarget = view.findViewById(R.id.goalValue);
                fundImage = view.findViewById(R.id.fund_image);
                fundCurrent = view.findViewById(R.id.currentValue);

                printInfo(email, emailTextView);
                printInfo(user.getUsername(), usernameTextView);
                printInfo(user.getFirst_name(), firstNameTextView);
                printInfo(user.getLast_name(), lastNameTextView);
                picasso.load(profilePictureUri).into(userProfileView);

                TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

                // Check if user has an active fundraiser
                if (fundID != null && !fundID.equals("null")) {
                    constraintLayout = view.findViewById(R.id.activeFundraiser);
                    deleteButton = view.findViewById(R.id.deleteButton);
                    updateButton = view.findViewById(R.id.updateButton);

                    constraintLayout.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                    updateButton.setVisibility(View.VISIBLE);

                    // Retrieve and populate fundraiser information
                    DatabaseReference fundraiserRef = db.getInstance().getReference("fundraisers").child(fundID);
                    fundraiserRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                FundraiserClass fundraiser = dataSnapshot.getValue(FundraiserClass.class);
                                if (fundraiser != null) {
                                    // Populate fundraiser information
                                    title = fundraiser.getTitle();
                                    description = fundraiser.getDescription();
                                    bio = fundraiser.getBio();
                                    endDate = fundraiser.getEndDate();
                                    target = fundraiser.getTarget();
                                    imageUrl = fundraiser.getImage();
                                    current = fundraiser.getCurrent();

                                    fundTitle.setText(title);
                                    fundDesc.setText(description);
                                    fundBio.setText(bio);
                                    fundEndDate.setText(endDate);

                                    fundTarget.setText(target);
                                    fundCurrent.setText(String.valueOf(current));

                                    progressBar.setMax(Integer.parseInt(target));
                                    progressBar.setProgress(current);

                                    Uri uri = Uri.parse(imageUrl);
                                    Picasso.get().load(uri).into(fundImage);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Error reading fundraiser data", databaseError.toException());
                        }
                    });

                    constraintLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pageTitle.setText("FUNDRAISER");
                            Fragment fragment = SingleFundFragment.newInstance(title, description,
                                    bio, endDate,
                                    target, imageUrl,
                                    current, fundID);
                            replaceFragment(fragment, R.id.container);
                        }
                    });

                    updateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pageTitle.setText("UPDATE FUND");
                            Fragment fragment = UpdateFundFragment.newInstance(title, description, bio, imageUrl, fundID);
                            replaceFragment(fragment, R.id.container);
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pageTitle.setText("DELETE FUND");
                            Fragment fragment = DeleteConfirmFragment.newInstance(fundID, "fundraiser");
                            replaceFragment(fragment, R.id.container);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error reading user data", databaseError.toException());
            }
        });

        ImageButton editIcon = view.findViewById(R.id.edit_profile_icon);
        TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

        editIcon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                pageTitle.setText("UPDATE PROFILE");
                replaceFragment(new UpdateProfileFragment(), R.id.container);
            }
        });
        return view;
    }

    public void printInfo(String detail, TextView tView) {
        if (detail.isEmpty()) {
            detail = "...";
        }
        tView.setText(detail);
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}


