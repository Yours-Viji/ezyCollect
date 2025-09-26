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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

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
        )
    )

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Track validation errors
    val validationErrors = remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(state.isActivationSuccessful) {
        if (state.isActivationSuccessful) {
            onLoginSuccess()
        }
    }

    var showOTPDialog = remember { mutableStateOf(false) }

    // Show validation errors when they occur
    LaunchedEffect(validationErrors.value) {
        if (validationErrors.value.isNotEmpty()) {
            val firstError = validationErrors.value.values.firstOrNull()
            firstError?.let { errorMessage ->
                snackbarHostState.showSnackbar(errorMessage)
            }
        }
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Full Name Field with validation
            OutlinedTextField(
                value = state.fullName,
                onValueChange = {
                    viewModel.onNameChange(it)
                    // Clear validation error when user starts typing
                    if (validationErrors.value.containsKey("fullName")) {
                        validationErrors.value = validationErrors.value - "fullName"
                    }
                },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("fullName"),
                supportingText = {
                    validationErrors.value["fullName"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            // Shop Name Field with validation
            OutlinedTextField(
                value = state.shopName,
                onValueChange = {
                    viewModel.onShopNameChange(it)
                    if (validationErrors.value.containsKey("shopName")) {
                        validationErrors.value = validationErrors.value - "shopName"
                    }
                },
                label = { Text("Company / Shop Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("shopName"),
                supportingText = {
                    validationErrors.value["shopName"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            // Phone Field with validation
            OutlinedTextField(
                value = state.phone,
                onValueChange = {
                    viewModel.onPhoneChange(it)
                    if (validationErrors.value.containsKey("phone")) {
                        validationErrors.value = validationErrors.value - "phone"
                    }
                },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("phone"),
                supportingText = {
                    validationErrors.value["phone"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            // Email Field with validation
            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    viewModel.onEmailChange(it)
                    if (validationErrors.value.containsKey("email")) {
                        validationErrors.value = validationErrors.value - "email"
                    }
                },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("email"),
                supportingText = {
                    validationErrors.value["email"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            // Address Field with validation
            OutlinedTextField(
                value = state.address,
                onValueChange = {
                    viewModel.onAddressChange(it)
                    if (validationErrors.value.containsKey("address")) {
                        validationErrors.value = validationErrors.value - "address"
                    }
                },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("address"),
                supportingText = {
                    validationErrors.value["address"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            // Bank Account Field with validation
            OutlinedTextField(
                value = state.bankAccount,
                onValueChange = {
                    viewModel.onBankAccountChange(it)
                    if (validationErrors.value.containsKey("bankAccount")) {
                        validationErrors.value = validationErrors.value - "bankAccount"
                    }
                },
                label = { Text("Bank Account") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("bankAccount"),
                supportingText = {
                    validationErrors.value["bankAccount"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            // Bank Name Field with validation
            OutlinedTextField(
                value = state.bankName,
                onValueChange = {
                    viewModel.onBankNameChange(it)
                    if (validationErrors.value.containsKey("bankName")) {
                        validationErrors.value = validationErrors.value - "bankName"
                    }
                },
                label = { Text("Bank Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("bankName"),
                supportingText = {
                    validationErrors.value["bankName"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            // Password Field with validation
            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                    if (validationErrors.value.containsKey("password")) {
                        validationErrors.value = validationErrors.value - "password"
                    }
                },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("password"),
                supportingText = {
                    validationErrors.value["password"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            // Login Pin Field with validation
            OutlinedTextField(
                value = state.loginPin,
                onValueChange = {
                    viewModel.onPinChange(it)
                    if (validationErrors.value.containsKey("loginPin")) {
                        validationErrors.value = validationErrors.value - "loginPin"
                    }
                },
                label = { Text("Login Pin") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = validationErrors.value.containsKey("loginPin"),
                supportingText = {
                    validationErrors.value["loginPin"]?.let { error ->
                        Text(text = error, color = Color.Red)
                    }
                }
            )

            Spacer(Modifier.height(10.dp))
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

            // Terms Checkbox with validation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Checkbox(
                    checked = state.termsAccepted,
                    onCheckedChange = {
                        viewModel.onTermsAcceptedChange(it)
                        if (validationErrors.value.containsKey("termsAccepted")) {
                            validationErrors.value = validationErrors.value - "termsAccepted"
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = if (validationErrors.value.containsKey("termsAccepted")) Color.Red else colorResource(id = R.color.colorPrimary)
                    )
                )

                Text(
                    text = "I hereby confirm that the provided information is accurate and request to process my registration.",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp),
                    color = if (validationErrors.value.containsKey("termsAccepted")) Color.Red else Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    // Perform validation before activating device
                    val errors = validateFields(state)
                    if (errors.isEmpty()) {
                        viewModel.activateDevice()
                    } else {
                        validationErrors.value = errors
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
            ) {
                Text("Submit")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "(OR)",
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp).align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Already Registered?",
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    onLoginSuccess()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.colorGreen),
                    contentColor = Color.White
                ),
            ) {
                Text("Login")
            }
        }
    }

    if (showOTPDialog.value) {
        OtpVerificationDialog(
            onDismiss = { showOTPDialog.value = false },
            onVerify = { otp ->
                // Handle OTP verification
                println("Entered OTP: $otp")
                showOTPDialog.value = false
            }
        )
    }
}

/**
 * Validation function that checks all required fields
 */
private fun validateFields(state: ActivationState): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    // Check all fields are non-empty
    if (state.fullName.isBlank()) {
        errors["fullName"] = "Full name is required"
    }

    if (state.shopName.isBlank()) {
        errors["shopName"] = "Shop/Company name is required"
    }

    if (state.phone.isBlank()) {
        errors["phone"] = "Phone number is required"
    } else if (!isValidPhone(state.phone)) {
        errors["phone"] = "Please enter a valid phone number"
    }

    if (state.email.isBlank()) {
        errors["email"] = "Email address is required"
    } else if (!isValidEmail(state.email)) {
        errors["email"] = "Please enter a valid email address"
    }

    if (state.address.isBlank()) {
        errors["address"] = "Address is required"
    }

    if (state.bankAccount.isBlank()) {
        errors["bankAccount"] = "Bank account is required"
    }

    if (state.bankName.isBlank()) {
        errors["bankName"] = "Bank name is required"
    }

    if (state.password.isBlank()) {
        errors["password"] = "Password is required"
    } else if (state.password.length < 6) {
        errors["password"] = "Password must be at least 6 characters"
    }

    if (state.loginPin.isBlank()) {
        errors["loginPin"] = "Login PIN is required"
    } else if (state.loginPin.length != 6) {
        errors["loginPin"] = "PIN must be 6 digits"
    }

    if (!state.termsAccepted) {
        errors["termsAccepted"] = "You must accept the terms and conditions"
    }

    return errors
}

/**
 * Simple email validation
 */
private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return email.matches(emailRegex.toRegex())
}

/**
 * Simple phone validation (adjust based on your requirements)
 */
private fun isValidPhone(phone: String): Boolean {
    // Basic validation - at least 10 digits, you can customize this
    val phoneRegex = "^[0-9]{10,}$"
    return phone.replace("\\s".toRegex(), "").matches(phoneRegex.toRegex())
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