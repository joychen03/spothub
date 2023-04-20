package com.itb.dam.jiafuchen.spothub.data.mongodb

import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.interfaces.IAuthRepository
import io.realm.kotlin.mongodb.Credentials

object AuthRepository : IAuthRepository {

    override suspend fun createAccount(email: String, password: String) {
        app.emailPasswordAuth.registerUser(email, password)
    }

    override suspend fun login(email: String, password: String) {
        app.login(Credentials.emailPassword(email, password))
    }

}