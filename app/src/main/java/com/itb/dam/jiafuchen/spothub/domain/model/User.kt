package com.itb.dam.jiafuchen.spothub.domain.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class User() : RealmObject {

    @PrimaryKey
    var _id : ObjectId = ObjectId()

    @Index
    var owner_id : String = ""
    var email : String = ""
    var username : String = ""
    var description : String = ""
    var avatar : ByteArray = byteArrayOf()
    var updateDataTime : RealmInstant = RealmInstant.now()
    var createDateTime : RealmInstant = RealmInstant.now()
    var followers : RealmList<ObjectId> = realmListOf()
    var followings : RealmList<ObjectId> = realmListOf()

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}