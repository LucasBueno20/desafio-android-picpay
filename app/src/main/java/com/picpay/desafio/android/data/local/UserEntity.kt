package com.picpay.desafio.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_user")
data class UserEntity (
    @PrimaryKey
    val id:Int,
    val img: String,
    val name: String,
    val userName: String
)