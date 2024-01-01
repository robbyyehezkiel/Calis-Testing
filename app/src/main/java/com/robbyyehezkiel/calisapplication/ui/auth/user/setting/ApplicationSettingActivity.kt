package com.robbyyehezkiel.calisapplication.ui.auth.user.setting

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.data.model.User
import com.robbyyehezkiel.calisapplication.databinding.ActivityApplicationSettingBinding
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.SettingPreferences
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.dataStore
import com.robbyyehezkiel.calisapplication.ui.non_auth.LoginActivity
import com.robbyyehezkiel.calisapplication.ui.utils.LoadingState
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ApplicationSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicationSettingBinding
    private lateinit var viewModel: ApplicationSettingViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicationSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setLoadingState(LoadingState.Success)

        userRepository = UserRepository(SettingPreferences.getInstance(application.dataStore))

        val viewModelFactory = ApplicationSettingViewModelFactory(userRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[ApplicationSettingViewModel::class.java]

        setupUI()
        auth = FirebaseAuth.getInstance()

        binding.logoutButton.setOnClickListener {
            signOut()
        }
    }

    private fun setupUI() {
        setupToolbar()
        observeUserSettings()
        observeLoadingState()
        observeSuccessMessage()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeSuccessMessage() {
        viewModel.showSuccessMessage.observe(this) {
            showSnackBarWithoutAction(binding.root, "User profile updated successfully")
        }
    }

    private fun observeUserSettings() {
        lifecycleScope.launch {
            userRepository.getUserSettingsFlow().collect { userSettings ->
                updateUIWithUserSettings(userSettings)
            }
        }
    }

    private fun observeLoadingState() {
        viewModel.loadingState.observe(this) { loadingState ->
            setLoadingState(loadingState)
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info_save -> {
                updateUserProfile()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUIWithUserSettings(userSettings: User) {
        binding.nameEditText.text = SpannableStringBuilder(userSettings.name)
        binding.ageEditText.text = SpannableStringBuilder(userSettings.age.toString())
    }

    private fun updateUserProfile() {
        val updatedName = binding.nameEditText.text.toString()
        val updatedAge = binding.ageEditText.text.toString().toInt()

        lifecycleScope.launch {
            val userSettings = userRepository.getUserSettingsFlow().first()

            val updatedUser = User(
                userId = userSettings.userId,
                name = updatedName,
                age = updatedAge,
                email = userSettings.email
            )

            viewModel.updateUserProfile(updatedUser)
        }
    }

    private fun setLoadingState(loadingState: LoadingState) {
        binding.loadingAnimation.visibility = when (loadingState) {
            LoadingState.Loading -> View.VISIBLE
            else -> View.GONE
        }

        if (loadingState is LoadingState.Error) {
            showSnackBarWithoutAction(binding.root, loadingState.message)
        }
    }

    private fun signOut() {
        auth.signOut()
        lifecycleScope.launch {
            userRepository.clearUserSettings()
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
