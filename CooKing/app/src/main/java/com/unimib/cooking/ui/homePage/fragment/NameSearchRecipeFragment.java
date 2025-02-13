package com.unimib.cooking.ui.homePage.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.android.material.snackbar.Snackbar;
import com.unimib.cooking.R;
import com.unimib.cooking.adapter.RecipeAdapter;
import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.repository.recipe.RecipeRepository;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModel;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModelFactory;
import com.unimib.cooking.util.Constants;
import com.unimib.cooking.util.NetworkUtil;
import com.unimib.cooking.util.ServiceLocator;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NameSearchRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NameSearchRecipeFragment extends Fragment {
    private RecipeAdapter recipeAdapter;
    private RecipeViewModel recipeViewModel;
    private ArrayList<Recipe> recipeList;
    private RecyclerView recyclerView;
    private LinearLayout noRecipesLayout;


    public NameSearchRecipeFragment() {
        // Required empty public constructor
    }

    public static NameSearchRecipeFragment newInstance() {
        NameSearchRecipeFragment fragment = new NameSearchRecipeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Ensure this is called to enable the menu


        RecipeRepository articleRepository =
                ServiceLocator.getInstance().getRecipesRepository(
                        requireActivity().getApplication()
                );

        recipeViewModel = new ViewModelProvider(
                requireActivity(),
                new RecipeViewModelFactory(articleRepository)).get(RecipeViewModel.class);

        recipeList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_recipe, container, false);
        // Ricezione del nome del country dal Bundle
        String categoryName = getArguments().getString("category_name");
        noRecipesLayout = rootView.findViewById(R.id.noRecipesLayout);
        recyclerView = rootView.findViewById(R.id.ricetteView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        recipeList = new ArrayList<>();
        for (int i = 0; i < 5; i++) recipeList.add(Recipe.getSampleRecipe());

        recipeAdapter =
                new RecipeAdapter(R.layout.card_recipe, recipeList, true,
                        new RecipeAdapter.OnItemClickListener() {
                            @Override
                            public void onRecipeItemClick(Recipe recipe) {

                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_RECIPE, recipe);

                                Navigation.findNavController(rootView).navigate(R.id.action_searchFragment_to_visualize_card, bundle);
                            }

                            @Override
                            public void onFavoriteButtonPressed(int position) {
                                recipeList.get(position).setLiked(!recipeList.get(position).getLiked());
                                recipeViewModel.updateRecipe(recipeList.get(position));
                            }

                            @Override
                            public void onRemoveFromFavorites(int position) {
                                // Rimuovi la ricetta dalla lista dei preferiti
                                Recipe recipe = recipeList.get(position);
                                recipe.setLiked(false);  // Imposta la ricetta come non preferita
                                Log.d("AreaRecipeFragment", "false: "+ recipeList.get(position).getStrMeal());
                                recipeViewModel.updateRecipe(recipe);
                            }
                        });

        recyclerView.setAdapter(recipeAdapter);

        String lastUpdate = "0";

        if (!NetworkUtil.isInternetAvailable(getContext())) {
            lastUpdate = System.currentTimeMillis() + "";
        }

        recipeViewModel.getRecipesByName(categoryName ,Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        this.recipeList.clear();
                        this.recipeList.addAll(((Result.RecipeSuccess) result).getData().getRecipes());
                        recipeAdapter.notifyDataSetChanged();

                        if (recipeList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            noRecipesLayout.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecipesLayout.setVisibility(View.GONE);
                        }


                    } else {
                        Snackbar.make(getView(), getString(R.string.noInternetMessage), Snackbar.LENGTH_SHORT).show();
                    }
                });


        recipeAdapter.updateData(recipeList);


        return rootView;
    }

        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavHostFragment.findNavController(this).navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}