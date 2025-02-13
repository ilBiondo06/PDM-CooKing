package com.unimib.cooking.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.cooking.model.Result;
import com.unimib.cooking.model.User;

public interface IUserRepository {

    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getUserFavoriteNews(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);

}
