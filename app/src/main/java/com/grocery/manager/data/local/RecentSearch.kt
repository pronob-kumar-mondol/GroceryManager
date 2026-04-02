package com.grocery.manager.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_search",
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
data class RecentSearch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val timestamp: Long = System.currentTimeMillis()
)