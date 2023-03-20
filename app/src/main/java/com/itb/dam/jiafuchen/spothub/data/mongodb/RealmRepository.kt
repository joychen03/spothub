package com.itb.dam.jiafuchen.spothub.data.mongodb

import android.util.Log
import com.itb.dam.jiafuchen.spothub.app
import com.itb.dam.jiafuchen.spothub.data.mongodb.interfaces.IRealmRepository
import com.itb.dam.jiafuchen.spothub.domain.model.Post
import com.itb.dam.jiafuchen.spothub.domain.model.User
import io.realm.kotlin.Realm
import io.realm.kotlin.log.LogLevel

import io.realm.kotlin.mongodb.sync.SyncConfiguration

object RealmRepository : IRealmRepository {
    private val user = app.currentUser
    private lateinit var realm : Realm

    init {
        requireNotNull(user)
        setup()
    }

    override fun setup() {
        if(user != null){
            val config = SyncConfiguration.Builder(
                user,
                setOf(User::class, Post::class)
            ).log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
            Log.v("Realm","Successfully opened realm: ${realm.configuration.name}")
        }
    }


}