package com.example.image_app.ui.data

import com.google.gson.annotations.SerializedName

data class MImage (
    val id : String,
    val width : String,
    val height : String,
    val urls : MUrl,
    var position : Int,
    val links : MLink
)

data class MLink(val download:String)

data class MUrl(val small :String,val thumb:String,val regular:String)
