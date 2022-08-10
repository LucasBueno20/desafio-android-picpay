package com.picpay.desafio.android.data.repository

import android.content.res.Resources
import com.picpay.desafio.android.data.local.UserDatabase
import com.picpay.desafio.android.data.mapper.toUser
import com.picpay.desafio.android.data.mapper.toUserEntity
import com.picpay.desafio.android.data.remote.PicPayService
import com.picpay.desafio.android.data.remote.dto.UserDto
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.repository.UserListRepository
import com.picpay.desafio.android.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class UserListRepositoryImpl(
    private val api: PicPayService,
    db: UserDatabase
): UserListRepository {

    private val dao = db.dao

    override suspend fun fetchUserList(): Flow<Resource<List<User>>> {
        return flow {
            emit(Resource.Loading())

            try {
                val remoteListing = api.getUsers()
                addUserListToDb(remoteListing)
            }
            catch (e:HttpException) {
                emit(Resource.Error(Resources.getSystem().getString(android.R.string.untitled)))
            }
            catch (e:IOException) {
                emit(Resource.Error(Resources.getSystem().getString(android.R.string.untitled)))
            }

            val newUserList = dao.getUserList().map { it.toUser() }
            emit(Resource.Success(newUserList))

        }.flowOn(Dispatchers.IO)
    }

    private suspend fun addUserListToDb(userList: List<UserDto>) {
        dao.clearUserList()
        dao.insertUserList(userList.map { it.toUserEntity() })
    }

}