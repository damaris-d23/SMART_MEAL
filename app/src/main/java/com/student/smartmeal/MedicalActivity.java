package com.student.smartmeal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.student.smartmeal.model.Recipe;
import com.student.smartmeal.model.UserPreferences;
import com.student.smartmeal.util.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class MedicalActivity extends AppCompatActivity {

    private UserPreferences prefsFromPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);

        setupToolbar();

        CheckBox cbLactose = findViewById(R.id.cbLactoseFree);
        CheckBox cbGluten = findViewById(R.id.cbGlutenFree);
        CheckBox cbNut = findViewById(R.id.cbNutFree);
        CheckBox cbEgg = findViewById(R.id.cbEggFree);
        CheckBox cbSeafood = findViewById(R.id.cbSeafoodFree);
        CheckBox cbDiabetic = findViewById(R.id.cbDiabetic);
        CheckBox cbLowSodium = findViewById(R.id.cbLowSodium);
        CheckBox cbEasyDigest = findViewById(R.id.cbEasyDigestion);
        CheckBox cbVegetarian = findViewById(R.id.cbVegetarian);
        CheckBox cbVegan = findViewById(R.id.cbVegan);
        CheckBox cbNoSpicy = findViewById(R.id.cbNoSpicy);
        CheckBox cbNoFried = findViewById(R.id.cbNoFried);
        CheckBox cbNoSugar = findViewById(R.id.cbNoSugar);
        CheckBox cbNoRedMeat = findViewById(R.id.cbNoRedMeat);
        CheckBox cbNoPork = findViewById(R.id.cbNoPork);
        CheckBox cbHighProtein = findViewById(R.id.cbHighProtein);
        CheckBox cbLowCalorie = findViewById(R.id.cbLowCalorie);

        TextInputEditText etAvoid = findViewById(R.id.etAvoidIngredients);

        prefsFromPrev = getIntent().getParcelableExtra("preferences");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnFindRecipes).setOnClickListener(v -> {
            UserPreferences prefs = prefsFromPrev != null
                    ? prefsFromPrev
                    : new UserPreferences();

            List<String> restrictions = new ArrayList<>();

            if (cbLactose.isChecked()) restrictions.add(Recipe.LACTOSE_FREE);
            if (cbGluten.isChecked()) restrictions.add(Recipe.GLUTEN_FREE);
            if (cbNut.isChecked()) restrictions.add(Recipe.NUT_FREE);
            if (cbEgg.isChecked()) restrictions.add(Recipe.EGG_FREE);
            if (cbSeafood.isChecked()) restrictions.add(Recipe.SEAFOOD_FREE);
            if (cbDiabetic.isChecked()) restrictions.add(Recipe.DIABETIC);
            if (cbLowSodium.isChecked()) restrictions.add(Recipe.LOW_SODIUM);
            if (cbEasyDigest.isChecked()) restrictions.add(Recipe.EASY_DIGESTION);
            if (cbVegetarian.isChecked()) restrictions.add(Recipe.VEGETARIAN);
            if (cbVegan.isChecked()) restrictions.add(Recipe.VEGAN);
            if (cbNoSpicy.isChecked()) restrictions.add(Recipe.NO_SPICY);
            if (cbNoFried.isChecked()) restrictions.add(Recipe.NO_FRIED);
            if (cbNoSugar.isChecked()) restrictions.add(Recipe.NO_SUGAR);
            if (cbNoRedMeat.isChecked()) restrictions.add(Recipe.NO_RED_MEAT);
            if (cbNoPork.isChecked()) restrictions.add(Recipe.NO_PORK);
            if (cbHighProtein.isChecked()) restrictions.add(Recipe.HIGH_PROTEIN);
            if (cbLowCalorie.isChecked()) restrictions.add(Recipe.LOW_CALORIE);

            prefs.setMedicalRestrictions(restrictions);

            String avoidText = etAvoid.getText() != null
                    ? etAvoid.getText().toString().trim()
                    : "";

            List<String> avoidList = new ArrayList<>();

            if (!avoidText.isEmpty()) {
                String[] parts = avoidText.split(",");

                for (String part : parts) {
                    String trimmed = part.trim();

                    if (!trimmed.isEmpty()) {
                        avoidList.add(trimmed);
                    }
                }
            }

            prefs.setAvoidIngredients(avoidList);

            PreferencesManager pm = new PreferencesManager(this);
            pm.saveLastRestrictions(restrictions);
            pm.saveLastAvoid(avoidList);

            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("preferences", prefs);
            startActivity(intent);
        });

        setupBottomNav();
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle("Restricții și preferințe");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_results);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }

            if (id == R.id.nav_results) {
                UserPreferences empty = new UserPreferences();
                empty.setAvailableTimeMinutes(999);
                empty.setMood(UserPreferences.Mood.NORMAL);
                empty.setCookingSkill(UserPreferences.CookingSkill.ADVANCED);

                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("preferences", empty);
                startActivity(intent);
                return true;
            }

            if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            }

            return false;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}