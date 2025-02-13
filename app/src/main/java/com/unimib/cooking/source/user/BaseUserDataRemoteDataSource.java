package com.unimib.cooking.source.user;

import com.unimib.cooking.model.User;
import com.unimib.cooking.repository.user.UserResponseCallback;

import java.util.Set;

public abstract class BaseUserDataRemoteDataSource {
        protected UserResponseCallback userResponseCallback;

        public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
            this.userResponseCallback = userResponseCallback;
        }

        public abstract void saveUserData(User user);

        public abstract void getUserFavoriteRecipes(String idToken);

    }

