package com.ubb.citizen_u.data.repositories

import com.google.firebase.firestore.DocumentSnapshot
import com.ubb.citizen_u.data.model.citizens.Comment

interface CommentRepository {

    suspend fun getAllComments(objectDocSnapshot: DocumentSnapshot): MutableList<Comment?>
}