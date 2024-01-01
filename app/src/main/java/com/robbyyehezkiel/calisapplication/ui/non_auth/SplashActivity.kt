package com.robbyyehezkiel.calisapplication.ui.non_auth

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.robbyyehezkiel.calisapplication.databinding.ActivitySplashBinding
import com.robbyyehezkiel.calisapplication.ui.auth.home.HomeActivity
import com.robbyyehezkiel.calisapplication.ui.auth.user.add.AddUserActivity
import com.robbyyehezkiel.calisapplication.ui.auth.user.view.UserSettingActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseAuthActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val loadingStartTime = System.currentTimeMillis()
            checkUserDataAndNavigate()
            val loadingTime = System.currentTimeMillis() - loadingStartTime
            delay((SPLASH_DELAY - loadingTime + EXTRA_DELAY).coerceAtLeast(0))
            finish()
        }
    }

    private suspend fun checkUserDataAndNavigate() {
        if (auth.currentUser != null) {
            if (isUserDataAvailableInDataStore()) {
                navigateTo(HomeActivity::class.java)
            } else {
                handleUserDataAvailability(
                    auth.currentUser?.email,
                    UserSettingActivity::class.java,
                    AddUserActivity::class.java
                )
            }
        } else {
            navigateTo(LoginActivity::class.java)
        }
    }

    companion object {
        private const val SPLASH_DELAY = 2000L
        private const val EXTRA_DELAY = 3000L
    }
}
