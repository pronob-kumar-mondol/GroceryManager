package com.grocery.manager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.grocery.manager.data.local.AppDatabase
import com.grocery.manager.data.local.Product
import com.grocery.manager.data.local.Variant
import com.grocery.manager.data.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(AppDatabase.getDatabase(application))

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // All products or search results
    val products: StateFlow<List<Product>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query -> repository.searchProducts(query) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Recent products - always top 5
    val recentProducts: StateFlow<List<Product>> = repository
        .getRecentProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun insertProduct(product: Product, variants: List<Variant>) {
        viewModelScope.launch {
            repository.insertProduct(product, variants)
        }
    }

    fun updateProduct(product: Product, variants: List<Variant>) {
        viewModelScope.launch {
            repository.updateProduct(product, variants)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun addToRecentSearches(productId: Int) {
        viewModelScope.launch {
            repository.addToRecentSearches(productId)
        }
    }

    suspend fun getProductById(id: Int): Product? =
        repository.getProductById(id)

    fun getVariantsForProduct(productId: Int) =
        repository.getVariantsForProduct(productId)
}