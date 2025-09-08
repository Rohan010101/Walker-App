package com.example.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class WandererProfile(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: ObjectId,
)
