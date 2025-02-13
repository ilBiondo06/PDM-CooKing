package com.unimib.cooking.ui.welcome.fragment;

import static com.unimib.cooking.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.unimib.cooking.util.Constants.INVALID_USER_ERROR;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.unimib.cooking.R;
import com.unimib.cooking.model.User;
import com.unimib.cooking.repository.user.IUserRepository;
import com.unimib.cooking.ui.homePage.activity.MainActivity;
import com.unimib.cooking.ui.welcome.viewmodel.UserViewModel;
import com.unimib.cooking.ui.welcome.viewmodel.UserViewModelFactory;
import com.unimib.cooking.util.ServiceLocator;
import java.util.concurrent.atomic.AtomicReference;
public class autenticationFragment extends Fragment {

    public static final String TAG = autenticationFragment.class.getName();
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;


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
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if (currentUser != null) {
                                            Log.d(TAG, "Utente attualmente autenticato: " + currentUser.getEmail());
                                            // Apri MainActivity
                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                            intent.putExtra("email", currentUser.getEmail());
                                            intent.putExtra("displayName", currentUser.getDisplayName());
                                            if (currentUser.getPhotoUrl() != null) {
                                                intent.putExtra("profilePicture", currentUser.getPhotoUrl().toString());
                                            }
                                            startActivity(intent);
                                        } else {
                                            Log.e(TAG, "Autenticazione fallita, utente nullo.");
                                        }
                                    } else {
                                        Log.e(TAG, "Autenticazione con Firebase non riuscita: " + task.getException().getMessage());
                                    }
                                });
                    } else {
                        Log.e(TAG, "ID Token nullo, autenticazione fallita.");
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.error_unexpected),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_autentication, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button loginButton = view.findViewById(R.id.accediButton);
        Button registrationButton = view.findViewById(R.id.registratiButton);
        Button googleButton = view.findViewById(R.id.googleButton);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AtomicReference<FirebaseUser> currentUser = new AtomicReference<>(mAuth.getCurrentUser());
        Log.d(TAG, "currentUser: " + currentUser);

        if (userViewModel.getLoggedUser() != null) {
            goToNextPage(view);
        }

        loginButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_autenticationFragment_to_loginFragment);
        });

        registrationButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_autenticationFragment_to_registrationFragment);
        });

        googleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");

                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_unexpected),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));


    }


    private void goToNextPage(View view) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
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

}