package com.unimib.cooking.ui.homePage.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.unimib.cooking.R;
import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.repository.recipe.RecipeRepository;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModel;
import com.unimib.cooking.ui.homePage.viewmodel.RecipeViewModelFactory;
import com.unimib.cooking.ui.welcome.activity.WelcomeActivity;
import com.unimib.cooking.util.FavoriteOrganizator;
import com.unimib.cooking.util.ServiceLocator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileLocation;
    private Button deleteProfileButton;
    private Button deleteFavouritesButton;
    private Button logoutButton;
    private Button exportFavouritesButton;
    private RecipeViewModel recipeViewModel;
    private List<Recipe> recipeList;
    private SwitchMaterial darkModeSwitch;
    private boolean darkMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FavoriteOrganizator favoriteOrganizator;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initializing views from the XML layout
        profileImage = rootView.findViewById(R.id.profile_image);
        profileName = rootView.findViewById(R.id.profile_name);
        profileLocation = rootView.findViewById(R.id.profile_location);
        deleteProfileButton = rootView.findViewById(R.id.delete_profile_button);
        deleteFavouritesButton = rootView.findViewById(R.id.delete_favourites_button);
        darkModeSwitch = rootView.findViewById(R.id.dark_mode_switch);
        logoutButton = rootView.findViewById(R.id.logout_button);
        exportFavouritesButton = rootView.findViewById(R.id.export_favourites_button);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            profileName.setText(currentUser.getDisplayName());
            profileLocation.setText(currentUser.getEmail());
        }


        sharedPreferences = requireContext().getSharedPreferences("MODE",Context.MODE_PRIVATE);
        darkMODE = sharedPreferences.getBoolean("dark_mode", false);

        if(darkMODE){
            darkModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(darkMODE){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("dark_mode", false);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("dark_mode", true);
                }
                editor.apply();
            }
        });

        logoutButton.setOnClickListener(v -> logoutUser());

        exportFavouritesButton.setOnClickListener(v -> exportFavourites());

        // Set up listeners for buttons
        deleteProfileButton.setOnClickListener(v -> {
            deleteFavourites();
            deleteProfile();
        });

        deleteFavouritesButton.setOnClickListener(v -> {
            deleteFavourites();
        });

        return rootView;
    }

    private void deleteProfile() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.conferma_eliminazione)
                .setMessage(R.string.sicura_eliminazione)
                .setPositiveButton(R.string.elimina_conferma, (dialog, which) -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        Snackbar.make(requireView(), R.string.profilo_eliminato_con_successo, Snackbar.LENGTH_SHORT).show();
                                        requireActivity().finish();  // Chiudi l'app o reindirizza
                                    } else {
                                        Snackbar.make(requireView(), R.string.errore_eliminazione_del_profilo, Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    private void deleteFavourites() {
        recipeViewModel.getFavoriteRecipesLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                this.recipeList.clear();
                this.recipeList.addAll(((Result.RecipeSuccess) result).getData().getRecipes());


                for (Recipe recipe : this.recipeList) {

                    recipe.setLiked(false);
                    recipeViewModel.updateRecipe(recipe);
                }

                favoriteOrganizator.deleteAllFavorites();

                Snackbar.make(requireView(), R.string.preferiti_eliminate_con_successo, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(requireView(), R.string.errore_eliminazione_preferiti, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        favoriteOrganizator.clearOldFavorites();
        FavoriteOrganizator.destroy();

        // Avvia la WelcomeActivity e rimuove le altre attività dalla cronologia
        Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish(); // Chiude l'attuale Activity
    }

    private void exportFavourites() {
        recipeViewModel.getFavoriteRecipesLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                List<Recipe> favoriteRecipes = ((Result.RecipeSuccess) result).getData().getRecipes(); //CONTROLLARE QUI

                if (favoriteRecipes.isEmpty()) {
                    Snackbar.make(requireView(), R.string.nessuna_ricetta_preferita_da_esportare, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String fileName = "ricette_preferite.json";
                File file = new File(requireContext().getExternalFilesDir(null), fileName);

                try (FileWriter writer = new FileWriter(file)) {
                    Gson gson = new Gson();
                    writer.write(gson.toJson(favoriteRecipes));

                    Snackbar.make(requireView(), R.string.esportazione_completata, Snackbar.LENGTH_SHORT).show();

                    shareFile(file);
                } catch (IOException e) {
                    Snackbar.make(requireView(), R.string.errore_durante_l_esportazione, Snackbar.LENGTH_SHORT).show();

                }
            } else {
                Snackbar.make(requireView(), R.string.errore_nel_recupero_delle_ricette, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void shareFile(File file) {
        Uri uri = FileProvider.getUriForFile(requireContext(), "com.unimib.cooking.fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/json");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, getString(R.string.condividi_le_tue_ricette_preferite)));
    }

}
