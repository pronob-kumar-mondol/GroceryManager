package com.grocery.manager.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "variant",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId")]
)
data class Variant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val label: String,
    val buyingPrice: Double,
    val sellingPrice: Double
)