package com.robbyyehezkiel.calisapplication.ui.auth.user.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.robbyyehezkiel.calisapplication.databinding.ActivityUserSettingBinding
import com.robbyyehezkiel.calisapplication.ui.auth.home.HomeActivity
import com.robbyyehezkiel.calisapplication.ui.auth.user.add.AddUserActivity
import com.robbyyehezkiel.calisapplication.ui.utils.LoadingState
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction

class UserSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserSettingBinding
    private lateinit var userAdapter: UserAdapter
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        observeViewModel()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchUserByEmail()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun initViews() {
        userAdapter = UserAdapter { user ->
            showSnackBarWithoutAction(binding.root, "User: ${user.name}")
            viewModel.saveUserSettings(user)
            startActivity(Intent(this, HomeActivity::class.java))
        }

        with(binding.rvUser) {
            layoutManager = GridLayoutManager(this@UserSettingActivity, 2)
            adapter = userAdapter
        }

        binding.addButton.setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
        }
    }

    private fun observeViewModel() {
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

        viewModel.usersLiveData.observe(this) { users ->
            userAdapter.submitList(users)
        }

        viewModel.fetchUserByEmail()
    }

    private fun setLoadingState(loading: Boolean) {
        binding.loading.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
