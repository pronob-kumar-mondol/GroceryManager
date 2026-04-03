package com.grocery.manager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grocery.manager.data.local.Product
import com.grocery.manager.ui.components.ProductCard
import com.grocery.manager.ui.components.RecentProductsRow
import com.grocery.manager.ui.components.SearchBar
import com.grocery.manager.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    productViewModel: ProductViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToCompanies: () -> Unit
) {
    val products by productViewModel.products.collectAsStateWithLifecycle()
    val recentProducts by productViewModel.recentProducts.collectAsStateWithLifecycle()
    val searchQuery by productViewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Grocery Manager",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToCompanies) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Companies"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = productViewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Main list
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Recent products section
                if (recentProducts.isNotEmpty() && searchQuery.isBlank()) {
                    item {
                        RecentProductsRow(
                            products = recentProducts,
                            onProductClick = { product ->
                                productViewModel.addToRecentSearches(product.id)
                                onNavigateToEdit(product.id)
                            }
                        )
                    }
                    item {
                        Text(
                            text = "All Products",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                }

                // Product list or empty state
                if (products.isEmpty()) {
                    item {
                        EmptyState(
                            isSearching = searchQuery.isNotBlank(),
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                } else {
                    items(products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            productViewModel = productViewModel,
                            onEdit = {
                                productViewModel.addToRecentSearches(product.id)
                                onNavigateToEdit(product.id)
                            },
                            onDelete = {
                                productViewModel.deleteProduct(product)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isSearching) "No products found" else "No products yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isSearching) "Try searching something else"
            else "Tap the + button to add your first product",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}