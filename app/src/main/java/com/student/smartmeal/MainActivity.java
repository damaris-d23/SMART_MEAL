package com.student.smartmeal;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.student.smartmeal.model.UserPreferences;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ascunde toolbar-ul pe Home (nu are back button)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Buton "Găsește rețete" → pornește flow-ul
        findViewById(R.id.btnStart).setOnClickListener(v ->
                startActivity(new Intent(this, PreferencesActivity.class))
        );

        // Buton "Toate rețetele" → Results fără filtre
        findViewById(R.id.btnAllRecipes).setOnClickListener(v -> {
            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("preferences", buildEmptyPreferences());
            startActivity(intent);
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);

        // Setăm tab-ul activ: Home
        nav.setSelectedItemId(R.id.nav_home);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Suntem deja pe Home, nu facem nimic
                return true;
            }

            if (id == R.id.nav_results) {
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("preferences", buildEmptyPreferences());
                // FLAG_ACTIVITY_REORDER_TO_FRONT evită stack-uri duplicate
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }

            if (id == R.id.nav_favorites) {
                Intent intent = new Intent(this, FavoritesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    /**
     * Preferințe goale pentru "Toate rețetele" — fără filtre.
     */
    private UserPreferences buildEmptyPreferences() {
        UserPreferences p = new UserPreferences();
        p.setAvailableTimeMinutes(999);
        p.setMood(UserPreferences.Mood.ENERGETIC);
        p.setCookingSkill(UserPreferences.CookingSkill.ADVANCED);
        return p;
    }
}