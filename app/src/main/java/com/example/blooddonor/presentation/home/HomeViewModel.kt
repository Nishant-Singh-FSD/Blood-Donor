package com.example.blooddonor.presentation.home

import androidx.lifecycle.ViewModel
import com.example.blooddonor.domain.model.BloodRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _requests = MutableStateFlow<List<BloodRequest>>(emptyList())
    val requests: StateFlow<List<BloodRequest>> = _requests.asStateFlow()

    private val _userName = MutableStateFlow("Hero")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userBloodGroup = MutableStateFlow("")
    val userBloodGroup: StateFlow<String> = _userBloodGroup.asStateFlow()

    init {
        fetchRequests()
        fetchUserDetails()
    }

    private fun fetchRequests() {
        db.collection("bloodRequests")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val requestList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(BloodRequest::class.java)
                }
                // Filter to only show OPEN requests, sorted by newest
                _requests.value = requestList
                    .filter { it.status == "OPEN" }
                    .sortedByDescending { it.timestamp }
            }
    }

    private fun fetchUserDetails() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && snapshot.exists()) {
                    val name = snapshot.getString("name")
                    if (!name.isNullOrBlank()) {
                        _userName.value = name
                    }

                    val bloodGroup = snapshot.getString("bloodGroup")
                    if (!bloodGroup.isNullOrBlank()) {
                        _userBloodGroup.value = bloodGroup
                    }
                }
            }
    }

    // --- GAMIFICATION LOGIC ---
    fun acceptRequest(requestId: String) {
        val userId = auth.currentUser?.uid ?: return

        // 1. Mark the request as ACCEPTED so it leaves the public feed
        db.collection("bloodRequests").document(requestId)
            .update(
                mapOf(
                    "status" to "ACCEPTED",
                    "acceptedBy" to userId
                )
            )

        // 2. Instantly award 10 XP and 1 Life Saved to the user's profile
        db.collection("users").document(userId)
            .update(
                mapOf(
                    "points" to FieldValue.increment(10),
                    "donationsCompleted" to FieldValue.increment(1)
                )
            )
    }
}