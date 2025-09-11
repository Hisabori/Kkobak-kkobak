package com.example.kkobakkobak.data.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Ggmindmedinst", strict = false)
data class Ggmindmedinst(
    @field:Element(name = "head")
    var head: Head? = null,
    @field:ElementList(name = "row", inline = true, required = false)
    var row: List<Hospital>? = null
)

@Root(name = "head", strict = false)
data class Head(
    @field:Element(name = "list_total_count")
    var totalCount: Int = 0,
    @field:Element(name = "RESULT")
    var result: Result? = null
)

@Root(name = "RESULT", strict = false)
data class Result(
    @field:Element(name = "CODE")
    var code: String? = null,
    @field:Element(name = "MESSAGE")
    var message: String? = null
)

@Root(name = "row", strict = false)
data class Hospital(
    @field:Element(name = "INST_NM", required = false)
    var name: String? = null,
    @field:Element(name = "REFINE_ROADNM_ADDR", required = false)
    var address: String? = null,
    @field:Element(name = "REFINE_WGS84_LAT", required = false)
    var latitude: String? = null,
    @field:Element(name = "REFINE_WGS84_LOGT", required = false)
    var longitude: String? = null,
    @field:Element(name = "TELNO_INFO", required = false)
    var tel: String? = null
)