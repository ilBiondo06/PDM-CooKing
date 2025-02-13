package com.unimib.cooking.source.recipe;

import com.unimib.cooking.repository.recipe.RecipeResponseCallBack;

public abstract class BaseRecipeRemoteDataSource {

    protected RecipeResponseCallBack recipeCallback;

    public void setRecipeCallback(RecipeResponseCallBack recipeCallback) {
        this.recipeCallback = recipeCallback;
    }

    public abstract void getRecipesByLetter(String letter);

    public abstract void getRecipesByName(String name);

    public abstract void getRecipesByCategory(String category);

    public abstract void getRecipesByArea(String area);

    public abstract void getRandomRecipe();

    public abstract void getRecipesById(long id);


}
