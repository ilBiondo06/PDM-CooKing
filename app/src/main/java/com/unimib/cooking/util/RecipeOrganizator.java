package com.unimib.cooking.util;

import android.util.Log;
import com.unimib.cooking.model.Recipe;
import java.util.ArrayList;
import java.util.List;

public class RecipeOrganizator {

    List <String> ingredienti;
    List<String> misure;


    public static ArrayList<String> getIngredients(Recipe recipe){
        ArrayList <String> ingredienti = new ArrayList<>();

        String[] ingredients = {
                recipe.getStrIngredient1(), recipe.getStrIngredient2(), recipe.getStrIngredient3(),
                recipe.getStrIngredient4(), recipe.getStrIngredient5(), recipe.getStrIngredient6(),
                recipe.getStrIngredient7(), recipe.getStrIngredient8(), recipe.getStrIngredient9(),
                recipe.getStrIngredient10(), recipe.getStrIngredient11(), recipe.getStrIngredient12(),
                recipe.getStrIngredient13(), recipe.getStrIngredient14(), recipe.getStrIngredient15(),
                recipe.getStrIngredient16(), recipe.getStrIngredient17(), recipe.getStrIngredient18(),
                recipe.getStrIngredient19(), recipe.getStrIngredient20()
        };

        for (String ingredient : ingredients) {
            if (ingredient != null) {
                ingredienti.add(ingredient);
            }
        }

        return ingredienti;

    }

    public static String getIngredientsString(Recipe recipe){
        ArrayList <String> ingredienti = RecipeOrganizator.getIngredients(recipe);

        String ingredientiString = "";

        for (int i = 0; i < ingredienti.size(); i++) {
            ingredientiString = ingredientiString + ingredienti.get(i) + "\n"; // Concatenazione con un salto di riga
        }

        return ingredientiString;
    }

    public static String getMeasuresString(Recipe recipe){
        ArrayList <String> misure = RecipeOrganizator.getMeasures(recipe);

        String measuresString = "";

        for (int i = 0; i < misure.size(); i++) {
            measuresString = measuresString + misure.get(i) + "\n"; // Concatenazione con un salto di riga
        }

        return measuresString;
    }

    public static ArrayList<String> getMeasures(Recipe recipe){
        ArrayList <String> misure = new ArrayList<>();

        String[] measures = {
                recipe.getStrMeasure1(), recipe.getStrMeasure2(), recipe.getStrMeasure3(),
                recipe.getStrMeasure4(), recipe.getStrMeasure5(), recipe.getStrMeasure6(),
                recipe.getStrMeasure7(), recipe.getStrMeasure8(), recipe.getStrMeasure9(),
                recipe.getStrMeasure10(), recipe.getStrMeasure11(), recipe.getStrMeasure12(),
                recipe.getStrMeasure13(), recipe.getStrMeasure14(), recipe.getStrMeasure15(),
                recipe.getStrMeasure16(), recipe.getStrMeasure17(), recipe.getStrMeasure18(),
                recipe.getStrMeasure19(), recipe.getStrMeasure20()
        };

        for (String measure : measures) {
            if (measure != null) {
                misure.add(measure);
            }
        }

        return misure;

    }

}
