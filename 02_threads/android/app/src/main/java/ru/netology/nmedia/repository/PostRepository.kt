package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: PostRepositoryCallback<List<Post>>)
    fun likeByIdAsync(id: Long, callback: PostRepositoryCallback<Post>)
    fun removeLikeByIdAsync(id: Long, callback: PostRepositoryCallback<Post>)
    fun saveAsync(post: Post, callback: PostRepositoryCallback<Post>)
    fun removeByIdAsync(id: Long, callback: PostRepositoryCallback<Unit>)


    interface PostRepositoryCallback<T> {
        fun onSuccess(value: T) {}
        fun onError(e: Exception) {}
    }
}
