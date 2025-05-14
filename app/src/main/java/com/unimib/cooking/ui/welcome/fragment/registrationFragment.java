package com.unimib.cooking.ui.welcome.fragment;

import static android.widget.Toast.makeText;
import static com.unimib.cooking.util.Constants.USER_COLLISION_ERROR;
import static com.unimib.cooking.util.Constants.WEAK_PASSWORD_ERROR;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.unimib.cooking.R;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.model.User;
import com.unimib.cooking.repository.user.IUserRepository;
import com.unimib.cooking.ui.homePage.activity.MainActivity;
import com.unimib.cooking.ui.welcome.viewmodel.UserViewModel;
import com.unimib.cooking.ui.welcome.viewmodel.UserViewModelFactory;
import com.unimib.cooking.util.Constants;
import com.unimib.cooking.util.ServiceLocator;

import org.apache.commons.validator.routines.EmailValidator;


public class registrationFragment extends Fragment {

    public static final String TAG = registrationFragment.class.getName();

    private UserViewModel userViewModel;
    private TextInputEditText textInputEmail, textInputPassword, textNome, textCognome;


    public registrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        textInputEmail = view.findViewById(R.id.email);
        textInputPassword = view.findViewById(R.id.password);
        textNome = view.findViewById(R.id.textNome);
        textCognome = view.findViewById(R.id.textCognome);

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });


        view.findViewById(R.id.BottoneRegistrazione).setOnClickListener(v -> {
            String email = textInputEmail.getText().toString().trim();
            String password = textInputPassword.getText().toString().trim();
            String name = textNome.getText().toString().trim();
            String surname = textCognome.getText().toString().trim();

            //controllo siano presenti nome e cognome
            boolean checkName = !name.isEmpty();
            boolean checkSurname = !surname.isEmpty();
            boolean checkEmail = !email.isEmpty();
            boolean checkPassword = !password.isEmpty();

           if (checkName && checkSurname && checkEmail && checkPassword) {
                //binding.progressBar.setVisibility(View.VISIBLE);
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData(email, password, false).observe(
                            getViewLifecycleOwner(), result -> {
                                Log.d(TAG, "risultato autenticazione: "+result);
                                if (result.isSuccess()) {
                                    User user = ((Result.UserSuccess) result).getData();
                                    Log.d(TAG, "Login successful. User ID: " + user.getIdToken());
                                    //saveLoginData(email, password, user.getIdToken());
                                    userViewModel.setAuthenticationError(false);

                                    Intent intent = new Intent(getContext(), MainActivity.class);

                                    //add name and email in the profile

                                    startActivity(intent);
                                    //requireActivity().finish();

                                } else {
                                    Log.e(TAG, "Login failed: " + ((Result.Error) result).getMessage());
                                    userViewModel.setAuthenticationError(true);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(email, password, false);
                }
                //binding.progressBar.setVisibility(View.GONE);
            } else {
                userViewModel.setAuthenticationError(true);
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.error_email_login, Snackbar.LENGTH_SHORT).show();
            }

        });
        return view;
    }


    private String getErrorMessage(String message) {
        switch(message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_collision_user);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }
}