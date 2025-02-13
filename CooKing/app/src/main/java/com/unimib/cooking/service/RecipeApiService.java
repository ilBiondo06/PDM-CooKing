package com.unimib.cooking.service;


import static com.unimib.cooking.util.Constants.*;

import com.unimib.cooking.model.RecipeAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApiService {

    @GET(SEARCH_ENDPOINT)
    Call <RecipeAPIResponse> getRecipes(
      @Query("f") String letterStart);

    @GET(SEARCH_ENDPOINT)
    Call <RecipeAPIResponse> getRecipesByName(
            @Query("s") String name);

    @GET(FILTER_ENDPOINT)
    Call <RecipeAPIResponse> getRecipesByCategory(
      @Query("c") String category);

    @GET(FILTER_ENDPOINT)
    Call <RecipeAPIResponse> getRecipesByArea(
      @Query("a") String area);

    @GET(ID_ENDPOINT)
    Call <RecipeAPIResponse> getRecipesById(
            @Query("i") String id);


    @GET(RANDOM_ENDPOINT)
    Call <RecipeAPIResponse> getRandomRecipe();

}
