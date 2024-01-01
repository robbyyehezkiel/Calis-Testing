package com.robbyyehezkiel.calisapplication.ui.auth.lesson.add

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.databinding.ActivityAddQuestionBinding
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.add.fragment.BlankFragment
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.add.fragment.QuestionEssayFragment
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.add.fragment.QuestionOptionFragment
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddQuestionBinding
    private lateinit var viewModel: AddQuestionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AddQuestionViewModel::class.java]

        setupViews()
        observeViewModel()
        viewModel.fetchCategories()
    }

    private fun setupViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setupCategoryAutoCompleteTextView()
    }

    private fun setupCategoryAutoCompleteTextView() {
        val autoCompleteCategory = binding.autocompleteCategory
        val autoCompleteSubcategory = binding.autocompleteSubcategory

        autoCompleteCategory.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = autoCompleteCategory.adapter.getItem(position) as? String
            viewModel.setSelectedCategory(selectedCategory ?: "")
            viewModel.fetchAndPopulateSubcategories(selectedCategory)
        }

        autoCompleteSubcategory.setOnItemClickListener { _, _, position, _ ->
            val selectedSubcategory = autoCompleteSubcategory.adapter.getItem(position) as? String
            viewModel.setSelectedSubcategory(selectedSubcategory ?: "")
        }

        autoCompleteSubcategory.isEnabled = false
    }

    private fun observeViewModel() {
        viewModel.categories.observe(this) { categories ->
            updateAutoCompleteAdapter(binding.autocompleteCategory, categories)
        }

        viewModel.subcategories.observe(this) { subcategories ->
            updateAutoCompleteAdapter(binding.autocompleteSubcategory, subcategories)
            binding.autocompleteSubcategory.isEnabled = true
        }

        viewModel.showProgressBar.observe(this) { showProgressBar ->
            binding.loadingAnimationView.visibility = if (showProgressBar) View.VISIBLE else View.GONE
        }

        viewModel.errorSnackBar.observe(this) { event ->
            event.getContentIfNotHandled()?.let { errorMessage ->
                showSnackBarWithoutAction(binding.root, errorMessage)
            }
        }

        viewModel.loadQuestionEssayFragment.observe(this) { shouldLoad ->
            if (shouldLoad) {
                loadFragment(QuestionEssayFragment())
            }
        }

        viewModel.loadQuestionOptionFragment.observe(this) { shouldLoad ->
            if (shouldLoad) {
                loadFragment(QuestionOptionFragment())
            }
        }
        viewModel.clearForm.observe(this) {
            binding.autocompleteCategory.text.clear()
            binding.autocompleteSubcategory.text.clear()
            viewModel.resetFormState()
        }

        viewModel.showBlankFragment.observe(this) { shouldShowBlankFragment ->
            if (shouldShowBlankFragment) {
                loadFragment(BlankFragment())
            }
        }
    }


    private fun updateAutoCompleteAdapter(
        autoCompleteTextView: AutoCompleteTextView,
        data: List<String>
    ) {
        val adapter = ArrayAdapter(
            this@AddQuestionActivity,
            android.R.layout.simple_dropdown_item_1line,
            data
        )
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentContainer.id, fragment)
        fragmentTransaction.commitNow()
    }

    private fun handleSaveButtonClick() {
        val questionFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val selectedCategory = viewModel.selectedCategory.value
        val selectedSubcategory = viewModel.selectedSubcategory.value

        if (selectedCategory == null && selectedSubcategory == null) {
            showSnackBarWithoutAction(binding.root, getString(R.string.choose_category))
        } else if (selectedCategory != null && selectedSubcategory != null) {
            if (questionFragment is QuestionOptionFragment) {
                val questionData = questionFragment.getQuestionOption(selectedCategory, selectedSubcategory)
                viewModel.saveQuestionToFireStore(questionData, binding.root)
            } else if (questionFragment is QuestionEssayFragment) {
                val questionData = questionFragment.getQuestionEssay(selectedCategory, selectedSubcategory)
                viewModel.saveQuestionToFireStore(questionData, binding.root)
            }
        } else {
            showSnackBarWithoutAction(binding.root, getString(R.string.category_cannot_be_null))
        }
    }


    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info_save -> {
                handleSaveButtonClick()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
