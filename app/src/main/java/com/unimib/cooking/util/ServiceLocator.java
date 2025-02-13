package com.unimib.cooking.util;


import android.app.Application;

import com.unimib.cooking.database.RecipeRoomDatabase;
import com.unimib.cooking.repository.recipe.RecipeRepository;
import com.unimib.cooking.repository.user.IUserRepository;
import com.unimib.cooking.repository.user.UserRepository;
import com.unimib.cooking.service.RecipeApiService;
import com.unimib.cooking.source.recipe.BaseRecipeLocalDataSource;
import com.unimib.cooking.source.recipe.BaseRecipeRemoteDataSource;
import com.unimib.cooking.source.recipe.RecipeLocalDataSource;
import com.unimib.cooking.source.recipe.RecipeRemoteDataSource;
import com.unimib.cooking.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.cooking.source.user.BaseUserDataRemoteDataSource;
import com.unimib.cooking.source.user.UserAuthenticationFirebaseDataSource;
import com.unimib.cooking.source.user.UserFirebaseDataSource;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                return chain.proceed(request);
            })
            .build();

    public RecipeApiService getRecipeAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.RECIPES_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(RecipeApiService.class);
    }

    public RecipeRoomDatabase getRecipesDao(Application application) {
        return RecipeRoomDatabase.getDatabase(application);
    }

    /**
     * Returns an instance of INewsRepositoryWithLiveData.
     * @param application Param for accessing the global application state.
     * @return An instance of INewsRepositoryWithLiveData.
     */
    public RecipeRepository getRecipesRepository(Application application) {
        BaseRecipeRemoteDataSource recipesRemoteDataSource;
        BaseRecipeLocalDataSource recipesLocalDataSource;
        SharedPreferencesUtils sharedPreferencesUtil = new SharedPreferencesUtils(application);

        recipesRemoteDataSource = new RecipeRemoteDataSource();

        recipesLocalDataSource = new RecipeLocalDataSource(getRecipesDao(application), sharedPreferencesUtil);

        return new RecipeRepository(recipesRemoteDataSource, recipesLocalDataSource);
    }

    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtils sharedPreferencesUtil = new SharedPreferencesUtils(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationFirebaseDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserFirebaseDataSource(sharedPreferencesUtil);

        BaseRecipeLocalDataSource newsLocalDataSource =
                new RecipeLocalDataSource(getRecipesDao(application), sharedPreferencesUtil);

        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource, newsLocalDataSource);
    }
}
