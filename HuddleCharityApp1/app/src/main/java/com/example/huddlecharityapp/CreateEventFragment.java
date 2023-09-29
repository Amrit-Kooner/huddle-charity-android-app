package com.example.huddlecharityapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;

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

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri imageUri;

    private String eventID;

    private EditText editTextTitle, editTextBio, editTextDescription, editTextLink, editTextStreet, editTextCity, editTextCountry, editTextPostcode;

    private ImageView eventImage;

    private Button confirm_button, cancel_button;
    private ImageButton image_button;

    private final Picasso picasso = Picasso.get();
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public CreateEventFragment() {
        // Required empty public constructor
    }

    public static CreateEventFragment newInstance(String param1, String param2) {
        CreateEventFragment fragment = new CreateEventFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

        confirm_button = view.findViewById(R.id.confirmButton);
        cancel_button = view.findViewById(R.id.cancelButton);
        image_button = view.findViewById(R.id.uploadImageButton);
        eventImage = view.findViewById(R.id.mainImage);

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
              editTextStreet = view.findViewById(R.id.editStreet);
              editTextCity = view.findViewById(R.id.editCity);
              editTextCountry = view.findViewById(R.id.editCountry);
              editTextPostcode = view.findViewById(R.id.editPostcode);

              if (eventImage.getDrawable() == null) {
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
              if (!Patterns.WEB_URL.matcher(editTextLink.getText().toString()).matches()) {
                  Toast.makeText(getActivity(), "Enter a valid link e.g 'www.google.com'", Toast.LENGTH_SHORT).show();
                  return;
              }
              if (TextUtils.isEmpty(editTextStreet.getText().toString().trim())) {
                  Toast.makeText(getActivity(), "Enter street", Toast.LENGTH_SHORT).show();
                  return;
              }
              if (TextUtils.isEmpty(editTextCity.getText().toString().trim())) {
                  Toast.makeText(getActivity(), "Enter city", Toast.LENGTH_SHORT).show();
                  return;
              }
                if (TextUtils.isEmpty(editTextCity.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Enter city", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextCountry.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Enter country", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(editTextPostcode.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "Enter postcode", Toast.LENGTH_SHORT).show();
                    return;
                }
              DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").push();
              eventID = eventRef.getKey();
              writeNewEvent(editTextTitle.getText().toString(), editTextBio.getText().toString(), editTextDescription.getText().toString(), editTextStreet.getText().toString(),
                      editTextCity.getText().toString(), editTextPostcode.getText().toString(), editTextCountry.getText().toString(), editTextLink.getText().toString(), "");

              DatabaseReference event = FirebaseDatabase.getInstance().getReference().child("events").child(eventID);

              event.addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if (snapshot.exists()) {
                          EventClass event = snapshot.getValue(EventClass.class);
                          pageTitle.setText("EVENT SEARCH");
                          replaceFragment(new SearchEventFragment(), R.id.container);
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
                        picasso.load(imageUri).into(eventImage);
                    }
                }
            });

    private void uploadToFirebase(Uri uri) {
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventID).child("image");

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        eventRef.setValue(uri.toString());
                    }
                });
            }
        });
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    public void writeNewEvent(String title, String bio, String description, String street, String city, String postcode, String country, String link, String image) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").push();
        eventID = eventRef.getKey();

        EventClass newEvent = new EventClass(title, bio, description, street, city, postcode, country, link, image);
        eventRef.setValue(newEvent);
        uploadToFirebase(imageUri);
        Toast.makeText(getActivity(), "New Event Created!", Toast.LENGTH_SHORT).show();
    }

    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}