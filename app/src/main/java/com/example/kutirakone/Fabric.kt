package com.example.kutirakone

import java.io.Serializable

data class Fabric(
    val id: String = "",
    val title: String = "",
    val materialType: String = "",
    val size: String = "",
    val price: String = "",
    val color: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val location: String = "Unknown"
) : Serializable
