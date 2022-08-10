package com.picpay.desafio.android.domain.repository

import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserListRepository {

    suspend fun fetchUserList(): Flow<Resource<List<User>>>

}