// Fișier: java/com/student/smartmeal/util/PreferencesManager.java
package com.student.smartmeal.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gestionează persistența locală cu SharedPreferences:
 * - Ultimele ingrediente introduse
 * - Ultimele restricții selectate
 * - Ultimul timp disponibil
 * - Rețete favorite (după ID)
 */
public class PreferencesManager {

    private static final String PREFS_NAME = "smartmeal_prefs";
    private static final String KEY_LAST_TIME = "last_time";
    private static final String KEY_LAST_MOOD = "last_mood";
    private static final String KEY_LAST_SKILL = "last_skill";
    private static final String KEY_LAST_INGR  = "last_ingredients";
    private static final String KEY_LAST_AVOID = "last_avoid";
    private static final String KEY_LAST_RESTR = "last_restrictions";
    private static final String KEY_FAVORITES = "favorites";

    private final SharedPreferences sp;

    public PreferencesManager(Context context) {
        sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ─── Timp ───────────────────────────────────────
    public void saveLastTime(int minutes) {
        sp.edit().putInt(KEY_LAST_TIME, minutes).apply();
    }
    public int getLastTime() {
        return sp.getInt(KEY_LAST_TIME, 30); // default 30 min
    }

    // ─── Mood (salvăm ca String) ─────────────────────
    public void saveLastMood(String mood) {
        sp.edit().putString(KEY_LAST_MOOD, mood).apply();
    }
    public String getLastMood() {
        return sp.getString(KEY_LAST_MOOD, "NORMAL");
    }

    // ─── Skill ──────────────────────────────────────
    public void saveLastSkill(String skill) {
        sp.edit().putString(KEY_LAST_SKILL, skill).apply();
    }
    public String getLastSkill() {
        return sp.getString(KEY_LAST_SKILL, "BEGINNER");
    }

    // ─── Ingrediente ─────────────────────────────────
    public void saveLastIngredients(List<String> ingredients) {
        sp.edit().putStringSet(KEY_LAST_INGR,
                new HashSet<>(ingredients)).apply();
    }
    public List<String> getLastIngredients() {
        Set<String> set = sp.getStringSet(KEY_LAST_INGR, new HashSet<>());
        return new ArrayList<>(set);
    }

    // ─── Ingrediente de evitat ────────────────────────
    public void saveLastAvoid(List<String> avoid) {
        sp.edit().putString(KEY_LAST_AVOID,
                listToString(avoid)).apply();
    }
    public List<String> getLastAvoid() {
        return stringToList(sp.getString(KEY_LAST_AVOID, ""));
    }

    // ─── Restricții medicale ─────────────────────────
    public void saveLastRestrictions(List<String> restrictions) {
        sp.edit().putStringSet(KEY_LAST_RESTR,
                new HashSet<>(restrictions)).apply();
    }
    public List<String> getLastRestrictions() {
        Set<String> set = sp.getStringSet(KEY_LAST_RESTR, new HashSet<>());
        return new ArrayList<>(set);
    }

    // ─── Favorite ────────────────────────────────────
    public void addFavorite(String recipeId) {
        Set<String> favs = new HashSet<>(getFavorites());
        favs.add(recipeId);
        sp.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }
    public void removeFavorite(String recipeId) {
        Set<String> favs = new HashSet<>(getFavorites());
        favs.remove(recipeId);
        sp.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }
    public boolean isFavorite(String recipeId) {
        return getFavorites().contains(recipeId);
    }
    public List<String> getFavorites() {
        Set<String> set = sp.getStringSet(KEY_FAVORITES, new HashSet<>());
        return new ArrayList<>(set);
    }

    // ─── Helpers ─────────────────────────────────────
    private static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return android.text.TextUtils.join(",", list);
    }
    private static List<String> stringToList(String s) {
        if (s == null || s.trim().isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(s.split(",")));
    }
}