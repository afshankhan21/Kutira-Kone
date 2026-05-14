package com.example.kutirakone

data class SwapRequest(
    val id: String = "",
    val targetId: String = "",
    val targetName: String = "",
    val offer: String = "",
    val status: String = "Pending",
    val timestamp: Long = 0
)