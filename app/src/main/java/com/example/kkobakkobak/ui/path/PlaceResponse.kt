package com.example.kkobakkobak.ui.path

import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @SerializedName("documents") val documents: List<PlaceDocument> = emptyList()
)

data class PlaceDocument(
    @SerializedName("place_name") val placeName: String,
    @SerializedName("road_address_name") val roadAddressName: String?,
    @SerializedName("x") val x: String, // 경도
    @SerializedName("y") val y: String  // 위도
)
