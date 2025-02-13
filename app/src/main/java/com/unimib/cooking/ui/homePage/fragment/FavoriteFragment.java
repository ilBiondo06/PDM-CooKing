package com.unimib.cooking.ui.homePage.fragment;

import static android.widget.Toast.makeText;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.unimib.cooking.R;
import com.unimib.cooking.adapter.RecipeAdapter;
import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.repository.recipe.RecipeRepository;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModel;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModelFactory;
import com.unimib.cooking.util.Constants;
import com.unimib.cooking.util.FavoriteOrganizator;
import com.unimib.cooking.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private List<Recipe> recipeList;
    private RecipeAdapter adapter;

    private RecipeViewModel recipeViewModel;
    private RecyclerView recyclerView;
    TextView noRecipesMessage;

    LinearLayout noRecipeLayout;

    boolean firstTime;
    private CircularProgressIndicator circularProgressIndicator;
    private FavoriteOrganizator favoriteOrganizator;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
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

        recipeList = new ArrayList<>();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firstTime = true;

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        noRecipesMessage = view.findViewById(R.id.noRecipesMessage);
        Log.d("FAVORITE_FRAGMENT", "onCreateView: " + noRecipesMessage);
        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        recyclerView = view.findViewById(R.id.recyclerView);
        SearchView searchView = view.findViewById(R.id.searchView);
        noRecipeLayout = view.findViewById(R.id.noRecipesLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new RecipeAdapter(R.layout.card_recipe, recipeList, false, new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onRecipeItemClick(Recipe recipe) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_RECIPE, recipe);

                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_visualize_card, bundle);
            }

            @Override
            public void onFavoriteButtonPressed(int position) {
                // Handle favorite button press
            }

            @Override
            public void onRemoveFromFavorites(int position) {
                if (position >= 0 && position < recipeList.size()) {
                    // Rimuovi l'elemento dalla lista
                    Recipe recipe = recipeList.get(position);
                    recipe.setLiked(false);

                    // Aggiorna il database
                    recipeViewModel.updateRecipe(recipe);
                    favoriteOrganizator.updateFavoriteRecipe(recipe, false);
                    recipeList.remove(position);
                    // Notifica l'adapter
                    adapter.notifyItemRemoved(position);

                    // Mostra il messaggio
                    makeText(getContext(), R.string.recipe_removed_from_favourite, Toast.LENGTH_SHORT).show();
                } else {
                    makeText(getContext(), R.string.not_valid_index, Toast.LENGTH_SHORT).show();
                }
            }


        });

        recyclerView.setAdapter(adapter);

        recipeViewModel.getFavoriteRecipesLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                this.recipeList.clear();
                this.recipeList.addAll(((Result.RecipeSuccess) result).getData().getRecipes());

                adapter.notifyDataSetChanged();

                if (recipeList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    noRecipeLayout.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noRecipeLayout.setVisibility(View.GONE);
                }

                circularProgressIndicator.setVisibility(View.GONE);
            } else {
                Snackbar.make(view, "error", Snackbar.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        return view;
    }

    private void filterList(String text) {

        ArrayList<Recipe> filteredList = new ArrayList<>();

        for (Recipe recipe : recipeList) {
            if (recipe.getStrMeal().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(recipe);
            }
        }

        if(text.isEmpty() && filteredList.isEmpty()){
            adapter.setFilteredList(recipeList);
            return;
        }

        if (filteredList.isEmpty()) {
            noRecipeLayout.setVisibility(View.VISIBLE);
        }else{
            noRecipeLayout.setVisibility(View.GONE);
        }
        adapter.setFilteredList(filteredList);

    }
}