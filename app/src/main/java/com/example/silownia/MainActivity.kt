package com.example.silownia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.silownia.ui.components.AppBottomBar
import com.example.silownia.ui.screens.DietScreen
import com.example.silownia.ui.screens.HealthScreen
import com.example.silownia.ui.screens.ProfileScreen
import com.example.silownia.ui.screens.ProgressScreen
import com.example.silownia.ui.screens.RecipesScreen
import com.example.silownia.ui.screens.TrainingScreen
import com.example.silownia.ui.theme.AppTheme
import com.example.silownia.ui.theme.SilowniaTheme
import com.example.silownia.viewmodel.AppTab
import com.example.silownia.viewmodel.JournalViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: JournalViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startStepSensor()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkActivityRecognitionPermission()
        setContent {
            SilowniaTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }

    private fun checkActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permission = Manifest.permission.ACTIVITY_RECOGNITION
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: JournalViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.bg,
        bottomBar = {
            AppBottomBar(
                currentTab = selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (selectedTab) {
                AppTab.DIET -> DietScreen(viewModel = viewModel)
                AppTab.RECIPES -> RecipesScreen(viewModel = viewModel)
                AppTab.TRAINING -> TrainingScreen(viewModel = viewModel)
                AppTab.HEALTH -> HealthScreen(viewModel = viewModel)
                AppTab.PROGRESS -> ProgressScreen(viewModel = viewModel)
                AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
