package com.student.smartmeal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.student.smartmeal.model.UserPreferences;
import com.student.smartmeal.util.PreferencesManager;

public class PreferencesActivity extends AppCompatActivity {

    private TextInputEditText etTime;
    private RadioGroup rgMood, rgSkill;
    private PreferencesManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        etTime = findViewById(R.id.etTime);
        rgMood = findViewById(R.id.rgMood);
        rgSkill = findViewById(R.id.rgSkill);

        pm = new PreferencesManager(this);

        setupToolbar();
        loadSavedPreferences();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (!validateInputs()) return;

            UserPreferences prefs = new UserPreferences();

            prefs.setAvailableTimeMinutes(
                    Integer.parseInt(etTime.getText().toString().trim())
            );

            int moodId = rgMood.getCheckedRadioButtonId();
            if (moodId == R.id.rbTired) {
                prefs.setMood(UserPreferences.Mood.TIRED);
            } else if (moodId == R.id.rbEnergetic) {
                prefs.setMood(UserPreferences.Mood.ENERGETIC);
            } else {
                prefs.setMood(UserPreferences.Mood.NORMAL);
            }

            int skillId = rgSkill.getCheckedRadioButtonId();
            if (skillId == R.id.rbMedium) {
                prefs.setCookingSkill(UserPreferences.CookingSkill.MEDIUM);
            } else if (skillId == R.id.rbAdvanced) {
                prefs.setCookingSkill(UserPreferences.CookingSkill.ADVANCED);
            } else {
                prefs.setCookingSkill(UserPreferences.CookingSkill.BEGINNER);
            }

            pm.saveLastTime(prefs.getAvailableTimeMinutes());
            pm.saveLastMood(prefs.getMood().name());
            pm.saveLastSkill(prefs.getCookingSkill().name());

            Intent intent = new Intent(this, IngredientsActivity.class);
            intent.putExtra("preferences", prefs);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle("Pasul 1: Preferințe");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadSavedPreferences() {
        int lastTime = pm.getLastTime();
        if (lastTime > 0) {
            etTime.setText(String.valueOf(lastTime));
        }

        String lastMood = pm.getLastMood();
        if ("TIRED".equals(lastMood)) {
            rgMood.check(R.id.rbTired);
        } else if ("ENERGETIC".equals(lastMood)) {
            rgMood.check(R.id.rbEnergetic);
        } else {
            rgMood.check(R.id.rbNormal);
        }

        String lastSkill = pm.getLastSkill();
        if ("MEDIUM".equals(lastSkill)) {
            rgSkill.check(R.id.rbMedium);
        } else if ("ADVANCED".equals(lastSkill)) {
            rgSkill.check(R.id.rbAdvanced);
        } else {
            rgSkill.check(R.id.rbBeginner);
        }
    }

    private boolean validateInputs() {
        String timeStr = etTime.getText() != null
                ? etTime.getText().toString().trim()
                : "";

        if (timeStr.isEmpty()) {
            etTime.setError("Introdu timpul disponibil");
            return false;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}