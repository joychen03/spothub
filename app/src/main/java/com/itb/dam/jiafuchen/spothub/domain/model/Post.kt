package com.itb.dam.jiafuchen.spothub.domain.model

import android.os.Parcelable
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.mongodb.kbson.ObjectId

@Parcelize
class Post() : RealmObject , Parcelable {

    @PrimaryKey
    var _id : ObjectId = ObjectId()

    @Index
    var owner_id : String = ""

    var title : String = ""
    var description : String = ""
    var image : ByteArray = byteArrayOf()
    var longitude : Double = 0.0
    var latitude : Double = 0.0
    var updateDataTime : RealmInstant = RealmInstant.now()
    var createDateTime : RealmInstant = RealmInstant.now()
    var likes : RealmList<ObjectId> = realmListOf()

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}
