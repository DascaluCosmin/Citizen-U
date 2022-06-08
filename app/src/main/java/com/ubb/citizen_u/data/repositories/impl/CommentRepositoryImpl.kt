package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.firestore.DocumentSnapshot
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.repositories.CommentRepository
import com.ubb.citizen_u.util.DatabaseConstants.COMMENTS_COL
import kotlinx.coroutines.tasks.await

class CommentRepositoryImpl : CommentRepository {
    override suspend fun getAllComments(objectDocSnapshot: DocumentSnapshot): MutableList<Comment?> {
        val commentsSnapshot = objectDocSnapshot.reference
            .collection(COMMENTS_COL).get().await()
        return commentsSnapshot.documents.map {
            it.toObject(Comment::class.java)
        }.toMutableList()
    }
}