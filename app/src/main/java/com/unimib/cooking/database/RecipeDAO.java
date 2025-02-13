package com.unimib.cooking.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.unimib.cooking.model.Recipe;

@Dao
public interface RecipeDAO {

    @Query("SELECT * FROM Recipe")
    List<Recipe> getAll();

    @Query("SELECT * FROM recipe WHERE idMeal = :idMeal")
    Recipe getRecipe(long idMeal);

    @Query("SELECT * FROM recipe ORDER BY RANDOM() LIMIT 1")
    List<Recipe> getRandomRecipe();

    @Query("SELECT * FROM recipe WHERE strMeal LIKE '%' || :name || '%'")
    List<Recipe> getRecipesByName(String name);

    @Query("SELECT * FROM Recipe WHERE liked = 1")
    List<Recipe> getLiked();

    @Query("SELECT * FROM Recipe WHERE strMeal LIKE :letter || '%'")
    List<Recipe> getRecipesByLetter(String letter);


    @Query("SELECT * FROM Recipe WHERE strCategory = :category")
    List<Recipe> getRecipesByCategory(String category);

    @Query("SELECT * FROM Recipe WHERE strArea = :area")
    List<Recipe> getRecipesByArea(String area);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe... Recipes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Recipe> Recipes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertRecipesList(List<Recipe> recipeList);

    @Update
    int updateRecipe(Recipe Recipe);

    @Update
    int updateListFavoriteRecipes(List<Recipe> recipes);

    @Delete
    void delete(Recipe user);

    @Query("DELETE from Recipe WHERE liked = 0")
    void deleteCached();

    @Query("DELETE from Recipe")
    void deleteAll();

    @Query("UPDATE Recipe SET liked = 0 WHERE idMeal = :idMeal")
    void updateRecipeLikeStatus(long idMeal);


}
