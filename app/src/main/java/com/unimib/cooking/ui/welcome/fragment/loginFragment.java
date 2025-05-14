package com.unimib.cooking.ui.welcome.fragment;

import static android.widget.Toast.makeText;
import static com.unimib.cooking.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.unimib.cooking.util.Constants.INVALID_USER_ERROR;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.cooking.R;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.model.User;
import com.unimib.cooking.repository.user.IUserRepository;
import com.unimib.cooking.ui.homePage.activity.MainActivity;
import com.unimib.cooking.ui.welcome.viewmodel.UserViewModel;
import com.unimib.cooking.ui.welcome.viewmodel.UserViewModelFactory;
import com.unimib.cooking.util.ServiceLocator;
import org.apache.commons.validator.routines.EmailValidator;

public class loginFragment extends Fragment {

    public static final String TAG = loginFragment.class.getName();
    private TextInputEditText editTextEmail, editTextPassword;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;
    //private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserSuccess) authenticationResult).getData();
                                //saveLoginData(user.getEmail(), null, user.getIdToken());
                                Log.i(TAG, "Logged as: " + user.getEmail());
                                userViewModel.setAuthenticationError(false);
                                retrieveUserInformationAndStartActivity(user, getView());
                            } else {
                                userViewModel.setAuthenticationError(true);
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.error_unexpected),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retrieveUserInformationAndStartActivity(User user, View view) {
        //progressIndicator.setVisibility(View.VISIBLE);
        goToNextPage(view);
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_email_login);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }

    private void goToNextPage(View view) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (userViewModel.getLoggedUser() != null) {
            goToNextPage(view);
        }

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);
        //Button loginGoogleButton = view.findViewById(R.id.loginButtonGoogle);

        Button resetPasswordButton = view.findViewById(R.id.passwordDimenticata);

        //resetPasswordButton.setOnClickListener(v -> showResetPasswordDialog());


        loginButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                userViewModel.getUserMutableLiveData(email, password, true)
                        .observe(getViewLifecycleOwner(), result -> {
                            if (result instanceof Result.UserSuccess) {
                                // Login riuscito
                                User user = ((Result.UserSuccess) result).getData();
                                Log.d(TAG, "Login successful: " + user.getEmail());
                                goToNextPage(v);
                            } else if (result instanceof Result.Error) {
                                // Gestione errori
                                String error = ((Result.Error) result).getMessage();
                                Log.e(TAG, "Login error: " + error);

                            }
                        });
            }else{
                makeText(getContext(), R.string.riempi_campi, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*private void showResetPasswordDialog() {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RoundedAlertDialog);

        // Creazione del titolo personalizzato
        TextView title = new TextView(context);
        title.setText(R.string.reimposta_password);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 24, 0, 16);

        // Creazione del campo email centrato con padding superiore
        final EditText input = new EditText(context);
        input.setHint(R.string.inserisci_la_tua_email);
        input.setSingleLine(true);
        input.setGravity(Gravity.CENTER);
        input.setPadding(32, 24, 32, 16); // Aggiunto padding superiore di 24dp

        // Imposta margini al campo email per separarlo dal titolo
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                800, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams.setMargins(0, 24, 0, 0); // Margine superiore di 24dp
        input.setLayoutParams(inputParams);

        // Layout contenitore centrato
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);
        layout.setGravity(Gravity.CENTER);
        layout.addView(input);

        // Costruzione dell'AlertDialog
        builder.setCustomTitle(title);
        builder.setView(layout);

        builder.setPositiveButton(R.string.invia, (dialog, which) -> {
            String email = input.getText().toString();
            if (!email.isEmpty()) {
                resetPassword(email);
            } else {
                Toast.makeText(context, R.string.insert_valid_email, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.annulla, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid ->
                        makeText(getContext(), R.string.email_sent, Toast.LENGTH_LONG).show())
                .addOnFailureListener(e ->
                        makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());
    }*/
}