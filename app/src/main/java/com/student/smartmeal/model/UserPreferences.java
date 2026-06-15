package com.student.smartmeal.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class UserPreferences implements Parcelable {

    public enum Mood { TIRED, NORMAL, ENERGETIC }
    public enum CookingSkill { BEGINNER, MEDIUM, ADVANCED }

    // bugetLei ELIMINAT
    private int availableTimeMinutes;
    private Mood mood;
    private CookingSkill cookingSkill;
    private List<String> ownedIngredients;
    private List<String> medicalRestrictions;
    private List<String> avoidIngredients;  //  ingrediente de evitat

    public UserPreferences() {
        ownedIngredients   = new ArrayList<>();
        medicalRestrictions = new ArrayList<>();
        avoidIngredients   = new ArrayList<>();
        mood               = Mood.NORMAL;
        cookingSkill       = CookingSkill.BEGINNER;
        availableTimeMinutes = 60;
    }

    // Getteri și setteri
    public int getAvailableTimeMinutes(){ return availableTimeMinutes; }
    public void setAvailableTimeMinutes(int t) { this.availableTimeMinutes = t; }

    public Mood getMood(){ return mood; }
    public void setMood(Mood m) { this.mood = m; }

    public CookingSkill getCookingSkill(){ return cookingSkill; }
    public void setCookingSkill(CookingSkill s){ this.cookingSkill = s; }

    public List<String> getOwnedIngredients() { return ownedIngredients; }
    public void setOwnedIngredients(List<String> l) { this.ownedIngredients = l; }

    public List<String> getMedicalRestrictions(){ return medicalRestrictions; }
    public void setMedicalRestrictions(List<String> r){ this.medicalRestrictions = r; }

    public List<String> getAvoidIngredients(){ return avoidIngredients; }
    public void setAvoidIngredients(List<String> a) { this.avoidIngredients = a; }

    // Parcelable ---
    protected UserPreferences(Parcel in) {
        availableTimeMinutes = in.readInt();
        mood         = Mood.values()[in.readInt()];
        cookingSkill = CookingSkill.values()[in.readInt()];
        ownedIngredients    = in.createStringArrayList();
        medicalRestrictions = in.createStringArrayList();
        avoidIngredients    = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(availableTimeMinutes);
        dest.writeInt(mood.ordinal());
        dest.writeInt(cookingSkill.ordinal());
        dest.writeStringList(ownedIngredients);
        dest.writeStringList(medicalRestrictions);
        dest.writeStringList(avoidIngredients);
    }

    @Override public int describeContents() { return 0; }

    public static final Creator<UserPreferences> CREATOR = new Creator<UserPreferences>() {
        @Override
        public UserPreferences createFromParcel(Parcel in) { return new UserPreferences(in); }
        @Override
        public UserPreferences[] newArray(int size) { return new UserPreferences[size]; }
    };
}