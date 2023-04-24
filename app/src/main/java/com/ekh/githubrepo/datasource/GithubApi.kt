package com.ekh.githubrepo.datasource

import com.ekh.githubrepo.data.RepoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories")
    fun search(@Query("q") q: String): RepoResponse
}