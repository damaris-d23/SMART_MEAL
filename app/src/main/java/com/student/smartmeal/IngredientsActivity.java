package com.student.smartmeal;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.student.smartmeal.model.UserPreferences;
import com.student.smartmeal.util.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class IngredientsActivity extends AppCompatActivity {

    private TextInputEditText etIngredient;
    private ChipGroup chipGroupIngredients;
    private final List<String> ingredientsList = new ArrayList<>();

    private final String[] SUGGESTIONS = {
            "ouă", "pâine", "paste", "orez", "cartofi",
            "lapte", "brânză", "roșii", "usturoi", "ceapă",
            "unt", "ulei", "ketchup", "iaurt", "banană"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        etIngredient = findViewById(R.id.etIngredient);
        chipGroupIngredients = findViewById(R.id.chipGroupIngredients);
        ChipGroup chipSugg = findViewById(R.id.chipGroupSuggestions);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pasul 2: Ingrediente");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        List<String> lastIngredients = new PreferencesManager(this).getLastIngredients();
        for (String ing : lastIngredients) {
            addIngredient(ing);
        }

        for (String s : SUGGESTIONS) {
            Chip chip = new Chip(this);
            chip.setText(s);
            chip.setCheckable(false);
            chip.setClickable(true);
            chip.setOnClickListener(v -> addIngredient(s));
            chipSugg.addView(chip);
        }

        findViewById(R.id.btnAddIngredient).setOnClickListener(v -> {
            String text = etIngredient.getText() != null
                    ? etIngredient.getText().toString().trim()
                    : "";

            if (!text.isEmpty()) {
                addIngredient(text);
                etIngredient.setText("");
            }
        });

        etIngredient.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String text = etIngredient.getText() != null
                        ? etIngredient.getText().toString().trim()
                        : "";

                if (!text.isEmpty()) {
                    addIngredient(text);
                    etIngredient.setText("");
                }

                return true;
            }

            return false;
        });

        findViewById(R.id.btnNextFromIngredients).setOnClickListener(v -> {
            UserPreferences prefs = getIntent().getParcelableExtra("preferences");
            if (prefs == null) {
                prefs = new UserPreferences();
            }

            prefs.setOwnedIngredients(new ArrayList<>(ingredientsList));
            new PreferencesManager(this).saveLastIngredients(ingredientsList);

            Intent intent = new Intent(this, MedicalActivity.class);
            intent.putExtra("preferences", prefs);
            startActivity(intent);
        });


    }

    private void addIngredient(String ingredient) {
        for (String existing : ingredientsList) {
            if (existing.equalsIgnoreCase(ingredient)) {
                return;
            }
        }

        ingredientsList.add(ingredient);

        Chip chip = new Chip(this);
        chip.setText(ingredient);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            ingredientsList.remove(ingredient);
            chipGroupIngredients.removeView(chip);
        });

        chipGroupIngredients.addView(chip);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}