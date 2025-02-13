package com.unimib.cooking.util;

import static com.unimib.cooking.util.Constants.REALTIME_DATABASE;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModel;
import java.util.ArrayList;
import java.util.List;

public class FavoriteOrganizator {

    private static FavoriteOrganizator instance;
    private List<Recipe> favoriteRecipes;
    private static final String TAG = "FavoriteOrganizator";
    //private RecipeRepository recipeRepository;
    private RecipeViewModel recipeViewModel;


    private FavoriteOrganizator(RecipeViewModel recipeViewModel) {
        favoriteRecipes = new ArrayList<>();
        this.recipeViewModel = recipeViewModel;
        recipeViewModel.getFavoriteRecipesLiveData().observeForever(new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result.isSuccess()) {
                    favoriteRecipes.addAll(((Result.RecipeSuccess) result).getData().getRecipes());
                    Log.d(TAG, "Lista preferiti caricata porca di quella maledetta: " + favoriteRecipes.size());
                    if(!favoriteRecipes.isEmpty())loadFavoriteRecipes();
                } else {
                    Log.e(TAG, "Errore nel caricamento delle ricette preferite");
                }

                // 🔥 Rimuove l'osservatore dopo il primo aggiornamento
                recipeViewModel.getFavoriteRecipesLiveData().removeObserver(this);
            }
        });
        //recipeRepository.deleteFavoriteRecipes();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d(TAG, "Utente corrente: " + user.getEmail());
            String uid = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance(REALTIME_DATABASE).getReference("favoriteRecipes").child(uid);


            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //clearOldFavorites();
                    List<Recipe> deleteRecipes = new ArrayList<>(favoriteRecipes);

                    favoriteRecipes.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = snapshot.getValue(Recipe.class);
                        recipeViewModel.updateRecipe(recipe);
                        favoriteRecipes.add(recipe);
                        deleteRecipes.remove(recipe);
                        Log.d(TAG, "Ricetta dell'utente" + recipe.getStrMeal());
                    }

                    //recipeViewModel.insertRecipes(favoriteRecipes);

                    /*for(Recipe recipe : favoriteRecipes){
                        recipe.setLiked(true);
                        recipeViewModel.updateRecipe(recipe);
                    }*/

                    for(Recipe recipe : deleteRecipes){
                        recipe.setLiked(false);
                        recipeViewModel.updateRecipe(recipe);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "❌ Errore nel caricamento", databaseError.toException());
                }
            });
        }else{
            Log.d(TAG, "Utente non loggato");
        }

    }

    public static FavoriteOrganizator getInstance(RecipeViewModel recipeViewModel) {
        if (instance == null) {
            Log.d(TAG, "Creazione di un'istanza di FavoriteOrganizator");
            instance = new FavoriteOrganizator(recipeViewModel);
        }
        return instance;
    }

    public void addFavoriteRecipe(Recipe recipe) {
        favoriteRecipes.add(recipe);
    }

    public void removeFavoriteRecipe(Recipe recipe) {
        favoriteRecipes.remove(recipe);


    }

    public List<Recipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(List<Recipe> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    public void updateFavoriteRecipe(Recipe recipe, boolean isFavorite) {
        if (isFavorite) {
            addFavoriteRecipe(recipe);
            Log.d("FavoriteOrganizator", "Added recipe to favorites: " + recipe.getStrMeal());
        } else {
            Log.d("FavoriteOrganizator", "Remove recipe to favorites: " + recipe.getStrMeal());
            removeFavoriteRecipe(recipe);
        }

        stampaLista();
        loadFavoriteRecipes();

    }

    public void deleteAllFavorites() {
        favoriteRecipes.clear();
        loadFavoriteRecipes();
    }

    private void loadFavoriteRecipes(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance(REALTIME_DATABASE).getReference("favoriteRecipes").child(uid);
            myRef.removeValue();

            for (Recipe recipe : favoriteRecipes) {
                myRef.child(String.valueOf(recipe.getIdMeal())).setValue(recipe)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "✅ Ricetta salvata per " + uid + ": " + recipe.getStrMeal()))
                        .addOnFailureListener(e -> Log.e(TAG, "❌ Errore nel salvataggio", e));
            }
        }
    }

    public void stampaLista(){
        for(Recipe recipe : favoriteRecipes){
            Log.d(TAG, "Ricetta nella lista dei preferiti: " + recipe.getStrMeal());
        }
    }

    public void clearOldFavorites() {
        recipeViewModel.deleteAllFavoriteRecipes();
    }

    public static void destroy(){
        instance = null;
    }

    public boolean isFavorite(Recipe recipe){
        return favoriteRecipes.contains(recipe);
    }

}