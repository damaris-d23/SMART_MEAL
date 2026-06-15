package com.student.smartmeal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.student.smartmeal.util.PreferencesManager;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private PreferencesManager pm;
    private String recipeId;
    private TextView btnFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        pm = new PreferencesManager(this);


        // Toolbar cu săgeată Back ──
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalii rețetă");
        }

        // Extrage datele din Intent ──
        recipeId          = getIntent().getStringExtra("recipe_id");
        String name       = getIntent().getStringExtra("recipe_name");

        setTextSafe(R.id.tvDetailTitle, name);
        int score         = getIntent().getIntExtra("match_score", 0);
        String description= getIntent().getStringExtra("description");
        int time          = getIntent().getIntExtra("time", 0);
        int calories      = getIntent().getIntExtra("calories", 0);
        String difficulty = getIntent().getStringExtra("difficulty");
        String ingSum     = getIntent().getStringExtra("ingredient_summary");

        ArrayList<String> steps       = getIntent().getStringArrayListExtra("steps");
        ArrayList<String> ingredients = getIntent().getStringArrayListExtra("ingredients");
        ArrayList<String> missing     = getIntent().getStringArrayListExtra("missing");
        ArrayList<String> owned       = getIntent().getStringArrayListExtra("owned");
        ArrayList<String> allergens   = getIntent().getStringArrayListExtra("allergens");
        ArrayList<String> tags        = getIntent().getStringArrayListExtra("tags");
        ArrayList<String> reasons     = getIntent().getStringArrayListExtra("reasons");

        // ── Completează UI ──
        if (getSupportActionBar() != null && name != null) {
            getSupportActionBar().setTitle(name);
        }

        // Scor (ascunde dacă e 0 — vine din Favorites)
        TextView tvScore = findViewById(R.id.tvDetailScore);
        if (score > 0) {
            tvScore.setVisibility(View.VISIBLE);
            tvScore.setText(score + "% potrivire");
            if (score >= 70)      tvScore.setTextColor(Color.parseColor("#2E7D32"));
            else if (score >= 45) tvScore.setTextColor(Color.parseColor("#E65100"));
            else                  tvScore.setTextColor(Color.parseColor("#B71C1C"));
        } else {
            tvScore.setVisibility(View.GONE);
        }

        setTextSafe(R.id.tvDetailDescription, description);
        setTextSafe(R.id.tvDetailTime,     "⏱\n" + time + " min");
        setTextSafe(R.id.tvDetailCalories, "🔥\n" + calories + " kcal");
        setTextSafe(R.id.tvDetailDifficulty, "⭐\n" + (difficulty != null ? difficulty : "-"));

        // Rezumat ingrediente
        TextView tvIngSum = findViewById(R.id.tvDetailIngredientSummary);
        if (ingSum != null && !ingSum.isEmpty()) {
            tvIngSum.setVisibility(View.VISIBLE);
            tvIngSum.setText(ingSum);
        } else {
            tvIngSum.setVisibility(View.GONE);
        }

        // Ingrediente disponibile (verzi)
        TextView tvOwned = findViewById(R.id.tvDetailOwned);
        if (owned != null && !owned.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String ing : owned) sb.append(" - ").append(ing).append("\n");
            tvOwned.setText(sb.toString().trim());
            tvOwned.setVisibility(View.VISIBLE);
        } else {
            tvOwned.setVisibility(View.GONE);
        }

        // Ingrediente lipsă
        TextView tvMissingLabel = findViewById(R.id.tvDetailMissingLabel);
        TextView tvMissing      = findViewById(R.id.tvDetailMissing);
        if (missing == null || missing.isEmpty()) {
            tvMissingLabel.setVisibility(View.GONE);
            tvMissing.setVisibility(View.GONE);
        } else {
            tvMissingLabel.setVisibility(View.VISIBLE);
            tvMissing.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (String ing : missing) sb.append(" - ").append(ing).append("\n");
            tvMissing.setText(sb.toString().trim());
        }

        // Alergeni
        TextView tvAllergens = findViewById(R.id.tvDetailAllergens);
        if (allergens == null || allergens.isEmpty()) {
            tvAllergens.setVisibility(View.GONE);
        } else {
            tvAllergens.setVisibility(View.VISIBLE);
            tvAllergens.setText("⚠️ Conține: " + String.join(", ", allergens));
        }

        // Ingrediente necesare (lista completă)
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        if (ingredients != null && !ingredients.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String ing : ingredients) sb.append("• ").append(ing).append("\n");
            tvIngredients.setText(sb.toString().trim());
        }

        // Pași de preparare
        TextView tvSteps = findViewById(R.id.tvDetailSteps);
        if (steps != null && !steps.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < steps.size(); i++) {
                sb.append(i + 1).append(". ").append(steps.get(i)).append("\n\n");
            }
            tvSteps.setText(sb.toString().trim());
        }

        // Motive recomandare
        TextView tvReasonsLabel   = findViewById(R.id.tvReasonsLabel);
        LinearLayout layoutReasons = findViewById(R.id.layoutReasons);
        if (reasons != null && !reasons.isEmpty()) {
            tvReasonsLabel.setVisibility(View.VISIBLE);
            layoutReasons.setVisibility(View.VISIBLE);
            layoutReasons.removeAllViews();
            for (String reason : reasons) {
                TextView tv = new TextView(this);
                tv.setText("• " + reason);
                tv.setTextSize(13f);
                tv.setTextColor(Color.parseColor("#2E7D32"));
                tv.setPadding(0, 6, 0, 6);
                layoutReasons.addView(tv);
            }
        } else {
            tvReasonsLabel.setVisibility(View.GONE);
            layoutReasons.setVisibility(View.GONE);
        }

        // ── Buton Favorite ──
        btnFav = findViewById(R.id.btnDetailFavorite);
        updateFavoriteButton();

        btnFav.setOnClickListener(v -> {
            if (recipeId != null) {
                if (pm.isFavorite(recipeId)) {
                    pm.removeFavorite(recipeId);
                } else {
                    pm.addFavorite(recipeId);
                }
                updateFavoriteButton();
            }
        });
    }

    /**
     * Actualizează textul și culoarea butonului favorite.
     */
    private void updateFavoriteButton() {
        if (recipeId == null || btnFav == null) return;

        if (pm.isFavorite(recipeId)) {
            btnFav.setText("❤️ Salvat la favorite");
            btnFav.setTextColor(Color.parseColor("#B71C1C"));
        } else {
            btnFav.setText("🤍 Adaugă la favorite");
            btnFav.setTextColor(Color.parseColor("#555555"));
        }
    }

    private void setTextSafe(int viewId, String text) {
        TextView tv = findViewById(viewId);
        if (tv != null && text != null) tv.setText(text);
    }

    // Butonul de back din toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}