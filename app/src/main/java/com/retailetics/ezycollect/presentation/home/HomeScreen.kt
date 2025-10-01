package com.retailetics.ezycollect.presentation.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.size.Size
import com.retailetics.ezycollect.R
import com.retailetics.ezycollect.data.remote.dto.Item
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*// Data class for items
data class CartItem(
    val id: Int,
    var name: String,
    var price: Double,
    var quantity: Int = 1
)*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentEntryScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onViewTransaction: () -> Unit,
    onLoggedOut: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val cartDataList = viewModel.cartDataList.collectAsState()
    val totalAmount = viewModel.totalAmount.collectAsState()
    val cartCount = viewModel.cartCount.collectAsState()

    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    //var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var showCartDialog by remember { mutableStateOf(false) }
    var nextItemId by remember { mutableStateOf(1) }


    var showQrDialog = remember { mutableStateOf(false) }
    var showPaymentDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val context = LocalContext.current
    LaunchedEffect(state.isLogOutSuccess) {
        if (state.isLogOutSuccess) {
            onLoggedOut()
        }
    }
    LaunchedEffect(Unit) {
        //focusRequester.requestFocus()
        viewModel.initNewShopping()
    }
    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            // Optional: Clear the error after showing
            // viewModel.clearError()
        }
    }
    if (showQrDialog.value) {
        PaymentOptionDialog(
            onDismiss = { showQrDialog.value = false },
            onTapToPay = {
                // Handle tap to pay logic
                viewModel.checkout("Tap On Pay")
                showQrDialog.value = false
            },
            onQrPayment = {
                showQrDialog.value = false
                showPaymentDialog.value = true

            },
            onCashPayment = {
                viewModel.checkout("Cash")
                showQrDialog.value = false
            }
        )
    }
    if (showPaymentDialog.value) {

        QrPaymentAlert(
            amount = "%.2f".format(totalAmount.value),
            qrPainter = painterResource(id = R.drawable.baseline_qr_code_2_24), // replace with your QR
            onDismiss = {
                viewModel.checkout("QR Payment")
                showPaymentDialog.value = false
            }
        )

    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onTransActionSelected = {
                    scope.launch { drawerState.close() }
                    onViewTransaction()

                },
                onLogOutSelected = {
                    scope.launch { drawerState.close() }
                    viewModel.setLoggedOut()
                },

            )
        }
    ) { Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Entry") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White // Optional: set icon color
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {scope.launch { drawerState.open() }},
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Cart Icon with Badge
                    Box(
                        modifier = Modifier
                            .padding(end = 1.dp)
                    ) {
                        IconButton(
                            onClick = {
                                // Handle cart click - open cart dialog or navigate to cart screen
                                showCartDialog = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_cart), // Use your cart icon
                                contentDescription = "Shopping Cart",
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Badge for item count
                        if (cartCount.value > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                                    .background(Color.Red, CircleShape)
                            ) {
                                Text(
                                    text = if (cartCount.value > 9) "9+" else cartCount.value.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(20.dp))
                    IconButton(
                        onClick = {
                            viewModel.initNewShopping()
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.baseline_refresh_24),
                            contentDescription = "Refresh",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                   /* IconButton(
                        onClick = {
                            onViewTransaction()
                            println("Icon clicked!")
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.outline_contract_24),
                            contentDescription = "Settings",
                            modifier = Modifier.size(30.dp)
                        )
                    }*/


                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make the entire column scrollable
        ) {
            // Header
            /* Text(
                text = "Menu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )*/

            // Input Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Enter product name") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth()
                    )
                    /*Text(
                        text = "Enter product name",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        placeholder = { Text("Product name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )*/

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Price",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Price input with increment/decrement buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            /*Icon(
                                painter = painterResource(id = R.drawable.outline_remove_24),
                                contentDescription = "Decrease",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        val currentPrice = itemPrice.toDoubleOrNull() ?: 0.0
                                        if (currentPrice > 0) {
                                            itemPrice = "%.2f".format(currentPrice - 0.10)
                                        }
                                    }
                                    .padding(4.dp)
                            )
*/
                            OutlinedTextField(
                                value = itemPrice,
                                onValueChange = {
                                    if (it.isEmpty() || it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                        itemPrice = it
                                    }
                                },
                                placeholder = { Text("0.00") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                prefix = { Text("RM") }
                            )

                          /*  Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        val currentPrice = itemPrice.toDoubleOrNull() ?: 0.0
                                        itemPrice = "%.2f".format(currentPrice + 0.10)
                                    }
                                    .padding(4.dp)
                            )*/
                        }

                        // Enter button
                        Button(
                            onClick = {
                                if (itemPrice.isNotBlank()) {
                                    val price = itemPrice.toDoubleOrNull() ?: 0.0
                                    if (price > 0) {
                                        viewModel.addProductToShoppingCart(itemName, 1, price)
                                        scope.launch {
                                            delay(100L)
                                            itemName = ""
                                            itemPrice = ""
                                        }
                                    }
                                }
                            },
                            enabled = itemPrice.isNotBlank(),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Add",
                                modifier = Modifier.size(20.dp)
                            )
                            Text("Enter")
                        }
                    }
                }
            }

            // View Cart Button
            Button(
                onClick = { showCartDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                ),
                enabled = cartDataList.value.isNotEmpty()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "View Cart",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Cart (${cartDataList.value.size})")
            }

            // Recent Items Section - Limited height and scrollable
            if (cartDataList.value.isNotEmpty()) {
                Text(
                    text = "Recent Items",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Container with fixed height for recent items
                Box(
                    modifier = Modifier
                        .height(170.dp) // Fixed height to ensure bottom bar is visible
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(
                            cartDataList.value.reversed().take(2)
                        ) { item -> // Show only up to 5 items
                            CartItemRow(
                                item = item,
                                onRemove = {
                                    viewModel.deleteProductFromShoppingCart(item.id)
                                    //cartItems = cartDataList.value.filter { it.id != item.id }
                                },
                                onUpdateQuantity = { newQuantity ->
                                    viewModel.editProductInShoppingCart(
                                        item.price.toDouble(),
                                        newQuantity,
                                        item.id
                                    )
                                    /* cartItems = cartItems.map {
                                        if (it.id == item.id) it.copy(quantity = newQuantity)
                                        else it
                                    }*/
                                }
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .height(100.dp) // Fixed height for empty state
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items added yet",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Add some space at the bottom to ensure the content doesn't get hidden behind the bottom bar
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    // Adding a small elevation or shadow can help distinguish it
                    .border(
                        width = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: RM${"%.2f".format(totalAmount.value)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Button(
                        onClick = {
                            showQrDialog.value = true
                            // Process payment logic here
                            // This button should ideally navigate or trigger an action
                            // For now, we can show a Toast or a Snackbar if needed for testing
                        },
                        enabled = cartDataList.value.isNotEmpty(), // Enable only if there are items
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("PAY NOW")
                    }
                }
            }

        }
    }
}

    // Cart Dialog
    if (showCartDialog) {
        Dialog(
            onDismissRequest = { showCartDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Add some padding so it doesn't touch screen edges
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cart Items",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(cartDataList.value) { item ->
                            CartItemRow(
                                item = item,
                                onRemove = {
                                    viewModel.deleteProductFromShoppingCart(item.id)
                                },
                                onUpdateQuantity = { newQuantity ->
                                    viewModel.editProductInShoppingCart(
                                        item.price.toDouble(),
                                        newQuantity,
                                        item.id
                                    )
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: RM${"%.2f".format(totalAmount.value)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        // ❌ Cancel Button (Red)
                        Button(
                            onClick = { showCartDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text("Cancel", color = Color.White)
                        }

                        // ✅ Pay Now Button (Green)
                        Button(
                            onClick = {
                                showCartDialog = false
                                showQrDialog.value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50) // Green
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Pay Now", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DrawerContent(
    onTransActionSelected: () -> Unit,
    onLogOutSelected: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(350.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(50.dp))
        /* Image(
             painter = painterResource(id = R.drawable.ic_merchant_logo),
             contentDescription = "Ad Banner",
             contentScale = ContentScale.Crop,
             modifier = Modifier.fillMaxWidth().height(70.dp)
         )

         Divider()*/

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTransActionSelected() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.transaction),
                contentDescription = "transaction",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "View Transaction",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
        Divider()
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogOutSelected() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_logout_24),
                contentDescription = "transaction",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Log Out",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

    }
}
@Composable
fun CartItemRow(
    item: Item,
    onRemove: () -> Unit,
    onUpdateQuantity: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product_name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "RM${"%.2f".format(item.price.toDouble() ?: 0.0)} each",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Quantity controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_remove_24),
                    contentDescription = "Decrease",
                    modifier = Modifier
                        .size(21.dp)
                        .clickable {
                            if (item.quantity > 1) {
                                onUpdateQuantity(item.quantity - 1)
                            }
                        }
                )

                Text(
                    text = "${item.quantity}",
                    modifier = Modifier.padding(horizontal = 5.dp),
                    fontSize = 13.sp
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    modifier = Modifier
                        .size(21.dp)
                        .clickable {
                            onUpdateQuantity(item.quantity + 1)
                        }
                )
            }

            Text(
                text = "RM${"%.2f".format(item.price.toDouble() * item.quantity)}",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove",
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onRemove() }
                    .padding(start = 5.dp)
            )
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun PaymentEntryScreenPreview() {
    PaymentEntryScreen()
}*/

@Composable
fun QrPaymentAlert(
    amount: String,
    qrPainter: Painter, // QR code image painter
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // Top-right close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center), // keep column centered
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.id_duit_now_logo),
                    contentDescription = "Ad Banner",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                // QR Code Image
                Image(
                    painter = qrPainter,
                    contentDescription = "QR Code",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Amount
                Text(
                    text = "RM $amount",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
@Composable
fun PaymentOptionDialog(
    onDismiss: () -> Unit,
    onTapToPay: () -> Unit,
    onQrPayment: () -> Unit,
    onCashPayment: () -> Unit

) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Choose Payment Method",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Tap to Pay option
                Button(
                    onClick = onTapToPay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2) // Blue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_contactless_24),
                        contentDescription = "Tap to Pay",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Tap to Pay (Credit / Debit Card)", color = Color.White)
                }

                // QR Payment option
                Button(
                    onClick = onQrPayment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50) // Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_qr_code_scanner_24),
                        contentDescription = "QR Payment",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("QR Payment", color = Color.White)
                }

                Button(
                    onClick = onCashPayment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.colorOrange) // Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_money_24),
                        contentDescription = "Cash",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Cash", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cancel option
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
