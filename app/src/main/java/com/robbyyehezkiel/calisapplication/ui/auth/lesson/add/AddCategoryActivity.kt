package com.robbyyehezkiel.calisapplication.ui.auth.lesson.add

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.data.model.Quiz
import com.robbyyehezkiel.calisapplication.data.model.SubCategory
import com.robbyyehezkiel.calisapplication.databinding.ActivityAddCategoryBinding
import com.robbyyehezkiel.calisapplication.ui.utils.LoadingState
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var subCategoryContainer: LinearLayout
    private val subCategories: MutableList<EditText> = mutableListOf()

    private val viewModel: AddCategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        observeLoadingState()
    }

    private fun initializeViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        subCategoryContainer = binding.subCategoryContainer

        binding.btnAddCategory.setOnClickListener { addCategoryToFireStore() }
        binding.btnAddSubCategory.setOnClickListener { addSubCategoryEditText(isDefault = false) }

        addSubCategoryEditText(isDefault = true)
    }

    private fun addCategoryToFireStore() {
        val category = binding.editCategory.text.toString().trim()

        if (category.isEmpty()) {
            showSnackBarWithoutAction(binding.root, "Category name cannot be empty.")
            return
        }

        val subCategoriesList = subCategories.map { it.text.toString().trim() }
        if (subCategoriesList.any { it.isEmpty() }) {
            showSnackBarWithoutAction(binding.root, "Subcategory names cannot be empty.")
            return
        }

        val quiz = Quiz(category, subCategoriesList.map { SubCategory(it) })

        viewModel.addOrUpdateCategory(quiz)
    }

    private fun observeLoadingState() {
        viewModel.loadingState.observe(this) { loadingState ->
            when (loadingState) {
                LoadingState.Loading -> showProgressBar()
                LoadingState.Success -> {
                    hideProgressBar()
                    showSnackBarWithoutAction(binding.root, "Operation successful.")
                    clearFormInputs()
                }

                is LoadingState.Error -> {
                    hideProgressBar()
                    showSnackBarWithoutAction(binding.root, loadingState.message)
                    clearFormInputs()
                }
            }
        }
    }

    private fun clearFormInputs() {
        binding.editCategory.text?.clear()
        clearSubCategories()
        addSubCategoryEditText(isDefault = true)
    }

    private fun clearSubCategories() {
        for (subCategoryEditText in subCategories) {
            subCategoryEditText.text.clear()
        }
        subCategoryContainer.removeAllViews()
        subCategories.clear()
    }

    private fun showProgressBar() {
        binding.loadingAnimation.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.loadingAnimation.visibility = View.GONE
    }

    private fun addSubCategoryEditText(isDefault: Boolean = false) {
        val subCategoryLayout = createSubCategoryLayout()
        val subCategoryTextInputLayout = createSubCategoryEditText()

        if (!isDefault) {
            val removeButton =
                createRemoveButton(subCategoryLayout, subCategoryTextInputLayout.editText!!)
            subCategoryLayout.addView(subCategoryTextInputLayout)
            subCategoryLayout.addView(removeButton)
            subCategories.add(subCategoryTextInputLayout.editText!!)
        } else {
            subCategoryLayout.addView(subCategoryTextInputLayout)
            if (!subCategories.contains(subCategoryTextInputLayout.editText!!)) {
                subCategories.add(subCategoryTextInputLayout.editText!!)
            }
        }

        val layoutParams = createLayoutParams()
        subCategoryLayout.layoutParams = layoutParams
        subCategoryContainer.addView(subCategoryLayout)
    }

    private fun createSubCategoryLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    private fun createSubCategoryEditText(): TextInputLayout {
        val textInputLayout = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                resources.getDimensionPixelSize(R.dimen.dimens_60dp),
                1.0f
            )
        }

        val subCategoryEditText = TextInputEditText(textInputLayout.context).apply {
            hint = getString(R.string.subcategory_hint)
        }

        textInputLayout.addView(subCategoryEditText)
        return textInputLayout
    }

    private fun createRemoveButton(
        subCategoryLayout: LinearLayout,
        subCategoryEditText: EditText
    ): ImageButton {
        return ImageButton(this).apply {
            setImageResource(R.drawable.ic_remove)
            setOnClickListener { removeSubCategory(subCategoryLayout, subCategoryEditText) }
            layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.dimens_60dp),
                resources.getDimensionPixelSize(R.dimen.dimens_60dp)
            )
        }
    }

    private fun removeSubCategory(subCategoryLayout: LinearLayout, subCategoryEditText: EditText) {
        subCategoryContainer.removeView(subCategoryLayout)
        subCategories.remove(subCategoryEditText)
    }

    private fun createLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.dimens_60dp)
        ).apply {
            topMargin = resources.getDimensionPixelSize(R.dimen.dimens_8dp)
        }
    }
}
