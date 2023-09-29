package com.example.huddlecharityapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.example.huddlecharityapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    ActivityMainBinding binding;
    View mainToolbar;
    View headerBar;
    ImageButton burgerIcon;
    ImageButton backIcon;
    TextView pageTitle;

    Button anchor;

    private MutableLiveData<String> adminCheckLiveData = new MutableLiveData<>();

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // FOOTER MENU (MAIN NAVIGATION)
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mainToolbar = findViewById(R.id.main_toolbar);
        headerBar = findViewById(R.id.header_bar);
        anchor = findViewById(R.id.anchor);

        backIcon = findViewById(R.id.back_icon);
        burgerIcon = findViewById(R.id.burger_icon);
        pageTitle = findViewById(R.id.pageTitle);

        backIcon.setVisibility(View.GONE);

        // IF USER IN LOGGED RETURN HOME PAGE, ELSE RETURN WELCOME PAGE
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            pageTitle.setText("NEWS");
            replaceFragment(new HomeFragment(), R.id.container);
        } else {
            setViewVisibility(false);
            burgerIcon.setVisibility(View.GONE);
            pageTitle.setText("WELCOME");
            replaceFragment(new WelcomeFragment(), R.id.container);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_icon:
                    backIcon.setVisibility(View.GONE);
                    pageTitle.setText("NEWS");
                    replaceFragment(new HomeFragment(), R.id.container);
                    break;
                case R.id.create_icon:
                    backIcon.setVisibility(View.VISIBLE);
                    pageTitle.setText("CREATE");
                    replaceFragment(new CreateFundFragment(), R.id.container);
                    break;
                case R.id.inbox_icon:
                    backIcon.setVisibility(View.VISIBLE);
                    pageTitle.setText("INBOX");
                    replaceFragment(new InboxFragment(), R.id.container);
                    break;
                case R.id.portfolio_icon:
                    backIcon.setVisibility(View.VISIBLE);
                    pageTitle.setText("PORTFOLIO");
                    replaceFragment(new PortfolioFragment(), R.id.container);
                    break;
            }
            return true;
        });

        // HEADER MENU (TOOLBAR)
        ImageButton profileIcon = findViewById(R.id.profile_icon);
        ImageButton searchIcon = findViewById(R.id.search_icon);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new UserProfileFragment(), R.id.container);
                pageTitle.setText("YOUR PROFILE");
                backIcon.setVisibility(View.VISIBLE);
            }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new MainSearchFragment(), R.id.container);
                pageTitle.setText("SEARCH");
                backIcon.setVisibility(View.VISIBLE);
            }
        });
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                updateUIForFragment();
            }
        });
        burgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupMenu();
            }
        });
    }

    public void updateUIForFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);

        String pageTitleText = "";

        int backIconVisibility = View.VISIBLE;
        int burgerIconVisibility = View.VISIBLE;

        //NavigationView navigationView = findViewById(R.id.navigation_view);
        //Menu menu = navigationView.getMenu();
        //MenuItem homeItem = menu.findItem(R.id.home_icon);

        if (currentFragment instanceof HomeFragment) {
            pageTitleText = "NEWS";
            backIconVisibility = View.GONE;
        } else if (currentFragment instanceof CreateFundFragment) {
            pageTitleText = "CREATE FUND";
        } else if (currentFragment instanceof InboxFragment) {
            pageTitleText = "INBOX";
        } else if (currentFragment instanceof PortfolioFragment) {
            pageTitleText = "PORTFOLIO";
        } else if (currentFragment instanceof MainSearchFragment) {
            pageTitleText = "SEARCH";
        } else if (currentFragment instanceof UserProfileFragment) {
            pageTitleText = "YOUR PROFILE";
        } else if (currentFragment instanceof SearchFundFragment) {
            pageTitleText = "SEARCH FUNDS";
        } else if (currentFragment instanceof SearchCharityFragment) {
            pageTitleText = "SEARCH CHARITIES";
        } else if (currentFragment instanceof SearchEventFragment) {
            pageTitleText = "SEARCH EVENTS";
        } else if (currentFragment instanceof SingleFundFragment) {
            pageTitleText = "FUNDRAISER";
        } else if (currentFragment instanceof CreateCharityFragment) {
            pageTitleText = "CREATE CHARITY";
        } else if (currentFragment instanceof CreateEventFragment) {
            pageTitleText = "CREATE EVENT";
        } else if (currentFragment instanceof UpdateProfileFragment) {
            pageTitleText = "UPDATE PROFILE";
        } else if (currentFragment instanceof SingleCharityFragment) {
            pageTitleText = "CHARITY";
        } else if (currentFragment instanceof SingleEventFragment) {
            pageTitleText = "EVENT";
        } else if (currentFragment instanceof CreateNewsFragment) {
            pageTitleText = "CREATE NEWS";
        }

        pageTitle.setText(pageTitleText);
        backIcon.setVisibility(backIconVisibility);
        burgerIcon.setVisibility(burgerIconVisibility);
    }

    private void createPopupMenu() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("admin");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String adminCheck = "";
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        adminCheck = snapshot.getValue(String.class);
                        adminCheckLiveData.setValue(adminCheck);
                        PopupMenu popup = new PopupMenu(MainActivity.this, anchor, Gravity.CENTER);
                        if (adminCheckLiveData.getValue() != null && adminCheckLiveData.getValue().equals("TRUE")) {
                            popup.getMenuInflater().inflate(R.menu.admin_burger_menu, popup.getMenu());
                        } else {
                            popup.getMenuInflater().inflate(R.menu.normal_burger_menu, popup.getMenu());
                        }
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @SuppressLint("NonConstantResourceId")
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.createNews:
                                        pageTitle.setText("CREATE NEWS");
                                        backIcon.setVisibility(View.VISIBLE);
                                        replaceFragment(new CreateNewsFragment(), R.id.container);
                                        break;
                                    case R.id.createCharity:
                                        pageTitle.setText("CREATE CHARITY");
                                        backIcon.setVisibility(View.VISIBLE);
                                        replaceFragment(new CreateCharityFragment(), R.id.container);
                                        break;
                                    case R.id.createEvent:
                                        pageTitle.setText("CREATE EVENT");
                                        backIcon.setVisibility(View.VISIBLE);
                                        replaceFragment(new CreateEventFragment(), R.id.container);
                                        break;
                                    case R.id.logout:
                                        logout();
                                        break;
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });
                        popup.show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error getting data", databaseError.toException());
                }
            });
        }
    }

    private void logout(){
        pageTitle.setText("");
        burgerIcon.setVisibility(View.GONE);
        backIcon.setVisibility(View.GONE);
        setViewVisibility(false);
        FirebaseAuth.getInstance().signOut();
        replaceFragment(new WelcomeFragment(), R.id.container);
    }

    private void setViewVisibility(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mainToolbar.setVisibility(visibility);
        binding.bottomNavigationView.setVisibility(visibility);
    }

    // CHANGE FRAGMENT PAGE
    private void replaceFragment(Fragment fragment, int containerId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}