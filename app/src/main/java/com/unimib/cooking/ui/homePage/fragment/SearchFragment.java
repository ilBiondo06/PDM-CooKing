package com.unimib.cooking.ui.homePage.fragment;

import static android.view.View.INVISIBLE;
import static android.widget.Toast.makeText;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unimib.cooking.R;
import com.unimib.cooking.adapter.CategoryAdapter;
import com.unimib.cooking.adapter.RecipeAdapter;
import com.unimib.cooking.model.Category;
import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.repository.recipe.RecipeRepository;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModel;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModelFactory;
import com.unimib.cooking.util.Constants;
import com.unimib.cooking.util.FavoriteOrganizator;
import com.unimib.cooking.util.NetworkUtil;
import com.unimib.cooking.util.ServiceLocator;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<Recipe> recipes;
    private RecipeViewModel recipeViewModel;
    private RecipeAdapter recipeAdapter;
    private View rootView;
    RecyclerView ricetteView;
    RecyclerView areaRecyclerView, categoryRecyclerView;
    private boolean firstTime;
    private FavoriteOrganizator favoriteOrganizator;


    public SearchFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecipeRepository articleRepository =
                ServiceLocator.getInstance().getRecipesRepository(
                        requireActivity().getApplication()
                );

        recipeViewModel = new ViewModelProvider(
                requireActivity(),
                new RecipeViewModelFactory(articleRepository)).get(RecipeViewModel.class);

        favoriteOrganizator = FavoriteOrganizator.getInstance(recipeViewModel);

        recipes = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        areaRecyclerView = rootView.findViewById(R.id.areaRecyclerView);
        ricetteView = rootView.findViewById(R.id.ricetteView);
        categoryRecyclerView = rootView.findViewById(R.id.categoryRecyclerView);

        areaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ricetteView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        for (int i = 0; i < 1; i++) recipes.add(Recipe.getSampleRecipe());

        //DISCOVER PRESENTE MA INVIISIBILE
        areaRecyclerView.setVisibility(INVISIBLE);
        categoryRecyclerView.setVisibility(INVISIBLE);


        // Imposta RecipeAdapter
        recipeAdapter =
                new RecipeAdapter(R.layout.card_recipe, recipes, true,
                        new RecipeAdapter.OnItemClickListener() {
                            @Override
                            public void onRecipeItemClick(Recipe recipe) {

                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_RECIPE, recipe);

                                Navigation.findNavController(rootView).navigate(R.id.action_searchFragment_to_visualize_card, bundle);
                            }

                            @Override
                            public void onFavoriteButtonPressed(int position) {
                                recipes.get(position).setLiked(!recipes.get(position).getLiked());
                                recipeViewModel.updateRecipe(recipes.get(position));
                                favoriteOrganizator.updateFavoriteRecipe(recipes.get(position), recipes.get(position).getLiked());
                            }

                            @Override
                            public void onRemoveFromFavorites(int position) {
                                // Gestisci la rimozione dai preferiti
                                Recipe recipe = recipes.get(position);
                                recipe.setLiked(false);  // Imposta la ricetta come non preferita
                                recipeViewModel.updateRecipe(recipe);
                                favoriteOrganizator.updateFavoriteRecipe(recipe, false);
                            }
                        });
        ricetteView.setAdapter(recipeAdapter);


        SearchView searchView = rootView.findViewById(R.id.searchView);

        // Adattatore per categoria
        List<Category> categoryList = Constants.generateCategoryList(getContext());
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categoryList, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category category) {
                Bundle bundle = new Bundle();
                bundle.putString("category_name", category.getName());
                //recipeViewModel.clearRecipes();

                // Navigazione al fragment_category_recipe
                Navigation.findNavController(rootView).navigate(R.id.action_searchFragment_to_categoryRecipesFragment, bundle);

            }
        });
        categoryRecyclerView.setAdapter(categoryAdapter);

        //recipeAdapter.updateData(recipes);

        SharedPreferences prefs = requireContext().getSharedPreferences("SearchFragmentPrefs", Context.MODE_PRIVATE);
        long lastUpdate = prefs.getLong("last_fetch_time", 0);
        long lastRecipeId = prefs.getLong("last_recipe_id", -1);
        boolean firstTime = prefs.getBoolean("firstTime", true);
        long currentTime = System.currentTimeMillis();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Bundle bundle = new Bundle();
                bundle.putString("category_name", query);

                // Navigazione al fragment_area_recipe con il bundle
                NavController navController = NavHostFragment.findNavController(SearchFragment.this);
                if (navController.getCurrentDestination().getId() == R.id.searchFragment) {
                    navController.navigate(R.id.action_searchFragment_to_categoryRecipesFragment, bundle);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        // Controllo se bisogna aggiornare la ricetta o prendere l'ultima salvata
        if (currentTime - lastUpdate >= Constants.RANDOM_REFRESH || firstTime || lastRecipeId == -1 ) {
            // aggiorno la ricetta
            if (!NetworkUtil.isInternetAvailable(getContext())) {
                lastUpdate = System.currentTimeMillis();
            }

            recipeViewModel.getRandomRecipe(lastUpdate).observe(getViewLifecycleOwner(), result -> {
                if (result.isSuccess()) {
                    this.recipes.clear();
                    this.recipes.addAll(((Result.RecipeSuccess) result).getData().getRecipes());
                    recipeAdapter.notifyDataSetChanged();

                    // Salvo ID della ricetta e il tempo dell'ultimo aggiornamento
                    if(!recipes.isEmpty()){
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("last_fetch_time", currentTime);
                        editor.putLong("last_recipe_id", recipes.get(0).getIdMeal()); // Ora salviamo un long
                        editor.putBoolean("firstTime", false);
                        editor.apply();
                    }

                } else {
                    makeText(getContext(), R.string.random_recipe_failure, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            lastRecipeId = prefs.getLong("last_recipe_id", -1);
            if (lastRecipeId != -1) {
                recipeViewModel.getRecipeById(lastRecipeId, lastUpdate).observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        this.recipes.clear();
                        this.recipes.addAll(((Result.RecipeSuccess) result).getData().getRecipes());
                        recipeAdapter.notifyDataSetChanged();
                    } else {
                        makeText(getContext(), R.string.local_recipe_error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                makeText(getContext(), R.string.local_recipe_error_no_saved, Toast.LENGTH_SHORT).show();
            }
        }
        recipeAdapter.updateData(recipes);

        return rootView;
    }

}

