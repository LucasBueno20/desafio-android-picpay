package com.picpay.desafio.android.data.mapper

import com.picpay.desafio.android.data.local.UserEntity
import com.picpay.desafio.android.data.remote.dto.UserDto
import com.picpay.desafio.android.domain.model.User

const val defaultImage = "https://randomuser.me/api/portraits/men/1.jpg"

fun UserEntity.toUser(): User {
    return User (
        id = id,
        name = name,
        img = img,
        username = userName
    )
}

fun UserDto.toUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name?: "N/A",
        img = img?: defaultImage,
        userName = username?: "N/A"
    )
}
