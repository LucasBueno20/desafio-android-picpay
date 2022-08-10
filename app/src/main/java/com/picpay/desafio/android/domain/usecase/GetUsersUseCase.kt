package com.picpay.desafio.android.domain.usecase

import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.repository.UserListRepository
import com.picpay.desafio.android.utils.Resource
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(
    private val repository: UserListRepository
    ) {

    suspend fun invoke(): Flow<Resource<List<User>>> {
        val response = repository.fetchUserList()
        //here we can add our business rules logic
        return response
    }
}
