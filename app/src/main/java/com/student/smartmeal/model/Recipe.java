package com.student.smartmeal.model;

import java.util.List;

public class Recipe {

    public enum Difficulty { EASY, MEDIUM, HARD }

    // Constante restricții medicale
    public static final String LACTOSE_FREE = "lactose_free";
    public static final String GLUTEN_FREE = "gluten_free";
    public static final String DIABETIC = "diabetic";
    public static final String VEGETARIAN = "vegetarian";
    public static final String VEGAN = "vegan";
    public static final String LOW_SODIUM = "low_sodium";
    public static final String NUT_FREE = "nut_free";
    public static final String EGG_FREE = "egg_free";
    public static final String SEAFOOD_FREE = "seafood_free";
    public static final String NO_SPICY = "no_spicy";
    public static final String NO_FRIED = "no_fried";
    public static final String NO_SUGAR = "no_sugar";
    public static final String NO_RED_MEAT = "no_red_meat";
    public static final String NO_PORK = "no_pork";
    public static final String HIGH_PROTEIN = "high_protein";
    public static final String LOW_CALORIE = "low_calorie";
    public static final String EASY_DIGESTION= "easy_digestion";

    private String id;
    private String name;
    private String description;
    private List<String> ingredients;
    private int timeMinutes;
    private Difficulty difficulty;
    private int calories;
    private List<String> allergens;
    private List<String> tags;// ex: "vegan", "high_protein", "quick"
    private List<String> compatibleWith; // restricții medicale compatibile
    private List<String> steps;

    // Constructor complet
    public Recipe(String id, String name, String description,
                  List<String> ingredients, int timeMinutes,
                  Difficulty difficulty, int calories,
                  List<String> allergens, List<String> tags,
                  List<String> compatibleWith, List<String> steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.timeMinutes = timeMinutes;
        this.difficulty = difficulty;
        this.calories = calories;
        this.allergens = allergens;
        this.tags = tags;
        this.compatibleWith = compatibleWith;
        this.steps = steps;
    }

    // Getteri
    public String getId(){ return id; }
    public String getName(){ return name; }
    public String getDescription(){ return description; }
    public List<String> getIngredients(){ return ingredients; }
    public int getTimeMinutes(){ return timeMinutes; }
    public Difficulty getDifficulty(){ return difficulty; }
    public int getCalories(){ return calories; }
    public List<String> getAllergens(){ return allergens; }
    public List<String> getTags(){ return tags; }
    public List<String> getCompatibleWith(){ return compatibleWith; }
    public List<String> getSteps(){ return steps; }

    public String getDifficultyString() {
        switch (difficulty) {
            case EASY:   return "Ușor";
            case MEDIUM: return "Mediu";
            case HARD:   return "Dificil";
            default:     return "Ușor";
        }
    }

    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }
}