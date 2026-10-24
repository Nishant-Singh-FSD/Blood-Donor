package com.example.blooddonor.domain.model

data class BloodRequest(
    val id: String = "",
    val requesterId: String = "",
    val bloodGroup: String = "",
    val unitsRequired: String = "",
    val location: String = "",
    val status: String = "OPEN",
    val acceptedBy: String = "", // NEW: Tracks who accepted it
    val timestamp: Long = System.currentTimeMillis()
)