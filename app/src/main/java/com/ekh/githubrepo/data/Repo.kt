package com.ekh.githubrepo.data

import com.google.gson.annotations.SerializedName


data class Repo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
)