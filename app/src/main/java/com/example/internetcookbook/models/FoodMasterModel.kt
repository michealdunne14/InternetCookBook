package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodMasterModel(
    val image: BufferModel = BufferModel(),
    val food: FoodModel = FoodModel()
) : Parcelable