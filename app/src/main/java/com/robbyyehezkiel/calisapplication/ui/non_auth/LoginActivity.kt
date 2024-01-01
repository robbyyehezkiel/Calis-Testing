package com.robbyyehezkiel.calisapplication.ui.non_auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.data.model.User
import com.robbyyehezkiel.calisapplication.databinding.ActivityLoginBinding
import com.robbyyehezkiel.calisapplication.ui.auth.home.HomeActivity
import com.robbyyehezkiel.calisapplication.ui.auth.user.add.AddUserActivity
import com.robbyyehezkiel.calisapplication.ui.auth.user.view.UserSettingActivity
import com.robbyyehezkiel.calisapplication.ui.main.MainActivity
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction
import kotlinx.coroutines.launch

class LoginActivity : BaseAuthActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeGoogleSignIn()
        setClickListeners()
    }

    private fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let { handleGoogleSignInResult(it) }
                }
            }
    }

    private fun setClickListeners() {
        binding.googleSignIn.setOnClickListener {
            signOutAndSignIn()
        }
        binding.withoutSignIn.setOnClickListener {
            navigateTo(MainActivity::class.java)
        }
    }

    private fun signOutAndSignIn() {
        googleSignInClient.signOut()
            .addOnCompleteListener(this) { signOutTask ->
                if (signOutTask.isSuccessful) {
                    auth.signOut()
                    startGoogleSignIn()
                } else {
                    handleError("Error signing out from Google: ${signOutTask.exception?.message}")
                }
            }
    }

    private fun startGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(data: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let {
                lifecycleScope.launch {
                    firebaseAuthWithGoogle(it)
                }
            }
        } catch (e: ApiException) {
            handleError("Google sign in failed: ${e.message}")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        if (idToken.isBlank()) {
            showSnackBarWithoutAction(binding.root, "Google Sign-In failed. Please try again.")
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { signInTask ->
                if (signInTask.isSuccessful) {
                    handleGoogleSignInSuccess()
                } else {
                    val errorMessage = signInTask.exception?.message ?: "Authentication failed."
                    handleError(
                        "Firebase authentication failed: $errorMessage",
                        signInTask.exception
                    )
                }
            }
    }

    private fun handleGoogleSignInSuccess() {
        lifecycleScope.launch {
            val user = auth.currentUser ?: return@launch

            val userEmail = user.email
            if (userEmail.isNullOrBlank()) {
                showSnackBarWithoutAction(binding.root, "User email is blank.")
                return@launch
            }

            val users = userRepository.getUsersByEmail(userEmail)
            when {
                users.size == 1 -> handleSingleUserSignIn(users[0])
                isUserDataAvailableInDataStore() -> navigateTo(HomeActivity::class.java)
                else -> handleUserDataAvailability(
                    userEmail,
                    UserSettingActivity::class.java,
                    AddUserActivity::class.java
                )
            }
        }
    }

    private suspend fun handleSingleUserSignIn(loggedInUser: User) {
        saveUserToFireStore(loggedInUser)
        userRepository.saveUserSettings(loggedInUser)
        navigateTo(HomeActivity::class.java)
    }

    private suspend fun saveUserToFireStore(user: User) {
        userRepository.updateUserInFireStore(user)
    }
}
