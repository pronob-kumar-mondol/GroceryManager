package com.grocery.manager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grocery.manager.data.local.Product
import com.grocery.manager.ui.theme.Gold
import com.grocery.manager.ui.theme.SuccessGreen
import com.grocery.manager.ui.theme.Teal500
import com.grocery.manager.ui.theme.TextSecondary
import com.grocery.manager.viewmodel.ProductViewModel

@Composable
fun ProductCard(
    product: Product,
    productViewModel: ProductViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val variants by productViewModel
        .getVariantsForProduct(product.id)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Delete Product",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Delete \"${product.name}\"? All its variants will be deleted too.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Teal left accent border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Teal500)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Product image
                ProductImage(imageUri = product.imageUri)

                Spacer(modifier = Modifier.width(12.dp))

                // Product info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Variants
                    if (variants.isEmpty()) {
                        Text(
                            text = "No variants added",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    } else {
                        variants.forEach { variant ->
                            val profit = variant.sellingPrice - variant.buyingPrice
                            val margin = if (variant.buyingPrice > 0)
                                (profit / variant.buyingPrice) * 100 else 0.0
                            val isLoss = profit < 0

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = variant.label,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Buying price
                                    Text(
                                        text = "৳${variant.buyingPrice}",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "→",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    // Selling price
                                    Text(
                                        text = "৳${variant.sellingPrice}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold
                                    )
                                    // Profit chip
                                    Surface(
                                        shape = MaterialTheme.shapes.extraSmall,
                                        color = if (isLoss)
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                        else
                                            SuccessGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (isLoss)
                                                "-৳${"%.0f".format(-profit)}"
                                            else
                                                "+৳${"%.0f".format(profit)} (${"%.0f".format(margin)}%)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLoss)
                                                MaterialTheme.colorScheme.error
                                            else
                                                SuccessGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Action buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Teal500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductImage(imageUri: String, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.small
    if (imageUri.isNotEmpty()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = "Product image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(64.dp)
                .clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .size(64.dp)
                .background(
                    color = Teal500.copy(alpha = 0.15f),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = Teal500.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}