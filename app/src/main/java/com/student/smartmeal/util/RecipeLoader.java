package com.student.smartmeal.util;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.student.smartmeal.model.Recipe;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecipeLoader {

    // Cache: citim JSON o singură dată, păstrăm în memorie
    private static List<Recipe> cachedRecipes = null;

    public static List<Recipe> loadRecipes(Context context) {
        if (cachedRecipes != null) return cachedRecipes;

        cachedRecipes = new ArrayList<>();
        try {
            // Citește fișierul assets/recipes.json ca String
            String json = readAssetFile(context, "recipes.json");

            // Parsează JSON-ul cu Gson
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray recipesArray = root.getAsJsonArray("recipes");

            for (JsonElement elem : recipesArray) {
                JsonObject obj = elem.getAsJsonObject();
                Recipe recipe = parseRecipe(obj);
                cachedRecipes.add(recipe);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cachedRecipes;
    }

    private static Recipe parseRecipe(JsonObject obj) {
        String id          = obj.get("id").getAsString();
        String name        = obj.get("name").getAsString();
        String description = obj.get("description").getAsString();
        int timeMinutes    = obj.get("timeMinutes").getAsInt();
        int calories       = obj.get("calories").getAsInt();

        // Dificultate
        String diffStr = obj.get("difficulty").getAsString();
        Recipe.Difficulty difficulty;
        switch (diffStr) {
            case "MEDIUM": difficulty = Recipe.Difficulty.MEDIUM; break;
            case "HARD":   difficulty = Recipe.Difficulty.HARD;   break;
            default:       difficulty = Recipe.Difficulty.EASY;
        }

        // Liste de String-uri
        List<String> ingredients   = jsonArrayToList(obj.getAsJsonArray("ingredients"));
        List<String> allergens     = jsonArrayToList(obj.getAsJsonArray("allergens"));
        List<String> tags          = jsonArrayToList(obj.getAsJsonArray("tags"));
        List<String> compatibleWith= jsonArrayToList(obj.getAsJsonArray("compatibleWith"));
        List<String> steps         = jsonArrayToList(obj.getAsJsonArray("steps"));

        return new Recipe(id, name, description, ingredients, timeMinutes,
                difficulty, calories, allergens, tags, compatibleWith, steps);
    }

    private static List<String> jsonArrayToList(JsonArray arr) {
        List<String> list = new ArrayList<>();
        if (arr == null) return list;
        for (JsonElement e : arr) list.add(e.getAsString());
        return list;
    }

    private static String readAssetFile(Context context, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }
}