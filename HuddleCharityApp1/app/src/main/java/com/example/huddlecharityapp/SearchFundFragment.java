package com.example.huddlecharityapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFundFragment extends Fragment implements FundraiserAdapter.ItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference dbRef;

    private Date fundEndDate, currentDate;

    private String fundEndDateString, currentDateString;


    private TextView pageTitle;

    private List<FundraiserClass> list = new ArrayList<>();

    private String fundID;

    public SearchFundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFundFragment newInstance(String param1, String param2) {
        SearchFundFragment fragment = new SearchFundFragment();
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
        View view = inflater.inflate(R.layout.fragment_search_fund, container, false);
        pageTitle = getActivity().findViewById(R.id.pageTitle);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDateString = dateFormat.format(new Date());

        dbRef = FirebaseDatabase.getInstance().getReference().child("fundraisers");

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        FundraiserAdapter adapter = new FundraiserAdapter(list, this);
        recyclerView.setAdapter(adapter);


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FundraiserClass fund = dataSnapshot.getValue(FundraiserClass.class);
                    fundEndDateString = fund.getEndDate();
                    fundID = dataSnapshot.getKey();
                    try {
                        currentDate = dateFormat.parse(currentDateString);
                        fundEndDate = dateFormat.parse(fundEndDateString);
                        if (currentDate.after(fundEndDate)) {
                            dataSnapshot.getRef().removeValue();;
                            database.child("users").child(fund.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserClass user = dataSnapshot.getValue(UserClass.class);
                                        user.setFundraiserID("null");
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle error
                                }
                            });
                        } else {
                            list.add(0,fund);
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }

    @Override
    public void onItemClick(FundraiserClass fundraiser) {
        pageTitle.setText("FUNDRAISER");
        Fragment fragment = SingleFundFragment.newInstance(fundraiser.getTitle(), fundraiser.getDescription(), fundraiser.getBio(), fundraiser.getEndDate(), fundraiser.getTarget(), fundraiser.getImage(), fundraiser.getCurrent(), fundID);
        replaceFragment(fragment, R.id.container);
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}