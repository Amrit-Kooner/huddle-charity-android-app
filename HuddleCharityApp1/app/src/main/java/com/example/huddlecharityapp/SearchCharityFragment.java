package com.example.huddlecharityapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchCharityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchCharityFragment extends Fragment implements CharityAdapter.ItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference dbRef;

    private TextView pageTitle;

    private List<CharityClass> list = new ArrayList<>();

    private String charityID;

    public SearchCharityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchCharityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchCharityFragment newInstance(String param1, String param2) {
        SearchCharityFragment fragment = new SearchCharityFragment();
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
        View view = inflater.inflate(R.layout.fragment_search_charity, container, false);
        pageTitle = getActivity().findViewById(R.id.pageTitle);


        dbRef = FirebaseDatabase.getInstance().getReference().child("charities");

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_charity1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        CharityAdapter adapter = new CharityAdapter(list, this);
        recyclerView.setAdapter(adapter);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CharityClass charity = dataSnapshot.getValue(CharityClass.class);
                    charityID = dataSnapshot.getKey();
                    list.add(0, charity);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
    @Override
    public void onItemClick(CharityClass charity) {
        pageTitle.setText("CHARITY");
        Fragment fragment = SingleCharityFragment.newInstance(charity.getTitle(), charity.getBio(), charity.getDescription(), charity.getLink(), charity.getImage(), charityID);
        replaceFragment(fragment, R.id.container);

    }
}