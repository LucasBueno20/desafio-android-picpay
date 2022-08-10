package com.picpay.desafio.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserList(userList: List<UserEntity>)

    @Query("DELETE FROM table_user")
    suspend fun clearUserList()

    @Query("SELECT * FROM table_user")
    fun getUserList(): List<UserEntity>
}