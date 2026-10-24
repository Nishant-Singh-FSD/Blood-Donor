package com.example.blooddonor.presentation.profile

import androidx.lifecycle.ViewModel
import com.example.blooddonor.domain.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && snapshot.exists()) {
                    val userProfile = snapshot.toObject(UserProfile::class.java)
                    if (userProfile != null) {
                        _profile.value = userProfile
                    }
                } else {
                    // Create default profile in database if it doesn't exist yet
                    val newProfile = UserProfile(userId = userId)
                    db.collection("users").document(userId).set(newProfile)
                }
            }
    }

    fun updateProfile(name: String, bloodGroup: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .update(
                mapOf(
                    "name" to name,
                    "bloodGroup" to bloodGroup
                )
            )
    }
}