package com.robbyyehezkiel.calisapplication.ui.auth.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.data.model.Menu
import com.robbyyehezkiel.calisapplication.databinding.ItemListNavigationBinding
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.view.BalloonTextActivity
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.view.NameLetterActivity
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.add.AddCategoryActivity
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.add.AddQuestionActivity

class HomeMenuAdapter(
    private val context: Context,
    private val listMenu: ArrayList<Menu>
) :
    RecyclerView.Adapter<HomeMenuAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemListNavigationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.menuTitle
        val imgPhoto = binding.menuIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListNavigationBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = listMenu[position]
        val cardView = holder.itemView as MaterialCardView
        setCardBackgroundColor(menu.name, cardView)
        holder.binding.apply {
            holder.tvName.text = menu.name
            holder.imgPhoto.setImageResource(menu.photo)
            navigationCard.setOnClickListener {
                when (menu.name) {
                    "MEMBACA" -> {
                        val intent = Intent(context, BalloonTextActivity::class.java)
                        context.startActivity(intent)
                    }

                    "MENGHITUNG" -> {
                        val intent = Intent(context, AddCategoryActivity::class.java)
                        context.startActivity(intent)
                    }

                    "MENULIS" -> {
                        val intent = Intent(context, NameLetterActivity::class.java)
                        context.startActivity(intent)
                    }

                    "PROGRES" -> {
                        val intent = Intent(context, AddQuestionActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }

        }
    }

    private fun setCardBackgroundColor(name: String, cardView: MaterialCardView) {
        cardView.setCardBackgroundColor(
            when (name) {
                context.getString(R.string.membaca) -> Color.YELLOW
                context.getString(R.string.menulis) -> Color.GREEN
                context.getString(R.string.menghitung) -> Color.MAGENTA
                context.getString(R.string.progress) -> Color.CYAN
                else -> Color.WHITE
            }
        )
    }

    override fun getItemCount(): Int = listMenu.size
}
