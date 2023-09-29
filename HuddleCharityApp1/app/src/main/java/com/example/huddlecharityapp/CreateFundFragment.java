package com.example.huddlecharityapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFundFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button confirm_button, cancel_button;
    private ImageButton image_button;
    private EditText editTextTitle, editTextDescription, editTextBio, editTextTarget, editTextEndDate;
    private ImageView fundPicture;
    private String endDateString, currentDateString;
    private Uri imageUri;
    private Date endDate, currentDate;
    private long valueTarget;
    private String fundraiserID;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    private Picasso picasso = Picasso.get();
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private String userID;

    private String fundCheck;

    public CreateFundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateFundFragment newInstance(String param1, String param2) {
        CreateFundFragment fragment = new CreateFundFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_fund, container, false);

        TextView pageTitle = getActivity().findViewById(R.id.pageTitle);

        confirm_button = view.findViewById(R.id.confirmButton);
        cancel_button = view.findViewById(R.id.cancelButton);
        image_button = view.findViewById(R.id.uploadImageButton);
        editTextEndDate = view.findViewById(R.id.editEndDate);
        fundPicture = view.findViewById(R.id.mainImage);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        currentDateString = dateFormat.format(Calendar.getInstance().getTime());

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Set the selected date in the EditText
                        endDateString = String.format("%04d-%02d-%02d", selectedYear, (selectedMonth + 1), selectedDay);
                        editTextEndDate.setText(endDateString);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


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
              pageTitle.setText("NEWS");
              replaceFragment(new HomeFragment(), R.id.container);
          }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextTitle = view.findViewById(R.id.editTitle);
                editTextDescription = view.findViewById(R.id.editDescription);
                editTextBio = view.findViewById(R.id.editBio);
                editTextTarget = view.findViewById(R.id.editTarget);
                String textTarget = editTextTarget.getText().toString().trim();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                userID = currentUser.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);

                userRef.child("fundraiserID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String fundCheck = dataSnapshot.getValue(String.class);

                        if (fundCheck != null && !fundCheck.equals("null")) {
                            Toast.makeText(getActivity(), "You already have a fundraiser active", Toast.LENGTH_SHORT).show();
                            pageTitle.setText("YOUR PROFILE");
                            replaceFragment(new UserProfileFragment(), R.id.container);
                            return;
                        }

                        if (fundPicture.getDrawable() == null) {
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
                        if (TextUtils.isEmpty(editTextTarget.getText().toString().trim())) {
                            Toast.makeText(getActivity(), "Enter target", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            valueTarget = Long.parseLong(textTarget);
                            if (valueTarget > 100 && valueTarget < 100000) {
                                Toast.makeText(getActivity(), "Max target is 100,000, Min target is 100", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (TextUtils.isEmpty(editTextEndDate.getText().toString().trim())) {
                            Toast.makeText(getActivity(), "Enter end date", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            endDate = dateFormat.parse(endDateString);
                            currentDate = dateFormat.parse(currentDateString);

                            if (endDate.before(currentDate)) {
                                Toast.makeText(getActivity(), "End date cannot be before current date", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        DatabaseReference fundraiserRef = FirebaseDatabase.getInstance().getReference("fundraisers").push();
                        fundraiserID = fundraiserRef.getKey();
                        writeNewFundraiser("",editTextTitle.getText().toString(),editTextDescription.getText().toString(), editTextBio.getText().toString(), editTextTarget.getText().toString(),0, currentDateString, endDateString, userID);
                        DatabaseReference fund = FirebaseDatabase.getInstance().getReference().child("fundraisers").child(fundraiserID);

                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        DatabaseReference currentUserRef = usersRef.child(user.getUid());

                        fund.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    FundraiserClass fund = snapshot.getValue(FundraiserClass.class);
                                    pageTitle.setText("FUND SEARCH");
                                    replaceFragment(new SearchFundFragment(), R.id.container);
                                    currentUserRef.child("fundraiserID").setValue(fundraiserID);

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
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
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
                        picasso.load(imageUri).into(fundPicture);
                    }
                }
            });

    private void uploadToFirebase(Uri uri){
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        DatabaseReference fundRef = FirebaseDatabase.getInstance().getReference().child("fundraisers").child(fundraiserID).child("image");

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

    public void writeNewFundraiser(String image, String title, String description, String bio, String target, int current, String startDate, String endDate, String userId) {
        DatabaseReference fundraiserRef = FirebaseDatabase.getInstance().getReference("fundraisers").push();
        fundraiserID = fundraiserRef.getKey();

        FundraiserClass newFundraiser = new FundraiserClass(image,title,description,bio,target,current,startDate,endDate,userId);
        fundraiserRef.setValue(newFundraiser);
        uploadToFirebase(imageUri);
        Toast.makeText(getActivity(), "New Fundraiser Created!", Toast.LENGTH_SHORT).show();
    }



    private void replaceFragment(Fragment fragment, int containerId) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }
}