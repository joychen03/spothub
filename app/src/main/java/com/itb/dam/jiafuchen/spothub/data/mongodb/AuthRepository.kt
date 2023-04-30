package com.itb.dam.jiafuchen.spothub.data.mongodb

import com.itb.dam.jiafuchen.spothub.app
import io.realm.kotlin.mongodb.Credentials

object AuthRepository {

    suspend fun createAccount(email: String, password: String) {
        app.emailPasswordAuth.registerUser(email, password)
    }

    suspend fun login(email: String, password: String) {
        app.login(Credentials.emailPassword(email, password))
    }

}