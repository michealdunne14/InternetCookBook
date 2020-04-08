package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.helper.readBit64ImageSingle
import com.example.internetcookbook.item.ItemFragmentPresenter
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.UserMasterModel
import kotlinx.android.synthetic.main.ingredients_list.view.*

class IngredientsAdapter(
    private var food: ArrayList<FoodMasterModel>,
    private var currentUser: UserMasterModel,
    private var selectedOption: String,
    private var presenter: BasePresenter
) : RecyclerView.Adapter<IngredientsAdapter.MainHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
            return MainHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.ingredients_list,
                    parent,
                    false
                )
            )
        }

        //  Item Count
        override fun getItemCount(): Int = food.size

        override fun onBindViewHolder(holder: MainHolder, position: Int) {
            val postModel = food[holder.adapterPosition]
            holder.bind(postModel,currentUser,selectedOption,presenter)
        }

        class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
            fun bind(
                foodModel: FoodMasterModel,
                currentUser: UserMasterModel,
                selectedOption: String,
                presenter: BasePresenter
            ) {
                if (selectedOption == "basket") {
                    itemView.mSelectedIngredient.setImageResource(R.drawable.baseline_remove_black_36)
                    for (basket in currentUser.user.basket) {
                        if (basket.basketoid == foodModel.food.oid) {
                            itemView.mIngredientCounter.text = basket.counter.toString()
                        }
                    }
                    itemView.mSelectedIngredient.setOnClickListener {
                        val itemPresenter = presenter as ItemFragmentPresenter
                        itemPresenter.doRemoveItem(foodModel)
                    }
                }else if(selectedOption == "cupboard"){
                    itemView.mSelectedIngredient.setImageResource(R.drawable.baseline_remove_black_36)
                    for (cupboard in currentUser.user.cupboard) {
                        if (cupboard.cupboardoid == foodModel.food.oid) {
                            itemView.mIngredientCounter.text = cupboard.foodPurchasedCounter.toString()
                        }
                    }
                    itemView.mSelectedIngredient.setOnClickListener {

                    }
                }
                itemView.mIngredientsName.text = foodModel.food.name
                val bitmapImage = readBit64ImageSingle(foodModel.image)
                itemView.mFoodPicture.setImageBitmap(bitmapImage)
            }
        }


}