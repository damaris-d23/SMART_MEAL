package com.student.smartmeal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.student.smartmeal.R;
import com.student.smartmeal.model.RecipeResult;
import com.student.smartmeal.util.PreferencesManager;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeResult result);
    }

    private final List<RecipeResult> results;
    private final OnRecipeClickListener listener;
    private final PreferencesManager prefsManager;

    public RecipeAdapter(List<RecipeResult> results,
                         OnRecipeClickListener listener,
                         Context context) {
        this.results      = results;
        this.listener     = listener;
        this.prefsManager = new PreferencesManager(context);
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        RecipeResult result = results.get(position);
        Context ctx = h.itemView.getContext();

        // ── Nume ──
        h.tvName.setText(result.getRecipe().getName());

        // ── Scor cu culoare ──
        int score = result.getMatchScore();
        h.tvScore.setText(score + "%");
        if (score >= 70) h.tvScore.setTextColor(Color.parseColor("#2E7D32"));
        else if (score >= 45) h.tvScore.setTextColor(Color.parseColor("#E65100"));
        else h.tvScore.setTextColor(Color.parseColor("#B71C1C"));

        // Badge "Cea mai bună potrivire" pentru primele 3
        h.tvBestMatch.setVisibility(position < 3 && score >= 60
                ? View.VISIBLE : View.GONE);

        // ── Info rapide ──
        h.tvTime.setText("⏱ " + result.getRecipe().getTimeMinutes() + " min");
        h.tvCalories.setText("🔥 " + result.getRecipe().getCalories() + " cal");
        h.tvDifficulty.setText("⭐ " + result.getRecipe().getDifficultyString());

        // ── Rezumat ingrediente ──
        String summary = result.getIngredientSummary();
        if (summary != null && !summary.isEmpty()) {
            h.tvIngredientSummary.setVisibility(View.VISIBLE);
            h.tvIngredientSummary.setText(summary);
        } else {
            h.tvIngredientSummary.setVisibility(View.GONE);
        }

        // ── Ingrediente lipsă ──
        List<String> missing = result.getMissingIngredients();
        if (missing == null || missing.isEmpty()) {
            h.tvMissing.setText("✅ Ai toate ingredientele");
            h.tvMissing.setTextColor(Color.parseColor("#2E7D32"));
        } else if (missing.size() <= 2) {
            h.tvMissing.setText("⚠️ Lipsesc: " + String.join(", ", missing));
            h.tvMissing.setTextColor(Color.parseColor("#E65100"));
        } else {
            h.tvMissing.setText("⚠️ Lipsesc " + missing.size() + " ingrediente");
            h.tvMissing.setTextColor(Color.parseColor("#B71C1C"));
        }

        // ── Primul motiv ca text scurt ──
        List<String> reasons = result.getReasons();
        if (reasons != null && !reasons.isEmpty()) {
            h.tvFirstReason.setVisibility(View.VISIBLE);
            h.tvFirstReason.setText( reasons.get(0));
        } else {
            h.tvFirstReason.setVisibility(View.GONE);
        }

        // ── Chips etichete ──
        h.chipGroupTags.removeAllViews();
        List<String> tags = result.getRecipe().getTags();
        if (tags != null) {
            int shown = 0;
            for (String tag : tags) {
                if (shown >= 3) break; // max 3 chips pe card
                String label = tagToLabel(tag);
                if (label == null) continue;

                Chip chip = new Chip(ctx);
                chip.setText(label);
                chip.setTextSize(10f);
                chip.setChipMinHeight(26f);
                chip.setClickable(false);
                chip.setFocusable(false);
                chip.setChipBackgroundColorResource(tagToColor(tag));
                chip.setTextColor(Color.WHITE);
                h.chipGroupTags.addView(chip);
                shown++;
            }
        }

        // ── Buton favorite ──
        boolean isFav = prefsManager.isFavorite(result.getRecipe().getId());
        h.btnFavorite.setText(isFav ? "❤️" : "🤍");
        h.btnFavorite.setOnClickListener(v -> {
            String id = result.getRecipe().getId();
            if (prefsManager.isFavorite(id)) {
                prefsManager.removeFavorite(id);
                h.btnFavorite.setText("🤍");
            } else {
                prefsManager.addFavorite(id);
                h.btnFavorite.setText("❤️");
            }
        });

        // ── Click pe card ──
        h.itemView.setOnClickListener(v -> listener.onRecipeClick(result));
    }

    @Override
    public int getItemCount() { return results.size(); }

    private String tagToLabel(String tag) {
        switch (tag) {
            case "vegan":          return "Vegan";
            case "vegetarian":     return "Vegetarian";
            case "gluten_free":    return "Fără gluten";
            case "lactose_free":   return "Fără lactoză";
            case "high_protein":   return "Proteine ↑";
            case "low_calorie":    return "Calorii ↓";
            case "quick":          return "Rapid";
            case "easy_digestion": return "Ușor digerabil";
            default:               return null;
        }
    }

    private int tagToColor(String tag) {
        switch (tag) {
            case "vegan":
            case "vegetarian":     return R.color.tag_vegan;
            case "gluten_free":    return R.color.tag_gluten_free;
            case "quick":          return R.color.tag_quick;
            case "high_protein":   return R.color.tag_protein;
            case "low_calorie":    return R.color.tag_low_cal;
            default:               return R.color.text_secondary;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvScore, tvBestMatch, tvTime, tvCalories,
                tvDifficulty, tvMissing, tvIngredientSummary, tvFirstReason;
        ChipGroup chipGroupTags;
        TextView btnFavorite; // folosim TextView ca buton emoji simplu

        ViewHolder(View view) {
            super(view);
            tvName             = view.findViewById(R.id.tvRecipeName);
            tvScore            = view.findViewById(R.id.tvMatchScore);
            tvBestMatch        = view.findViewById(R.id.tvBestMatch);
            tvTime             = view.findViewById(R.id.tvTime);
            tvCalories         = view.findViewById(R.id.tvCalories);
            tvDifficulty       = view.findViewById(R.id.tvDifficulty);
            tvMissing          = view.findViewById(R.id.tvMissing);
            tvIngredientSummary= view.findViewById(R.id.tvIngredientSummary);
            tvFirstReason      = view.findViewById(R.id.tvFirstReason);
            chipGroupTags      = view.findViewById(R.id.chipGroupTags);
            btnFavorite        = view.findViewById(R.id.btnFavorite);
        }
    }
}