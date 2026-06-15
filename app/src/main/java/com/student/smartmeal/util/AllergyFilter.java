package com.student.smartmeal.util;

import com.student.smartmeal.model.Recipe;
import com.student.smartmeal.model.UserPreferences;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapare între codurile de alergie și ingredientele reale pe care le conțin.
 * DOAR acestea duc la excluderea completă a unei rețete.
 * Preferințele (vegetarian, high_protein etc.) NU sunt alergii și nu exclud rețete.
 */
public class AllergyFilter {

    // Codurile de restricție care sunt ALERGII REALE (excludere completă)
    private static final List<String> REAL_ALLERGIES = Arrays.asList(
            "lactose_free",
            "gluten_free",
            "nut_free",
            "egg_free",
            "seafood_free"
    );

    // Mapare: cod alergie → cuvinte cheie în ingrediente
    private static final Map<String, List<String>> ALLERGEN_KEYWORDS = new HashMap<>();

    static {
        ALLERGEN_KEYWORDS.put("lactose_free", Arrays.asList(
                "lapte", "unt", "brânză", "smântână", "iaurt",
                "frișcă", "parmezan", "feta", "cașcaval", "cremă de brânză"
        ));

        ALLERGEN_KEYWORDS.put("gluten_free", Arrays.asList(
                "făină", "pâine", "paste", "noodles", "griș", "cereale",
                "lipie", "covrigi", "biscuiți", "fulgi de ovăz", "tăiței"
        ));

        ALLERGEN_KEYWORDS.put("nut_free", Arrays.asList(
                "nuci", "arahide", "migdale", "alune", "cashew",
                "fistic", "unt de arahide", "tahini", "susan"
        ));

        ALLERGEN_KEYWORDS.put("egg_free", Arrays.asList(
                "ouă", "ou", "gălbenuș", "albuș"
        ));

        ALLERGEN_KEYWORDS.put("seafood_free", Arrays.asList(
                "pește", "ton", "somon", "creveți", "fructe de mare",
                "calamari", "midii", "crab", "hering", "macrou", "cod"
        ));
    }

    /**
     * Verifică dacă o rețetă trebuie EXCLUSĂ COMPLET pe baza alergiilor reale.
     * Preferințele (vegetarian, high_protein etc.) nu sunt verificate aici.
     *
     * @return true = excludere completă, false = rețeta poate rămâne
     */
    public static boolean shouldExclude(Recipe recipe, UserPreferences prefs) {
        List<String> userRestrictions = prefs.getMedicalRestrictions();
        if (userRestrictions == null || userRestrictions.isEmpty()) return false;

        for (String restriction : userRestrictions) {

            // Ignorăm preferințele — ele nu exclud rețete
            if (!REAL_ALLERGIES.contains(restriction)) continue;

            List<String> forbiddenKeywords = ALLERGEN_KEYWORDS.get(restriction);
            if (forbiddenKeywords == null) continue;

            // Verificăm dacă vreun ingredient din rețetă conține un cuvânt cheie interzis
            for (String recipeIngredient : recipe.getIngredients()) {
                String ingLower = recipeIngredient.toLowerCase().trim();
                for (String keyword : forbiddenKeywords) {
                    if (ingLower.contains(keyword.toLowerCase())) {
                        return true; // găsit alergen → excludere completă
                    }
                }
            }
        }

        return false; // nicio alergie detectată → rețeta rămâne
    }

    /**
     * Verifică dacă ingredientele de evitat (lista liberă a userului) există în rețetă.
     */
    public static boolean containsAvoidedIngredient(Recipe recipe, UserPreferences prefs) {
        List<String> avoid = prefs.getAvoidIngredients();
        if (avoid == null || avoid.isEmpty()) return false;

        for (String avoidItem : avoid) {
            String avoidLower = avoidItem.toLowerCase().trim();
            if (avoidLower.isEmpty()) continue;

            for (String ingredient : recipe.getIngredients()) {
                if (ingredient.toLowerCase().contains(avoidLower)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returnează lista de alergeni găsiți în rețetă față de restricțiile userului.
     * Folosit pentru avertismente în UI.
     */
    public static List<String> getDetectedAllergens(Recipe recipe, UserPreferences prefs) {
        java.util.List<String> detected = new java.util.ArrayList<>();
        List<String> userRestrictions = prefs.getMedicalRestrictions();
        if (userRestrictions == null) return detected;

        for (String restriction : userRestrictions) {
            if (!REAL_ALLERGIES.contains(restriction)) continue;
            List<String> keywords = ALLERGEN_KEYWORDS.get(restriction);
            if (keywords == null) continue;

            for (String ingredient : recipe.getIngredients()) {
                String ingLower = ingredient.toLowerCase();
                for (String keyword : keywords) {
                    if (ingLower.contains(keyword.toLowerCase())) {
                        detected.add(ingredient);
                        break;
                    }
                }
            }
        }
        return detected;
    }
}