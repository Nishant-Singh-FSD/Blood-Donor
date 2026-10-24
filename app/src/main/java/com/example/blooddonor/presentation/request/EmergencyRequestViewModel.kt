package com.example.blooddonor.presentation.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blooddonor.domain.model.BloodRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class EmergencyRequestViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _requestState = MutableStateFlow<RequestState>(RequestState.Idle)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    fun submitBloodRequest(bloodGroup: String, units: String, location: String) {
        if (bloodGroup.isBlank() || units.isBlank() || location.isBlank()) {
            _requestState.value = RequestState.Error("All fields are required")
            return
        }

        _requestState.value = RequestState.Loading
        val userId = auth.currentUser?.uid ?: return

        // Create a unique ID for this request
        val requestId = UUID.randomUUID().toString()

        val newRequest = BloodRequest(
            id = requestId,
            requesterId = userId,
            bloodGroup = bloodGroup,
            unitsRequired = units,
            location = location
        )

        viewModelScope.launch {
            db.collection("bloodRequests").document(requestId)
                .set(newRequest)
                .addOnSuccessListener {
                    _requestState.value = RequestState.Success
                }
                .addOnFailureListener { e ->
                    _requestState.value = RequestState.Error(e.localizedMessage ?: "Failed to save request")
                }
        }
    }
}

sealed class RequestState {
    object Idle : RequestState()
    object Loading : RequestState()
    object Success : RequestState()
    data class Error(val message: String) : RequestState()
}