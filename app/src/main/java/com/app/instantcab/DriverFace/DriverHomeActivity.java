package com.app.instantcab.DriverFace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.app.instantcab.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DriverHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        bottomNav = (BottomNavigationView)findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_history:
                            selectedFragment = new DriverHistoryFragment();
                            break;

                        case R.id.nav_notification:
                            selectedFragment = new DriverNotificationFragment();
                            break;

                        case R.id.nav_ride:
                            selectedFragment = new DriverRideFragment();
                            break;

                        case R.id.nav_rating:
                            selectedFragment = new DriverRatingFragment();
                            break;

                        case R.id.nav_account:
                            selectedFragment = new DriverAccountFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,selectedFragment).commit();
                    return true;

                }
            };
}