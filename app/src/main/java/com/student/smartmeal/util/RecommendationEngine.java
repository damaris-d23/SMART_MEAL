// Fișier: java/com/student/smartmeal/util/RecommendationEngine.java
package com.student.smartmeal.util;

import com.student.smartmeal.model.Recipe;
import com.student.smartmeal.model.RecipeResult;
import com.student.smartmeal.model.UserPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Motor de recomandare SmartMeal.
 *
 * FILOZOFIE:
 * - Excludere completă DOAR pentru alergii reale (lapte, gluten, pește etc.)
 * - Preferințele (vegetarian, high_protein, low_calorie) modifică SCORUL, nu exclud
 * - Ingredientele lipsă reduc scorul, dar nu exclud rețeta
 * - "No results" apare FOARTE rar — doar dacă toate rețetele au alergeni
 *
 * FORMULA SCOR (max 100):
 *   +40  ingrediente disponibile (proporțional)
 *   +20  timp disponibil
 *   +20  mood + dificultate
 *   +10  cooking skill
 *   +10  bonus preferințe (high_protein, low_calorie etc.)
 *   -10  penalizare dacă lipsesc multe ingrediente (>50%)
 */
public class RecommendationEngine {

    public static List<RecipeResult> getRecommendations(
            UserPreferences prefs, List<Recipe> allRecipes) {

        List<RecipeResult> results = new ArrayList<>();

        for (Recipe recipe : allRecipes) {

            // ── PASUL 1: Excludere completă DOAR pentru alergii reale ──
            if (AllergyFilter.shouldExclude(recipe, prefs)) continue;

            // ── PASUL 2: Excludere pentru ingrediente explicit evitate ──
            if (AllergyFilter.containsAvoidedIngredient(recipe, prefs)) continue;

            // ── PASUL 3: Calculează scorul ──
            ScoreBreakdown breakdown = calculateScore(recipe, prefs);

            // ── PASUL 4: Ingrediente lipsă ──
            List<String> missing  = getMissingIngredients(recipe, prefs);
            List<String> owned    = getOwnedIngredients(recipe, prefs);

            // ── PASUL 5: Motive text ──
            List<String> reasons  = buildReasons(recipe, prefs, missing, breakdown);

            // ── PASUL 6: Text "X/Y ingrediente disponibile" ──
            String ingredientSummary = buildIngredientSummary(recipe, prefs);

            results.add(new RecipeResult(
                    recipe,
                    breakdown.total(),
                    missing,
                    owned,
                    reasons,
                    ingredientSummary
            ));
        }

        // Sortare descrescătoare după scor
        Collections.sort(results, (a, b) -> b.getMatchScore() - a.getMatchScore());

        return results;
    }

    // ══════════════════════════════════════════════════════════════
    // CALCULUL SCORULUI
    // ══════════════════════════════════════════════════════════════

    private static ScoreBreakdown calculateScore(Recipe recipe, UserPreferences prefs) {
        ScoreBreakdown s = new ScoreBreakdown();

        // ── CRITERIU 1: Ingrediente disponibile (0–40 pts) ──
        List<String> needed = recipe.getIngredients();
        List<String> owned  = prefs.getOwnedIngredients();
        int matches = countMatches(owned, needed);

        if (!needed.isEmpty()) {
            // Scor proporțional: dacă ai 3/5 ingrediente → (3/5) × 40 = 24 pts
            s.ingredientScore = (int) (40.0 * matches / needed.size());

            // Penalizare ușoară dacă lipsesc mai mult de jumătate
            double missingRatio = 1.0 - (double) matches / needed.size();
            if (missingRatio > 0.5) {
                s.missingPenalty = -10;
            }
        } else {
            s.ingredientScore = 40; // rețetă fără ingrediente specifice → punctaj maxim
        }

        // ── CRITERIU 2: Timp disponibil (0–20 pts) ──
        int availTime  = prefs.getAvailableTimeMinutes();
        int recipeTime = recipe.getTimeMinutes();

        if (recipeTime <= availTime) {
            s.timeScore = 20;
        } else if (recipeTime <= availTime + 10) {
            s.timeScore = 12; // depășește cu cel mult 10 minute
        } else if (recipeTime <= availTime + 20) {
            s.timeScore = 5;  // depășește cu cel mult 20 minute
        }
        // peste 20 minute în plus → 0 pts

        // ── CRITERIU 3: Mood + Dificultate (0–20 pts) ──
        UserPreferences.Mood mood = prefs.getMood();
        Recipe.Difficulty diff    = recipe.getDifficulty();

        if (mood == UserPreferences.Mood.TIRED) {
            if (diff == Recipe.Difficulty.EASY && recipeTime <= 15) s.moodScore = 20;
            else if (diff == Recipe.Difficulty.EASY)                s.moodScore = 15;
            else if (diff == Recipe.Difficulty.MEDIUM)              s.moodScore = 7;
            // HARD + tired → 0 pts

        } else if (mood == UserPreferences.Mood.NORMAL) {
            if (diff == Recipe.Difficulty.EASY)        s.moodScore = 20;
            else if (diff == Recipe.Difficulty.MEDIUM) s.moodScore = 15;
            else                                       s.moodScore = 8;

        } else { // ENERGETIC
            s.moodScore = 20; // orice dificultate e bună
        }

        // ── CRITERIU 4: Cooking Skill (0–10 pts) ──
        UserPreferences.CookingSkill skill = prefs.getCookingSkill();

        if (skill == UserPreferences.CookingSkill.BEGINNER) {
            if (diff == Recipe.Difficulty.EASY)        s.skillScore = 10;
            else if (diff == Recipe.Difficulty.MEDIUM) s.skillScore = 4;
            // HARD + beginner → 0

        } else if (skill == UserPreferences.CookingSkill.MEDIUM) {
            if (diff == Recipe.Difficulty.HARD)        s.skillScore = 7;
            else                                       s.skillScore = 10;

        } else { // ADVANCED
            s.skillScore = 10;
        }

        // ── CRITERIU 5: Bonus preferințe (0–10 pts, cumulativ) ──
        // Preferințele adaugă puncte, NU exclud rețete
        List<String> restrictions = prefs.getMedicalRestrictions();
        if (restrictions != null) {

            if (restrictions.contains("high_protein") && recipe.hasTag("high_protein")) {
                s.preferenceBonus += 5;
            }
            if (restrictions.contains("low_calorie") && recipe.hasTag("low_calorie")) {
                s.preferenceBonus += 5;
            }
            if (restrictions.contains("easy_digestion") && recipe.hasTag("easy_digestion")) {
                s.preferenceBonus += 3;
            }
            if (restrictions.contains("vegetarian") && recipe.hasTag("vegetarian")) {
                s.preferenceBonus += 4;
            }
            if (restrictions.contains("vegan") && recipe.hasTag("vegan")) {
                s.preferenceBonus += 5;
            }
            if (restrictions.contains("no_spicy") && !recipe.hasTag("spicy")) {
                s.preferenceBonus += 2;
            }
            if (restrictions.contains("no_fried") && !recipe.hasTag("fried")) {
                s.preferenceBonus += 2;
            }
        }

        // Bonus "quick" dacă mood = tired și rețeta e rapidă
        if (mood == UserPreferences.Mood.TIRED && recipe.hasTag("quick")) {
            s.preferenceBonus += 5;
        }

        // Limitează bonusul la 10 pts
        s.preferenceBonus = Math.min(s.preferenceBonus, 10);

        return s;
    }

    // ══════════════════════════════════════════════════════════════
    // INGREDIENTE: LIPSĂ / DISPONIBILE
    // ══════════════════════════════════════════════════════════════

    private static List<String> getMissingIngredients(Recipe recipe, UserPreferences prefs) {
        List<String> missing = new ArrayList<>();
        List<String> owned   = prefs.getOwnedIngredients();

        for (String needed : recipe.getIngredients()) {
            if (!containsIgnoreCase(owned, needed)) {
                missing.add(needed);
            }
        }
        return missing;
    }

    private static List<String> getOwnedIngredients(Recipe recipe, UserPreferences prefs) {
        List<String> have  = new ArrayList<>();
        List<String> owned = prefs.getOwnedIngredients();

        for (String needed : recipe.getIngredients()) {
            if (containsIgnoreCase(owned, needed)) {
                have.add(needed);
            }
        }
        return have;
    }

    private static String buildIngredientSummary(Recipe recipe, UserPreferences prefs) {
        List<String> needed = recipe.getIngredients();
        List<String> owned  = prefs.getOwnedIngredients();
        int matches = countMatches(owned, needed);
        int total   = needed.size();

        if (total == 0) return "";
        return "Ai " + matches + " din " + total + " ingrediente";
    }

    // ══════════════════════════════════════════════════════════════
    // MOTIVE TEXT (afișate pe card)
    // ══════════════════════════════════════════════════════════════

    private static List<String> buildReasons(Recipe recipe, UserPreferences prefs,
                                             List<String> missing,
                                             ScoreBreakdown breakdown) {
        List<String> reasons = new ArrayList<>();
        List<String> restrictions = prefs.getMedicalRestrictions();

        // Ingrediente
        if (missing.isEmpty()) {
            reasons.add("Ai toate ingredientele necesare");
        } else if (missing.size() <= 2) {
            reasons.add("Îți lipsesc doar " + missing.size() + " ingrediente");
        }

        // Timp
        if (recipe.getTimeMinutes() <= prefs.getAvailableTimeMinutes()) {
            reasons.add("Gata în " + recipe.getTimeMinutes() + " min");
        }

        // Mood
        if (prefs.getMood() == UserPreferences.Mood.TIRED
                && recipe.getDifficulty() == Recipe.Difficulty.EASY) {
            reasons.add("Rețetă simplă, perfectă când ești obosit");
        }
        if (prefs.getMood() == UserPreferences.Mood.TIRED && recipe.hasTag("quick")) {
            reasons.add("Rapidă — ideal pentru energie scăzută");
        }

        // Preferințe nutriționale
        if (restrictions != null) {
            if (restrictions.contains("high_protein") && recipe.hasTag("high_protein")) {
                reasons.add("Bogată în proteine");
            }
            if (restrictions.contains("low_calorie") && recipe.hasTag("low_calorie")) {
                reasons.add("Puține calorii — " + recipe.getCalories() + " kcal");
            }
            if (restrictions.contains("vegetarian") && recipe.hasTag("vegetarian")) {
                reasons.add("Compatibilă cu dieta vegetariană");
            }
            if (restrictions.contains("vegan") && recipe.hasTag("vegan")) {
                reasons.add("Compatibilă cu dieta vegană");
            }
            if (restrictions.contains("easy_digestion") && recipe.hasTag("easy_digestion")) {
                reasons.add("Ușor digerabilă");
            }
            if (restrictions.contains("lactose_free")) {
                reasons.add("Fără lactoză");
            }
        }

        return reasons;
    }

    // ══════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════

    private static int countMatches(List<String> owned, List<String> needed) {
        if (owned == null || owned.isEmpty()) return 0;
        int count = 0;
        for (String ing : needed) {
            if (containsIgnoreCase(owned, ing)) count++;
        }
        return count;
    }

    private static boolean containsIgnoreCase(List<String> list, String target) {
        if (list == null) return false;
        String t = target.toLowerCase().trim();
        for (String item : list) {
            if (item.toLowerCase().trim().equals(t)) return true;
        }
        return false;
    }

    // ══════════════════════════════════════════════════════════════
    // CLASA INTERNĂ: defalcare scor pe criterii
    // ══════════════════════════════════════════════════════════════

    /**
     * Ține scorul defalcat pe categorii — util pentru debugging și pentru
     * generarea motivelor text.
     */
    static class ScoreBreakdown {
        int ingredientScore = 0;
        int timeScore       = 0;
        int moodScore       = 0;
        int skillScore      = 0;
        int preferenceBonus = 0;
        int missingPenalty  = 0;

        int total() {
            return Math.max(0, Math.min(100,
                    ingredientScore + timeScore + moodScore
                            + skillScore + preferenceBonus + missingPenalty));
        }
    }
}