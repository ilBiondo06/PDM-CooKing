package com.unimib.cooking.model;
import java.util.List;

public class RecipeAPIResponse {
    private List<Recipe> meals;

    public RecipeAPIResponse(List<Recipe> recipes) {
        this.meals = recipes;
    }

    public RecipeAPIResponse(){}

    public List<Recipe> getRecipes() {
        return meals;
    }

    public void setRecipes(List<Recipe> meals) {
        this.meals = meals;
    }
}
