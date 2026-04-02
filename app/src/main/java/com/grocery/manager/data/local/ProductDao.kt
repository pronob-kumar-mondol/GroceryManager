package com.grocery.manager.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("""
        SELECT DISTINCT p.* FROM product p
        LEFT JOIN company c ON p.companyId = c.id
        WHERE p.name LIKE '%' || :query || '%'
        OR c.name LIKE '%' || :query || '%'
        ORDER BY p.name ASC
    """)
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM product ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE id = :id")
    suspend fun getProductById(id: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    // Recent searches
    @Query("""
        SELECT p.* FROM product p
        INNER JOIN recent_search r ON p.id = r.productId
        ORDER BY r.timestamp DESC
        LIMIT 5
    """)
    fun getRecentProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(recentSearch: RecentSearch)

    @Query("DELETE FROM recent_search WHERE productId = :productId")
    suspend fun deleteRecentSearch(productId: Int)

    @Query("SELECT COUNT(*) FROM recent_search")
    suspend fun getRecentSearchCount(): Int

    @Query("DELETE FROM recent_search WHERE id = (SELECT id FROM recent_search ORDER BY timestamp ASC LIMIT 1)")
    suspend fun deleteOldestRecentSearch()
}