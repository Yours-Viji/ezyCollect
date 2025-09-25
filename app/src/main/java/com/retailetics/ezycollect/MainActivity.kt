package com.retailetics.ezycollect

import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
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

        //enableEdgeToEdge()
        setContent {

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val splashViewModel: SplashViewModel = hiltViewModel()
                    val isActivated = splashViewModel.isDeviceActivated.collectAsState()
                    splashViewModel.getDeviceId(this)



                    when (isActivated.value) {
                        null -> {
                            // Optional loading UI
                            Text("Checking device activation...")
                        }

                        else -> {
                            val startDestination =
                                if (isActivated.value == true) "activation" else "login"

                            NavHost(navController, startDestination = startDestination) {
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

                                        onThemeChange = {
                                            // 🔹 Handle theme change
                                            //  Toast.makeText(this, "Theme change clicked", Toast.LENGTH_SHORT).show()
                                        },
                                        onLanguageChange = {
                                            // 🔹 Handle language change
                                            // Toast.makeText(this, "Language change clicked", Toast.LENGTH_SHORT).show()
                                        },
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
                                                popUpTo("transaction") { inclusive = false }
                                            }
                                        }
                                    )
                                }
                                composable("transaction") {

                                    TransactionReportScreen(

                                    )
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
