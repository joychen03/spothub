package com.itb.dam.jiafuchen.spothub.domain.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Post() : RealmObject {
    @PrimaryKey
    var _id : ObjectId = ObjectId()

    @Index
    var owner_id : String = ""

    var title : String = ""
    var description : String = ""
    var image : ByteArray = byteArrayOf()
    var longitude : Double = 0.0
    var latitude : Double = 0.0
    var likes : RealmList<User> = realmListOf()

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}
