package com.example.kkobakkobak.ui.path

import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @SerializedName("documents") val documents: List<PlaceDocument>
)

data class PlaceDocument(
    @SerializedName("place_name") val place_name: String,
    @SerializedName("x") val x: String,
    @SerializedName("y") val y: String
)