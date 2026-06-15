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
import com.student.smartmeal.util.PreferencesManager;
import com.student.smartmeal.util.RecipeLoader;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Ascunde toolbar — navigarea e prin Bottom Nav
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loadFavorites();
        setupBottomNav();
    }

    /**
     * Se apelează și la onCreate și la onResume (când revii din Details
     * după ce ai adăugat/eliminat un favorit).
     */
    private void loadFavorites() {
        PreferencesManager pm = new PreferencesManager(this);
        List<String> favoriteIds = pm.getFavorites();

        RecyclerView recycler = findViewById(R.id.recyclerFavorites);
        // În loc de TextView tvEmpty:
        View tvEmpty = findViewById(R.id.tvFavoritesEmpty);

        if (favoriteIds.isEmpty()) {
            // Ecran gol
            recycler.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        // Încarcă toate rețetele și filtrează doar favoritele
        List<Recipe> allRecipes = RecipeLoader.loadRecipes(this);
        List<RecipeResult> favResults = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (favoriteIds.contains(recipe.getId())) {
                // Creăm un RecipeResult simplu (fără scor, fără lipsă)
                favResults.add(new RecipeResult(
                        recipe,
                        0,                      // scor — nu relevan în Favorites
                        new ArrayList<>(),      // nicio lipsă
                        new ArrayList<>(),      // niciun owned
                        new ArrayList<>(),      // niciun reason
                        ""                      // niciun ingredient summary
                ));
            }
        }

        if (favResults.isEmpty()) {
            recycler.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new RecipeAdapter(favResults, result -> {
            // Click pe rețetă favorită → detalii
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id",   result.getRecipe().getId());
            intent.putExtra("recipe_name", result.getRecipe().getName());
            intent.putExtra("match_score", 0);
            intent.putExtra("description", result.getRecipe().getDescription());
            intent.putExtra("time",        result.getRecipe().getTimeMinutes());
            intent.putExtra("calories",    result.getRecipe().getCalories());
            intent.putExtra("difficulty",  result.getRecipe().getDifficultyString());
            intent.putExtra("ingredient_summary", "");
            intent.putStringArrayListExtra("steps",
                    new ArrayList<>(result.getRecipe().getSteps()));
            intent.putStringArrayListExtra("ingredients",
                    new ArrayList<>(result.getRecipe().getIngredients()));
            intent.putStringArrayListExtra("missing",   new ArrayList<>());
            intent.putStringArrayListExtra("owned",     new ArrayList<>());
            intent.putStringArrayListExtra("allergens",
                    new ArrayList<>(result.getRecipe().getAllergens()));
            intent.putStringArrayListExtra("tags",
                    new ArrayList<>(result.getRecipe().getTags()));
            intent.putStringArrayListExtra("reasons",   new ArrayList<>());
            startActivity(intent);
        }, this));
    }

    /**
     * onResume: reîncarcă lista după ce revii din RecipeDetailActivity
     * (unde ai putut adăuga/elimina un favorit).
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_favorites);

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
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("preferences", buildEmptyPreferences());
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            if (id == R.id.nav_favorites) {
                return true; // suntem deja aici
            }
            return false;
        });
    }

    private com.student.smartmeal.model.UserPreferences buildEmptyPreferences() {
        com.student.smartmeal.model.UserPreferences p =
                new com.student.smartmeal.model.UserPreferences();
        p.setAvailableTimeMinutes(999);
        p.setMood(com.student.smartmeal.model.UserPreferences.Mood.ENERGETIC);
        p.setCookingSkill(com.student.smartmeal.model.UserPreferences.CookingSkill.ADVANCED);
        return p;
    }
}