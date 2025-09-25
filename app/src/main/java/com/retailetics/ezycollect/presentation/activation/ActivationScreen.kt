package com.retailetics.ezycollect.presentation.activation

import android.Manifest
import android.hardware.biometrics.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.retailetics.ezycollect.domain.model.AppMode
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import com.retailetics.ezycollect.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreen(
    viewModel: ActivationViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val permissions = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(
                permission = Manifest.permission.CAMERA,
                description = "Camera access is needed to take photos. Please grant this permission.",
                isRequired = true
            ),
            /* AppPermission(
                 permission = Manifest.permission.RECORD_AUDIO,
                 description = "Microphone access is needed for voice recording. Please grant this permission.",
                 isRequired = false
             ),
             AppPermission(
                 permission = Manifest.permission.READ_CONTACTS,
                 description = "Contact access is needed to show the contacts in the App. Please grant this permission",
                 isRequired = true
             ),*/
        )
    )

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()


    /*val isActivated = viewModel.isDeviceActivated.collectAsState()
    LaunchedEffect(isActivated.value) {
        if (isActivated.value == false) {
          //  viewModel.getDeviceInfo()
        }
    }*/

    LaunchedEffect(state.isActivationSuccessful) {
        if (state.isActivationSuccessful) {
            onLoginSuccess()
        }
    }
   /* var fullName = remember { mutableStateOf("") }
    var phone = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }
    var address = remember { mutableStateOf("") }
    var bankAccount = remember { mutableStateOf("") }
    var bankName = remember { mutableStateOf("") }
    var shopName = remember { mutableStateOf("") }

    var checked = remember { mutableStateOf(false) }

    var selectedLoanType = remember { mutableStateOf("Personal") }*/
    var showOTPDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "On-Boarding Registration",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.colorPrimary)
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            OutlinedTextField(
                value = state.fullName,
                onValueChange = viewModel::onNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.shopName,
                onValueChange = viewModel::onShopNameChange,
                label = { Text("Company / Shop Name") },
                modifier = Modifier.fillMaxWidth()
            )
           /* OutlinedTextField(
                value = dob.value,
                onValueChange = { dob.value = it },
                label = { Text("Date of Birth") },
                placeholder = { Text("dd/mm/yyyy") },
                modifier = Modifier.fillMaxWidth()
            )*/

            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.bankAccount,
                onValueChange = viewModel::onBankAccountChange,
                label = { Text("Bank Account") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.bankName,
                onValueChange = viewModel::onBankNameChange,
                label = { Text("Bank Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Enable Biometric Login", style = MaterialTheme.typography.labelLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val options = listOf(
                    true to "Enable",
                    false to "Not Now"
                )

                options.forEach { (value, text) ->
                    Row(
                        modifier = Modifier.clickable { viewModel.onBiometricEnabledChange(value) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.biometricEnabled == value,
                            onClick = { viewModel.onBiometricEnabledChange(value) }
                        )
                        Text(
                            text = text,
                            modifier = Modifier.clickable { viewModel.onBiometricEnabledChange(value) }
                        )
                    }
                }
            }



            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Checkbox(
                    checked = state.termsAccepted,
                    onCheckedChange = viewModel::onTermsAcceptedChange
                )

                Text(
                    text = "I hereby confirm that the provided information is accurate and request to process my registration.",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }



            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.activateDevice()
                   // showOTPDialog.value = true
                          },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Submit")
            }
        }
    }
    if (showOTPDialog.value) {
        OtpVerificationDialog(
            onDismiss = { showOTPDialog.value = false },
            onVerify = { otp ->
                // Handle OTP verification
                //viewModel.activateDeviceForTesting()
                println("Entered OTP: $otp")
                showOTPDialog.value = false
            }
        )
    }


}

@Composable
fun OtpVerificationDialog(
    onDismiss: () -> Unit,
    onVerify: (String) -> Unit
) {
    var otp = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Verify OTP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter the OTP sent to your registered number",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = otp.value,
                    onValueChange = {
                        if (it.length <= 6) otp.value = it // restrict to 6 digits
                    },
                    label = { Text("OTP") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onVerify(otp.value) },
                enabled = otp.value.length == 6 // only enable when valid OTP
            ) {
                Text("Verify")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}





