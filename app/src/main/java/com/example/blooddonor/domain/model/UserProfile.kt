package com.example.blooddonor.domain.model

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val bloodGroup: String = "",
    val points: Int = 0,
    val donationsCompleted: Int = 0
)