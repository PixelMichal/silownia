package com.example.silownia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.silownia.data.num
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
import kotlin.math.max
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val viewModel: JournalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            SilowniaTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null) {
            val krokiParam = uri.getQueryParameter("kroki")
            if (krokiParam != null) {
                var dataParam = uri.getQueryParameter("data") ?: viewModel.today
                if (!Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(dataParam)) {
                    dataParam = viewModel.today
                }
                val stepCount = max(0, num(krokiParam).roundToInt())
                viewModel.updateState { prev ->
                    prev.copy(steps = prev.steps + (dataParam to stepCount))
                }
                viewModel.setCur(dataParam)
                viewModel.selectTab(AppTab.HEALTH)
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
