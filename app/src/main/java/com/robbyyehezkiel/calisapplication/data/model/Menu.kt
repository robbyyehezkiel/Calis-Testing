package com.robbyyehezkiel.calisapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
    val name: String,
    val photo: Int,
) : Parcelable
