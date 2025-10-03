package com.retailetics.ezycollect.presentation.transaction

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.retailetics.ezycollect.R
import com.retailetics.ezycollect.data.remote.dto.TransactionData
import com.retailetics.ezycollect.data.remote.dto.TransactionReport
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionReportScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
) {
    val transactionReport = viewModel.transactionReport.collectAsState()
    val isLoadingState = viewModel.isLoading.collectAsState()
    val errorState = viewModel.error.collectAsState()
    val totalTransaction = viewModel.totalTransaction.collectAsState()
    val totalCollection = viewModel.totalCollection.collectAsState()

    var startDate = remember { mutableStateOf(viewModel.getCurrentDate()) }
    var endDate = remember { mutableStateOf(viewModel.getCurrentDate()) }
    var expandedTransactionId = remember { mutableStateOf<Int?>(null) }

    // Date validation
    val isStartDateValid = remember(startDate.value) {
        startDate.value.isEmpty() || isValidDateFormat(startDate.value)
    }

    val isEndDateValid = remember(endDate.value) {
        endDate.value.isEmpty() || isValidDateFormat(endDate.value)
    }

    // Clear transactions when there's an error
    LaunchedEffect(errorState.value) {
        if (errorState.value.isNotEmpty()) {
            // Error occurred, ensure loading is stopped
            // The ViewModel should handle clearing transactions on error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Report", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colorResource(R.color.colorIconGray))
        ) {
            // Filter Section
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Filter Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // Date Range Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Date Field
                       /* OutlinedTextField(
                            value = startDate.value,
                            onValueChange = { startDate.value = it },
                            label = { Text("Start Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            placeholder = { Text("YYYY-MM-DD") },
                            isError = !isStartDateValid,
                            supportingText = {
                                if (!isStartDateValid) {
                                    Text("Use format: YYYY-MM-DD")
                                }
                            }
                        )*/
                        CustomDatePickerField(selectedDate = startDate.value,
                            onDateSelected = { selectedDate ->
                                startDate.value = selectedDate
                            },
                            modifier = Modifier.weight(1f))

                        CustomDatePickerField(selectedDate = endDate.value,
                            onDateSelected = { selectedDate ->
                                endDate.value = selectedDate
                            },
                            modifier = Modifier.weight(1f))

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Show error message if any
                    if (errorState.value.isNotEmpty()) {
                        Text(
                            text = errorState.value,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Get Results Button
                    Button(
                        onClick = {
                            viewModel.getShoppingCartDetails(startDate.value, endDate.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        shape = MaterialTheme.shapes.large,
                        enabled = startDate.value.isNotEmpty() &&
                                endDate.value.isNotEmpty() &&
                                isStartDateValid &&
                                isEndDateValid &&
                                !isLoadingState.value
                    ) {
                        if (isLoadingState.value) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Get Transaction Report", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    // Clear Button
                    if (transactionReport.value.isNotEmpty() || errorState.value.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                // Clear all states
                                viewModel.clearTransactions()
                                startDate.value = viewModel.getCurrentDate()
                                endDate.value = viewModel.getCurrentDate()
                                expandedTransactionId.value = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.colorOrange)
                            )
                        ) {
                            Text("Clear Results", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Show loading indicator separately if needed
            if (isLoadingState.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Transactions List
                if (transactionReport.value.isNotEmpty()) {
                    SummaryCard(
                        totalCollection = totalCollection.value,
                        totalTransactions = totalTransaction.value
                    )
                    Text(
                        text = "Transactions (${transactionReport.value.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(transactionReport.value) { transaction ->
                            TransactionCard(
                                transaction = transaction,
                                isExpanded = expandedTransactionId.value == transaction.order_id,
                                onCardClick = {
                                    expandedTransactionId.value = if (expandedTransactionId.value == transaction.order_id) {
                                        null
                                    } else {
                                        transaction.order_id
                                    }
                                }
                            )
                        }
                    }
                } else if (startDate.value.isNotEmpty() && endDate.value.isNotEmpty()) {
                    // Empty State when filters are applied but no results
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painterResource(R.drawable.transaction),
                            contentDescription = "No transactions",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No transactions found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Try different date range",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    // Initial state - no filters applied
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painterResource(R.drawable.transaction),
                            contentDescription = "No filters applied",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Enter filters to view transactions",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Select date range above",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CustomDatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker = remember { mutableStateOf(false) } // Initialize as false

    // Parse current date if exists
    val calendar = Calendar.getInstance()
    if (selectedDate.isNotEmpty()) {
        try {
            val parts = selectedDate.split("-")
            if (parts.size == 3) {
                calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val year = remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    val month = remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    val day = remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    // Clickable field that opens date picker
    OutlinedTextField(
        value = selectedDate,
        onValueChange = { },
        label = { Text("Start Date") },
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                showDatePicker.value = true
            },
        singleLine = true,
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Pick date",
                modifier = Modifier.clickable {
                    showDatePicker.value = true
                }
            )
        }
    )

    // Date Picker Dialog
    if (showDatePicker.value) {
        AlertDialog(
            onDismissRequest = { showDatePicker.value = false },
            title = { Text("Select Date", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column {
                    DatePicker(
                        year = year.value,
                        month = month.value,
                        day = day.value, // Added day parameter
                        onDateChanged = { y, m, d ->
                            year.value = y
                            month.value = m
                            day.value = d
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedDateStr = String.format(
                            "%04d-%02d-%02d",
                            year.value,
                            month.value + 1,
                            day.value
                        )
                        onDateSelected(selectedDateStr)
                        showDatePicker.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Updated Native Android DatePicker Composable with day parameter
@Composable
fun DatePicker(
    year: Int,
    month: Int,
    day: Int, // Added day parameter
    onDateChanged: (Int, Int, Int) -> Unit
) {
    AndroidView(
        factory = { context ->
            android.widget.DatePicker(context).apply {
                init(year, month, day) { _, selectedYear, selectedMonth, selectedDay ->
                    onDateChanged(selectedYear, selectedMonth, selectedDay)
                }
            }
        },
        update = { datePicker ->
            datePicker.updateDate(year, month, day)
        }
    )
}
// New Summary Card Component
@Composable
fun SummaryCard(totalCollection: Double, totalTransactions: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "Transaction Summary",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Collection",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "RM$totalCollection",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total Transactions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = totalTransactions.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
// Transaction Card Component
@Composable
fun TransactionCard(
    transaction: TransactionReport,
    isExpanded: Boolean,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Main Transaction Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCardClick() }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "TXN #${transaction.order_id}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = transaction.shop_name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "By ${transaction.full_name} • ${transaction.created_date}-${transaction.created_time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                // Right Side Content
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "RM${transaction.total_amount}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        painterResource(if (isExpanded) R.drawable.baseline_expand_less_24 else R.drawable.outline_keyboard_arrow_down_24),
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Expanded Items List - FIX: Check if items is not null and not empty
            if (isExpanded && !transaction.items.isNullOrEmpty()) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Items (${transaction.items.size})",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    transaction.items.forEachIndexed { index, item ->
                        ItemRow(item = item, isLast = index == transaction.items.size - 1)
                    }
                }
            } else if (isExpanded) {
                // Show message when there are no items
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "No items available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemRow(item: TransactionData, isLast: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product_name ?: "Unknown Product", // Handle null product name
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Qty: ${item.qty ?: 0} × RM${item.unit_price ?: 0.0}", // Handle null values
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                text = "RM${item.line_total ?: 0.0}", // Handle null line total
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (!isLast) {
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                thickness = 1.dp
            )
        }
    }
}
// Date validation function
fun isValidDateFormat(date: String): Boolean {
    return try {
        val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        if (!regex.matches(date)) return false

        val parts = date.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()

        // Basic validation
        year in 2000..2100 && month in 1..12 && day in 1..31
    } catch (e: Exception) {
        false
    }
}

/// Utility function to format date - FIXED to handle null
fun formatDate(dateString: String?): String {
    // Handle null case first
    if (dateString == null || dateString.isBlank()) {
        return "Date not available"
    }

    return try {
        // Try multiple common date formats
        val formats = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "dd-MM-yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss"
        )

        var parsedDate: Date? = null
        for (format in formats) {
            try {
                val inputFormat = SimpleDateFormat(format, Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Handle UTC times
                parsedDate = inputFormat.parse(dateString)
                if (parsedDate != null) break
            } catch (e: Exception) {
                // Try next format
                continue
            }
        }

        if (parsedDate != null) {
            val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault() // Convert to local timezone
            outputFormat.format(parsedDate)
        } else {
            "Invalid date format"
        }
    } catch (e: Exception) {
        "Date format error"
    }
}
