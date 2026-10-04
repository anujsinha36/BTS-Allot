package com.example.btsallot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.btsallot.navigation.NavGraph
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var destinationState by mutableStateOf<String?>(null)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        isGranted: Boolean->
        if (isGranted){
            Log.d("MainActivityDebug", "Notification permission granted")
        }
        else Log.d("MainActivityDebug", "Notification permission denied")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        destinationState = intent.getStringExtra("destination")
        askNotificationPermission()
        subscribeToDutyNotifications()

        setContent {
            BTSAllotTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                NavGraph(destination = destinationState)

//                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        destinationState = intent.getStringExtra("destination")
    } // Updates intent so intent.getStringExtra("destination") gets the new value }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun subscribeToDutyNotifications(){
        FirebaseMessaging.getInstance().subscribeToTopic("new_duties")
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d("MainActivityDebug", "Subscribed to FCM Topic: new_duties")
                }
                else {
                    Log.d("MainActivityDebug", "Failed to subscribe to FCM Topic", task.exception)
                }
            }
    }
}
