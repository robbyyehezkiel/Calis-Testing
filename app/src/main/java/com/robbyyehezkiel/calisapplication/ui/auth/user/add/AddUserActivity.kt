package com.robbyyehezkiel.calisapplication.ui.auth.user.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.SettingPreferences
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.dataStore
import com.robbyyehezkiel.calisapplication.databinding.ActivityAddUserBinding
import com.robbyyehezkiel.calisapplication.ui.auth.home.HomeActivity
import com.robbyyehezkiel.calisapplication.ui.utils.LoadingState
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private val viewModel: AddUserViewModel by viewModels {
        AddUserViewModelFactory(
            application,
            UserRepository(SettingPreferences.getInstance(application.dataStore)),
            FirebaseFirestore.getInstance()
        )
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setLoadingState(false)

        auth = FirebaseAuth.getInstance()

        viewModel.loadingState.observe(this) { loadingState ->
            when (loadingState) {
                is LoadingState.Loading -> setLoadingState(true)
                is LoadingState.Success -> setLoadingState(false)
                is LoadingState.Error -> {
                    setLoadingState(false)
                    showSnackBarWithoutAction(binding.root, loadingState.message)
                }
            }
        }

        binding.continueButton.setOnClickListener {
            val userEmail = auth.currentUser?.email
            userEmail?.let {
                val name = binding.nameEditText.text?.toString()
                val ageText = binding.ageEditText.text?.toString()

                if (!name.isNullOrBlank() && !ageText.isNullOrBlank()) {
                    val age = ageText.toIntOrNull()
                    if (age != null) {
                        val user = viewModel.createUser(name, age, userEmail)
                        viewModel.addUserToFireStoreAndSaveSettings(user)
                    } else {
                        showSnackBarWithoutAction(binding.root, "Please enter a valid age.")
                    }
                } else {
                    showSnackBarWithoutAction(binding.root, "Please enter name and age.")
                }
            } ?: run {
                showSnackBarWithoutAction(binding.root, "User email is null.")
            }
        }


        viewModel.snackBarMessage.observe(this) { message ->
            message?.let {
                showSnackBarWithoutAction(binding.root, it)
            }
        }

        viewModel.userAddedSuccessfully.observe(this) { addedSuccessfully ->
            if (addedSuccessfully) {
                val intent = Intent(this@AddUserActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setLoadingState(loading: Boolean) {
        binding.loadingAnimationView.visibility = if (loading) View.VISIBLE else View.GONE
    }

}
