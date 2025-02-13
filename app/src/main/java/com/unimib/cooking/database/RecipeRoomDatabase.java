package com.unimib.cooking.database;

import static com.unimib.cooking.util.Constants.DATABASE_VERSION;

import android.content.Context;

import com.unimib.cooking.util.Constants;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.unimib.cooking.model.Recipe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**

 Main access point for the underlying connection to the local database.
 <a href="https://developer.android.com/reference/kotlin/androidx/room/Database">...</a>
 */
@Database(entities = {Recipe.class}, version = DATABASE_VERSION, exportSchema = true)
public abstract class RecipeRoomDatabase extends RoomDatabase {
    public abstract RecipeDAO recipeDao();

    private static volatile RecipeRoomDatabase INSTANCE;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static RecipeRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecipeRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RecipeRoomDatabase.class, Constants.SAVED_RECIPES_DATABASE)
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
