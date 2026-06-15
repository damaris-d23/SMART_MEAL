# SmartMeal

SmartMeal is an Android mobile application that helps users discover recipe recommendations based on their ingredients, available time, mood, cooking skill and dietary preferences.

## Screenshots

| Home | Ingredients |
|------|------------|
| ![Home](screenshots/home.png) | ![Ingredients](screenshots/ingredients.png) |

| Preferences | Results |
|-------------|---------|
| ![Preferences](screenshots/preferences.png) | ![Results](screenshots/results.png) |

| Favorites |
|-----------|
| ![Favorites](screenshots/favorites.png) |

## Features

- Choose cooking preferences such as time, mood and skill level
- Add available ingredients
- Set dietary restrictions or ingredients to avoid
- Get personalized recipe recommendations
- View all recipes
- Save favorite recipes
- See recipe details and missing ingredients
- Simple bottom navigation between Home, Results and Favorites

## Technologies Used

- Java
- Android Studio
- Gradle
- Android SDK
- AppCompat
- Material Components
- RecyclerView
- CardView
- Gson
- JSON assets for recipe data

## How the Recommendation Works

The recommendation engine scores recipes based on available ingredients, cooking time, mood, difficulty, cooking skill, dietary preferences and avoided ingredients. Recipes are then sorted by the best match score.

## Project Structure

```text
SMART_MEAL/
├── app/
│   ├── src/main/java/com/student/smartmeal/
│   ├── src/main/assets/recipes.json
│   └── build.gradle.kts
├── screenshots/
├── README.md
├── build.gradle.kts
└── settings.gradle.kts
