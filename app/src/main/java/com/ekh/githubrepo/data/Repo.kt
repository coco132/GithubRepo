package com.ekh.githubrepo.data

import com.google.gson.annotations.SerializedName


data class Repo(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("description") var description: String,
)