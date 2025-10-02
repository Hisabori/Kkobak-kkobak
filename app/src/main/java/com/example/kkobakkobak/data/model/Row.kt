package com.example.kkobakkobak.data.model

//import com.tickaroo.tikxml.annotation.PropertyElement
//import com.tickaroo.tikxml.annotation.Xml

/*
@Xml(name = "row")
data class Row(
    @PropertyElement(name = "SIGUN_CD")
    val sigun_cd: String,
    @PropertyElement(name = "SIGUN_NM")
    val sigun_nm: String,
    @PropertyElement(name = "YADM_NM")
    val yadm_nm: String,
    @PropertyElement(name = "YADR_NM")
    val yadr_nm: String,
    @PropertyElement(name = "TELNO")
    val telno: String
)
 */

data class Row(
    val sigun_cd: String,
    val sigun_nm: String,
    val yadm_nm: String,
    val yadr_nm: String,
    val telno: String
)