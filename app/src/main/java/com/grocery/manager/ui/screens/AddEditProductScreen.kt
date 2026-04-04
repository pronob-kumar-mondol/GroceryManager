package com.grocery.manager.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.grocery.manager.data.local.ImageUtils
import com.grocery.manager.data.local.Product
import com.grocery.manager.data.local.Variant
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
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()

    // Form state
    var productName by remember { mutableStateOf("") }
    var selectedImagePath by remember { mutableStateOf("") }
    var selectedCompanyId by remember { mutableStateOf<Int?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var variants by remember { mutableStateOf(listOf(VariantFormState())) }

    // Dropdown state
    var companyDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Validation
    var nameError by remember { mutableStateOf(false) }

    // Load existing product in edit mode
    LaunchedEffect(productId) {
        if (productId != null) {
            val product = productViewModel.getProductById(productId)
            product?.let {
                productName = it.name
                selectedImagePath = it.imageUri
                selectedCompanyId = it.companyId
                selectedCategoryId = it.categoryId
            }
        }
    }

    // Load existing variants in edit mode
    val existingVariants by if (productId != null)
        productViewModel.getVariantsForProduct(productId).collectAsStateWithLifecycle(initialValue = emptyList())
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
            categoryId = selectedCategoryId
        )

        val variantList = variants
            .filter { it.label.isNotBlank() && it.buyingPrice.isNotBlank() && it.sellingPrice.isNotBlank() }
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = ::save) {
                        Text(
                            text = "Save",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
            ImagePickerSection(
                imagePath = selectedImagePath,
                onPickImage = { imagePicker.launch("image/*") }
            )

            // Product name
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it; nameError = false },
                label = { Text("Product Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = { if (nameError) Text("Name is required") },
                singleLine = true
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
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = companyDropdownExpanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = categories.find { it.id == selectedCategoryId }?.name ?: "None",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category (optional)") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            selectedCategoryId = null
                            categoryDropdownExpanded = false
                        }
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.id
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Variants section
            VariantsSection(
                variants = variants,
                onVariantsChange = { variants = it }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Holds the form state for a single variant row
data class VariantFormState(
    val id: Int = 0,
    val label: String = "",
    val buyingPrice: String = "",
    val sellingPrice: String = ""
)

@Composable
private fun ImagePickerSection(
    imagePath: String,
    onPickImage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable { onPickImage() },
        contentAlignment = Alignment.Center
    ) {
        if (imagePath.isNotEmpty()) {
            AsyncImage(
                model = imagePath,
                contentDescription = "Product image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Edit overlay
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
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
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add photo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap to add photo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun VariantsSection(
    variants: List<VariantFormState>,
    onVariantsChange: (List<VariantFormState>) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Variants",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = {
                        onVariantsChange(variants + VariantFormState())
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Variant")
                }
            }

            variants.forEachIndexed { index, variant ->
                VariantRow(
                    variant = variant,
                    showDelete = variants.size > 1,
                    onVariantChange = { updated ->
                        onVariantsChange(variants.toMutableList().also { it[index] = updated })
                    },
                    onDelete = {
                        onVariantsChange(variants.toMutableList().also { it.removeAt(index) })
                    }
                )
                if (index < variants.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun VariantRow(
    variant: VariantFormState,
    showDelete: Boolean,
    onVariantChange: (VariantFormState) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = variant.label,
                onValueChange = { onVariantChange(variant.copy(label = it)) },
                label = { Text("Label") },
                placeholder = { Text("e.g. 1kg, 500ml") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            if (showDelete) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove variant",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = variant.buyingPrice,
                onValueChange = { onVariantChange(variant.copy(buyingPrice = it)) },
                label = { Text("Buy ৳") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            OutlinedTextField(
                value = variant.sellingPrice,
                onValueChange = { onVariantChange(variant.copy(sellingPrice = it)) },
                label = { Text("Sell ৳") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
        }
    }
}