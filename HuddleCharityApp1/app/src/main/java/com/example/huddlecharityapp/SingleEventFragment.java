package com.example.huddlecharityapp;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SingleEventFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";
    private static final String ARG_PARAM7 = "param7";
    private static final String ARG_PARAM8 = "param8";
    private static final String ARG_PARAM9 = "param9";
    private static final String ARG_PARAM10 = "param10";

    private String titleParam;
    private String bioParam;
    private String descParam;
    private String streetParam;
    private String cityParam;
    private String postcodeParam;
    private String countryParam;
    private String linkParam;
    private String imageParam;

    private String idParam;

    private ImageView image;
    private TextView title, bio, description;
    private Button link;

    private Uri uri;

    Button deleteButton, updateButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private MapView mapView;
    private GoogleMap googleMap;
    private final String API_KEY = "AIzaSyC1awybZb1PoVuc8W1XMD1HX7gGGiyOWMY";

    public SingleEventFragment() {
        // Required empty public constructor
    }

    public static SingleEventFragment newInstance(String param1, String param2, String param3, String param4,
                                                  String param5, String param6, String param7, String param8,
                                                  String param9, String param10) {
        SingleEventFragment fragment = new SingleEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        args.putString(ARG_PARAM8, param8);
        args.putString(ARG_PARAM9, param9);
        args.putString(ARG_PARAM10, param10);
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
            streetParam = getArguments().getString(ARG_PARAM4);
            cityParam = getArguments().getString(ARG_PARAM5);
            postcodeParam = getArguments().getString(ARG_PARAM6);
            countryParam = getArguments().getString(ARG_PARAM7);
            linkParam = getArguments().getString(ARG_PARAM8);
            imageParam = getArguments().getString(ARG_PARAM9);
            idParam = getArguments().getString(ARG_PARAM9);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_event, container, false);

        image = view.findViewById(R.id.eventImage);
        title = view.findViewById(R.id.eventTitle);
        bio = view.findViewById(R.id.eventBio);
        description = view.findViewById(R.id.eventDescription);
        link = view.findViewById(R.id.link_button);

        deleteButton = view.findViewById(R.id.delete_button);
        TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

        Uri uri = Uri.parse(imageParam);
        Picasso.get().load(uri).into(image);

        title.setText(titleParam);
        description.setText(descParam);
        bio.setText(bioParam);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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
                pageTitle.setText("DELETE EVENT");
                Fragment fragment = DeleteConfirmFragment.newInstance(idParam, "events");
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

    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        String address =  postcodeParam;
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (!addresses.isEmpty()) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(15)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}