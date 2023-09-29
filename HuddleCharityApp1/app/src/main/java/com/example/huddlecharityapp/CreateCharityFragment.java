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
import android.util.Patterns;
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
 * Use the {@link CreateCharityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateCharityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri imageUri;

    private String charityID;

    private EditText editTextTitle, editTextBio, editTextDescription, editTextLink;

    private ImageView charImage;

    private Button confirm_button, cancel_button;
    private ImageButton image_button;

    private Picasso picasso = Picasso.get();
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public CreateCharityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateCharityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateCharityFragment newInstance(String param1, String param2) {
        CreateCharityFragment fragment = new CreateCharityFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_charity, container, false);

        TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

        confirm_button = view.findViewById(R.id.confirmButton);
        cancel_button = view.findViewById(R.id.cancelButton);
        image_button = view.findViewById(R.id.uploadImageButton);
        charImage = view.findViewById(R.id.mainImage);

        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextTitle = view.findViewById(R.id.editTitle);
                editTextBio = view.findViewById(R.id.editBio);
                editTextDescription = view.findViewById(R.id.editDescription);
                editTextLink = view.findViewById(R.id.editLink);

                if (charImage.getDrawable() == null) {
                    Toast.makeText(getActivity(), "Please add a profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextTitle.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Enter title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextDescription.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Enter description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextBio.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Enter bio", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextLink.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Enter link", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.WEB_URL.matcher(editTextLink.getText().toString().trim()).matches()) {
                    Toast.makeText(getActivity(), "Enter a valid link e.g 'www.google.com'", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference charityRef = FirebaseDatabase.getInstance().getReference("charities").push();
                charityID = charityRef.getKey();
                writeNewCharity(editTextTitle.getText().toString(), editTextBio.getText().toString(), editTextDescription.getText().toString(), editTextLink.getText().toString(),"");

                DatabaseReference charity = FirebaseDatabase.getInstance().getReference().child("charities").child(charityID);

                charity.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            CharityClass charity = snapshot.getValue(CharityClass.class);
                            pageTitle.setText("CHARITY SEARCH");
                            replaceFragment(new SearchCharityFragment(), R.id.container);

                            /* Fragment fragment = SingleFundFragment.newInstance(fund.getTitle(), fund.getDescription(), fund.getBio(), fund.getEndDate(), fund.getTarget(), fund.getImage(), fund.getCurrent(), fundraiserID);
                            replaceFragment(fragment, R.id.container);*/
                            ////
                            //
                            //
                            //
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
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
                        picasso.load(imageUri).into(charImage);
                    }
                }
            });

    private void uploadToFirebase(Uri uri){
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        DatabaseReference charityRef = FirebaseDatabase.getInstance().getReference().child("charities").child(charityID).child("image");

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        charityRef.setValue(uri.toString());
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

    public void writeNewCharity(String title, String bio, String description, String link, String image) {
        DatabaseReference charityRef = FirebaseDatabase.getInstance().getReference("charities").push();
        charityID = charityRef.getKey();

        //String title, String bio, String description, String link, String image

        CharityClass newCharity = new CharityClass(title, bio, description, link, image);
        charityRef.setValue(newCharity);
        uploadToFirebase(imageUri);
        Toast.makeText(getActivity(), "New Charity Created!", Toast.LENGTH_SHORT).show();
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}