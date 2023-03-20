package com.itb.dam.jiafuchen.spothub.data.mongodb.interfaces

interface IAuthRepository {

    suspend fun createAccount(email: String, password: String)

    suspend fun login(email: String, password: String)
}
