package com.grocery.manager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company")
data class Company(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dealerName: String = "",
    val srName: String = "",
    val phone1: String = "",
    val phone2: String = ""
)
