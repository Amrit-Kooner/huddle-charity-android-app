package com.example.huddlecharityapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeleteConfirmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteConfirmFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String IDparam;

    private String typeParam;

    private FirebaseDatabase db;
    public DeleteConfirmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeleteFundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteConfirmFragment newInstance(String param1, String param2) {
        DeleteConfirmFragment fragment = new DeleteConfirmFragment();
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
            IDparam = getArguments().getString(ARG_PARAM1);
            typeParam = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_delete, container, false);

        Button confirm_button = view.findViewById(R.id.confirmButton);
        Button cancel_button = view.findViewById(R.id.cancelButton);

        TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

        String ID = IDparam;

        db = FirebaseDatabase.getInstance();

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeParam.equals("fundraiser")) {
                    DatabaseReference fundraisersRef = db.getReference("fundraisers");
                    DatabaseReference fundraiserRef = fundraisersRef.child(ID);
                    fundraiserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Fundraiser found, retrieve the object and delete it
                                FundraiserClass fundraiser = dataSnapshot.getValue(FundraiserClass.class);
                                fundraiserRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            fundraiser.getUserId();
                                            String userId = fundraiser.getUserId();
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                            userRef.child("fundraiserID").setValue("null");
                                            Toast.makeText(getActivity(), "Fundraiser deleted", Toast.LENGTH_SHORT).show();
                                            replaceFragment(new UserProfileFragment(), R.id.container);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Fundraiser not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Error retrieving fundraiser data", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (typeParam.equals("charity")) {
                    DatabaseReference charityRef = db.getReference("charities").child(ID);
                    charityRef.removeValue();
                } else if (typeParam.equals("event")) {
                    DatabaseReference eventRef = db.getReference("events").child(ID);
                    eventRef.removeValue();
                } else if (typeParam.equals("news")) {
                    DatabaseReference newsRef = db.getReference("news").child(ID);
                    newsRef.removeValue();
                }
                replaceFragment(new MainSearchFragment(), R.id.container);
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setText("SEARCH");
                replaceFragment(new MainSearchFragment(), R.id.container);
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