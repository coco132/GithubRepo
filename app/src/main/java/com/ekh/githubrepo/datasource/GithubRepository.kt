package com.ekh.githubrepo.datasource

import com.ekh.githubrepo.data.RepoResponse
import javax.inject.Inject

class GithubRepository @Inject constructor(
    val api: GithubApi
) {
    fun search(q: String): RepoResponse {
        return api.search(q)
    }

}