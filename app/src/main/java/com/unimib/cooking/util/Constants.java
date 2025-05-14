package com.unimib.cooking.util;

import android.content.Context;

import com.unimib.cooking.R;
import com.unimib.cooking.model.Category;
import com.unimib.cooking.model.Country;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final int DATABASE_VERSION = 1;

    public static final int MINIMUM_LENGTH_PASSWORD = 8;
    public static final String SAVED_RECIPES_DATABASE = "saved_datab";
    public static final String SEARCH_ENDPOINT = "search.php";
    public static final String FILTER_ENDPOINT = "filter.php";
    public static final String RANDOM_ENDPOINT = "random.php";
    public static final String ID_ENDPOINT = "lookup.php";

    public static final String REMOVED_RECIPE_STR_MEAL = "[Removed]";

    public static final String RECIPES_API_BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    public static final int FRESH_TIMEOUT = 1000 * 60; // 1 minute in milliseconds
    public static final long RANDOM_REFRESH = 24*60*60*1000; //24 hours in milliseconds

    public static final String SHARED_PREFERENCES_FILENAME = "com.unimib.cooking.preferences";
    public static final String SHARED_PREFERNECES_LAST_UPDATE = "last_update";

    public static final String BUNDLE_KEY_CURRENT_RECIPE = "current_article";

    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";

    public static final String FIREBASE_REALTIME_DATABASE = "https://pilotpdm-b7cfd-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_FAVORITE_NEWS_COLLECTION = "favoriteRecipes";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

    public static final String SHARED_PREFERENCES_SELECTED_FILTER = "selected_filter";
    public static final String REALTIME_DATABASE = "https://cooking-ac351-default-rtdb.europe-west1.firebasedatabase.app";

    public static List<Country> generateCountryList(Context context) {
        List<Country> countryList = new ArrayList<>();

        countryList.add(new Country("American", R.drawable.ic_usa));
        countryList.add(new Country("British", R.drawable.ic_britainn));
        countryList.add(new Country("Canadian", R.drawable.ic_canadian));
        countryList.add(new Country("Chinese", R.drawable.ic_china));
        countryList.add(new Country("Croatian", R.drawable.ic_croatian));
        countryList.add(new Country("Dutch", R.drawable.ic_netherlands));
        countryList.add(new Country("Egyptian", R.drawable.ic_egyptian));
        countryList.add(new Country("Filipino", R.drawable.ic_filippine));
        countryList.add(new Country("French", R.drawable.ic_france));
        countryList.add(new Country("Greek", R.drawable.ic_greece));
        countryList.add(new Country("Indian", R.drawable.ic_india));
        countryList.add(new Country("Irish", R.drawable.ic_ireland));
        countryList.add(new Country("Italian", R.drawable.ic_italian));
        countryList.add(new Country("Jamaican", R.drawable.ic_jamaican));
        countryList.add(new Country("Japanese", R.drawable.ic_japanese));
        countryList.add(new Country("Kenyan", R.drawable.ic_kenya));
        countryList.add(new Country("Malaysian", R.drawable.ic_malesyan));
        countryList.add(new Country("Mexican", R.drawable.ic_mexico));
        countryList.add(new Country("Moroccan", R.drawable.ic_marocco));
        countryList.add(new Country("Polish", R.drawable.ic_polonia));
        countryList.add(new Country("Portuguese", R.drawable.ic_portugese));
        countryList.add(new Country("Russian", R.drawable.ic_russia));
        countryList.add(new Country("Spanish", R.drawable.ic_spain));
        countryList.add(new Country("Thai", R.drawable.ic_thay));
        countryList.add(new Country("Tunisian", R.drawable.ic_tunisian));
        countryList.add(new Country("Turkish", R.drawable.ic_turkish));
        countryList.add(new Country("Ukrainian", R.drawable.ic_ucrenian));
        countryList.add(new Country("Vietnamese", R.drawable.ic_vietnam));
        countryList.add(new Country("Unknown", R.drawable.ic_origin));
        // Aggiungi altri paesi...

        return countryList;
    }

    public static List<Category> generateCategoryList(Context context) {
        List<Category> categoryList = new ArrayList<>();

        // Aggiungi le categorie con le rispettive icone
        categoryList.add(new Category("Beef", R.drawable.ic_beef));
        categoryList.add(new Category("Breakfast", R.drawable.ic_breakfast));
        categoryList.add(new Category("Chicken", R.drawable.ic_chicken));
        categoryList.add(new Category("Dessert", R.drawable.ic_dessert));
        categoryList.add(new Category("Goat", R.drawable.ic_goat));
        categoryList.add(new Category("Lamb", R.drawable.ic_lamb));
        categoryList.add(new Category("Miscellaneous", R.drawable.ic_miscellaneous));
        categoryList.add(new Category("Pasta", R.drawable.ic_pasta));
        categoryList.add(new Category("Pork", R.drawable.ic_pork));
        categoryList.add(new Category("Seafood", R.drawable.ic_seafood));
        categoryList.add(new Category("Side", R.drawable.ic_side_dish));
        categoryList.add(new Category("Starter", R.drawable.ic_starter));
        categoryList.add(new Category("Vegan", R.drawable.ic_vegan));
        categoryList.add(new Category("Vegetarian", R.drawable.ic_vegetarian));

        return categoryList;
    }

}
