package com.unimib.cooking.source.recipe;

import com.unimib.cooking.database.RecipeDAO;
import com.unimib.cooking.database.RecipeRoomDatabase;
import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.util.Constants;
import com.unimib.cooking.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeLocalDataSource extends BaseRecipeLocalDataSource {

    private final RecipeDAO recipeDAO;
    private final SharedPreferencesUtils sharedPreferencesUtil;

    public RecipeLocalDataSource(RecipeRoomDatabase newsRoomDatabase,
                                 SharedPreferencesUtils sharedPreferencesUtil) {
        this.recipeDAO = newsRoomDatabase.recipeDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void getFavoriteRecipes() {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> favoriteRecipes = recipeDAO.getLiked();
            recipeCallback.onRecipesFavoriteStatusChanged(favoriteRecipes);
        });
    }

    @Override
    public void updateRecipe(Recipe recipe) {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter = recipeDAO.updateRecipe(recipe);

            // It means that the update succeeded because only one row had to be updated
            if (rowUpdatedCounter == 1) {
                Recipe updatedRecipes = recipeDAO.getRecipe(recipe.getIdMeal());
                recipeCallback.onRecipesFavoriteStatusChanged(updatedRecipes, recipeDAO.getLiked());
            } else if(rowUpdatedCounter == 0) {
                recipeDAO.insert(recipe);
                Recipe updatedRecipes = recipeDAO.getRecipe(recipe.getIdMeal());
                recipeCallback.onRecipesFavoriteStatusChanged(updatedRecipes, recipeDAO.getLiked());
            }
        });
    }

    @Override
    public void deleteFavoriteRecipes() {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> favoriteRecipes = recipeDAO.getLiked();
            for (Recipe recipe : favoriteRecipes) {
                recipe.setLiked(false);
            }
            int updatedRowsNumber = recipeDAO.updateListFavoriteRecipes(favoriteRecipes);

            if (updatedRowsNumber == favoriteRecipes.size()) {
                recipeCallback.onDeleteFavoriteRecipesSuccess(favoriteRecipes);
            } else {
                recipeCallback.onFailureFromLocal(new Exception("error"/*UNEXPECTED_ERROR*/));
            }
        });
    }




    @Override
    public synchronized void insertRecipes(List<Recipe> recipesList) {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> allRecipes = recipeDAO.getAll();

            if (recipesList != null) {
                for (Recipe recipe : allRecipes) {
                    if (recipesList.contains(recipe)) {
                        recipesList.set(recipesList.indexOf(recipe), recipe);
                    }
                }

                List<Long> insertedRecipesIds = recipeDAO.insertRecipesList(recipesList);
                for (int i = 0; i < recipesList.size(); i++) {
                    recipesList.get(i).setIdMeal(insertedRecipesIds.get(i));
                }

                sharedPreferencesUtil.writeStringData(Constants.SHARED_PREFERENCES_FILENAME,
                        Constants.SHARED_PREFERNECES_LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                recipeCallback.onSuccessFromLocal(recipesList);
            }
        });
    }

    @Override
    public void getRecipeById(long id) {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipe = new ArrayList<>();
            recipe.add(recipeDAO.getRecipe(id));
            recipeCallback.onSuccessFromLocal(recipe);
        });
    }

    @Override
    public void getRecipesByLetter(String letter) {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDAO.getRecipesByLetter(letter);
            recipeCallback.onSuccessFromLocal(recipes);
        });
    }

    @Override
    public void getRecipesByName(String name) {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDAO.getRecipesByName(name);
            recipeCallback.onSuccessFromLocal(recipes);
        });
    }

    @Override
    public void getRecipesByCategory(String category) {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDAO.getRecipesByCategory(category);
            recipeCallback.onSuccessFromLocal(recipes);
        });
    }

    @Override
    public void getRecipesByArea(String area) {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDAO.getRecipesByArea(area);
            recipeCallback.onSuccessFromLocal(recipes);
        });
    }

    public void updateRecipeLikeStatus(Recipe recipe) {
        new Thread(() -> {
            // Qui chiami il metodo che aggiorna solo il campo 'liked' della ricetta
            recipeDAO.updateRecipeLikeStatus(recipe.getIdMeal());

            // Recupera le ricette favorite aggiornate
            List<Recipe> updatedFavorites = recipeDAO.getLiked();

            // Notifica l'interfaccia utente con le ricette favorite aggiornate
            recipeCallback.onRecipesFavoriteStatusChanged(updatedFavorites);
        }).start();
    }

    @Override
    public void getRandomRecipe() {
        RecipeRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Recipe> recipes = recipeDAO.getRandomRecipe();
            recipeCallback.onSuccessFromLocal(recipes);
        });
    }

}
