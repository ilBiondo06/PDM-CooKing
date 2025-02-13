package com.unimib.cooking.ui.homePage.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.bumptech.glide.Glide;
import com.unimib.cooking.R;
import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.repository.recipe.RecipeRepository;
import com.unimib.cooking.util.Constants;
import com.unimib.cooking.util.RecipeOrganizator;

import java.util.ArrayList;

public class RecipeVisualizeFragment extends Fragment {

    private static final String TAG = RecipeRepository.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_visualize, container, false);

        // Enable the back button in the ActionBar
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Collega gli elementi del layout
        ImageView recipeImage = view.findViewById(R.id.recipe_image);
        TextView recipeTitle = view.findViewById(R.id.recipe_title);
        TextView recipeCategory = view.findViewById(R.id.recipe_category);
        GridLayout ingredientsContainer = view.findViewById(R.id.ingredient_container);
        TextView recipeInstructions = view.findViewById(R.id.recipe_procedure);
        TextView recipeOrigin = view.findViewById(R.id.recipe_origin);
        Button tutorialButton = view.findViewById(R.id.button_tutorial);
        Button sourceButton = view.findViewById(R.id.button_source);

        // Recupera i dati dal Bundle
        Bundle arguments = getArguments();
        assert arguments != null;
        Recipe recipe = arguments.getParcelable(Constants.BUNDLE_KEY_CURRENT_RECIPE);
        if (recipe != null) {
            String title = recipe.getStrMeal();
            String category = recipe.getStrCategory();


            ArrayList<String> ingredients = RecipeOrganizator.getIngredients(recipe);
            ArrayList<String> measures = RecipeOrganizator.getMeasures(recipe);

            for (int i = 0; i < ingredients.size(); i++) {

                if(ingredients.get(i)==null || ingredients.get(i).isEmpty()){
                    break;
                }
                // Inflate il layout predefinito per l'ingrediente
                TextView ingredientTextView = (TextView) LayoutInflater.from(getContext())
                        .inflate(R.layout.ingredient_measure_view, null);
                ingredientTextView.setText(ingredients.get(i));

                // Inflate il layout predefinito per la misura
                TextView measureTextView = (TextView) LayoutInflater.from(getContext())
                        .inflate(R.layout.ingredient_measure_view, null);
                measureTextView.setText(measures.get(i));

                // Aggiungere le TextView al GridLayout
                GridLayout.LayoutParams ingredientParams = new GridLayout.LayoutParams();
                ingredientParams.columnSpec = GridLayout.spec(0, 1, 1f); // Colonna 0
                ingredientParams.width = 0; // Usare il peso per la larghezza
                ingredientTextView.setLayoutParams(ingredientParams);
                ingredientsContainer.addView(ingredientTextView);

                GridLayout.LayoutParams measureParams = new GridLayout.LayoutParams();
                measureParams.columnSpec = GridLayout.spec(1, 1, 1f); // Colonna 1
                measureParams.width = 0; // Usare il peso per la larghezza
                measureTextView.setLayoutParams(measureParams);
                ingredientsContainer.addView(measureTextView);
            }

            String instructions = recipe.getStrInstructions();
            String origin = recipe.getStrArea();
            String imageUrl = recipe.getStrMealThumb(); // metodi get per recuperare i dati
            String tutorialUrl = recipe.getStrYoutube(); // URL del tutorial
            String sourceUrl = recipe.getStrSource(); // URL della fonte

            // Popola i campi del layout con i dati
            recipeTitle.setText(title);
            recipeCategory.setText(category);
            recipeInstructions.setText(instructions);
            recipeOrigin.setText(origin);

            // Usa Glide per caricare l'immagine
            Context context = getContext()  ;
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(new ColorDrawable(context.getColor(R.color.md_theme_onSurfaceVariant_mediumContrast)))
                    .into(recipeImage);


            // Imposta il click listener per il pulsante tutorial
            tutorialButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tutorialUrl));
                startActivity(intent);
            });

            // Imposta il click listener per il pulsante source
            sourceButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl));
                startActivity(intent);
            });
        }

        return view;
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