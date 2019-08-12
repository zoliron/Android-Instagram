package zoli.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import zoli.instagram.Api.UserApi;
import zoli.instagram.Fragments.HomeFragment;
import zoli.instagram.Fragments.NotificationFragment;
import zoli.instagram.Fragments.ProfileFragment;
import zoli.instagram.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets the navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Sets the navigation bar item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else {

            // Setting the default fragment to Home Fragment at startup
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        }



    }

    // Listener for item selected on the navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.nav_add:
                    selectedFragment = null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    break;
                case R.id.nav_heart:
                    selectedFragment = new NotificationFragment();
                    break;
                case R.id.nav_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", UserApi.currentUser.getUid());
                    editor.apply();
                    selectedFragment = new ProfileFragment();
                    break;
            }

            if (selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        }
    };
}
