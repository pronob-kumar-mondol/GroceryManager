package com.grocery.manager.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VariantDao {

    @Query("SELECT * FROM variant WHERE productId = :productId")
    fun getVariantsForProduct(productId: Int): Flow<List<Variant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: Variant): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(variants: List<Variant>)

    @Update
    suspend fun updateVariant(variant: Variant)

    @Delete
    suspend fun deleteVariant(variant: Variant)

    @Query("DELETE FROM variant WHERE productId = :productId")
    suspend fun deleteAllVariantsForProduct(productId: Int)
}