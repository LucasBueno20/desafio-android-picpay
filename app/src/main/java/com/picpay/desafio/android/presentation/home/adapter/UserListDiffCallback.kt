package com.picpay.desafio.android.presentation.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.picpay.desafio.android.domain.model.User

class UserListDiffCallback(
    private val oldList: List<User>,
    private val newList: List<User>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> {
                false
            }
            oldList[oldItemPosition].name != newList[newItemPosition].name -> {
                false
            }
            oldList[oldItemPosition].img != newList[newItemPosition].img -> {
                false
            }
            oldList[oldItemPosition].username != newList[newItemPosition].username -> {
                false
            }
            else -> true
        }
    }
}