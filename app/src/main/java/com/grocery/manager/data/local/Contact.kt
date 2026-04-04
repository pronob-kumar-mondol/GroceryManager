package com.grocery.manager.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contact",
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("companyId")]
)
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val companyId: Int,
    val name: String,
    val role: String = "",
    val phone: String
)