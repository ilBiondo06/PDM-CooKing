package com.unimib.cooking.util;

import android.content.Context;
import com.unimib.cooking.model.RecipeAPIResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.Gson;

public class JSONParserUtils {
    public Context context;

    public JSONParserUtils(Context context) {
        this.context = context;
    }


    public RecipeAPIResponse parseJSONFileWithGSon(String filename) throws IOException {
        InputStream inputStream = context.getAssets().open(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return new Gson().fromJson(bufferedReader, RecipeAPIResponse.class);
    }

}
