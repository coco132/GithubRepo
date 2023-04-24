package com.ekh.githubrepo.datasource

import com.ekh.githubrepo.data.RepoResponse
import javax.inject.Inject

class GithubRepository @Inject constructor(
    private val api: GithubApi
) {
    suspend fun search(q: String, page: Int): RepoResponse {
        return api.search(q, page, 20)
    }

}