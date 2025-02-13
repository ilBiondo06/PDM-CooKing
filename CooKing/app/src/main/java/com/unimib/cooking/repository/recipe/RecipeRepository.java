package com.unimib.cooking.repository.recipe;

import static com.unimib.cooking.util.Constants.FRESH_TIMEOUT;
import static com.unimib.cooking.util.Constants.RANDOM_REFRESH;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.RecipeAPIResponse;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.source.recipe.BaseRecipeLocalDataSource;
import com.unimib.cooking.source.recipe.BaseRecipeRemoteDataSource;

import java.util.List;


/**
 * Repository class to get the recipe from local or from a remote source.
 */
public class RecipeRepository implements RecipeResponseCallBack {

    private static final String TAG = RecipeRepository.class.getSimpleName();

    private final MutableLiveData<Result> allRecipesMutableLiveData;
    private final MutableLiveData<Result> favoriteRecipesMutableLiveData;
    private final BaseRecipeRemoteDataSource recipeRemoteDataSource;
    private final BaseRecipeLocalDataSource recipeLocalDataSource;

    public RecipeRepository(BaseRecipeRemoteDataSource recipeRemoteDataSource,
                            BaseRecipeLocalDataSource recipeLocalDataSource) {

        allRecipesMutableLiveData = new MutableLiveData<>();
        favoriteRecipesMutableLiveData = new MutableLiveData<>();
        this.recipeRemoteDataSource = recipeRemoteDataSource;
        this.recipeLocalDataSource = recipeLocalDataSource;
        this.recipeRemoteDataSource.setRecipeCallback(this);
        this.recipeLocalDataSource.setRecipeCallback(this);
    }

    public MutableLiveData<Result> fetchRecipesByLetter(String letter, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the recipe from the Web Service if the last download
        // of the recipe has been performed more than FRESH_TIMEOUT value ago
      if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            recipeRemoteDataSource.getRecipesByLetter(letter);
        } else {
            recipeLocalDataSource.getRecipesByLetter(letter);
        }

        return allRecipesMutableLiveData;
    }

    public MutableLiveData<Result> fetchRecipesByName(String name, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the recipe from the Web Service if the last download
        // of the recipe has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            recipeRemoteDataSource.getRecipesByName(name);
        } else {
            recipeLocalDataSource.getRecipesByName(name);
        }

        return allRecipesMutableLiveData;
    }

    public void insertRecipe(Recipe recipe) {
        recipeLocalDataSource.insertRecipes((List<Recipe>) recipe);
    }

    public MutableLiveData<Result> fetchRandomRecipe(int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            Log.d(TAG, "fetchRecipesByCategory called remoto");
            recipeRemoteDataSource.getRandomRecipe();
        } else {
            recipeLocalDataSource.getRandomRecipe();
        }


        return allRecipesMutableLiveData;
    }

    public MutableLiveData<Result> fetchRecipesByCategory(String category, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            Log.d(TAG, "fetchRecipesByCategory called remoto");
            recipeRemoteDataSource.getRecipesByCategory(category);
        } else {
            recipeLocalDataSource.getRecipesByCategory(category);
        }

        return allRecipesMutableLiveData;
    }

    public MutableLiveData<Result> fetchRecipesByArea(String area, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            recipeRemoteDataSource.getRecipesByArea(area);
        } else {
            recipeLocalDataSource.getRecipesByArea(area);
        }

        return allRecipesMutableLiveData;
    }

    public MutableLiveData<Result> fetchRecipeById(long id, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if(currentTime - lastUpdate > FRESH_TIMEOUT) {
            recipeRemoteDataSource.getRecipesById(id);
        } else {
            recipeLocalDataSource.getRecipeById(id);
        }

        return allRecipesMutableLiveData;
    }

    public MutableLiveData<Result> getRecipeById(long id) {
        recipeLocalDataSource.getRecipeById(id);
        return allRecipesMutableLiveData;
    }

    public MutableLiveData<Result> getFavoriteRecipes() {
        recipeLocalDataSource.getFavoriteRecipes();
        return favoriteRecipesMutableLiveData;
    }

    public MutableLiveData<Result> getRecipesByLetter(String letter) {
        recipeLocalDataSource.getRecipesByLetter(letter);
        return allRecipesMutableLiveData;
    }

    public MutableLiveData<Result> getRecipesByCategory(String category) {
        recipeLocalDataSource.getRecipesByCategory(category);
        return allRecipesMutableLiveData;
    }

    public MutableLiveData<Result> getRecipesByArea(String area) {
        recipeLocalDataSource.getRecipesByArea(area);
        return allRecipesMutableLiveData;
    }

    public void insertFavoriteRecipes(List<Recipe> recipes) {
        recipeLocalDataSource.insertFavoriteRecipes(recipes);
    }

    public void updateRecipe(Recipe recipe) {
        recipeLocalDataSource.updateRecipe(recipe);
    }

    public void deleteFavoriteRecipes() {
        Log.d(TAG, "deleteFavoriteRecipes called");
        recipeLocalDataSource.deleteFavoriteRecipes();
    }

    public void onSuccessFromRemote(RecipeAPIResponse recipeApiResponse, long lastUpdate) {
        recipeLocalDataSource.insertRecipes(recipeApiResponse.getRecipes());
    }

    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allRecipesMutableLiveData.postValue(result);
    }

    public void onSuccessFromLocal(List<Recipe> recipeList) {
        Result.RecipeSuccess result = new Result.RecipeSuccess(new RecipeAPIResponse(recipeList));
        allRecipesMutableLiveData.postValue(result);
    }

    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allRecipesMutableLiveData.postValue(resultError);
        favoriteRecipesMutableLiveData.postValue(resultError);
    }

    @Override
    public void onRecipesFavoriteStatusChanged(Recipe recipe, List<Recipe> favoriteRecipes) {

        Result allRecipesResult = allRecipesMutableLiveData.getValue();

        if (allRecipesResult != null && allRecipesResult.isSuccess()) {
            List<Recipe> oldAllRecipes = ((Result.RecipeSuccess)allRecipesResult).getData().getRecipes();
            if (oldAllRecipes.contains(recipe)) {
                oldAllRecipes.set(oldAllRecipes.indexOf(recipe), recipe);
                allRecipesMutableLiveData.postValue(allRecipesResult);
            }
        }
        favoriteRecipesMutableLiveData.postValue(new Result.RecipeSuccess(new RecipeAPIResponse(favoriteRecipes)));
    }

    @Override
    public void onRecipesFavoriteStatusChanged(List<Recipe> recipes) {
        favoriteRecipesMutableLiveData.postValue(new Result.RecipeSuccess(new RecipeAPIResponse(recipes)));
    }

    public void onDeleteFavoriteRecipesSuccess(List<Recipe> favoriteRecipes) {
        Result allRecipesResult = allRecipesMutableLiveData.getValue();

        if (allRecipesResult != null && allRecipesResult.isSuccess()) {
            List<Recipe> oldAllRecipes = ((Result.RecipeSuccess)allRecipesResult).getData().getRecipes();
            for (Recipe recipe : favoriteRecipes) {
                if (oldAllRecipes.contains(recipe)) {
                    oldAllRecipes.set(oldAllRecipes.indexOf(recipe), recipe);
                }
            }
            allRecipesMutableLiveData.postValue(allRecipesResult);
        }

        if (favoriteRecipesMutableLiveData.getValue() != null &&
                favoriteRecipesMutableLiveData.getValue().isSuccess()) {
            favoriteRecipes.clear();
            Result.RecipeSuccess result = new Result.RecipeSuccess(new RecipeAPIResponse(favoriteRecipes));
            favoriteRecipesMutableLiveData.postValue(result);
        }
    }

    @Override
    public void onSuccessFromLocalByLetter(List<Recipe> recipesList) {
        Result.RecipeSuccess result = new Result.RecipeSuccess(new RecipeAPIResponse(recipesList));
        allRecipesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocalByCategory(List<Recipe> recipesList) {
        Result.RecipeSuccess result = new Result.RecipeSuccess(new RecipeAPIResponse(recipesList));
        allRecipesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocalByArea(List<Recipe> recipesList) {
        Result.RecipeSuccess result = new Result.RecipeSuccess(new RecipeAPIResponse(recipesList));
        allRecipesMutableLiveData.postValue(result);
    }

    public void removeFromFavorites(Recipe recipe) {
        recipe.setLiked(false); // Imposta il like a false
        recipeLocalDataSource.updateRecipeLikeStatus(recipe);
    }

    public void insertRecipes(List<Recipe> recipes) {
        recipeLocalDataSource.insertRecipes(recipes);
    }

}
