package com.picpay.desafio.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class],
    version = 2
)
abstract class UserDatabase: RoomDatabase() {
    abstract val dao: UserDao
}