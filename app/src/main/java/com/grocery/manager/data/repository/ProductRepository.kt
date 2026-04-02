package com.grocery.manager.data.repository

import com.grocery.manager.data.local.AppDatabase
import com.grocery.manager.data.local.Product
import com.grocery.manager.data.local.RecentSearch
import com.grocery.manager.data.local.Variant
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val db: AppDatabase) {

    private val productDao = db.productDao()
    private val variantDao = db.variantDao()

    fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts()

    fun searchProducts(query: String): Flow<List<Product>> =
        if (query.isBlank()) productDao.getAllProducts()
        else productDao.searchProducts(query)

    fun getRecentProducts(): Flow<List<Product>> =
        productDao.getRecentProducts()

    suspend fun getProductById(id: Int): Product? =
        productDao.getProductById(id)

    suspend fun insertProduct(product: Product, variants: List<Variant>): Long {
        val productId = productDao.insertProduct(product)
        val variantsWithId = variants.map { it.copy(productId = productId.toInt()) }
        variantDao.insertVariants(variantsWithId)
        return productId
    }

    suspend fun updateProduct(product: Product, variants: List<Variant>) {
        productDao.updateProduct(product)
        variantDao.deleteAllVariantsForProduct(product.id)
        val variantsWithId = variants.map { it.copy(productId = product.id) }
        variantDao.insertVariants(variantsWithId)
    }

    suspend fun deleteProduct(product: Product) =
        productDao.deleteProduct(product)

    suspend fun addToRecentSearches(productId: Int) {
        // Remove if already exists to avoid duplicates
        productDao.deleteRecentSearch(productId)

        // If we already have 5, delete the oldest
        if (productDao.getRecentSearchCount() >= 5) {
            productDao.deleteOldestRecentSearch()
        }

        // Insert the new recent search
        productDao.insertRecentSearch(RecentSearch(productId = productId))
    }

    fun getVariantsForProduct(productId: Int): Flow<List<Variant>> =
        variantDao.getVariantsForProduct(productId)
}