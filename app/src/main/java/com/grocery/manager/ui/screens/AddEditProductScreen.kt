package com.grocery.manager.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.grocery.manager.data.local.ImageUtils
import com.grocery.manager.data.local.Product
import com.grocery.manager.data.local.Variant
import com.grocery.manager.ui.theme.Gold
import com.grocery.manager.ui.theme.SuccessGreen
import com.grocery.manager.ui.theme.Teal500
import com.grocery.manager.ui.theme.TextSecondary
import com.grocery.manager.viewmodel.CategoryViewModel
import com.grocery.manager.viewmodel.CompanyViewModel
import com.grocery.manager.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    productViewModel: ProductViewModel,
    companyViewModel: CompanyViewModel,
    categoryViewModel: CategoryViewModel,
    productId: Int? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isEditMode = productId != null
    val companies by companyViewModel.companies.collectAsStateWithLifecycle()

    // Form state
    var productName by remember { mutableStateOf("") }
    var selectedImagePath by remember { mutableStateOf("") }
    var selectedCompanyId by remember { mutableStateOf<Int?>(null) }
    var variants by remember { mutableStateOf(listOf(VariantFormState())) }
    var companyDropdownExpanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    // Load existing product in edit mode
    LaunchedEffect(productId) {
        if (productId != null) {
            val product = productViewModel.getProductById(productId)
            product?.let {
                productName = it.name
                selectedImagePath = it.imageUri
                selectedCompanyId = it.companyId
            }
        }
    }

    // Load existing variants in edit mode
    val existingVariants by if (productId != null)
        productViewModel.getVariantsForProduct(productId)
            .collectAsStateWithLifecycle(initialValue = emptyList())
    else
        remember { mutableStateOf(emptyList()) }

    LaunchedEffect(existingVariants) {
        if (existingVariants.isNotEmpty()) {
            variants = existingVariants.map { v ->
                VariantFormState(
                    id = v.id,
                    label = v.label,
                    buyingPrice = v.buyingPrice.toString(),
                    sellingPrice = v.sellingPrice.toString()
                )
            }
        }
    }

    // Image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = ImageUtils.saveImageToInternalStorage(context, it)
            if (savedPath.isNotEmpty()) selectedImagePath = savedPath
        }
    }

    fun save() {
        nameError = productName.isBlank()
        if (nameError) return
        val product = Product(
            id = productId ?: 0,
            name = productName.trim(),
            imageUri = selectedImagePath,
            companyId = selectedCompanyId,
            categoryId = null
        )
        val variantList = variants
            .filter {
                it.label.isNotBlank() &&
                        it.buyingPrice.isNotBlank() &&
                        it.sellingPrice.isNotBlank()
            }
            .map { v ->
                Variant(
                    id = v.id,
                    productId = productId ?: 0,
                    label = v.label.trim(),
                    buyingPrice = v.buyingPrice.toDoubleOrNull() ?: 0.0,
                    sellingPrice = v.sellingPrice.toDoubleOrNull() ?: 0.0
                )
            }
        if (isEditMode) {
            productViewModel.updateProduct(product, variantList)
        } else {
            productViewModel.insertProduct(product, variantList)
        }
        onNavigateBack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Product" else "Add Product",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Teal500
                        )
                    }
                },
                actions = {
                    TextButton(onClick = ::save) {
                        Text(
                            text = "Save",
                            color = Teal500,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImagePath.isNotEmpty()) {
                    AsyncImage(
                        model = selectedImagePath,
                        contentDescription = "Product image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
                    )
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        shape = MaterialTheme.shapes.small,
                        color = Teal500
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Change",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add photo",
                            modifier = Modifier.size(48.dp),
                            tint = Teal500.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tap to add photo",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Product name
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it; nameError = false },
                label = { Text("Product Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = { if (nameError) Text("Name is required") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    cursorColor = Teal500
                )
            )

            // Company dropdown
            ExposedDropdownMenuBox(
                expanded = companyDropdownExpanded,
                onExpandedChange = { companyDropdownExpanded = !companyDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = companies.find { it.id == selectedCompanyId }?.name ?: "None",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Company (optional)") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = companyDropdownExpanded
                        )
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal500,
                        cursorColor = Teal500
                    )
                )
                ExposedDropdownMenu(
                    expanded = companyDropdownExpanded,
                    onDismissRequest = { companyDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            selectedCompanyId = null
                            companyDropdownExpanded = false
                        }
                    )
                    companies.forEach { company ->
                        DropdownMenuItem(
                            text = { Text(company.name) },
                            onClick = {
                                selectedCompanyId = company.id
                                companyDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Variants section
            Text(
                text = "VARIANTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 3.sp
            )

            variants.forEachIndexed { index, variant ->
                VariantCard(
                    variant = variant,
                    index = index,
                    showDelete = variants.size > 1,
                    onVariantChange = { updated ->
                        variants = variants.toMutableList().also { it[index] = updated }
                    },
                    onDelete = {
                        variants = variants.toMutableList().also { it.removeAt(index) }
                    }
                )
            }

            // Add variant button
            OutlinedButton(
                onClick = { variants = variants + VariantFormState() },
                modifier = Modifier.fillMaxWidth(),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Teal500)
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Teal500,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add Variant",
                    color = Teal500,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class VariantFormState(
    val id: Int = 0,
    val label: String = "",
    val buyingPrice: String = "",
    val sellingPrice: String = ""
)

@Composable
private fun VariantCard(
    variant: VariantFormState,
    index: Int,
    showDelete: Boolean,
    onVariantChange: (VariantFormState) -> Unit,
    onDelete: () -> Unit
) {
    val buyDouble = variant.buyingPrice.toDoubleOrNull() ?: 0.0
    val sellDouble = variant.sellingPrice.toDoubleOrNull() ?: 0.0
    val profit = sellDouble - buyDouble
    val isLoss = profit < 0 && buyDouble > 0 && sellDouble > 0

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Variant header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Variant ${index + 1}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal500,
                    letterSpacing = 1.sp
                )
                if (showDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Label
            OutlinedTextField(
                value = variant.label,
                onValueChange = { onVariantChange(variant.copy(label = it)) },
                label = { Text("Label") },
                placeholder = { Text("e.g. 1kg, 500ml, 1 litre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    cursorColor = Teal500
                )
            )

            // Prices
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = variant.buyingPrice,
                    onValueChange = { onVariantChange(variant.copy(buyingPrice = it)) },
                    label = { Text("Buy ৳") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal500,
                        cursorColor = Teal500
                    )
                )
                OutlinedTextField(
                    value = variant.sellingPrice,
                    onValueChange = { onVariantChange(variant.copy(sellingPrice = it)) },
                    label = { Text("Sell ৳") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal500,
                        cursorColor = Teal500
                    )
                )
            }

            // Live profit preview
            if (buyDouble > 0 && sellDouble > 0) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isLoss)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    else
                        SuccessGreen.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isLoss) "⚠ Selling below cost!" else "Profit",
                            fontSize = 12.sp,
                            color = if (isLoss)
                                MaterialTheme.colorScheme.error
                            else
                                SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isLoss)
                                "-৳${"%.2f".format(-profit)}"
                            else
                                "+৳${"%.2f".format(profit)} (${"%.1f".format((profit/buyDouble)*100)}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLoss)
                                MaterialTheme.colorScheme.error
                            else
                                Gold
                        )
                    }
                }
            }
        }
    }
}