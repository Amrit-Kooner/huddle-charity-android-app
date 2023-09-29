package com.example.huddlecharityapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button backIcon;

    private AppCompatActivity activity;

    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView userProfileView;

    private Uri imageUri;

    Picasso picasso = Picasso.get();

    Boolean updated_image = false;

    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    final private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    final private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfileFragment newInstance(String param1, String param2) {
        UpdateProfileFragment fragment = new UpdateProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);


        picasso = Picasso.get();

        CircleImageView profilePicture = view.findViewById(R.id.profile_picture);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass user = dataSnapshot.getValue(UserClass.class);
                assert user != null;
                Uri profilePictureUri = Uri.parse(user.getProfile_picture());

                EditText editFirstName = view.findViewById(R.id.editFirstName);
                EditText editLastName = view.findViewById(R.id.editLastName);

                printInfo(user.getFirst_name(), editFirstName);
                printInfo(user.getLast_name(), editLastName);

                picasso.load(profilePictureUri).into(profilePicture);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error reading user data", databaseError.toException());
            }
        });

        userProfileView = view.findViewById(R.id.profile_picture);
        ImageButton uploadImage = view.findViewById(R.id.upload_image_button);


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        Button confirm_button = view.findViewById(R.id.confirmButton);
        Button cancel_button = view.findViewById(R.id.cancelButton);

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etOldPass = view.findViewById(R.id.editOldPassword);
                EditText etNewPass = view.findViewById(R.id.editNewPassword);
                EditText etCfmNewPass = view.findViewById(R.id.editConfirmNewPassword);
                EditText etFirst = view.findViewById(R.id.editFirstName);
                EditText etLast = view.findViewById(R.id.editLastName);

                if ((!TextUtils.isEmpty(etNewPass.getText().toString()) || !TextUtils.isEmpty(etCfmNewPass.getText().toString())) && TextUtils.isEmpty(etOldPass.getText().toString())) {
                    Toast.makeText(getActivity(), "Enter old password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(etOldPass.getText().toString())) {
                    if (TextUtils.isEmpty(etNewPass.getText().toString())) {
                        Toast.makeText(getActivity(), "Enter a new password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(etCfmNewPass.getText().toString())) {
                        Toast.makeText(getActivity(), "Confirm your new password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (etNewPass.length() < 6) {
                        Toast.makeText(getActivity(), "New password must be over 5 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!etNewPass.getText().toString().equals(etCfmNewPass.getText().toString())) {
                        Toast.makeText(getActivity(), "New password and confirmation password do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String userEmail = currentUser.getEmail();
                    String password = etOldPass.getText().toString();
                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, password);

                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                currentUser.updatePassword(etNewPass.getText().toString());
                                Toast.makeText(getActivity(), "Updated password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Old password is not correct", Toast.LENGTH_SHORT).show();
                                replaceFragment(new UpdateProfileFragment(), R.id.container);
                            }
                        }
                    });
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            UserClass currentUser = snapshot.getValue(UserClass.class);
                            // Use the currentUser object here
                            assert currentUser != null;
                            if (!currentUser.getFirst_name().equals(etFirst.getText().toString())) {
                                currentUser.setFirst_name(etFirst.getText().toString());
                                Toast.makeText(getActivity(), "Updated First Name", Toast.LENGTH_SHORT).show();
                            }
                            if (!currentUser.getLast_name().equals(etLast.getText().toString())) {
                                currentUser.setLast_name(etLast.getText().toString());
                                Toast.makeText(getActivity(), "Updated Last Name", Toast.LENGTH_SHORT).show();
                            }
                            if(imageUri != null){
                                uploadToFirebase(imageUri);
                                Toast.makeText(getActivity(), "Updated Profile Picture", Toast.LENGTH_SHORT).show();
                            }
                            reference.setValue(currentUser); //
                        }
                        replaceFragment(new UserProfileFragment(), R.id.container);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        picasso.load(imageUri).into(userProfileView);
                    }
                }
            });

    private void uploadToFirebase(Uri uri){
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("profile_picture");

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userRef.setValue(uri.toString());
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

    public void printInfo(String detail, EditText eText) {
        eText.setText(detail);
    }
    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}
