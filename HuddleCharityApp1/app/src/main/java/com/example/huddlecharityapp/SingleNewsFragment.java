package com.example.huddlecharityapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleNewsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private static final String ARG_PARAM6 = "param6";

    // TODO: Rename and change types of parameters
    private String titleParam;
    private String bioParam;

    private String descParam;
    private String linkParam;
    private String imageParam;

    private String IDparam;

    private ImageView image;

    private TextView title, bio, description;

    private Button link;

    private WebView webView;

    Button deleteButton, updateButton;

    Uri uri;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public SingleNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleNewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleNewsFragment newInstance(String param1, String param2, String param3, String param4, String param5, String param6) {
        SingleNewsFragment fragment = new SingleNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleParam = getArguments().getString(ARG_PARAM1);
            bioParam = getArguments().getString(ARG_PARAM2);
            descParam = getArguments().getString(ARG_PARAM3);
            linkParam = getArguments().getString(ARG_PARAM4);
            imageParam = getArguments().getString(ARG_PARAM5);
            IDparam = getArguments().getString(ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_charity, container, false);

        AppCompatImageButton backButton = getActivity().findViewById(R.id.back_icon);
        backButton.setVisibility(View.VISIBLE);

        deleteButton = view.findViewById(R.id.delete_button);
        TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

        image = view.findViewById(R.id.charImage);
        title = view.findViewById(R.id.charTitle);
        bio = view.findViewById(R.id.charBio);
        description = view.findViewById(R.id.charDescription);
        link = view.findViewById(R.id.link_button);

        uri = Uri.parse(imageParam);
        Picasso.get().load(uri).into(image);

        title.setText(titleParam);
        description.setText(descParam);
        bio.setText(bioParam);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = WebFragment.newInstance(linkParam);
                replaceFragment(fragment, R.id.container);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    UserClass currentUser = snapshot.getValue(UserClass.class);
                    if (currentUser.getAdmin().equals("TRUE")) {
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setText("DELETE NEWS");
                Fragment fragment = DeleteConfirmFragment.newInstance(IDparam, "news");
                replaceFragment(fragment, R.id.container);
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