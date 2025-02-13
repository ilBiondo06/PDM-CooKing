package com.unimib.cooking.repository.user;

import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.User;

import java.util.List;

public interface UserResponseCallback {

    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(List<Recipe> recipesList);
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();

}
