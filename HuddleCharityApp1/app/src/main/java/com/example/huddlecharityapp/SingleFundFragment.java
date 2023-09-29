package com.example.huddlecharityapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleFundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleFundFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";

    private static final String ARG_PARAM7 = "param7";

    private static final String ARG_PARAM8 = "param8";

    private TextView title, description, bio, endDate, target, current;

    private ImageView image;


    // TODO: Rename and change types of parameters
    private String mParam1, mParam2, mParam3, mParam4, mParam5, mParam6, mParam8;

    private int mParam7;


    String username, firstname, lastname, userImage, fundID;

    private ConstraintLayout constraintLayout;
    private TextView pageTitle;

    private Uri uri;

    UserClass user;

    int donationAmountNum;

    String uid;


    public SingleFundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SingleFundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleFundFragment newInstance(String param1, String param2, String param3, String param4, String param5, String param6, int param7, String param8) {
        SingleFundFragment fragment = new SingleFundFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putInt(ARG_PARAM7, param7);
        args.putString(ARG_PARAM8, param8);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
            mParam6 = getArguments().getString(ARG_PARAM6);
            mParam7 = getArguments().getInt(ARG_PARAM7);
            mParam8 = getArguments().getString(ARG_PARAM8);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_fund, container, false);


        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(Integer.parseInt(mParam5));
        progressBar.setProgress(mParam7);

        title = view.findViewById(R.id.fundTitle);
        description = view.findViewById(R.id.fundDescription);
        bio = view.findViewById(R.id.fundBio);
        endDate = view.findViewById(R.id.fundEnd);
        image = view.findViewById(R.id.fundImage);
        target = view.findViewById(R.id.goalValue);
        current = view.findViewById(R.id.currentValue);

        pageTitle = getActivity().findViewById(R.id.pageTitle);
        Button donateButton = view.findViewById(R.id.donate_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        EditText donationAmount = view.findViewById(R.id.donateExitText);

        uri = Uri.parse(mParam6);
        Picasso.get().load(uri).into(image);

        title.setText(mParam1);
        description.setText(mParam2);
        bio.setText(mParam3);
        endDate.setText(mParam4);
        target.setText("£" + mParam5);
        current.setText("£" + String.valueOf(mParam7));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pop the backstack to go back to the previous fragment
                pageTitle.setText("SEARCH FUND");
                replaceFragment(new SearchFundFragment(), R.id.container);
            }
        });

        fundID = mParam8;

        DatabaseReference fundraisersRef = FirebaseDatabase.getInstance().getReference("fundraisers");
        DatabaseReference fundraiserRef = fundraisersRef.child(fundID);

        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int donationAmountNum;

                String donationAmountText = donationAmount.getText().toString().trim();
                if (donationAmountText.isEmpty()) {
                    return;
                }
                donationAmountNum = Integer.parseInt(donationAmountText);

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String uid = currentUser.getUid();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserClass user = dataSnapshot.getValue(UserClass.class);
                            if (user != null) {
                                int convertedValue = (int) user.getTotal_donations();
                                user.setTotal_donations(convertedValue + donationAmountNum); // Set the updated value
                                userRef.setValue(user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle onCancelled as needed
                    }
                });


                fundraiserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            FundraiserClass fundraiser = dataSnapshot.getValue(FundraiserClass.class);
                            if (fundraiser != null) {
                                int total = fundraiser.getCurrent() + donationAmountNum;
                                fundraiser.setCurrent(total);
                                progressBar.setProgress(donationAmountNum);

                                fundraiserRef.setValue(fundraiser);
                                if (fundraiser.getCurrent() >= Integer.parseInt(fundraiser.getTarget())) {
                                    fundraiser.getUserId();
                                    String userId = fundraiser.getUserId();
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                                    fundraiserRef.removeValue();
                                    userRef.child("fundraiserID").setValue("null");

                                    Toast.makeText(getActivity(), "Fundraiser goal has been reached!!!", Toast.LENGTH_SHORT).show();
                                    replaceFragment(new SearchFundFragment(), R.id.container);
                                } else {
                                    Fragment fragment = SingleFundFragment.newInstance(fundraiser.getTitle(), fundraiser.getDescription(), fundraiser.getBio(), fundraiser.getEndDate(), fundraiser.getTarget(), fundraiser.getImage(), fundraiser.getCurrent(), fundID);
                                    replaceFragment(fragment, R.id.container);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}