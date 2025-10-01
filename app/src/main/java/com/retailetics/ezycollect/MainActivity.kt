package com.retailetics.ezycollect

import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.retailetics.ezycollect.R
import com.retailetics.ezycollect.domain.usecase.LoadingManager
import com.retailetics.ezycollect.presentation.SplashViewModel
import com.retailetics.ezycollect.presentation.common.components.CustomRationaleDialog
import com.retailetics.ezycollect.presentation.common.components.GlobalLoadingOverlay

import com.meticha.permissions_compose.PermissionManagerConfig
import com.retailetics.ezycollect.presentation.activation.ActivationScreen
import com.retailetics.ezycollect.presentation.home.PaymentEntryScreen
import com.retailetics.ezycollect.presentation.login.LoginScreen
import com.retailetics.ezycollect.presentation.transaction.TransactionReportScreen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loadingManager: LoadingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = getColor(R.color.colorPrimaryDark)

        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = false

        PermissionManagerConfig.setCustomRationaleUI { permission, onDismiss, onConfirm ->
            CustomRationaleDialog(
                description = permission.description,
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val splashViewModel: SplashViewModel = hiltViewModel()
                    val startDestination = splashViewModel.startDestination.collectAsState()

                    splashViewModel.getDeviceId(this)

                    // Show loading while determining destination
                    when (val destination = startDestination.value) {
                        null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else -> {
                            NavHost(navController, startDestination = destination) {
                                composable("activation") {
                                    ActivationScreen(
                                        onLoginSuccess = {
                                            navController.navigate("login") {
                                                popUpTo("activation") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                composable("login") {
                                    LoginScreen(
                                        onThemeChange = { /* handle */ },
                                        onLanguageChange = { /* handle */ },
                                        onLoginSuccess = {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                composable("home") {
                                    PaymentEntryScreen(
                                        onViewTransaction = {
                                            navController.navigate("transaction") {
                                                popUpTo("home") { inclusive = false }
                                            }
                                        },
                                                onLoggedOut = {
                                            navController.navigate("login") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                composable("transaction") {
                                    TransactionReportScreen()
                                }
                            }
                        }
                    }

                    GlobalLoadingOverlay(loadingManager)
                }
            }
        }
    }

}
