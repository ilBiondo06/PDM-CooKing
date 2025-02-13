package com.unimib.cooking.ui.homePage.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.cooking.repository.recipe.RecipeRepository;

public class RecipeViewModelFactory implements ViewModelProvider.Factory {

    private final RecipeRepository recipeRepository;

    public RecipeViewModelFactory(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RecipeViewModel(recipeRepository);
    }
}
