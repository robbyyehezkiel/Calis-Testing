package com.robbyyehezkiel.calisapplication.ui.auth.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.data.model.Menu
import com.robbyyehezkiel.calisapplication.databinding.ActivityHomeBinding
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter.LetterHomeActivity
import com.robbyyehezkiel.calisapplication.ui.auth.user.setting.ApplicationSettingActivity
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.SettingPreferences
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.dataStore
import com.robbyyehezkiel.calisapplication.ui.auth.user.view.UserSettingActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var rvMenu: RecyclerView
    private lateinit var binding: ActivityHomeBinding
    private val list = ArrayList<Menu>()
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setupClickListeners()
        initializeRecyclerView()

        val settingPreferences = SettingPreferences.getInstance(application.dataStore)
        userRepository = UserRepository(settingPreferences)

        lifecycleScope.launch {
            userRepository.getUserSettingsFlow().collect { userSettings ->
                binding.edHomeUserName.text = userSettings.name
            }
        }

        binding.edHomeNavEnrichment.setOnClickListener {
            startLetterHome()
        }
    }

    private fun initializeViews() {
        rvMenu = binding.rvMenu
    }

    private fun setupClickListeners() {
        binding.edHomeNavAppSetting.setOnClickListener {
            startApplicationSettingActivity()
        }
        binding.edHomeNavUserSetting.setOnClickListener {
            startUserSettingActivity()
        }
    }

    private fun initializeRecyclerView() {
        rvMenu.setHasFixedSize(true)
        list.addAll(getListMenus())
        showRecyclerList()
    }

    private fun startApplicationSettingActivity() {
        val intentId = intent.getStringExtra("intent_uuid")
        val intent = Intent(this@HomeActivity, ApplicationSettingActivity::class.java)
            .putExtra("intent_uuid", intentId)
        startActivity(intent)
    }

    private fun startUserSettingActivity() {
        val intent = Intent(this@HomeActivity, UserSettingActivity::class.java)
        startActivity(intent)
    }

    private fun startLetterHome() {
        val intent = Intent(this@HomeActivity, LetterHomeActivity::class.java)
        startActivity(intent)
    }

    private fun getListMenus(): ArrayList<Menu> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listMenu = ArrayList<Menu>()
        for (i in dataName.indices) {
            val menu = Menu(dataName[i], dataPhoto.getResourceId(i, -1))
            listMenu.add(menu)
        }
        dataPhoto.recycle() // Recycle the typed array
        return listMenu
    }

    private fun showRecyclerList() {
        rvMenu.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        val listMenuAdapter =
            HomeMenuAdapter(this@HomeActivity, list)
        rvMenu.adapter = listMenuAdapter
    }

}
