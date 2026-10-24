package com.example.blooddonor.presentation.home

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blooddonor.domain.model.BloodRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFAAB4C0)
val DarkGlassCard = Color(0xFF1E293B).copy(alpha = 0.75f)
val MatchGreenGlass = Color(0xFF14532D).copy(alpha = 0.75f)
val MatchGreenNeon = Color(0xFF4ADE80)
val BloodRed = Color(0xFFFF4B4B)
val EmergencyGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFF4B4B), Color(0xFFB91C1C))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEmergencyClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAiTriageClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val requests by viewModel.requests.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userBloodGroup by viewModel.userBloodGroup.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color(0xFF0B0F19))
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard", fontWeight = FontWeight.Bold, color = TextPrimary) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(
                            onClick = onProfileClick,
                            modifier = Modifier.padding(end = 8.dp).background(Color(0xFF1E293B).copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = TextPrimary)
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Welcome back,\n$userName", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(24.dp))

                    EmergencyCard(onEmergencyClick)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onAiTriageClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MatchGreenNeon),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(text = "Ask AI Clinical Triage Assistant", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF064E3B))
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "Active Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(requests) { request ->
                    RequestCard(request = request, userBloodGroup = userBloodGroup, onAccept = { viewModel.acceptRequest(request.id) })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun RequestCard(request: BloodRequest, userBloodGroup: String, onAccept: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isMatch = request.bloodGroup.trim().equals(userBloodGroup.trim(), ignoreCase = true)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Accept Request?", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Pledge to donate ${request.unitsRequired} units of ${request.bloodGroup} at ${request.location}?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        onAccept()
                        showDialog = false
                        Toast.makeText(context, "Request Accepted!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MatchGreenNeon)
                ) { Text("I Will Donate", color = Color(0xFF064E3B)) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel", color = TextSecondary) } },
            containerColor = Color(0xFF1E293B)
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isMatch) MatchGreenGlass else DarkGlassCard),
        modifier = Modifier.fillMaxWidth().then(if (isMatch) Modifier.clickable { showDialog = true } else Modifier)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)).background(if (isMatch) MatchGreenNeon.copy(alpha = 0.15f) else BloodRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = request.bloodGroup, color = if (isMatch) MatchGreenNeon else BloodRed, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${request.unitsRequired} Units Needed", fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = request.location, color = TextSecondary)
            }
        }
    }
}

@Composable
fun EmergencyCard(onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.background(brush = EmergencyGradient).padding(28.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Rounded.Warning, contentDescription = "Emergency", tint = Color.White, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "URGENT BLOOD REQUIRED", color = Color.White, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF991B1B)),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(50)
                ) { Text("BROADCAST EMERGENCY", fontWeight = FontWeight.ExtraBold) }
            }
        }
    }
}