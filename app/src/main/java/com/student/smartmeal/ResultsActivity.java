package com.student.smartmeal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.student.smartmeal.adapter.RecipeAdapter;
import com.student.smartmeal.model.Recipe;
import com.student.smartmeal.model.RecipeResult;
import com.student.smartmeal.model.UserPreferences;
import com.student.smartmeal.util.RecipeLoader;
import com.student.smartmeal.util.RecommendationEngine;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Ascunde toolbar — navigarea e prin Bottom Nav și butonul Back fizic
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        UserPreferences prefs = getIntent().getParcelableExtra("preferences");
        if (prefs == null) prefs = new UserPreferences();

        // Încarcă rețetele din JSON
        List<Recipe> allRecipes = RecipeLoader.loadRecipes(this);

        // Rulează algoritmul
        List<RecipeResult> results = RecommendationEngine.getRecommendations(prefs, allRecipes);

        TextView tvTitle    = findViewById(R.id.tvResultsTitle);
        TextView tvSubtitle = findViewById(R.id.tvResultSubtitle);
        TextView tvEmpty    = findViewById(R.id.tvEmpty);
        RecyclerView recycler = findViewById(R.id.recyclerRecipes);

        tvTitle.setText("Rețetele tale 🍽️");

        if (results.isEmpty()) {
            recycler.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvSubtitle.setText("Nicio rețetă disponibilă");
            tvEmpty.setText(
                    "Nu am găsit rețete compatibile cu restricțiile tale.\n\n" +
                            "Sugestii:\n" +
                            "• Încearcă să reduci alergiile selectate\n" +
                            "• Adaugă mai multe ingrediente\n" +
                            "• Mărește timpul disponibil"
            );
        } else {
            tvEmpty.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
            tvSubtitle.setText(results.size() + " rețete găsite pentru tine");

            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.setAdapter(new RecipeAdapter(results, result -> {
                // Click pe card → detalii
                Intent intent = buildDetailIntent(result);
                startActivity(intent);
            }, this));
        }

        setupBottomNav();
    }

    private Intent buildDetailIntent(RecipeResult result) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe_id",    result.getRecipe().getId());
        intent.putExtra("recipe_name",  result.getRecipe().getName());
        intent.putExtra("match_score",  result.getMatchScore());
        intent.putExtra("description",  result.getRecipe().getDescription());
        intent.putExtra("time",         result.getRecipe().getTimeMinutes());
        intent.putExtra("calories",     result.getRecipe().getCalories());
        intent.putExtra("difficulty",   result.getRecipe().getDifficultyString());
        intent.putExtra("ingredient_summary", result.getIngredientSummary());
        intent.putStringArrayListExtra("steps",
                new ArrayList<>(result.getRecipe().getSteps()));
        intent.putStringArrayListExtra("ingredients",
                new ArrayList<>(result.getRecipe().getIngredients()));
        intent.putStringArrayListExtra("missing",
                new ArrayList<>(result.getMissingIngredients()));
        intent.putStringArrayListExtra("owned",
                new ArrayList<>(result.getOwnedIngredients()));
        intent.putStringArrayListExtra("allergens",
                new ArrayList<>(result.getRecipe().getAllergens()));
        intent.putStringArrayListExtra("tags",
                new ArrayList<>(result.getRecipe().getTags()));
        intent.putStringArrayListExtra("reasons",
                new ArrayList<>(result.getReasons()));
        return intent;
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_results);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            if (id == R.id.nav_results) {
                return true; // suntem deja aici
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
}