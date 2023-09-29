package com.example.huddlecharityapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateFundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateFundFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private static final String ARG_PARAM5 = "param5";

    // TODO: Rename and change types of parameters
    private String mParam1; //title
    private String mParam2; //desc

    private String mParam3; //bio

    private String mParam4; //image

    private String mParam5; //ID

    private Button confirm_button, cancel_button;
    private ImageButton image_button;

    private Uri imageUri;

    private String fundID;

    private EditText editTextTitle, editTextDescription, editTextBio;
    private ImageView fundPicture;

    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    public UpdateFundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateFundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateFundFragment newInstance(String param1, String param2, String param3, String param4, String param5) {
        UpdateFundFragment fragment = new UpdateFundFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_fund, container, false);

        TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

        confirm_button = view.findViewById(R.id.confirmButton);
        cancel_button = view.findViewById(R.id.cancelButton);
        image_button = view.findViewById(R.id.uploadImageButton);

        editTextTitle = view.findViewById(R.id.editTitle);
        editTextDescription = view.findViewById(R.id.editDescription);
        editTextBio = view.findViewById(R.id.editBio);
        fundPicture = view.findViewById(R.id.mainImage);

        fundID = mParam5;

        editTextTitle.setText(mParam1);
        editTextBio.setText(mParam2);
        editTextDescription.setText(mParam3);

        Uri uri = Uri.parse(mParam4);
        Picasso.get().load(uri).into(fundPicture);

        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fundPicture.getDrawable() == null) {
                    Toast.makeText(getActivity(), "Please add a profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextTitle.getText().toString())) {
                    Toast.makeText(getActivity(), "Enter title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextDescription.getText().toString())) {
                    Toast.makeText(getActivity(), "Enter description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextBio.getText().toString())) {
                    Toast.makeText(getActivity(), "Enter bio", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference fundraiserRef = db.getReference("fundraisers").child(fundID);

                fundraiserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            FundraiserClass fundraiser = dataSnapshot.getValue(FundraiserClass.class);
                            if (fundraiser != null) {
                                if (!fundraiser.getTitle().equals(editTextTitle.getText().toString())) {
                                    fundraiser.setTitle(editTextTitle.getText().toString());
                                    Toast.makeText(getActivity(), "Updated title", Toast.LENGTH_SHORT).show();
                                }
                                if (!fundraiser.getBio().equals(editTextBio.getText().toString())) {
                                    fundraiser.setBio(editTextBio.getText().toString());
                                    Toast.makeText(getActivity(), "Updated Bio", Toast.LENGTH_SHORT).show();
                                }
                                if (!fundraiser.getDescription().equals(editTextDescription.getText().toString())) {
                                    fundraiser.setDescription(editTextDescription.getText().toString());
                                    Toast.makeText(getActivity(), "Updated Description", Toast.LENGTH_SHORT).show();
                                }
                                if(imageUri != null){
                                    uploadToFirebase(imageUri);
                                    Toast.makeText(getActivity(), "Updated Profile Picture", Toast.LENGTH_SHORT).show();
                                }
                                fundraiserRef.setValue(fundraiser); //
                            }
                        }
                        pageTitle.setText("YOUR PROFILE");
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageTitle.setText("YOUR PROFILE");
                replaceFragment(new UserProfileFragment(), R.id.container);
            }
        });

        return view;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Picasso.get().load(imageUri).into(fundPicture);
                    }
                }
            });

    private void uploadToFirebase(Uri uri){
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        DatabaseReference fundRef = FirebaseDatabase.getInstance().getReference().child("fundraisers").child(fundID).child("image");

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fundRef.setValue(uri.toString());
                    }
                });
            }
        });
    }

    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}