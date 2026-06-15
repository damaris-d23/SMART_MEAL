package com.student.smartmeal.model;

import java.util.List;

public class RecipeResult {

    private Recipe recipe;
    private int matchScore;// 0–100
    private List<String> missingIngredients; // ingrediente care lipsesc
    private List<String> ownedIngredients; // ingrediente pe care userul le are
    private List<String> reasons;// motive text pentru recomandare
    private String ingredientSummary; // ex: "Ai 4 din 6 ingrediente"

    public RecipeResult(Recipe recipe, int matchScore,
                        List<String> missingIngredients,
                        List<String> ownedIngredients,
                        List<String> reasons,
                        String ingredientSummary) {
        this.recipe = recipe;
        this.matchScore = matchScore;
        this.missingIngredients = missingIngredients;
        this.ownedIngredients   = ownedIngredients;
        this.reasons            = reasons;
        this.ingredientSummary  = ingredientSummary;
    }

    public Recipe getRecipe(){ return recipe; }
    public int getMatchScore(){ return matchScore; }
    public List<String> getMissingIngredients(){ return missingIngredients; }
    public List<String> getOwnedIngredients(){ return ownedIngredients; }
    public List<String> getReasons(){ return reasons; }
    public String getIngredientSummary(){ return ingredientSummary; }

    // Compatibilitate cu codul vechi care folosea getMatchReason()
    public String getMatchReason() {
        if (reasons == null || reasons.isEmpty()) return "";
        return String.join(" • ", reasons);
    }
}