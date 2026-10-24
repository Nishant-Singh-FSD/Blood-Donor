package com.example.blooddonor.presentation.request

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyRequestScreen(
    onNavigateBack: () -> Unit,
    viewModel: EmergencyRequestViewModel = viewModel()
) {
    var bloodGroup by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var units by remember { mutableStateOf("") }

    val requestState by viewModel.requestState.collectAsState()
    val context = LocalContext.current // NEW: Get the context for the Toast

    // When the request successfully saves to Firebase
    LaunchedEffect(requestState) {
        if (requestState is RequestState.Success) {
            // NEW: Show the success message
            Toast.makeText(context, "Request Sent Successfully!", Toast.LENGTH_SHORT).show()
            // Then close the screen
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Request") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Fill details immediately. We will notify nearby donors.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = { Text("Required Blood Group (e.g., O+)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = units,
                onValueChange = { units = it },
                label = { Text("Units Required") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Hospital Name & City (e.g., Ranchi)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.submitBloodRequest(bloodGroup, units, location) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = requestState !is RequestState.Loading
            ) {
                if (requestState is RequestState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onError)
                } else {
                    Text("Broadcast Emergency Request")
                }
            }

            if (requestState is RequestState.Error) {
                Text(
                    text = (requestState as RequestState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}